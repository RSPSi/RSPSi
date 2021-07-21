package com.rspsi.editor.tools.integrated;

import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.object.ObjectSelectionMutation;
import com.rspsi.editor.game.save.tile.NoOpMutation;
import com.rspsi.editor.tools.ObjectTool;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.object.DefaultWorldObject;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.options.KeyActions;
import com.rspsi.options.KeyBindings;
import lombok.var;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.UUID;

public class SelectObjectTool extends ObjectTool {

    public List<DefaultWorldObject> selectedObjects = Lists.newArrayList();

    public SelectObjectTool(UndoRedoSystem undoRedoSystem) {
        super(undoRedoSystem);
    }

    /*

            controller.getAddObjectToSwatchBtn().setOnAction(evt -> {

                if (!clientInstance.sceneGraph.selectedObjects.isEmpty()) {
                    for (DefaultWorldObject selectedObject : clientInstance.sceneGraph.selectedObjects) {
                        ObjectKey key = selectedObject.getKey();
                        int id = key.getId();
                        int type = key.getType();
                        ObjectDefinition def = ObjectDefinitionLoader.lookup(id);

                        ObjectDataset set = new ObjectDataset(id, type, def.getName());
                        ObjectPreviewWindow.instance.loadToSwatches(set);
                    }
                }
            });
     */
    @Override
    public void applyToObject(SceneGraph sceneGraph, DefaultWorldObject worldObject, boolean mouseIsDown) {
        if (mouseIsDown) {
            if (worldObject != null) {
                if (KeyBindings.actionValid(KeyActions.ADD_TO_SELECTION_OBJECT)) {
                    if (selectedObjects.contains(worldObject)) {
                        worldObject.setSelected(false);
                        selectedObjects.remove(worldObject);
                    } else {
                        worldObject.setSelected(true);
                        selectedObjects.add(worldObject);
                    }
                } else {

                    if (selectedObjects.contains(worldObject)) {
                        selectedObjects.clear();
                        worldObject.setSelected(false);
                    } else {
                        for (DefaultWorldObject deselectObj : selectedObjects) {
                            deselectObj.setSelected(false);
                        }
                        selectedObjects.clear();
                        worldObject.setSelected(true);
                        selectedObjects.add(worldObject);
                    }

                }

            }
        }
    }


    @Override
    public boolean shouldResetTiles() {
        return false;
    }

    @Override
    public String getId() {
        return IDENTIFIER;
    }


    public static String IDENTIFIER = "select_object";

    @Override
    public TileMutation newMutation() {
        return new ObjectSelectionMutation();
    }
}
