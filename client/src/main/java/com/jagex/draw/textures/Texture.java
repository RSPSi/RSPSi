package com.jagex.draw.textures;

import java.nio.IntBuffer;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.jagex.util.ColourUtils;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

public abstract class Texture {

	protected int[] palette;
	protected int[] paletteIndexes;
	protected int[] pixels;
	protected int[] originalPixels;
	protected int width;
	protected int height;
	protected int averageTextureColour;
	protected double brightness = 0.8;
	
	public Texture(int width, int height) {
		this.width = width;
		this.height = height;
		this.originalPixels = new int[width * height];
		this.pixels = new int[width * height];
	}
	
	private void generatePixels() {
		for(int i = 0;i<paletteIndexes.length;i++) {
			pixels[i] = palette[paletteIndexes[i]];
		}
	}
	
	public int[] getPixels() {
		return pixels;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}
	
	public int getPixel(int x, int y) {
		return pixels[x + (y * x)];
	}
	
	public int getAlpha(int x, int y) {
		return supportsAlpha() ? ColourUtils.getAlpha(getPixel(x, y)) : 0;
	}
	
	public int getAlpha(int index) {
		return supportsAlpha() ? ColourUtils.getAlpha(getPixel(index)) : 0;
	}
	
	public int getPixel(int index) {
		return pixels[index];
	}
	
	public WritableImage getAsFXImage() {
		int height = this.height;
		int width = this.width;
		//System.out.println(width + ":" + height);
		WritableImage image = new WritableImage(this.width, this.height);
		
		PixelFormat<IntBuffer> f = PixelFormat.getIntArgbInstance();
		image.getPixelWriter().setPixels(0, 0, width, height, f, ColourUtils.getARGB(pixels), 0, width);
		return image;
	}
	

	public int averageTextureColour() {
		if (averageTextureColour > 0)
			return averageTextureColour;

		int rgb = averageColorForPixels(pixels);
		averageTextureColour = rgb;
		return rgb;
	}
	
	private static int averageColorForPixels(int[] pixels) {
		int redTotal = 0;
		int greenTotal = 0;
		int blueTotal = 0;
		int totalPixels = pixels.length;

		for (int i = 0; i < totalPixels; i++) {
			if (pixels[i] == 0xff00ff) {
				totalPixels--;
				continue;
			}

			redTotal += pixels[i] >> 16 & 0xff;
			greenTotal += pixels[i] >> 8 & 0xff;
			blueTotal += pixels[i] & 0xff;
		}

		int averageRGB = (redTotal / totalPixels << 16) + (greenTotal / totalPixels << 8) + blueTotal / totalPixels;
		if (averageRGB == 0) {
			averageRGB = 1;
		}

		return averageRGB;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void setBrightness(double brightness) {
		this.brightness = brightness;
		generatePalette();
		generatePixels();
	}
	
	public void generatePalette() {
		List<Integer> colours = Lists.newArrayList();
		for(int pixel = 0;pixel<originalPixels.length;pixel++) {
			int newPixel = ColourUtils.exponent(originalPixels[pixel], brightness);
		/*	if((newPixel & 0xf8f8ff) == 0 && pixel != 0) {
				newPixel = 1;
			}*/
			if(!colours.contains(newPixel))
				colours.add(newPixel);
		}
		
		paletteIndexes = new int[originalPixels.length];
		for(int pixel = 0;pixel<originalPixels.length;pixel++) {
			int newPixel = ColourUtils.exponent(originalPixels[pixel], brightness);
			/*if((newPixel & 0xf8f8ff) == 0 && pixel != 0) {
				newPixel = 1;
			}*/
			paletteIndexes[pixel] = colours.indexOf(newPixel);
		}
		
		palette = Ints.toArray(colours);
		averageTextureColour = 0;
		
		generatePixels();
	}

	public int[] getPalette() {
		return palette;
	}

	public int[] getPaletteIndexes() {
		return paletteIndexes;
	}

	public double getBrightness() {
		return brightness;
	}
	
	public abstract boolean supportsAlpha();
}
