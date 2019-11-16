package com.jagex.map.object;

import com.jagex.util.ObjectKey;
import lombok.Getter;
import lombok.Setter;

public final class GroundDecoration extends DefaultWorldObject {

	public GroundDecoration(ObjectKey id, int x, int y, int renderHeight) {
		super(id, x, y, renderHeight);
	}

	@Getter @Setter
	private int minimapFunction = -1;
	@Override
	public WorldObjectType getType() {
		return WorldObjectType.GROUND_DECORATION;
	}

}