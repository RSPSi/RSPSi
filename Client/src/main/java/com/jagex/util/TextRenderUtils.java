package com.jagex.util;

import java.awt.Color;
import java.awt.Graphics2D;

public class TextRenderUtils {
	
	public static void renderCenter(Graphics2D g, String text, double x, double y, int colour) {
		int xOffset = g.getFontMetrics().stringWidth(text) / 2;
		int yOffset = g.getFontMetrics().getHeight() / 2;
		String color = colour  + "";
		color = color.replaceFirst("0x", "#");
		Color oldColor = g.getColor();
		g.setColor(Color.decode(color));
		g.drawString(text, (int) (x - xOffset), (int) (y - yOffset));
		g.setColor(oldColor);
	}
	
	public static void renderLeft(Graphics2D g, String text, double x, double y, int colour) {
		int xOffset = g.getFontMetrics().stringWidth(text);
		int yOffset = g.getFontMetrics().getHeight();
		String color = colour  + "";
		color = color.replaceFirst("0x", "#");
		Color oldColor = g.getColor();
		g.setColor(Color.decode(color));
		g.drawString(text, (int) (x - xOffset), (int) (y - yOffset));
		g.setColor(oldColor);
	}

}
