package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.IntStream;

import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.io.Buffer;
import com.rspsi.options.Config;

public class MyMapIndexLoader extends MapIndexLoader {


	private int[] mapHashes, landscapes, objects;

	@Override
	public void init(Archive archive) {
		try {
			Buffer buffer = new Buffer(Files.readAllBytes(new File(Config.cacheLocation.get() + "map_index.dat").toPath()));
	
			int mapCount = buffer.readUShort();
			this.mapHashes = new int[mapCount];
			this.landscapes = new int[mapCount];
			this.objects = new int[mapCount];
	
			for(int i2 = 0; i2 < mapCount; i2++)
			{
				mapHashes[i2] = buffer.readUShort();
				landscapes[i2] = buffer.readUShort();
				objects[i2] = buffer.readUShort();
		
	
			}

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void init(Buffer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getFileId(int hash, MapType type) {
		int index = IntStream.range(0, mapHashes.length)
				.filter(i -> hash == mapHashes[i])
				.findFirst()
				.orElse(-1);
		if(index >= 0) {
			return type == MapType.LANDSCAPE ? landscapes[index] : objects[index];
		}
		
		return -1;
	}

	@Override
	public boolean landscapePresent(int id) {
		return IntStream.range(0, mapHashes.length)
				.filter(i -> id == landscapes[i])
				.findFirst()
				.orElse(-1) >= 0;
	}

	@Override
	public boolean objectPresent(int id) {
		return IntStream.range(0, mapHashes.length)
				.filter(i -> id == objects[i])
				.findFirst()
				.orElse(-1) >= 0;
	}

	@Override
	public byte[] encode() {
		ByteBuffer buffer = ByteBuffer.allocate((mapHashes.length * 6) + 2);
		buffer.putShort((short) mapHashes.length);
		for(int index = 0;index<mapHashes.length;index++) {
			buffer.putShort((short) mapHashes[index]);
			buffer.putShort((short) landscapes[index]);
			buffer.putShort((short) objects[index]);
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
			landscapes[index] = landscapeId;
			objects[index] = objectsId;
		} else {
			System.out.println("Adding new index");
			int[] mapHashes = Arrays.copyOf(this.mapHashes, this.landscapes.length + 1);
			int[] landscapes = Arrays.copyOf(this.landscapes, this.landscapes.length + 1);
			int[] objects = Arrays.copyOf(this.objects, this.landscapes.length + 1);
			index = mapHashes.length - 1;
			mapHashes[index] = hash;
			landscapes[index] = landscapeId;
			objects[index] = objectsId;
		
			this.mapHashes = mapHashes;
			this.landscapes = landscapes;
			this.objects = objects;
		}
		
	}


}
