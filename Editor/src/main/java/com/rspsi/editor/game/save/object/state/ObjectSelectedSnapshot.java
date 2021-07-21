package com.rspsi.editor.game.save.object.state;

import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.util.ObjectKey;
import org.joml.Vector3i;

public class ObjectSelectedSnapshot extends TileSnapshot {

    public ObjectSelectedSnapshot(Vector3i position) {
        super(position);
    }

    @Override
    public int getUniqueId() {
        return Integer.MAX_VALUE - 2;
    }

    public ObjectKey key;
    public boolean selected;


    @Override
    public void preserve(SceneGraph sceneGraph) {

    }
}
