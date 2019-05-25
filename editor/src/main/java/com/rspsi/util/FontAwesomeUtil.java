package com.rspsi.util;

import java.io.InputStream;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

public class FontAwesomeUtil {
	
	private static GlyphFont font;
	
	private static void init() {
		InputStream inputStream = FontAwesomeUtil.class.getResourceAsStream("/font/fontawesome-webfont.ttf");
		GlyphFontRegistry.register(new FontAwesome(inputStream));
	}

	public static GlyphFont getFont() {
		if(font == null) {
			init();
		}
		return GlyphFontRegistry.font("FontAwesome");
	}

}
