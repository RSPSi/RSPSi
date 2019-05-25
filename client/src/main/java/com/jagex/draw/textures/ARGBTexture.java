package com.jagex.draw.textures;

import com.jagex.io.Buffer;

public class ARGBTexture extends Texture {
	
	private boolean hasAlpha;

	public ARGBTexture(int width, int height, Buffer buffer) {
		super(width, height);
		for (int i = 0; i < originalPixels.length; i++)
		{
			int pixel = buffer.readInt();
			if ((pixel & 0xff000000) == 0)
				pixel = 0;

			int alpha = pixel & 0xff000000;
			if (alpha != 0xff000000)
			{
				if (alpha != 0)
					hasAlpha = true;

			}
			originalPixels[i] = pixel;
		}

		generatePalette();
	}

	@Override
	public boolean supportsAlpha() {
		return hasAlpha;
	}
	
	

}
