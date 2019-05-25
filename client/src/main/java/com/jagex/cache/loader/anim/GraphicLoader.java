package com.jagex.cache.loader.anim;

import com.jagex.cache.anim.Graphic;
import com.jagex.cache.loader.DataLoaderBase;

public abstract class GraphicLoader implements DataLoaderBase<Graphic>{

	public static GraphicLoader instance;
	
	public static Graphic lookup(int id) {
		return instance.forId(id);
	}
	
	

}
