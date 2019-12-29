package com.rspsi.game.save.tile.state;

import com.jagex.Client;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class HeightState extends TileState {
	
	private int height;
	
	public HeightState(int x, int y, int z) {
		super(x, y, z);
	}
	
	public int getHeight() {
		return height;
	}

	@Override
	public void preserve() {
		this.height = Client.getSingleton().mapRegion.tileHeights[z][x][y];
	}

	@Override
	public int getUniqueId() {
		return 3;
	}

	
	
}
