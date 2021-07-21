package com.rspsi.editor.tools.integrated;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.tile.OverlayMutation;
import com.rspsi.editor.game.save.tile.snapshot.OverlaySnapshot;
import com.rspsi.editor.tools.BrushTool;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.options.KeyActions;
import com.rspsi.options.KeyBindings;
import com.rspsi.options.Options;
import lombok.var;
import org.joml.Vector3i;

public class PaintOverlayTool extends BrushTool {

    public static String IDENTIFIER = "paint_overlay_tool";

    public PaintOverlayTool(UndoRedoSystem undoRedoSystem) {
        super(undoRedoSystem);
        this.commitAfterPreserve = false;
    }

    @Override
    public void applyToTile(SceneGraph sceneGraph, SceneTile tile, boolean mouseDown) throws InvalidMutationException {
        super.applyToTile(sceneGraph, tile, mouseDown);
        commit();
    }

    @Override
    public void applyToBrushTile(SceneGraph sceneGraph, SceneTile brushTile, boolean mouseDown) throws InvalidMutationException {

        var mapRegion = sceneGraph.getMapRegion();

        int plane = brushTile.worldPos.z;
        int x = brushTile.worldPos.x;
        int y = brushTile.worldPos.y;

        if (mouseDown) {
            preserve(new OverlaySnapshot(brushTile.worldPos));
            if (Options.overlayPaintShapeId.get() == 0 || KeyBindings.actionValid(KeyActions.OVERLAY_REMOVE)) {
                mapRegion.overlays[plane][x][y] = (byte) 0;
            } else {
                if (!KeyBindings.actionValid(KeyActions.OVERLAY_ONLY_PAINT)) {
                    mapRegion.overlays[plane][x][y] = (byte) Options.overlayPaintId
                            .get();
                    mapRegion.overlayShapes[plane][x][y] = (byte) (Options.overlayPaintShapeId
                            .get() - 1);
                    mapRegion.overlayOrientations[plane][x][y] = (byte) Options.rotation.get();
                } else {
                    if (mapRegion.overlays[plane][x][y] > 0) {
                        mapRegion.overlays[plane][x][y] = (byte) Options.overlayPaintId
                                .get();
                    }
                }
            }
        } else {
            if (KeyBindings.actionValid(KeyActions.OVERLAY_REMOVE)) {
                int existing = mapRegion.overlays[plane][x][y];
                int shape = mapRegion.overlayShapes[plane][x][y];
                int rotation = mapRegion.overlayOrientations[plane][x][y];
                if (existing > 0) {
                    this.addTemporaryTile(plane, x, y, shape + 1, rotation, -1, 0, 62000);//TODO Make this reflect the tile colour
                }
            } else if (KeyBindings.actionValid(KeyActions.OVERLAY_ONLY_PAINT)) {
                int existing = mapRegion.overlays[plane][x][y];
                int shape = mapRegion.overlayShapes[plane][x][y];
                int rotation = mapRegion.overlayOrientations[plane][x][y];
                if (existing > 0) {
                    this.addTemporaryTile(plane, x, y, shape + 1, rotation, -1, 0, 9997965);//TODO Make this reflect the tile colour
                }
            } else {
                this.addTemporaryTile(plane, x, y, Options.overlayPaintShapeId.get(), Options.rotation.get(), -1, 0, 9997965);//TODO Make this reflect the tile colour
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
