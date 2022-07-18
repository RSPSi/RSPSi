package com.rspsi.plugin.loader;

import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.textures.Texture;
import com.rspsi.core.misc.FixedHashMap;
import com.displee.cache.index.archive.Archive;


public class TextureLoaderOSRS extends TextureLoader {

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
		textureCache.put(textureId, texels);

		return texels;
	}

	@Override
	public void init(Archive archive) {

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
		
	}

}
