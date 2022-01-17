package com.jagex.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.jagex.chunk.Chunk;
import com.rspsi.core.misc.Vector2;

public class MultiMapEncoder {
	
	public static byte[] encode(List<Chunk> chunks) {
		ByteBuffer buffer = ByteBuffer.allocate(5000000);//5mb
		buffer.putInt(chunks.size());
		for(Chunk chunk : chunks) {
			if(chunk.hasLoaded()) {
				byte[] objectMap = chunk.scenegraph.saveObjects(chunk);
				byte[] tileMap = chunk.mapRegion.save_terrain_block(chunk);
				
				
				buffer.putInt(chunk.objectMapId);
				buffer.putInt(chunk.tileMapId);
				
				buffer.putInt(chunk.offsetX / 64);
				buffer.putInt(chunk.offsetY / 64);
				
				buffer.putInt(objectMap.length);
				buffer.put(objectMap);
				
				buffer.putInt(tileMap.length);
				buffer.put(tileMap);
				
			}
		}
		
		return Arrays.copyOf(buffer.array(), buffer.position());
	}
	
	public static byte[] encodeShallow(List<Chunk> chunks) {
		ByteBuffer buffer = ByteBuffer.allocate(5_000_000);//5mb
		buffer.putInt(chunks.size());
		for(Chunk chunk : chunks) {
			if(chunk.hasLoaded()) {
				byte[] objectMap = chunk.objectMapData;
				byte[] tileMap = chunk.tileMapData;
				
				
				buffer.putInt(chunk.objectMapId);
				buffer.putInt(chunk.tileMapId);
				
				buffer.putInt(chunk.offsetX / 64);
				buffer.putInt(chunk.offsetY / 64);
				
				buffer.putInt(objectMap.length);
				buffer.put(objectMap);
				
				buffer.putInt(tileMap.length);
				buffer.put(tileMap);
				
			}
		}
		
		return Arrays.copyOf(buffer.array(), buffer.position());
	}

	public static Vector2 getSize(byte[] encoded) {
		ByteBuffer buffer = ByteBuffer.wrap(encoded);
		int size = buffer.getInt();

		int maximumX = 0;
		int maximumY = 0;
		for(int i = 0;i<size;i++) {
			buffer.getInt();
			buffer.getInt();

			int positionX = buffer.getInt();
			int positionY = buffer.getInt();

			if(positionX > maximumX)
				maximumX = positionX;
			if(positionY > maximumY)
				maximumY = positionY;

			buffer.get(new byte[buffer.getInt()]);
			buffer.get(new byte[buffer.getInt()]);

		}

		return new Vector2(maximumX, maximumY);
	}
	public static List<Chunk> decode(byte[] encoded){
		List<Chunk> chunks = Lists.newArrayList();
		ByteBuffer buffer = ByteBuffer.wrap(encoded);
		int size = buffer.getInt();
		
		for(int i = 0;i<size;i++) {
			int objectMapId = buffer.getInt();
			int tileMapId = buffer.getInt();
			
			int positionX = buffer.getInt();
			int positionY = buffer.getInt();
			
			int cX = (0 + 64 * positionX) / 64;
			int cY = (0 + 64 * positionY) / 64;

			int hash = (cX << 8) + cY;
			
			int objLen = buffer.getInt();
			byte[] objData = new byte[objLen];
			buffer.get(objData);
			
			int landscapeLen = buffer.getInt();
			byte[] landscapeData = new byte[landscapeLen];
			buffer.get(landscapeData);
			
			Chunk chunk = new Chunk(hash);

			chunk.offsetX = 64 * positionX;
			chunk.offsetY = 64 * positionY;
			
			chunk.objectMapData = objData;
			chunk.tileMapData = landscapeData;
			chunk.objectMapId = objectMapId;
			chunk.tileMapId = tileMapId;
			
			System.out.println("Loaded chunk " + chunk.offsetX + " : " + chunk.offsetY + " IDS: " + chunk.objectMapId + " : " + chunk.tileMapId);
			chunks.add(chunk);
		}
		return chunks;
	}

}
