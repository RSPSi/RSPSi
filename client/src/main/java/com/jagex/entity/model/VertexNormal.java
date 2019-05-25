package com.jagex.entity.model;

import javafx.geometry.Point3D;

public class VertexNormal {

	// Class33

	public int magnitude;

	public int x;
	public int y;
	public int z;

	public Point3D getAsPoint3D() {
		return new Point3D(x, y, z);
	}

	public int getFaceCount() {
		return magnitude;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public void setFaceCount(int faces) {
		this.magnitude = faces;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

}