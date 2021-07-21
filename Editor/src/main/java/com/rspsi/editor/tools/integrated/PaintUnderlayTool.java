package com.rspsi.editor.tools.integrated;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.tile.OverlayMutation;
import com.rspsi.editor.game.save.tile.snapshot.OverlaySnapshot;
import com.rspsi.editor.tools.BrushGroup;
import com.rspsi.editor.tools.TileTool;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.options.KeyActions;
import com.rspsi.options.KeyBindings;
import com.rspsi.options.Options;
import lombok.var;
import org.joml.Vector3i;

public class PaintUnderlayTool extends TileTool {

    public static String IDENTIFIER = "paint_overlay_tool";
    public BrushGroup brush;
    public int radius = 1;
    public int diameter = 2;

    public PaintUnderlayTool(UndoRedoSystem undoRedoSystem) {
        super(undoRedoSystem);
    }

    @Override
    public void applyToTile(SceneGraph sceneGraph, SceneTile tile, boolean mouseDown) throws InvalidMutationException {

        var mapRegion = sceneGraph.getMapRegion();


        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                if (brush != null) {
                    if (brush.getAlpha()[x + (y * brush.diameter)] <= 0) {
                        continue;
                    }
                }
                int plane = tile.worldPos.z;
                int absX = tile.worldPos.x + x;
                int absY = tile.worldPos.y + y;
                SceneTile brushTile = sceneGraph.tiles.get(new Vector3i(tile.worldPos).add(x, y, 0));
                if (!mouseDown) {

                    if (KeyBindings.actionValid(KeyActions.OVERLAY_REMOVE)) {
                        int existing = mapRegion.overlays[plane][absX][absY];
                        int shape = mapRegion.overlayShapes[plane][absX][absY];
                        int rotation = mapRegion.overlayOrientations[plane][absX][absY];
                        if (existing > 0) {
                            this.addTemporaryTile(plane, absX, absY, shape + 1, rotation, -1, 0, 62000);//TODO Make this reflect the tile colour
                        }
                    } else if (KeyBindings.actionValid(KeyActions.OVERLAY_ONLY_PAINT)) {
                        int existing = mapRegion.overlays[plane][absX][absY];
                        int shape = mapRegion.overlayShapes[plane][absX][absY];
                        int rotation = mapRegion.overlayOrientations[plane][absX][absY];
                        if (existing > 0) {
                            this.addTemporaryTile(plane, absX, absY, shape + 1, rotation, -1, 0, 9997965);//TODO Make this reflect the tile colour
                        }
                    } else {
                        this.addTemporaryTile(plane, absX, absY, Options.overlayPaintShapeId.get(), Options.rotation.get(), -1, 0, 9997965);//TODO Make this reflect the tile colour
                    }
                } else {
                    preserve(new OverlaySnapshot(new Vector3i(plane, absX, absY)));
                    if (Options.underlayPaintId.get() == -1) {
                        mapRegion.underlays[plane][absX][absY] = (byte) 0;
                    } else {
                        mapRegion.underlays[plane][absX][absY] = (byte) Options.underlayPaintId.get();
                    }
                    brushTile.hasUpdated = true;
                }


            }

        }
    }

    @Override
    public String getId() {
        return IDENTIFIER;
    }

    @Override
    public TileMutation newMutation() {
        return new OverlayMutation();
    }

    @Override
    public boolean shouldResetTiles() {
        return false;
    }

    public void addTemporaryTile(int plane, int absX, int absY, int i, int rotation, int i1, int i2, int i3) {

    }
}
