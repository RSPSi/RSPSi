package com.rspsi.editor.tools.integrated;

import com.rspsi.editor.game.save.InvalidMutationException;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.tile.ImportMutation;
import com.rspsi.editor.game.save.tile.OverlayMutation;
import com.rspsi.editor.game.save.tile.snapshot.ImportTileSnapshot;
import com.rspsi.editor.tools.BrushGroup;
import com.rspsi.editor.tools.BrushTool;
import com.rspsi.editor.tools.TileTool;
import com.rspsi.jagex.cache.def.ObjectDefinition;
import com.rspsi.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.SceneTileData;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.misc.ToolType;
import com.rspsi.options.KeyActions;
import com.rspsi.options.KeyBindings;
import com.rspsi.options.Options;
import lombok.val;
import lombok.var;
import org.joml.Vector3i;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class ImportTool extends BrushTool {

    public static String IDENTIFIER = "import_tool";
    private BrushGroup brush;

    /*

            controller.getPasteTilesBtn().setOnAction(evt -> {

                SceneGraph scene = clientInstance.sceneGraph;
                scene.resetTiles();
                Options.currentTool.set(ToolType.IMPORT_SELECTION);

            });

     */


    public ImportTool(UndoRedoSystem undoRedoSystem) {
        super(undoRedoSystem);
    }

    @Override
    public void applyToBrushTile(SceneGraph sceneGraph, SceneTile tile, boolean mouseDown) throws InvalidMutationException {

        this.commitAfterPreserve = false;
        var mapRegion = sceneGraph.getMapRegion();

        int[] brushTiles = brush.getAlpha();
        int plane = tile.worldPos.z;

        var importData = Options.importData;
        if (importData == null) {
            System.out.println("import tool data is null");
            return;
        }
        int lowestPlane = importData.stream().mapToInt(SceneTileData::getZ).min().orElse(4);

        if (mouseDown) {
            //Set tile flags and heights first
            for (SceneTileData data : importData) {
                int savedX = tile.worldPos.x + data.getX();
                int savedY = tile.worldPos.y + data.getY();
                int zPos = tile.worldPos.z + (data.getZ() - lowestPlane);
                if (zPos >= 4) continue;

                int angle = 90 * Options.rotation.get();
                Point2D result = new Point2D.Double();
                AffineTransform rotation = new AffineTransform();
                double angleInRadians = angle * Math.PI / 180;
                rotation.rotate(angleInRadians, tile.worldPos.x, tile.worldPos.y);
                rotation.transform(new Point2D.Double(savedX, savedY), result);
                int xPos = (int) result.getX();
                int yPos = (int) result.getY();

                if (xPos > sceneGraph.width - 1 || xPos < 0 || yPos > sceneGraph.length - 1 || yPos < 0) {
                    continue;
                }




                var targetTile = sceneGraph.getTileOrCreate(new Vector3i(xPos, yPos, zPos));
                val importSnapshot = new ImportTileSnapshot(targetTile.worldPos);
                importSnapshot.preserve(sceneGraph);

                if (data.getOverlayId() != -1) {
                    mapRegion.overlays[zPos][xPos][yPos] = data.getOverlayId();
                    mapRegion.overlayShapes[zPos][xPos][yPos] = data.getOverlayType();
                    mapRegion.overlayOrientations[zPos][xPos][yPos] = (byte) ((data.getOverlayOrientation()
                            - Options.rotation.get()) & 3);
                }

                if (data.getUnderlayId() != -1) {
                    mapRegion.underlays[zPos][xPos][yPos] = data.getUnderlayId();
                }
                if (data.getTileHeight() != -1) {
                    if (zPos == data.getZ()) {
                        mapRegion.tileHeights[zPos][xPos][yPos] = data.getTileHeight();
                    } else if (data.getZ() <= 0) {
                        mapRegion.tileHeights[zPos][xPos][yPos] = mapRegion.tileHeights[zPos - 1][xPos][yPos] + data.getTileHeight();
                    } else {

                        importData.stream()
                                .filter(dataBelow -> dataBelow.getX() == xPos && dataBelow.getY() == yPos && dataBelow.getZ() == zPos - 1)
                                .forEach(dataBelow -> {
                                    mapRegion.tileHeights[zPos][xPos][yPos] = dataBelow.getTileHeight() - data.getTileHeight();
                                });

                    }

                    mapRegion.manualTileHeight[zPos][xPos][yPos] = 1;

                }
                if (data.getTileFlag() != -1) {
                    mapRegion.tileFlags[zPos][xPos][yPos] = data.getTileFlag();
                }

            }

            mapRegion.updateTiles();
            commit();

        } else {
            //System.out.println("Found? " + (Options.importData.stream().anyMatch(data -> data.getGroundDecoration())));
           /* for (SceneTileData data : Options.importData) {
                int savedX = tileX + data.getX();
                int savedY = tileY + data.getY();
                int zPos = Options.currentHeight.get() + (data.getZ() - lowestPlane);

                int angle = 90 * Options.rotation.get();
                Point2D result = new Point2D.Double();
                AffineTransform rotation = new AffineTransform();
                double angleInRadians = angle * Math.PI / 180;

                rotation.rotate(angleInRadians, tileX, tileY);
                rotation.transform(new Point2D.Double(savedX, savedY), result);
                int rotatedXPos = (int) result.getX();

                int rotatedYPos = (int) result.getY();

                if (rotatedXPos > width - 1 || rotatedXPos < 0 || rotatedYPos > length - 1 || rotatedYPos < 0 || zPos >= 4) {
                    continue;
                }

                if (data.getOverlayId() > 0 && data.getOverlayType() >= 0) {
                    this.addTemporaryTile(plane, rotatedXPos, rotatedYPos, data.getOverlayType() + 1, data.getOverlayOrientation() - Options.rotation.get() & 3, -1, 0, 62000);
                } else {
                    highlightTile(rotatedXPos, rotatedYPos, zPos);
                }


                if (data.getGameObjectIds() != null) {
                    for (int i = 0; i < data.getGameObjectIds().length; i++) {
                        ObjectDefinition def = ObjectDefinitionLoader.lookup(data.getGameObjectIds()[i]);
                        if (def.getWidth() > 1 || def.getLength() > 1) {

                            this.addObject(rotatedXPos, rotatedYPos, zPos, data.getGameObjectIds()[i],
                                    data.getGameObjectConfigs()[i] >> 2,
                                    (data.getGameObjectConfigs()[i] & 0xff) - Options.rotation.get() & 3, true);

                        } else {

                            this.addObject(rotatedXPos, rotatedYPos, zPos, data.getGameObjectIds()[i],
                                    data.getGameObjectConfigs()[i] >> 2,
                                    (data.getGameObjectConfigs()[i] & 0xff) - Options.rotation.get() & 3, true);

                        }
                    }
                }
                if (data.getGroundDecoId() != -1) {
                    this.addObject(rotatedXPos, rotatedYPos, zPos, data.getGroundDecoId(), data.getGroundDecoConfig() >> 2,
                            data.getGroundDecoConfig() - Options.rotation.get() & 3, true);
                    System.out.println("CFG: " + (data.getGroundDecoConfig() >> 2) + " : " + (data.getGroundDecoConfig() - Options.rotation.get() & 3) + ": " + data.getGroundDecoId());
                }
                if (data.getWallId() != -1) {
                    this.addObject(rotatedXPos, rotatedYPos, zPos, data.getWallId(), data.getWallConfig() >> 2,
                            data.getWallConfig() - Options.rotation.get() & 3, true);
                    //System.out.println("CFG: " + (data.getWallConfig() >> 2) + " : " + (data.getWallConfig() - Options.rotation.get() & 3) + ": " + data.getWallId());
                }
                if (data.getWallDecoId() != -1) {
                    this.addObject(rotatedXPos, rotatedYPos, zPos, data.getWallDecoId(), data.getWallDecoConfig() >> 2,
                            data.getWallDecoConfig() - Options.rotation.get() & 3, true);
                }

            }*/
        }

    }

    private void highlightTile(int plane, int x, int y, com.rspsi.jagex.map.MapRegion mapRegion) {
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


    public void spawnObjects(SceneGraph sceneGraph, SceneTileData data) {

        Vector3i position = new Vector3i(data.getX(), data.getY(), data.getZ());

        /*if (data.getGameObjectIds() != null) {
            for (int i = 0; i < data.getGameObjectIds().length; i++) {
                var objectId = data.getGameObjectIds()[i];
                var rotation = ((data.getGameObjectConfigs()[i] & 0xff) - (Options.rotation.get())) & 3;
                var type = data.getGameObjectConfigs()[i] >> 2;

                sceneGraph.addObject(position, objectId, type, rotation, false, , );

            }
        }
        if (data.getGroundDecoId() != -1) {

            this.addObject(xPos, yPos, zPos, data.getGroundDecoId(), data.getGroundDecoConfig() >> 2,
                    ((data.getGroundDecoConfig() & 0xff) - (Options.rotation.get())) & 3, false);
        }
        if (data.getWallId() != -1) {
            this.addObject(xPos, yPos, zPos, data.getWallId(), data.getWallConfig() >> 2,
                    ((data.getWallConfig() & 0xff) - (Options.rotation.get())) & 3, false);
        }
        if (data.getWallDecoId() != -1) {
            this.addObject(xPos, yPos, zPos, data.getWallDecoId(), data.getWallDecoConfig() >> 2,
                    ((data.getWallDecoConfig() & 0xff) - (Options.rotation.get())) & 3, false);
        }*/
    }

    @Override
    public String getId() {
        return IDENTIFIER;
    }

    @Override
    public TileMutation newMutation() {
        return new ImportMutation();
    }

    @Override
    public boolean shouldResetTiles() {
        return false;
    }

    public void addTemporaryTile(int plane, int absX, int absY, int i, int rotation, int i1, int i2, int i3) {

    }
}
