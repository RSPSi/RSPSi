package com.rspsi.editor.game.save.tile;

import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.map.SceneGraph;

public class NoOpMutation extends TileMutation {

    @Override
    public TileMutation getInverse(SceneGraph sceneGraph) {
        return new NoOpMutation();
    }

    @Override
    public void restoreStates(SceneGraph sceneGraph) {

    }

    @Override
    public boolean canPreserve(TileSnapshot snapshot) {
        return false;
    }
}
