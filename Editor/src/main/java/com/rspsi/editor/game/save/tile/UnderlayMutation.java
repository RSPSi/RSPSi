package com.rspsi.editor.game.save.tile;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.InvalidTileSnapshotType;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.editor.game.save.tile.snapshot.UnderlaySnapshot;
import com.rspsi.jagex.map.SceneGraph;
import lombok.var;


public class UnderlayMutation extends TileMutation {

    public UnderlayMutation() {
        super();
    }


    @Override
    public boolean canPreserve(TileSnapshot snapshot) {
        return snapshot instanceof UnderlaySnapshot;
    }

    @Override
    public void restoreStates(SceneGraph sceneGraph) {
        for (TileSnapshot state : preservedTileStates.values()) {
            if (state instanceof UnderlaySnapshot) {
                var snapshot = (UnderlaySnapshot) state;
                int x = state.getX();
                int y = state.getY();
                int z = state.getZ();
                sceneGraph.getMapRegion().underlays[z][x][y] = snapshot.getId();
            }
        }
    }

    @Override
    public UnderlayMutation getInverse(SceneGraph sceneGraph) {

        UnderlayMutation change = new UnderlayMutation();
        preservedTileStates.keySet().forEach(state -> {
            try {
                UnderlaySnapshot newState = new UnderlaySnapshot(state.getPosition());
                newState.preserve(sceneGraph);
                change.storeSnapshot(newState);
            } catch (InvalidMutationException e) {
                e.printStackTrace();
            }
        });
        return change;


    }


}
