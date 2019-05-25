package com.jagex.cache.loader;

import com.jagex.io.Buffer;

import io.nshusa.rsam.binary.Archive;

public interface IndexedLoaderBase<T> {

	public T forId(int id);
	public int count();
	
	public void init(Archive archive);
	public void init(Buffer data, Buffer indexBuffer);
}
