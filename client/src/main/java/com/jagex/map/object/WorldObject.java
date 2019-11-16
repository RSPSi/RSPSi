package com.jagex.map.object;

import com.jagex.util.ObjectKey;

public interface WorldObject {

	ObjectKey getKey();

	int getPlane();

	int getRenderHeight();

	int getX();

	int getY();

}
