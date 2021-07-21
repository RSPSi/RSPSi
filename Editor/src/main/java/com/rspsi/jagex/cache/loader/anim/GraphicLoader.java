package com.rspsi.jagex.cache.loader.anim;

import com.rspsi.jagex.cache.anim.Graphic;
import com.rspsi.jagex.cache.loader.DataLoaderBase;

public abstract class GraphicLoader implements DataLoaderBase<Graphic>{

	public static GraphicLoader instance;
	
	public static Graphic lookup(int id) {
		return instance.forId(id);
	}
	
	

}
