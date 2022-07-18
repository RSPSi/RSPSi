package com.jagex.cache.loader.map;

import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;

public abstract class MapIndexLoader {
	
	public static MapIndexLoader instance;
	
	public abstract void init(Archive archive);
	public abstract void init(Buffer buffer);
	public abstract int getFileId(int hash, MapType type);
	public abstract boolean landscapePresent(int id);
	public abstract boolean objectPresent(int id);
	public abstract void set(int regionX, int regionY, int landscapeId, int objectsId);
	public abstract byte[] encode();
	
	public static int lookup(int hash, MapType type) {
		return instance.getFileId(hash, type);
	}
	
	public static int resolve(int regionX, int regionY, MapType type) {
		int code = (regionX << 8) + regionY;
		return MapIndexLoader.lookup(code, type);
	}
	
	public static int getLandscapeId(int regionX, int regionY) {
		return resolve(regionX, regionY, MapType.LANDSCAPE);
	}
	
	public static int getObjectId(int regionX, int regionY) {
		return resolve(regionX, regionY, MapType.OBJECT);
	}
	
	public static void setRegionData(int regionX, int regionY, int landscapeId, int objectsId) {
		instance.set(regionX, regionY, landscapeId, objectsId);
	}
	
	public String getFileName(int hash, MapType type) {
		return String.valueOf(lookup(hash, type));
	}
	public static String getName(int regionX, int regionY, MapType type) {
		int code = (regionX << 8) + regionY;
		return MapIndexLoader.instance.getFileName(code, type);
	}



}
