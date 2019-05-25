package com.rspsi.game.save.tile;

import com.jagex.Client;
import com.jagex.chunk.Chunk;
import com.rspsi.game.save.StateChangeType;
import com.rspsi.game.save.TileChange;
import com.rspsi.game.save.tile.state.FlagState;

import java.awt.Rectangle;


public class FlagChange extends TileChange<FlagState> {
	
	public FlagChange() {
	}

	
	@Override
	public void restoreStates() {
		for(FlagState state : preservedTileStates.values()) {
			int x = state.getX();
			int y = state.getY();
			int z = state.getZ();
			Client.getSingleton().mapRegion.tileFlags[z][x][y] = state.getFlag();
		}
	}

	@Override
	public StateChangeType getType() {
		return StateChangeType.TILE_FLAG;
	}


	@Override
	public FlagChange getInverse() {
		{
			try {
				FlagChange change = new FlagChange();
				preservedTileStates.values().forEach(state -> {
					try {
						FlagState newState = new FlagState(state.getX(), state.getY(), state.getZ());
						newState.preserve();
						change.preserveTileState(newState);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				return change;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
			
		}
	}
}
