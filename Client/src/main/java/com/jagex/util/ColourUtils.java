package com.jagex.util;

import javafx.scene.paint.Color;

public class ColourUtils {

	public static int getAlpha(int rgb) {
		return rgb >> 24 & 0xFF;
	}

	public static int getARGB(int rgb) {
		return getRGB(getRed(rgb), getGreen(rgb), getBlue(rgb));
	}

	public static int getBlue(int rgb) {
		return rgb & 0xFF;
	}

	public static Color getColor(int rgb) {
		//System.out.println(getRed(rgb) + ":" + getGreen(rgb) + ":" + getBlue(rgb));
		return Color.color(getRed(rgb) / 255.0, getGreen(rgb) / 255.0, getBlue(rgb) / 255.0);
	}

	public static Color getColorWithAlpha(int argb) {
		return Color.color(getRed(argb) / 255.0, getGreen(argb) / 255.0, getBlue(argb) / 255.0, getAlpha(argb) / 255.0);
	}

	public static int getGreen(int rgb) {
		return rgb >> 8 & 0xFF;
	}

	public static int getRed(int rgb) {
		return rgb >> 16 & 0xFF;
	}

	public static int getRGB(int argb) {
		return (getRed(argb) & 0xFF) << 16 | (getGreen(argb) & 0xFF) << 8 | (getBlue(argb) & 0xFF) << 0;
	}

	public static int getRGB(int red, int green, int blue) {
		return getRGB(0xFF, red, green, blue);
	}

	public static int getRGB(int alpha, int red, int green, int blue) {
		return alpha << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF) << 0;
	}

	public static int stripAlpha(int rgb) {
		return rgb & 0xFFFFFF;
	}
	public static int addAlpha(int rgb, int alpha) {
		return getRGB(alpha, getRed(rgb), getGreen(rgb), getBlue(rgb));
	}
	

	/**
	 * Encodes the hue, saturation, and luminance into a hsl value.
	 *
	 * @param hue
	 *            The hue.
	 * @param saturation
	 *            The saturation.
	 * @param luminance
	 *            The luminance.
	 * @return The colour.
	 */
	public final static int toHsl(int hue, int saturation, int luminance) {
		if (luminance > 179) {
			saturation /= 2;
		}
		if (luminance > 192) {
			saturation /= 2;
		}
		if (luminance > 217) {
			saturation /= 2;
		}
		if (luminance > 243) {
			saturation /= 2;
		}

		return (hue / 4 << 10) + (saturation / 32 << 7) + luminance / 2;
	}
	
	public final static int checkedLight(int colour, int light) {
		if (colour == -2)
			return 0xbc614e;

		if (colour == -1) {
			if (light < 0) {
				light = 0;
			} else if (light > 127) {
				light = 127;
			}
			return 127 - light;
		}

		light = light * (colour & 0x7f) / 128;
		if (light < 2) {
			light = 2;
		} else if (light > 126) {
			light = 126;
		}
		return (colour & 0xff80) + light;
	}
	
	public static int exponent(int colour, double exponent) {
		int alpha = colour >> 24 & 0xff;
		double r = (colour >> 16 & 0xff) / 256D;
		double g = (colour >> 8 & 0xff) / 256D;
		double b = (colour & 0xff) / 256D;

		r = Math.pow(r, exponent);
		g = Math.pow(g, exponent);
		b = Math.pow(b, exponent);

		int newR = (int) (r * 256D);
		int newG = (int) (g * 256D);
		int newB = (int) (b * 256D);
		return alpha << 24 | (newR << 16) | (newG << 8) | newB;
	}

	public static int[] getARGB(int[] pixels) {
		for(int i = 0;i<pixels.length;i++) {
			pixels[i] = addAlpha(pixels[i], 255);
		}
		return pixels;
	}

}
