package com.jagex.draw.textures;

import com.jagex.io.Buffer;
import com.jagex.util.ColourUtils;

public class AlphaPalettedTexture extends PalettedTexture {

	public AlphaPalettedTexture(int width, int height, Buffer buffer) {
		super(width, height, buffer);
		
		for(int i = 0;i<originalPixels.length;i++) {
			byte alpha = buffer.readByte();
			if(alpha == 0)
				originalPixels[i] = 0;
			else
				originalPixels[i] = ColourUtils.addAlpha(originalPixels[i], alpha);
		}

		generatePalette();
	}
	
	@Override
	public boolean supportsAlpha() {
		return true;
	}

}
