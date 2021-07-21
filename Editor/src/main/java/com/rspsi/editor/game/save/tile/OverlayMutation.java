package com.rspsi.editor.game.save.tile;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.InvalidTileSnapshotType;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.tile.snapshot.OverlaySnapshot;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.map.SceneGraph;
import lombok.val;


public class OverlayMutation extends TileMutation {

    public OverlayMutation() {
        super();
    }

    @Override
    public boolean canPreserve(TileSnapshot snapshot) {
        return snapshot instanceof OverlaySnapshot;
    }

    @Override
    public void restoreStates(SceneGraph sceneGraph) {
        for (TileSnapshot state : preservedTileStates.values()) {
            if (state instanceof OverlaySnapshot) {
                val snapshot = (OverlaySnapshot) state;
                int x = snapshot.getX();
                int y = snapshot.getY();
                int z = snapshot.getZ();
                //System.out.println("LOADING " + x + ":" + y + ":" + z);
                sceneGraph.getMapRegion().overlayOrientations[z][x][y] = snapshot.getRotation();
                sceneGraph.getMapRegion().overlays[z][x][y] = snapshot.getId();
                sceneGraph.getMapRegion().overlayShapes[z][x][y] = snapshot.getShape();
            }
        }
    }

    @Override
    public OverlayMutation getInverse(SceneGraph sceneGraph) {
        OverlayMutation change = new OverlayMutation();
        preservedTileStates.keySet().forEach(uniqueIdentifier -> {
            try {
                OverlaySnapshot newState = new OverlaySnapshot(uniqueIdentifier.getPosition());
                newState.preserve(sceneGraph);
                change.storeSnapshot(newState);
            } catch (InvalidMutationException e) {
                e.printStackTrace();
            }
        });
        return change;


    }


}
