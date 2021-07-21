package com.rspsi.editor.tools;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.DefaultTileMutator;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.tile.SceneTile;
import lombok.var;

public abstract class TileTool extends DefaultTileMutator implements Tool  {

    public TileTool(UndoRedoSystem undoRedoSystem) {
        super(undoRedoSystem);
    }

    @Override
    public void raycastCallback(SceneGraph sceneGraph, CollisionWorld.RayResultCallback callback, boolean mouseDown) {
        if(callback.hasHit()) {
            var userPointer = callback.collisionObject.getUserPointer();

            if(userPointer instanceof SceneTile) {
                var tile = (SceneTile) userPointer;
                undoRedoSystem.setMutation(newMutation());
                try {
                    applyToTile(sceneGraph, tile, mouseDown);
                    undoRedoSystem.commitChanges();
                } catch (InvalidMutationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public abstract void applyToTile(SceneGraph sceneGraph, SceneTile tile, boolean mouseDown) throws InvalidMutationException;

}
