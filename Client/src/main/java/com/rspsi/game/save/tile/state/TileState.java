package com.rspsi.game.save.tile.state;

import com.jagex.util.ObjectKey;

public abstract class TileState {

	private TileState(){

	}
	
	protected int x, y, z;
	
	public TileState(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	
	public abstract void preserve();
	
	public ObjectKey getKey() {
		return null;
	}
	
	public int getUniqueId() {
		return 0;
	}
}
