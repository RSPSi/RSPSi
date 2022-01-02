package com.rspsi.plugin.loader;

import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;

public class MyMapIndexLoader extends MapIndexLoader {


	private int[] mapHashes, landscapeIds, objectIds;

	@Override
	public void init(Archive archive) {
		Buffer buffer = new Buffer(archive.file("map_index"));

		int mapCount = buffer.readUShort();
		this.mapHashes = new int[mapCount];
		this.landscapeIds = new int[mapCount];
		this.objectIds = new int[mapCount];

		for(int i2 = 0; i2 < mapCount; i2++)
		{
			mapHashes[i2] = buffer.readUShort();
			landscapeIds[i2] = buffer.readUShort();
			objectIds[i2] = buffer.readUShort();
	

		}


	}

	@Override
	public void init(Buffer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getFileId(int hash, MapType type) {
		for(int j1 = 0; j1 < mapHashes.length; j1++)
			if(mapHashes[j1] == hash) {
				if(type == MapType.LANDSCAPE) {
					return landscapeIds[j1];
				} else {
					return objectIds[j1];

				}
			}
		return -1;
	}



	@Override
	public boolean landscapePresent(int id) {
		return IntStream.range(0, mapHashes.length)
				.filter(i -> id == landscapeIds[i])
				.findFirst()
				.orElse(-1) >= 0;
	}

	@Override
	public boolean objectPresent(int id) {
		return IntStream.range(0, mapHashes.length)
				.filter(i -> id == objectIds[i])
				.findFirst()
				.orElse(-1) >= 0;
	}

	@Override
	public byte[] encode() {
		ByteBuffer buffer = ByteBuffer.allocate((mapHashes.length * 6) + 2);
		buffer.putShort((short) mapHashes.length);
		for(int index = 0;index<mapHashes.length;index++) {
			buffer.putShort((short) mapHashes[index]);
			buffer.putShort((short) landscapeIds[index]);
			buffer.putShort((short) objectIds[index]);
		}
		return buffer.array();
	}

	@Override
	public void set(int regionX, int regionY, int landscapeId, int objectsId) {
		int hash = (regionX << 8) + regionY;
		int index = IntStream.range(0, mapHashes.length)
				.filter(i -> hash == mapHashes[i])
				.findFirst()
				.orElse(-1);
		if(index >= 0) {
			System.out.println("Setting index " + index);
			landscapeIds[index] = landscapeId;
			objectIds[index] = objectsId;
		} else {
			System.out.println("Adding new index");
			int[] mapHashes = Arrays.copyOf(this.mapHashes, this.landscapeIds.length + 1);
			int[] landscapes = Arrays.copyOf(this.landscapeIds, this.landscapeIds.length + 1);
			int[] objects = Arrays.copyOf(this.objectIds, this.landscapeIds.length + 1);
			index = mapHashes.length - 1;
			mapHashes[index] = hash;
			landscapes[index] = landscapeId;
			objects[index] = objectsId;
		
			this.mapHashes = mapHashes;
			this.landscapeIds = landscapes;
			this.objectIds = objects;
		}
		
	}



}
