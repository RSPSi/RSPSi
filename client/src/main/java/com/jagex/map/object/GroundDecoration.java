package com.jagex.map.object;

import com.jagex.util.ObjectKey;

public final class GroundDecoration extends DefaultWorldObject {

	public GroundDecoration(ObjectKey id, int x, int y, int renderHeight) {
		super(id, x, y, renderHeight);
	}

	@Override
	public WorldObjectType getType() {
		return WorldObjectType.GROUND_DECORATION;
	}

}