package com.jagex.util;

import java.awt.Graphics;

public class GraphicUtils {

	public static void drawStringCentered(Graphics g, int y, int width, String text) {
		g.drawString(text, width / 2 - g.getFontMetrics().stringWidth(text), y);
	}

}
