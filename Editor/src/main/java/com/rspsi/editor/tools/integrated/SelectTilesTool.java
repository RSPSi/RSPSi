package com.rspsi.editor.tools.integrated;

import com.google.common.collect.Maps;
import com.rspsi.editor.MainWindow;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.tile.TileSelectedMutation;
import com.rspsi.editor.tools.TileTool;
import com.rspsi.editor.tools.UserInterfaceSupplier;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.misc.TileArea;
import com.rspsi.options.KeyActions;
import com.rspsi.options.KeyBindings;
import com.rspsi.options.Options;
import com.rspsi.util.ChangeListenerUtil;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import lombok.val;
import org.joml.Vector3i;

import java.util.Map;

public class SelectTilesTool extends TileTool implements UserInterfaceSupplier {

    //TODO Allow for tile selection based on
    private boolean mouseWasDown;
    private SceneTile selectionStart;
    private SceneTile lastHovered;
    private SceneTile lastSelected;

    public Map<Vector3i, SceneTile> tilesBeingSelected = Maps.newConcurrentMap();
    public Map<Vector3i, SceneTile> selectedTiles = Maps.newConcurrentMap();

    public SelectTilesTool(UndoRedoSystem undoRedoSystem) {
        super(undoRedoSystem);
    }

    public void resetTileSelections(SceneGraph sceneGraph) {
        selectedTiles.values().forEach(tile -> {
            tile.tileBeingSelected = false;
            tile.tileSelected = false;
            tile.hasUpdated = true;
        });
    }

    private void resetTilesBeingSelected() {
        tilesBeingSelected.values().forEach(tile -> {
            tile.tileBeingSelected = false;
            tile.tileSelected = false;
            tile.hasUpdated = true;
        });
    }

    @Override
    public void applyToTile(SceneGraph sceneGraph, SceneTile tile, boolean mouseDown) {
        if(tile == lastHovered) {
            return;
        }

        if(!mouseWasDown && mouseDown) {
            resetTilesBeingSelected();
            selectionStart = tile;
            tile.tileBeingSelected = true;

            if(!KeyBindings.actionValid(KeyActions.ADD_TO_SELECTION_TILE) && !KeyBindings.actionValid(KeyActions.ADD_TO_SELECTION_TILE_SINGLE)) {
                resetTileSelections(sceneGraph);
            }
        }

        TileArea tileArea = new TileArea(selectionStart.worldPos.x, selectionStart.worldPos.y, tile.worldPos.x, tile.worldPos.y);


        if(mouseWasDown) {
            if(!mouseDown) {
                tileArea.forEach(worldPos -> {

                    val targetTile = sceneGraph.tiles.get(worldPos);
                    if(targetTile != null) targetTile.tileSelected = true;
                });

            } else {
                resetTilesBeingSelected();
                tileArea.forEach(worldPos -> {
                    val targetTile = sceneGraph.tiles.get(worldPos);
                    if(targetTile != null) targetTile.tileBeingSelected = true;
                });
                lastSelected = tile;
            }
        }

        mouseWasDown = mouseDown;
        lastHovered = tile;
    }


    @Override
    public TileMutation newMutation() {
        return new TileSelectedMutation();
    }

    @Override
    public String getId() {
        return IDENTIFIER;
    }

    @Override
    public boolean shouldResetTiles() {
        return false;
    }

    public static String IDENTIFIER = "select_tiles";

    @Override
    public void setupUI(MainWindow mainWindow) {
        val button = new ToggleButton("S_T");
        button.setStyle("tool-button");
        button.setAccessibleText("Select tiles");
        val controller = mainWindow.getController();
        controller.addToolButton(button, IDENTIFIER);
        ChangeListenerUtil.addListener(true, () -> Options.currentTool.set(IDENTIFIER), button.selectedProperty());
    }

    /*
              controller.getCopySelectedTilesBtn().setOnAction(evt -> {
                if (Options.currentTool.get() == ToolType.SELECT_OBJECT) {
                    SceneGraph.onCycleEnd.add(sceneGraph -> {
                        sceneGraph.copyObjects();
                    });

                    controller.getPasteTilesBtn().setDisable(false);
                } else {
                    copyWindow.show();
                    controller.getPasteTilesBtn().setDisable(false);
                }
            });


     */
}
