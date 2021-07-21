package com.rspsi.editor.game.save.tile;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.InvalidTileSnapshotType;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.tile.snapshot.ImportTileSnapshot;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;
import lombok.val;
import lombok.var;

public class ImportMutation extends TileMutation {

    public ImportMutation() {
        super();
    }

    @Override
    public boolean canPreserve(TileSnapshot snapshot) {
        return snapshot instanceof ImportTileSnapshot;
    }


    @Override
    public ImportMutation getInverse(SceneGraph sceneGraph) {
        ImportMutation change = new ImportMutation();
        preservedTileStates.keySet().forEach(uniqueIdentifier -> {
            try {
                ImportTileSnapshot newState = new ImportTileSnapshot(uniqueIdentifier.getPosition());
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
        int minX = 1000;
        int minY = 1000;
        int maxX = 0;
        int maxY = 0;
        
        val mapRegion = sceneGraph.getMapRegion();

        //TODO Object spawning/despawning
        for (TileSnapshot state : preservedTileStates.values()) {
            int x = state.getX();
            int y = state.getY();
            int z = state.getZ();
            if (state instanceof ImportTileSnapshot) {
                val snapshot = (ImportTileSnapshot) state;
                mapRegion.tileFlags[z][x][y] = snapshot.flagState.getFlag();

                mapRegion.overlayOrientations[z][x][y] = snapshot.overlayState.getRotation();
                mapRegion.overlays[z][x][y] = snapshot.overlayState.getId();
                mapRegion.overlayShapes[z][x][y] = snapshot.overlayState.getShape();

                mapRegion.underlays[z][x][y] = snapshot.underlayState.getId();

                mapRegion.tileHeights[z][x][y] = snapshot.heightState.getHeight();
            }
            if (x > maxX)
                maxX = x;
            if (y > maxY)
                maxY = y;
            if (x < minX)
                minX = x;
            if (y < minY)
                minY = y;
        }

        sceneGraph.updateHeights(minX - 6, minY - 6, (maxX - minX) + 6, (maxY - minY) + 6);
    }


}
