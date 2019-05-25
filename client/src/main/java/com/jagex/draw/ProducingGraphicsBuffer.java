package com.jagex.draw;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.util.Arrays;

import com.jagex.draw.raster.GameRaster;

import net.coobird.thumbnailator.Thumbnails;

public class ProducingGraphicsBuffer implements ImageProducer, ImageObserver {

	protected ImageConsumer consumer;
	protected GameRaster raster;
	private int height;
	protected Image image;
	protected ColorModel model;
	protected int[] pixels;
	private int width;

	protected ProducingGraphicsBuffer() {

	}

	public ProducingGraphicsBuffer(Component component, int width, int height) {
		this.width = width;
		this.height = height;

		this.raster = new GameRaster();
		pixels = new int[width * height];
		model = new DirectColorModel(32, 0xFF0000, 65280, 255);
		image = component.createImage(this);

		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		initializeRasterizer();
	}
	
	public ProducingGraphicsBuffer(Component component, int width, int height, GameRaster raster) {
		this.width = width;
		this.height = height;

		this.raster = raster;
		pixels = new int[width * height];
		model = new DirectColorModel(32, 0xFF0000, 65280, 255);
		image = component.createImage(this);

		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		initializeRasterizer();
	}

	@Override
	public synchronized void addConsumer(ImageConsumer consumer) {
		this.consumer = consumer;
		consumer.setDimensions(width, height);
		consumer.setProperties(null);
		consumer.setColorModel(model);
		consumer.setHints(14);
	}

	public void clear(int colour) {
		Arrays.fill(pixels, colour);
	}

	public void drawImage(Graphics graphics, int x, int y) {
		setPixels();
		graphics.drawImage(image, x, y, this);
	}
	
	public void drawImage(Graphics graphics, int x, int y, int w, int h) {
		setPixels();
		graphics.drawImage(image, x, y, w, h, this);
	}

	public void drawImageMax(Graphics graphics, int x, int y, int w, int h) {
		setPixels();
		try {
			BufferedImage img = Thumbnails.of((BufferedImage) image).size(w, h).asBufferedImage();
			graphics.drawImage(img, x, y, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getHeight() {
		return height;
	}

	public Image getImage() {
		return image;
	}

	public int getPixel(int index) {
		return pixels[index];
	}

	public int[] getPixels() {
		return pixels;
	}

	public int getWidth() {
		return width;
	}

	@Override
	public boolean imageUpdate(Image image, int flags, int x, int y, int width, int height) {
		return true;
	}

	public void initializeRasterizer() {
		raster.init(height, width, pixels);
	}

	@Override
	public synchronized boolean isConsumer(ImageConsumer consumer) {
		return this.consumer == consumer;
	}

	@Override
	public synchronized void removeConsumer(ImageConsumer consumer) {
		if (this.consumer == consumer) {
			this.consumer = null;
		}
	}

	@Override
	public void requestTopDownLeftRightResend(ImageConsumer consumer) {
		System.out.println("TDLR");
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setPixel(int index, int colour) {
		pixels[index] = colour;
	}

	public synchronized void setPixels() {
		if (consumer == null)
			return;
		System.out.println("consumer not null");

		consumer.setPixels(0, 0, width, height, model, pixels, 0, width);
		consumer.imageComplete(2);
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public void startProduction(ImageConsumer consumer) {
		System.out.println("add consumer");
		addConsumer(consumer);
	}

	public GameRaster getRaster() {
		return raster;
	}

	
}