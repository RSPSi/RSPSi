package com.rspsi.game.save.object.state;

import com.jagex.Client;
import com.jagex.util.ObjectKey;
import com.rspsi.game.save.tile.state.TileState;

public class ObjectState extends TileState {

	/**
	 * The key of the object on this tile being preserved
	 */
	private ObjectKey key;
	
	/**
	 * The previous shading on this tile
	 */
	private byte shading = -1;//XXX UNUSED
	
	
	public ObjectState(int x, int y, int z) {
		super(x, y, z);
	}

	@Override
	public void preserve() {
		this.shading = Client.getSingleton().mapRegion.shading[z][x][y];
	}

	public ObjectKey getKey() {
		return key;
	}

	public void setKey(ObjectKey key) {
		this.key = key;
	}

	public byte getShading() {
		return shading;
	}
	
	
}
