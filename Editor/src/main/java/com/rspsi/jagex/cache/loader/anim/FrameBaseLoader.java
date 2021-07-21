package com.rspsi.jagex.cache.loader.anim;

import com.rspsi.jagex.cache.anim.FrameBase;
import com.rspsi.jagex.io.Buffer;

public abstract class FrameBaseLoader {
	
	
	public static FrameBaseLoader instance;
	
	public FrameBase get(int id) {
		return null;
	}
	public abstract FrameBase decode(Buffer buffer);

}
