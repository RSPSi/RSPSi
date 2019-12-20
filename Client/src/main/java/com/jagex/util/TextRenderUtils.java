package com.jagex.util;

import com.jagex.draw.ImageGraphicsBuffer;
import com.jagex.draw.ProducingGraphicsBuffer;

import java.awt.Color;
import java.awt.Graphics2D;

public class TextRenderUtils {
	
	public static int renderCenter(Graphics2D g, String text, double x, double y, int colour) {
		int xOffset = g.getFontMetrics().stringWidth(text) / 2;
		int yOffset = g.getFontMetrics().getHeight() / 2;
		String color = colour  + "";
		color = color.replaceFirst("0x", "#");
		Color oldColor = g.getColor();
		g.setColor(Color.decode(color));
		g.drawString(text, (int) (x - xOffset), (int) (y - yOffset));
		g.setColor(oldColor);
		return yOffset;
	}
	
	public static int renderLeft(ImageGraphicsBuffer graphicsBuffer, String text, double x, double y, int colour) {
		if(text == null || text.isEmpty())
			return 0;
		Graphics2D g = graphicsBuffer.getGraphics();
		int xOffset = g.getFontMetrics().stringWidth(text);
		int yOffset = g.getFontMetrics().getHeight();

		String color = colour  + "";
		color = color.replaceFirst("0x", "#");
		Color oldColor = g.getColor();
		g.setColor(Color.decode(color));
		if(xOffset > graphicsBuffer.getWidth()){
			while(!text.trim().isEmpty()){
				String s = "";

				for(String part : text.split(" ")) {
					String mixed = s + part;
					int width = g.getFontMetrics().stringWidth(mixed);
					if(width >= graphicsBuffer.getWidth()){
						break;
					}
					s = mixed;

				}
				g.drawString(text, (int) (x - xOffset), (int) (y - yOffset));
				yOffset += g.getFontMetrics().getHeight();
				text = text.replace(s, "").trim();
			}

		} else {
			g.drawString(text, (int) (x - xOffset), (int) (y - yOffset));
		}

		g.setColor(oldColor);
		return yOffset;
	}

}
