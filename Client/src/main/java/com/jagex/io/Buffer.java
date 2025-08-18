package com.jagex.io;

import com.displee.cache.index.archive.file.File;
import com.google.common.base.Preconditions;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public final class Buffer {

	public static final boolean ENABLE_RSA = false;

	private static final int[] BIT_MASKS = { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383,
			32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff,
			0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1 };

	public static Buffer create() {
		return new Buffer(new byte[5000]);
	}

	private int bitPosition;
	private byte[] payload;
	public int position;

	/**
	 * Creates the buffer with the specified payload.
	 * 
	 * @param payload
	 *            The payload.
	 */
	
	public Buffer(ByteBuffer buffer) {
		this.payload = buffer.array();
		this.position = 0;
	}
	public Buffer(byte[] payload) {
		this.payload = payload;
		position = 0;
	}

	public Buffer(File payload) {
		this.payload = payload.getData();
		position = 0;
	}

	public void disableBitAccess() {
		position = (bitPosition + 7) / 8;
	}

	public void enableBitAccess() {
		bitPosition = position * 8;
	}

	public void encodeRSA(BigInteger exponent, BigInteger modulus) {
		int length = position;
		position = 0;
		byte[] buffer = new byte[length];
		readData(buffer, 0, length);
		byte[] rsa = buffer;

		if (ENABLE_RSA) {
			rsa = new BigInteger(buffer).modPow(exponent, modulus).toByteArray();
		}

		position = 0;
		writeByte(rsa.length);
		writeBytes(rsa, 0, rsa.length);
	}

	public int getBitPosition() {
		return bitPosition;
	}

	public byte[] getPayload() {
		return payload;
	}

	public int getPosition() {
		return position;
	}

	public int readBits(int amount) {
		int byteOffset = bitPosition / 8;
		int bitOffset = 8 - (bitPosition & 7);
		int value = 0;
		bitPosition += amount;

		for (; amount > bitOffset; bitOffset = 8) {
			value += (payload[byteOffset++] & BIT_MASKS[bitOffset]) << amount - bitOffset;
			amount -= bitOffset;
		}

		if (amount == bitOffset) {
			value += payload[byteOffset] & BIT_MASKS[bitOffset];
		} else {
			value += payload[byteOffset] >> bitOffset - amount & BIT_MASKS[amount];
		}

		return value;
	}

	public byte readByte() {
		return payload[position++];
	}

	public byte readByteS() {
		return (byte) (128 - payload[position++]);
	}

	public void readData(byte[] data, int offset, int length) {
		for (int i = offset; i < offset + length; i++) {
			data[i] = payload[position++];
		}
	}

	public int readIMEInt() { // V2
		position += 4;
		return ((payload[position - 3] & 0xff) << 24) + ((payload[position - 4] & 0xff) << 16)
				+ ((payload[position - 1] & 0xff) << 8) + (payload[position - 2] & 0xff);
	}

	public int readInt() {
		position += 4;
		return ((payload[position - 4] & 0xff) << 24) + ((payload[position - 3] & 0xff) << 16)
				+ ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
	}

	public int readLEShort() {
		position += 2;
		int value = ((payload[position - 1] & 0xff) << 8) + (payload[position - 2] & 0xff);
		if (value > 32767) {
			value -= 0x10000;
		}
		return value;
	}

	public int readLEShortA() {
		position += 2;
		int value = ((payload[position - 1] & 0xff) << 8) + (payload[position - 2] - 128 & 0xff);
		if (value > 32767) {
			value -= 0x10000;
		}
		return value;
	}

	public int readLEUShort() {
		position += 2;
		return ((payload[position - 1] & 0xff) << 8) + (payload[position - 2] & 0xff);
	}

	public int readLEUShortA() {
		position += 2;
		return ((payload[position - 1] & 0xff) << 8) + (payload[position - 2] - 128 & 0xff);
	}

	public long readLong() {
		long msi = readInt() & 0xFFFFFFFFL;
		long lsi = readInt() & 0xFFFFFFFFL;
		return (msi << 32) + lsi;
	}

	public int readMEInt() { // V1
		position += 4;
		return ((payload[position - 2] & 0xff) << 24) + ((payload[position - 1] & 0xff) << 16)
				+ ((payload[position - 4] & 0xff) << 8) + (payload[position - 3] & 0xff);
	}

	public byte readNegByte() {
		return (byte) -payload[position++];
	}

	public int readNegUByte() {
		return -payload[position++] & 0xff;
	}

	public void readReverseData(byte[] data, int offset, int length) {
		for (int i = length + offset - 1; i >= length; i--) {
			data[i] = payload[position++];
		}
	}

	public short readShortRegular() {
		return (short) ((payload[position++] << 8) | (payload[position++] & 0xFF));
	}

	public int readShort() {
		position += 2;
		int value = ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
		if (value > 32767) {
			value -= 0x10000;
		}

		return value;
	}

	public int readShort2() {
		position += 2;
		int value = ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
		if (value > 60000) {
			value -= 65535;
		}
		return value;
	}

	public int readSmart() {
		int value = payload[position] & 0xff;
		if (value < 128)
			return readUByte() - 64;

		return readUShort() - 49152;
	}


	
	public int readBigSmart() {
		int value = payload[position] & 0xff;
		if (value >= 0)
			return readUShort() & 0xFFFF;

		return readInt() & Integer.MAX_VALUE;
	}

	private static final char[] CHARACTERS = new char[]
			{
					'\u20ac', '\u0000', '\u201a', '\u0192', '\u201e', '\u2026',
					'\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039',
					'\u0152', '\u0000', '\u017d', '\u0000', '\u0000', '\u2018',
					'\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014',
					'\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\u0000',
					'\u017e', '\u0178'
			};

	public String readOSRSString()
	{
		StringBuilder sb = new StringBuilder();

		for (; ; )
		{
			int ch = this.readUByte();

			if (ch == 0)
			{
				break;
			}

			if (ch >= 128 && ch < 160)
			{
				char var7 = CHARACTERS[ch - 128];
				if (0 == var7)
				{
					var7 = '?';
				}

				ch = var7;
			}

			sb.append((char) ch);
		}
		return sb.toString();
	}

	public String readString() {
		int start = position;
		while (payload[position++] != 10) {

		}
		return new String(payload, start, position - start - 1);
	}
	
	public String readStringAlternative() {
		int start = position;
		while (payload[position++] != 0) {

		}
		return new String(payload, start, position - start - 1);
	}

	public byte[] readStringBytes() {
		int start = position;
		while (payload[position++] != 10) {

		}

		byte[] bytes = new byte[position - start - 1];
		for (int i = start; i < position - 1; i++) {
			bytes[i - start] = payload[i];
		}

		return bytes;
	}

	public int readUByte() {
		return payload[position++] & 0xff;
	}

	public final int getULEShort() {
		this.position += 2;
		return (this.payload[-2 + this.position] << 8 & 65280) - -(this.payload[-1 + this.position] & 255);
	}

	public int readUByteA() {
		return payload[position++] - 128 & 0xff;
	}

	public int readUByteS() {
		return 128 - payload[position++] & 0xff;
	}

	public int readUShort() {
		position += 2;
		return ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
	}

	public int readUShortA() {
		position += 2;
		return ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] - 128 & 0xff);
	}

	public int readUSmart() {
		int value = payload[position] & 0xff;
		if (value < 128)
			return readUByte();

		return readUShort() - 0x8000;
	}

	public byte peek()
	{
		return payload[position];
	}

	public int readUnsignedByte()
	{
		return this.readByte() & 0xFF;
	}

	public int readUnsignedShort()
	{
		return this.readShortRegular() & 0xFFFF;
	}

	public int readUnsignedShortSmart()
	{
		int peek = this.peek() & 0xFF;
		return peek < 128 ? this.readUnsignedByte() : this.readUnsignedShort() - 0x8000;
	}

	public int readUnsignedIntSmartShortCompat()
	{
		int var1 = 0;

		int var2;
		for (var2 = this.readUnsignedShortSmart(); var2 == 32767; var2 = this.readUnsignedShortSmart())
		{
			var1 += 32767;
		}

		var1 += var2;
		return var1;
	}
	
	public int readUSmartInt() {
		int val = 0;
		int lastVal = 0;
		while((lastVal = readUSmart()) == 32767) {
			val += 32767;
		}

		return val + lastVal;
	}

	public int readUTriByte() {
		position += 3;
		return ((payload[position - 3] & 0xff) << 16) + ((payload[position - 2] & 0xff) << 8)
				+ (payload[position - 1] & 0xff);
	}

	public void setBitPosition(int bitPosition) {
		this.bitPosition = bitPosition;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void writeByte(int i) {
		payload[position++] = (byte) i;
	}

	public void writeBytes(byte[] data, int offset, int length) {
		for (int i = offset; i < offset + length; i++) {
			payload[position++] = data[i];
		}
	}

	public void writeByteS(int i) {
		payload[position++] = (byte) (128 - i);
	}

	public void writeInt(int i) {
		payload[position++] = (byte) (i >> 24);
		payload[position++] = (byte) (i >> 16);
		payload[position++] = (byte) (i >> 8);
		payload[position++] = (byte) i;
	}

	@SuppressWarnings("deprecation")
	public void writeJString(String s) {
		s.getBytes(0, s.length(), payload, position);
		position += s.length();
		payload[position++] = 10;
	}

	public void writeLEInt(int i) {
		payload[position++] = (byte) i;
		payload[position++] = (byte) (i >> 8);
		payload[position++] = (byte) (i >> 16);
		payload[position++] = (byte) (i >> 24);
	}

	public void writeLEShort(int i) {
		payload[position++] = (byte) i;
		payload[position++] = (byte) (i >> 8);
	}

	public void writeLEShortA(int i) {
		payload[position++] = (byte) (i + 128);
		payload[position++] = (byte) (i >> 8);
	}

	public void writeLong(long l) {
		payload[position++] = (byte) (int) (l >> 56);
		payload[position++] = (byte) (int) (l >> 48);
		payload[position++] = (byte) (int) (l >> 40);
		payload[position++] = (byte) (int) (l >> 32);
		payload[position++] = (byte) (int) (l >> 24);
		payload[position++] = (byte) (int) (l >> 16);
		payload[position++] = (byte) (int) (l >> 8);
		payload[position++] = (byte) (int) l;
	}

	public void writeNegatedByte(int i) {
		payload[position++] = (byte) -i;
	}

	public void writeOpcode(int i) {
		payload[position++] = (byte) i;
	}

	public void writeReverseDataA(byte[] data, int length, int offset) {
		for (int i = length + offset - 1; i >= length; i--) {
			payload[position++] = (byte) (data[i] + 128);
		}
	}

	public void writeShort(int i) {
		payload[position++] = (byte) (i >> 8);
		payload[position++] = (byte) i;
	}

	public void writeShortA(int i) {
		payload[position++] = (byte) (i >> 8);
		payload[position++] = (byte) (i + 128);
	}

	public void writeSizeByte(int i) {
		payload[position - i - 1] = (byte) i;
	}

	public void writeTriByte(int i) {
		payload[position++] = (byte) (i >> 16);
		payload[position++] = (byte) (i >> 8);
		payload[position++] = (byte) i;
	}

	public void writeUSmart(int value) {
		if (value < 128) {
			this.writeByte(value);
		} else {
			this.writeShort(0x8000 | value);
		}
	}

	public void writeUSmartInt(int value) {
		if (value > Short.MAX_VALUE) {
			writeIntSmart(value);
		} else {
			writeUSmart(value);
		}
	}

	public void writeIntSmart(int value) {
		Preconditions.checkArgument(value > 32767);
		Preconditions.checkArgument(value < (Integer.MAX_VALUE - 32767));
		int diff = value - 32767;
		writeShort(65535);
		writeUSmart(diff);
	}

	public void skip(int bytesToSkip) {
		position += bytesToSkip;
	}

}