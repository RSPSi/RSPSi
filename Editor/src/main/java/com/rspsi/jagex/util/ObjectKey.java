package com.rspsi.jagex.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.joml.Vector3i;

@Data
@AllArgsConstructor
public class ObjectKey {
	
	private int x, y, z;
	private int id;
	private int type;
	private int orientation;
	private boolean solid;
	private boolean interactive;

	public Vector3i getPosition() {
		return new Vector3i(x, y, z);
	}
	@JsonIgnore
	private boolean temporary;
	
}
