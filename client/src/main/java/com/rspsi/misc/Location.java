package com.rspsi.misc;

public class Location {
	
	private int x, y, z;
	
	public Location(int x, int y, int z) {
		super();
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
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}
	
	

}
