package com.rspsi.editor.tools.integrated;

import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.object.ObjectDeleteMutation;
import com.rspsi.editor.tools.ObjectTool;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.object.DefaultWorldObject;
import javafx.scene.Scene;
import org.joml.Vector3i;

import java.util.UUID;

public class DeleteObjectTool extends ObjectTool {

	public DeleteObjectTool(UndoRedoSystem undoRedoSystem) {
		super(undoRedoSystem);
	}

	@Override
	public void applyToObject(SceneGraph sceneGraph, DefaultWorldObject worldObject, boolean clicked) {



	}


	@Override
	public TileMutation newMutation() {
		return new ObjectDeleteMutation();
	}

	@Override
	public boolean shouldResetTiles() {
		return false;
	}

	@Override
	public String getId() {
		return IDENTIFIER;
	}

	public static String IDENTIFIER = "delete_object";
}
