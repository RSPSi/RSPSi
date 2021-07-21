package com.rspsi.editor.tools.integrated;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.tile.HeightMutation;
import com.rspsi.editor.game.save.tile.snapshot.FlagSnapshot;
import com.rspsi.editor.tools.BrushTool;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.jagex.util.BitFlag;
import com.rspsi.options.KeyActions;
import com.rspsi.options.KeyBindings;
import com.rspsi.options.Options;

public class ModifyFlagsTool extends BrushTool {

    public static String IDENTIFIER = "modify_height";

    public ModifyFlagsTool(UndoRedoSystem undoRedoSystem) {
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

        /*case SET_FLAGS: {

            brushSelection(brushSize, true, (absX, absY) -> {
                mouseWasDown = true;
                if (currentState.isPresent()) {
                    FlagSnapshot tileState = new FlagSnapshot(absX, absY, plane);
                    tileState.preserve();
                    ((TileMutation<FlagSnapshot>) currentState.get()).storeSnapshot(tileState);
                }
                if (KeyBindings.actionValid(KeyActions.INVERSE_FLAG_SET)) {
                    BitFlag bitFlag = SceneGraph.inverseFlag(new BitFlag(this.getMapRegion().tileFlags[plane][absX][absY]), Options.tileFlags.get());
                    this.getMapRegion().tileFlags[plane][absX][absY] = bitFlag.encode();
                } else {
                    this.getMapRegion().tileFlags[plane][absX][absY] = Options.tileFlags.get().encode();

                }
                this.tiles[plane][absX][absY].hasUpdated = true;

            }, null, null);


            if (!mouseIsDown && mouseWasDown) {
                tileQueue.clear();
                getMapRegion().updateTiles();
                mouseWasDown = false;
            }

        }
        break;*/
    }

    @Override
    public String getId() {
        return IDENTIFIER;
    }

    @Override
    public TileMutation newMutation() {
        return new HeightMutation();
    }

    @Override
    public boolean shouldResetTiles() {
        return false;
    }

    public void addTemporaryTile(int plane, int absX, int absY, int i, int rotation, int i1, int i2, int i3) {

    }
}
