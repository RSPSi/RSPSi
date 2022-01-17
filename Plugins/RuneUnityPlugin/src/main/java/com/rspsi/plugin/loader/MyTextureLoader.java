package com.rspsi.plugin.loader;


import com.displee.cache.index.archive.Archive;

import com.jagex.cache.def.TextureDef;
import com.jagex.cache.graphics.IndexedImage;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.textures.ARGBTexture;
import com.jagex.draw.textures.AlphaPalettedTexture;
import com.jagex.draw.textures.PalettedTexture;
import com.jagex.draw.textures.RGBTexture;
import com.jagex.draw.textures.Texture;
import com.jagex.io.Buffer;
import com.jagex.net.ResourceProvider;
import com.rspsi.cache.CacheFileType;
import com.rspsi.core.misc.FixedHashMap;
import com.rspsi.options.Options;


public class MyTextureLoader extends TextureLoader {
	
	private double clientBrightness;
	private FixedHashMap<Integer, int[]> textureCache = new FixedHashMap<Integer, int[]>(20);
	private Texture[] sdTextures, hdTextures;
	private boolean[] sdTextureTransparent;
	private boolean[] hdLoaded;
	private ResourceProvider provider;
	
	public MyTextureLoader(int sdCount, int hdCount, ResourceProvider provider) {
		this.sdTextures = new Texture[sdCount];
		this.sdTextureTransparent = new boolean[sdCount];
		this.hdTextures = new Texture[hdCount];
		this.hdLoaded = new boolean[hdCount];
		this.provider = provider;
	}

	@Override
	public Texture forId(int id) {
		if(Options.hdTextures.get()) {

			if (id < 0 || id >= hdTextures.length)
				return null;
			if (id == 0)
				id = 24;

			if (hdLoaded[id])
				return hdTextures[id];
			provider.requestFile(CacheFileType.TEXTURE, id);
			
			return hdTextures[id];
		} else {
			if (id < 0 || id >= sdTextures.length)
				return null;
			if (id == 1) {
				id = 24;
			}
			return sdTextures[id];
		}
	}


	@Override
	public int[] getPixels(int textureId) {
		
		if(Options.hdTextures.get()) {
			if (textureId == 0)
				textureId = 24;
			Texture texture = forId(textureId);
			if(texture == null)
				return null;
			if(textureCache.contains(textureId))
				return textureCache.get(textureId);
			int[] texels = new int[0x10000];
			if (texture.getWidth() == 64)
				for (int y = 0; y < 128; y++)
					for (int x = 0; x < 128; x++)
						texels[x + (y << 7)] = texture.getPixel((x >> 1) + ((y >> 1) << 6));


			else
				for (int texelPtr = 0; texelPtr < 16384; texelPtr++)
					texels[texelPtr] = texture.getPixel(texelPtr);


			TextureDef def = textureId >= 0 && textureId < TextureDef.textures.length ? TextureDef.textures[textureId]:null;
			int blendType = def != null ? def.anInt1226 : 0;
			if (blendType != 1 && blendType != 2)
				blendType = 0;

			for (int texelPtr = 0; texelPtr < 16384; texelPtr++) {
				int texel = texels[texelPtr];
				int alpha;
				if (blendType == 2)
					alpha = texel >>> 24;
				else if (blendType == 1)
					alpha = texel != 0 ? 0xff:0;

				else
				{
					alpha = texel >>> 24;
					if (def != null && !def.aBoolean1223)
						alpha /= 5;

				}
				texel &= 0xffffff;
				if (textureId == 1 || textureId == 24)
				texel = adjustBrightnessLinear(texel, 379);
				else
					texel = adjustBrightnessLinear(texel, 179);
				if (textureId == 1 || textureId == 24)
				texel = adjustBrightness(texel, 0.90093F);
				else
					texel = adjustBrightness(texel, brightness);
				texel &= 0xf8f8ff;
				texels[texelPtr] = texel | (alpha << 24);
				texels[16384 + texelPtr] = ((texel - (texel >>> 3)) & 0xf8f8ff) | (alpha << 24);
				texels[32768 + texelPtr] = ((texel - (texel >>> 2)) & 0xf8f8ff) | (alpha << 24);
				texels[49152 + texelPtr] = ((texel - (texel >>> 3) - (texel >>> 3)) & 0xf8f8ff) | (alpha << 24);
			}
			textureCache.put(textureId, texels);
			return texels;
		} else {
			if (textureId == 1) {
				textureId = 24;
			}
			Texture texture = forId(textureId);
			if(texture == null)
				return null;

			if(textureCache.contains(textureId))
				return textureCache.get(textureId);
			
			int[] texels = new int[0x10000];
			
			texture.setBrightness(clientBrightness);
			if (texture.getWidth() == 64)
				for (int y = 0; y < 128; y++)
					for (int x = 0; x < 128; x++)
						texels[x + (y << 7)] = texture.getPixel((x >> 1) + ((y >> 1) << 6));


			else
				for (int texelPtr = 0; texelPtr < 16384; texelPtr++)
					texels[texelPtr] = texture.getPixel(texelPtr);
			
			sdTextureTransparent[textureId] = false;
			for (int l1 = 0; l1 < 16384; l1++) {
				texels[l1] &= 0xf8f8ff;
				int k2 = texels[l1];
				if (k2 == 0)
					sdTextureTransparent[textureId] = true;
				texels[16384 + l1] = k2 - (k2 >>> 3) & 0xf8f8ff;
				texels[32768 + l1] = k2 - (k2 >>> 2) & 0xf8f8ff;
				texels[49152 + l1] = k2 - (k2 >>> 2) - (k2 >>> 3) & 0xf8f8ff;
			}


			textureCache.put(textureId, texels);
		return texels;
		}
	}

	@Override
	public void init(Archive archive) {
		
		//Init SD textures
		for (int j = 0; j < sdTextures.length; j++)
			try {
				IndexedImage texture = new IndexedImage(archive, String.valueOf(j), 0);
				texture.resize();
				sdTextures[j] = new PalettedTexture(texture);
			} catch (Exception _ex) {
				_ex.printStackTrace();
			}
	}

	public void load(int id, byte[] data) {
		hdLoaded[id] = true;
		if (data != null && data.length >= 5)
		{
			Buffer buffer = new Buffer(data);
			int type = buffer.readUByte();
			int width = buffer.readUShort();
			int height = buffer.readUShort();
			if (type == 0)
				hdTextures[id] = new PalettedTexture(width, height, buffer);
			else if (type == 1)
				hdTextures[id] = new RGBTexture(width, height, buffer);
			else if (type == 2)
				hdTextures[id] = new AlphaPalettedTexture(width, height, buffer);
			else if (type == 3)
				hdTextures[id] = new ARGBTexture(width, height, buffer);

		}
	}

	@Override
	public boolean isTransparent(int id) {
		if(forId(id) == null)
			return false;
		if(Options.hdTextures.get()) {
			return false;//TODO
		} else 
			return sdTextureTransparent[id];
	}

	@Override
	public void setBrightness(double arg0) {
		textureCache.clear();
		clientBrightness = arg0;
		
	}	
	
	private static float brightness = 1.0F;

	private static int adjustBrightness(int rgb, float brightness) {
		return ((int) ((float) Math.pow((double) ((float) (rgb >>> 16) / 256.0F), (double) brightness) * 256.0F) << 16) | 
			((int) ((float) Math.pow((double) ((float) ((rgb >>> 8) & 0xff) / 256.0F), (double) brightness) * 256.0F) << 8) | 
			(int) ((float) Math.pow((double) ((float) (rgb & 0xff) / 256.0F), (double) brightness) * 256.0F);
	}


	private static int adjustBrightnessLinear(int rgb, int factor)
	{
		return ((((rgb >>> 16) * factor) & 0xff00) << 8) | 
			((((rgb >>> 8) & 0xff) * factor) & 0xff00) | 
			(((rgb & 0xff) * factor) >> 8);
	}

	public void clearCache() {
		textureCache.clear();
	}
	

	@Override
	public int count() {
		if(Options.hdTextures.get()) {
		return hdTextures.length;//TODO
	} else 
		return sdTextures.length;
	}


}
