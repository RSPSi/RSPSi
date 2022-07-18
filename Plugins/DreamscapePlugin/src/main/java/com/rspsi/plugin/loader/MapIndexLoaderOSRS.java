package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.displee.cache.index.archive.file.File;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.io.Buffer;

public class MapIndexLoaderOSRS extends MapIndexLoader {
	

	private int[] mapHashes, objects, landscapes, members;

	@Override
	public void init(Archive archive) {
		File indices = archive.file("map_index");
		Buffer buffer = new Buffer(indices);
		int count = indices.getData().length / 7;
		mapHashes = new int[count];
		landscapes = new int[count];
		objects = new int[count];
		members = new int[count];
		int pos = 0;
		for (int region = 0; region < count; region++) {
			mapHashes[region] = buffer.readUShort();
			landscapes[region] = buffer.readUShort();
			objects[region] = buffer.readUShort();
			members[region] = buffer.readUByte();
			pos++;
		}
		System.out.println("expected regions " + count + " - actual " + pos);
	}

	@Override
	public void init(Buffer buffer) {
		int count = buffer.readUShort();
		mapHashes = new int[count];
		landscapes = new int[count];
		objects = new int[count];
		int pos = 0;
		for (int region = 0; region < count; region++) {
			mapHashes[region] = buffer.readUShort();
			landscapes[region] = buffer.readUShort();
			objects[region] = buffer.readUShort();
		}
		System.out.println("expected regions " + count + " - actual " + pos);
	}

	@Override
	public int getFileId(int hash, MapType type) {
		int index = Arrays.binarySearch(mapHashes, hash);
		if(index >= 0) {
			return type == MapType.LANDSCAPE ? landscapes[index] : objects[index];
		}
		
		return -1;
	}

	@Override
	public boolean landscapePresent(int id) {
		return Arrays.binarySearch(landscapes, id) >= 0;
	}

	@Override
	public boolean objectPresent(int id) {
		return Arrays.binarySearch(objects, id) >= 0;
	}
	
	@Override
	public byte[] encode() {
		ByteBuffer buffer = ByteBuffer.allocate((mapHashes.length * 6) + 2);
		
		for(int index = 0;index<mapHashes.length;index++) {
			buffer.putShort((short) mapHashes[index]);
			buffer.putShort((short) landscapes[index]);
			buffer.putShort((short) objects[index]);
			buffer.put((byte) members[index]);
		}
		
		return buffer.array();
	}

	@Override
	public void set(int regionX, int regionY, int landscapeId, int objectsId) {
		int hash = (regionX << 8) + regionY;
		int index = Arrays.binarySearch(mapHashes, hash);
		
		if(index >= 0) {
			landscapes[index] = landscapeId;
			objects[index] = objectsId;
		}
		
	}


}
