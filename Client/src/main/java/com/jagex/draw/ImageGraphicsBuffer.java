package com.jagex.draw;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Hashtable;

import com.jagex.draw.raster.GameRaster;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.util.ColourUtils;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

public class ImageGraphicsBuffer extends ProducingGraphicsBuffer {
	
	public WritableImage finalImage;

	public ImageGraphicsBuffer(int width, int height, GameRaster raster) {
		super.pixels = new int[width * height + 1];
		super.model = new DirectColorModel(32, 0xff0000, 65280, 255);
		super.image = new BufferedImage(model,
				Raster.createWritableRaster(model.createCompatibleSampleModel(width, height),
						new DataBufferInt(pixels, pixels.length), null),
				false, new Hashtable<>());
		super.raster = raster;
		finalImage = new WritableImage(width, height);
		super.setWidth(width);
		super.setHeight(height);
	
		super.initializeRasterizer();
		
	}
	
	public ImageGraphicsBuffer(int width, int height) {
		this(width, height, new GameRasterizer());
	}

	public void clearPixels() {
		this.clearPixels(0);
	}

	public void clearPixels(int rgb) {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0xFF000000 | rgb;
		}
	}

	@Override
	public void drawImage(Graphics graphics, int x, int y) {
		graphics.drawImage(image, x, y, this);
	}

	public void finalize() {
		int[] pixelCopy = Arrays.copyOf(pixels, pixels.length);
		for(int i = 0;i<pixelCopy.length;i++)
			pixelCopy[i] = 0xFF000000 | pixelCopy[i];
		finalImage.getPixelWriter().setPixels(0, 0, getWidth(), getHeight(), PixelFormat.getIntArgbInstance(), IntBuffer.wrap(pixelCopy), getWidth());
	}
	
	@Override
	public BufferedImage getImage() {
		return null;
	}
	
	public WritableImage getFXImage() {
		return finalImage;
	}
	
	public Graphics2D getGraphics() {
		return (Graphics2D) image.getGraphics();
	}

}
