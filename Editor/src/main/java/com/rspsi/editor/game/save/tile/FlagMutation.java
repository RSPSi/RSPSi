package com.rspsi.editor.game.save.tile;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.InvalidTileSnapshotType;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.tile.snapshot.FlagSnapshot;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;


public class FlagMutation extends TileMutation {

    public FlagMutation() {
    }


    @Override
    public void restoreStates(SceneGraph sceneGraph) {
        for (TileSnapshot state : preservedTileStates.values()) {
            if (state instanceof FlagSnapshot) {
                int x = state.getX();
                int y = state.getY();
                int z = state.getZ();
                Client.getSingleton().mapRegion.tileFlags[z][x][y] = ((FlagSnapshot) state).getFlag();
            }
        }
    }

    @Override
    public boolean canPreserve(TileSnapshot snapshot) {
        return snapshot instanceof FlagSnapshot;
    }


    @Override
    public FlagMutation getInverse(SceneGraph sceneGraph) {
        FlagMutation change = new FlagMutation();
        preservedTileStates.keySet().forEach(uniqueIdentifier -> {
            try {
                FlagSnapshot newState = new FlagSnapshot(uniqueIdentifier.getPosition());
                newState.preserve(sceneGraph);
                change.storeSnapshot(newState);
            } catch (InvalidMutationException e) {
                e.printStackTrace();
            }
        });
        return change;


    }
}
