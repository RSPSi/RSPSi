package com.jagex.util;

import java.nio.ByteBuffer;

import com.jagex.chunk.Chunk;

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
