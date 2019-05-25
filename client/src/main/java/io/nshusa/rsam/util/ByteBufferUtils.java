package io.nshusa.rsam.util;

import java.nio.ByteBuffer;

public final class ByteBufferUtils {

    private ByteBufferUtils() {

    }

    public static void write24Int(ByteBuffer buffer, int value) {
        buffer.put((byte) (value >> 16)).put((byte) (value >> 8)).put((byte) value);
    }

    public static int getUMedium(ByteBuffer buffer) {
        return (buffer.getShort() & 0xFFFF) << 8 | buffer.get() & 0xFF;
    }

    public static int getUShort(ByteBuffer buffer) {
        return buffer.getShort() & 0xffff;
    }

    public static int readU24Int(ByteBuffer buffer) {
        return (buffer.get() & 0x0ff) << 16 | (buffer.get() & 0x0ff) << 8 | (buffer.get() & 0x0ff);
    }

    public static int getSmart(ByteBuffer buffer) {
        int peek = buffer.get(buffer.position()) & 0xFF;
        if (peek < 128) {
            return buffer.get() & 0xFF;
        }
        return (buffer.getShort() & 0xFFFF) - 32768;
    }

    public static String getString(ByteBuffer buffer) {
        final StringBuilder bldr = new StringBuilder();
        byte b;
        while (buffer.hasRemaining() && (b = buffer.get()) != 10) {
            bldr.append((char) b);
        }
        return bldr.toString();
    }

}
