package com.rspsi.game.save;

import com.jagex.Cache;
import com.jagex.Client;
import com.jagex.chunk.Chunk;
import com.jagex.util.GZIPUtils;

public class SaveAction {
	
	private Chunk chunk;

	
	public SaveAction(Chunk chunk) {
		this.chunk = chunk;
	}
	
	public void saveToCache(Cache cache) {
		try {
/*	TODO		byte[] landscape = GZIPUtils.gzipBytes(chunk.mapRegion.save_terrain_block());
			byte[] objects = GZIPUtils.gzipBytes(Client.getSingleton().sceneGraph.saveObjects());
			cache.putFile(3, chunk.tileMapId, landscape);
			cache.putFile(3, chunk.objectMapId, objects);*/
		} catch(Exception ex) {
			
		}
	}

}
