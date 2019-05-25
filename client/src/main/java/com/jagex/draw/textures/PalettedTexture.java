package com.jagex.draw.textures;

import com.jagex.cache.graphics.IndexedImage;
import com.jagex.io.Buffer;

public class PalettedTexture extends Texture {
	

	public PalettedTexture(IndexedImage image) {
		super(image.getWidth(), image.getHeight());

		for(int i = 0;i<originalPixels.length;i++) {
			int paletteIdx = image.getImageRaster()[i];
			originalPixels[i] = image.getPalette()[paletteIdx];
		}

		generatePalette();
	}
	

	public PalettedTexture(int width, int height, Buffer buffer) {
		super(width, height);

		int paletteSize = buffer.readUByte();
		int[] palette = new int[paletteSize + 1];
		for (int i = 1; i <= paletteSize; i++)
		{
			palette[i] = buffer.readUTriByte() | 0xff000000;
		}
		
		for(int i = 0;i<originalPixels.length;i++) {
			int paletteIdx = buffer.readByte();
			originalPixels[i] = palette[paletteIdx & 0xff];
		}
		generatePalette();
	}

	@Override
	public boolean supportsAlpha() {
		return false;
	}

}
