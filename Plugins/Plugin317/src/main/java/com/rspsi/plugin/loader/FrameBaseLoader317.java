package com.rspsi.plugin.loader;

import com.jagex.cache.anim.FrameBase;
import com.jagex.cache.loader.anim.FrameBaseLoader;
import com.jagex.io.Buffer;

public class FrameBaseLoader317 extends FrameBaseLoader {

	@Override
	public FrameBase decode(Buffer buffer) {
		FrameBase base = new FrameBase();
		int count = buffer.readUShort();
		int[] transformationType = new int[count];
		int[][] vertexGroups = new int[count][];
		for (int index = 0; index < count; index++) {
			transformationType[index] = buffer.readUByte();
		}

		for (int label = 0; label < count; label++) {
			int count2 = buffer.readUByte();
			vertexGroups[label] = new int[count2];

			for (int index = 0; index < count2; index++) {
				vertexGroups[label][index] = buffer.readUByte();
			}
		}
		base.setCount(count);
		base.setTransformationType(transformationType);
		base.setVertexGroups(vertexGroups);
		return base;
	}

}
