package com.rspsi.util;

import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

public class GlyphConstants {
	public static final Glyph OPEN_FOLDER_GLYPH = FontAwesomeUtil.getFont()
			.create(FontAwesome.Glyph.FOLDER_OPEN)
            .size(12)
            .color(Color.WHITE);
	
	public static final Glyph SAVE_GLYPH = FontAwesomeUtil.getFont()
			 .create(FontAwesome.Glyph.SAVE)
             .size(12)
             .color(Color.WHITE);

}
