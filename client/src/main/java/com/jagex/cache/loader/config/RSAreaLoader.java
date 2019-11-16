package com.jagex.cache.loader.config;

import com.jagex.cache.def.RSArea;
import com.jagex.cache.loader.IndexedLoaderBase;

public abstract class RSAreaLoader implements IndexedLoaderBase<RSArea> {

	public static RSAreaLoader instance;

	public static RSArea get(int id) {
		return instance.forId(id);
	}
	
}
