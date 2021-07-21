package com.rspsi.plugin.loader;

import com.rspsi.jagex.cache.graphics.IndexedImage;
import com.rspsi.jagex.cache.loader.textures.TextureLoader;
import com.rspsi.jagex.draw.textures.PalettedTexture;
import com.rspsi.jagex.draw.textures.Texture;
import com.rspsi.misc.FixedHashMap;
import com.rspsi.options.Config;
import com.displee.cache.index.archive.Archive;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Stream;


public class MyTextureLoader extends TextureLoader {

	private Texture[] textures = new Texture[50];
	private boolean[] transparent = new boolean[50];
	private double brightness = 0.8;
	private FixedHashMap<Integer, int[]> textureCache = new FixedHashMap<Integer, int[]>(20);


	private File getTextureFromFile(int id){
		File textureFolder = new File(Config.cacheLocation.get(), "Textures/");
		return new File(textureFolder, id + ".png");
	}

	private int getHighestCustomTexId(){
		File textureFolder = new File(Config.cacheLocation.get(), "Textures/");
		return Stream.of(textureFolder.listFiles())
				.filter(file -> file.getName().toLowerCase().endsWith(".png"))
				.mapToInt(file -> Integer.parseInt(file.getName().toLowerCase().replace(".png", "").trim()))
				.max().orElse(50);
	}

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
			if (k2 == 0) {
				transparent[textureId] = true;
			}
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
		textures[1] = textures[24];
		int customTextureMax = getHighestCustomTexId() + 1;
		textures = Arrays.copyOf(textures, customTextureMax);
		transparent = Arrays.copyOf(transparent, customTextureMax);

			for(int i = 0;i<customTextureMax;i++){
				try {
					File texturePNG = getTextureFromFile(i);
					if(texturePNG.exists()) {
						IndexedImage texture = new IndexedImage(Files.readAllBytes(texturePNG.toPath()), 0, 0);
						texture.setResizeHeight(128);
						texture.setResizeWidth(128);
						texture.resize();

						textures[i] = new PalettedTexture(texture);
						transparent[i] = Arrays.asList(texture.getImageRaster()).contains(Color.MAGENTA.getRGB());
					}
				} catch(Exception ex){
					ex.printStackTrace();
				}
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



}
