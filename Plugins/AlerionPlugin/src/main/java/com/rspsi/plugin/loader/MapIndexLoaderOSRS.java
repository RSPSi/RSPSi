//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.file.File;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.io.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;
import com.displee.cache.index.archive.Archive;

public class MapIndexLoaderOSRS extends MapIndexLoader {
    private int[] mapHashes;
    private int[] objects;
    private int[] landscapes;

    public MapIndexLoaderOSRS() {
    }

    public void init(Archive archive) {
        File indices = archive.file("map_index");
        Buffer buffer = new Buffer(indices);
        int count = buffer.readUShort();
        this.mapHashes = new int[count];
        this.landscapes = new int[count];
        this.objects = new int[count];
        int pos = 0;

        for(int region = 0; region < count; ++region) {
            this.mapHashes[region] = buffer.readUShort();
            this.landscapes[region] = buffer.readUShort();
            this.objects[region] = buffer.readUShort();
            ++pos;
        }

        System.out.println("expected regions " + count + " - actual " + pos);
    }

    public void init(Buffer buffer) {
        int count = buffer.readUShort();
        this.mapHashes = new int[count];
        this.landscapes = new int[count];
        this.objects = new int[count];
        int pos = 0;

        for(int region = 0; region < count; ++region) {
            this.mapHashes[region] = buffer.readUShort();
            this.landscapes[region] = buffer.readUShort();
            this.objects[region] = buffer.readUShort();
            ++pos;
        }

        System.out.println("expected regions " + count + " - actual " + pos);
    }

    public int getFileId(int hash, MapType type) {
        int index = IntStream.range(0, this.mapHashes.length).filter((i) -> {
            return hash == this.mapHashes[i];
        }).findFirst().orElse(-1);
        if (index >= 0) {
            return type == MapType.LANDSCAPE ? this.landscapes[index] : this.objects[index];
        } else {
            return -1;
        }
    }

    public boolean landscapePresent(int id) {
        return IntStream.range(0, this.mapHashes.length).filter((i) -> {
            return id == this.landscapes[i];
        }).findFirst().orElse(-1) >= 0;
    }

    public boolean objectPresent(int id) {
        return IntStream.range(0, this.mapHashes.length).filter((i) -> {
            return id == this.objects[i];
        }).findFirst().orElse(-1) >= 0;
    }

    public byte[] encode() {
        ByteBuffer buffer = ByteBuffer.allocate(this.mapHashes.length * 6 + 2);
        buffer.putShort((short)this.mapHashes.length);

        for(int index = 0; index < this.mapHashes.length; ++index) {
            buffer.putShort((short)this.mapHashes[index]);
            buffer.putShort((short)this.landscapes[index]);
            buffer.putShort((short)this.objects[index]);
        }

        return buffer.array();
    }

    public void set(int regionX, int regionY, int landscapeId, int objectsId) {
        int hash = (regionX << 8) + regionY;
        int index = IntStream.range(0, this.mapHashes.length).filter((i) -> {
            return hash == this.mapHashes[i];
        }).findFirst().orElse(-1);
        if (index >= 0) {
            System.out.println("Setting index " + index);
            this.landscapes[index] = landscapeId;
            this.objects[index] = objectsId;
        } else {
            System.out.println("Adding new index");
            int[] mapHashes = Arrays.copyOf(this.mapHashes, this.landscapes.length + 1);
            int[] landscapes = Arrays.copyOf(this.landscapes, this.landscapes.length + 1);
            int[] objects = Arrays.copyOf(this.objects, this.landscapes.length + 1);
            index = mapHashes.length - 1;
            mapHashes[index] = hash;
            landscapes[index] = landscapeId;
            objects[index] = objectsId;
            this.mapHashes = mapHashes;
            this.landscapes = landscapes;
            this.objects = objects;
        }

    }
}
