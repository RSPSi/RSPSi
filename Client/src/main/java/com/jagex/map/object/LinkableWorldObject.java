package com.jagex.map.object;

import com.jagex.link.Linkable;

public class LinkableWorldObject extends Linkable {

	/**
	 * A packed config value containing the type and orientation of this decoration,
	 * in the form {@code (orientation << 6) | type}.
	 */
	private final int x;
	private final int y;
	private final int z;

	private int id;

	public LinkableWorldObject(int id, int x, int y, int z) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getId() {
		return id;
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

}
