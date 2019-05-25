package com.rspsi.spriteloader;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;

import com.jagex.cache.graphics.Sprite;
import com.jagex.io.Buffer;

public class SpriteLoader {

	public SpriteLoader(String idxPath, String datPath) {
		loadSprites(idxPath, datPath);
	}



	/**
	 * Loads the sprite data and index files from the overlays location.
	 * This can be edited to use an archive such as config or media to load from the overlays.
	 * @param archive
	 */
	public void loadSprites(String idxPath, String datPath) {
		try {
			Buffer index = new Buffer(Files.readAllBytes(new File(idxPath).toPath()));
			Buffer data = new Buffer(Files.readAllBytes(new File(datPath).toPath()));
			DataInputStream indexFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(index.getPayload())));
			DataInputStream dataFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data.getPayload())));
			int totalSprites = indexFile.readInt();
			if (cache == null) {
				cache = new SpriteLoaderSprite[totalSprites];
				sprites = new Sprite[totalSprites];
			}
			for (int i = 0; i < totalSprites; i++) {
				int id = indexFile.readInt();
				if (cache[id] == null) {
					cache[id] = new SpriteLoaderSprite();
				}
				cache[id].readValues(indexFile, dataFile);
				createSprite(cache[id]);
			}
			indexFile.close();
			dataFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	/**
	 * Creates a sprite out of the spriteData.
	 * @param sprite
	 */
	public void createSprite(SpriteLoaderSprite sprite) {
		
		sprites[sprite.id] = new Sprite(sprite.spriteData, null);
		sprites[sprite.id].setHorizontalOffset(sprite.drawOffsetX);
		sprites[sprite.id].setVerticalOffset(sprite.drawOffsetY);
	}

	/**
	 * Gets the name of a specified sprite index.
	 * @param index
	 * @return
	 */
	public String getName(int index) {
		if (cache[index].name != null) {
			return cache[index].name;
		} else {
			return "null";
		}
	}

	/**
	 * Gets the drawOffsetX of a specified sprite index.
	 * @param index
	 * @return
	 */
	public int getOffsetX(int index) {
		return cache[index].drawOffsetX;
	}

	/**
	 * Gets the drawOffsetY of a specified sprite index.
	 * @param index
	 * @return
	 */
	public int getOffsetY(int index) {
		return cache[index].drawOffsetY;
	}

	/**
	 * Sets the default values.
	 */
	

	public SpriteLoaderSprite[] cache;
	public Sprite[] sprites = null;
	public int totalSprites;
}