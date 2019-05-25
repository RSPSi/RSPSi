package com.jagex.map.object;

import com.jagex.util.ObjectKey;

public final class WallDecoration extends DefaultWorldObject {

	private int attributes;

	private int orientation;

	public WallDecoration(ObjectKey id, int x, int y, int z) {
		super(id, x, y, z);
		// TODO Auto-generated constructor stub
	}

	public int getAttributes() {
		return attributes;
	}

	public int getOrientation() {
		return orientation;
	}

	@Override
	public WorldObjectType getType() {
		return WorldObjectType.WALL_DECORATION;
	}

	public void setAttributes(int attributes) {
		this.attributes = attributes;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
}