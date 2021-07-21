package com.rspsi.editor.game.save.tile;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.tile.snapshot.SelectedSnapshot;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.map.SceneGraph;
import lombok.val;

public class TileSelectedMutation extends TileMutation {


    @Override
    public TileMutation getInverse(SceneGraph sceneGraph) {
        TileSelectedMutation change = new TileSelectedMutation();
        preservedTileStates.keySet().forEach(uniqueIdentifier -> {
            try {
                SelectedSnapshot newState = new SelectedSnapshot(uniqueIdentifier.getPosition());
                newState.preserve(sceneGraph);
                change.storeSnapshot(newState);
            } catch (InvalidMutationException e) {
                e.printStackTrace();
            }
        });
        return change;

    }

    @Override
    public void restoreStates(SceneGraph sceneGraph) {
        for (TileSnapshot state : preservedTileStates.values()) {
            if (state instanceof SelectedSnapshot) {
                val snapshot = (SelectedSnapshot) state;
                int x = snapshot.getX();
                int y = snapshot.getY();
                int z = snapshot.getZ();
                //System.out.println("LOADING " + x + ":" + y + ":" + z);
                val tile =  sceneGraph.tiles.get(state.position);
                if(tile != null)
                 tile.tileSelected = snapshot.selected;
            }
        }
    }

    @Override
    public boolean canPreserve(TileSnapshot snapshot) {
        return snapshot instanceof SelectedSnapshot;
    }
}
