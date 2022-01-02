package com.jagex.cache.loader;

import com.displee.cache.index.archive.Archive;

public interface DataLoaderBase<T> {

	T forId(int id);
	int count();
	
	void init(Archive archive);
	void init(byte[] data);
}
