package com.rspsi.game.save.object;

import com.jagex.Client;
import com.jagex.map.tile.SceneTile;
import com.jagex.util.ObjectKey;
import com.rspsi.game.save.StateChangeType;
import com.rspsi.game.save.TileChange;
import com.rspsi.game.save.object.state.ObjectState;

/**
 * Represents a spawn of object action
 * @author James
 *
 */
public class SpawnObject extends TileChange<ObjectState> {
	
	public SpawnObject() {
	}

	
	@Override
	public void restoreStates() {
		for(ObjectState state : preservedTileStates.values()) {
			int x = state.getX();
			int y = state.getY();
			int z = state.getZ();
			ObjectKey key = state.getKey();
			if(key != null) {
				SceneTile tile = Client.getSingleton().sceneGraph.tiles[z][x][y];
				tile.removeByUID(key);
			}
		}
		
		for(ObjectState state : preservedTileStates.values()) {
			int x = state.getX();
			int y = state.getY();
			int z = state.getZ();
	
			if(state.getShading() != -1) {
				Client.getSingleton().mapRegion.shading[z][x][y] = state.getShading();
			}
		}
	}
	
	@Override
	public DeleteObject getInverse(){
		try {
			DeleteObject change = new DeleteObject();
			preservedTileStates.values().forEach(state -> {
				if(state.getKey() != null)
				try {
					change.preserveTileState(state);
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

	@Override
	public StateChangeType getType() {
		return StateChangeType.OBJECT_SPAWN;
	}

}
