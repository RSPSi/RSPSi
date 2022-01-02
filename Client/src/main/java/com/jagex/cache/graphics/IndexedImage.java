package com.jagex.cache.graphics;

import com.displee.cache.index.archive.Archive;
import com.google.common.collect.Lists;
import com.jagex.draw.raster.GameRaster;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.io.Buffer;
import com.jagex.util.ColourUtils;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.PixelGrabber;
import java.nio.ByteBuffer;
import java.util.List;

public final class IndexedImage extends GameRaster {

	private static void draw(int height, int[] raster, byte[] image, int destStep, int destIndex, int width,
			int sourceIndex, int[] palette, int sourceStep) {
		int minX = -(width >> 2);
		width = -(width & 3);

		for (int y = -height; y < 0; y++) {
			for (int x = minX; x < 0; x++) {
				byte pixel = image[sourceIndex++];

				if (pixel != 0) {
					raster[destIndex++] = palette[pixel & 0xff];
				} else {
					destIndex++;
				}

				pixel = image[sourceIndex++];
				if (pixel != 0) {
					raster[destIndex++] = palette[pixel & 0xff];
				} else {
					destIndex++;
				}

				pixel = image[sourceIndex++];
				if (pixel != 0) {
					raster[destIndex++] = palette[pixel & 0xff];
				} else {
					destIndex++;
				}

				pixel = image[sourceIndex++];
				if (pixel != 0) {
					raster[destIndex++] = palette[pixel & 0xff];
				} else {
					destIndex++;
				}
			}

			for (int x = width; x < 0; x++) {
				byte pixel = image[sourceIndex++];
				if (pixel != 0) {
					raster[destIndex++] = palette[pixel & 0xff];
				} else {
					destIndex++;
				}
			}

			destIndex += destStep;
			sourceIndex += sourceStep;
		}
	}

	private int drawOffsetX;
	private int drawOffsetY;
	private int height;
	private int[] palette;
	private byte[] raster;
	private int resizeHeight;
	private int resizeWidth;

	private int width;
	
	public IndexedImage(byte[] data, int drawOffsetX, int drawOffsetY) {
		this.drawOffsetX = drawOffsetX;
		this.drawOffsetY = drawOffsetY;
		Image image =  Toolkit.getDefaultToolkit().createImage(data);
		ImageIcon sprite = new ImageIcon(image);
		width = sprite.getIconWidth();
		height = sprite.getIconHeight();
		resizeWidth = width;
		resizeHeight = height;
		drawOffsetX = 0;
		drawOffsetY = 0;
		int[] myPixels = new int[width * height];
		PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, width, height, myPixels, 0, width);
		try {
			pixelgrabber.grabPixels();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		image = null;
		for(int x = 0; x < width; x++) {				
			for(int y = 0; y < height; y++) {	
				int pixel = myPixels[x + y * width];
				int alpha = ColourUtils.getAlpha(pixel);
				myPixels[x + y * width] = alpha == 0 ? 0xff00ff : ColourUtils.getRGB(pixel);
			}
		}
		List<Integer> colourList = Lists.newArrayList();
		colourList.add(0);
		for(int pixel : myPixels) {
			if(!colourList.contains(pixel))
				colourList.add(pixel);
		}
		
		palette = new int[colourList.size()];
		for(int i = 0;i<colourList.size();i++)
			palette[i] = colourList.get(i);
		
		raster = new byte[width * height];
		for(int x = 0; x < width; x++) {				
			for(int y = 0; y < height; y++) {					
				int pixel = myPixels[x + y * width];
				if(pixel == 0xff00ff)
					continue;
				raster[x + y * width] = (byte) colourList.indexOf(pixel);
			}
		}
	}

	public IndexedImage(Archive archive, String name, int id) throws Exception {
		Buffer image = new Buffer(archive.file(name + ".dat"));
		Buffer meta = new Buffer(archive.file("index.dat"));
		

		meta.setPosition(image.readUShort());
		resizeWidth = meta.readUShort();
		resizeHeight = meta.readUShort();

		int colours = meta.readUByte();
		palette = new int[colours];

		for (int index = 0; index < colours - 1; index++) {
			palette[index + 1] = meta.readUTriByte();
		}

		for (int i = 0; i < id; i++) {
			meta.setPosition(meta.getPosition() + 2);
			image.setPosition(image.getPosition() + meta.readUShort() * meta.readUShort());
			meta.setPosition(meta.getPosition() + 1);
		}

		drawOffsetX = meta.readUByte();
		drawOffsetY = meta.readUByte();
		width = meta.readUShort();
		height = meta.readUShort();
		int type = meta.readUByte();
		int pixels = width * height;
		raster = new byte[pixels];

		if (type == 0) {
			for (int index = 0; index < pixels; index++) {
				raster[index] = image.readByte();
			}
		} else {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					raster[x + y * width] = image.readByte();
				}
			}
		}
	}

	private static int colorToArgb(int rgb) {
		int r = rgb >> 16 & 0xFF;
		int g = rgb >> 8 & 0xFF;
		int b = rgb & 0xFF;
		return 0xFF << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0;
	}

	public void downscale() {
		resizeWidth /= 2;
		resizeHeight /= 2;
		byte[] raster = new byte[resizeWidth * resizeHeight];
		int sourceIndex = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				raster[(x + drawOffsetX >> 1) + (y + drawOffsetY >> 1) * resizeWidth] = this.raster[sourceIndex++];
			}
		}

		this.raster = raster;
		width = resizeWidth;
		height = resizeHeight;
		drawOffsetX = 0;
		drawOffsetY = 0;
	}
	
	public void draw(int x, int y) {
		draw(GameRasterizer.getInstance(), x, y);
	}

	public void draw(GameRasterizer rasterizer, int x, int y) {
		x += drawOffsetX;
		y += drawOffsetY;
		int destOffset = x + y * rasterizer.getWidth();
		int sourceOffset = 0;
		int height = this.height;
		int width = this.width;
		int destStep = rasterizer.getWidth() - width;
		int sourceStep = 0;

		if (y < rasterizer.getClipBottom()) {
			int dy = rasterizer.getClipBottom() - y;
			height -= dy;
			y = rasterizer.getClipBottom();
			sourceOffset += dy * width;
			destOffset += dy * rasterizer.getWidth();
		}

		if (y + height > rasterizer.getClipTop()) {
			height -= y + height - rasterizer.getClipTop();
		}

		if (x < rasterizer.getClipLeft()) {
			int dx = rasterizer.getClipLeft() - x;
			width -= dx;
			x = rasterizer.getClipLeft();
			sourceOffset += dx;
			destOffset += dx;
			sourceStep += dx;
			destStep += dx;
		}

		if (x + width > rasterizer.getClipRight()) {
			int dx = x + width - rasterizer.getClipRight();
			width -= dx;
			sourceStep += dx;
			destStep += dx;
		}

		if (width > 0 && height > 0) {
			draw(height, rasterizer.getRaster(), raster, destStep, destOffset, width, sourceOffset, palette, sourceStep);
		}
	}

	public void flipHorizontally() {
		byte[] raster = new byte[width * height];
		int pixel = 0;

		for (int y = 0; y < height; y++) {
			for (int x = width - 1; x >= 0; x--) {
				raster[pixel++] = this.raster[x + y * width];
			}
		}

		this.raster = raster;
		drawOffsetX = resizeWidth - width - drawOffsetX;
	}

	public void flipVertically() {
		byte[] raster = new byte[width * height];
		int pixel = 0;

		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				raster[pixel++] = this.raster[x + y * width];
			}
		}

		this.raster = raster;
		drawOffsetY = resizeHeight - height - drawOffsetY;
	}

	public WritableImage getAsFXImage() {
		int height = this.height;
		int width = this.width;
		//System.out.println(width + ":" + height);
		WritableImage image = new WritableImage(this.width, this.height);
		int[] argbPalette = new int[palette.length];
		for (int i = 0; i < argbPalette.length; i++) {
			argbPalette[i] = colorToArgb(palette[i]);
		}
		PixelFormat<ByteBuffer> f = PixelFormat.createByteIndexedInstance(argbPalette);
		image.getPixelWriter().setPixels(0, 0, width, height, f, raster, 0, width);
		return image;
	}

	public int getDrawOffsetX() {
		return drawOffsetX;
	}

	public int getDrawOffsetY() {
		return drawOffsetY;
	}

	public int getHeight() {
		return height;
	}

	public int[] getPalette() {
		return palette;
	}

	public byte getPixel(int index) {
		return raster[index];
	}

	public byte[] getImageRaster() {
		return raster;
	}

	public int getResizeHeight() {
		return resizeHeight;
	}

	public int getResizeWidth() {
		return resizeWidth;
	}

	public int getWidth() {
		return width;
	}

	public void offsetColour(int redOffset, int greenOffset, int blueOffset) {
		for (int index = 0; index < palette.length; index++) {
			int red = palette[index] >> 16 & 0xff;
			red += redOffset;

			if (red < 0) {
				red = 0;
			} else if (red > 255) {
				red = 255;
			}

			int green = palette[index] >> 8 & 0xff;
			green += greenOffset;

			if (green < 0) {
				green = 0;
			} else if (green > 255) {
				green = 255;
			}

			int blue = palette[index] & 0xff;
			blue += blueOffset;

			if (blue < 0) {
				blue = 0;
			} else if (blue > 255) {
				blue = 255;
			}

			palette[index] = (red << 16) + (green << 8) + blue;
		}
	}

	public void resize() {
		if (width == resizeWidth && height == resizeHeight)
			return;

		byte[] raster = new byte[resizeWidth * resizeHeight];
		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				raster[x + drawOffsetX + (y + drawOffsetY) * resizeWidth] = this.raster[i++];
			}
		}

		this.raster = raster;
		width = resizeWidth;
		height = resizeHeight;
		drawOffsetX = 0;
		drawOffsetY = 0;
	}

	public void setDrawOffsetX(int drawOffsetX) {
		this.drawOffsetX = drawOffsetX;
	}

	public void setDrawOffsetY(int drawOffsetY) {
		this.drawOffsetY = drawOffsetY;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setPalette(int[] palette) {
		this.palette = palette;
	}

	public void setRaster(byte[] raster) {
		this.raster = raster;
	}

	public void setResizeHeight(int resizeHeight) {
		this.resizeHeight = resizeHeight;
	}

	public void setResizeWidth(int resizeWidth) {
		this.resizeWidth = resizeWidth;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}