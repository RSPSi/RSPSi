package com.rspsi.jagex.draw.textures;

import com.rspsi.jagex.cache.graphics.Sprite;

import java.util.Arrays;

public class SpriteTexture extends Texture {
	private boolean supportsAlpha;

	public SpriteTexture(Sprite sprite) {
		super(sprite.getWidth(), sprite.getHeight());
		this.originalPixels = Arrays.copyOf(sprite.getRaster(), sprite.getRaster().length);
		this.pixels = Arrays.copyOf(sprite.getRaster(), sprite.getRaster().length);
		supportsAlpha = sprite.hasAlpha();
		this.generatePalette();
	}

	@Override
	public boolean supportsAlpha() {
		return supportsAlpha;
	}

}
