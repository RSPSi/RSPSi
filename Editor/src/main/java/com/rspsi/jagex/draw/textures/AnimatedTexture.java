package com.rspsi.jagex.draw.textures;

import lombok.Data;

@Data
public class AnimatedTexture extends Texture {

	private float animationSpeed, u, v;
	private int animationDirection;

	public AnimatedTexture(int width, int height) {
		super(width, height);
	}

	@Override
	public boolean supportsAlpha() {
		return true;
	}
}
