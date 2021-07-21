package com.rspsi.editor.game.save.object;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.object.state.ObjectSnapshot;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.map.SceneGraph;
import lombok.val;
import org.joml.Vector3i;

import java.util.Comparator;

/**
 * Represents a delete object action
 *
 * @author James
 */
public class ObjectDeleteMutation extends TileMutation {


    @Override
    public boolean canPreserve(TileSnapshot snapshot) {
        return snapshot instanceof ObjectSnapshot;
    }

    @Override
    public void restoreStates(SceneGraph sceneGraph) {
        Comparator<ObjectSnapshot> objectStateComparator = Comparator.comparingInt(state -> state.key.getType());
        preservedTileStates.values()
                .stream()
                .filter(ObjectSnapshot.class::isInstance)
                .map(ObjectSnapshot.class::cast)
                .sorted(objectStateComparator)
                .forEachOrdered(state -> {
                    if (state.key == null)
                        return;

                    int id = state.key.getId();
                    int type = state.key.getType();
                    int rotation = state.key.getOrientation();
                    sceneGraph.getMapRegion().spawnObjectToWorld(sceneGraph, state.position, id, type, rotation, false);
                    //sceneGraph.addObject(x, y, z, id, type, rotation, false);
                });

    }


    @Override
    public ObjectSpawnMutation getInverse(SceneGraph sceneGraph) {

        ObjectSpawnMutation change = new ObjectSpawnMutation();
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
