package com.jagex;

import lombok.Setter;
import org.displee.CacheLibrary;
import org.displee.cache.index.Index;
import org.displee.cache.index.archive.Archive;
import org.displee.cache.index.archive.file.File;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.jagex.cache.graphics.Sprite;
import com.jagex.net.ResourceProvider;
import com.rspsi.cache.CacheFileType;
import com.rspsi.misc.FixedIntegerKeyMap;
import com.rspsi.misc.XTEAManager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.displee.utilities.Miscellaneous;

@Slf4j
public class Cache {


	@Setter
	/**
	 * Set this to override how all cache files except maps are loaded
	 * args = [fileType, fileId]
	 * return byte[] or Optional.empty() to continue with normal loading
	 */
	private BiFunction<CacheFileType, Integer, Optional<byte[]>> fileRetrieverOverride;

	@Setter
	/**
	 * Set this to override how the map files are loaded
	 * args = [fileId, regionId]
	 * return byte[] or Optional.empty() to continue with normal loading
	 */
	private BiFunction<Integer, Integer, Optional<byte[]>> mapRetrieverOverride;
	

	@Getter
	private CacheLibrary indexedFileSystem;

	private Index modelArchive, mapArchive, configArchive, skeletonArchive, skinArchive, spriteIndex, textureIndex, spotAnimIndex, varbitIndex, locIndex;

	public Cache(Path path) throws IOException {
			log.info("Loading cache at {}", path);
			indexedFileSystem = new CacheLibrary(path);
			if(indexedFileSystem.is317()){
				modelArchive = indexedFileSystem.getIndex(1);
				mapArchive = indexedFileSystem.getIndex(4);
				configArchive = indexedFileSystem.getIndex(0);
				skinArchive= indexedFileSystem.getIndex(2);
				skeletonArchive = null;//317 loads inside skins
				log.info("Loaded cache in 317 format!");
			} else if(indexedFileSystem.isOSRS()){
				modelArchive = indexedFileSystem.getIndex(7);
				mapArchive = indexedFileSystem.getIndex(5);
				configArchive = indexedFileSystem.getIndex(2);
				skeletonArchive = indexedFileSystem.getIndex(0);
				skinArchive = indexedFileSystem.getIndex(1);
				spriteIndex = indexedFileSystem.getIndex(8);
				textureIndex = indexedFileSystem.getIndex(9);
				log.info("Loaded cache in OSRS format!");
			} else if(indexedFileSystem.isRS2()){
				modelArchive = indexedFileSystem.getIndex(7);
				mapArchive = indexedFileSystem.getIndex(5);
				configArchive = indexedFileSystem.getIndex(2);
				skeletonArchive = indexedFileSystem.getIndex(0);
				skinArchive = indexedFileSystem.getIndex(1);
				spriteIndex = indexedFileSystem.getIndex(8);
				textureIndex = indexedFileSystem.getIndex(9);
				spotAnimIndex = indexedFileSystem.getIndex(21);
				varbitIndex = indexedFileSystem.getIndex(22);
				locIndex = indexedFileSystem.getIndex(16);
				log.info("Loaded cache in RS3 format!");
			} else if(indexedFileSystem.isRS3()){
				throw new UnsupportedOperationException("RS3 Cache not supported!");
			}
			resourceProvider = new ResourceProvider(this);
			Thread t = new Thread(resourceProvider);
			t.start();
	}
	
	public ResourceProvider resourceProvider;
	

	private FixedIntegerKeyMap<Sprite> spriteCache = new FixedIntegerKeyMap<Sprite>(100);
	
	
	public Sprite getSprite(int id) {
		if(spriteCache.contains(id))
			return spriteCache.get(id);
		if(!indexedFileSystem.isOSRS())
			throw new RuntimeException("Cannot grab sprite by ID on 317!");
		Sprite sprite = Sprite.decode(ByteBuffer.wrap(spriteIndex.getArchive(id).readFile(0)));
		spriteCache.put(id, sprite);
		System.out.println("GETSPRITE " + id);
		return sprite;
	}
	public final Index readFile(CacheFileType index){
		try {
			switch(index){
				case CONFIG:
					return configArchive;
				case MODEL:
					return modelArchive;
				case ANIMATION:
					return skinArchive;
				case SKELETON:
					return skeletonArchive;
				case SOUND:
					break;
				case MAP:
					return mapArchive;
				case SPRITE:
					return spriteIndex;
				case TEXTURE:
					return textureIndex;
				case SPOT:
					return spotAnimIndex;
				case VARBIT:
					return varbitIndex;
				case LOC:
					return locIndex;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public final byte[] readMap(int fileId, int regionId) {
		if(mapRetrieverOverride != null){
			Optional<byte[]> data = mapRetrieverOverride.apply(fileId, regionId);
			if(data.isPresent())
				return data.get();
		}
		if(indexedFileSystem.is317())
			return mapArchive.getArchive(fileId).readFile(0);
		return mapArchive.getArchive(fileId, XTEAManager.lookupMap(regionId)).readFile(0);
	}
	
	public final byte[] readFile(CacheFileType type, int file){
		try {
			if(fileRetrieverOverride != null){
				Optional<byte[]> data = fileRetrieverOverride.apply(type, file);
				if(data.isPresent())
					return data.get();
			}
			switch(type){
				case CONFIG:
					return configArchive.getArchive(file).readFile(0);
				case MODEL:
					return modelArchive.getArchive(file).readFile(0);
				case ANIMATION:
					return skinArchive.getArchive(file).readFile(0);
				case SKELETON:
					return skeletonArchive.getArchive(file).readFile(0);
				case SOUND:
					break;
				case MAP:
					return mapArchive.getArchive(file).readFile(0);
				case TEXTURE:
					break;
				case SPOT:
					return spotAnimIndex.getArchive(file >>> 8).readFile(file & 0xff);
				case VARBIT:
					return varbitIndex.getArchive(file >>> 1416501898).readFile(file & 0x3ffff);
				case LOC:
					return locIndex.getArchive(Miscellaneous.getConfigArchive(file, 8)).readFile(Miscellaneous.getConfigFile(file, 8));
			}
		} catch(Exception ex) {
			//ex.printStackTrace();
		}
		return null;
	}
	
	public final File writeFile(CacheFileType index, String name, int file, byte[] data, int[] xteas){
		try {
			switch(index){
				case CONFIG:
					configArchive.createIfNotExist(file);
					return configArchive.getArchive(file).addFileKeepName(0, data);
				case MODEL:
					modelArchive.createIfNotExist(file);
					return modelArchive.getArchive(file).addFileKeepName(0, data);
				case ANIMATION:
					skinArchive.createIfNotExist(file);
					return skinArchive.getArchive(file).addFileKeepName(0, data);
				case SOUND:
					break;
				case MAP:
					mapArchive.createIfNotExist(file);
					return mapArchive.getArchive(file).addFileKeepName(0, data);
				case TEXTURE:
					break;
				case SPOT:
					return spotAnimIndex.addArchive(file >>> 8).addFile(file & 0xff, data);
				case VARBIT:
					return varbitIndex.addArchive(file >>> 1416501898).addFile(file & 0x3ffff, data);
				case LOC:
					return locIndex.addArchive(Miscellaneous.getConfigArchive(file, 8)).addFile(Miscellaneous.getConfigFile(file, 8), data);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public final Archive createArchive(int file, String name) {
        return configArchive.getArchive(file);
	}
	
	public void close() throws IOException {
		indexedFileSystem.close();
	}

	public ResourceProvider getProvider() {
		return resourceProvider;
	}
	

}
