package com.rspsi.editor.tools.integrated;

import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.object.ObjectSpawnMutation;
import com.rspsi.editor.tools.ObjectTool;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.object.DefaultWorldObject;

import java.util.UUID;

public class SpawnObjectTool extends ObjectTool {

	public SpawnObjectTool(UndoRedoSystem undoRedoSystem) {
		super(undoRedoSystem);
	}

	@Override
	public TileMutation newMutation() {
		return new ObjectSpawnMutation();
	}

	@Override
	public boolean shouldResetTiles() {
		return false;
	}

	@Override
	public String getId() {
		return IDENTIFIER;
	}

	public static String IDENTIFIER = "spawn_object";

	@Override
	public void applyToObject(SceneGraph sceneGraph, DefaultWorldObject worldObject, boolean clicked) {

	}
}
