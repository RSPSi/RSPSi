package com.jagex.map.object;

import com.jagex.cache.graphics.Sprite;
import com.jagex.util.ObjectKey;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public final class GroundDecoration extends DefaultWorldObject {

	public GroundDecoration(ObjectKey id, int x, int y, int renderHeight) {
		super(id, x, y, renderHeight);
	}

	@Getter @Setter
	private Sprite minimapFunction = null;
	@Override
	public WorldObjectType getType() {
		return WorldObjectType.GROUND_DECORATION;
	}

}