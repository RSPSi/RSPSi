package com.rspsi.editor.game.save;

import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;

public class InvalidTileSnapshotType extends Throwable {

    private TileMutation tileMutation;
    private TileSnapshot currentState;

    public InvalidTileSnapshotType(TileMutation tileMutation, TileSnapshot requestedSnapshot) {
        this.tileMutation = tileMutation;
        this.currentState = requestedSnapshot;
    }

    @Override
    public String toString() {
        return "InvalidTileSnapshotType{" +
                "attempted to store invalid snapshot class [" + currentState.getClass() + "]" +
                "to [" + tileMutation.getClass() + "]";
    }
}
