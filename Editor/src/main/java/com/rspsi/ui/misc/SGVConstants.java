package com.rspsi.ui.misc;

import javafx.scene.paint.Color;

public class SGVConstants {

	static {

		SVGGlyph minus = new SVGGlyph(0, "MINUS",
				"M804.571 420.571v109.714q0 22.857-16 38.857t-38.857 16h-694.857q-22.857 0-38.857-16t-16-38.857v-109.714q0-22.857 16-38.857t38.857-16h694.857q22.857 0 38.857 16t16 38.857z",
				Color.WHITE);
		minus.setSize(7, 2);
		minus.setTranslateY(5);
		MINIMIZE = minus;

		SVGGlyph resizeMax = new SVGGlyph(0, "RESIZE_MAX",
				"M726 810v-596h-428v596h428zM726 44q34 0 59 25t25 59v768q0 34-25 60t-59 26h-428q-34 0-59-26t-25-60v-768q0-34 25-60t59-26z",
				Color.WHITE);
		resizeMax.setSize(7, 7);
		resizeMax.setTranslateY(2);
		MAXIMIZE = resizeMax;
		SVGGlyph resizeMin = new SVGGlyph(0, "RESIZE_MIN",
				"M80.842 943.158v-377.264h565.894v377.264h-565.894zM0 404.21v619.79h727.578v-619.79h-727.578zM377.264 161.684h565.894v377.264h-134.736v80.842h215.578v-619.79h-727.578v323.37h80.842v-161.686z",
				Color.WHITE);
		resizeMin.setSize(8, 8);
		resizeMin.setTranslateY(2);
		RESIZE_MIN = resizeMin;
		SVGGlyph close = new SVGGlyph(0, "CLOSE",
				"M810 274l-238 238 238 238-60 60-238-238-238 238-60-60 238-238-238-238 60-60 238 238 238-238z",
				Color.WHITE);
		close.setSize(8, 8);
		close.setTranslateY(1);
		CLOSE_ICON = close;
	}

	private static final SVGGlyph CLOSE_ICON, MINIMIZE, MAXIMIZE, RESIZE_MIN;

	public static SVGGlyph getCloseIcon() {
		return CLOSE_ICON.copy();
	}

	public static SVGGlyph getMaximize() {
		return MAXIMIZE.copy();
	}

	public static SVGGlyph getMinimize() {
		return MINIMIZE.copy();
	}

	public static SVGGlyph getResizeMin() {
		return RESIZE_MIN.copy();
	}

}
