package com.jagex.cache.loader;

import com.jagex.io.Buffer;

import io.nshusa.rsam.binary.Archive;

public interface DataLoaderBase<T> {

	public T forId(int id);
	public int count();
	
	public void init(Archive archive);
	public void init(byte[] data);
}
