package com.rspsi.jagex.entity;

import com.rspsi.jagex.draw.raster.GameRasterizer;
import com.rspsi.jagex.entity.model.Mesh;
import com.rspsi.jagex.entity.model.ModelInstance;
import com.rspsi.jagex.entity.model.VertexNormal;
import com.rspsi.jagex.util.ObjectKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Data
@Slf4j
public abstract class Renderable implements Comparable<Renderable> {

	public int id;

	public boolean selected, highlighted, translucent;

	public int height = 100;

	public void render(GameRasterizer rasterizer, int x, int y, int orientation, int ySine, int yCosine, int xSine, int xCosine, int height, ObjectKey key, int plane) {

		ModelInstance modelInstance = model();
		if(modelInstance != null) {
			this.height = modelInstance.height;
			modelInstance.render(rasterizer, x, y, orientation, ySine, yCosine, xSine, xCosine, height, key, plane);
		}
	}


	public ModelInstance model() {
		return null;
	}

	public abstract <T extends Renderable> T copy();

	int uvBufferOffset, bufferOffset, bufferLen;
	public int sceneId;

	@Override
	public int compareTo(Renderable o) {
		if(o == null) return -1;
		return Long.compare(o.id, id);
	}
}