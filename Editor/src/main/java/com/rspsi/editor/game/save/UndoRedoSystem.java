package com.rspsi.editor.game.save;

import com.google.common.collect.Lists;
import com.rspsi.editor.tools.ToolKeyEventHandler;
import com.rspsi.jagex.map.SceneGraph;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyEvent;


public class UndoRedoSystem implements ToolKeyEventHandler {

    public UndoRedoSystem(SceneGraph sceneGraph) {
        this.sceneGraph = sceneGraph;
    }

    private final SceneGraph sceneGraph;
/*

 SceneGraph.undoList.addListener((ListChangeListener<TileMutation>) listener -> {
                controller.getUndoMenuItem().disableProperty().set(SceneGraph.undoList.isEmpty());
            });

            SceneGraph.redoList.addListener((ListChangeListener<TileMutation>) listener -> {
                controller.getRedoMenuItem().disableProperty().set(SceneGraph.redoList.isEmpty());
            });




            controller.getUndoMenuItem().setOnAction(evt -> SceneGraph.undo());
            controller.getRedoMenuItem().setOnAction(evt -> SceneGraph.redo());
 */


    public void clearStates() {
        currentState = null;
        undoList.clear();
        redoList.clear();
        sceneGraph.minimapUpdate = true;
        System.out.println("STATE CLEARED");
    }

    public void undo() {
        if (undoList.isEmpty())
            return;
        System.out.println("UNDO");
        TileMutation change = undoList.get(0);
        undoList.remove(0);
        TileMutation currState = change.getInverse(sceneGraph);
        redoList.add(0, currState);
        change.restoreStates(sceneGraph);
        sceneGraph.onCycleEnd.add(sceneGraph -> {
            sceneGraph.tileQueue.clear();
            sceneGraph.getMapRegion().updateTiles();
            sceneGraph.shadeObjects(-50, -10, -50);
            sceneGraph.minimapUpdate = true;
        });
    }

    public void redo() {
        if (redoList.isEmpty())
            return;
        System.out.println("REDO");
        TileMutation change = redoList.get(0);
        redoList.remove(0);
        TileMutation currState = change.getInverse(sceneGraph);
        undoList.add(0, currState);

        change.restoreStates(sceneGraph);

        SceneGraph.onCycleEnd.add(sceneGraph -> {
            sceneGraph.tileQueue.clear();
            sceneGraph.getMapRegion().updateTiles();
            sceneGraph.shadeObjects(-50, -10, -50);
            sceneGraph.minimapUpdate = true;
        });
    }

    public void commitChanges() {
        if (currentState == null)
            return;

        TileMutation change = currentState;
        currentState = null;
        if (change.containsChanges()) {
            undoList.add(0, change);
            redoList.clear();
            sceneGraph.minimapUpdate = true;
        }
    }

    public TileMutation currentState = null;
    public ObservableList<TileMutation> undoList = FXCollections.observableList(Lists.newLinkedList());
    public ObservableList<TileMutation> redoList = FXCollections.observableList(Lists.newLinkedList());

    public void setMutation(TileMutation newMutation) {
        commitChanges();
        currentState = newMutation;
    }

    /**
     * Handles the incoming key event
     * @param event The event to handle
     * @return <code>true</code> if the event should be consumed
     */
    public boolean handleKeyEvent(KeyEvent event) {
        boolean keyReleased = event.getEventType() == KeyEvent.KEY_RELEASED;
        switch(event.getCode()) {
            case Y:
                if (event.isControlDown() && !keyReleased) {
                    redo();
                    return true;
                }
                break;
            case Z:
                if (event.isControlDown() && !keyReleased) {
                    undo();
                    return true;
                }
                break;
        }

        return false;
    }
}
