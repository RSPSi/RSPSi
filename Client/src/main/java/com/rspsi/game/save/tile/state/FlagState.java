package com.rspsi.game.save.tile.state;

import com.jagex.Client;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class FlagState extends TileState {
	
	private byte flag;
	
	public FlagState(int x, int y, int z) {
		super(x, y, z);
	}
	
	public byte getFlag() {
		return flag;
	}

	@Override
	public void preserve() {
		this.flag = Client.getSingleton().mapRegion.tileFlags[z][x][y];
	}

	@Override
	public int getUniqueId() {
		return 4;
	}

	
	
}
