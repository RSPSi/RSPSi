package com.rspsi.plugin.loader;

import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;

import java.util.Map;

import com.google.common.collect.Maps;
import com.rspsi.jagex.cache.anim.FrameBase;
import com.rspsi.jagex.cache.loader.anim.FrameBaseLoader;
import com.rspsi.jagex.io.Buffer;

public class FrameBaseLoaderOSRS extends FrameBaseLoader {
	
	private Map<Integer, FrameBase> skeletons = Maps.newConcurrentMap();
	
	@Override
	public FrameBase get(int id) {
		return skeletons.get(id);
	}

	@Override
	public FrameBase decode(Buffer buffer) {
		FrameBase base = new FrameBase();
		int count = buffer.readUShort();
		int[] transformationType = new int[count];
		int[][] vertexGroups = new int[count][];
		for (int index = 0; index < count; index++) {
			transformationType[index] = buffer.readUShort();
		}

		for (int label = 0; label < count; label++) {
			vertexGroups[label] = new int[buffer.readUShort()];
		}

		for (int label = 0; label < count; label++) {
			for (int index = 0; index < vertexGroups[label].length; index++) {
				vertexGroups[label][index] = buffer.readUShort();
			}
		}
		base.setCount(count);
		base.setTransformationType(transformationType);
		base.setVertexGroups(vertexGroups);
		return base;
	}

	public void init(Index skeletonIndex) {
		for(Archive archive : skeletonIndex.archives()) {
			if(archive != null && archive.containsData()) {
				FrameBase base = decode(new Buffer(archive.file(0).getData()));
				skeletons.put(archive.getId(), base);
			}
		}
	}

}
