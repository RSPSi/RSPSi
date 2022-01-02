package com.jagex.cache.loader;

import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;

public interface IndexedLoaderBase<T> {

	T forId(int id);
	int count();
	
	void init(Archive archive);
	void init(Buffer data, Buffer indexBuffer);
}
