package com.rspsi.jagex.cache.loader.sprite;

import com.rspsi.jagex.cache.graphics.Sprite;

public abstract class MinimapSpriteLoader {
	
	public static MinimapSpriteLoader instance;
	
	public static Sprite getMinimapFunctionSprite(int id) {
		return instance.forId(id, MinimapSpriteType.FUNCTION);
	}
	
	public static Sprite getMinimapSceneSprite(int id) {
		return instance.forId(id, MinimapSpriteType.SCENE);
	}

	public abstract Sprite forId(int id, MinimapSpriteType type);

}
