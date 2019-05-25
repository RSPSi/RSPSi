package com.jagex.draw;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.util.Hashtable;

import com.jagex.draw.raster.GameRaster;
import com.jagex.draw.raster.GameRasterizer;

import net.coobird.thumbnailator.makers.FixedSizeThumbnailMaker;
import net.coobird.thumbnailator.resizers.DefaultResizerFactory;
import net.coobird.thumbnailator.resizers.Resizer;

public class ImageGraphicsBuffer extends ProducingGraphicsBuffer {
	
	private BufferedImage finalImage;

	public ImageGraphicsBuffer(int width, int height, GameRaster raster) {
		super.pixels = new int[width * height + 1];
		super.model = new DirectColorModel(32, 0xff0000, 65280, 255);
		super.image = new BufferedImage(model,
				Raster.createWritableRaster(model.createCompatibleSampleModel(width, height),
						new DataBufferInt(pixels, pixels.length), null),
				false, new Hashtable<>());
		super.raster = raster;
		finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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
			pixels[i] = rgb;
		}
	}

	@Override
	public void drawImage(Graphics graphics, int x, int y) {
		graphics.drawImage(image, x, y, this);
	}

	public void finalize() {
		finalImage.getGraphics().drawImage(image, 0, 0, this);
	}
	
	@Override
	public BufferedImage getImage() {
		return finalImage;
	}
	
	public Graphics2D getGraphics() {
		return (Graphics2D) image.getGraphics();
	}
	
	public BufferedImage getImage(int width, int height) {
		Resizer resizer = DefaultResizerFactory.getInstance().getResizer(new Dimension(finalImage.getWidth(), finalImage.getHeight()), new Dimension(width, height));
		BufferedImage scaledImage = new FixedSizeThumbnailMaker(width, height, true, true).resizer(resizer).make(finalImage);
		return scaledImage;
	}
	
	
}
