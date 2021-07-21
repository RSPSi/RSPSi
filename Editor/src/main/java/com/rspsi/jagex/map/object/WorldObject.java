package com.rspsi.jagex.map.object;

import com.rspsi.jagex.util.ObjectKey;

public interface WorldObject {

	ObjectKey getKey();

	int getPlane();

	int getRenderHeight();

	int getX();

	int getY();

}
