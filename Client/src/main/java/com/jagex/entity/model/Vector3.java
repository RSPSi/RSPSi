package com.jagex.entity.model;

import javafx.geometry.Point3D;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Vector3 {

	// Class33

	public int x;
	public int y;
	public int z;

	public Point3D getAsPoint3D() {
		return new Point3D(x, y, z);
	}


}