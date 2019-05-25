package com.jagex.cache.loader.anim;

import com.jagex.cache.anim.FrameBase;
import com.jagex.io.Buffer;

public abstract class FrameBaseLoader {
	
	public static FrameBaseLoader instance;
	
	public abstract FrameBase decode(Buffer buffer);

}
