package com.rspsi.util;

import java.awt.image.BufferedImage;

import net.coobird.thumbnailator.filters.ImageFilter;

public class GrayscaleFilter implements ImageFilter {

	@Override
	public BufferedImage apply(BufferedImage img) {
		BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		 for (int x = 0; x < img.getWidth(); x++) {
		        for (int y = 0; y < img.getHeight(); y++) {
		            int pixel = img.getRGB(x, y);

		            int red = ((pixel >> 16) & 0xff);
		            int green = ((pixel >> 8) & 0xff);
		            int blue = (pixel & 0xff);

		            int grayLevel = red + green + blue / 3;
		            grayLevel = 255 - grayLevel; // Inverted the grayLevel value here.
		            int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;

		            newImage.setRGB(x, y, -gray); // AMENDED TO -gray here.
		        }
		    }
		return newImage;
	}

}
