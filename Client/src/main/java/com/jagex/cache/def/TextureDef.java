package com.jagex.cache.def;

import com.displee.cache.index.archive.Archive;
import com.jagex.io.Buffer;

public final class TextureDef
{
	private TextureDef()
	{
	}

	public static void unpackConfig(Archive streamLoader)
	{
		Buffer buffer = new Buffer(streamLoader.file("textures.dat"));
		int count = buffer.readUShort();
		textures = new TextureDef[count];
		for (int i = 0; i != count; ++i)
			if (buffer.readUByte() == 1)
				textures[i] = new TextureDef();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aBoolean1223 = buffer.readUByte() == 1;


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aBoolean1204 = buffer.readUByte() == 1;


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aBoolean1205 = buffer.readUByte() == 1;


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aByte1217 = buffer.readByte();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aByte1225 = buffer.readByte();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aByte1214 = buffer.readByte();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aByte1213 = buffer.readByte();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aShort1221 = (short) buffer.readUShort();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aByte1211 = buffer.readByte();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aByte1203 = buffer.readByte();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aBoolean1222 = buffer.readUByte() == 1;


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aBoolean1216 = buffer.readUByte() == 1;


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aByte1207 = buffer.readByte();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aBoolean1212 = buffer.readUByte() == 1;


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aBoolean1210 = buffer.readUByte() == 1;


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].aBoolean1215 = buffer.readUByte() == 1;


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].anInt1202 = buffer.readUByte();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].anInt1206 = buffer.readInt();


		for (int i = 0; i != count; ++i)
			if (textures[i] != null)
				textures[i].anInt1226 = buffer.readUByte();


	}

	public static void nullLoader()
	{
		textures = null;
	}

	public boolean aBoolean1223;
	public boolean aBoolean1204;
	public boolean aBoolean1205;
	public byte aByte1217;
	public byte aByte1225;
	public byte aByte1214;
	public byte aByte1213;
	public short aShort1221;
	public byte aByte1211;
	public byte aByte1203;
	public boolean aBoolean1222;
	public boolean aBoolean1216;
	public byte aByte1207;
	public boolean aBoolean1212;
	public boolean aBoolean1210;
	public boolean aBoolean1215;
	public int anInt1202;
	public int anInt1206;
	public int anInt1226;
	public static TextureDef[] textures;
}
