package com.rspsi.plugin.loader;

import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.jagex.cache.graphics.Sprite;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.textures.SpriteTexture;
import com.jagex.draw.textures.Texture;
import com.jagex.io.Buffer;
import com.rspsi.core.misc.FixedHashMap;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class TextureLoaderOSRS extends TextureLoader {

	private Texture[] textures;
	private boolean[] transparent;
	private double brightness = 0.8;
	private FixedHashMap<Integer, int[]> textureCache = new FixedHashMap<Integer, int[]>(20);
	
	
	@Override
	public Texture forId(int arg0) {
		if(arg0 < 0 || arg0 >= textures.length)
			return null;
		return textures[arg0];
	}

	@Override
	public int[] getPixels(int textureId) {
		Texture texture = forId(textureId);
		if(texture == null) {
			log.info("Texture {} was not found!", textureId);
			return null;
		}

		if(textureCache.contains(textureId))
			return textureCache.get(textureId);
		
		int[] texels = new int[0x10000];
		
		texture.setBrightness(brightness);
		if (texture.getWidth() == 64)
			for (int y = 0; y < 128; y++)
				for (int x = 0; x < 128; x++)
					texels[x + (y << 7)] = texture.getPixel((x >> 1) + ((y >> 1) << 6));


		else
			for (int texelPtr = 0; texelPtr < 16384; texelPtr++)
				texels[texelPtr] = texture.getPixel(texelPtr);
		
		for (int l1 = 0; l1 < 16384; l1++) {
			texels[l1] &= 0xf8f8ff;
			int k2 = texels[l1];
			texels[16384 + l1] = k2 - (k2 >>> 3) & 0xf8f8ff;
			texels[32768 + l1] = k2 - (k2 >>> 2) & 0xf8f8ff;
			texels[49152 + l1] = k2 - (k2 >>> 2) - (k2 >>> 3) & 0xf8f8ff;
		}


		textureCache.put(textureId, texels);
	return texels;
	}

	
	public void init(Archive archive, Index spriteIndex) {
		val highestId = Arrays.stream(archive.fileIds()).max().getAsInt();
		textures = new Texture[highestId + 1];
		transparent = new boolean[highestId + 1];
		for(File file : archive.files()) {
			if(file != null && file.getData() != null) {
				log.info("Loading texture {}", file.getId());
				Buffer buffer = new Buffer(file.getData());
				buffer.skip(3);
				int count = buffer.readUByte();
				int[] texIds = new int[count];
				for(int i = 0;i<count;i++) {
					texIds[i] = buffer.readUShort();
				}
				Sprite sprite = Sprite.decode(ByteBuffer.wrap(spriteIndex.archive(texIds[0]).file(0).getData()));
				if(sprite.getWidth() != 128 || sprite.getHeight() != 128)
					sprite.resize(128, 128);
				Texture texture = new SpriteTexture(sprite);
				textures[file.getId()] = texture;
				transparent[file.getId()] = texture.supportsAlpha();
			}
		}
	}

	

	@Override
	public boolean isTransparent(int arg0) {
		if(arg0 < 0 || arg0 >= transparent.length)
			return false;
		return transparent[arg0];
	}

	@Override
	public void setBrightness(double arg0) {
		textureCache.clear();
		this.brightness = arg0;
	}

	@Override
	public int count() {
		return textures.length;
	}

	@Override
	public void init(byte[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Archive archive) {
		// TODO Auto-generated method stub
		
	}


	

}
