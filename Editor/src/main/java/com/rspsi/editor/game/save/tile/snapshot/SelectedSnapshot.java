package com.rspsi.editor.game.save.tile.snapshot;

import com.rspsi.jagex.map.SceneGraph;
import lombok.val;
import org.joml.Vector3i;

public class SelectedSnapshot extends TileSnapshot {

    public SelectedSnapshot(Vector3i position) {
        super(position);
    }

    @Override
    public int getUniqueId() {
        return Integer.MAX_VALUE - 1;
    }

    public boolean selected;
    public boolean anyObjectSelected;

    @Override
    public void preserve(SceneGraph sceneGraph) {
        val tile = sceneGraph.tiles.get(position);
        if(tile != null)
            selected = tile.tileSelected;
    }
}
