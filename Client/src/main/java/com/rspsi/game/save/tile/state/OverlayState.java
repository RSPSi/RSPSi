package com.rspsi.game.save.tile.state;

import com.jagex.Client;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class OverlayState extends TileState {
	
	private byte id;
	private byte rotation;
	private byte shape;
	
	public OverlayState(int x, int y, int z) {
		super(x, y, z);
	}

	public byte getId() {
		return id;
	}
	public byte getRotation() {
		return rotation;
	}
	public byte getShape() {
		return shape;
	}
	
	@Override
	public void preserve() {
		this.id = Client.getSingleton().mapRegion.overlays[z][x][y];
		this.rotation = Client.getSingleton().mapRegion.overlayOrientations[z][x][y];
		this.shape = Client.getSingleton().mapRegion.overlayShapes[z][x][y];
	}

	@Override
	public int getUniqueId() {
		return 2;
	}
	
	

}
