package com.rspsi.spriteloader;

import java.io.DataInputStream;
import java.io.IOException;

public class SpriteLoaderSprite {

	public String name;
	public int id;
	public int drawOffsetX;
	public int drawOffsetY;
	public byte[] spriteData;
	public SpriteLoaderSprite() {
		name = "name";
		id = -1;
		drawOffsetX = 0;
		drawOffsetY = 0;
		spriteData = null;
	}
	/**
	 * Reads the information from the index and data files.
	 * @param index holds the sprite indices
	 * @param data holds the sprite data per index
	 * @throws IOException
	 */
	public void readValues(DataInputStream index, DataInputStream data) throws IOException {
		do {
			int opCode = data.readByte();
			if (opCode == 0) {
				break;
			}
			if (opCode == 1) {
				id = data.readShort();
			} else if (opCode == 2) {
				name = data.readUTF();
			} else if (opCode == 3) {
				drawOffsetX = data.readShort();
			} else if (opCode == 4) {
				drawOffsetY = data.readShort();
			} else if (opCode == 5) {
				int indexLength = index.readInt();
				byte[] dataread = new byte[indexLength];
				data.readFully(dataread);
				spriteData = dataread;
			}
		} while (true);
	}
}
