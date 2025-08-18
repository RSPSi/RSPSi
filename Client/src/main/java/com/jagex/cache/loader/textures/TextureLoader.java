package com.jagex.cache.loader.textures;

import com.jagex.cache.loader.DataLoaderBase;
import com.jagex.draw.textures.Texture;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class TextureLoader implements DataLoaderBase<Texture> {

	public static TextureLoader instance;

	public abstract int[] getPixels(int id);
	public abstract void setBrightness(double exponent);
	public abstract boolean isTransparent(int id);

	public static Texture[] getTextures(){
		return IntStream.range(0, instance.count()).mapToObj(TextureLoader::getTexture).collect(Collectors.toList()).toArray(new Texture[instance.count()]);
	}
	
	public static Texture getTexture(int id) {
		return instance.forId(id);
	}

	public static int[] getTexturePixels(int id) {
		return instance.getPixels(id);
	}
	public static boolean getTextureTransparent(int textureId) {
		return instance.isTransparent(textureId);
	}
	
}
