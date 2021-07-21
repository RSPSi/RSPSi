package com.rspsi.editor.tools;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.misc.BrushType;
import com.rspsi.options.Options;
import lombok.val;
import org.apache.commons.compress.utils.Lists;
import org.joml.Vector3i;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class BrushTool extends TileTool {

    public List<SceneTile> recentlyHighlighted = Lists.newArrayList();

    public BrushGroup currentBrush;

    public BrushTool(UndoRedoSystem undoRedoSystem) {
        super(undoRedoSystem);
    }

    @Override
    public void applyToTile(SceneGraph sceneGraph, SceneTile tile, boolean mouseDown) throws InvalidMutationException {

        for (int x = 0; x < currentBrush.diameter; x++) {
            for (int y = 0; y < currentBrush.diameter; y++) {
                val offsetWorldPos = new Vector3i(tile.worldPos).add(new Vector3i(x, y, 0));
                val brushTile = sceneGraph.tiles.get(offsetWorldPos);
                if(brushTile != null)
                    applyToBrushTile(sceneGraph, brushTile, mouseDown);
            }
        }
    }


    public abstract void applyToBrushTile(SceneGraph sceneGraph, SceneTile brushTile, boolean mouseDown) throws InvalidMutationException;

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean shouldResetTiles() {
        return false;
    }
}
