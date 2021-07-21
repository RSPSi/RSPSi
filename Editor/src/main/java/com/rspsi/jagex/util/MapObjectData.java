package com.rspsi.jagex.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.joml.Vector3i;

@Data
@AllArgsConstructor
public class MapObjectData {
	
	private int id, x, y, z, type, orientation;

    public Vector3i getPosition() {
        return new Vector3i(x, y, z);
    }
}
