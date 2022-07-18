package com.rspsi.plugin.loader;

import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class MyMapIndexLoader extends MapIndexLoader {


	private int[] mapHashes, landscapeIds, objectIds;
	private int[] mapHashesOSRS, landscapeIdsOSRS, objectIdsOSRS;
	
	@Override
	public void init(Archive archive) {
		Buffer buffer = new Buffer(archive.file("map_index"));

		int mapCount = buffer.getPayload().length / 6;
		this.mapHashes = new int[mapCount];
		this.landscapeIds = new int[mapCount];
		this.objectIds = new int[mapCount];

		for(int i = 0;i<mapCount;i++){
			mapHashes[i] = buffer.readUShort();
			landscapeIds[i] = buffer.readUShort();
			objectIds[i] = buffer.readUShort();
		}


		buffer = new Buffer(archive.file("map_index_osrs"));
		int mapCountOSRS = buffer.readUShort();
		this.mapHashesOSRS = new int[mapCountOSRS];
		this.landscapeIdsOSRS = new int[mapCountOSRS];
		this.objectIdsOSRS = new int[mapCountOSRS];

		for(int i = 0;i<mapCountOSRS;i++){
			mapHashesOSRS[i] = buffer.readUShort();
			landscapeIdsOSRS[i] = buffer.readUShort();
			objectIdsOSRS[i] = buffer.readUShort();
		}
	}

	@Override
	public void init(Buffer arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getFileId(int hash, MapType type) {
		if(isOsrsRegion(hash)){
			for(int j1 = 0; j1 < mapHashesOSRS.length; j1++)
				if(mapHashesOSRS[j1] == hash) {
					if(type == MapType.LANDSCAPE) {
						return landscapeIdsOSRS[j1];
					} else {
						return objectIdsOSRS[j1];
					}
				}
		}
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

	private static final Set<Integer> OSRS_REGIONS = new HashSet<>(Arrays.asList(12889, 12115, 9772, 9619, 5022, 5023,
			5279, 5280, 5535, 5536, 9023, 9043, 9261, 10797, 12342, 12611, 12889, 4883));

	public static boolean isOsrsRegion(int regionId) {
		return OSRS_REGIONS.contains(regionId);
	}

	@Override
	public boolean landscapePresent(int id) {
		return isOsrsRegion(id) ? IntStream.range(0, mapHashesOSRS.length)
				.filter(i -> id == landscapeIdsOSRS[i])
				.findFirst()
				.orElse(-1) >= 0
		:
				IntStream.range(0, mapHashes.length)
				.filter(i -> id == landscapeIdsOSRS[i])
				.findFirst()
				.orElse(-1) >= 0;
	}

	@Override
	public boolean objectPresent(int id) {
		return isOsrsRegion(id) ? IntStream.range(0, mapHashesOSRS.length)
				.filter(i -> id == objectIdsOSRS[i])
				.findFirst()
				.orElse(-1) >= 0
				:
				IntStream.range(0, mapHashesOSRS.length)
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
