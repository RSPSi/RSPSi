package com.jagex.draw.textures;

import com.jagex.io.Buffer;

public class RGBTexture extends Texture {

	public RGBTexture(int width, int height, Buffer buffer) {
		super(width, height);
		for (int i = 0; i < originalPixels.length; i++)
		{
			int pixel = buffer.readUTriByte();

		
			originalPixels[i] = pixel | 0xff000000;
		}

		generatePalette();
	}

	@Override
	public boolean supportsAlpha() {
		return false;
	}
	
	

}
