package com.rspsi.plugin.loader;

import com.rspsi.jagex.cache.anim.FrameBase;
import com.rspsi.jagex.cache.loader.anim.FrameBaseLoader;
import com.rspsi.jagex.io.Buffer;

public class MyFrameBaseLoader extends FrameBaseLoader {

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

}