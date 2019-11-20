package com.jagex.entity;

import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.model.Mesh;
import com.jagex.entity.model.VertexNormal;
import com.jagex.util.ObjectKey;
import lombok.Getter;
import lombok.Setter;

public class Renderable {

	// Class30_Sub2_Sub4

	protected int modelHeight = 1000;
	protected VertexNormal[] normals;
	public boolean selected;

	public int getModelHeight() {
		return modelHeight;
	}

	public VertexNormal getNormal(int index) {
		return normals[index];
	}

	public VertexNormal[] getNormals() {
		return normals;
	}

	public boolean hasNormals() {
		return normals != null;
	}

	public Mesh model() {
		return null;
	}

	public void render(GameRasterizer rasterizer, int x, int y, int orientation, int ySine, int yCosine, int xSine, int xCosine, int height, ObjectKey key, int plane) {
		Mesh model = model();
		if (model != null) {
			modelHeight = model.modelHeight;
			model.render(rasterizer, x, y, orientation, ySine, yCosine, xSine, xCosine, height, key, plane);
		}
	}

	public void setModelHeight(int modelHeight) {
		this.modelHeight = modelHeight;
	}

	public void setNormals(VertexNormal[] normals) {
		this.normals = normals;
	}
	
	public Mesh asMesh() {
		if(this instanceof Mesh) {
			return (Mesh) this;
		}
		
		throw new ClassCastException("This is not an instance of mesh!");
		
	}


	public Renderable copy(){
		return this;
	}
	@Getter
	@Setter
	private int bufferOffset, uvBufferOffset, bufferLen;

}