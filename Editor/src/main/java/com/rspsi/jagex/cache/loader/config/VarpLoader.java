package com.rspsi.jagex.cache.loader.config;

import com.rspsi.jagex.cache.config.VariableParameter;
import com.rspsi.jagex.cache.loader.IndexedLoaderBase;

public abstract class VarpLoader implements IndexedLoaderBase<VariableParameter> {
	
	public static VarpLoader instance;

	public static VariableParameter lookup(int id) {
		return instance.forId(id);
	}
	
	public static int getCount() {
		return instance.count();
	}


}
