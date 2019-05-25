package com.jagex.map.object;

import com.jagex.util.ObjectKey;

public final class GameObject extends DefaultWorldObject {

	public int anInt527;

	public int lastRenderCycle;
	public int centreX;
	public int centreY;
	public int minX;
	public int minY;
	public int maxX;
	public int maxY;
	public int yaw;

	public GameObject(ObjectKey id, int x, int y, int z) {
		super(id, x, y, z);
	}

	@Override
	public WorldObjectType getType() {
		return WorldObjectType.GAME_OBJECT;
	}

}