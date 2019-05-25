package com.jagex.draw.raster;

public class GameRaster {

	protected int maxRight;
	protected int height;
	protected int[] raster;
	protected int width;
	protected int centreX;
	protected int centreY;
	protected int clipBottom;
	protected int clipLeft;
	protected int clipRight;
	protected int clipTop;

	public void drawHorizontal(int x, int y, int length, int colour) {
		if (y < clipBottom || y >= clipTop)
			return;

		if (x < clipLeft) {
			length -= clipLeft - x;
			x = clipLeft;
		}

		if (x + length > clipRight) {
			length = clipRight - x;
		}

		int offset = x + y * width;
		for (int index = 0; index < length; index++) {
			raster[offset + index] = colour;
		}
	}

	public void drawHorizontal(int x, int y, int length, int colour, int alpha) {
		if (y < clipBottom || y >= clipTop)
			return;

		if (x < clipLeft) {
			length -= clipLeft - x;
			x = clipLeft;
		}

		if (x + length > clipRight) {
			length = clipRight - x;
		}

		int invertedAlpha = 256 - alpha;
		int r = (colour >> 16 & 0xff) * alpha;
		int g = (colour >> 8 & 0xff) * alpha;
		int b = (colour & 0xff) * alpha;
		int index = x + y * width;

		for (int i = 0; i < length; i++) {
			int r2 = (raster[index] >> 16 & 0xff) * invertedAlpha;
			int g2 = (raster[index] >> 8 & 0xff) * invertedAlpha;
			int b2 = (raster[index] & 0xff) * invertedAlpha;
			raster[index++] = (r + r2 >> 8 << 16) + (g + g2 >> 8 << 8) + (b + b2 >> 8);
		}
	}

	public void drawRectangle(int x, int y, int width, int height, int colour) {
		drawHorizontal(x, y, width, colour);
		drawHorizontal(x, y + height - 1, width, colour);
		drawVertical(x, y, height, colour);
		drawVertical(x + width - 1, y, height, colour);
	}

	public void drawRectangle(int x, int y, int width, int height, int colour, int alpha) {
		drawHorizontal(x, y, width, colour, alpha);
		drawHorizontal(x, y + height - 1, width, colour, alpha);
		if (height >= 3) {
			drawVertical(x, y + 1, height - 2, colour, alpha);
			drawVertical(x + width - 1, y + 1, height - 2, colour, alpha);
		}
	}

	public void drawVertical(int x, int y, int length, int colour) {
		if (x < clipLeft || x >= clipRight)
			return;

		if (y < clipBottom) {
			length -= clipBottom - y;
			y = clipBottom;
		}

		if (y + length > clipTop) {
			length = clipTop - y;
		}

		int offset = x + y * width;
		for (int index = 0; index < length; index++) {
			raster[offset + index * width] = colour;
		}
	}

	public void drawVertical(int x, int y, int length, int colour, int alpha) {
		if (x < clipLeft || x >= clipRight)
			return;

		if (y < clipBottom) {
			length -= clipBottom - y;
			y = clipBottom;
		}

		if (y + length > clipTop) {
			length = clipTop - y;
		}

		int invertedAlpha = 256 - alpha;
		int r = (colour >> 16 & 0xff) * alpha;
		int g = (colour >> 8 & 0xff) * alpha;
		int b = (colour & 0xff) * alpha;
		int index = x + y * width;

		for (int i = 0; i < length; i++) {
			int r2 = (raster[index] >> 16 & 0xff) * invertedAlpha;
			int g2 = (raster[index] >> 8 & 0xff) * invertedAlpha;
			int b2 = (raster[index] & 0xff) * invertedAlpha;
			raster[index] = (r + r2 >> 8 << 16) + (g + g2 >> 8 << 8) + (b + b2 >> 8);
			index += width;
		}
	}

	public void fillRectangle(int x, int y, int width, int height, int colour) {
		if (x < clipLeft) {
			width -= clipLeft - x;
			x = clipLeft;
		}

		if (y < clipBottom) {
			height -= clipBottom - y;
			y = clipBottom;
		}

		if (x + width > clipRight) {
			width = clipRight - x;
		}

		if (y + height > clipTop) {
			height = clipTop - y;
		}

		int dx = this.width - width;
		int pixel = x + y * this.width;

		for (int i2 = -height; i2 < 0; i2++) {
			for (int j2 = -width; j2 < 0; j2++) {
				raster[pixel++] = colour;
			}

			pixel += dx;
		}
	}

	public void fillRectangle(int drawX, int drawY, int width, int height, int colour, int alpha) {
		if (drawX < clipLeft) {
			width -= clipLeft - drawX;
			drawX = clipLeft;
		}

		if (drawY < clipBottom) {
			height -= clipBottom - drawY;
			drawY = clipBottom;
		}

		if (drawX + width > clipRight) {
			width = clipRight - drawX;
		}

		if (drawY + height > clipTop) {
			height = clipTop - drawY;
		}

		int inverseAlpha = 256 - alpha;
		int r = (colour >> 16 & 0xff) * alpha;
		int g = (colour >> 8 & 0xff) * alpha;
		int b = (colour & 0xff) * alpha;
		int dx = this.width - width;
		int pixel = drawX + drawY * this.width;

		for (int x = 0; x < height; x++) {
			for (int y = -width; y < 0; y++) {
				int r2 = (raster[pixel] >> 16 & 0xff) * inverseAlpha;
				int g2 = (raster[pixel] >> 8 & 0xff) * inverseAlpha;
				int b2 = (raster[pixel] & 0xff) * inverseAlpha;
				raster[pixel++] = (r + r2 >> 8 << 16) + (g + g2 >> 8 << 8) + (b + b2 >> 8);
			}

			pixel += dx;
		}
	}

	public int getCentreX() {
		return centreX;
	}

	public int getCentreY() {
		return centreY;
	}

	public int getClipBottom() {
		return clipBottom;
	}

	public int getClipLeft() {
		return clipLeft;
	}

	public int getClipRight() {
		return clipRight;
	}

	public int getClipTop() {
		return clipTop;
	}

	public void init(int height, int width, int[] pixels) {
		this.raster = pixels;
		this.width = width;
		this.height = height;
		setBounds(height, 0, width, 0);
	}

	public void reset() {
		int count = width * height;
		for (int index = 0; index < count; index++) {
			raster[index] = 0;
		}
	}

	public void setBounds(int clipTop, int clipLeft, int clipRight, int clipBottom) {
		if (clipLeft < 0) {
			clipLeft = 0;
		}

		if (clipBottom < 0) {
			clipBottom = 0;
		}

		if (clipRight > this.width) {
			clipRight = this.width;
		}

		if (clipTop > this.height) {
			clipTop = this.height;
		}

		this.clipLeft = clipLeft;
		this.clipBottom = clipBottom;
		this.clipRight = clipRight;
		this.clipTop = clipTop;

		maxRight = this.clipRight - 1;
		centreX = this.clipRight / 2;
		centreY = this.clipTop / 2;
	}

	public void setCentreX(int centreX) {
		this.centreX = centreX;
	}

	public void setCentreY(int centreY) {
		this.centreY = centreY;
	}

	public void setClipBottom(int clipBottom) {
		this.clipBottom = clipBottom;
	}

	public void setClipLeft(int clipLeft) {
		this.clipLeft = clipLeft;
	}

	public void setClipRight(int clipRight) {
		this.clipRight = clipRight;
	}

	public void setClipTop(int clipTop) {
		this.clipTop = clipTop;
	}

	public void setDefaultBounds() {
		clipLeft = 0;
		clipBottom = 0;
		clipRight = width;
		clipTop = height;
		maxRight = clipRight - 1;
		centreX = clipRight / 2;
	}

	public int getMaxRight() {
		return maxRight;
	}

	public int getHeight() {
		return height;
	}

	public int[] getRaster() {
		return raster;
	}

	public int getWidth() {
		return width;
	}
	
	
	

}