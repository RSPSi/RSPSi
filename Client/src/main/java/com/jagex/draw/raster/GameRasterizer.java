package com.jagex.draw.raster;

import com.jagex.map.MapRegion;

import com.jagex.cache.def.TextureDef;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.textures.Texture;
import com.jagex.util.ColourUtils;
import com.jagex.util.Constants;
import com.jagex.util.Point2D;

public class GameRasterizer extends GameRaster {

	private static GameRasterizer instance;

	public static GameRasterizer getInstance() {
		return instance;
	}

	public static void setInstance(GameRasterizer rasterizer) {
		instance = rasterizer;
	}

	public boolean[] aBooleanArray1663 = new boolean[4096];
	 public boolean[] cullFaces = new boolean[4096];
	 public boolean[] cullFacesOther = new boolean[4096];
	 public int[] vertexScreenX = new int[4096];
	 public int[] vertexScreenY = new int[4096];
	 public int[] vertexScreenZ = new int[4096];
	 public int[] camera_vertex_x = new int[4096];
	 public int[] camera_vertex_y = new int[4096];
	 public int[] camera_vertex_z = new int[4096];
	 public int[] depthListIndices = new int[1500];
	 public int[] anIntArray1673 = new int[12];
	 public int[] anIntArray1675 = new int[2000];
	 public int[] anIntArray1676 = new int[2000];
	 public int[] anIntArray1677 = new int[12];
	 public int[] anIntArray1678 = new int[10];
	 public int[] anIntArray1679 = new int[10];
	 public int[] anIntArray1680 = new int[10];
	 public int[][] faceList = new int[1500][512];
	 public int[][] anIntArrayArray1674 = new int[12][2000];

	public boolean restrictEdges;
	public int anInt1481;
	public int[] anIntArray1480 = new int[50];
	public int[] colourPalette = new int[0x10000];
	public boolean approximateAlphaBlending = true;
	public int currentAlpha;
	public Point2D viewCenter;
	public int[] scanOffsets;
	boolean currentTextureTransparent;
	int anInt1477;

	public int getFuchsia(){
		return colourPalette[MapRegion.light(ColourUtils.toHsl(128, 255, 127), 96)];//colourPalette[MapRegion.light(0xFF0000, 96)];
	}


	public void dispose() {
		scanOffsets = null;
		anIntArray1480 = null;
		colourPalette = null;
	}

	public void drawLine(int[] pixels, int i, int j, int k, int startX, int endX, int j1, int k1) {
		if (approximateAlphaBlending) {
			int l1;
			if (restrictEdges) {
				if (endX - startX > 3) {
					l1 = (k1 - j1) / (endX - startX);
				} else {
					l1 = 0;
				}
				if (endX > this.maxRight) {
					endX = this.maxRight;
				}
				if (startX < 0) {
					j1 -= startX * l1;
					startX = 0;
				}
				if (startX >= endX)
					return;
				i += startX;
				k = endX - startX >> 2;
				l1 <<= 2;
			} else {
				if (startX >= endX)
					return;
				i += startX;
				k = endX - startX >> 2;
				if (k > 0) {
					l1 = (k1 - j1) * Constants.SHADOW_DECAY[k] >> 15;
				} else {
					l1 = 0;
				}
			}
			if (currentAlpha == 0) {
				while (--k >= 0) {
					j = colourPalette[j1 >> 8];
					j1 += l1;
					pixels[i++] = j;
					pixels[i++] = j;
					pixels[i++] = j;
					pixels[i++] = j;
				}
				k = endX - startX & 3;
				if (k > 0) {
					j = colourPalette[j1 >> 8];
					do {
						pixels[i++] = j;
					} while (--k > 0);
					return;
				}
			} else {
				int j2 = currentAlpha;
				int l2 = 256 - currentAlpha;
				while (--k >= 0) {
					j = colourPalette[j1 >> 8];
					j1 += l1;
					j = ((j & 0xff00ff) * l2 >> 8 & 0xff00ff) + ((j & 0xff00) * l2 >> 8 & 0xff00);
					pixels[i++] = j + ((pixels[i] & 0xff00ff) * j2 >> 8 & 0xff00ff)
							+ ((pixels[i] & 0xff00) * j2 >> 8 & 0xff00);
					pixels[i++] = j + ((pixels[i] & 0xff00ff) * j2 >> 8 & 0xff00ff)
							+ ((pixels[i] & 0xff00) * j2 >> 8 & 0xff00);
					pixels[i++] = j + ((pixels[i] & 0xff00ff) * j2 >> 8 & 0xff00ff)
							+ ((pixels[i] & 0xff00) * j2 >> 8 & 0xff00);
					pixels[i++] = j + ((pixels[i] & 0xff00ff) * j2 >> 8 & 0xff00ff)
							+ ((pixels[i] & 0xff00) * j2 >> 8 & 0xff00);
				}
				k = endX - startX & 3;
				if (k > 0) {
					j = colourPalette[j1 >> 8];
					j = ((j & 0xff00ff) * l2 >> 8 & 0xff00ff) + ((j & 0xff00) * l2 >> 8 & 0xff00);
					do {
						pixels[i++] = j + ((pixels[i] & 0xff00ff) * j2 >> 8 & 0xff00ff)
								+ ((pixels[i] & 0xff00) * j2 >> 8 & 0xff00);
					} while (--k > 0);
				}
			}
			return;
		}
		if (startX >= endX)
			return;
		int i2 = (k1 - j1) / (endX - startX);
		if (restrictEdges) {
			if (endX > this.maxRight) {
				endX = this.maxRight;
			}
			if (startX < 0) {
				j1 -= startX * i2;
				startX = 0;
			}
			if (startX >= endX)
				return;
		}
		i += startX;
		k = endX - startX;
		if (currentAlpha == 0) {
			do {
				pixels[i++] = colourPalette[j1 >> 8];
				j1 += i2;
			} while (--k > 0);
			return;
		}
		int k2 = currentAlpha;
		int i3 = 256 - currentAlpha;
		do {
			j = colourPalette[j1 >> 8];
			j1 += i2;
			j = ((j & 0xff00ff) * i3 >> 8 & 0xff00ff) + ((j & 0xff00) * i3 >> 8 & 0xff00);
			pixels[i++] = j + ((pixels[i] & 0xff00ff) * k2 >> 8 & 0xff00ff) + ((pixels[i] & 0xff00) * k2 >> 8 & 0xff00);
		} while (--k > 0);
	}

	public void drawShadedTriangle(int i, int j, int k, int l, int i1, int j1, int r, int g, int b) {
		int j2 = 0;
		int k2 = 0;
		if (j != i) {
			j2 = (i1 - l << 16) / (j - i);
			k2 = (g - r << 15) / (j - i);
		}

		int l2 = 0;
		int i3 = 0;
		if (k != j) {
			l2 = (j1 - i1 << 16) / (k - j);
			i3 = (b - g << 15) / (k - j);
		}

		int j3 = 0;
		int k3 = 0;
		if (k != i) {
			j3 = (l - j1 << 16) / (i - k);
			k3 = (r - b << 15) / (i - k);
		}

		if (i <= j && i <= k) {
			if (i >= this.getClipTop())
				return;
			if (j > this.getClipTop()) {
				j = this.getClipTop();
			}
			if (k > this.getClipTop()) {
				k = this.getClipTop();
			}
			if (j < k) {
				j1 = l <<= 16;
				b = r <<= 15;
				if (i < 0) {
					j1 -= j3 * i;
					l -= j2 * i;
					b -= k3 * i;
					r -= k2 * i;
					i = 0;
				}
				i1 <<= 16;
				g <<= 15;
				if (j < 0) {
					i1 -= l2 * j;
					g -= i3 * j;
					j = 0;
				}
				if (i != j && j3 < j2 || i == j && j3 > l2) {
					k -= j;
					j -= i;
					for (i = scanOffsets[i]; --j >= 0; i += this.width) {
						drawLine(this.raster, i, 0, 0, j1 >> 16, l >> 16, b >> 7, r >> 7);
						j1 += j3;
						l += j2;
						b += k3;
						r += k2;
					}

					while (--k >= 0) {
						drawLine(this.raster, i, 0, 0, j1 >> 16, i1 >> 16, b >> 7, g >> 7);
						j1 += j3;
						i1 += l2;
						b += k3;
						g += i3;
						i += this.width;
					}
					return;
				}
				k -= j;
				j -= i;
				for (i = scanOffsets[i]; --j >= 0; i += this.width) {
					drawLine(this.raster, i, 0, 0, l >> 16, j1 >> 16, r >> 7, b >> 7);
					j1 += j3;
					l += j2;
					b += k3;
					r += k2;
				}

				while (--k >= 0) {
					drawLine(this.raster, i, 0, 0, i1 >> 16, j1 >> 16, g >> 7, b >> 7);
					j1 += j3;
					i1 += l2;
					b += k3;
					g += i3;
					i += this.width;
				}
				return;
			}
			i1 = l <<= 16;
			g = r <<= 15;
			if (i < 0) {
				i1 -= j3 * i;
				l -= j2 * i;
				g -= k3 * i;
				r -= k2 * i;
				i = 0;
			}
			j1 <<= 16;
			b <<= 15;
			if (k < 0) {
				j1 -= l2 * k;
				b -= i3 * k;
				k = 0;
			}
			if (i != k && j3 < j2 || i == k && l2 > j2) {
				j -= k;
				k -= i;
				for (i = scanOffsets[i]; --k >= 0; i += this.width) {
					drawLine(this.raster, i, 0, 0, i1 >> 16, l >> 16, g >> 7, r >> 7);
					i1 += j3;
					l += j2;
					g += k3;
					r += k2;
				}

				while (--j >= 0) {
					drawLine(this.raster, i, 0, 0, j1 >> 16, l >> 16, b >> 7, r >> 7);
					j1 += l2;
					l += j2;
					b += i3;
					r += k2;
					i += this.width;
				}
				return;
			}
			j -= k;
			k -= i;
			for (i = scanOffsets[i]; --k >= 0; i += this.width) {
				drawLine(this.raster, i, 0, 0, l >> 16, i1 >> 16, r >> 7, g >> 7);
				i1 += j3;
				l += j2;
				g += k3;
				r += k2;
			}

			while (--j >= 0) {
				drawLine(this.raster, i, 0, 0, l >> 16, j1 >> 16, r >> 7, b >> 7);
				j1 += l2;
				l += j2;
				b += i3;
				r += k2;
				i += this.width;
			}
			return;
		}
		if (j <= k) {
			if (j >= this.getClipTop())
				return;
			if (k > this.getClipTop()) {
				k = this.getClipTop();
			}
			if (i > this.getClipTop()) {
				i = this.getClipTop();
			}
			if (k < i) {
				l = i1 <<= 16;
				r = g <<= 15;
				if (j < 0) {
					l -= j2 * j;
					i1 -= l2 * j;
					r -= k2 * j;
					g -= i3 * j;
					j = 0;
				}
				j1 <<= 16;
				b <<= 15;
				if (k < 0) {
					j1 -= j3 * k;
					b -= k3 * k;
					k = 0;
				}
				if (j != k && j2 < l2 || j == k && j2 > j3) {
					i -= k;
					k -= j;
					for (j = scanOffsets[j]; --k >= 0; j += this.width) {
						drawLine(this.raster, j, 0, 0, l >> 16, i1 >> 16, r >> 7, g >> 7);
						l += j2;
						i1 += l2;
						r += k2;
						g += i3;
					}

					while (--i >= 0) {
						drawLine(this.raster, j, 0, 0, l >> 16, j1 >> 16, r >> 7, b >> 7);
						l += j2;
						j1 += j3;
						r += k2;
						b += k3;
						j += this.width;
					}
					return;
				}
				i -= k;
				k -= j;
				for (j = scanOffsets[j]; --k >= 0; j += this.width) {
					drawLine(this.raster, j, 0, 0, i1 >> 16, l >> 16, g >> 7, r >> 7);
					l += j2;
					i1 += l2;
					r += k2;
					g += i3;
				}

				while (--i >= 0) {
					drawLine(this.raster, j, 0, 0, j1 >> 16, l >> 16, b >> 7, r >> 7);
					l += j2;
					j1 += j3;
					r += k2;
					b += k3;
					j += this.width;
				}
				return;
			}
			j1 = i1 <<= 16;
			b = g <<= 15;
			if (j < 0) {
				j1 -= j2 * j;
				i1 -= l2 * j;
				b -= k2 * j;
				g -= i3 * j;
				j = 0;
			}
			l <<= 16;
			r <<= 15;
			if (i < 0) {
				l -= j3 * i;
				r -= k3 * i;
				i = 0;
			}
			if (j2 < l2) {
				k -= i;
				i -= j;
				for (j = scanOffsets[j]; --i >= 0; j += this.width) {
					drawLine(this.raster, j, 0, 0, j1 >> 16, i1 >> 16, b >> 7, g >> 7);
					j1 += j2;
					i1 += l2;
					b += k2;
					g += i3;
				}

				while (--k >= 0) {
					drawLine(this.raster, j, 0, 0, l >> 16, i1 >> 16, r >> 7, g >> 7);
					l += j3;
					i1 += l2;
					r += k3;
					g += i3;
					j += this.width;
				}
				return;
			}
			k -= i;
			i -= j;
			for (j = scanOffsets[j]; --i >= 0; j += this.width) {
				drawLine(this.raster, j, 0, 0, i1 >> 16, j1 >> 16, g >> 7, b >> 7);
				j1 += j2;
				i1 += l2;
				b += k2;
				g += i3;
			}

			while (--k >= 0) {
				drawLine(this.raster, j, 0, 0, i1 >> 16, l >> 16, g >> 7, r >> 7);
				l += j3;
				i1 += l2;
				r += k3;
				g += i3;
				j += this.width;
			}
			return;
		}
		if (k >= this.getClipTop())
			return;
		if (i > this.getClipTop()) {
			i = this.getClipTop();
		}
		if (j > this.getClipTop()) {
			j = this.getClipTop();
		}
		if (i < j) {
			i1 = j1 <<= 16;
			g = b <<= 15;
			if (k < 0) {
				i1 -= l2 * k;
				j1 -= j3 * k;
				g -= i3 * k;
				b -= k3 * k;
				k = 0;
			}
			l <<= 16;
			r <<= 15;
			if (i < 0) {
				l -= j2 * i;
				r -= k2 * i;
				i = 0;
			}
			if (l2 < j3) {
				j -= i;
				i -= k;
				for (k = scanOffsets[k]; --i >= 0; k += this.width) {
					drawLine(this.raster, k, 0, 0, i1 >> 16, j1 >> 16, g >> 7, b >> 7);
					i1 += l2;
					j1 += j3;
					g += i3;
					b += k3;
				}

				while (--j >= 0) {
					drawLine(this.raster, k, 0, 0, i1 >> 16, l >> 16, g >> 7, r >> 7);
					i1 += l2;
					l += j2;
					g += i3;
					r += k2;
					k += this.width;
				}
				return;
			}
			j -= i;
			i -= k;
			for (k = scanOffsets[k]; --i >= 0; k += this.width) {
				drawLine(this.raster, k, 0, 0, j1 >> 16, i1 >> 16, b >> 7, g >> 7);
				i1 += l2;
				j1 += j3;
				g += i3;
				b += k3;
			}

			while (--j >= 0) {
				drawLine(this.raster, k, 0, 0, l >> 16, i1 >> 16, r >> 7, g >> 7);
				i1 += l2;
				l += j2;
				g += i3;
				r += k2;
				k += this.width;
			}
			return;
		}
		l = j1 <<= 16;
		r = b <<= 15;
		if (k < 0) {
			l -= l2 * k;
			j1 -= j3 * k;
			r -= i3 * k;
			b -= k3 * k;
			k = 0;
		}
		i1 <<= 16;
		g <<= 15;
		if (j < 0) {
			i1 -= j2 * j;
			g -= k2 * j;
			j = 0;
		}
		if (l2 < j3) {
			i -= j;
			j -= k;
			for (k = scanOffsets[k]; --j >= 0; k += this.width) {
				drawLine(this.raster, k, 0, 0, l >> 16, j1 >> 16, r >> 7, b >> 7);
				l += l2;
				j1 += j3;
				r += i3;
				b += k3;
			}

			while (--i >= 0) {
				drawLine(this.raster, k, 0, 0, i1 >> 16, j1 >> 16, g >> 7, b >> 7);
				i1 += j2;
				j1 += j3;
				g += k2;
				b += k3;
				k += this.width;
			}
			return;
		}
		i -= j;
		j -= k;
		for (k = scanOffsets[k]; --j >= 0; k += this.width) {
			drawLine(this.raster, k, 0, 0, j1 >> 16, l >> 16, b >> 7, r >> 7);
			l += l2;
			j1 += j3;
			r += i3;
			b += k3;
		}

		while (--i >= 0) {
			drawLine(this.raster, k, 0, 0, j1 >> 16, i1 >> 16, b >> 7, g >> 7);
			i1 += j2;
			j1 += j3;
			g += k2;
			b += k3;
			k += this.width;
		}
	}

	public void drawShadedTriangle(int i, int j, int k, int l, int i1, int j1, int k1) {
		int l1 = 0;
		if (j != i) {
			l1 = (i1 - l << 16) / (j - i);
		}
		int i2 = 0;
		if (k != j) {
			i2 = (j1 - i1 << 16) / (k - j);
		}
		int j2 = 0;
		if (k != i) {
			j2 = (l - j1 << 16) / (i - k);
		}
		if (i <= j && i <= k) {
			if (i >= this.getClipTop())
				return;
			if (j > this.getClipTop()) {
				j = this.getClipTop();
			}
			if (k > this.getClipTop()) {
				k = this.getClipTop();
			}
			if (j < k) {
				j1 = l <<= 16;
				if (i < 0) {
					j1 -= j2 * i;
					l -= l1 * i;
					i = 0;
				}
				i1 <<= 16;
				if (j < 0) {
					i1 -= i2 * j;
					j = 0;
				}
				if (i != j && j2 < l1 || i == j && j2 > i2) {
					k -= j;
					j -= i;
					for (i = scanOffsets[i]; --j >= 0; i += this.width) {
						method377(this.raster, i, k1, 0, j1 >> 16, l >> 16);
						j1 += j2;
						l += l1;
					}

					while (--k >= 0) {
						method377(this.raster, i, k1, 0, j1 >> 16, i1 >> 16);
						j1 += j2;
						i1 += i2;
						i += this.width;
					}
					return;
				}
				k -= j;
				j -= i;
				for (i = scanOffsets[i]; --j >= 0; i += this.width) {
					method377(this.raster, i, k1, 0, l >> 16, j1 >> 16);
					j1 += j2;
					l += l1;
				}

				while (--k >= 0) {
					method377(this.raster, i, k1, 0, i1 >> 16, j1 >> 16);
					j1 += j2;
					i1 += i2;
					i += this.width;
				}
				return;
			}
			i1 = l <<= 16;
			if (i < 0) {
				i1 -= j2 * i;
				l -= l1 * i;
				i = 0;
			}
			j1 <<= 16;
			if (k < 0) {
				j1 -= i2 * k;
				k = 0;
			}
			if (i != k && j2 < l1 || i == k && i2 > l1) {
				j -= k;
				k -= i;
				for (i = scanOffsets[i]; --k >= 0; i += this.width) {
					method377(this.raster, i, k1, 0, i1 >> 16, l >> 16);
					i1 += j2;
					l += l1;
				}

				while (--j >= 0) {
					method377(this.raster, i, k1, 0, j1 >> 16, l >> 16);
					j1 += i2;
					l += l1;
					i += this.width;
				}
				return;
			}
			j -= k;
			k -= i;
			for (i = scanOffsets[i]; --k >= 0; i += this.width) {
				method377(this.raster, i, k1, 0, l >> 16, i1 >> 16);
				i1 += j2;
				l += l1;
			}

			while (--j >= 0) {
				method377(this.raster, i, k1, 0, l >> 16, j1 >> 16);
				j1 += i2;
				l += l1;
				i += this.width;
			}
			return;
		}
		if (j <= k) {
			if (j >= this.getClipTop())
				return;
			if (k > this.getClipTop()) {
				k = this.getClipTop();
			}
			if (i > this.getClipTop()) {
				i = this.getClipTop();
			}
			if (k < i) {
				l = i1 <<= 16;
				if (j < 0) {
					l -= l1 * j;
					i1 -= i2 * j;
					j = 0;
				}
				j1 <<= 16;
				if (k < 0) {
					j1 -= j2 * k;
					k = 0;
				}
				if (j != k && l1 < i2 || j == k && l1 > j2) {
					i -= k;
					k -= j;
					for (j = scanOffsets[j]; --k >= 0; j += this.width) {
						method377(this.raster, j, k1, 0, l >> 16, i1 >> 16);
						l += l1;
						i1 += i2;
					}

					while (--i >= 0) {
						method377(this.raster, j, k1, 0, l >> 16, j1 >> 16);
						l += l1;
						j1 += j2;
						j += this.width;
					}
					return;
				}
				i -= k;
				k -= j;
				for (j = scanOffsets[j]; --k >= 0; j += this.width) {
					method377(this.raster, j, k1, 0, i1 >> 16, l >> 16);
					l += l1;
					i1 += i2;
				}

				while (--i >= 0) {
					method377(this.raster, j, k1, 0, j1 >> 16, l >> 16);
					l += l1;
					j1 += j2;
					j += this.width;
				}
				return;
			}
			j1 = i1 <<= 16;
			if (j < 0) {
				j1 -= l1 * j;
				i1 -= i2 * j;
				j = 0;
			}
			l <<= 16;
			if (i < 0) {
				l -= j2 * i;
				i = 0;
			}
			if (l1 < i2) {
				k -= i;
				i -= j;
				for (j = scanOffsets[j]; --i >= 0; j += this.width) {
					method377(this.raster, j, k1, 0, j1 >> 16, i1 >> 16);
					j1 += l1;
					i1 += i2;
				}

				while (--k >= 0) {
					method377(this.raster, j, k1, 0, l >> 16, i1 >> 16);
					l += j2;
					i1 += i2;
					j += this.width;
				}
				return;
			}
			k -= i;
			i -= j;
			for (j = scanOffsets[j]; --i >= 0; j += this.width) {
				method377(this.raster, j, k1, 0, i1 >> 16, j1 >> 16);
				j1 += l1;
				i1 += i2;
			}

			while (--k >= 0) {
				method377(this.raster, j, k1, 0, i1 >> 16, l >> 16);
				l += j2;
				i1 += i2;
				j += this.width;
			}
			return;
		}
		if (k >= this.getClipTop())
			return;
		if (i > this.getClipTop()) {
			i = this.getClipTop();
		}
		if (j > this.getClipTop()) {
			j = this.getClipTop();
		}
		if (i < j) {
			i1 = j1 <<= 16;
			if (k < 0) {
				i1 -= i2 * k;
				j1 -= j2 * k;
				k = 0;
			}
			l <<= 16;
			if (i < 0) {
				l -= l1 * i;
				i = 0;
			}
			if (i2 < j2) {
				j -= i;
				i -= k;
				for (k = scanOffsets[k]; --i >= 0; k += this.width) {
					method377(this.raster, k, k1, 0, i1 >> 16, j1 >> 16);
					i1 += i2;
					j1 += j2;
				}

				while (--j >= 0) {
					method377(this.raster, k, k1, 0, i1 >> 16, l >> 16);
					i1 += i2;
					l += l1;
					k += this.width;
				}
				return;
			}
			j -= i;
			i -= k;
			for (k = scanOffsets[k]; --i >= 0; k += this.width) {
				method377(this.raster, k, k1, 0, j1 >> 16, i1 >> 16);
				i1 += i2;
				j1 += j2;
			}

			while (--j >= 0) {
				method377(this.raster, k, k1, 0, l >> 16, i1 >> 16);
				i1 += i2;
				l += l1;
				k += this.width;
			}
			return;
		}
		l = j1 <<= 16;
		if (k < 0) {
			l -= i2 * k;
			j1 -= j2 * k;
			k = 0;
		}
		i1 <<= 16;
		if (j < 0) {
			i1 -= l1 * j;
			j = 0;
		}
		if (i2 < j2) {
			i -= j;
			j -= k;
			for (k = scanOffsets[k]; --j >= 0; k += this.width) {
				method377(this.raster, k, k1, 0, l >> 16, j1 >> 16);
				l += i2;
				j1 += j2;
			}

			while (--i >= 0) {
				method377(this.raster, k, k1, 0, i1 >> 16, j1 >> 16);
				i1 += l1;
				j1 += j2;
				k += this.width;
			}
			return;
		}
		i -= j;
		j -= k;
		for (k = scanOffsets[k]; --j >= 0; k += this.width) {
			method377(this.raster, k, k1, 0, j1 >> 16, l >> 16);
			l += i2;
			j1 += j2;
		}

		while (--i >= 0) {
			method377(this.raster, k, k1, 0, j1 >> 16, i1 >> 16);
			i1 += l1;
			j1 += j2;
			k += this.width;
		}
	}


	public void render_texture_triangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int z_a, int z_b, int z_c, int grad_a, int grad_b, int grad_c, int Px, int Mx, int Nx, int Pz, int My, int Nz, int Py, int Mz, int Ny, int t_id, int color, boolean floor, boolean isFloor) {
		if (t_id < 0 || t_id >= TextureDef.textures.length)
		{
			drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, grad_a, grad_b, grad_c);
			return;
		}


		TextureDef def = TextureDef.textures[t_id];
		if (def == null)
		{
			drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, grad_a, grad_b, grad_c);
			return;
		}

		Texture tex = TextureLoader.getTexture(t_id);

		if(tex == null || TextureLoader.getTexturePixels(t_id) == null) {
			drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, grad_a, grad_b, grad_c);
			return;
		}
        int[] texture = TextureLoader.getTexturePixels(t_id);

        if (color >= 0xffff)
            color = -1;

        if (color >= 0 && color < 65535) {
            color = colourPalette[color];
        }

        Mx = Px - Mx;
        My = Pz - My;
        Mz = Py - Mz;
        Nx -= Px;
        Nz -= Pz;
        Ny -= Py;
        int Oa = (Nx * Pz - Nz * Px) << 14;
        int Ha = Nz * Py - Ny * Pz << 8;
        int Va = Ny * Px - Nx * Py << 5;
        int Ob = (Mx * Pz - My * Px) << 14;
        int Hb = My * Py - Mz * Pz << 8;
        int Vb = Mz * Px - Mx * Py << 5;
        int Oc = (My * Nx - Mx * Nz) << 14;
        int Hc = Mz * Nz - My * Ny << 8;
        int Vc = Mx * Ny - Mz * Nx << 5;


        int x_a_off = 0;
        int z_a_off = 0;
        int grad_a_off = 0;

        int col_a = grad_a;
        int col_b = grad_b;
        int col_c = grad_c;
        int col_a_off = 0;
        int col_b_off = 0;
        int col_c_off = 0;

        if (y_b != y_a) {
            x_a_off = (x_b - x_a << 16) / (y_b - y_a);
            z_a_off = (z_b - z_a << 16) / (y_b - y_a);
            grad_a_off = (grad_b - grad_a << 16) / (y_b - y_a);
            col_a_off = (col_b - col_a << 15) / (y_b - y_a);
        }
        int x_b_off = 0;
        int z_b_off = 0;
        int grad_b_off = 0;
        if (y_c != y_b) {
            x_b_off = (x_c - x_b << 16) / (y_c - y_b);
            z_b_off = (z_c - z_b << 16) / (y_c - y_b);
            grad_b_off = (grad_c - grad_b << 16) / (y_c - y_b);
            col_b_off = (col_c - col_b << 15) / (y_c - y_b);
        }
        int x_c_off = 0;
        int z_c_off = 0;
        int grad_c_off = 0;
        if (y_c != y_a) {
            x_c_off = (x_a - x_c << 16) / (y_a - y_c);
            z_c_off = (z_a - z_c << 16) / (y_a - y_c);
            grad_c_off = (grad_a - grad_c << 16) / (y_a - y_c);
            col_c_off = (col_a - col_c << 15) / (y_a - y_c);
        }
        if (y_a <= y_b && y_a <= y_c) {
            if (y_a >= this.getClipTop())
                return;
            if (y_b > this.getClipTop())
                y_b = this.getClipTop();
            if (y_c > this.getClipTop())
                y_c = this.getClipTop();
            if (y_b < y_c) {
                x_c = x_a <<= 16;
                z_c = z_a <<= 16;
                grad_c = grad_a <<= 16;
                col_c = col_a <<= 15;
                if (y_a < 0) {
                    x_c -= x_c_off * y_a;
                    x_a -= x_a_off * y_a;
                    z_c -= z_c_off * y_a;
                    z_a -= z_a_off * y_a;
                    grad_c -= grad_c_off * y_a;
                    grad_a -= grad_a_off * y_a;
                    col_c -= col_c_off * y_a;
                    col_a -= col_a_off * y_a;
                    y_a = 0;
                }
                x_b <<= 16;
                z_b <<= 16;
                grad_b <<= 16;
                col_b <<= 15;
                if (y_b < 0) {
                    x_b -= x_b_off * y_b;
                    z_b -= z_b_off * y_b;
                    grad_b -= grad_b_off * y_b;
                    col_b -= col_b_off * y_b;
                    y_b = 0;
                }
                int jA = y_a - viewCenter.getX();
                Oa += Va * jA;
                Ob += Vb * jA;
                Oc += Vc * jA;
                if (y_a != y_b && x_c_off < x_a_off || y_a == y_b
                        && x_c_off > x_b_off) {
                    y_c -= y_b;
                    y_b -= y_a;
                    y_a = scanOffsets[y_a];
                    while (--y_b >= 0) {
                        drawTexturedScanline(this.raster, texture, y_a, x_c >> 16,
                                x_a >> 16, grad_c >> 8, grad_a >> 8,
                                col_c >> 7, col_a >> 7, Oa, Ob, Oc, Ha, Hb, Hc,
                                color, false, floor, z_c, z_a);
                        x_c += x_c_off;
                        x_a += x_a_off;
                        z_c += z_c_off;
                        z_a += z_a_off;
                        grad_c += grad_c_off;
                        grad_a += grad_a_off;
                        col_c += col_c_off;
                        col_a += col_a_off;
                        y_a += width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    while (--y_c >= 0) {
                        drawTexturedScanline(this.raster, texture, y_a, x_c >> 16,
                                x_b >> 16, grad_c >> 8, grad_b >> 8,
                                col_c >> 7, col_b >> 7, Oa, Ob, Oc, Ha, Hb, Hc,
                                color, false, floor, z_c, z_b);
                        x_c += x_c_off;
                        x_b += x_b_off;
                        z_c += z_c_off;
                        z_b += z_b_off;
                        grad_c += grad_c_off;
                        grad_b += grad_b_off;
                        col_c += col_c_off;
                        col_b += col_b_off;
                        y_a += width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    return;
                }
                y_c -= y_b;
                y_b -= y_a;
                y_a = scanOffsets[y_a];
                while (--y_b >= 0) {
                    drawTexturedScanline(this.raster, texture, y_a, x_a >> 16,
                            x_c >> 16, grad_a >> 8, grad_c >> 8, col_a >> 7,
                            col_c >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false,
                            floor, z_a, z_c);
                    x_c += x_c_off;
                    x_a += x_a_off;
                    z_c += z_c_off;
                    z_a += z_a_off;
                    grad_c += grad_c_off;
                    grad_a += grad_a_off;
                    col_c += col_c_off;
                    col_a += col_a_off;
                    y_a += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while (--y_c >= 0) {
                    drawTexturedScanline(this.raster, texture, y_a, x_b >> 16,
                            x_c >> 16, grad_b >> 8, grad_c >> 8, col_b >> 7,
                            col_c >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false,
                            floor, z_b, z_c);
                    x_c += x_c_off;
                    x_b += x_b_off;
                    z_c += z_c_off;
                    z_b += z_b_off;
                    grad_c += grad_c_off;
                    grad_b += grad_b_off;
                    col_c += col_c_off;
                    col_b += col_b_off;
                    y_a += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return;
            }
            x_b = x_a <<= 16;
            z_b = z_a <<= 16;
            grad_b = grad_a <<= 16;
            col_b = col_a <<= 15;
            if (y_a < 0) {
                x_b -= x_c_off * y_a;
                x_a -= x_a_off * y_a;
                z_b -= z_c_off * y_a;
                z_a -= z_a_off * y_a;
                grad_b -= grad_c_off * y_a;
                grad_a -= grad_a_off * y_a;
                col_b -= col_c_off * y_a;
                col_a -= col_a_off * y_a;
                y_a = 0;
            }
            x_c <<= 16;
            z_c <<= 16;
            grad_c <<= 16;
            col_c <<= 15;
            if (y_c < 0) {
                x_c -= x_b_off * y_c;
                z_c -= z_b_off * y_c;
                grad_c -= grad_b_off * y_c;
                col_c -= col_b_off * y_c;
                y_c = 0;
            }
            int l8 = y_a - viewCenter.getY();
            Oa += Va * l8;
            Ob += Vb * l8;
            Oc += Vc * l8;
            if (y_a != y_c && x_c_off < x_a_off || y_a == y_c
                    && x_b_off > x_a_off) {
                y_b -= y_c;
                y_c -= y_a;
                y_a = scanOffsets[y_a];
                while (--y_c >= 0) {
                    drawTexturedScanline(this.raster, texture, y_a, x_b >> 16,
                            x_a >> 16, grad_b >> 8, grad_a >> 8, col_b >> 7,
                            col_a >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false,
                            floor, z_b, z_a);
                    x_b += x_c_off;
                    x_a += x_a_off;
                    z_b += z_c_off;
                    z_a += z_a_off;
                    grad_b += grad_c_off;
                    grad_a += grad_a_off;
                    col_b += col_c_off;
                    col_a += col_a_off;
                    y_a += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while (--y_b >= 0) {
                    drawTexturedScanline(this.raster, texture, y_a, x_c >> 16,
                            x_a >> 16, grad_c >> 8, grad_a >> 8, col_c >> 7,
                            col_a >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false,
                            floor, z_c, z_a);
                    x_c += x_b_off;
                    x_a += x_a_off;
                    z_c += z_b_off;
                    z_a += z_a_off;
                    grad_c += grad_b_off;
                    grad_a += grad_a_off;
                    col_c += col_b_off;
                    col_a += col_a_off;
                    y_a += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return;
            }
            y_b -= y_c;
            y_c -= y_a;
            y_a = scanOffsets[y_a];
            while (--y_c >= 0) {
                drawTexturedScanline(this.raster, texture, y_a, x_a >> 16,
                        x_b >> 16, grad_a >> 8, grad_b >> 8, col_a >> 7,
                        col_b >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false, floor, z_a, z_b);
                x_b += x_c_off;
                x_a += x_a_off;
                z_b += z_c_off;
                z_a += z_a_off;
                grad_b += grad_c_off;
                grad_a += grad_a_off;
                col_b += col_c_off;
                col_a += col_a_off;
                y_a += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            while (--y_b >= 0) {
                drawTexturedScanline(this.raster, texture, y_a, x_a >> 16,
                        x_c >> 16, grad_a >> 8, grad_c >> 8, col_a >> 7,
                        col_c >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false, floor, z_a, z_c);
                x_c += x_b_off;
                x_a += x_a_off;
                z_c += z_b_off;
                z_a += z_a_off;
                grad_c += grad_b_off;
                grad_a += grad_a_off;
                col_c += col_b_off;
                col_a += col_a_off;
                y_a += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            return;
        }
        if (y_b <= y_c) {
            if (y_b >= this.getClipTop())
                return;
            if (y_c > this.getClipTop())
                y_c = this.getClipTop();
            if (y_a > this.getClipTop())
                y_a = this.getClipTop();
            if (y_c < y_a) {
                x_a = x_b <<= 16;
                z_a = z_b <<= 16;
                grad_a = grad_b <<= 16;
                col_a = col_b <<= 15;
                if (y_b < 0) {
                    x_a -= x_a_off * y_b;
                    x_b -= x_b_off * y_b;
                    z_a -= z_a_off * y_b;
                    z_b -= z_b_off * y_b;
                    grad_a -= grad_a_off * y_b;
                    grad_b -= grad_b_off * y_b;
                    col_a -= col_a_off * y_b;
                    col_b -= col_b_off * y_b;
                    y_b = 0;
                }
                x_c <<= 16;
                z_c <<= 16;
                grad_c <<= 16;
                col_c <<= 15;
                if (y_c < 0) {
                    x_c -= x_c_off * y_c;
                    z_c -= z_c_off * y_c;
                    grad_c -= grad_c_off * y_c;
                    col_c -= col_c_off * y_c;
                    y_c = 0;
                }
                int i9 = y_b - viewCenter.getY();
                Oa += Va * i9;
                Ob += Vb * i9;
                Oc += Vc * i9;
                if (y_b != y_c && x_a_off < x_b_off || y_b == y_c
                        && x_a_off > x_c_off) {
                    y_a -= y_c;
                    y_c -= y_b;
                    y_b = scanOffsets[y_b];
                    while (--y_c >= 0) {
                        drawTexturedScanline(this.raster, texture, y_b, x_a >> 16,
                                x_b >> 16, grad_a >> 8, grad_b >> 8,
                                col_a >> 7, col_b >> 7, Oa, Ob, Oc, Ha, Hb, Hc,
                                color, false, floor, z_a, z_b);
                        x_a += x_a_off;
                        x_b += x_b_off;
                        z_a += z_a_off;
                        z_b += z_b_off;
                        grad_a += grad_a_off;
                        grad_b += grad_b_off;
                        col_a += col_a_off;
                        col_b += col_b_off;
                        y_b += width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    while (--y_a >= 0) {
                        drawTexturedScanline(this.raster, texture, y_b, x_a >> 16,
                                x_c >> 16, grad_a >> 8, grad_c >> 8,
                                col_a >> 7, col_c >> 7, Oa, Ob, Oc, Ha, Hb, Hc,
                                color, false, floor, z_a, z_c);
                        x_a += x_a_off;
                        x_c += x_c_off;
                        z_a += z_a_off;
                        z_c += z_c_off;
                        grad_a += grad_a_off;
                        grad_c += grad_c_off;
                        col_a += col_a_off;
                        col_c += col_c_off;
                        y_b += width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    return;
                }
                y_a -= y_c;
                y_c -= y_b;
                y_b = scanOffsets[y_b];
                while (--y_c >= 0) {
                    drawTexturedScanline(this.raster, texture, y_b, x_b >> 16,
                            x_a >> 16, grad_b >> 8, grad_a >> 8, col_b >> 7,
                            col_a >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false,
                            floor, z_b, z_a);
                    x_a += x_a_off;
                    x_b += x_b_off;
                    z_a += z_a_off;
                    z_b += z_b_off;
                    grad_a += grad_a_off;
                    grad_b += grad_b_off;
                    col_a += col_a_off;
                    col_b += col_b_off;
                    y_b += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while (--y_a >= 0) {
                    drawTexturedScanline(this.raster, texture, y_b, x_c >> 16,
                            x_a >> 16, grad_c >> 8, grad_a >> 8, col_c >> 7,
                            col_a >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false,
                            floor, z_c, z_a);
                    x_a += x_a_off;
                    x_c += x_c_off;
                    z_a += z_a_off;
                    z_c += z_c_off;
                    grad_a += grad_a_off;
                    grad_c += grad_c_off;
                    col_a += col_a_off;
                    col_c += col_c_off;
                    y_b += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return;
            }
            x_c = x_b <<= 16;
            z_c = z_b <<= 16;
            grad_c = grad_b <<= 16;
            col_c = col_b <<= 15;
            if (y_b < 0) {
                x_c -= x_a_off * y_b;
                x_b -= x_b_off * y_b;
                z_c -= z_a_off * y_b;
                z_b -= z_b_off * y_b;
                grad_c -= grad_a_off * y_b;
                grad_b -= grad_b_off * y_b;
                col_c -= col_a_off * y_b;
                col_b -= col_b_off * y_b;
                y_b = 0;
            }
            x_a <<= 16;
            z_a <<= 16;
            grad_a <<= 16;
            col_a <<= 15;
            if (y_a < 0) {
                x_a -= x_c_off * y_a;
                grad_a -= grad_c_off * y_a;
                col_a -= col_c_off * y_a;
                y_a = 0;
            }
            int j9 = y_b - viewCenter.getY();
            Oa += Va * j9;
            Ob += Vb * j9;
            Oc += Vc * j9;
            if (x_a_off < x_b_off) {
                y_c -= y_a;
                y_a -= y_b;
                y_b = scanOffsets[y_b];
                while (--y_a >= 0) {
                    drawTexturedScanline(this.raster, texture, y_b, x_c >> 16,
                            x_b >> 16, grad_c >> 8, grad_b >> 8, col_c >> 7,
                            col_b >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false,
                            floor, z_c, z_b);
                    x_c += x_a_off;
                    x_b += x_b_off;
                    z_c += x_a_off;
                    z_b += x_b_off;
                    grad_c += grad_a_off;
                    grad_b += grad_b_off;
                    col_c += col_a_off;
                    col_b += col_b_off;
                    y_b += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while (--y_c >= 0) {
                    drawTexturedScanline(this.raster, texture, y_b, x_a >> 16,
                            x_b >> 16, grad_a >> 8, grad_b >> 8, col_a >> 7,
                            col_b >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false,
                            floor, z_a, z_b);
                    x_a += x_c_off;
                    x_b += x_b_off;
                    z_a += z_c_off;
                    z_b += z_b_off;
                    grad_a += grad_c_off;
                    grad_b += grad_b_off;
                    col_a += col_c_off;
                    col_b += col_b_off;
                    y_b += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return;
            }
            y_c -= y_a;
            y_a -= y_b;
            y_b = scanOffsets[y_b];
            while (--y_a >= 0) {
                drawTexturedScanline(this.raster, texture, y_b, x_b >> 16,
                        x_c >> 16, grad_b >> 8, grad_c >> 8, col_b >> 7,
                        col_c >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false, floor, z_b, z_c);
                x_c += x_a_off;
                x_b += x_b_off;
                z_c += z_a_off;
                z_b += z_b_off;
                grad_c += grad_a_off;
                grad_b += grad_b_off;
                col_c += col_a_off;
                col_b += col_b_off;
                y_b += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            while (--y_c >= 0) {
                drawTexturedScanline(this.raster, texture, y_b, x_b >> 16,
                        x_a >> 16, grad_b >> 8, grad_a >> 8, col_b >> 7,
                        col_a >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false, floor, z_b, z_a);
                x_a += x_c_off;
                x_b += x_b_off;
                z_a += z_c_off;
                z_b += z_b_off;
                grad_a += grad_c_off;
                grad_b += grad_b_off;
                col_a += col_c_off;
                col_b += col_b_off;
                y_b += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            return;
        }
        if (y_c >= this.getClipTop())
            return;
        if (y_a > this.getClipTop())
            y_a = this.getClipTop();
        if (y_b > this.getClipTop())
            y_b = this.getClipTop();
        if (y_a < y_b) {
            x_b = x_c <<= 16;
            z_b = z_c <<= 16;
            grad_b = grad_c <<= 16;
            col_b = col_c <<= 15;
            if (y_c < 0) {
                x_b -= x_b_off * y_c;
                x_c -= x_c_off * y_c;
                z_b -= z_b_off * y_c;
                z_c -= z_c_off * y_c;
                grad_b -= grad_b_off * y_c;
                grad_c -= grad_c_off * y_c;
                col_b -= col_b_off * y_c;
                col_c -= col_c_off * y_c;
                y_c = 0;
            }
            x_a <<= 16;
            z_a <<= 16;
            grad_a <<= 16;
            col_a <<= 15;
            if (y_a < 0) {
                x_a -= x_a_off * y_a;
                z_a -= z_a_off * y_a;
                grad_a -= grad_a_off * y_a;
                col_a -= col_a_off * y_a;
                y_a = 0;
            }
            int k9 = y_c - viewCenter.getY();
            Oa += Va * k9;
            Ob += Vb * k9;
            Oc += Vc * k9;
            if (x_b_off < x_c_off) {
                y_b -= y_a;
                y_a -= y_c;
                y_c = scanOffsets[y_c];
                while (--y_a >= 0) {
                    drawTexturedScanline(this.raster, texture, y_c, x_b >> 16,
                            x_c >> 16, grad_b >> 8, grad_c >> 8, col_b >> 7,
                            col_c >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false,
                            floor, z_b, z_c);
                    x_b += x_b_off;
                    x_c += x_c_off;
                    z_b += z_b_off;
                    z_c += z_c_off;
                    grad_b += grad_b_off;
                    grad_c += grad_c_off;
                    col_b += col_b_off;
                    col_c += col_c_off;
                    y_c += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while (--y_b >= 0) {
                    drawTexturedScanline(this.raster, texture, y_c, x_b >> 16,
                            x_a >> 16, grad_b >> 8, grad_a >> 8, col_b >> 7,
                            col_a >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false,
                            floor, z_b, z_a);
                    x_b += x_b_off;
                    x_a += x_a_off;
                    z_b += z_b_off;
                    z_a += z_a_off;
                    grad_b += grad_b_off;
                    grad_a += grad_a_off;
                    col_b += col_b_off;
                    col_a += col_a_off;
                    y_c += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return;
            }
            y_b -= y_a;
            y_a -= y_c;
            y_c = scanOffsets[y_c];
            while (--y_a >= 0) {
                drawTexturedScanline(this.raster, texture, y_c, x_c >> 16,
                        x_b >> 16, grad_c >> 8, grad_b >> 8, col_c >> 7,
                        col_b >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false, floor, z_c, z_b);
                x_b += x_b_off;
                x_c += x_c_off;
                z_b += z_b_off;
                z_c += z_c_off;
                grad_b += grad_b_off;
                grad_c += grad_c_off;
                col_b += col_b_off;
                col_c += col_c_off;
                y_c += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            while (--y_b >= 0) {
                drawTexturedScanline(this.raster, texture, y_c, x_a >> 16,
                        x_b >> 16, grad_a >> 8, grad_b >> 8, col_a >> 7,
                        col_b >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false, floor, z_a, z_b);
                x_b += x_b_off;
                x_a += x_a_off;
                z_b += z_b_off;
                z_a += z_a_off;
                grad_b += grad_b_off;
                grad_a += grad_a_off;
                col_b += col_b_off;
                col_a += col_a_off;
                y_c += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            return;
        }
        x_a = x_c <<= 16;
        z_a = z_c <<= 16;
        grad_a = grad_c <<= 16;
        col_a = col_c <<= 15;
        if (y_c < 0) {
            x_a -= x_b_off * y_c;
            x_c -= x_c_off * y_c;
            z_a -= z_b_off * y_c;
            z_c -= z_c_off * y_c;
            grad_a -= grad_b_off * y_c;
            grad_c -= grad_c_off * y_c;
            col_a -= col_b_off * y_c;
            col_c -= col_c_off * y_c;
            y_c = 0;
        }
        x_b <<= 16;
        z_b <<= 16;
        grad_b <<= 16;
        col_b <<= 15;
        if (y_b < 0) {
            x_b -= x_a_off * y_b;
            z_b -= z_a_off * y_b;
            grad_b -= grad_a_off * y_b;
            col_b -= col_a_off * y_b;
            y_b = 0;
        }
        int l9 = y_c - viewCenter.getY();
        Oa += Va * l9;
        Ob += Vb * l9;
        Oc += Vc * l9;
        if (x_b_off < x_c_off) {
            y_a -= y_b;
            y_b -= y_c;
            y_c = scanOffsets[y_c];
            while (--y_b >= 0) {
                drawTexturedScanline(this.raster, texture, y_c, x_a >> 16,
                        x_c >> 16, grad_a >> 8, grad_c >> 8, col_a >> 7,
                        col_c >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false, floor, z_a, z_c);
                x_a += x_b_off;
                x_c += x_c_off;
                z_a += z_b_off;
                z_c += z_c_off;
                grad_a += grad_b_off;
                grad_c += grad_c_off;
                col_a += col_b_off;
                col_c += col_c_off;
                y_c += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            while (--y_a >= 0) {
                drawTexturedScanline(this.raster, texture, y_c, x_b >> 16, x_c >> 16, grad_b >> 8, grad_c >> 8, col_b >> 7, col_c >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false, floor, z_b, z_c);
                x_b += x_a_off;
                x_c += x_c_off;
                z_b += z_a_off;
                z_c += z_c_off;
                grad_b += grad_a_off;
                grad_c += grad_c_off;
                col_b += col_a_off;
                col_c += col_c_off;
                y_c += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            return;
        }
        y_a -= y_b;
        y_b -= y_c;
        y_c = scanOffsets[y_c];
        while (--y_b >= 0) {
            drawTexturedScanline(this.raster, texture, y_c, x_c >> 16, x_a >> 16, grad_c >> 8, grad_a >> 8, col_c >> 7, col_a >> 7, Oa, Ob, Oc, Ha, Hb, Hc, color, false, floor, z_c, z_a);
            x_a += x_b_off;
            x_c += x_c_off;
            z_a += z_b_off;
            z_c += z_c_off;
            grad_a += grad_b_off;
            grad_c += grad_c_off;
            col_a += col_b_off;
            col_c += col_c_off;
            y_c += width;
            Oa += Va;
            Ob += Vb;
            Oc += Vc;
        }
        while (--y_a >= 0) {
            drawTexturedScanline(this.raster, texture, y_c, x_c >> 16, x_b >> 16,
                    grad_c >> 8, grad_b >> 8, col_c >> 7, col_b >> 7, Oa, Ob,
                    Oc, Ha, Hb, Hc, color, false, floor, z_c, z_b);
            x_b += x_a_off;
            x_c += x_c_off;
            z_b += z_a_off;
            z_c += z_c_off;
            grad_b += grad_a_off;
            grad_c += grad_c_off;
            col_b += col_a_off;
            col_c += col_c_off;
            y_c += width;
            Oa += Va;
            Ob += Vb;
            Oc += Vc;
        }
        return;
    }

	 private void drawTexturedScanline(int[] dest, int[] texture, int dest_off, int start_x, int end_x, int shadeValue, int gradient, int start_col, int end_col, int arg7, int arg8, int arg9, int arg10, int arg11, int arg12, int color, boolean force, boolean floor, int z1, int z2) {
	        boolean isObject = floor;
	        int rgb = 0;
	        int loops = 0;
	        if (end_x - start_x > 0) {
	            z2 = (z2 - z1) / (end_x - start_x);
	        }
	        if (start_x >= end_x) {
	            return;
	        }
	        int k3;
	        int j3;
	        if (restrictEdges) {
	            j3 = (gradient - shadeValue) / (end_x - start_x);
	            if (end_x > maxRight) {
	                end_x = maxRight;
	            }
	            if (start_x < 0) {
	                shadeValue -= start_x * j3;
	                z1 -= start_x * z2;
	                start_x = 0;
	            }
	            if (start_x >= end_x) {
	                return;
	            }
	            k3 = end_x - start_x >> 3;
	            j3 <<= 12;
	            shadeValue <<= 9;
	        } else {
	            if (end_x - start_x > 7) {
	                k3 = end_x - start_x >> 3;
	                j3 = (gradient - shadeValue) * Constants.SHADOW_DECAY[k3] >> 6;
	            } else {
	                k3 = 0;
	                j3 = 0;
	            }
	            shadeValue <<= 9;
	        }
	        end_col = (end_col - start_col) * Constants.SHADOW_DECAY[end_x - start_x >> 2] >> 14;
	        dest_off += start_x;
	        int j4 = 0;
	        int l4 = 0;
	        int l6 = start_x - viewCenter.getX();
	        arg7 += (arg10 >> 3) * l6;
	        arg8 += (arg11 >> 3) * l6;
	        arg9 += (arg12 >> 3) * l6;
	        int l5 = arg9 >> 14;
	        if (l5 != 0) {
	            rgb = arg7 / l5;
	            loops = arg8 / l5;
	            if (rgb < 0) {
	                rgb = 0;
	            } else if (rgb > 16256) {
	                rgb = 16256;
	            }
	        }
	        arg7 += arg10;
	        arg8 += arg11;
	        arg9 += arg12;
	        l5 = arg9 >> 14;
	        if (l5 != 0) {
	            j4 = arg7 / l5;
	            l4 = arg8 / l5;
	            if (j4 < 7) {
	                j4 = 7;
	            } else if (j4 > 16256) {
	                j4 = 16256;
	            }
	        }
	        int j7 = j4 - rgb >> 3;
	        int l7 = l4 - loops >> 3;
	        rgb += shadeValue & 0x600000;
	        int glb_alpha = currentAlpha;
	        if (glb_alpha < 0 || glb_alpha > 0xff) {
	            glb_alpha = 0;
			}
	        glb_alpha = 0xff - glb_alpha;
	        int src;
	        int src_alpha;
	        int src_delta;
	        int dst;
	        while (k3-- > 0) {
	            for (int i = 0; i != 8; ++i) {
	                src = texture[(loops & 0x3f80) + (rgb >> 7)];
	                src_alpha = src >>> 24;
	                if (src_alpha != 0 || force) {
	                    if (src_alpha != 0xff && color >= 0) {
	                        if (src_alpha == 0)
	                            src = color;
	                        else {
	                            src_delta = 0xff - src_alpha;
	                            src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
	                        }
	                        src_alpha = 0xff;
	                    }
	                    if (glb_alpha != 0xff) {
	                        src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;
						}
	                    if (src_alpha != 0) {
	                        if (src_alpha == 0xff) {
	                            dest[dest_off] = (src & 0xffffff);
	                        } else {
	                            int dest_alpha = 0xff - src_alpha;
	                            dst = dest[dest_off];
	                            src_delta = 0xff - src_alpha;
	                            dest[dest_off] = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8));
	                        }
	                    }
	                } else {

	                }
	                if (!isObject) {
	                    int tex = texture[(loops & 0x3f80) + (rgb >> 7)];
	                    dest[dest_off] = colourPalette[(start_col >> 8 & 0xff80) | ((start_col >> 8 & 0x7f) * ((tex >> 17 & 0x7f) + (tex >> 9 & 0x7f) + (tex >> 1 & 0x7f) + 0x7f >> 2) >> 7)];
	                } else {
	                    dst = dest[dest_off];
	                    src_delta = 0xff - src_alpha;
	                    dest[dest_off] = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8));
	                }
	                dest_off++;
	                rgb += j7;
	                loops += l7;
	            }
	            start_col += end_col;
	            rgb = j4;
	            loops = l4;
	            arg7 += arg10;
	            arg8 += arg11;
	            arg9 += arg12;
	            int i6 = arg9 >> 14;
	            if (i6 != 0) {
	                j4 = arg7 / i6;
	                l4 = arg8 / i6;
	                if (j4 < 7) {
	                    j4 = 7;
	                } else if (j4 > 16256) {
	                    j4 = 16256;
	                }
	            }
	            j7 = j4 - rgb >> 3;
	            l7 = l4 - loops >> 3;
	            shadeValue += j3;
	            rgb += shadeValue & 0x600000;
	        }
	        for (k3 = end_x - start_x & 7; k3-- > 0; ) {
	            src = texture[(loops & 0x3f80) + (rgb >> 7)];
	            src_alpha = src >>> 24;
	            if (src_alpha != 0) {
	                if (src_alpha != 0xff && color >= 0) {
	                    if (src_alpha == 0)
	                        src = color;
	                    else {
	                        src_delta = 0xff - src_alpha;
	                        src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
	                    }
	                    src_alpha = 0xff;
	                }
	                if (glb_alpha != 0xff) {
	                    src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;
					}
	                if (src_alpha != 0) {
	                    if (src_alpha == 0xff) {
	                        dest[dest_off] = src & 0xffffff;
	                    } else {
	                        int dest_alpha = 0xff - src_alpha;
	                        dst = dest[dest_off];
	                        src_delta = 0xff - src_alpha;
	                        dest[dest_off] = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8));
	                    }
	                }
	            }
	            if (!isObject) {
	                int tex = texture[(loops & 0x3f80) + (rgb >> 7)];
	                dest[dest_off] = colourPalette[(start_col >> 8 & 0xff80) | ((start_col >> 8 & 0x7f) * ((tex >> 17 & 0x7f) + (tex >> 9 & 0x7f) + (tex >> 1 & 0x7f) + 0x7f >> 2) >> 7)];
	            } else {
	                dst = dest[dest_off];
	                src_delta = 0xff - src_alpha;
	                dest[dest_off] = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8));
	            }
	            dest_off++;
	            rgb += j7;
	            loops += l7;
	        }
	    }


	public void drawTexturedTriangle(int faceYX, int faceYY, int faceYZ, int faceXX, int faceXY, int faceXZ, int k1, int l1, int i2, int j2,
			int k2, int l2, int i3, int j3, int k3, int l3, int i4, int j4, int textureId) {

        int[] pixels = TextureLoader.getTexturePixels(textureId);

		if(pixels == null) {
			drawShadedTriangle(faceYX, faceYY, faceYZ, faceXX, faceXY, faceXZ, k1, l1, i2);
			return;
		}
		currentTextureTransparent = !TextureLoader.getTextureTransparent(textureId);
		k2 = j2 - k2;
		j3 = i3 - j3;
		i4 = l3 - i4;
		l2 -= j2;
		k3 -= i3;
		j4 -= l3;
		int l4 = l2 * i3 - k3 * j2 << 14;
		int i5 = k3 * l3 - j4 * i3 << 8;
		int j5 = j4 * j2 - l2 * l3 << 5;
		int k5 = k2 * i3 - j3 * j2 << 14;
		int l5 = j3 * l3 - i4 * i3 << 8;
		int i6 = i4 * j2 - k2 * l3 << 5;
		int j6 = j3 * l2 - k2 * k3 << 14;
		int k6 = i4 * k3 - j3 * j4 << 8;
		int l6 = k2 * j4 - i4 * l2 << 5;
		int i7 = 0;
		int j7 = 0;
		if (faceYY != faceYX) {
			i7 = (faceXY - faceXX << 16) / (faceYY - faceYX);
			j7 = (l1 - k1 << 16) / (faceYY - faceYX);
		}
		int k7 = 0;
		int l7 = 0;
		if (faceYZ != faceYY) {
			k7 = (faceXZ - faceXY << 16) / (faceYZ - faceYY);
			l7 = (i2 - l1 << 16) / (faceYZ - faceYY);
		}
		int i8 = 0;
		int j8 = 0;
		if (faceYZ != faceYX) {
			i8 = (faceXX - faceXZ << 16) / (faceYX - faceYZ);
			j8 = (k1 - i2 << 16) / (faceYX - faceYZ);
		}
		if (faceYX <= faceYY && faceYX <= faceYZ) {
			if (faceYX >= this.getClipTop())
				return;
			if (faceYY > this.getClipTop()) {
				faceYY = this.getClipTop();
			}
			if (faceYZ > this.getClipTop()) {
				faceYZ = this.getClipTop();
			}
			if (faceYY < faceYZ) {
				faceXZ = faceXX <<= 16;
				i2 = k1 <<= 16;
				if (faceYX < 0) {
					faceXZ -= i8 * faceYX;
					faceXX -= i7 * faceYX;
					i2 -= j8 * faceYX;
					k1 -= j7 * faceYX;
					faceYX = 0;
				}
				faceXY <<= 16;
				l1 <<= 16;
				if (faceYY < 0) {
					faceXY -= k7 * faceYY;
					l1 -= l7 * faceYY;
					faceYY = 0;
				}
				int k8 = faceYX - viewCenter.getY();
				l4 += j5 * k8;
				k5 += i6 * k8;
				j6 += l6 * k8;
				if (faceYX != faceYY && i8 < i7 || faceYX == faceYY && i8 > k7) {
					faceYZ -= faceYY;
					faceYY -= faceYX;
					faceYX = scanOffsets[faceYX];
					while (--faceYY >= 0) {
						drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXZ >> 16, faceXX >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5,
								l5, k6);
						faceXZ += i8;
						faceXX += i7;
						i2 += j8;
						k1 += j7;
						faceYX += this.width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					while (--faceYZ >= 0) {
						drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXZ >> 16, faceXY >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5,
								l5, k6);
						faceXZ += i8;
						faceXY += k7;
						i2 += j8;
						l1 += l7;
						faceYX += this.width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					return;
				}
				faceYZ -= faceYY;
				faceYY -= faceYX;
				faceYX = scanOffsets[faceYX];
				while (--faceYY >= 0) {
					drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXX >> 16, faceXZ >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5,
							k6);
					faceXZ += i8;
					faceXX += i7;
					i2 += j8;
					k1 += j7;
					faceYX += this.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--faceYZ >= 0) {
					drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXY >> 16, faceXZ >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5,
							k6);
					faceXZ += i8;
					faceXY += k7;
					i2 += j8;
					l1 += l7;
					faceYX += this.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			faceXY = faceXX <<= 16;
			l1 = k1 <<= 16;
			if (faceYX < 0) {
				faceXY -= i8 * faceYX;
				faceXX -= i7 * faceYX;
				l1 -= j8 * faceYX;
				k1 -= j7 * faceYX;
				faceYX = 0;
			}
			faceXZ <<= 16;
			i2 <<= 16;
			if (faceYZ < 0) {
				faceXZ -= k7 * faceYZ;
				i2 -= l7 * faceYZ;
				faceYZ = 0;
			}
			int l8 = faceYX - viewCenter.getY();
			l4 += j5 * l8;
			k5 += i6 * l8;
			j6 += l6 * l8;
			if (faceYX != faceYZ && i8 < i7 || faceYX == faceYZ && k7 > i7) {
				faceYY -= faceYZ;
				faceYZ -= faceYX;
				faceYX = scanOffsets[faceYX];
				while (--faceYZ >= 0) {
					drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXY >> 16, faceXX >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5,
							k6);
					faceXY += i8;
					faceXX += i7;
					l1 += j8;
					k1 += j7;
					faceYX += this.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--faceYY >= 0) {
					drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXZ >> 16, faceXX >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5,
							k6);
					faceXZ += k7;
					faceXX += i7;
					i2 += l7;
					k1 += j7;
					faceYX += this.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			faceYY -= faceYZ;
			faceYZ -= faceYX;
			faceYX = scanOffsets[faceYX];
			while (--faceYZ >= 0) {
				drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXX >> 16, faceXY >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
				faceXY += i8;
				faceXX += i7;
				l1 += j8;
				k1 += j7;
				faceYX += this.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--faceYY >= 0) {
				drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXX >> 16, faceXZ >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				faceXZ += k7;
				faceXX += i7;
				i2 += l7;
				k1 += j7;
				faceYX += this.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		if (faceYY <= faceYZ) {
			if (faceYY >= this.getClipTop())
				return;
			if (faceYZ > this.getClipTop()) {
				faceYZ = this.getClipTop();
			}
			if (faceYX > this.getClipTop()) {
				faceYX = this.getClipTop();
			}
			if (faceYZ < faceYX) {
				faceXX = faceXY <<= 16;
				k1 = l1 <<= 16;
				if (faceYY < 0) {
					faceXX -= i7 * faceYY;
					faceXY -= k7 * faceYY;
					k1 -= j7 * faceYY;
					l1 -= l7 * faceYY;
					faceYY = 0;
				}
				faceXZ <<= 16;
				i2 <<= 16;
				if (faceYZ < 0) {
					faceXZ -= i8 * faceYZ;
					i2 -= j8 * faceYZ;
					faceYZ = 0;
				}
				int i9 = faceYY - viewCenter.getY();
				l4 += j5 * i9;
				k5 += i6 * i9;
				j6 += l6 * i9;
				if (faceYY != faceYZ && i7 < k7 || faceYY == faceYZ && i7 > i8) {
					faceYX -= faceYZ;
					faceYZ -= faceYY;
					faceYY = scanOffsets[faceYY];
					while (--faceYZ >= 0) {
						drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXX >> 16, faceXY >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5,
								l5, k6);
						faceXX += i7;
						faceXY += k7;
						k1 += j7;
						l1 += l7;
						faceYY += this.width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					while (--faceYX >= 0) {
						drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXX >> 16, faceXZ >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5,
								l5, k6);
						faceXX += i7;
						faceXZ += i8;
						k1 += j7;
						i2 += j8;
						faceYY += this.width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					return;
				}
				faceYX -= faceYZ;
				faceYZ -= faceYY;
				faceYY = scanOffsets[faceYY];
				while (--faceYZ >= 0) {
					drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXY >> 16, faceXX >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5,
							k6);
					faceXX += i7;
					faceXY += k7;
					k1 += j7;
					l1 += l7;
					faceYY += this.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--faceYX >= 0) {
					drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXZ >> 16, faceXX >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5,
							k6);
					faceXX += i7;
					faceXZ += i8;
					k1 += j7;
					i2 += j8;
					faceYY += this.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			faceXZ = faceXY <<= 16;
			i2 = l1 <<= 16;
			if (faceYY < 0) {
				faceXZ -= i7 * faceYY;
				faceXY -= k7 * faceYY;
				i2 -= j7 * faceYY;
				l1 -= l7 * faceYY;
				faceYY = 0;
			}
			faceXX <<= 16;
			k1 <<= 16;
			if (faceYX < 0) {
				faceXX -= i8 * faceYX;
				k1 -= j8 * faceYX;
				faceYX = 0;
			}
			int j9 = faceYY - viewCenter.getY();
			l4 += j5 * j9;
			k5 += i6 * j9;
			j6 += l6 * j9;
			if (i7 < k7) {
				faceYZ -= faceYX;
				faceYX -= faceYY;
				faceYY = scanOffsets[faceYY];
				while (--faceYX >= 0) {
					drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXZ >> 16, faceXY >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5,
							k6);
					faceXZ += i7;
					faceXY += k7;
					i2 += j7;
					l1 += l7;
					faceYY += this.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--faceYZ >= 0) {
					drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXX >> 16, faceXY >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5,
							k6);
					faceXX += i8;
					faceXY += k7;
					k1 += j8;
					l1 += l7;
					faceYY += this.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			faceYZ -= faceYX;
			faceYX -= faceYY;
			faceYY = scanOffsets[faceYY];
			while (--faceYX >= 0) {
				drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXY >> 16, faceXZ >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				faceXZ += i7;
				faceXY += k7;
				i2 += j7;
				l1 += l7;
				faceYY += this.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--faceYZ >= 0) {
				drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXY >> 16, faceXX >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
				faceXX += i8;
				faceXY += k7;
				k1 += j8;
				l1 += l7;
				faceYY += this.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		if (faceYZ >= this.getClipTop())
			return;
		if (faceYX > this.getClipTop()) {
			faceYX = this.getClipTop();
		}
		if (faceYY > this.getClipTop()) {
			faceYY = this.getClipTop();
		}
		if (faceYX < faceYY) {
			faceXY = faceXZ <<= 16;
			l1 = i2 <<= 16;
			if (faceYZ < 0) {
				faceXY -= k7 * faceYZ;
				faceXZ -= i8 * faceYZ;
				l1 -= l7 * faceYZ;
				i2 -= j8 * faceYZ;
				faceYZ = 0;
			}
			faceXX <<= 16;
			k1 <<= 16;
			if (faceYX < 0) {
				faceXX -= i7 * faceYX;
				k1 -= j7 * faceYX;
				faceYX = 0;
			}
			int k9 = faceYZ - viewCenter.getY();
			l4 += j5 * k9;
			k5 += i6 * k9;
			j6 += l6 * k9;
			if (k7 < i8) {
				faceYY -= faceYX;
				faceYX -= faceYZ;
				faceYZ = scanOffsets[faceYZ];
				while (--faceYX >= 0) {
					drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXY >> 16, faceXZ >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5,
							k6);
					faceXY += k7;
					faceXZ += i8;
					l1 += l7;
					i2 += j8;
					faceYZ += this.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--faceYY >= 0) {
					drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXY >> 16, faceXX >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5,
							k6);
					faceXY += k7;
					faceXX += i7;
					l1 += l7;
					k1 += j7;
					faceYZ += this.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			faceYY -= faceYX;
			faceYX -= faceYZ;
			faceYZ = scanOffsets[faceYZ];
			while (--faceYX >= 0) {
				drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXZ >> 16, faceXY >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
				faceXY += k7;
				faceXZ += i8;
				l1 += l7;
				i2 += j8;
				faceYZ += this.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--faceYY >= 0) {
				drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXX >> 16, faceXY >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
				faceXY += k7;
				faceXX += i7;
				l1 += l7;
				k1 += j7;
				faceYZ += this.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		faceXX = faceXZ <<= 16;
		k1 = i2 <<= 16;
		if (faceYZ < 0) {
			faceXX -= k7 * faceYZ;
			faceXZ -= i8 * faceYZ;
			k1 -= l7 * faceYZ;
			i2 -= j8 * faceYZ;
			faceYZ = 0;
		}
		faceXY <<= 16;
		l1 <<= 16;
		if (faceYY < 0) {
			faceXY -= i7 * faceYY;
			l1 -= j7 * faceYY;
			faceYY = 0;
		}
		int l9 = faceYZ - viewCenter.getY();
		l4 += j5 * l9;
		k5 += i6 * l9;
		j6 += l6 * l9;
		if (k7 < i8) {
			faceYX -= faceYY;
			faceYY -= faceYZ;
			faceYZ = scanOffsets[faceYZ];
			while (--faceYY >= 0) {
				drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXX >> 16, faceXZ >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				faceXX += k7;
				faceXZ += i8;
				k1 += l7;
				i2 += j8;
				faceYZ += this.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--faceYX >= 0) {
				drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXY >> 16, faceXZ >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				faceXY += i7;
				faceXZ += i8;
				l1 += j7;
				i2 += j8;
				faceYZ += this.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		faceYX -= faceYY;
		faceYY -= faceYZ;
		faceYZ = scanOffsets[faceYZ];
		while (--faceYY >= 0) {
			drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXZ >> 16, faceXX >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
			faceXX += k7;
			faceXZ += i8;
			k1 += l7;
			i2 += j8;
			faceYZ += this.width;
			l4 += j5;
			k5 += i6;
			j6 += l6;
		}
		while (--faceYX >= 0) {
			drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXZ >> 16, faceXY >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
			faceXY += i7;
			faceXZ += i8;
			l1 += j7;
			i2 += j8;
			faceYZ += this.width;
			l4 += j5;
			k5 += i6;
			j6 += l6;
		}
	}

	public void drawTriangleOutline(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2) {
		int j2 = 0;
		int k2 = 0;
		if (j != i) {
			j2 = (i1 - l << 16) / (j - i);
			k2 = (l1 - k1 << 15) / (j - i);
		}

		int l2 = 0;
		int i3 = 0;
		if (k != j) {
			l2 = (j1 - i1 << 16) / (k - j);
			i3 = (i2 - l1 << 15) / (k - j);
		}

		int j3 = 0;
		int k3 = 0;
		if (k != i) {
			j3 = (l - j1 << 16) / (i - k);
			k3 = (k1 - i2 << 15) / (i - k);
		}

		if (i <= j && i <= k) {
			if (i >= this.getClipTop())
				return;
			if (j > this.getClipTop()) {
				j = this.getClipTop();
			}
			if (k > this.getClipTop()) {
				k = this.getClipTop();
			}
			if (j < k) {
				j1 = l <<= 16;
				i2 = k1 <<= 15;
				if (i < 0) {
					j1 -= j3 * i;
					l -= j2 * i;
					i2 -= k3 * i;
					k1 -= k2 * i;
					i = 0;
				}
				i1 <<= 16;
				l1 <<= 15;
				if (j < 0) {
					i1 -= l2 * j;
					l1 -= i3 * j;
					j = 0;
				}
				if (i != j && j3 < j2 || i == j && j3 > l2) {
					k -= j;
					j -= i;
					for (i = scanOffsets[i]; --j >= 0; i += this.width) {
						drawLine(this.raster, i, 0, 0, j1 >> 16, l >> 16, i2 >> 7, k1 >> 7);
						j1 += j3;
						l += j2;
						i2 += k3;
						k1 += k2;
					}

					while (--k >= 0) {
						drawLine(this.raster, i, 0, 0, j1 >> 16, i1 >> 16, i2 >> 7, l1 >> 7);
						j1 += j3;
						i1 += l2;
						i2 += k3;
						l1 += i3;
						i += this.width;
					}
					return;
				}
				k -= j;
				j -= i;
				for (i = scanOffsets[i]; --j >= 0; i += this.width) {
					drawLine(this.raster, i, 0, 0, l >> 16, j1 >> 16, k1 >> 7, i2 >> 7);
					j1 += j3;
					l += j2;
					i2 += k3;
					k1 += k2;
				}

				while (--k >= 0) {
					drawLine(this.raster, i, 0, 0, i1 >> 16, j1 >> 16, l1 >> 7, i2 >> 7);
					j1 += j3;
					i1 += l2;
					i2 += k3;
					l1 += i3;
					i += this.width;
				}
				return;
			}
			i1 = l <<= 16;
			l1 = k1 <<= 15;
			if (i < 0) {
				i1 -= j3 * i;
				l -= j2 * i;
				l1 -= k3 * i;
				k1 -= k2 * i;
				i = 0;
			}
			j1 <<= 16;
			i2 <<= 15;
			if (k < 0) {
				j1 -= l2 * k;
				i2 -= i3 * k;
				k = 0;
			}
			if (i != k && j3 < j2 || i == k && l2 > j2) {
				j -= k;
				k -= i;
				for (i = scanOffsets[i]; --k >= 0; i += this.width) {
					drawLine(this.raster, i, 0, 0, i1 >> 16, l >> 16, l1 >> 7, k1 >> 7);
					i1 += j3;
					l += j2;
					l1 += k3;
					k1 += k2;
				}

				while (--j >= 0) {
					drawLine(this.raster, i, 0, 0, j1 >> 16, l >> 16, i2 >> 7, k1 >> 7);
					j1 += l2;
					l += j2;
					i2 += i3;
					k1 += k2;
					i += this.width;
				}
				return;
			}
			j -= k;
			k -= i;
			for (i = scanOffsets[i]; --k >= 0; i += this.width) {
				drawLine(this.raster, i, 0, 0, l >> 16, i1 >> 16, k1 >> 7, l1 >> 7);
				i1 += j3;
				l += j2;
				l1 += k3;
				k1 += k2;
			}

			while (--j >= 0) {
				drawLine(this.raster, i, 0, 0, l >> 16, j1 >> 16, k1 >> 7, i2 >> 7);
				j1 += l2;
				l += j2;
				i2 += i3;
				k1 += k2;
				i += this.width;
			}
			return;
		}
		if (j <= k) {
			if (j >= this.getClipTop())
				return;
			if (k > this.getClipTop()) {
				k = this.getClipTop();
			}
			if (i > this.getClipTop()) {
				i = this.getClipTop();
			}
			if (k < i) {
				l = i1 <<= 16;
				k1 = l1 <<= 15;
				if (j < 0) {
					l -= j2 * j;
					i1 -= l2 * j;
					k1 -= k2 * j;
					l1 -= i3 * j;
					j = 0;
				}
				j1 <<= 16;
				i2 <<= 15;
				if (k < 0) {
					j1 -= j3 * k;
					i2 -= k3 * k;
					k = 0;
				}
				if (j != k && j2 < l2 || j == k && j2 > j3) {
					i -= k;
					k -= j;
					for (j = scanOffsets[j]; --k >= 0; j += this.width) {
						drawLine(this.raster, j, 0, 0, l >> 16, i1 >> 16, k1 >> 7, l1 >> 7);
						l += j2;
						i1 += l2;
						k1 += k2;
						l1 += i3;
					}

					while (--i >= 0) {
						drawLine(this.raster, j, 0, 0, l >> 16, j1 >> 16, k1 >> 7, i2 >> 7);
						l += j2;
						j1 += j3;
						k1 += k2;
						i2 += k3;
						j += this.width;
					}
					return;
				}
				i -= k;
				k -= j;
				for (j = scanOffsets[j]; --k >= 0; j += this.width) {
					drawLine(this.raster, j, 0, 0, i1 >> 16, l >> 16, l1 >> 7, k1 >> 7);
					l += j2;
					i1 += l2;
					k1 += k2;
					l1 += i3;
				}

				while (--i >= 0) {
					drawLine(this.raster, j, 0, 0, j1 >> 16, l >> 16, i2 >> 7, k1 >> 7);
					l += j2;
					j1 += j3;
					k1 += k2;
					i2 += k3;
					j += this.width;
				}
				return;
			}
			j1 = i1 <<= 16;
			i2 = l1 <<= 15;
			if (j < 0) {
				j1 -= j2 * j;
				i1 -= l2 * j;
				i2 -= k2 * j;
				l1 -= i3 * j;
				j = 0;
			}
			l <<= 16;
			k1 <<= 15;
			if (i < 0) {
				l -= j3 * i;
				k1 -= k3 * i;
				i = 0;
			}
			if (j2 < l2) {
				k -= i;
				i -= j;
				for (j = scanOffsets[j]; --i >= 0; j += this.width) {
					drawLine(this.raster, j, 0, 0, j1 >> 16, i1 >> 16, i2 >> 7, l1 >> 7);
					j1 += j2;
					i1 += l2;
					i2 += k2;
					l1 += i3;
				}

				while (--k >= 0) {
					drawLine(this.raster, j, 0, 0, l >> 16, i1 >> 16, k1 >> 7, l1 >> 7);
					l += j3;
					i1 += l2;
					k1 += k3;
					l1 += i3;
					j += this.width;
				}
				return;
			}
			k -= i;
			i -= j;
			for (j = scanOffsets[j]; --i >= 0; j += this.width) {
				drawLine(this.raster, j, 0, 0, i1 >> 16, j1 >> 16, l1 >> 7, i2 >> 7);
				j1 += j2;
				i1 += l2;
				i2 += k2;
				l1 += i3;
			}

			while (--k >= 0) {
				drawLine(this.raster, j, 0, 0, i1 >> 16, l >> 16, l1 >> 7, k1 >> 7);
				l += j3;
				i1 += l2;
				k1 += k3;
				l1 += i3;
				j += this.width;
			}
			return;
		}
		if (k >= this.getClipTop())
			return;
		if (i > this.getClipTop()) {
			i = this.getClipTop();
		}
		if (j > this.getClipTop()) {
			j = this.getClipTop();
		}
		if (i < j) {
			i1 = j1 <<= 16;
			l1 = i2 <<= 15;
			if (k < 0) {
				i1 -= l2 * k;
				j1 -= j3 * k;
				l1 -= i3 * k;
				i2 -= k3 * k;
				k = 0;
			}
			l <<= 16;
			k1 <<= 15;
			if (i < 0) {
				l -= j2 * i;
				k1 -= k2 * i;
				i = 0;
			}
			if (l2 < j3) {
				j -= i;
				i -= k;
				for (k = scanOffsets[k]; --i >= 0; k += this.width) {
					drawLine(this.raster, k, 0, 0, i1 >> 16, j1 >> 16, l1 >> 7, i2 >> 7);
					i1 += l2;
					j1 += j3;
					l1 += i3;
					i2 += k3;
				}

				while (--j >= 0) {
					drawLine(this.raster, k, 0, 0, i1 >> 16, l >> 16, l1 >> 7, k1 >> 7);
					i1 += l2;
					l += j2;
					l1 += i3;
					k1 += k2;
					k += this.width;
				}
				return;
			}
			j -= i;
			i -= k;
			for (k = scanOffsets[k]; --i >= 0; k += this.width) {
				drawLine(this.raster, k, 0, 0, j1 >> 16, i1 >> 16, i2 >> 7, l1 >> 7);
				i1 += l2;
				j1 += j3;
				l1 += i3;
				i2 += k3;
			}

			while (--j >= 0) {
				drawLine(this.raster, k, 0, 0, l >> 16, i1 >> 16, k1 >> 7, l1 >> 7);
				i1 += l2;
				l += j2;
				l1 += i3;
				k1 += k2;
				k += this.width;
			}
			return;
		}
		l = j1 <<= 16;
		k1 = i2 <<= 15;
		if (k < 0) {
			l -= l2 * k;
			j1 -= j3 * k;
			k1 -= i3 * k;
			i2 -= k3 * k;
			k = 0;
		}
		i1 <<= 16;
		l1 <<= 15;
		if (j < 0) {
			i1 -= j2 * j;
			l1 -= k2 * j;
			j = 0;
		}
		if (l2 < j3) {
			i -= j;
			j -= k;
			for (k = scanOffsets[k]; --j >= 0; k += this.width) {
				drawLine(this.raster, k, 0, 0, l >> 16, j1 >> 16, k1 >> 7, i2 >> 7);
				l += l2;
				j1 += j3;
				k1 += i3;
				i2 += k3;
			}

			while (--i >= 0) {
				drawLine(this.raster, k, 0, 0, i1 >> 16, j1 >> 16, l1 >> 7, i2 >> 7);
				i1 += j2;
				j1 += j3;
				l1 += k2;
				i2 += k3;
				k += this.width;
			}
			return;
		}
		i -= j;
		j -= k;
		for (k = scanOffsets[k]; --j >= 0; k += this.width) {
			drawLine(this.raster, k, 0, 0, j1 >> 16, l >> 16, i2 >> 7, k1 >> 7);
			l += l2;
			j1 += j3;
			k1 += i3;
			i2 += k3;
		}

		while (--i >= 0) {
			drawLine(this.raster, k, 0, 0, j1 >> 16, i1 >> 16, i2 >> 7, l1 >> 7);
			i1 += j2;
			j1 += j3;
			l1 += k2;
			i2 += k3;
			k += this.width;
		}
	}

	public void method377(int[] ai, int i, int j, int k, int l, int i1) {
		if (restrictEdges) {
			if (i1 > this.maxRight) {
				i1 = this.maxRight;
			}
			if (l < 0) {
				l = 0;
			}
		}
		if (l >= i1)
			return;
		i += l;
		k = i1 - l >> 2;
		if (currentAlpha == 0) {
			while (--k >= 0) {
				ai[i++] = j;
				ai[i++] = j;
				ai[i++] = j;
				ai[i++] = j;
			}
			for (k = i1 - l & 3; --k >= 0;) {
				ai[i++] = j;
			}

			return;
		}
		int j1 = currentAlpha;
		int k1 = 256 - currentAlpha;
		j = ((j & 0xff00ff) * k1 >> 8 & 0xff00ff) + ((j & 0xff00) * k1 >> 8 & 0xff00);
		while (--k >= 0) {
			ai[i++] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff) + ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			ai[i++] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff) + ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			ai[i++] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff) + ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			ai[i++] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff) + ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
		}
		for (k = i1 - l & 3; --k >= 0;) {
			ai[i++] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff) + ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
		}

	}

	public void drawTexturedLine(int[] ai, int[] ai1, int i, int j, int k, int l, int i1, int j1, int k1, int l1,
                                 int i2, int j2, int k2, int l2, int i3) {
		if (l >= i1)
			return;
		int j3;
		int k3;
		if (restrictEdges) {
			j3 = (k1 - j1) / (i1 - l);
			if (i1 > this.maxRight) {
				i1 = this.maxRight;
			}
			if (l < 0) {
				j1 -= l * j3;
				l = 0;
			}
			if (l >= i1)
				return;
			k3 = i1 - l >> 3;
			j3 <<= 12;
			j1 <<= 9;
		} else {
			if (i1 - l > 7) {
				k3 = i1 - l >> 3;
				j3 = (k1 - j1) * Constants.SHADOW_DECAY[k3] >> 6;
			} else {
				k3 = 0;
				j3 = 0;
			}
			j1 <<= 9;
		}
		k += l;
		int j4 = 0;
		int l4 = 0;
		int l6 = l - viewCenter.getX();
		l1 += (k2 >> 3) * l6;
		i2 += (l2 >> 3) * l6;
		j2 += (i3 >> 3) * l6;
		int l5 = j2 >> 14;
		if (l5 != 0) {
			i = l1 / l5;
			j = i2 / l5;
			if (i < 0) {
				i = 0;
			} else if (i > 16256) {
				i = 16256;
			}
		}
		l1 += k2;
		i2 += l2;
		j2 += i3;
		l5 = j2 >> 14;
		if (l5 != 0) {
			j4 = l1 / l5;
			l4 = i2 / l5;
			if (j4 < 7) {
				j4 = 7;
			} else if (j4 > 16256) {
				j4 = 16256;
			}
		}
		int j7 = j4 - i >> 3;
		int l7 = l4 - j >> 3;
		i += j1 & 0x600000;
		int j8 = j1 >> 23;
		if (currentTextureTransparent) {
			while (k3-- > 0) {
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i = j4;
				j = l4;
				l1 += k2;
				i2 += l2;
				j2 += i3;
				int i6 = j2 >> 14;
				if (i6 != 0) {
					j4 = l1 / i6;
					l4 = i2 / i6;
					if (j4 < 7) {
						j4 = 7;
					} else if (j4 > 16256) {
						j4 = 16256;
					}
				}
				j7 = j4 - i >> 3;
				l7 = l4 - j >> 3;
				j1 += j3;
				i += j1 & 0x600000;
				j8 = j1 >> 23;
			}
			for (k3 = i1 - l & 7; k3-- > 0;) {
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
			}

			return;
		}
		while (k3-- > 0) {
			int i9;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
				ai[k] = i9;
			}
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
				ai[k] = i9;
			}
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
				ai[k] = i9;
			}
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
				ai[k] = i9;
			}
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
				ai[k] = i9;
			}
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
				ai[k] = i9;
			}
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
				ai[k] = i9;
			}
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
				ai[k] = i9;
			}
			k++;
			i = j4;
			j = l4;
			l1 += k2;
			i2 += l2;
			j2 += i3;
			int j6 = j2 >> 14;
			if (j6 != 0) {
				j4 = l1 / j6;
				l4 = i2 / j6;
				if (j4 < 7) {
					j4 = 7;
				} else if (j4 > 16256) {
					j4 = 16256;
				}
			}
			j7 = j4 - i >> 3;
			l7 = l4 - j >> 3;
			j1 += j3;
			i += j1 & 0x600000;
			j8 = j1 >> 23;
		}
		for (int l3 = i1 - l & 7; l3-- > 0;) {
			int j9;
			if ((j9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
				ai[k] = j9;
			}
			k++;
			i += j7;
			j += l7;
		}
	}

	public void reposition(int height, int width) {
		System.out.println("Reposition " + width + ", " + height);
		scanOffsets = new int[height];

		for (int x = 0; x < height; x++) {
			scanOffsets[x] = width * x;
		}

		viewCenter = new Point2D(width / 2, height / 2);
	}

	public void setBrightness(double exponent) {
		//exponent += Math.random() * 0.03 - 0.015;
		int j = 0;

		for (int k = 0; k < 512; k++) {
			double d1 = k / 8 / 64D + 0.0078125D;
			double d2 = (k & 7) / 8D + 0.0625D;

			for (int k1 = 0; k1 < 128; k1++) {
				double initial = k1 / 128D;
				double r = initial;
				double g = initial;
				double b = initial;

				if (d2 != 0.0D) {
					double d7;
					if (initial < 0.5D) {
						d7 = initial * (1.0D + d2);
					} else {
						d7 = initial + d2 - initial * d2;
					}

					double d8 = 2D * initial - d7;
					double d9 = d1 + 0.33333333333333331D;
					if (d9 > 1.0D) {
						d9--;
					}

					double d10 = d1;
					double d11 = d1 - 0.33333333333333331D;
					if (d11 < 0.0D) {
						d11++;
					}

					if (6D * d9 < 1.0D) {
						r = d8 + (d7 - d8) * 6D * d9;
					} else if (2D * d9 < 1.0D) {
						r = d7;
					} else if (3D * d9 < 2D) {
						r = d8 + (d7 - d8) * (0.66666666666666663D - d9) * 6D;
					} else {
						r = d8;
					}

					if (6D * d10 < 1.0D) {
						g = d8 + (d7 - d8) * 6D * d10;
					} else if (2D * d10 < 1.0D) {
						g = d7;
					} else if (3D * d10 < 2D) {
						g = d8 + (d7 - d8) * (0.66666666666666663D - d10) * 6D;
					} else {
						g = d8;
					}

					if (6D * d11 < 1.0D) {
						b = d8 + (d7 - d8) * 6D * d11;
					} else if (2D * d11 < 1.0D) {
						b = d7;
					} else if (3D * d11 < 2D) {
						b = d8 + (d7 - d8) * (0.66666666666666663D - d11) * 6D;
					} else {
						b = d8;
					}
				}
				int newR = (int) (r * 256D);
				int newG = (int) (g * 256D);
				int newB = (int) (b * 256D);
				int colour = (newR << 16) + (newG << 8) + newB;

				colour = ColourUtils.exponent(colour, exponent);
				if (colour == 0) {
					colour = 1;
				}

				colourPalette[j++] = colour;
			}
		}

		/*BufferedImage img = new BufferedImage(1024, 64, BufferedImage.TYPE_INT_RGB);
		System.out.println("palette len " + colourPalette.length);
		for (int idx = 0; idx < colourPalette.length; idx++) {
			Color color = new Color(colourPalette[idx]);
			int xPos = idx % 1024;
			int yPos = idx / 1024;
			img.setRGB(xPos, yPos, color.getRGB());
		}

		  try { ImageIO.write(img, "png", new File("F:/data/palette.png")); } catch (IOException
		  e) {   e.printStackTrace(); }*/
		


	}
	
	public void setTextureBrightness(double exponent) {
		if(TextureLoader.instance != null)
			TextureLoader.instance.setBrightness(exponent);
	}

	public void useViewport() {
		scanOffsets = new int[this.height];
		for (int j = 0; j < this.height; j++) {
			scanOffsets[j] = this.width * j;
		}

		viewCenter = new Point2D(this.width / 2, this.height / 2);
	}

}