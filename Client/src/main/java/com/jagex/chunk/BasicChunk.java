package com.jagex.chunk;

import com.rspsi.cache.CacheFileType;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.jagex.Client;
import com.jagex.map.MapRegion;
import com.jagex.map.SceneGraph;
import com.jagex.net.ResourceResponse;

public class BasicChunk {
	
	private byte[][][] tileFlags;
	private SceneGraph sceneGraph;
	//private MapRegion mapRegion;
	public int landscapeId = -1;
	private byte[] landscapeBytes;
	private int regionX, regionY;
	
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onResourceResponse(ResourceResponse response) {
		if(response.getRequest().getType() == CacheFileType.MAP) {
			int fileId = response.getRequest().getFile();
			if(fileId == landscapeId) {
				landscapeBytes = response.decompress();
			}
		}
	}

	
	
	public BasicChunk(int x, int y) {
		this.regionX = x;
		this.regionY = y;
	}
	
	public void init() {

		EventBus.getDefault().register(this);
		//sceneGraph = new SceneGraph(this, 64, 64, 4);
		tileFlags = new byte[4][64][64];
		//mapRegion = new MapRegion(sceneGraph, tileFlags, 64, 64);
		
	}
	
	public int[] drawMinimapOriented(int plane) {
		int pixels = 256 * 256;
        int[] raster = new int[pixels];
		for (int i = 0; i < pixels; i++) {
			raster[i] = 0;
		}

		for (int y = 0; y < 64; y++) {
			int i1 = (y) * 4;
			for (int x = 0; x < 64; x++) {
				if ((tileFlags[plane][x][y] & 0x18) == 0) {
					sceneGraph.drawMinimapTile(raster, x, y, plane, i1, 256);
				}

				if (plane < 3 && (tileFlags[plane + 1][x][y] & 8) != 0) {
					sceneGraph.drawMinimapTile(raster, x, y, plane + 1, i1, 256);
				}
				i1 += 4;
			}
		}
		return raster;
	}
	
	public int[] drawMinimapBasic(int plane) {
		int pixels = 256 * 256;
        int[] raster = new int[pixels];
		for (int i = 0; i < pixels; i++) {
			raster[i] = 0;
		}

		for (int y = 0; y < 64; y++) {
			int i1 = (63 - y) * 256 * 4;
			for (int x = 0; x < 64; x++) {
				if ((tileFlags[plane][x][y] & 0x18) == 0) {
					sceneGraph.drawMinimapTile(raster, x, y, plane, i1, 256);
				}

				if (plane < 3 && (tileFlags[plane + 1][x][y] & 8) != 0) {
					sceneGraph.drawMinimapTile(raster, x, y, plane + 1, i1, 256);
				}
				i1 += 4;
			}
		}
		return raster;
	}

	public void loadChunk() {
		try {
			if(landscapeBytes == null)
				return;


			for (int z = 0; z < 4; z++) {
				for (int x = 0; x < 64; x++) {
					for (int y = 0; y < 64; y++) {
						tileFlags[z][x][y] = 0;
					}
				}
			}
			//mapRegion.decodeRegionMapData(landscapeBytes, 0, 0, regionX, regionY);
			//mapRegion.method171(sceneGraph);


		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}
