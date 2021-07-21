package com.rspsi.editor.tools;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.DefaultTileMutator;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.object.DefaultWorldObject;
import lombok.var;

public abstract class ObjectTool extends DefaultTileMutator implements Tool  {

    public ObjectTool(UndoRedoSystem undoRedoSystem) {
        super(undoRedoSystem);
    }

    @Override
    public void raycastCallback(SceneGraph sceneGraph, CollisionWorld.RayResultCallback callback, boolean clicked) {
        if(callback.hasHit()) {
            var userPointer = callback.collisionObject.getUserPointer();

            if(userPointer instanceof DefaultWorldObject) {
                applyToObject(sceneGraph, (DefaultWorldObject) userPointer, clicked);
            }
        }
    }

    public abstract void applyToObject(SceneGraph sceneGraph, DefaultWorldObject worldObject, boolean clicked);

}
