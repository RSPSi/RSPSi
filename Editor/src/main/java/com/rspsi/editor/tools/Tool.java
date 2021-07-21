package com.rspsi.editor.tools;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.jagex.map.SceneGraph;
import javafx.scene.input.KeyEvent;

public interface Tool {

	/**
	 * @return the identifer string of this tool
	 */
	String getId();

	/**
	 * Handles a raycast result
     * @param sceneGraph
     * @param callback The callback for the raycast
     * @param mouseDown if the mouse was pressed during the raycast
     */
	void raycastCallback(SceneGraph sceneGraph, CollisionWorld.RayResultCallback callback, boolean mouseDown);

	boolean shouldResetTiles();

}
