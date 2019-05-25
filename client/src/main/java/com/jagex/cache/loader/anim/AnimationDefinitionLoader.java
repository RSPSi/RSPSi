package com.jagex.cache.loader.anim;

import com.jagex.cache.anim.Animation;
import com.jagex.cache.loader.DataLoaderBase;

public abstract class AnimationDefinitionLoader implements DataLoaderBase<Animation>{
	
	public static AnimationDefinitionLoader instance;
	
	public static Animation getAnimation(int id) {
		return instance.forId(id);
	}

	public static int getCount() {
		return instance.count();
	}
}
