package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;

import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.io.Buffer;


public class MyObjectDefinitionLoader extends ObjectDefinitionLoader {

	
	@Override
	public void init(Archive archive) {
		
	}

	@Override
	public void init(Buffer buffer, Buffer index) {

		
	}

	@Override
	public ObjectDefinition forId(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}


}
