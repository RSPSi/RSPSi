package com.rspsi.editor.game.save.object;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.object.state.ObjectSelectedSnapshot;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.map.SceneGraph;
import lombok.val;
import org.joml.Vector3i;

public class ObjectSelectionMutation extends TileMutation {
    @Override
    public TileMutation getInverse(SceneGraph sceneGraph) {

        ObjectSelectionMutation change = new ObjectSelectionMutation();
        preservedTileStates.values()
                .stream()
                .filter(ObjectSelectedSnapshot.class::isInstance)
                .map(ObjectSelectedSnapshot.class::cast)
                .forEach(state -> {
                    try {
                        change.storeSnapshot(state);
                    } catch (InvalidMutationException e) {
                        e.printStackTrace();
                    }
                });
        return change;

    }

    @Override
    public void restoreStates(SceneGraph sceneGraph) {
        preservedTileStates.values()
                .stream()
                .filter(ObjectSelectedSnapshot.class::isInstance)
                .map(ObjectSelectedSnapshot.class::cast)
                .forEachOrdered(state -> {
                    if (state.key == null)
                        return;
                    int x = state.getX();
                    int y = state.getY();
                    int z = state.getZ();
                    val tile = sceneGraph.tiles.get(state.position);
                    if (tile != null && state.key != null) {
                        val worldObject = tile.getObject(state.key);
                        if (worldObject != null) {
                            worldObject.setSelected(state.selected);
                        }
                    }
                });
    }

    @Override
    public boolean canPreserve(TileSnapshot snapshot) {
        return snapshot instanceof ObjectSelectedSnapshot;
    }
}
