package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;

import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.io.Buffer;


public class MyMapIndexLoader extends MapIndexLoader {

	@Override
	public void init(Archive arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(Buffer arg0) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public int getFileId(int arg0, MapType arg1) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean landscapePresent(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean objectPresent(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public byte[] encode() {
		return null;
	}

	@Override
	public void set(int regionX, int regionY, int landscapeId, int objectsId) {
		
	}


}
