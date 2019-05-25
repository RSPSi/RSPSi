package com.jagex.map.object;

import com.jagex.util.ObjectKey;

public interface WorldObject {

	public ObjectKey getKey();

	public int getPlane();

	public int getRenderHeight();

	public int getX();

	public int getY();

}
