package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;

import com.jagex.cache.graphics.IndexedImage;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.textures.PalettedTexture;
import com.jagex.draw.textures.Texture;
import com.rspsi.core.misc.FixedHashMap;


public class TextureLoader474 extends TextureLoader {

	private Texture[] textures = new Texture[50];
	private boolean[] transparent = new boolean[50];
	private double brightness = 0.8;
	private FixedHashMap<Integer, int[]> textureCache = new FixedHashMap<Integer, int[]>(20);
	
	
	@Override
	public Texture forId(int arg0) {
		if(arg0 < 0 || arg0 > textures.length)
			return null;
		return textures[arg0];
	}

	@Override
	public int[] getPixels(int textureId) {
		Texture texture = forId(textureId);
		if(texture == null)
			return null;

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
		
		transparent[textureId] = false;
		for (int l1 = 0; l1 < 16384; l1++) {
			texels[l1] &= 0xf8f8ff;
			int k2 = texels[l1];
			if (k2 == 0)
				transparent[textureId] = true;
			texels[16384 + l1] = k2 - (k2 >>> 3) & 0xf8f8ff;
			texels[32768 + l1] = k2 - (k2 >>> 2) & 0xf8f8ff;
			texels[49152 + l1] = k2 - (k2 >>> 2) - (k2 >>> 3) & 0xf8f8ff;
		}


		textureCache.put(textureId, texels);
	return texels;
	}

	@Override
	public void init(Archive archive) {
		for (int j = 0; j < textures.length; j++)
			try {
				IndexedImage texture = new IndexedImage(archive, String.valueOf(j), 0);
				texture.resize();
				textures[j] = new PalettedTexture(texture);
			} catch (Exception _ex) {
				_ex.printStackTrace();
			}
	}

	

	@Override
	public boolean isTransparent(int arg0) {
		if(arg0 < 0 || arg0 > transparent.length)
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


	

}
