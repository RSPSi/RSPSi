package com.rspsi.editor.tools.integrated;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.tile.HeightMutation;
import com.rspsi.editor.game.save.tile.OverlayMutation;
import com.rspsi.editor.game.save.tile.snapshot.OverlaySnapshot;
import com.rspsi.editor.tools.BrushTool;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.options.KeyActions;
import com.rspsi.options.KeyBindings;
import com.rspsi.options.Options;
import lombok.var;

public class ModifyHeightTool extends BrushTool {

    public static String IDENTIFIER = "modify_height";

    public ModifyHeightTool(UndoRedoSystem undoRedoSystem) {
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

            /* case MODIFY_HEIGHT:
                    if (!ctrlDown && Config.HEIGHT_SMOOTHING) {
                        brushSize += 1;//For smoothing
                    }
                    final int[][] oldHeights = getMapRegion().tileHeights[plane];
                    brushSelection(brushSize, true,
                            (absX, absY) -> {
                                if (currentState.isPresent() && currentState.get().getType() == StateChangeType.TILE_HEIGHT) {
                                    for (int z = plane; z < 4; z++) {
                                        HeightSnapshot state = new HeightSnapshot(absX, absY, z);
                                        state.preserve();
                                        ((TileMutation<HeightSnapshot>) currentState.get()).storeSnapshot(state);
                                    }
                                }

                                this.getMapRegion().manualTileHeight[plane][absX][absY] = 1;
                                if (KeyboardState.isKeyPressed(KeyCode.SHIFT) && KeyboardState.isKeyPressed(KeyCode.ALT)) {
                                    this.getMapRegion().tileHeights[plane][absX][absY] = -Options.tileHeightLevel.get();
                                    System.out.println("ABS");
                                } else if (KeyboardState.isKeyPressed(KeyCode.SHIFT)) {
                                    this.getMapRegion().tileHeights[plane][absX][absY] += Config.HEIGHT_ADJUST;
                                    for (int z = plane + 1; z < 4; z++) {
                                        this.getMapRegion().tileHeights[z][absX][absY] += Config.HEIGHT_ADJUST;
                                    }
                                } else if (KeyboardState.isKeyPressed(KeyCode.ALT)) {
                                    this.getMapRegion().tileHeights[plane][absX][absY] = (plane > 0 ? !Options.absoluteHeightProperty.get() ? this.getMapRegion().tileHeights[plane - 1][absX][absY] : 0 : 0) - Options.tileHeightLevel.get();

                                } else if (KeyboardState.isKeyPressed(KeyCode.CONTROL)) {
                                    int total = 0;
                                    int count = 0;
                                    for (int xMod = absX - 1; xMod <= absX + 1; xMod++)
                                        for (int yMod = absY - 1; yMod <= absY + 1; yMod++)
                                            if (xMod >= 0 && yMod >= 0 && xMod < this.width && yMod < this.length) {
                                                total += oldHeights[xMod][yMod];
                                                count++;
                                            }
                                    int avg = total / count;
                                    int diff = getMapRegion().tileHeights[plane][absX][absY] - avg;
                                    getMapRegion().tileHeights[plane][absX][absY] = avg;
                                    if (getMapRegion().tileHeights[plane][absX][absY] > 0)
                                        getMapRegion().tileHeights[plane][absX][absY] = 0;
                                    for (int z = plane + 1; z < 4; z++) {
                                        this.getMapRegion().tileHeights[z][absX][absY] -= diff;
                                    }
                                } else {
                                    this.getMapRegion().tileHeights[plane][absX][absY] -= Config.HEIGHT_ADJUST;
                                    for (int z = plane + 1; z < 4; z++) {
                                        this.getMapRegion().tileHeights[z][absX][absY] -= Config.HEIGHT_ADJUST;
                                    }
                                }
                                this.tiles[plane][absX][absY].hasUpdated = true;

                                 // if(this.getMapRegion().tileHeights[plane][absX][absY] < -480) {
                                 // this.getMapRegion().tileHeights[plane][absX][absY] = -480; }


                                if (getMapRegion().tileHeights[plane][absX][absY] > 0)
                                    getMapRegion().tileHeights[plane][absX][absY] = 0;

                                for (int z = 1; z < 4; z++) {
                                    this.tiles[z][absX][absY].hasUpdated = true;
                                    if (this.getMapRegion().tileHeights[z][absX][absY] > this.getMapRegion().tileHeights[z - 1][absX][absY]) {
                                        this.getMapRegion().tileHeights[z][absX][absY] = this.getMapRegion().tileHeights[z - 1][absX][absY];//Not sure on this
                                    } else if (this.getMapRegion().tileHeights[z - 1][absX][absY] < this.getMapRegion().tileHeights[z][absX][absY]) {
                                        this.getMapRegion().tileHeights[z - 1][absX][absY] = this.getMapRegion().tileHeights[z][absX][absY];
                                    }
                                }


                            },
                            (absX, absY) -> {
                                //	if(Options.brushSize.get() == 1 || absX == hoveredTileX - Options.brushSize.get() && absY == hoveredTileY - Options.brushSize.get()){
                                //		this.addTemporaryTile(plane, absX, absY, 3, 3, -1, GameRasterizer.getInstance().getFuchsia(), 62000);
                                //	}
                            }, null);

                    if (!ctrlDown && Config.HEIGHT_SMOOTHING) {
                        brushSize -= 1;
                    }


                    getMapRegion().setHeights();//For beyond edge updates

                    if (!mouseIsDown && mouseWasDown) {
                        tileQueue.clear();
                        System.out.println("MWD");
                        this.shadeObjects(-50, -10, -50);
                        getMapRegion().updateTiles();
                        mouseWasDown = false;
                    }
                    if (mouseIsDown) {
                        mouseWasDown = true;
                        brushSize += 1;
                        this.updateHeights(tileX - brushSize - 3, tileY - brushSize - 3, (brushSize * 2) + 3, (brushSize * 2) + 3);

                    }*/

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
