package com.rspsi.game.save;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class TileUniqueIdentifier {
	
	private Vector3D vector;
	private long key;
	
	public TileUniqueIdentifier(Vector3D vector, long key) {
		super();
		this.vector = vector;
		this.key = key;
	}
	public Vector3D getVector() {
		return vector;
	}
	
	public long getKey() {
		return key;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof TileUniqueIdentifier) {
			TileUniqueIdentifier t = (TileUniqueIdentifier) o;
			return t.vector.equals(this.vector) && t.key == this.key;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) (key + vector.hashCode());
	}

}
