package com.rspsi.game.save.tile;

import com.jagex.Client;
import com.jagex.chunk.Chunk;
import com.rspsi.game.save.StateChangeType;
import com.rspsi.game.save.TileChange;
import com.rspsi.game.save.tile.state.UnderlayState;

import java.awt.Rectangle;


public class UnderlayChange extends TileChange<UnderlayState> {
	
	public UnderlayChange() {
		super();
	}

	@Override
	public void restoreStates() {
		for(UnderlayState state : preservedTileStates.values()) {
			int x = state.getX();
			int y = state.getY();
			int z = state.getZ();
			Client.getSingleton().mapRegion.underlays[z][x][y] = state.getId();
		}
	}
	
	@Override
	public UnderlayChange getInverse() {
		{
			try {
				UnderlayChange change = new UnderlayChange();
				preservedTileStates.values().forEach(state -> {
					try {
						UnderlayState newState = new UnderlayState(state.getX(), state.getY(), state.getZ());
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

	@Override
	public StateChangeType getType() {
		return StateChangeType.UNDERLAY;
	}
}
