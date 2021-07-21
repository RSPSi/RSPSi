package com.rspsi.jagex.util;

import com.rspsi.jagex.chunk.Chunk;

import java.nio.ByteBuffer;

public class MapFormat {
	
	public byte[] getCombinedBytes(Chunk chunk) {
		byte[] landscape = getLandscapeBytes(chunk);
		byte[] object = getObjectBytes(chunk);
		
		ByteBuffer buffer = ByteBuffer.allocate(landscape.length + object.length + 8);
		buffer.putInt(landscape.length);
		buffer.put(landscape);
		buffer.putInt(object.length);
		buffer.put(object);
		return buffer.array();
	}
	
	public byte[] getLandscapeBytes(Chunk chunk) {

		return null;
	}
	
	public byte[] getObjectBytes(Chunk chunk) {
		
		return null;
	}

}
