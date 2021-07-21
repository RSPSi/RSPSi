package com.rspsi.editor.game.save.object;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.object.state.ObjectSnapshot;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.jagex.util.ObjectKey;
import lombok.val;

/**
 * Represents a spawn of object action
 *
 * @author James
 */
public class ObjectSpawnMutation extends TileMutation {


    @Override
    public void restoreStates(SceneGraph sceneGraph) {
        for (TileSnapshot state : preservedTileStates.values()) {
			if (state instanceof ObjectSnapshot) {
				val snapshot = (ObjectSnapshot) state;
				int x = snapshot.getX();
				int y = snapshot.getY();
				int z = snapshot.getZ();
				ObjectKey key = snapshot.key;
				if (key != null) {
					SceneTile tile = sceneGraph.tiles.get(snapshot.position);
					tile.remove(key);
				}

                if (snapshot.shading != -1) {
                    sceneGraph.getMapRegion().shading[z][x][y] = snapshot.shading;
                }
            }
        }
    }

    @Override
    public boolean canPreserve(TileSnapshot snapshot) {
        return snapshot instanceof ObjectSnapshot;
    }

    @Override
    public ObjectDeleteMutation getInverse(SceneGraph sceneGraph) {

        ObjectDeleteMutation change = new ObjectDeleteMutation();
        preservedTileStates.values()
                .stream()
                .filter(ObjectSnapshot.class::isInstance)
                .map(ObjectSnapshot.class::cast)
                .forEach(state -> {
                    try {
                        change.storeSnapshot(state);
                    } catch (InvalidMutationException e) {
                        e.printStackTrace();
                    }
                });
        return change;

    }


}
