package io.nshusa.rsam;

import io.nshusa.rsam.binary.Archive;
import io.nshusa.rsam.util.ByteBufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class FileStore {

    private static final String[] crcFileNames = {"model_crc", "anim_crc", "midi_crc", "map_crc"};
    private static final String[] versionFileNames = {"model_version", "anim_version", "midi_version", "map_version"};

    private final Checksum checksum = new CRC32();

    public static final int ARCHIVE_FILE_STORE = 0;
    public static final int MODEL_FILE_STORE = 1;
    public static final int ANIMATION_FILE_STORE = 2;
    public static final int MIDI_FILE_STORE = 3;
    public static final int MAP_FILE_STORE = 4;

    private static final int EXPANDED_HEADER_LENGTH = 10;
    private static final int HEADER_LENGTH = 8;

    private static final int EXPANDED_BLOCK_LENGTH = 510;
    private static final int BLOCK_LENGTH = 512;

    private static final int TOTAL_BLOCK_LENGTH = HEADER_LENGTH + BLOCK_LENGTH;
    private static final int META_BLOCK_LENGTH = 6;

    private static final ByteBuffer buffer = ByteBuffer.allocate(BLOCK_LENGTH + HEADER_LENGTH);

    private final int storeId;

    private final FileChannel dataChannel;

    private final FileChannel metaChannel;

    public FileStore(int storeId, FileChannel dataChannel, FileChannel metaChannel) {
        this.storeId = storeId;
        this.dataChannel = dataChannel;
        this.metaChannel = metaChannel;
    }

    public int calculateChecksum(Archive updateArchive, int fileId) throws IOException {
        // you can't calculate the checksum for archives like this they don't have an associated version and crc file in the version list archive
        if (storeId == 0) {
            return 0;
        }

        ByteBuffer versionBuf = updateArchive.readFile(versionFileNames[storeId - 1]);

        int versionCount = versionBuf.capacity() / Short.BYTES;

        int version = 0;

        if (fileId < versionCount) {
            versionBuf.position(fileId * Short.BYTES);

            version = versionBuf.getShort() & 0xFFFF;
        }

        // read the file
        ByteBuffer fileBuf = readFile(fileId);

        if (fileBuf == null) {
            return 0;
        }

        // file data first then version after
        ByteBuffer buf = ByteBuffer.allocate(fileBuf.capacity() + Short.BYTES);
        buf.put(fileBuf);
        buf.putShort((short) version);

        checksum.reset();
        checksum.update(buf.array(), 0, buf.capacity());

        return (int) checksum.getValue();
    }

    public synchronized ByteBuffer readFile(int fileId) {
        try {

            if (fileId * META_BLOCK_LENGTH + META_BLOCK_LENGTH > metaChannel.size()) {
                return null;
            }

            buffer.position(0).limit(META_BLOCK_LENGTH);
            metaChannel.read(buffer, fileId * META_BLOCK_LENGTH);
            buffer.flip();

            int size = ByteBufferUtils.readU24Int(buffer);
            int block = ByteBufferUtils.readU24Int(buffer);

            if (block <= 0 || (long) block > dataChannel.size() / 520L) {
                return null;
            }

            ByteBuffer fileBuffer = ByteBuffer.allocate(size);

            int remaining = size;
            int chunk = 0;
            int blockLength = fileId <= 0xFFFF ? BLOCK_LENGTH : EXPANDED_BLOCK_LENGTH;
            int headerLength = fileId <= 0xFFFF ? HEADER_LENGTH : EXPANDED_HEADER_LENGTH;

            while (remaining > 0) {
                if (block == 0) {
                    return null;
                }

                int blockSize = remaining > blockLength ? blockLength : remaining;
                buffer.position(0).limit(blockSize + headerLength);
                dataChannel.read(buffer, block * TOTAL_BLOCK_LENGTH);
                buffer.flip();

                int currentFile, currentChunk, nextBlock, currentIndex;

                if (fileId <= 65535) {
                    currentFile = buffer.getShort() & 0xFFFF;
                    currentChunk = buffer.getShort() & 0xFFFF;
                    nextBlock = ByteBufferUtils.readU24Int(buffer);
                    currentIndex = buffer.get() & 0xFF;
                } else {
                    currentFile = buffer.getInt();
                    currentChunk = buffer.getShort() & 0xFFFF;
                    nextBlock = ByteBufferUtils.readU24Int(buffer);
                    currentIndex = buffer.get() & 0xFF;
                }

                if (fileId != currentFile || chunk != currentChunk || (storeId + 1) != currentIndex) {
                    return null;
                }
                if (nextBlock < 0 || nextBlock > dataChannel.size() / TOTAL_BLOCK_LENGTH) {
                    return null;
                }

                int rem = buffer.remaining();

                for (int i = 0; i < rem; i++) {
                    fileBuffer.put(buffer.get());
                }

                remaining -= blockSize;
                block = nextBlock;
                chunk++;
            }
            fileBuffer.position(0);
            return fileBuffer;
        } catch (IOException _ex) {
            _ex.printStackTrace();
            return null;
        }
    }

    public synchronized boolean writeFile(int id, byte[] data) {
        return writeFile(id, data, true) || writeFile(id, data, false);
    }

    private synchronized boolean writeFile(int fileId, byte[] data, boolean exists) {
        try {

            ByteBuffer dataBuf = ByteBuffer.wrap(data);

            int block;

            if (exists) {

                if (fileId * META_BLOCK_LENGTH + META_BLOCK_LENGTH > metaChannel.size()) {
                    return false;
                }

                buffer.position(0).limit(META_BLOCK_LENGTH);
                metaChannel.read(buffer, fileId * META_BLOCK_LENGTH);
                buffer.flip();

                // skip size
                buffer.position(3);

                block = ByteBufferUtils.readU24Int(buffer);

                if (block <= 0 || (long) block > dataChannel.size() / TOTAL_BLOCK_LENGTH) {
                    return false;
                }

            } else {
                block = (int) ((dataChannel.size() + TOTAL_BLOCK_LENGTH - 1) / TOTAL_BLOCK_LENGTH);

                if (block == 0) {
                    block = 1;
                }

            }

            buffer.position(0);
            ByteBufferUtils.write24Int(buffer, data.length);
            ByteBufferUtils.write24Int(buffer, block);
            buffer.flip();

            metaChannel.write(buffer, fileId * META_BLOCK_LENGTH);

            int remaining = data.length;
            int chunk = 0;
            int blockLength = fileId <= 0xFFFF ? BLOCK_LENGTH : EXPANDED_BLOCK_LENGTH;
            int headerLength = fileId <= 0xFFFF ? HEADER_LENGTH : EXPANDED_HEADER_LENGTH;
            while (remaining > 0) {
                int nextBlock = 0;

                if (exists) {
                    buffer.position(0).limit(headerLength);
                    dataChannel.read(buffer, block * TOTAL_BLOCK_LENGTH);
                    buffer.flip();

                    int currentFile, currentChunk, currentIndex;
                    if (fileId <= 0xFFFF) {
                        currentFile = buffer.getShort() & 0xFFFF;
                        currentChunk = buffer.getShort() & 0xFFFF;
                        nextBlock = ByteBufferUtils.readU24Int(buffer);
                        currentIndex = buffer.get() & 0xFF;
                    } else {
                        currentFile = buffer.getInt();
                        currentChunk = buffer.getShort() & 0xFFFF;
                        nextBlock = ByteBufferUtils.readU24Int(buffer);
                        currentIndex = buffer.get() & 0xFF;
                    }

                    if (fileId != currentFile || chunk != currentChunk || (storeId + 1) != currentIndex) {
                        return false;
                    }

                    if (nextBlock < 0 || nextBlock > dataChannel.size() / TOTAL_BLOCK_LENGTH) {
                        return false;
                    }

                }

                if (nextBlock == 0) {
                    exists = false;
                    nextBlock = (int) ((dataChannel.size() + TOTAL_BLOCK_LENGTH - 1) / TOTAL_BLOCK_LENGTH);

                    if (nextBlock == 0) {
                        nextBlock = 1;
                    }

                    if (nextBlock == block) {
                        nextBlock++;
                    }

                }

                if (remaining <= blockLength) {
                    nextBlock = 0;
                }

                buffer.position(0).limit(TOTAL_BLOCK_LENGTH);

                if (fileId <= 0xFFFF) {
                    buffer.putShort((short) fileId);
                    buffer.putShort((short) chunk);
                    ByteBufferUtils.write24Int(buffer, nextBlock);
                    buffer.put((byte) (storeId + 1));
                } else {
                    buffer.putInt(fileId);
                    buffer.putShort((short) chunk);
                    ByteBufferUtils.write24Int(buffer, nextBlock);
                    buffer.put((byte) (storeId + 1));
                }

                int blockSize = remaining > blockLength ? blockLength : remaining;
                dataBuf.limit(dataBuf.position() + blockSize);
                buffer.put(dataBuf);
                buffer.flip();

                dataChannel.write(buffer, block * TOTAL_BLOCK_LENGTH);
                remaining -= blockSize;
                block = nextBlock;
                chunk++;
            }

            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public void close() {
        try {
            dataChannel.close();
            metaChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getFileCount() {
        if (!metaChannel.isOpen()) {
            return 0;
        }

        try {
            return Math.toIntExact(metaChannel.size() / META_BLOCK_LENGTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getStoreId() {
        return storeId;
    }

}
