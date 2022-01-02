package com.jagex.cache.graphics;

import com.displee.cache.index.archive.Archive;
import com.jagex.draw.raster.GameRaster;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.io.Buffer;
import com.jagex.util.ByteBufferUtils;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class Sprite extends GameRaster {

	private static void draw(int sourceIndex, int width, int[] dest, int k, int[] source, int sourceStep, int height,
			int destStep, int alpha, int destIndex) {
		int ialpha = 256 - alpha;

		for (int y = -height; y < 0; y++) {
			for (int x = -width; x < 0; x++) {
				k = source[sourceIndex++];
				if (k != 0) {
					int current = dest[destIndex];
					dest[destIndex++] = ((k & 0xff00ff) * alpha + (current & 0xff00ff) * ialpha & 0xff00ff00)
							+ ((k & 0xff00) * alpha + (current & 0xff00) * ialpha & 0xff0000) >> 8;
				} else {
					destIndex++;
				}
			}

			destIndex += destStep;
			sourceIndex += sourceStep;
		}
	}

	private static void draw(int[] raster, int[] image, int colour, int sourceIndex, int destIndex, int width,
                             int height, int destStep, int sourceStep) {
		int minX = -(width >> 2);
		width = -(width & 3);

		for (int y = -height; y < 0; y++) {
			for (int x = minX; x < 0; x++) {
				colour = image[sourceIndex++];
				if (colour != 0) {
					raster[destIndex++] = colour;
				} else {
					destIndex++;
				}
				colour = image[sourceIndex++];

				if (colour != 0) {
					raster[destIndex++] = colour;
				} else {
					destIndex++;
				}
				colour = image[sourceIndex++];

				if (colour != 0) {
					raster[destIndex++] = colour;
				} else {
					destIndex++;
				}
				colour = image[sourceIndex++];

				if (colour != 0) {
					raster[destIndex++] = colour;
				} else {
					destIndex++;
				}
			}

			for (int k2 = width; k2 < 0; k2++) {
				colour = image[sourceIndex++];
				if (colour != 0) {
					raster[destIndex++] = colour;
				} else {
					destIndex++;
				}
			}

			destIndex += destStep;
			sourceIndex += sourceStep;
		}
	}

	private static void drawBehind(byte[] image, int[] input, int width, int height, int[] output, int in, int destStep,
			int destIndex, int sourceStep, int sourceIndex) {
		int l1 = -(width >> 2);
		width = -(width & 3);

		for (int y = -height; y < 0; y++) {
			for (int x = l1; x < 0; x++) {

				in = input[sourceIndex++];
				if (in != 0 && image[destIndex] == 0) {
					output[destIndex++] = in;
				} else {
					destIndex++;
				}

				in = input[sourceIndex++];
				if (in != 0 && image[destIndex] == 0) {
					output[destIndex++] = in;
				} else {
					destIndex++;
				}

				in = input[sourceIndex++];
				if (in != 0 && image[destIndex] == 0) {
					output[destIndex++] = in;
				} else {
					destIndex++;
				}

				in = input[sourceIndex++];
				if (in != 0 && image[destIndex] == 0) {
					output[destIndex++] = in;
				} else {
					destIndex++;
				}
			}

			for (int l2 = width; l2 < 0; l2++) {
				in = input[sourceIndex++];
				if (in != 0 && image[destIndex] == 0) {
					output[destIndex++] = in;
				} else {
					destIndex++;
				}
			}

			destIndex += destStep;
			sourceIndex += sourceStep;
		}
	}

	private static void method347(int destIndex, int width, int height, int sourceStep, int sourceIndex, int destStep,
			int[] source, int[] raster) {
		int minX = -(width >> 2);
		width = -(width & 3);

		for (int y = -height; y < 0; y++) {
			for (int x = minX; x < 0; x++) {
				raster[destIndex++] = source[sourceIndex++];
				raster[destIndex++] = source[sourceIndex++];
				raster[destIndex++] = source[sourceIndex++];
				raster[destIndex++] = source[sourceIndex++];
			}

			for (int k2 = width; k2 < 0; k2++) {
				raster[destIndex++] = source[sourceIndex++];
			}

			destIndex += destStep;
			sourceIndex += sourceStep;
		}
	}

	private int height;
	private int horizontalOffset;
	private int[] raster;

	private int resizeHeight;

	private int resizeWidth;

	private int verticalOffset;

	private int width;
	
	/**
	 * This flag indicates that the pixels should be read vertically instead of
	 * horizontally.
	 */
	public static final int FLAG_VERTICAL = 0x01;

	/**
	 * This flag indicates that every pixel has an alpha, as well as red, green
	 * and blue, component.
	 */
	public static final int FLAG_ALPHA = 0x02;

	public static Sprite[] unpackAndDecode(ByteBuffer buffer) {
		/* find the size of this sprite set */
		buffer.position(buffer.limit() - 2);
		int size = buffer.getShort() & 0xFFFF;

		/* allocate arrays to store info */
		int[] offsetsX = new int[size];
		int[] offsetsY = new int[size];
		int[] subWidths = new int[size];
		int[] subHeights = new int[size];

		/* read the width, height and palette size */
		buffer.position(buffer.limit() - size * 8 - 7);
		int width = buffer.getShort() & 0xFFFF;
		int height = buffer.getShort() & 0xFFFF;
		int[] palette = new int[(buffer.get() & 0xFF) + 1];

		/* and allocate an object for this sprite set */

		/* read the offsets and dimensions of the individual sprites */
		for (int i = 0; i < size; i++) {
			offsetsX[i] = buffer.getShort() & 0xFFFF;
		}
		for (int i = 0; i < size; i++) {
			offsetsY[i] = buffer.getShort() & 0xFFFF;
		}
		for (int i = 0; i < size; i++) {
			subWidths[i] = buffer.getShort() & 0xFFFF;
		}
		for (int i = 0; i < size; i++) {
			subHeights[i] = buffer.getShort() & 0xFFFF;
		}

		/* read the palette */
		buffer.position(buffer.limit() - size * 8 - 7 - (palette.length - 1) * 3);
		palette[0] = 0; /* transparent colour (black) */
		for (int index = 1; index < palette.length; index++) {
			palette[index] = ByteBufferUtils.getUMedium(buffer);
			if (palette[index] == 0)
				palette[index] = 1;
		}

		Sprite[] sprites = new Sprite[size];
		/* read the pixels themselves */
		buffer.position(0);
		for (int id = 0; id < size; id++) {
			Sprite set = new Sprite(subWidths[id], subHeights[id]);
			/* grab some frequently used values */
			int subWidth = subWidths[id], subHeight = subHeights[id];
			int offsetX = offsetsX[id], offsetY = offsetsY[id];

			/* create a BufferedImage to store the resulting image */
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			/* allocate an array for the palette indices */
			int[][] indices = new int[subWidth][subHeight];

			/*
			 * read the flags so we know whether to read horizontally or
			 * vertically
			 */
			int flags = buffer.get() & 0xFF;

			/* now read the image */
			if (image != null) {
				/* read the palette indices */
				if ((flags & FLAG_VERTICAL) != 0) {
					for (int x = 0; x < subWidth; x++) {
						for (int y = 0; y < subHeight; y++) {
							indices[x][y] = buffer.get() & 0xFF;
						}
					}
				} else {
					for (int y = 0; y < subHeight; y++) {
						for (int x = 0; x < subWidth; x++) {
							indices[x][y] = buffer.get() & 0xFF;
						}
					}
				}

				/*
				 * read the alpha (if there is alpha) and convert values to ARGB
				 */
				set.hasAlpha = true;
				if ((flags & FLAG_ALPHA) != 0) {
					if ((flags & FLAG_VERTICAL) != 0) {
						for (int x = 0; x < subWidth; x++) {
							for (int y = 0; y < subHeight; y++) {
								int alpha = buffer.get() & 0xFF;
								image.setRGB(x + offsetX, y + offsetY, alpha << 24 | palette[indices[x][y]]);
							}
						}
					} else {
						for (int y = 0; y < subHeight; y++) {
							for (int x = 0; x < subWidth; x++) {
								int alpha = buffer.get() & 0xFF;
								image.setRGB(x + offsetX, y + offsetY, alpha << 24 | palette[indices[x][y]]);
							}
						}
					}
				} else {
					for (int x = 0; x < subWidth; x++) {
						for (int y = 0; y < subHeight; y++) {
							int index = indices[x][y];
							if (index == 0) {
								image.setRGB(x + offsetX, y + offsetY, 0);
							} else {
								image.setRGB(x + offsetX, y + offsetY, 0xFF000000 | palette[index]);
							}
						}
					}
				}
			}
		
			image.getRGB(0, 0, subWidth, subHeight, set.raster, 0, subWidth);
			sprites[id] = set;
		}
		return sprites;
	}
	
	public static Sprite decode(ByteBuffer buffer) {
		/* find the size of this sprite set */
		buffer.position(buffer.limit() - 2);
		int size = buffer.getShort() & 0xFFFF;

		/* allocate arrays to store info */
		int[] offsetsX = new int[size];
		int[] offsetsY = new int[size];
		int[] subWidths = new int[size];
		int[] subHeights = new int[size];

		/* read the width, height and palette size */
		buffer.position(buffer.limit() - size * 8 - 7);
		int width = buffer.getShort() & 0xFFFF;
		int height = buffer.getShort() & 0xFFFF;
		int[] palette = new int[(buffer.get() & 0xFF) + 1];

		/* and allocate an object for this sprite set */

		/* read the offsets and dimensions of the individual sprites */
		for (int i = 0; i < size; i++) {
			offsetsX[i] = buffer.getShort() & 0xFFFF;
		}
		for (int i = 0; i < size; i++) {
			offsetsY[i] = buffer.getShort() & 0xFFFF;
		}
		for (int i = 0; i < size; i++) {
			subWidths[i] = buffer.getShort() & 0xFFFF;
		}
		for (int i = 0; i < size; i++) {
			subHeights[i] = buffer.getShort() & 0xFFFF;
		}
		Sprite set = new Sprite(subWidths[0], subHeights[0]);

		/* read the palette */
		buffer.position(buffer.limit() - size * 8 - 7 - (palette.length - 1) * 3);
		palette[0] = 0; /* transparent colour (black) */
		for (int index = 1; index < palette.length; index++) {
			palette[index] = ByteBufferUtils.getUMedium(buffer);
			if (palette[index] == 0)
				palette[index] = 1;
		}

		/* read the pixels themselves */
		buffer.position(0);
		//for (int id = 0; id < size; id++) {
			/* grab some frequently used values */
			int subWidth = subWidths[0], subHeight = subHeights[0];
			int offsetX = offsetsX[0], offsetY = offsetsY[0];

			/* create a BufferedImage to store the resulting image */
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			/* allocate an array for the palette indices */
			int[][] indices = new int[subWidth][subHeight];

			/*
			 * read the flags so we know whether to read horizontally or
			 * vertically
			 */
			int flags = buffer.get() & 0xFF;

			/* now read the image */
			if (image != null) {
				/* read the palette indices */
				if ((flags & FLAG_VERTICAL) != 0) {
					for (int x = 0; x < subWidth; x++) {
						for (int y = 0; y < subHeight; y++) {
							indices[x][y] = buffer.get() & 0xFF;
						}
					}
				} else {
					for (int y = 0; y < subHeight; y++) {
						for (int x = 0; x < subWidth; x++) {
							indices[x][y] = buffer.get() & 0xFF;
						}
					}
				}

				/*
				 * read the alpha (if there is alpha) and convert values to ARGB
				 */
				set.hasAlpha = true;
				if ((flags & FLAG_ALPHA) != 0) {
					if ((flags & FLAG_VERTICAL) != 0) {
						for (int x = 0; x < subWidth; x++) {
							for (int y = 0; y < subHeight; y++) {
								int alpha = buffer.get() & 0xFF;
								image.setRGB(x + offsetX, y + offsetY, alpha << 24 | palette[indices[x][y]]);
							}
						}
					} else {
						for (int y = 0; y < subHeight; y++) {
							for (int x = 0; x < subWidth; x++) {
								int alpha = buffer.get() & 0xFF;
								image.setRGB(x + offsetX, y + offsetY, alpha << 24 | palette[indices[x][y]]);
							}
						}
					}
				} else {
					for (int x = 0; x < subWidth; x++) {
						for (int y = 0; y < subHeight; y++) {
							int index = indices[x][y];
							if (index == 0) {
								image.setRGB(x + offsetX, y + offsetY, 0);
							} else {
								image.setRGB(x + offsetX, y + offsetY, 0xFF000000 | palette[index]);
							}
						}
					}
				}
			}
		//}
			image.getRGB(0, 0, subWidth, subHeight, set.raster, 0, subWidth);
		return set;
	}
	
	public Sprite(Archive archive, String name, int id) {
		Buffer sprite = new Buffer(archive.file(name + ".dat"));
		Buffer meta = new Buffer(archive.file("index.dat"));
		if (sprite.getPayload() == null)
			return;
		meta.setPosition(sprite.readUShort());

		resizeWidth = meta.readUShort();
		resizeHeight = meta.readUShort();

		int colours = meta.readUByte();
		int[] palette = new int[colours];

		for (int index = 0; index < colours - 1; index++) {
			int colour = meta.readUTriByte();
			if(colour == 0)
				colour = 1;
			else if(colour == 0xff00ff)
				colour = 0;
			palette[index + 1] = colour;
		}

		for (int i = 0; i < id; i++) {
			meta.setPosition(meta.getPosition() + 2);
			sprite.setPosition(sprite.getPosition() + meta.readUShort() * meta.readUShort());
			meta.setPosition(meta.getPosition() + 1);
		}

		horizontalOffset = meta.readUByte();
		verticalOffset = meta.readUByte();
		width = meta.readUShort();
		height = meta.readUShort();

		int format = meta.readUByte();
		int pixels = width * height;
		raster = new int[pixels];

		if (format == 0) {
			for (int index = 0; index < pixels; index++) {
				raster[index] = palette[sprite.readUByte()];
			}
		} else {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					raster[x + y * width] = palette[sprite.readUByte()];
				}
			}
		}
	}

	public Sprite(byte[] data, Component component) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(data);
			MediaTracker tracker = new MediaTracker(component);
			tracker.addImage(image, 0);
			tracker.waitForAll();

			width = image.getWidth(component);
			height = image.getHeight(component);
			resizeWidth = width;
			resizeHeight = height;

			horizontalOffset = 0;
			verticalOffset = 0;
			raster = new int[width * height];

			PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, raster, 0, width);
			grabber.grabPixels();
		} catch (Exception ex) {
			Image image = Toolkit.getDefaultToolkit().createImage(data);
			ImageIcon sprite = new ImageIcon(image);
			width = sprite.getIconWidth();
			height = sprite.getIconHeight();
			resizeWidth = width;
			resizeHeight = height;
			horizontalOffset = 0;
			verticalOffset = 0;
			raster = new int[width * height];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, width, height, raster, 0, width);
			try {
				pixelgrabber.grabPixels();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Sprite(int width, int height) {
		raster = new int[width * height];
		this.width = resizeWidth = width;
		this.height = resizeHeight = height;
		horizontalOffset = verticalOffset = 0;
	}
	

	public void drawBehind(IndexedImage image, int y, int x) {
		drawBehind(GameRasterizer.getInstance(), image, y, x);
	}

	public void drawBehind(GameRasterizer rasterizer, IndexedImage image, int y, int x) {
		x += horizontalOffset;
		y += verticalOffset;
		int k = x + y * rasterizer.getWidth();
		int l = 0;
		int height = this.height;
		int width = this.width;
		int deltaWidth = rasterizer.getWidth() - width;
		int l1 = 0;

		if (y < rasterizer.getClipBottom()) {
			int dy = rasterizer.getClipBottom() - y;
			height -= dy;
			y = rasterizer.getClipBottom();
			l += dy * width;
			k += dy * rasterizer.getWidth();
		}

		if (y + height > rasterizer.getClipTop()) {
			height -= y + height - rasterizer.getClipTop();
		}

		if (x < rasterizer.getClipLeft()) {
			int dx = rasterizer.getClipLeft() - x;
			width -= dx;
			x = rasterizer.getClipLeft();
			l += dx;
			k += dx;
			l1 += dx;
			deltaWidth += dx;
		}

		if (x + width > rasterizer.getClipRight()) {
			int dx = x + width - rasterizer.getClipRight();
			width -= dx;
			l1 += dx;
			deltaWidth += dx;
		}
		if (width > 0 && height > 0) {
			drawBehind(image.getImageRaster(), raster, width, height, rasterizer.getRaster(), 0, deltaWidth, k, l1, l);
		}
	}
	

	public void drawSprite(int x, int y) {
		drawSprite(GameRasterizer.getInstance(), x, y);
	}

	public void drawSprite(GameRasterizer rasterizer, int x, int y) {
		x += horizontalOffset;
		y += verticalOffset;
		int rasterClip = x + y * rasterizer.getWidth();
		int imageClip = 0;
		int height = this.height;
		int width = this.width;
		int rasterOffset = rasterizer.getWidth() - width;
		int imageOffset = 0;

		if (y < rasterizer.getClipBottom()) {
			int dy = rasterizer.getClipBottom() - y;
			height -= dy;
			y = rasterizer.getClipBottom();
			imageClip += dy * width;
			rasterClip += dy * rasterizer.getWidth();
		}

		if (y + height > rasterizer.getClipTop()) {
			height -= y + height - rasterizer.getClipTop();
		}

		if (x < rasterizer.getClipLeft()) {
			int dx = rasterizer.getClipLeft() - x;
			width -= dx;
			x = rasterizer.getClipLeft();
			imageClip += dx;
			rasterClip += dx;
			imageOffset += dx;
			rasterOffset += dx;
		}

		if (x + width > rasterizer.getClipRight()) {
			int dx = x + width - rasterizer.getClipRight();
			width -= dx;
			imageOffset += dx;
			rasterOffset += dx;
		}

		if (width > 0 && height > 0) {
			draw(rasterizer.getRaster(), raster, 0, imageClip, rasterClip, width, height, rasterOffset, imageOffset);
		}
	}

	public void drawSprite(GameRasterizer rasterizer, int x, int y, boolean selected) {
		x += horizontalOffset;
		y += verticalOffset;
		int[] raster = Arrays.copyOf(this.raster, this.raster.length);
		if(selected) {
			for(int i = 0;i<raster.length;i++)
				raster[i] = raster[i] << 8;
		}
		int rasterClip = x + y * rasterizer.getWidth();
		int imageClip = 0;
		int height = this.height;
		int width = this.width;
		int rasterOffset = rasterizer.getWidth() - width;
		int imageOffset = 0;

		if (y < rasterizer.getClipBottom()) {
			int dy = rasterizer.getClipBottom() - y;
			height -= dy;
			y = rasterizer.getClipBottom();
			imageClip += dy * width;
			rasterClip += dy * rasterizer.getWidth();
		}

		if (y + height > rasterizer.getClipTop()) {
			height -= y + height - rasterizer.getClipTop();
		}

		if (x < rasterizer.getClipLeft()) {
			int dx = rasterizer.getClipLeft() - x;
			width -= dx;
			x = rasterizer.getClipLeft();
			imageClip += dx;
			rasterClip += dx;
			imageOffset += dx;
			rasterOffset += dx;
		}

		if (x + width > rasterizer.getClipRight()) {
			int dx = x + width - rasterizer.getClipRight();
			width -= dx;
			imageOffset += dx;
			rasterOffset += dx;
		}

		if (width > 0 && height > 0) {
			draw(rasterizer.getRaster(), raster, 0, imageClip, rasterClip, width, height, rasterOffset, imageOffset);
		}
	}
	

	public void drawSprite(GameRasterizer rasterizer, int x, int y, double scale) {
	
		int height = (int) (this.height * scale);
		int width = (int) (this.width * scale);
		int[] raster = Sprite.resizePixels(this.raster, this.width, this.height, width, height);
	
		x += horizontalOffset;
		y += verticalOffset;
		int rasterClip = x + y * rasterizer.getWidth();
		int imageClip = 0;
		int rasterOffset = rasterizer.getWidth() - width;
		int imageOffset = 0;

		if (y < rasterizer.getClipBottom()) {
			int dy = rasterizer.getClipBottom() - y;
			height -= dy;
			y = rasterizer.getClipBottom();
			imageClip += dy * width;
			rasterClip += dy * rasterizer.getWidth();
		}

		if (y + height > rasterizer.getClipTop()) {
			height -= y + height - rasterizer.getClipTop();
		}

		if (x < rasterizer.getClipLeft()) {
			int dx = rasterizer.getClipLeft() - x;
			width -= dx;
			x = rasterizer.getClipLeft();
			imageClip += dx;
			rasterClip += dx;
			imageOffset += dx;
			rasterOffset += dx;
		}

		if (x + width > rasterizer.getClipRight()) {
			int dx = x + width - rasterizer.getClipRight();
			width -= dx;
			imageOffset += dx;
			rasterOffset += dx;
		}

		if (width > 0 && height > 0) {
			draw(rasterizer.getRaster(), raster, 0, imageClip, rasterClip, width, height, rasterOffset, imageOffset);
		}
	}
	
	

	public void drawSprite(int x, int y, int alpha) {
		drawSprite(GameRasterizer.getInstance(), x, y, alpha);
	}

	public void drawSprite(GameRasterizer rasterizer, int x, int y, int alpha) {
		x += horizontalOffset;
		y += verticalOffset;
		int destIndex = x + y * rasterizer.getWidth();
		int sourceIndex = 0;
		int height = this.height;
		int width = this.width;
		int destStep = rasterizer.getWidth() - width;
		int sourceStep = 0;

		if (y < rasterizer.getClipBottom()) {
			int dx = rasterizer.getClipBottom() - y;
			height -= dx;
			y = rasterizer.getClipBottom();
			sourceIndex += dx * width;
			destIndex += dx * rasterizer.getWidth();
		}

		if (y + height > rasterizer.getClipTop()) {
			height -= y + height - rasterizer.getClipTop();
		}

		if (x < rasterizer.getClipLeft()) {
			int dx = rasterizer.getClipLeft() - x;
			width -= dx;
			x = rasterizer.getClipLeft();
			sourceIndex += dx;
			destIndex += dx;
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
			draw(sourceIndex, width, rasterizer.getRaster(), 0, raster, sourceStep, height, destStep, alpha, destIndex);
		}
	}

	public int getHeight() {
		return height;
	}

	public int getHorizontalOffset() {
		return horizontalOffset;
	}

	public int getPixel(int index) {
		return raster[index];
	}

	public int[] getRaster() {
		return raster;
	}

	public int getResizeHeight() {
		return resizeHeight;
	}

	public int getResizeWidth() {
		return resizeWidth;
	}

	public int getVerticalOffset() {
		return verticalOffset;
	}

	public int getWidth() {
		return width;
	}

	public void initRaster(GameRasterizer rasterizer) {
		rasterizer.init(height, width, raster);
	}
	

	public void method346(int x, int y) {
		method346(GameRasterizer.getInstance(), x, y);
	}

	public void method346(GameRasterizer rasterizer, int x, int y) {
		x += horizontalOffset;
		y += verticalOffset;

		int destIndex = x + y * rasterizer.getWidth();
		int sourceIndex = 0;
		int height = this.height;
		int width = this.width;
		int destStep = rasterizer.getWidth() - width;
		int sourceStep = 0;

		if (y < rasterizer.getClipBottom()) {
			int dy = rasterizer.getClipBottom() - y;
			height -= dy;
			y = rasterizer.getClipBottom();
			sourceIndex += dy * width;
			destIndex += dy * rasterizer.getWidth();
		}

		if (y + height > rasterizer.getClipTop()) {
			height -= y + height - rasterizer.getClipTop();
		}

		if (x < rasterizer.getClipLeft()) {
			int dx = rasterizer.getClipLeft() - x;
			width -= dx;
			x = rasterizer.getClipLeft();
			sourceIndex += dx;
			destIndex += dx;
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
			method347(destIndex, width, height, sourceStep, sourceIndex, destStep, raster, rasterizer.getRaster());
		}
	}
	
	public void method352(int height, int theta, int[] ai, int k, int[] destOffsets, int i1, int y, int x, int width,
			int i2) {
		method352(GameRasterizer.getInstance(), height, theta, ai, k, destOffsets, i1, y, x, width, i2);
	}

	public void method352(GameRasterizer rasterizer, int height, int theta, int[] ai, int k, int[] destOffsets, int i1, int y, int x, int width,
			int i2) {
		try {
			int midX = -width / 2;
			int midY = -height / 2;
			int sin = (int) (Math.sin(theta / 326.11D) * 65536);
			int cos = (int) (Math.cos(theta / 326.11D) * 65536);
			sin = sin * k >> 8;
			cos = cos * k >> 8;

			int j3 = (i2 << 16) + midY * sin + midX * cos;
			int k3 = (i1 << 16) + midY * cos - midX * sin;
			int destOffset = x + y * rasterizer.getWidth();

			for (y = 0; y < height; y++) {
				int offset = destOffsets[y];
				int destIndex = destOffset + offset;
				int k4 = j3 + cos * offset;
				int l4 = k3 - sin * offset;

				for (x = -ai[y]; x < 0; x++) {
					rasterizer.getRaster()[destIndex++] = raster[(k4 >> 16) + (l4 >> 16) * this.width];
					k4 += cos;
					l4 -= sin;
				}

				j3 += sin;
				k3 += cos;
				destOffset += rasterizer.getWidth();
			}
		} catch (Exception ex) {
		}
	}
	

	public void method353(int x, int y, int width, int height, double theta, int j, int l, int j1) {
		method353(GameRasterizer.getInstance(), x, y, width, height, theta, j, l, j1);
	}

	public void method353(GameRasterizer rasterizer, int x, int y, int width, int height, double theta, int j, int l, int j1) {
		try {
			int midX = -width / 2;
			int midY = -height / 2;
			int sin = (int) (Math.sin(theta) * 65536D);
			int cos = (int) (Math.cos(theta) * 65536D);
			sin = sin * j1 >> 8;
			cos = cos * j1 >> 8;
			int i3 = (l << 16) + midY * sin + midX * cos;
			int j3 = (j << 16) + midY * cos - midX * sin;
			int destOffset = x + y * rasterizer.getWidth();

			for (y = 0; y < height; y++) {
				int destIndex = destOffset;
				int i4 = i3;
				int j4 = j3;

				for (x = -width; x < 0; x++) {
					int colour = raster[(i4 >> 16) + (j4 >> 16) * this.width];
					if (colour != 0) {
						rasterizer.getRaster()[destIndex++] = colour;
					} else {
						destIndex++;
					}

					i4 += cos;
					j4 -= sin;
				}

				i3 += sin;
				j3 += cos;
				destOffset += rasterizer.getWidth();
			}
		} catch (Exception ex) {
		}
	}

	public void recolour(int redOffset, int greenOffset, int blueOffset) {
		for (int index = 0; index < raster.length; index++) {
			int rgb = raster[index];

			if (rgb != 0) {
				int red = rgb >> 16 & 0xff;
				red += redOffset;

				if (red < 1) {
					red = 1;
				} else if (red > 255) {
					red = 255;
				}

				int green = rgb >> 8 & 0xff;
				green += greenOffset;

				if (green < 1) {
					green = 1;
				} else if (green > 255) {
					green = 255;
				}

				int blue = rgb & 0xff;
				blue += blueOffset;

				if (blue < 1) {
					blue = 1;
				} else if (blue > 255) {
					blue = 255;
				}

				raster[index] = (red << 16) + (green << 8) + blue;
			}
		}
	}

	public void resize() {
		int[] raster = new int[resizeWidth * resizeHeight];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				raster[(y + verticalOffset) * resizeWidth + x + horizontalOffset] = this.raster[y * width + x];
			}
		}

		this.raster = raster;
		width = resizeWidth;
		height = resizeHeight;
		horizontalOffset = 0;
		verticalOffset = 0;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setHorizontalOffset(int horizontalOffset) {
		this.horizontalOffset = horizontalOffset;
	}

	public void setRaster(int[] raster) {
		this.raster = raster;
	}

	public void setResizeHeight(int resizeHeight) {
		this.resizeHeight = resizeHeight;
	}

	public void setResizeWidth(int resizeWidth) {
		this.resizeWidth = resizeWidth;
	}

	public void setVerticalOffset(int verticalOffset) {
		this.verticalOffset = verticalOffset;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	private static int[] resizePixels(int[] pixels,int w1,int h1,int w2,int h2) {
	    int[] temp = new int[w2*h2] ;
	    double x_ratio = w1/(double)w2 ;
	    double y_ratio = h1/(double)h2 ;
	    double px, py ; 
	    for (int i=0;i<h2;i++) {
	        for (int j=0;j<w2;j++) {
	            px = Math.floor(j*x_ratio) ;
	            py = Math.floor(i*y_ratio) ;
	            temp[(i*w2)+j] = pixels[(int)((py*w1)+px)] ;
	        }
	    }
	    return temp ;
	}
	
	private boolean hasAlpha;

	public boolean hasAlpha() {
		// TODO Auto-generated method stub
		return hasAlpha;
	}

	public void resize(int width, int height) {
		resizeWidth = width;
		resizeHeight = height;
		resize();
		
	}

}