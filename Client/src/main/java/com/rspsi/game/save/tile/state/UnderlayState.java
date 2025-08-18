package com.rspsi.game.save.tile.state;

import com.jagex.Client;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UnderlayState extends TileState {
	
	private short id;
	
	public UnderlayState(int x, int y, int z) {
		super(x, y, z);
	}
	
	public short getId() {
		return id;
	}

	@Override
	public void preserve() {
		this.id = Client.getSingleton().mapRegion.underlays[z][x][y];
	}

	@Override
	public int getUniqueId() {
		return 1;
	}


	
	
}
