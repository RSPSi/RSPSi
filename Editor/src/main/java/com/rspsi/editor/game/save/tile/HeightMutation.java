package com.rspsi.editor.game.save.tile;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.InvalidTileSnapshotType;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.tile.snapshot.HeightSnapshot;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.map.MapRegion;
import com.rspsi.jagex.map.SceneGraph;
import lombok.val;


public class HeightMutation extends TileMutation {

    public HeightMutation() {
        super();
    }

    @Override
    public boolean canPreserve(TileSnapshot snapshot) {
        return snapshot instanceof HeightSnapshot;
    }

    @Override
    public void restoreStates(SceneGraph sceneGraph) {
        MapRegion mapRegion = sceneGraph.getMapRegion();
        for (TileSnapshot state : preservedTileStates.values()) {

            if (state instanceof HeightSnapshot) {
                val snapshot = (HeightSnapshot) state;
                int x = snapshot.getX();
                int y = snapshot.getY();
                int z = snapshot.getZ();
                mapRegion.tileHeights[z][x][y] = snapshot.getHeight();
            }
        }

        int minX = 1000;
        int minY = 1000;
        int maxX = 0;
        int maxY = 0;

        for (TileSnapshot state : preservedTileStates.values()) {
            int x = state.getX();
            int y = state.getY();
            int z = state.getZ();

            if (x > maxX)
                maxX = x;
            if (y > maxY)
                maxY = y;
            if (x < minX)
                minX = x;
            if (y < minY)
                minY = y;
        }

        //For updating object heights
        sceneGraph.updateHeights(minX - 6, minY - 6, (maxX - minX) + 6, (maxY - minY) + 6);
        //System.out.println("DONE " + minX + ", " + minY + " : " + maxX + ", " + maxY);
    }


    @Override
    public HeightMutation getInverse(SceneGraph sceneGraph) {

        HeightMutation change = new HeightMutation();
        preservedTileStates.keySet().forEach(uniqueIdentifier -> {
            try {
                HeightSnapshot newState = new HeightSnapshot(uniqueIdentifier.getPosition());
                newState.preserve(sceneGraph);
                change.storeSnapshot(newState);
            } catch (InvalidMutationException e) {
                e.printStackTrace();
            }
        });
        return change;


    }

}
