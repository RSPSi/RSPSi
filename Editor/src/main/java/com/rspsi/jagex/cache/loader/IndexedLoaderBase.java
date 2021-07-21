package com.rspsi.jagex.cache.loader;

import com.rspsi.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;

public interface IndexedLoaderBase<T> {

	T forId(int id);
	int count();
	
	void init(Archive archive);
	void init(Buffer data, Buffer indexBuffer);
}
