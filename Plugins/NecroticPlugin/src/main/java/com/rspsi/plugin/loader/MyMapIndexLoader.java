package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;

import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.io.Buffer;

public class MyMapIndexLoader extends MapIndexLoader {


	private int[] mapHashes, landscapeIds, objectIds;
	
	@Override
	public void init(Archive archive) {
		Buffer buffer = new Buffer(archive.file("map_index"));
		
		int mapCount = buffer.readUShort();
		this.mapHashes = new int[mapCount];
		this.landscapeIds = new int[mapCount];
		this.objectIds = new int[mapCount];

		int[] dntUse = new int[] {5181, 5182, 5183, 5184, 5180, 5179, 5175, 5176, 4014, 3997, 5314, 5315, 5172};
		for(int i2 = 0; i2 < mapCount; i2++)
		{
			mapHashes[i2] = buffer.readUShort();
			landscapeIds[i2] = buffer.readUShort();
			objectIds[i2] = buffer.readUShort();
			for(int i : dntUse)
			{
				if(landscapeIds[i2] == i)
					landscapeIds[i2] = -1;
				if(objectIds[i2] == i)
					objectIds[i2] = -1;
			}
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
					//Soulwars
					if(landscapeIds[j1] >= 3700 && landscapeIds[j1] <= 3840) 
						return landscapeIds[j1];
					for(int cheapHax : cheapHaxValues)
						if(landscapeIds[j1] == cheapHax)
							return landscapeIds[j1];
					int id = landscapeIds[j1] > 3535 ? -1 : landscapeIds[j1];
					return id;
				} else {
					if(objectIds[j1] >= 3700 && objectIds[j1] <= 3840) 
						return objectIds[j1];
					for(int cheapHax : cheapHaxValues)
						if(objectIds[j1] == cheapHax)
							return objectIds[j1];
					int id = objectIds[j1] > 3535 ? -1 : objectIds[j1];
					return id;
				}
			}
		return -1;
	}
	
	private int[] cheapHaxValues = new int[]{
			3627,    		3628,    		
			3655,    		3656,    		
			3625,    		3626,    		
			3629,    		3630,
			4071,   		4072,
			5253,  			1816,
			1817,    		3653,
			3654,    		4067,    		
			4068,    		3639,    		
			3640,    		1976,    		
			1977,    		3571,    		
			3572,    		5129,    		
			5130,			2066,   
			2067,    		3545,  
			3546,    		3559,
			3560,    		3569,   
			3570,    		3551,  
			3552,    		3579,
			3580,    		3575,  
			3576,    		1766,   
			1767,    		3547,
			3548,    		3682,			
			3683,    		3696,
			3697,    		3692,
			3693,			4013,    		
			4079,    		4080,
			4082,    		3996,
			4083,    		4084,
			4075,    		4076,
			3664,    		3993,
			3994,    		3995,
			4077,    		4078,
			4073,    		4074,    		
			4011,    		4012,    
			3998,    		3999,   
			4081,
	};



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
