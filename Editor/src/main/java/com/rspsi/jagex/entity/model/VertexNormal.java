package com.rspsi.jagex.entity.model;

import lombok.Data;
import org.joml.Vector3i;

public class VertexNormal {
	public int magnitude = 0;
	public Vector3i position = new Vector3i(0, 0, 0);
	public VertexNormal(VertexNormal var1) {
		this.position.set(var1.position);
		this.magnitude = var1.magnitude;
	}
	public VertexNormal() {

	}

	public VertexNormal normalize() {
		return this;
	}
}