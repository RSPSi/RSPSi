package com.rspsi.tools;

import java.nio.ByteBuffer;

/**
 * @author Jire
 */
public final class ByteBufHelper {

    private ByteBufHelper() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static int getUnsignedByte(final ByteBuffer buffer) {
        return buffer.get() & 0xFF;
    }

    public static int get24BitInt(final ByteBuffer buffer) {
        return (getUnsignedByte(buffer) << 16) + (getUnsignedByte(buffer) << 8) + getUnsignedByte(buffer);
    }

}
