package com.rspsi.game.save.tile;

import com.jagex.Client;
import com.jagex.chunk.Chunk;
import com.jagex.map.MapRegion;
import com.jagex.map.SceneGraph;
import com.rspsi.game.save.StateChangeType;
import com.rspsi.game.save.TileChange;
import com.rspsi.game.save.tile.state.HeightState;

import java.awt.Rectangle;


public class HeightChange extends TileChange<HeightState> {
	
	public HeightChange() {
		super();
	}
	
	@Override
	public void restoreStates() {
		SceneGraph sceneGraph = Client.getSingleton().sceneGraph;
		MapRegion mapRegion = Client.getSingleton().mapRegion;
		for(HeightState state : preservedTileStates.values()) {

			int x = state.getX();
			int y = state.getY();
			int z = state.getZ();
			mapRegion.tileHeights[z][x][y] = state.getHeight();
		}
		
		int minX = 1000;
		int minY = 1000;
		int maxX = 0;
		int maxY = 0;
		
		for(HeightState state : preservedTileStates.values()) {
			int x = state.getX();
			int y = state.getY();
			int z = state.getZ();
			
			if(x > maxX)
				maxX = x;
			if(y > maxY)
				maxY = y;
			if(x < minX)
				minX = x;
			if(y < minY)
				minY = y;
		}

		//For updating object heights
		sceneGraph.updateHeights(minX - 6, minY - 6, (maxX - minX) + 6, (maxY - minY) + 6);
		//System.out.println("DONE " + minX + ", " + minY + " : " + maxX + ", " + maxY);
	}
	

	@Override
	public HeightChange getInverse() {
		{
			try {
				HeightChange change = new HeightChange();
				preservedTileStates.values().forEach(state -> {
					try {
						HeightState newState = new HeightState(state.getX(), state.getY(), state.getZ());
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
		return StateChangeType.TILE_HEIGHT;
	}
}
