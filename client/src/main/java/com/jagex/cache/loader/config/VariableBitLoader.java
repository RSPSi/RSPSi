package com.jagex.cache.loader.config;

import com.jagex.cache.config.VariableBits;
import com.jagex.cache.loader.IndexedLoaderBase;

public abstract class VariableBitLoader implements IndexedLoaderBase<VariableBits> {

	public static VariableBitLoader instance;

	public static VariableBits lookup(int id) {
		return instance.forId(id);
	}
	
	public static int getCount() {
		return instance.count();
	}

}
