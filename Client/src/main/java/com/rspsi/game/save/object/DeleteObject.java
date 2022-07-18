package com.rspsi.game.save.object;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.jagex.Client;
import com.jagex.chunk.Chunk;
import com.rspsi.game.save.StateChangeType;
import com.rspsi.game.save.TileChange;
import com.rspsi.game.save.TileUniqueIdentifier;
import com.rspsi.game.save.object.state.ObjectState;

/**
 * Represents a delete object action
 * @author James
 *
 */
public class DeleteObject extends TileChange<ObjectState> {
	
	public DeleteObject() {
	}
	

	@Override
	public void restoreStates() {
		Comparator<ObjectState> objectStateComparator = (state1, state2) -> {
			return Integer.compare(state1.getKey().getType(), state2.getKey().getType());
		};
		List<ObjectState> sortedStates = preservedTileStates.values().stream().sorted(objectStateComparator).collect(Collectors.toList());
		for(ObjectState state : sortedStates) {
			if(state.getKey() == null)
				continue;
			int x = state.getX();
			int y = state.getY();
			int z = state.getZ();
			
			int id = state.getKey().getId();
			int type = state.getKey().getType();
			int rotation = state.getKey().getOrientation();
			Client.getSingleton().sceneGraph.addObject(x, y, z, id, type, rotation, false);
			
			System.out.println("LOAD STATE SPAWNED " + id + ":" + type + ":" + rotation + " at " + x + ", " + y + ", " + z);
			
		}
	}
	
	@Override
	public SpawnObject getInverse(){
		try {
			SpawnObject change = new SpawnObject();
			preservedTileStates.values().forEach(state -> {
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
		return StateChangeType.OBJECT_DELETE;
	}
}
