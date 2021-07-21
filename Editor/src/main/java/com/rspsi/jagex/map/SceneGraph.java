package com.rspsi.jagex.map;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rspsi.editor.game.save.TileMutation;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.game.save.tile.snapshot.OverlaySnapshot;
import com.rspsi.editor.game.save.tile.snapshot.UnderlaySnapshot;
import com.rspsi.editor.tools.ToolNotRegisteredException;
import com.rspsi.editor.tools.ToolRegister;
import com.rspsi.editor.tools.integrated.SelectObjectTool;
import com.rspsi.editor.tools.integrated.SelectTilesTool;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.cache.def.ObjectDefinition;
import com.rspsi.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.rspsi.jagex.chunk.Chunk;
import com.rspsi.jagex.draw.raster.GameRasterizer;
import com.rspsi.jagex.entity.Renderable;
import com.rspsi.jagex.entity.model.Mesh;
import com.rspsi.jagex.entity.model.VertexNormal;
import com.rspsi.jagex.map.object.*;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.jagex.map.tile.ShapedTile;
import com.rspsi.jagex.map.tile.SimpleTile;
import com.rspsi.jagex.map.tile.TileUtils;
import com.rspsi.jagex.util.*;
import com.rspsi.misc.*;
import com.rspsi.options.Options;
import com.rspsi.renderer.Camera;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.joml.Vector3i;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class SceneGraph {

    public final static int PLANE_COUNT = 4;
    static final int[] anIntArray463 = {53, -53, -53, 53};
    static final int[] anIntArray464 = {-53, -53, 53, 53};
    static final int[] anIntArray465 = {-45, 45, 45, -45};
    static final int[] anIntArray466 = {45, 45, -45, -45};
    static final int[] anIntArray478 = {19, 55, 38, 155, 255, 110, 137, 205, 76};
    static final int[] anIntArray479 = {160, 192, 80, 96, 0, 144, 80, 48, 160};
    static final int[] anIntArray480 = {76, 8, 137, 4, 0, 1, 38, 2, 19};
    static final int[] anIntArray481 = {0, 0, 2, 0, 0, 2, 1, 1, 0};
    static final int[] anIntArray482 = {2, 0, 0, 2, 0, 0, 0, 4, 4};
    static final int[] anIntArray483 = {0, 4, 4, 8, 0, 0, 8, 0, 0};
    static final int[] anIntArray484 = {1, 1, 0, 0, 0, 8, 0, 0, 8};
    static final int[] TEXTURE_COLOURS = {41, 39248, 41, 4643, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 43086, 41,
            41, 41, 41, 41, 41, 41, 8602, 41, 28992, 41, 41, 41, 41, 41, 5056, 41, 41, 41, 7079, 41, 41, 41, 41, 41, 41,
            41, 41, 41, 41, 3131, 41, 41, 41};
    public static List<Consumer<SceneGraph>> onCycleEnd = new ArrayList<>();
    public static boolean minimapUpdate;
    public static int clusterCount;
    public static boolean lowMemory = true;
    public static int activePlane;
    static int anInt446;
    static int currentCameraPlane;
    static int currentRenderCycle;
    static int clickX;
    static int clickY;
    static int[][] tileShapeConfig = {new int[16], {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1}, {1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1}, {0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0}, {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1}};
    static int[][] tileShapeRotationIndices = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
            {12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3},
            {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
            {3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12}};
    private static int clickStartY = -1;
    private static int lastSelectedZ = -1;
    public UndoRedoSystem undoRedoSystem;
    public ArrayDeque<SceneTile> tileQueue;
    public int absoluteCameraX;
    public int absoluteCameraY;
    public int length;
    // public SceneTile[][][] tiles;
    public Map<Vector3i, SceneTile> tiles = Maps.newConcurrentMap();
    public int width;
    public Chunk chunk;
    public int offsetX, offsetY;
    boolean[][] aBooleanArrayArray492;
    boolean[][][][] aBooleanArrayArrayArrayArray491;
    SceneCluster[] gameObjectClusters;
    int minViewX;
    int maxViewX;
    int minViewY;
    int maxViewY;
    int ySine;
    int yCosine;
    int xSine;
    int xCosine;
    int anInt493;
    int anInt494;
    int anInt495;
    int anInt496;
    int anInt497;
    int anInt498;
    int[] clusterCounts;
    SceneCluster[][] clusters;
    int zCameraTile;
    GameObject[] interactables;
    int xCameraTile;
    int yCameraTile;
    DefaultWorldObject[] shortLivedGameObjects;//This can probably be removed
    int shortLivedObjectCount;
    int anInt488;
    int[] anIntArray486;
    int[] anIntArray487;
    // int getMapRegion().tileHeights[][][];
    int[][][] anIntArrayArrayArray445;
    int planeCount;


    public SceneGraph(int width, int length, int planes) {
        undoRedoSystem = new UndoRedoSystem(this);
        shortLivedGameObjects = new DefaultWorldObject[5000];
        anIntArray486 = new int[10000];
        anIntArray487 = new int[10000];
        planeCount = planes;
        this.width = width;
        this.length = length;
       /* tiles = new SceneTile[planes][width][length];
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < length; y++) {
                    tiles[z][x][y] = new SceneTile(x, y, z);
                }
            }
        }*/
        anIntArrayArrayArray445 = new int[planes][width + 1][length + 1];

        interactables = new GameObject[100];
        clusters = new SceneCluster[PLANE_COUNT][500];
        clusterCounts = new int[PLANE_COUNT];
        aBooleanArrayArrayArrayArray491 = new boolean[24][32][(Options.renderDistance.get() * 2) + 1][(Options.renderDistance.get() * 2) + 1];
        gameObjectClusters = new SceneCluster[500];
        interactables = new GameObject[100];
        tileQueue = new ArrayDeque<SceneTile>();
        reset();
    }


    private static DefaultWorldObject getTemporaryObject(SceneTile tile, TypeFilter type) {
        val defaultWorldObject = tile.getObject(type, obj -> obj.temporary);
        if (defaultWorldObject != null)
            return defaultWorldObject;

        return null;
    }

    private static boolean shouldShowFlags(int flag) {
        return (Options.showBlockedFlag.get() && (flag & RenderFlags.BLOCKED_TILE.getBit()) == RenderFlags.BLOCKED_TILE.getBit()) ||
                (Options.showBridgeFlag.get() && (flag & RenderFlags.BRIDGE_TILE.getBit()) == RenderFlags.BRIDGE_TILE.getBit()) ||
                (Options.showLowerZFlag.get() && (flag & RenderFlags.RENDER_ON_LOWER_Z.getBit()) != 0) ||
                (Options.showForceLowestPlaneFlag.get() && (flag & RenderFlags.FORCE_LOWEST_PLANE.getBit()) != 0) ||
                (Options.showDisableRenderFlag.get() && (flag & RenderFlags.DISABLE_RENDERING.getBit()) != 0);
    }

    private static BitFlag inverseFlag(BitFlag currentFlag, BitFlag toSet) {
        List<RenderFlags> toRemove = Lists.newArrayList();
        for (RenderFlags flag : RenderFlags.values()) {
            if (toSet.flagged(flag)) {
                if (currentFlag.flagged(flag)) {
                    toRemove.add(flag);
                }
            }
        }

        BitFlag newFlag = new BitFlag(currentFlag, toRemove);
        return newFlag;
    }

    public void setRenderDistance() {
        aBooleanArrayArrayArrayArray491 = new boolean[24][32][(Options.renderDistance.get() * 2) + 1][(Options.renderDistance.get() * 2) + 1];
    }

    public SceneTile getTileOrCreate(Vector3i worldPosition) {
        return tiles.computeIfAbsent(worldPosition, SceneTile::new);
    }

    public void addTile(int x, int y, int plane, int type, int orientation, int underlayColour, int underlayTextureId, int overlayTextureId, int overlayColour, int centreZ, int eastZ,
                        int northEastZ, int northZ, int centreUnderColour, int eastUnderColour, int neUnderColour,
                        int northUnderColour, int centreOverColour, int eastOverColour, int neOverColour, int northOverColour,
                        byte flags) {
        val tile = getTileOrCreate(new Vector3i(x, y, plane));
        boolean flat = centreZ == eastZ && centreZ == northEastZ && centreZ == northZ;
        if (type == 0) {
            SimpleTile simple = new SimpleTile(underlayColour, overlayTextureId, overlayColour, centreUnderColour, eastUnderColour, neUnderColour, northUnderColour,
                    flat);

            tile.shape = null;
            tile.simple = simple;
            tile.tileFlags = flags;
            tile.hasUpdated = true;
        } else if (type == 1) {
            SimpleTile simple = new SimpleTile(underlayColour, overlayTextureId, overlayColour, centreOverColour, eastOverColour, neOverColour, northOverColour,
                    flat);


            tile.shape = null;
            tile.simple = simple;
            tile.tileFlags = flags;
            tile.hasUpdated = true;
        } else {
            ShapedTile shaped = new ShapedTile(type, orientation, x, y,
                    underlayColour, underlayTextureId, overlayColour, overlayTextureId,
                    centreZ, northEastZ, northZ, eastZ,
                    centreOverColour, northOverColour, neOverColour, eastOverColour,
                    northUnderColour, neUnderColour, eastUnderColour, centreUnderColour);
            /*for (int z = plane; z >= 0; z--) {
                if (tiles[z][x][y] == null) {
                    tiles[z][x][y] = new SceneTile(x, y, z);
                }
            }*/
            tile.simple = null;
            tile.shape = shaped;
            tile.tileFlags = flags;
            tile.hasUpdated = true;
        }
    }

    public void addTemporaryTile(int plane, int x, int y, int type, int orientation, int texture, int underlayColour, int textureColour) {

        val tile = getTileOrCreate(new Vector3i(x, y, plane));
        int centreZ = getMapRegion().tileHeights[plane][x][y];
        int eastZ = getMapRegion().tileHeights[plane][x + 1][y];
        int northEastZ = getMapRegion().tileHeights[plane][x + 1][y + 1];
        int northZ = getMapRegion().tileHeights[plane][x][y + 1];
        if (type == 0) {
            SimpleTile simple = new SimpleTile(0, -1, underlayColour, underlayColour, underlayColour, underlayColour, underlayColour,
                    false);
            /*for (int z = plane; z >= 0; z--) {
                if (tiles[z][x][y] == null) {
                    tiles[z][x][y] = new SceneTile(x, y, z);
                }
            }*/
            tile.temporaryShapedTile = null;
            tile.temporarySimpleTile = simple;
        } else if (type == 1) {
            SimpleTile simple = new SimpleTile(0, texture, textureColour, textureColour, textureColour, textureColour, textureColour,
                    centreZ == eastZ && centreZ == northEastZ && centreZ == northZ);

            tile.temporaryShapedTile = null;
            tile.temporarySimpleTile = simple;
        } else {
            ShapedTile shaped = new ShapedTile(type, orientation, x, y, underlayColour, -1, textureColour, texture, centreZ, northEastZ, northZ, eastZ, textureColour, textureColour, textureColour, textureColour, underlayColour,
                    underlayColour, underlayColour, underlayColour);

            tile.temporarySimpleTile = null;
            tile.temporaryShapedTile = shaped;
        }

    }

    public void clearSelectedObjects() throws ToolNotRegisteredException {
        val selectionTool = (SelectObjectTool) ToolRegister.findTool(SelectObjectTool.IDENTIFIER);
        selectionTool.selectedObjects.clear();
    }

    public List<DefaultWorldObject> getSelectedObjects() throws ToolNotRegisteredException {
        val selectionTool = (SelectObjectTool) ToolRegister.findTool(SelectObjectTool.IDENTIFIER);
        return selectionTool.selectedObjects;
    }

    public void deleteObjects() throws ToolNotRegisteredException {
        val selected = getSelectedObjects();
        for (DefaultWorldObject obj : selected) {
            ObjectKey key = obj.getKey();

            obj.setSelected(false);

            SceneTile tile = tiles.get(key.getPosition());
            if (tile != null) {
                tile.remove(key);
            }

        }
        selected.clear();
    }

    private Rectangle getRectPredicate(Predicate<SceneTile> condition) {
        int lowestX = width;
        int lowestY = length;
        int highestX = 0;
        int highestY = 0;
        int plane = Options.currentHeight.get();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                SceneTile tile = this.getTileOrCreate(new Vector3i(x, y, plane));
                if (condition.test(tile)) {
                    if (x < lowestX)
                        lowestX = x;
                    if (y < lowestY)
                        lowestY = y;
                    if (x > highestX)
                        highestX = x;
                    if (y > highestY)
                        highestY = y;
                }
            }
        }

        Rectangle rect = new Rectangle(lowestX, lowestY, highestX - lowestX, highestY - lowestY);
        return rect;
    }

    private Rectangle getSelectedRect() {
        return getRectPredicate(tile -> tile.tileSelected);
    }

    public List<SceneTileData> copyObjects() throws ToolNotRegisteredException {
        val selectedObjects = getSelectedObjects();
        List<SceneTileData> selectedTiles = Lists.newArrayList();
        int minX = width;
        int minY = length;
        for (DefaultWorldObject obj : selectedObjects) {
            ObjectKey key = obj.getKey();

            int x = key.getX();
            int y = key.getY();
            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
        }


        for (DefaultWorldObject obj : selectedObjects) {
            ObjectKey key = obj.getKey();

            int x = key.getX();
            int y = key.getY();
            int z = obj.getPlane();

            SceneTileData sceneTileData = new SceneTileData();

            if (obj.getTypeFilter() == TypeFilter.genericAndRoof) {
                sceneTileData.setGameObjectIds(new int[]{obj.getId()});
                sceneTileData.setGameObjectConfigs(new int[]{obj.getConfig()});
            } else if (obj.getTypeFilter() == TypeFilter.groundDecoration) {
                sceneTileData.setGroundDecoId(obj.getId());
                sceneTileData.setGroundDecoConfig(obj.getConfig());
            } else if (obj.getTypeFilter() == TypeFilter.wallObjects) {
                sceneTileData.setWallId(obj.getId());
                sceneTileData.setWallConfig(obj.getConfig());
            } else if (obj.getTypeFilter() == TypeFilter.wallDecoration) {
                sceneTileData.setWallDecoId(obj.getId());
                sceneTileData.setWallDecoConfig(obj.getConfig());
            }

            sceneTileData.setX(x - minX);
            sceneTileData.setY(y - minY);
            sceneTileData.setZ(z);
            selectedTiles.add(sceneTileData);
            obj.setSelected(false);

        }
        Lists.reverse(selectedTiles);

        return selectedTiles;
    }

    public Collection<SceneTile> getSelectedTiles() throws ToolNotRegisteredException {
        val selectTilesTool = (SelectTilesTool) ToolRegister.findTool(SelectTilesTool.IDENTIFIER);
        return selectTilesTool.selectedTiles.values();
    }

    public List<SceneTileData> copyTiles(CopyOptions copyOptions) throws ToolNotRegisteredException {
        val tilesSelected = getSelectedTiles();
        List<SceneTile> additionalTiles = Lists.newArrayList();

        int minX = width;
        int minY = length;
        for (SceneTile tile : tilesSelected) {

            if (copyOptions.copyTilesAbove()) {
                for (int z = tile.worldPos.z; z < 4; z++) {
                    val tileAbove = tiles.get(new Vector3i(tile.worldPos.x, tile.worldPos.y, z));
                    if (tileAbove != null)
                        additionalTiles.add(tileAbove);
                }
            }
            if (tile.worldPos.x < minX) {
                minX = tile.worldPos.x;
            }
            if (tile.worldPos.y < minY) {
                minY = tile.worldPos.y;
            }

        }


        int fMinY = minY;
        int fMinX = minX;

        List<SceneTileData> data = Stream.
                concat(tilesSelected.stream(), additionalTiles.stream())
                .map(sceneTile -> {
                    int z = sceneTile.worldPos.z;
                    int x = sceneTile.worldPos.x;
                    int y = sceneTile.worldPos.y;
                    SceneTileData sceneTileData = new SceneTileData(copyOptions, sceneTile, x, y);


                    if (copyOptions.copyOverlays()) {
                        sceneTileData.setOverlayId(chunk.mapRegion.overlays[z][x][y]);
                        sceneTileData.setOverlayOrientation(chunk.mapRegion.overlayOrientations[z][x][y]);
                        sceneTileData.setOverlayType(chunk.mapRegion.overlayShapes[z][x][y]);
                    }
                    if (copyOptions.copyUnderlays()) {
                        sceneTileData.setUnderlayId(chunk.mapRegion.underlays[z][x][y]);
                    }
                    if (copyOptions.copyTileHeights()) {
                        sceneTileData.setTileHeight(chunk.mapRegion.tileHeights[z][x][y]);
                    }
                    if (copyOptions.copyTileFlags()) {
                        sceneTileData.setTileFlag(chunk.mapRegion.tileFlags[z][x][y]);
                    }
                    sceneTileData.setX(x - fMinX);
                    sceneTileData.setY(y - fMinY);
                    sceneTileData.setZ(z);
                    return sceneTileData;
                }).collect(Collectors.toList());


        Lists.reverse(data);
        return data;
    }

    public void deleteSelectedTiles(DeleteOptions deleteOptions) throws ToolNotRegisteredException {
        val tilesSelected = getSelectedTiles();
        List<SceneTile> additionalTiles = Lists.newArrayList();

        int minX = width;
        int minY = length;
        for (SceneTile tile : tilesSelected) {

            if (deleteOptions.deleteTilesAbove()) {
                for (int z = tile.worldPos.z; z < 4; z++) {
                    val tileAbove = tiles.get(new Vector3i(tile.worldPos.x, tile.worldPos.y, z));
                    if (tileAbove != null)
                        additionalTiles.add(tileAbove);
                }
            }
        }

        Stream.concat(tilesSelected.stream(), additionalTiles.stream()).forEach(tile -> {
            if (deleteOptions.deleteGameObjects()) {
                tile.remove(TypeFilter.genericAndRoof);
            }
            if (deleteOptions.deleteGroundDecorations()) {
                tile.remove(TypeFilter.groundDecoration);
            }
            if (deleteOptions.deleteWallDecorations()) {
                tile.remove(TypeFilter.wallDecoration);
            }
            if (deleteOptions.deleteWalls()) {
                tile.remove(TypeFilter.wallObjects);
            }

            int z = tile.worldPos.z;
            int y = tile.worldPos.y;
            int x = tile.worldPos.x;

            if (deleteOptions.deleteOverlays()) {
                chunk.mapRegion.overlays[z][x][y] = 0;
                chunk.mapRegion.overlayOrientations[z][x][y] = 0;
                chunk.mapRegion.overlayShapes[z][x][y] = 0;
            }
            if (deleteOptions.deleteUnderlays()) {
                chunk.mapRegion.underlays[z][x][y] = 0;
            }
            if (deleteOptions.deleteTileFlags()) {
                chunk.mapRegion.tileFlags[z][x][y] = 0;
            }
        });

        chunk.mapRegion.updateTiles();
    }

    public void displaceWallDecor(Vector3i worldPos, int displacement) {
        SceneTile tile = tiles.get(worldPos);
        if (tile == null)
            return;

        WallDecoration decoration = (WallDecoration) tile.getObject(TypeFilter.wallDecoration);
        if (decoration == null)
            return;

        //int absX = (x) * 128 + 64;
        //int absY = (y) * 128 + 64;
        int offset = 64 * (displacement / 16);
        decoration.worldTransform.transform(new Vector3f(offset, offset, 0f));
        // decoration.setX(absX + (decoration.getX() - absX) * displacement / 16);
        // decoration.setY(absY + (decoration.getY() - absY) * displacement / 16);
    }

    public void drawMinimapTile(int[] raster, Vector3i worldPos, int scanStart, int scanLength) {
        SceneTile tile = tiles.get(worldPos);
        if (tile == null)
            return;

        SimpleTile simple = tile.simple;
        if (simple != null) {

            int colour = simple.getOverlayColour();
            if (tile.tileSelected) {
                colour = colour >> 8;
            } else if (tile.tileBeingSelected) {
                colour = 0xcc00ff;
            }
            if (Options.hdMap.get() && simple.getCentreColour() != 12345678) {

                int hs = simple.getCentreColour() & ~0x7f;
                int l1 = simple.getNorthColour() & 0x7f;
                int l2 = simple.getNorthEastColour() & 0x7f;
                int l3 = (simple.getCentreColour() & 0x7f) - l1;
                int l4 = (simple.getEastColour() & 0x7f) - l2;
                l1 <<= 2;
                l2 <<= 2;
                for (int k1 = 0; k1 < 4; k1++) {
                    if (!simple.textured) {
                        raster[scanStart] = GameRasterizer.getInstance().colourPalette[hs | (l1 >> 2)];
                        raster[scanStart + 1] = GameRasterizer.getInstance().colourPalette[hs | (l1 * 3 + l2 >> 4)];
                        raster[scanStart + 2] = GameRasterizer.getInstance().colourPalette[hs | (l1 + l2 >> 3)];
                        raster[scanStart + 3] = GameRasterizer.getInstance().colourPalette[hs | (l1 + l2 * 3 >> 4)];
                    } else {
                        int j1 = colour;
                        int lig = 0xff - ((l1 >> 1) * (l1 >> 1) >> 8);
                        raster[scanStart] = ((j1 & 0xff00ff) * lig & ~0xff00ff) + ((j1 & 0xff00) * lig & 0xff0000) >> 8;
                        lig = 0xff - ((l1 * 3 + l2 >> 3) * (l1 * 3 + l2 >> 3) >> 8);
                        raster[scanStart + 1] = ((j1 & 0xff00ff) * lig & ~0xff00ff) + ((j1 & 0xff00) * lig & 0xff0000) >> 8;
                        lig = 0xff - ((l1 + l2 >> 2) * (l1 + l2 >> 2) >> 8);
                        raster[scanStart + 2] = ((j1 & 0xff00ff) * lig & ~0xff00ff) + ((j1 & 0xff00) * lig & 0xff0000) >> 8;
                        lig = 0xff - ((l1 + l2 * 3 >> 3) * (l1 + l2 * 3 >> 3) >> 8);
                        raster[scanStart + 3] = ((j1 & 0xff00ff) * lig & ~0xff00ff) + ((j1 & 0xff00) * lig & 0xff0000) >> 8;
                    }
                    l1 += l3;
                    l2 += l4;
                    scanStart += scanLength;
                }
                return;
            }

            if (colour == 0)
                return;

            for (int times = 0; times < 4; times++) {//Draws a 4x4 tile so minimum minimap size is 256 (64 * 4)
                raster[scanStart] = colour;
                raster[scanStart + 1] = colour;
                raster[scanStart + 2] = colour;
                raster[scanStart + 3] = colour;
                scanStart += scanLength;
            }

            return;
        }

        ShapedTile shaped = tile.shape;
        if (shaped == null)
            return;
        int tileShape = shaped.getTileType();
        int orientation = shaped.orientation;
        int defaultColour = shaped.getUnderlayColour();
        int primaryColour = shaped.getTextureColour();

        if (tile.tileSelected) {
            defaultColour = defaultColour >> 8;
            primaryColour = primaryColour >> 8;
        } else if (tile.tileBeingSelected) {
            defaultColour = 0xcc00ff;
            primaryColour = 0xff00ea;
        }
        int[] primary = tileShapeConfig[tileShape];
        int[] indices = tileShapeRotationIndices[orientation];
        int l2 = 0;

        if (Options.hdMap.get() && shaped.color62 != 12345678) {
            int hs1 = shaped.color62 & ~0x7f;
            int l11 = shaped.color92 & 0x7f;
            int l21 = shaped.color82 & 0x7f;
            int l31 = (shaped.color62 & 0x7f) - l11;
            int l41 = (shaped.color72 & 0x7f) - l21;
            l11 <<= 2;
            l21 <<= 2;
            for (int k1 = 0; k1 < 4; k1++) {
                if (!shaped.textured) {
                    if (primary[indices[l2++]] != 0)
                        raster[scanStart] = GameRasterizer.getInstance().colourPalette[hs1 | (l11 >> 2)];
                    if (primary[indices[l2++]] != 0)
                        raster[scanStart + 1] = GameRasterizer.getInstance().colourPalette[hs1 | (l11 * 3 + l21 >> 4)];
                    if (primary[indices[l2++]] != 0)
                        raster[scanStart + 2] = GameRasterizer.getInstance().colourPalette[hs1 | (l11 + l21 >> 3)];
                    if (primary[indices[l2++]] != 0)
                        raster[scanStart + 3] = GameRasterizer.getInstance().colourPalette[hs1 | (l11 + l21 * 3 >> 4)];
                } else {
                    int j1 = primaryColour;
                    if (primary[indices[l2++]] != 0) {
                        int lig = 0xff - ((l11 >> 1) * (l11 >> 1) >> 8);
                        raster[scanStart] = ((j1 & 0xff00ff) * lig & ~0xff00ff) + ((j1 & 0xff00) * lig & 0xff0000) >> 8;
                    }
                    if (primary[indices[l2++]] != 0) {
                        int lig = 0xff - ((l11 * 3 + l21 >> 3) * (l11 * 3 + l21 >> 3) >> 8);
                        raster[scanStart + 1] = ((j1 & 0xff00ff) * lig & ~0xff00ff) + ((j1 & 0xff00) * lig & 0xff0000) >> 8;
                    }
                    if (primary[indices[l2++]] != 0) {
                        int lig = 0xff - ((l11 + l21 >> 2) * (l11 + l21 >> 2) >> 8);
                        raster[scanStart + 2] = ((j1 & 0xff00ff) * lig & ~0xff00ff) + ((j1 & 0xff00) * lig & 0xff0000) >> 8;
                    }
                    if (primary[indices[l2++]] != 0) {
                        int lig = 0xff - ((l11 + l21 * 3 >> 3) * (l11 + l21 * 3 >> 3) >> 8);
                        raster[scanStart + 3] = ((j1 & 0xff00ff) * lig & ~0xff00ff) + ((j1 & 0xff00) * lig & 0xff0000) >> 8;
                    }
                }
                l11 += l31;
                l21 += l41;
                scanStart += scanLength;
            }
            if (defaultColour != 0 && shaped.color61 != 12345678) {
                scanStart -= 512 << 2;
                l2 -= 16;
                hs1 = shaped.color61 & ~0x7f;
                l11 = shaped.color91 & 0x7f;
                l21 = shaped.color81 & 0x7f;
                l31 = (shaped.color61 & 0x7f) - l11;
                l41 = (shaped.color71 & 0x7f) - l21;
                l11 <<= 2;
                l21 <<= 2;
                for (int k1 = 0; k1 < 4; k1++) {
                    if (primary[indices[l2++]] == 0)
                        raster[scanStart] = GameRasterizer.getInstance().colourPalette[hs1 | (l11 >> 2)];
                    if (primary[indices[l2++]] == 0)
                        raster[scanStart + 1] = GameRasterizer.getInstance().colourPalette[hs1 | (l11 * 3 + l21 >> 4)];
                    if (primary[indices[l2++]] == 0)
                        raster[scanStart + 2] = GameRasterizer.getInstance().colourPalette[hs1 | (l11 + l21 >> 3)];
                    if (primary[indices[l2++]] == 0)
                        raster[scanStart + 3] = GameRasterizer.getInstance().colourPalette[hs1 | (l11 + l21 * 3 >> 4)];
                    l11 += l31;
                    l21 += l41;
                    scanStart += scanLength;
                }
            }
            return;
        }

        if (defaultColour != 0) {
            for (int i3 = 0; i3 < 4; i3++) {
                raster[scanStart] = primary[indices[l2++]] != 0 ? primaryColour : defaultColour;
                raster[scanStart + 1] = primary[indices[l2++]] != 0 ? primaryColour : defaultColour;
                raster[scanStart + 2] = primary[indices[l2++]] != 0 ? primaryColour : defaultColour;
                raster[scanStart + 3] = primary[indices[l2++]] != 0 ? primaryColour : defaultColour;
                scanStart += scanLength;
            }
            return;
        }

        for (int j3 = 0; j3 < 4; j3++) {
            if (primary[indices[l2++]] != 0) {
                raster[scanStart] = primaryColour;
            }
            if (primary[indices[l2++]] != 0) {
                raster[scanStart + 1] = primaryColour;
            }
            if (primary[indices[l2++]] != 0) {
                raster[scanStart + 2] = primaryColour;
            }
            if (primary[indices[l2++]] != 0) {
                raster[scanStart + 3] = primaryColour;
            }
            scanStart += scanLength;
        }
    }

    public List<SceneTileData> exportSelectedTiles(ExportOptions exportOptions, File file)
            throws IOException, ToolNotRegisteredException {
        Collection<SceneTile> selectedTiles = getSelectedTiles();
        List<SceneTile> additionalTiles = Lists.newArrayList();

        int minX = width;
        int minY = length;
        for (SceneTile selectedTile : selectedTiles) {
            if (exportOptions.exportTilesAbove()) {
                for (int z = selectedTile.worldPos.z; z < 4; z++) {
                    val tileAbove = tiles.get(new Vector3i(selectedTile.worldPos.x, selectedTile.worldPos.y, z));
                    if (tileAbove != null) {
                        additionalTiles.add(tileAbove);
                    }
                }
            }


            if (selectedTile.worldPos.x < minX) {
                minX = selectedTile.worldPos.x;
            }
            if (selectedTile.worldPos.y < minY) {
                minY = selectedTile.worldPos.y;
            }
        }


        int fMinY = minY;
        int fMinX = minX;

        List<SceneTileData> sceneTileDataList = Stream
                .concat(selectedTiles.stream(), additionalTiles.stream())
                .map(tile -> {
                    SceneTileData sceneTileData = new SceneTileData(exportOptions, tile, tile.worldPos.x, tile.worldPos.y);

                    int z = tile.worldPos.z;
                    int x = tile.worldPos.x;
                    int y = tile.worldPos.y;

                    if (exportOptions.exportOverlays()) {
                        sceneTileData.setOverlayId(chunk.mapRegion.overlays[z][x][y]);
                        sceneTileData.setOverlayOrientation(chunk.mapRegion.overlayOrientations[z][x][y]);
                        sceneTileData.setOverlayType(chunk.mapRegion.overlayShapes[z][x][y]);
                    }
                    if (exportOptions.exportUnderlays()) {
                        sceneTileData.setUnderlayId(chunk.mapRegion.underlays[z][x][y]);
                    }
                    if (exportOptions.exportTileHeights()) {
                        sceneTileData.setTileHeight(chunk.mapRegion.tileHeights[z][x][y]);
                    }
                    if (exportOptions.exportTileFlags()) {
                        sceneTileData.setTileFlag(chunk.mapRegion.tileFlags[z][x][y]);
                    }
                    sceneTileData.setX(x - fMinX);
                    sceneTileData.setY(y - fMinY);
                    sceneTileData.setZ(z);
                    return sceneTileData;
                })
                .collect(Collectors.toList());


        Lists.reverse(sceneTileDataList);
        ObjectMapper mapper = JsonUtil.getDefaultMapper();
        mapper.writeValue(file, sceneTileDataList);

        return sceneTileDataList;

    }

    public void fill(int plane) {
        //activePlane = plane;
        //TODO

        /*for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                if (tiles[plane][x][y] == null) {
                    tiles[plane][x][y] = new SceneTile(x, y, plane);
                }
            }
        }*/
    }

    public ObjectKey getObjectKey(Vector3i worldPos, TypeFilter typeFilter) {
        SceneTile tile = tiles.get(worldPos);
        if (tile == null)
            return null;
        val defaultWorldObject = tile.getObject(typeFilter);

        return defaultWorldObject != null ? defaultWorldObject.getKey() : null;
    }

    public MapRegion getMapRegion() {
        return chunk.mapRegion;
    }

    public int[] getSurroundingHeights(int plane, int x, int y) {
        int[] heights = new int[9];
        int pos = 0;
        for (int xMod = -1; xMod <= 1; xMod++) {
            for (int yMod = -1; yMod <= 1; yMod++) {
                int absX = x + xMod;
                int absY = y + yMod;

                if (absX < 0 || absY < 0 || absX >= width || absY >= length)
                    continue;
                heights[pos] = getMapRegion().tileHeights[plane][x + xMod][y + yMod];

                pos++;
            }
        }
        return heights;
    }


    public void smoothHeights(int x, int y, int plane) {
        int brushSize = Options.brushSize.get();
        int mapSize = 64;
        int[][] oldHeights = getMapRegion().tileHeights[plane];
        double mod = 0;
        double rSq = brushSize * brushSize;
        for (double yPos = y - brushSize; yPos <= y + brushSize; yPos++) {
            double ySq = (yPos - mod - y) * (yPos - mod - y);
            for (double xPos = x - brushSize; xPos <= x + brushSize; xPos++) {
                double xSq = (xPos - mod - x) * (xPos - mod - x);

                if (Options.brushType.get() == BrushType.RECTANGLE || xSq + ySq <= rSq) {
                    int absX = (int) (xPos + mod);
                    int absY = (int) (yPos + mod);
                    int total = 0;
                    int count = 0;
                    for (int xMod = absX - 1; xMod <= absX + 1; xMod++)
                        for (int yMod = absY - 1; yMod <= absY + 1; yMod++)
                            if (xMod >= 0 && yMod >= 0 && xMod < mapSize && yMod < mapSize) {
                                total += oldHeights[xMod][yMod];
                                count++;
                            }
                    int avg = total / count;
                    getMapRegion().tileHeights[plane][absX][absY] = avg;
                }
            }
        }


    }

    public void updateHeights(double x, double y, double width, double height) {
        updateHeights((int) x, (int) y, (int) width, (int) height);
    }

    /**
     * Updates tile shapes, object render heights and ground contouring for objects that support it
     *
     * @param x      The X position on the map to start
     * @param y      The Y position on the map to start
     * @param width  The width of the area to update
     * @param height The height of the area to update
     */
    public void updateHeights(int x, int y, int width, int height) {
        int mapSize = getMapRegion().tileHeights[0].length - 1;
        for (int tileX = 0; tileX < width; tileX++) {
            for (int tileY = 0; tileY < height; tileY++) {
                for (int z = 3; z >= 0; z--) {
                    int absX = tileX + x;
                    int absY = tileY + y;
                    if (absX >= 0 && absY >= 0 && absX < mapSize && absY < mapSize) {
                        val worldPos = new Vector3i(absX, absY, z);
                        SceneTile selectedTile = tiles.get(worldPos);
                        if (selectedTile != null) {
                            if (selectedTile.shape != null) {
                                ShapedTile shapedTile = selectedTile.shape;
                                int[] heights = this.getSurroundingHeights(z, absX, absY);
                                shapedTile.regenerateHeights(absX, absY, heights[4], heights[5], heights[8],
                                        heights[7], heights[6], heights[3], heights[0], heights[1]);
                            }
                            //selectedTile.attributes = 0;
                            for (DefaultWorldObject obj : selectedTile.getExistingObjects()) {
                                ObjectKey key = obj.getKey();
                                int id = key.getId();

                                ObjectDefinition def = ObjectDefinitionLoader.lookup(id);
                                // if(def.contoursGround()){
                                int type = key.getType();
                                int orientation = obj.getOrientation();
                                if (type == 4 || type == 5) {//XXX was just type 4 before
                                    orientation /= 512;
                                }
                                int centre = getMapRegion().tileHeights[z][absX][absY];
                                int east = getMapRegion().tileHeights[z][absX + 1][absY];
                                int northEast = getMapRegion().tileHeights[z][absX + 1][absY + 1];
                                int north = getMapRegion().tileHeights[z][absX][absY + 1];
                                int mean = centre + east + northEast + north >> 2;
                                /*
                                 * if(type >= 12 && type <= 21) if (orientation == 1) { int tmp = dY; dY =
                                 * cY; cY = bY; bY = aY; aY = tmp; } else if (orientation == 2) { int tmp =
                                 * dY; dY = bY; bY = tmp; tmp = cY; cY = aY; aY = tmp; } else if
                                 * (orientation == 3) { int tmp = dY; dY = aY; aY = bY; bY = cY; cY = tmp; }
                                 *
                                 * int mean = aY + bY + cY + dY >> 2;
                                 */
                                // obj.setZ(mean);
                                /*
                                 * if(type >= 4 && type <= 8){ orientation = 0; type = 4; }
                                 */

                                if (type == 2 && obj.getTypeFilter() == TypeFilter.wallObjects) {
                                    int corner = orientation + 1 & 3;
                                    obj.primary = (def.modelAt(getMapRegion().tileHeights[z], type, 4 + orientation, centre, east,
                                            northEast, north, -1, mean));
                                    obj.secondary = (
                                            def.modelAt(getMapRegion().tileHeights[z], type, corner, centre, east, northEast, north, -1, mean));

                                } else if (type != 11) {
                                    obj.primary = (def.modelAt(getMapRegion().tileHeights[z], type, orientation, centre, east,
                                            northEast, north, -1, mean));
                                }

                                obj.worldTransform.transform(new Vector3f(0f, 0f, mean));//XXX

                                // shadeObjectsOnTile(absX, absY, plane, 64, -50, -10, -50, 768);

                            }
                            // }

                        }
                    }
                }
            }
        }
    }

    public void resetLastHighlightedTiles() {
        tiles.values().forEach(tile -> tile.tileHighlighted = false);
    }

    public void importSelection(File file) throws IOException {

        ObjectMapper mapper = JsonUtil.getDefaultMapper();
        // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Options.importData = mapper.readValue(file, new TypeReference<LinkedList<SceneTileData>>() {
        });
        transformInput();
        //Options.currentTool.set(ToolType.IMPORT_SELECTION);

    }

    public void transformInput() {

    }

    public boolean inCircle(int radius, int centerY, int centerX, int x, int y) {
        int rSq = radius * radius;
        for (int yPos = centerY - radius; yPos <= centerY + radius; yPos++) {
            int ySq = (yPos - centerY) * (yPos - centerY);
            for (int xPos = centerX - radius; xPos <= centerX + radius; xPos++) {
                int xSq = (xPos - centerX) * (xPos - centerX);
                if (xSq + ySq <= rSq) {
                }
            }
        }
        return false;
    }

    public int light(int colour, int light) {
        light = 127 - light;
        light = light * (colour & 0x7f) / 160;

        if (light < 2) {
            light = 2;
        } else if (light > 126) {
            light = 126;
        }

        return (colour & 0xff80) + light;

    }

    ////ModelData.method3343
    private void mergeNormals(Mesh first, Mesh second, int dx, int dy, int dz, boolean flag) {
        first.computeBounds();
        first.calculateNormals();
        second.computeBounds();
        second.calculateNormals();
        anInt488++;
        int count = 0;
        int[] secondX = second.verticesX;
        int secondVertices = second.numVertices;

        for (int vertexA = 0; vertexA < first.numVertices; vertexA++) {
            VertexNormal var10 = first.vertexNormals[vertexA];

            if (var10.magnitude != 0) {
                int y = first.verticesY[vertexA] - dy;
                if (y <= second.minimumY) {
                    int x = first.verticesX[vertexA] - dx;

                    if (x >= second.minimumX && x <= second.maximumX) {
                        int z = first.verticesZ[vertexA] - dz;

                        if (z >= second.minimumZ && z <= second.maximumZ) {
                            for (int vertexB = 0; vertexB < secondVertices; vertexB++) {
                                VertexNormal var15 = second.vertexNormals[vertexB];

                                if (x == secondX[vertexB] && z == second.verticesZ[vertexB]
                                        && y == second.verticesY[vertexB] && var15.magnitude != 0) {

                                    if (first.field1821 == null) {
                                        first.field1821 = new VertexNormal[first.numVertices];
                                    }
                                    if (second.field1821 == null) {
                                        second.field1821 = new VertexNormal[second.numVertices];
                                    }

                                    VertexNormal var16 = first.field1821[vertexA];
                                    if (var16 == null) {
                                        var16 = first.field1821[vertexA] = new VertexNormal();
                                    }
                                    VertexNormal var17 = second.field1821[vertexB];
                                    if (var17 == null) {
                                        var17 = second.field1821[vertexB] = new VertexNormal();
                                    }
                                    var16.position.add(var15.position);
                                    var16.magnitude += var15.magnitude;

                                    var17.position.add(var10.position);
                                    var17.magnitude += var10.magnitude;

                                    count++;
                                    anIntArray486[vertexA] = anInt488;
                                    anIntArray487[vertexB] = anInt488;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (count >= 3 && flag) {
            for (int k1 = 0; k1 < first.numFaces; k1++) {
                if (anIntArray486[first.faceIndicesA[k1]] == anInt488 && anIntArray486[first.faceIndicesB[k1]] == anInt488
                        && anIntArray486[first.faceIndicesC[k1]] == anInt488) {
                    first.calculateFaceTypes();
                    first.faceTypes[k1] = 2;
                }
            }

            for (int l1 = 0; l1 < second.numFaces; l1++) {
                if (anIntArray487[second.faceIndicesA[l1]] == anInt488 && anIntArray487[second.faceIndicesB[l1]] == anInt488
                        && anIntArray487[second.faceIndicesC[l1]] == anInt488) {
                    second.calculateFaceTypes();
                    second.faceTypes[l1] = 2;
                }
            }
        }
    }

    public void method276(int x, int y) {
       /* SceneTile tile = tiles[0][x][y];
        for (int z = 0; z < 3; z++) {
            SceneTile above = tiles[z][x][y] = tiles[z + 1][x][y];

            if (above != null) {
                above.plane--;

                for (int index = 0; index < above.objectCount; index++) {
                    GameObject object = above.gameObjects[index];
                    if ((object.getKey().isSolid()) && object.getX() == x && object.getY() == y) {
                        object.setRenderHeight(object.getRenderHeight() - 1);// XXX?
                    }
                }
            }
        }

        if (tiles[0][x][y] == null) {
            tiles[0][x][y] = new SceneTile(x, y, 0);
        }

        tiles[0][x][y].tileBelow = tile;
        tiles[3][x][y] = null;*/
    }

    public void method277(int plane, int j, int k, int l, int i1, int j1, int l1, int i2) {
        SceneCluster cluster = new SceneCluster();
        cluster.anInt787 = j / 128;
        cluster.anInt788 = l / 128;
        cluster.anInt789 = l1 / 128;
        cluster.anInt790 = i1 / 128;
        cluster.anInt791 = i2;
        cluster.anInt792 = j;
        cluster.anInt793 = l;
        cluster.anInt794 = l1;
        cluster.anInt795 = i1;
        cluster.anInt796 = j1;
        cluster.anInt797 = k;
        clusters[plane][clusterCounts[plane]++] = cluster;
    }

    public void cleanUpShortLivedObjects() {
       /* for (int index = 0; index < shortLivedObjectCount; index++) {
            GameObject object = shortLivedGameObjects[index];
            removeInteractable(object);
            shortLivedGameObjects[index] = null;
        }

        shortLivedObjectCount = 0;*/
    }

    private void method306(Mesh model, int x, int y, int z) {
        if (x < width - 1) {
            val worldPos = new Vector3i(x + 1, y, z);
            SceneTile tile = tiles.get(worldPos);
            if (tile != null) {
                val groundDecoration = tile.getObject(TypeFilter.groundDecoration);
                if (groundDecoration != null && groundDecoration.primary instanceof Mesh)
                    mergeNormals(model, (Mesh) groundDecoration.primary, 128, 0, 0, true);
            }
        }

        if (y < length - 1) {
            val worldPos = new Vector3i(x, y + 1, z);
            SceneTile tile = tiles.get(worldPos);
            if (tile != null) {
                val groundDecoration = tile.getObject(TypeFilter.groundDecoration);
                if (groundDecoration != null && groundDecoration.primary instanceof Mesh)
                    mergeNormals(model, (Mesh) groundDecoration.primary, 0, 0, 128, true);
            }

        }

        if (x < width - 1 && y < length - 1) {
            val worldPos = new Vector3i(x + 1, y + 1, z);
            SceneTile tile = tiles.get(worldPos);
            if (tile != null) {
                val groundDecoration = tile.getObject(TypeFilter.groundDecoration);
                if (groundDecoration != null && groundDecoration.primary instanceof Mesh)
                    mergeNormals(model, (Mesh) groundDecoration.primary, 128, 0, 128, true);
            }

        }

        if (x < width - 1 && y > 0) {
            val worldPos = new Vector3i(x + 1, y - 1, z);
            SceneTile tile = tiles.get(worldPos);
            if (tile != null) {
                val groundDecoration = tile.getObject(TypeFilter.groundDecoration);
                if (groundDecoration != null && groundDecoration.primary instanceof Mesh)
                    mergeNormals(model, (Mesh) groundDecoration.primary, 128, 0, -128, true);
            }
        }
    }

    private void method307(int plane, int sizeX, int sizeY, int startX, int startY, Mesh model) {
        boolean flag = true;
        int initialX = startX;
        int finalX = startX + sizeX;
        int initialY = startY - 1;
        int finalY = startY + sizeY;

        for (int z = plane; z <= plane + 1; z++) {
            if (z != planeCount) {
                for (int x = initialX; x <= finalX; x++) {
                    if (x >= 0 && x < width) {
                        for (int y = initialY; y <= finalY; y++) {
                            if (y >= 0 && y < length && (!flag || x >= finalX || y >= finalY || y < startY && x != startX)) {
                                SceneTile tile = tiles.get(new Vector3i(x, y, z));
                                if (tile != null) {
                                    int averageTileHeight = (getMapRegion().tileHeights[z][x][y]
                                            + getMapRegion().tileHeights[z][x + 1][y]
                                            + getMapRegion().tileHeights[z][x][y + 1]
                                            + getMapRegion().tileHeights[z][x + 1][y + 1]) / 4
                                            - (getMapRegion().tileHeights[plane][startX][startY]
                                            + getMapRegion().tileHeights[plane][startX + 1][startY]
                                            + getMapRegion().tileHeights[plane][startX][startY + 1]
                                            + getMapRegion().tileHeights[plane][startX + 1][startY + 1]) / 4;

                                    val worldObject = tile.getObject(TypeFilter.wallObjects);


                                    if (worldObject instanceof Wall) {
                                        int dy = (y - startY) * 128 + (1 - sizeY) * 64;
                                        int dx = (x - startX) * 128 + (1 - sizeX) * 64;

                                        Wall wall = (Wall) worldObject;

                                        if (wall.primary instanceof Mesh)
                                            mergeNormals(model, (Mesh) wall.primary, dx,
                                                    averageTileHeight, dy, flag);

                                        if (wall.secondary instanceof Mesh)
                                            mergeNormals(model, (Mesh) wall.secondary, dx,
                                                    averageTileHeight, dy, flag);

                                    }


                                    boolean finalFlag = flag;
                                    tile.getObjectsStream(TypeFilter.genericAndRoof)
                                            .filter(GameObject.class::isInstance)
                                            .map(GameObject.class::cast)
                                            .forEach(gameObject -> {
                                                if (gameObject.primary instanceof Mesh) {
                                                    int k3 = gameObject.maximum.x - gameObject.getX() + 1;
                                                    int l3 = gameObject.maximum.y - gameObject.getY() + 1;
                                                    mergeNormals(model, (Mesh) gameObject.primary,
                                                            (gameObject.getX() - startX) * 128 + (k3 - sizeX) * 64, averageTileHeight,
                                                            (gameObject.getY() - startY) * 128 + (l3 - sizeY) * 64, finalFlag);
                                                }
                                            });

                                }
                            }
                        }
                    }
                }
                initialX--;
                flag = false;
            }
        }
    }

    public void method310(int i, int j, int k, int l, int[] ai) {
        anInt495 = 0;
        anInt496 = 0;
        anInt497 = k;
        anInt498 = l;
        anInt493 = k / 2;
        anInt494 = l / 2;
        boolean[][][][] aflag = new boolean[13][32][(Options.renderDistance.get() * 2) + 3][(Options.renderDistance.get() * 2) + 3];

        for (int i1 = 0; i1 <= 384; i1 += 32) {
            for (int j1 = 0; j1 < 2048; j1 += 64) {
                ySine = Constants.SINE[i1];
                yCosine = Constants.COSINE[i1];
                xSine = Constants.SINE[j1];
                xCosine = Constants.COSINE[j1];
                int l1 = i1 / 32;
                int j2 = j1 / 64;
                for (int l2 = -(Options.renderDistance.get()) - 1; l2 <= (Options.renderDistance.get()) + 1; l2++) {
                    for (int j3 = -(Options.renderDistance.get()) - 1; j3 <= (Options.renderDistance.get()) + 1; j3++) {
                        int k3 = l2 * 128;
                        int i4 = j3 * 128;
                        boolean flag2 = false;
                        for (int k4 = -i; k4 <= j; k4 += 128) {
                            if (!method311(ai[l1] + k4, i4, k3)) {
                                continue;
                            }
                            flag2 = true;
                            break;
                        }
                        aflag[l1][j2][l2 + Options.renderDistance.get() + 1][j3 + Options.renderDistance.get() + 1] = flag2;
                    }
                }
            }
        }

        for (int k1 = 0; k1 < 12; k1++) {
            for (int i2 = 0; i2 < 32; i2++) {
                for (int k2 = -Options.renderDistance.get(); k2 < Options.renderDistance.get(); k2++) {
                    for (int i3 = -Options.renderDistance.get(); i3 < Options.renderDistance.get(); i3++) {
                        boolean flag1 = false;
                        label0:
                        for (int l3 = -1; l3 <= 1; l3++) {
                            for (int j4 = -1; j4 <= 1; j4++) {
                                if (aflag[k1][i2][k2 + l3 + Options.renderDistance.get() + 1][i3 + j4 + Options.renderDistance.get() + 1]) {
                                    flag1 = true;
                                } else if (aflag[k1][(i2 + 1) % 31][k2 + l3 + Options.renderDistance.get() + 1][i3 + j4 + Options.renderDistance.get() + 1]) {
                                    flag1 = true;
                                } else if (aflag[k1 + 1][i2][k2 + l3 + Options.renderDistance.get() + 1][i3 + j4 + Options.renderDistance.get() + 1]) {
                                    flag1 = true;
                                } else {
                                    if (!aflag[k1 + 1][(i2 + 1) % 31][k2 + l3 + Options.renderDistance.get() + 1][i3 + j4 + Options.renderDistance.get() + 1]) {
                                        continue;
                                    }
                                    flag1 = true;
                                }
                                break label0;
                            }
                        }
                        aBooleanArrayArrayArrayArray491[k1][i2][k2 + Options.renderDistance.get()][i3 + Options.renderDistance.get()] = flag1;
                    }
                }
            }
        }
    }

    public boolean method311(int i, int j, int k) {
        int l = j * xSine + k * xCosine >> 16;
        int i1 = j * xCosine - k * xSine >> 16;
        int j1 = i * ySine + i1 * yCosine >> 16;
        int k1 = i * yCosine - i1 * ySine >> 16;
        if (j1 < 50 || j1 > 3500)
            return false;
        int l1 = anInt493 + (l << 9) / j1;
        int i2 = anInt494 + (k1 << 9) / j1;
        return l1 >= anInt495 && l1 <= anInt497 && i2 >= anInt496 && i2 <= anInt498;
    }

    public void resetUpdates() {

    }

    public void renderScene(int cameraTileX, int cameraTileY, int xCameraCurve, int cameraTileZ, int cameraPlane, int yCameraCurve) {
		/*if (!sceneVisible(cameraTileX, cameraTileY))
			return;*/
        xCameraTile = cameraTileX;
        zCameraTile = cameraTileZ;
        yCameraTile = cameraTileY;
        absoluteCameraX = cameraTileX / 128;
        absoluteCameraY = cameraTileY / 128;
        minViewX = absoluteCameraX - Options.renderDistance.get();
        minViewY = absoluteCameraY - Options.renderDistance.get();
        maxViewX = absoluteCameraX + Options.renderDistance.get();
        maxViewY = absoluteCameraY + Options.renderDistance.get();

        boolean chunkHasTiles = false;

        for (int x = minViewX; x < maxViewX; x++) {
            for (int y = minViewY; y < maxViewY; y++) {
                if (x >= chunk.offsetX && x < chunk.offsetX + 64 && y >= chunk.offsetY && y < chunk.offsetY + 64) {
                    chunkHasTiles = true;
                    break;
                }
            }
        }

        if (!chunkHasTiles) {
            //System.out.println("Skipped chunk");
            return;
        }


        int cameraX = cameraTileX / 128 - offsetX;
        int cameraY = cameraTileY / 128 - offsetY;

        if (yCameraCurve < 0) {
            yCameraCurve = 0;
        }

        currentRenderCycle++;

        ySine = Constants.SINE[yCameraCurve];
        yCosine = Constants.COSINE[yCameraCurve];
        xSine = Constants.SINE[xCameraCurve];
        xCosine = Constants.COSINE[xCameraCurve];

        aBooleanArrayArray492 = aBooleanArrayArrayArrayArray491[yCameraCurve / 32][xCameraCurve / 64];


        currentCameraPlane = cameraPlane;

        long start = System.currentTimeMillis();

        /*
         * if (minViewX < 0) { minViewX = 0; }
         *
         * if (minViewY < 0) { minViewY = 0; } if (maxViewX > 64) { maxViewX = 64; } if
         * (maxViewY > 64) { maxViewY = 64; }
         */

        method319();
        anInt446 = 0;

        for (int z = activePlane; z < planeCount; z++) {

            for (int x = minViewX; x < maxViewX; x++) {
                for (int y = minViewY; y < maxViewY; y++) {
                    if (inChunk(x, y)) {
                        val worldPos = new Vector3i(x, y, z);
                        SceneTile tile = tiles.get(worldPos);
                        if (tile != null) {
                            if (tile.collisionPlane > cameraPlane || !aBooleanArrayArray492[x - minViewX][y - minViewY]
                                    && chunk.mapRegion.tileHeights[z][x][y] - cameraTileZ < 50) {
                                tile.needsRendering = false;
                                tile.aBoolean1323 = false;
                                tile.anInt1325 = 0;
                            } else {
                                tile.needsRendering = true;
                                tile.aBoolean1323 = true;
                                tile.hasObjects = tile.objectCount(TypeFilter.noFilter) > 0;
                                anInt446++;
                            }
                        }
                    }
                }
            }
        }
        boolean flag = true;
        for (int loop = 0; loop < 2; loop++) {

            for (int z = activePlane; z < planeCount; z++) {
                for (int dx = -Options.renderDistance.get(); dx <= 0; dx++) {
                    int tileXNeg = absoluteCameraX + dx;
                    int tileXPos = absoluteCameraX - dx;
                    if (tileXNeg >= minViewX || tileXPos < maxViewX) {
                        for (int dy = -Options.renderDistance.get(); dy <= 0; dy++) {
                            int tileYNeg = absoluteCameraY + dy;
                            int tileYPos = absoluteCameraY - dy;

                            if (inChunk(tileXNeg, tileYNeg)) {
                                val worldPos = new Vector3i(toChunkTileX(tileXNeg), toChunkTileY(tileYNeg), z);
                                SceneTile tile = tiles.get(worldPos);
                                if (tile != null && tile.needsRendering) {
                                    renderTile(tile, flag);
                                }
                            }

                            if (inChunk(tileXNeg, tileYPos)) {
                                val worldPos = new Vector3i(toChunkTileX(tileXNeg), toChunkTileY(tileYPos), z);
                                SceneTile tile = tiles.get(worldPos);
                                if (tile != null && tile.needsRendering) {
                                    renderTile(tile, flag);
                                }
                            }

                            if (inChunk(tileXPos, tileYNeg)) {

                                val worldPos = new Vector3i(toChunkTileX(tileXPos), toChunkTileY(tileYNeg), z);
                                SceneTile tile = tiles.get(worldPos);
                                if (tile != null && tile.needsRendering) {
                                    renderTile(tile, flag);
                                }
                            }

                            if (inChunk(tileXPos, tileYPos)) {
                                val worldPos = new Vector3i(toChunkTileX(tileXPos), toChunkTileY(tileYPos), z);
                                SceneTile tile = tiles.get(worldPos);
                                if (tile != null && tile.needsRendering) {
                                    renderTile(tile, flag);
                                }
                            }

                            //if (anInt446 == 0)
                            // clicked = false;
                            //return;
                        }
                    }
                }
            }


            flag = false;
        }

        val z = Options.currentHeight.get();
        for (int dx = -Options.renderDistance.get(); dx <= 0; dx++) {
            int tileXNeg = absoluteCameraX + dx;
            int tileXPos = absoluteCameraX - dx;
            if (tileXNeg >= minViewX || tileXPos < maxViewX) {
                for (int dy = -Options.renderDistance.get(); dy <= 0; dy++) {
                    int tileYNeg = absoluteCameraY + dy;
                    int tileYPos = absoluteCameraY - dy;

                    if (inChunk(tileXNeg, tileYNeg)) {

                        val worldPos = new Vector3i(toChunkTileX(tileXNeg), toChunkTileY(tileYNeg), z);
                        SceneTile tile = tiles.get(worldPos);
                        if (tile != null) {
                            renderAfterCycle(tile);
                        }
                    }

                    if (inChunk(tileXNeg, tileYPos)) {
                        val worldPos = new Vector3i(toChunkTileX(tileXNeg), toChunkTileY(tileYPos), z);
                        SceneTile tile = tiles.get(worldPos);
                        if (tile != null) {
                            renderAfterCycle(tile);
                        }
                    }

                    if (inChunk(tileXPos, tileYNeg)) {

                        val worldPos = new Vector3i(toChunkTileX(tileXPos), toChunkTileY(tileYNeg), z);
                        SceneTile tile = tiles.get(worldPos);
                        if (tile != null) {
                            renderAfterCycle(tile);
                        }
                    }

                    if (inChunk(tileXPos, tileYPos)) {
                        val worldPos = new Vector3i(toChunkTileX(tileXPos), toChunkTileY(tileYPos), z);
                        SceneTile tile = tiles.get(worldPos);
                        if (tile != null) {
                            renderAfterCycle(tile);
                        }
                    }

                    //if (anInt446 == 0)
                    // clicked = false;
                    //return;
                }
            }
        }
        // clicked = false;
    }

    public void renderAfterCycle(@NonNull SceneTile activeTile) {
        Vector2 screenPos = Client.getSingleton().getScreenPos(activeTile.worldPos.x * 128 + 64, activeTile.worldPos.y * 128 + 64, 64);

        if (screenPos.getX() > 0 && screenPos.getY() > 0) {
            if (Options.showMinimapFunctionModels.get()) {
                val worldObject = activeTile.getObject(TypeFilter.groundDecoration);
                if (worldObject instanceof GroundDecoration) {
                    GroundDecoration decor = (GroundDecoration) worldObject;
                    if (decor.getMinimapFunction() != null) {
                        decor.getMinimapFunction().drawSprite((int) screenPos.getX(), (int) screenPos.getY());

                    }
                }
            }
            if (Options.showUnderlayNumbers.get()) {
                try {
                    int underlayId = getMapRegion().underlays[activeTile.worldPos.z][activeTile.worldPos.x][activeTile.worldPos.y] - 1;
                    if (underlayId > 0 && screenPos.getX() > 0 && screenPos.getY() > 0)
                        Client.getSingleton().robotoFont.drawString("" + underlayId, (int) screenPos.getX(), (int) screenPos.getY(), 0xffff00);
                } catch (Exception e) {
                }
            }
            if (Options.showOverlayNumbers.get()) {
                try {
                    int overlayId = getMapRegion().overlays[activeTile.worldPos.z][activeTile.worldPos.x][activeTile.worldPos.y] - 1;
                    if (overlayId > 0 && screenPos.getX() > 0 && screenPos.getY() > 0)
                        Client.getSingleton().robotoFont.drawString("" + overlayId, (int) screenPos.getX(), (int) screenPos.getY(), 0xffff00);
                } catch (Exception e) {
                }
            }

            if (Options.showTileHeightNumbers.get()) {
                try {
                    int tileHeight = getMapRegion().tileHeights[activeTile.worldPos.z][activeTile.worldPos.x][activeTile.worldPos.y];
                    if (activeTile.worldPos.z > 0 && !Options.absoluteHeightProperty.get())
                        tileHeight -= getMapRegion().tileHeights[activeTile.worldPos.z - 1][activeTile.worldPos.x][activeTile.worldPos.y];
                    if (screenPos.getX() > 0 && screenPos.getY() > 0)
                        Client.getSingleton().robotoFont.drawString("" + (-tileHeight), (int) screenPos.getX(), (int) screenPos.getY(), 0xffff00);
                } catch (Exception e) {
                }

            }
        }
    }

    private boolean inChunk(int x, int y) {
        // TODO Auto-generated method stub
        return x >= 0 && x < width && y >= 0 && y < length;
    }

    private void method319() {
        int j = clusterCounts[currentCameraPlane];
        SceneCluster[] clusters = this.clusters[currentCameraPlane];
        clusterCount = 0;
        for (int k = 0; k < j; k++) {
            SceneCluster cluster = clusters[k];
            if (cluster.anInt791 == 1) {
                int l = cluster.anInt787 - absoluteCameraX + Options.renderDistance.get();
                if (l < 0 || l > 50) {
                    continue;
                }
                int k1 = cluster.anInt789 - absoluteCameraY + Options.renderDistance.get();
                if (k1 < 0) {
                    k1 = 0;
                }
                int j2 = cluster.anInt790 - absoluteCameraY + Options.renderDistance.get();
                if (j2 > 50) {
                    j2 = 50;
                }
                boolean flag = false;
                while (k1 <= j2) {
                    if (aBooleanArrayArray492[l][k1++]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    continue;
                }
                int j3 = xCameraTile - cluster.anInt792;
                if (j3 > 32) {
                    cluster.anInt798 = 1;
                } else {
                    if (j3 >= -32) {
                        continue;
                    }
                    cluster.anInt798 = 2;
                    j3 = -j3;
                }
                cluster.anInt801 = (cluster.anInt794 - yCameraTile << 8) / j3;
                cluster.anInt802 = (cluster.anInt795 - yCameraTile << 8) / j3;
                cluster.anInt803 = (cluster.anInt796 - zCameraTile << 8) / j3;
                cluster.anInt804 = (cluster.anInt797 - zCameraTile << 8) / j3;
                gameObjectClusters[clusterCount++] = cluster;
                continue;
            }
            if (cluster.anInt791 == 2) {
                int i1 = cluster.anInt789 - absoluteCameraY + Options.renderDistance.get();
                if (i1 < 0 || i1 > 50) {
                    continue;
                }
                int l1 = cluster.anInt787 - absoluteCameraX + Options.renderDistance.get();
                if (l1 < 0) {
                    l1 = 0;
                }
                int k2 = cluster.anInt788 - absoluteCameraX + Options.renderDistance.get();
                if (k2 > 50) {
                    k2 = 50;
                }
                boolean flag1 = false;
                while (l1 <= k2) {
                    if (aBooleanArrayArray492[l1++][i1]) {
                        flag1 = true;
                        break;
                    }
                }
                if (!flag1) {
                    continue;
                }
                int k3 = yCameraTile - cluster.anInt794;
                if (k3 > 32) {
                    cluster.anInt798 = 3;
                } else {
                    if (k3 >= -32) {
                        continue;
                    }
                    cluster.anInt798 = 4;
                    k3 = -k3;
                }
                cluster.anInt799 = (cluster.anInt792 - xCameraTile << 8) / k3;
                cluster.anInt800 = (cluster.anInt793 - xCameraTile << 8) / k3;
                cluster.anInt803 = (cluster.anInt796 - zCameraTile << 8) / k3;
                cluster.anInt804 = (cluster.anInt797 - zCameraTile << 8) / k3;
                gameObjectClusters[clusterCount++] = cluster;
            } else if (cluster.anInt791 == 4) {
                int j1 = cluster.anInt796 - zCameraTile;
                if (j1 > 128) {
                    int i2 = cluster.anInt789 - absoluteCameraY + Options.renderDistance.get();
                    if (i2 < 0) {
                        i2 = 0;
                    }
                    int l2 = cluster.anInt790 - absoluteCameraY + Options.renderDistance.get();
                    if (l2 > 50) {
                        l2 = 50;
                    }
                    if (i2 <= l2) {
                        int i3 = cluster.anInt787 - absoluteCameraX + Options.renderDistance.get();
                        if (i3 < 0) {
                            i3 = 0;
                        }
                        int l3 = cluster.anInt788 - absoluteCameraX + Options.renderDistance.get();
                        if (l3 > 50) {
                            l3 = 50;
                        }
                        boolean flag2 = false;
                        label0:
                        for (int i4 = i3; i4 <= l3; i4++) {
                            for (int j4 = i2; j4 <= l2; j4++) {
                                if (!aBooleanArrayArray492[i4][j4]) {
                                    continue;
                                }
                                flag2 = true;
                                break label0;
                            }

                        }

                        if (flag2) {
                            cluster.anInt798 = 5;
                            cluster.anInt799 = (cluster.anInt792 - xCameraTile << 8) / j1;
                            cluster.anInt800 = (cluster.anInt793 - xCameraTile << 8) / j1;
                            cluster.anInt801 = (cluster.anInt794 - yCameraTile << 8) / j1;
                            cluster.anInt802 = (cluster.anInt795 - yCameraTile << 8) / j1;
                            gameObjectClusters[clusterCount++] = cluster;
                        }
                    }
                }
            }
        }
    }

    private boolean method320(int x, int y, int z) {
        int l = anIntArrayArrayArray445[z][x][y];
        if (l == -currentRenderCycle)
            return false;
        else if (l == currentRenderCycle)
            return true;

        int worldX = x << 7;
        int worldY = y << 7;

        if (method324(worldX + 1, worldY + 1, getMapRegion().tileHeights[z][x][y])
                && method324(worldX + 128 - 1, worldY + 1, getMapRegion().tileHeights[z][x + 1][y])
                && method324(worldX + 128 - 1, worldY + 128 - 1, getMapRegion().tileHeights[z][x + 1][y + 1])
                && method324(worldX + 1, worldY + 128 - 1, getMapRegion().tileHeights[z][x][y + 1])) {
            anIntArrayArrayArray445[z][x][y] = currentRenderCycle;
            return true;
        }

        anIntArrayArrayArray445[z][x][y] = -currentRenderCycle;
        return false;
    }

    private boolean method321(int x, int y, int z, int l) {
        if (!method320(x, y, z))
            return false;

        int worldX = x << 7;
        int worldY = y << 7;
        int k1 = getMapRegion().tileHeights[z][x][y] - 1;
        int l1 = k1 - 120;
        int i2 = k1 - 230;
        int j2 = k1 - 238;

        if (l < 16) {
            if (l == 1) {
                if (worldX > xCameraTile) {
                    if (!method324(worldX, worldY, k1))
                        return false;
                    else if (!method324(worldX, worldY + 128, k1))
                        return false;
                }

                if (z > 0) {
                    if (!method324(worldX, worldY, l1))
                        return false;
                    else if (!method324(worldX, worldY + 128, l1))
                        return false;
                }
                if (!method324(worldX, worldY, i2))
                    return false;
                return method324(worldX, worldY + 128, i2);
            }
            if (l == 2) {
                if (worldY < yCameraTile) {
                    if (!method324(worldX, worldY + 128, k1))
                        return false;
                    else if (!method324(worldX + 128, worldY + 128, k1))
                        return false;
                }

                if (z > 0) {
                    if (!method324(worldX, worldY + 128, l1))
                        return false;
                    else if (!method324(worldX + 128, worldY + 128, l1))
                        return false;
                }
                if (!method324(worldX, worldY + 128, i2))
                    return false;
                return method324(worldX + 128, worldY + 128, i2);
            }
            if (l == 4) {
                if (worldX < xCameraTile) {
                    if (!method324(worldX + 128, worldY, k1))
                        return false;
                    else if (!method324(worldX + 128, worldY + 128, k1))
                        return false;
                }

                if (z > 0) {
                    if (!method324(worldX + 128, worldY, l1))
                        return false;
                    else if (!method324(worldX + 128, worldY + 128, l1))
                        return false;
                }
                if (!method324(worldX + 128, worldY, i2))
                    return false;
                return method324(worldX + 128, worldY + 128, i2);
            }
            if (l == 8) {
                if (worldY > yCameraTile) {
                    if (!method324(worldX, worldY, k1))
                        return false;
                    else if (!method324(worldX + 128, worldY, k1))
                        return false;
                }

                if (z > 0) {
                    if (!method324(worldX, worldY, l1))
                        return false;
                    else if (!method324(worldX + 128, worldY, l1))
                        return false;
                }
                if (!method324(worldX, worldY, i2))
                    return false;

                return method324(worldX + 128, worldY, i2);
            }
        }

        if (!method324(worldX + 64, worldY + 64, j2))
            return false;
        else if (l == 16)
            return method324(worldX, worldY + 128, i2);
        else if (l == 32)
            return method324(worldX + 128, worldY + 128, i2);
        else if (l == 64)
            return method324(worldX + 128, worldY, i2);
        else if (l == 128)
            return method324(worldX, worldY, i2);
        System.out.println("Warning unsupported wall type");
        return true;
    }

    private boolean method322(int plane, int x, int y, int l) {
        if (!method320(x, y, plane))
            return false;

        int absoluteX = x << 7;
        int absoluteY = y << 7;
        return method324(absoluteX + 1, absoluteY + 1, getMapRegion().tileHeights[plane][x][y] - l)
                && method324(absoluteX + 128 - 1, absoluteY + 1, getMapRegion().tileHeights[plane][x + 1][y] - l)
                && method324(absoluteX + 128 - 1, absoluteY + 128 - 1,
                getMapRegion().tileHeights[plane][x + 1][y + 1] - l)
                && method324(absoluteX + 1, absoluteY + 128 - 1, getMapRegion().tileHeights[plane][x][y + 1] - l);
    }

    private boolean method323(int plane, int minX, int maxX, int minY, int maxY, int j1) {
        if (minX == maxX && minY == maxY) {
            if (!method320(minX, minY, plane))
                return false;

            int worldX = minX << 7;
            int worldY = minY << 7;

            return method324(worldX + 1, worldY + 1, getMapRegion().tileHeights[plane][minX][minY] - j1)
                    && method324(worldX + 128 - 1, worldY + 1, getMapRegion().tileHeights[plane][minX + 1][minY] - j1)
                    && method324(worldX + 128 - 1, worldY + 128 - 1,
                    getMapRegion().tileHeights[plane][minX + 1][minY + 1] - j1)
                    && method324(worldX + 1, worldY + 128 - 1, getMapRegion().tileHeights[plane][minX][minY + 1] - j1);
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (anIntArrayArrayArray445[plane][x][y] == -currentRenderCycle)
                    return false;
            }
        }

        int minWorldX = (minX << 7) + 1;
        int minWorldY = (minY << 7) + 2;
        int i3 = getMapRegion().tileHeights[plane][minX][minY] - j1;
        if (!method324(minWorldX, minWorldY, i3))
            return false;

        int maxWorldX = (maxX << 7) - 1;
        if (!method324(maxWorldX, minWorldY, i3))
            return false;

        int maxWorldY = (maxY << 7) - 1;
        if (!method324(minWorldX, maxWorldY, i3))
            return false;

        return method324(maxWorldX, maxWorldY, i3);
    }

    private boolean method324(int worldX, int worldY, int j) {
        for (int l = 0; l < clusterCount; l++) {
            SceneCluster cluster = gameObjectClusters[l];

            if (cluster.anInt798 == 1) {
                int dx = cluster.anInt792 - worldX;

                if (dx > 0) {
                    int j2 = cluster.anInt794 + (cluster.anInt801 * dx >> 8);
                    int k3 = cluster.anInt795 + (cluster.anInt802 * dx >> 8);
                    int l4 = cluster.anInt796 + (cluster.anInt803 * dx >> 8);
                    int i6 = cluster.anInt797 + (cluster.anInt804 * dx >> 8);

                    if (worldY >= j2 && worldY <= k3 && j >= l4 && j <= i6)
                        return true;
                }
            } else if (cluster.anInt798 == 2) {
                int dx = worldX - cluster.anInt792;

                if (dx > 0) {
                    int k2 = cluster.anInt794 + (cluster.anInt801 * dx >> 8);
                    int l3 = cluster.anInt795 + (cluster.anInt802 * dx >> 8);
                    int i5 = cluster.anInt796 + (cluster.anInt803 * dx >> 8);
                    int j6 = cluster.anInt797 + (cluster.anInt804 * dx >> 8);

                    if (worldY >= k2 && worldY <= l3 && j >= i5 && j <= j6)
                        return true;
                }
            } else if (cluster.anInt798 == 3) {
                int dy = cluster.anInt794 - worldY;

                if (dy > 0) {
                    int l2 = cluster.anInt792 + (cluster.anInt799 * dy >> 8);
                    int i4 = cluster.anInt793 + (cluster.anInt800 * dy >> 8);
                    int j5 = cluster.anInt796 + (cluster.anInt803 * dy >> 8);
                    int k6 = cluster.anInt797 + (cluster.anInt804 * dy >> 8);

                    if (worldX >= l2 && worldX <= i4 && j >= j5 && j <= k6)
                        return true;
                }
            } else if (cluster.anInt798 == 4) {
                int dy = worldY - cluster.anInt794;
                if (dy > 0) {
                    int i3 = cluster.anInt792 + (cluster.anInt799 * dy >> 8);
                    int j4 = cluster.anInt793 + (cluster.anInt800 * dy >> 8);
                    int k5 = cluster.anInt796 + (cluster.anInt803 * dy >> 8);
                    int l6 = cluster.anInt797 + (cluster.anInt804 * dy >> 8);

                    if (worldX >= i3 && worldX <= j4 && j >= k5 && j <= l6)
                        return true;
                }
            } else if (cluster.anInt798 == 5) {
                int i2 = j - cluster.anInt796;
                if (i2 > 0) {
                    int j3 = cluster.anInt792 + (cluster.anInt799 * i2 >> 8);
                    int k4 = cluster.anInt793 + (cluster.anInt800 * i2 >> 8);
                    int l5 = cluster.anInt794 + (cluster.anInt801 * i2 >> 8);
                    int i7 = cluster.anInt795 + (cluster.anInt802 * i2 >> 8);

                    if (worldX >= j3 && worldX <= k4 && worldY >= l5 && worldY <= i7)
                        return true;
                }
            }
        }

        return false;
    }

    public boolean mouseInTriangle(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
        if (j < k && j < l && j < i1)
            return false;
        else if (j > k && j > l && j > i1)
            return false;
        else if (i < j1 && i < k1 && i < l1)
            return false;
        else if (i > j1 && i > k1 && i > l1)
            return false;

        int i2 = (j - k) * (k1 - j1) - (i - j1) * (l - k);
        int j2 = (j - i1) * (j1 - l1) - (i - l1) * (k - i1);
        int k2 = (j - l) * (l1 - k1) - (i - k1) * (i1 - l);
        return i2 * k2 > 0 && k2 * j2 > 0;
    }

    public void renderPlainTileNoMouse(SimpleTile tile, int plane, int ySin, int yCos, int xSin, int xCos, int tileX,
                                       int tileY, boolean hiddenTile, boolean highlighted, boolean tileSelected, boolean tileBeingSelected, byte flag) {
        int xC;
        int xA = xC = (tileX << 7) - xCameraTile;
        int yB;
        int yA = yB = (tileY << 7) - yCameraTile;
        int xD;
        int xB = xD = xA + 128;
        int yC;
        int yD = yC = yA + 128;
        int centreHeight = getMapRegion().tileHeights[plane][tileX][tileY] - zCameraTile;
        int eastHeight = getMapRegion().tileHeights[plane][tileX + 1][tileY] - zCameraTile;
        int northEastHeight = getMapRegion().tileHeights[plane][tileX + 1][tileY + 1] - zCameraTile;
        int northHeight = getMapRegion().tileHeights[plane][tileX][tileY + 1] - zCameraTile;
        int l4 = yA * xSin + xA * xCos >> 16;
        yA = yA * xCos - xA * xSin >> 16;
        xA = l4;
        l4 = centreHeight * yCos - yA * ySin >> 16;
        yA = centreHeight * ySin + yA * yCos >> 16;
        centreHeight = l4;
        if (yA < 50)
            return;
        l4 = yB * xSin + xB * xCos >> 16;
        yB = yB * xCos - xB * xSin >> 16;
        xB = l4;
        l4 = eastHeight * yCos - yB * ySin >> 16;
        yB = eastHeight * ySin + yB * yCos >> 16;
        eastHeight = l4;
        if (yB < 50)
            return;
        l4 = yD * xSin + xD * xCos >> 16;
        yD = yD * xCos - xD * xSin >> 16;
        xD = l4;
        l4 = northEastHeight * yCos - yD * ySin >> 16;
        yD = northEastHeight * ySin + yD * yCos >> 16;
        northEastHeight = l4;
        if (yD < 50)
            return;
        l4 = yC * xSin + xC * xCos >> 16;
        yC = yC * xCos - xC * xSin >> 16;
        xC = l4;
        l4 = northHeight * yCos - yC * ySin >> 16;
        yC = northHeight * ySin + yC * yCos >> 16;
        northHeight = l4;

        if (yC < 50)
            return;

        int screenXA = GameRasterizer.getInstance().viewCenter.getX() + (xA << 9) / yA;
        int screenYA = GameRasterizer.getInstance().viewCenter.getY() + (centreHeight << 9) / yA;
        int screenXB = GameRasterizer.getInstance().viewCenter.getX() + (xB << 9) / yB;
        int screenYB = GameRasterizer.getInstance().viewCenter.getY() + (eastHeight << 9) / yB;
        int screenXD = GameRasterizer.getInstance().viewCenter.getX() + (xD << 9) / yD;
        int screenYD = GameRasterizer.getInstance().viewCenter.getY() + (northEastHeight << 9) / yD;
        int screenXC = GameRasterizer.getInstance().viewCenter.getX() + (xC << 9) / yC;
        int screenYC = GameRasterizer.getInstance().viewCenter.getY() + (northHeight << 9) / yC;

        if ((screenXD - screenXC) * (screenYB - screenYC) - (screenYD - screenYC) * (screenXB - screenXC) > 0) {
            GameRasterizer.getInstance().restrictEdges = screenXD < 0 || screenXC < 0 || screenXB < 0 || screenXD > GameRasterizer.getInstance().getMaxRight() || screenXC > GameRasterizer.getInstance().getMaxRight()
                    || screenXB > GameRasterizer.getInstance().getMaxRight();


            if (Options.hdTextures.get()) {

                if (hiddenTile) {
                    GameRasterizer.getInstance().currentAlpha = 170;
                    GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                            tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
                    GameRasterizer.getInstance().currentAlpha = 0;
                } else if (!lowMemory && tile.getOverlayTextureId() != -1) {

                    if (tile.getNorthEastColour() != 0xbc614e) {
                        if (tile.isFlat()) {
                            GameRasterizer.getInstance().render_texture_triangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                    yD, yC, yB,
                                    tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(),
                                    xA, xB, xC,
                                    centreHeight, eastHeight, northHeight, yA, yB, yC, !lowMemory || tile.getOverlayColour() == -1 ? tile.getOverlayTextureId() : -1, tile.getUnderlayColour(), true, true);
                        } else {
                            GameRasterizer.getInstance().render_texture_triangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                    yD, yC, yB,
                                    tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xD, xC, xB,
                                    northEastHeight, northHeight, eastHeight, yD, yC, yB, !lowMemory || tile.getOverlayColour() == -1 ? tile.getOverlayTextureId() : -1, tile.getUnderlayColour(), true, true);
                        }
                    }
                } else if (tile.getNorthEastColour() != 0xbc614e) {
                    GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                            tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
                }
            } else {

                if (hiddenTile) {
                    GameRasterizer.getInstance().currentAlpha = 170;
                    GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                            tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
                    GameRasterizer.getInstance().currentAlpha = 0;
                } else if (tile.getOverlayTextureId() == -1) {
                    if (tile.getNorthEastColour() != 0xbc614e) {
                        GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
                    }
                } else if (!lowMemory) {
                    if (tile.isFlat()) {
                        GameRasterizer.getInstance().drawTexturedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xA, xB, xC,
                                centreHeight, eastHeight, northHeight, yA, yB, yC, tile.getOverlayTextureId());
                    } else {
                        GameRasterizer.getInstance().drawTexturedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xD, xC, xB,
                                northEastHeight, northHeight, eastHeight, yD, yC, yB, tile.getOverlayTextureId());
                    }
                } else {
                    int textureColour = TEXTURE_COLOURS[tile.getOverlayTextureId()];
                    GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                            light(textureColour, tile.getNorthEastColour()), light(textureColour, tile.getNorthColour()),
                            light(textureColour, tile.getEastColour()));
                }
            }
            // Rasterizer.drawRectangle(screen, y, screenXC, height, colour);
        }

        if ((screenXA - screenXB) * (screenYC - screenYB) - (screenYA - screenYB) * (screenXC - screenXB) > 0) {
            GameRasterizer.getInstance().restrictEdges = screenXA < 0 || screenXB < 0 || screenXC < 0 || screenXA > GameRasterizer.getInstance().getMaxRight() || screenXB > GameRasterizer.getInstance().getMaxRight()
                    || screenXC > GameRasterizer.getInstance().getMaxRight();

            if (hiddenTile) {
                GameRasterizer.getInstance().currentAlpha = 170;
                GameRasterizer.getInstance().drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                        tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour());
                GameRasterizer.getInstance().currentAlpha = 0;
            } else if (tile.getOverlayTextureId() == -1) {
                if (tile.getCentreColour() != 0xbc614e) {
                    GameRasterizer.getInstance().drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                            tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour());
                }
            } else {
                if (!lowMemory) {
                    if (Options.hdTextures.get() && tile.getCentreColour() != 0xbc614e) {

                        GameRasterizer.getInstance().render_texture_triangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                                yA, yB, yC,
                                tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour(), xA, xB, xC,
                                centreHeight, eastHeight, northHeight, yA, yB, yC, !lowMemory || tile.getOverlayColour() == -1 ? tile.getOverlayTextureId() : -1, tile.getUnderlayColour(), true, true);
                    } else if (tile.getCentreColour() != 0xbc614e) {
                        GameRasterizer.getInstance().drawTexturedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                                tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour(), xA, xB, xC,
                                centreHeight, eastHeight, northHeight, yA, yB, yC, tile.getOverlayTextureId());
                    }
                } else if (!Options.hdTextures.get()) {
                    int j7 = TEXTURE_COLOURS[tile.getOverlayTextureId()];
                    GameRasterizer.getInstance().drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                            light(j7, tile.getCentreColour()), light(j7, tile.getEastColour()),
                            light(j7, tile.getNorthColour()));

                }
            }

        }

    }

    public void renderPlainTile(SimpleTile tile, int plane, int ySin, int yCos, int xSin, int xCos, int tileX,
                                int tileY, boolean hiddenTile, boolean highlighted, boolean tileSelected, boolean tileBeingSelected, byte flag) {
        int xC;
        int xA = xC = (tileX << 7) - xCameraTile;
        int yB;
        int yA = yB = (tileY << 7) - yCameraTile;
        int xD;
        int xB = xD = xA + 128;
        int yC;
        int yD = yC = yA + 128;
        int centreHeight = getMapRegion().tileHeights[plane][tileX][tileY] - zCameraTile;
        int eastHeight = getMapRegion().tileHeights[plane][tileX + 1][tileY] - zCameraTile;
        int northEastHeight = getMapRegion().tileHeights[plane][tileX + 1][tileY + 1] - zCameraTile;
        int northHeight = getMapRegion().tileHeights[plane][tileX][tileY + 1] - zCameraTile;
        int l4 = yA * xSin + xA * xCos >> 16;
        yA = yA * xCos - xA * xSin >> 16;
        xA = l4;
        l4 = centreHeight * yCos - yA * ySin >> 16;
        yA = centreHeight * ySin + yA * yCos >> 16;
        centreHeight = l4;
        if (yA < 50)
            return;
        l4 = yB * xSin + xB * xCos >> 16;
        yB = yB * xCos - xB * xSin >> 16;
        xB = l4;
        l4 = eastHeight * yCos - yB * ySin >> 16;
        yB = eastHeight * ySin + yB * yCos >> 16;
        eastHeight = l4;
        if (yB < 50)
            return;
        l4 = yD * xSin + xD * xCos >> 16;
        yD = yD * xCos - xD * xSin >> 16;
        xD = l4;
        l4 = northEastHeight * yCos - yD * ySin >> 16;
        yD = northEastHeight * ySin + yD * yCos >> 16;
        northEastHeight = l4;
        if (yD < 50)
            return;
        l4 = yC * xSin + xC * xCos >> 16;
        yC = yC * xCos - xC * xSin >> 16;
        xC = l4;
        l4 = northHeight * yCos - yC * ySin >> 16;
        yC = northHeight * ySin + yC * yCos >> 16;
        northHeight = l4;

        if (yC < 50)
            return;

        int screenXA = GameRasterizer.getInstance().viewCenter.getX() + (xA << 9) / yA;
        int screenYA = GameRasterizer.getInstance().viewCenter.getY() + (centreHeight << 9) / yA;
        int screenXB = GameRasterizer.getInstance().viewCenter.getX() + (xB << 9) / yB;
        int screenYB = GameRasterizer.getInstance().viewCenter.getY() + (eastHeight << 9) / yB;
        int screenXD = GameRasterizer.getInstance().viewCenter.getX() + (xD << 9) / yD;
        int screenYD = GameRasterizer.getInstance().viewCenter.getY() + (northEastHeight << 9) / yD;
        int screenXC = GameRasterizer.getInstance().viewCenter.getX() + (xC << 9) / yC;
        int screenYC = GameRasterizer.getInstance().viewCenter.getY() + (northHeight << 9) / yC;

        if ((screenXD - screenXC) * (screenYB - screenYC) - (screenYD - screenYC) * (screenXB - screenXC) > 0) {
            GameRasterizer.getInstance().restrictEdges = screenXD < 0 || screenXC < 0 || screenXB < 0 || screenXD > GameRasterizer.getInstance().getMaxRight() || screenXC > GameRasterizer.getInstance().getMaxRight()
                    || screenXB > GameRasterizer.getInstance().getMaxRight();
           /* if (mouseInTriangle(clickX, clickY, screenYD, screenYC, screenYB, screenXD, screenXC, screenXB)) {
                handleMouseInTile(tileX, tileY, plane);
            }*/

            if (Options.hdTextures.get()) {

                if (hiddenTile) {
                    GameRasterizer.getInstance().currentAlpha = 170;
                    GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                            tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
                    GameRasterizer.getInstance().currentAlpha = 0;
                } else if (!lowMemory && tile.getOverlayTextureId() != -1) {

                    if (tile.getNorthEastColour() != 0xbc614e) {
                        if (tile.isFlat()) {
                            GameRasterizer.getInstance().render_texture_triangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                    yD, yC, yB,
                                    tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(),
                                    xA, xB, xC,
                                    centreHeight, eastHeight, northHeight, yA, yB, yC, !lowMemory || tile.getOverlayColour() == -1 ? tile.getOverlayTextureId() : -1, tile.getUnderlayColour(), true, true);
                        } else {
                            GameRasterizer.getInstance().render_texture_triangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                    yD, yC, yB,
                                    tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xD, xC, xB,
                                    northEastHeight, northHeight, eastHeight, yD, yC, yB, !lowMemory || tile.getOverlayColour() == -1 ? tile.getOverlayTextureId() : -1, tile.getUnderlayColour(), true, true);
                        }
                    }
                } else if (tile.getNorthEastColour() != 0xbc614e) {
                    GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                            tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
                }
            } else {

                if (hiddenTile) {
                    GameRasterizer.getInstance().currentAlpha = 170;
                    GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                            tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
                    GameRasterizer.getInstance().currentAlpha = 0;
                } else if (tile.getOverlayTextureId() == -1) {
                    if (tile.getNorthEastColour() != 0xbc614e) {
                        GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
                    } else if (Options.showHiddenTiles.get()) {
                        GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                GameRasterizer.getInstance().getFuchsia(), GameRasterizer.getInstance().getFuchsia(), GameRasterizer.getInstance().getFuchsia());
                    }
                } else if (!lowMemory) {
                    if (tile.isFlat()) {
                        GameRasterizer.getInstance().drawTexturedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xA, xB, xC,
                                centreHeight, eastHeight, northHeight, yA, yB, yC, tile.getOverlayTextureId());
                    } else {
                        GameRasterizer.getInstance().drawTexturedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                                tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xD, xC, xB,
                                northEastHeight, northHeight, eastHeight, yD, yC, yB, tile.getOverlayTextureId());
                    }
                } else {
                    int textureColour = TEXTURE_COLOURS[tile.getOverlayTextureId()];
                    GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
                            light(textureColour, tile.getNorthEastColour()), light(textureColour, tile.getNorthColour()),
                            light(textureColour, tile.getEastColour()));
                }
            }
            // Rasterizer.drawRectangle(screen, y, screenXC, height, colour);
        }

        if ((screenXA - screenXB) * (screenYC - screenYB) - (screenYA - screenYB) * (screenXC - screenXB) > 0) {
            GameRasterizer.getInstance().restrictEdges = screenXA < 0 || screenXB < 0 || screenXC < 0 || screenXA > GameRasterizer.getInstance().getMaxRight() || screenXB > GameRasterizer.getInstance().getMaxRight()
                    || screenXC > GameRasterizer.getInstance().getMaxRight();
            /*if (mouseInTriangle(clickX, clickY, screenYA, screenYB, screenYC, screenXA, screenXB, screenXC)) {
                this.handleMouseInTile(tileX, tileY, plane);
            }*/
            if (hiddenTile) {
                GameRasterizer.getInstance().currentAlpha = 170;
                GameRasterizer.getInstance().drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                        tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour());
                GameRasterizer.getInstance().currentAlpha = 0;
            } else if (tile.getOverlayTextureId() == -1) {
                if (tile.getCentreColour() != 0xbc614e) {
                    GameRasterizer.getInstance().drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                            tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour());
                } else if (Options.showHiddenTiles.get()) {
                    GameRasterizer.getInstance().drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                            GameRasterizer.getInstance().getFuchsia(), GameRasterizer.getInstance().getFuchsia(), GameRasterizer.getInstance().getFuchsia());

                }
            } else {
                if (!lowMemory) {
                    if (Options.hdTextures.get() && tile.getCentreColour() != 0xbc614e) {

                        GameRasterizer.getInstance().render_texture_triangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                                yA, yB, yC,
                                tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour(), xA, xB, xC,
                                centreHeight, eastHeight, northHeight, yA, yB, yC, !lowMemory || tile.getOverlayColour() == -1 ? tile.getOverlayTextureId() : -1, tile.getUnderlayColour(), true, true);
                    } else if (tile.getCentreColour() != 0xbc614e) {
                        GameRasterizer.getInstance().drawTexturedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                                tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour(), xA, xB, xC,
                                centreHeight, eastHeight, northHeight, yA, yB, yC, tile.getOverlayTextureId());
                    }
                } else if (!Options.hdTextures.get()) {
                    int j7 = TEXTURE_COLOURS[tile.getOverlayTextureId()];
                    GameRasterizer.getInstance().drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
                            light(j7, tile.getCentreColour()), light(j7, tile.getEastColour()),
                            light(j7, tile.getNorthColour()));

                }
            }

        }


        if (highlighted || tileSelected || tileBeingSelected) {
            GameRasterizer.getInstance().currentAlpha = 139;
            SimpleTile highlightTile = TileUtils.HIGHLIGHT_TILE;
            if (tileBeingSelected) {
                highlightTile = TileUtils.BEING_SELECTED_TILE;
                GameRasterizer.getInstance().currentAlpha = 33;
            } else if (tileSelected) {
                highlightTile = TileUtils.SELECTED_TILE;
                GameRasterizer.getInstance().currentAlpha = 44;
            }
            this.renderPlainTileNoMouse(highlightTile, plane, ySin, yCos, xSin, xCos, tileX, tileY, false, false, false, false, (byte) 0);
            GameRasterizer.getInstance().currentAlpha = 0;
        } else if (shouldShowFlags(flag)) {
            GameRasterizer.getInstance().currentAlpha = 88;
            SimpleTile highlightTile = plane == Options.currentHeight.get() ? TileUtils.NON_WALKABLE : TileUtils.NON_WALKABLE_OTHER_HEIGHT;

            this.renderPlainTileNoMouse(highlightTile, plane, ySin, yCos, xSin, xCos, tileX, tileY, false, false, false, false, (byte) 0);
            GameRasterizer.getInstance().currentAlpha = 0;
        }
    }

    public void renderShapedTile(int tileX, int ySin, int xSin, ShapedTile tile, int yCos, int tileY, int xCos,
                                 int plane, boolean highlight, boolean tileSelected, boolean tileBeingSelected, byte flag) {

        //Renders a tile in 2 triangle halves.

        int triangleCount = tile.getOrigVertexX().length;
        for (int vID = 0; vID < triangleCount; vID++) {
            int viewspaceX = tile.getOrigVertexX()[vID] - xCameraTile;
            int viewspaceY = tile.getOrigVertexY()[vID] - zCameraTile;
            int viewspaceZ = tile.getOrigVertexZ()[vID] - yCameraTile;
            int k3 = viewspaceZ * xSin + viewspaceX * xCos >> 16;
            viewspaceZ = viewspaceZ * xCos - viewspaceX * xSin >> 16;
            viewspaceX = k3;
            k3 = viewspaceY * yCos - viewspaceZ * ySin >> 16;
            viewspaceZ = viewspaceY * ySin + viewspaceZ * yCos >> 16;
            viewspaceY = k3;
            if (viewspaceZ < 50)
                return;
            if (tile.getTriangleTextureOverlay() != null) {
                ShapedTile.viewSpaceX[vID] = viewspaceX;
                ShapedTile.viewSpaceY[vID] = viewspaceY;
                ShapedTile.viewSpaceZ[vID] = viewspaceZ;
            }
            ShapedTile.screenX[vID] = GameRasterizer.getInstance().viewCenter.getX() + (viewspaceX << 9) / viewspaceZ;
            ShapedTile.screenY[vID] = GameRasterizer.getInstance().viewCenter.getY() + (viewspaceY << 9) / viewspaceZ;
            ShapedTile.screenZ[vID] = viewspaceZ;
        }

        GameRasterizer.getInstance().currentAlpha = 0;
        triangleCount = tile.getTriangleA().length;
        for (int triangleIndex = 0; triangleIndex < triangleCount; triangleIndex++) {
            int indexA = tile.getTriangleA()[triangleIndex];
            int indexB = tile.getTriangleB()[triangleIndex];
            int indexC = tile.getTriangleC()[triangleIndex];
            int sXA = ShapedTile.screenX[indexA];
            int sXB = ShapedTile.screenX[indexB];
            int sXC = ShapedTile.screenX[indexC];
            int sYA = ShapedTile.screenY[indexA];
            int sYB = ShapedTile.screenY[indexB];
            int sYC = ShapedTile.screenY[indexC];
            int sZA = ShapedTile.screenZ[indexA];
            int sZB = ShapedTile.screenZ[indexB];
            int sZC = ShapedTile.screenZ[indexC];
            if ((sXA - sXB) * (sYC - sYB) - (sYA - sYB) * (sXC - sXB) > 0) {
                GameRasterizer.getInstance().restrictEdges = sXA < 0 || sXB < 0 || sXC < 0 || sXA > GameRasterizer.getInstance().getMaxRight() || sXB > GameRasterizer.getInstance().getMaxRight()
                        || sXC > GameRasterizer.getInstance().getMaxRight();
               /* if (mouseInTriangle(clickX, clickY, sYA, sYB, sYC, sXA, sXB, sXC)) {
                    handleMouseInTile(tileX, tileY, plane);
                }*/

                if (Options.hdTextures.get()) {
                    if (tile.getTriangleTextureOverlay() == null || tile.getTriangleTextureOverlay()[triangleIndex] == -1) {
                        if (tile.getTriangleHslA()[triangleIndex] != 0xbc614e) {
                            GameRasterizer.getInstance().drawShadedTriangle(sYA, sYB, sYC, sXA, sXB, sXC,
                                    tile.getTriangleHslA()[triangleIndex], tile.getTriangleHslB()[triangleIndex],
                                    tile.getTriangleHslC()[triangleIndex]);
                        }
                    } else if (!lowMemory) {
                        if (tile.isFlat()) {
                            GameRasterizer.getInstance().render_texture_triangle(sYA, sYB, sYC, sXA, sXB, sXC, sZA, sZB, sZC,
                                    tile.getTriangleHslA()[triangleIndex], tile.getTriangleHslB()[triangleIndex],
                                    tile.getTriangleHslC()[triangleIndex],
                                    ShapedTile.viewSpaceX[0], ShapedTile.viewSpaceX[1],
                                    ShapedTile.viewSpaceX[3], ShapedTile.viewSpaceY[0],
                                    ShapedTile.viewSpaceY[1], ShapedTile.viewSpaceY[3],
                                    ShapedTile.viewSpaceZ[0], ShapedTile.viewSpaceZ[1],
                                    ShapedTile.viewSpaceZ[3],
                                    tile.getTextureIds()[triangleIndex], 0, true, true);
                        } else {
                            GameRasterizer.getInstance().render_texture_triangle(sYA, sYB, sYC, sXA, sXB, sXC, sZA, sZB, sZC,
                                    tile.getTriangleHslA()[triangleIndex], tile.getTriangleHslB()[triangleIndex],
                                    tile.getTriangleHslC()[triangleIndex], ShapedTile.viewSpaceX[indexA],
                                    ShapedTile.viewSpaceX[indexB], ShapedTile.viewSpaceX[indexC],
                                    ShapedTile.viewSpaceY[indexA], ShapedTile.viewSpaceY[indexB],
                                    ShapedTile.viewSpaceY[indexC], ShapedTile.viewSpaceZ[indexA],
                                    ShapedTile.viewSpaceZ[indexB], ShapedTile.viewSpaceZ[indexC],
                                    tile.getTextureIds()[triangleIndex], 0, true, true);
                        }
                    }
                } else {
                    if (tile.getTriangleTextureOverlay() == null || tile.getTriangleTextureOverlay()[triangleIndex] == -1) {
                        if (tile.getTriangleHslA()[triangleIndex] != 0xbc614e) {
                            GameRasterizer.getInstance().drawShadedTriangle(sYA, sYB, sYC, sXA, sXB, sXC,
                                    tile.getTriangleHslA()[triangleIndex], tile.getTriangleHslB()[triangleIndex],
                                    tile.getTriangleHslC()[triangleIndex]);
                        }
                    } else if (!lowMemory) {
                        if (tile.isFlat()) {
                            GameRasterizer.getInstance().drawTexturedTriangle(sYA, sYB, sYC, sXA, sXB, sXC,
                                    tile.getTriangleHslA()[triangleIndex], tile.getTriangleHslB()[triangleIndex],
                                    tile.getTriangleHslC()[triangleIndex], ShapedTile.viewSpaceX[0],
                                    ShapedTile.viewSpaceX[1], ShapedTile.viewSpaceX[3], ShapedTile.viewSpaceY[0],
                                    ShapedTile.viewSpaceY[1], ShapedTile.viewSpaceY[3], ShapedTile.viewSpaceZ[0],
                                    ShapedTile.viewSpaceZ[1], ShapedTile.viewSpaceZ[3],
                                    tile.getTriangleTextureOverlay()[triangleIndex]);
                        } else {
                            GameRasterizer.getInstance().drawTexturedTriangle(sYA, sYB, sYC, sXA, sXB, sXC,
                                    tile.getTriangleHslA()[triangleIndex], tile.getTriangleHslB()[triangleIndex],
                                    tile.getTriangleHslC()[triangleIndex], ShapedTile.viewSpaceX[indexA],
                                    ShapedTile.viewSpaceX[indexB], ShapedTile.viewSpaceX[indexC],
                                    ShapedTile.viewSpaceY[indexA], ShapedTile.viewSpaceY[indexB],
                                    ShapedTile.viewSpaceY[indexC], ShapedTile.viewSpaceZ[indexA],
                                    ShapedTile.viewSpaceZ[indexB], ShapedTile.viewSpaceZ[indexC],
                                    tile.getTriangleTextureOverlay()[triangleIndex]);
                        }
                    }
                }
            }

            /*
             * if(tileX == clickedTileX && tileZ == clickedTileY){ Rasterizer.currentAlpha =
             * 139; Rasterizer.drawPlainTriangle(sYA, sYB, sYC, sXA, sXB, sXC, 0xff80 + 127,
             * 0xff80 + 127, 0xff80 + 127); }
             */
        }
        if (highlight || tileSelected || tileBeingSelected) {
            GameRasterizer.getInstance().currentAlpha = 139;
            SimpleTile highlightTile = TileUtils.HIGHLIGHT_TILE;
            if (tileBeingSelected) {
                highlightTile = TileUtils.BEING_SELECTED_TILE;
                GameRasterizer.getInstance().currentAlpha = 33;
            } else if (tileSelected) {
                highlightTile = TileUtils.SELECTED_TILE;
                GameRasterizer.getInstance().currentAlpha = 44;
            }
            this.renderPlainTile(highlightTile, plane, ySin, yCos, xSin, xCos, tileX, tileY, false, false, false, false, (byte) 0);
            GameRasterizer.getInstance().currentAlpha = 0;
        } else if (shouldShowFlags(flag)) {
            GameRasterizer.getInstance().currentAlpha = 88;
            SimpleTile highlightTile = TileUtils.NON_WALKABLE;

            this.renderPlainTile(highlightTile, plane, ySin, yCos, xSin, xCos, tileX, tileY, false, false, false, false, (byte) 0);
            GameRasterizer.getInstance().currentAlpha = 0;
        }
    }

    public void renderTile(SceneTile newTile, boolean flag) {
        tileQueue.addLast(newTile);
        do {
            SceneTile activeTile;

            do {
                activeTile = tileQueue.poll();
                if (activeTile == null)
                    return;
            } while (!activeTile.aBoolean1323);

            int x = activeTile.worldPos.x;
            int y = activeTile.worldPos.y;
            int plane = activeTile.worldPos.z;
            int l = activeTile.worldPos.z;
            if (plane > 3) {
                continue;
            }

            if (activeTile.needsRendering) {
                if (flag) {
                    if (plane > 0) {
                        val worldPosBelow = new Vector3i(x, y, plane - 1);
                        SceneTile tile = tiles.get(worldPosBelow);
                        if (tile != null && tile.aBoolean1323) {
                            continue;
                        }
                    }

                    if (x <= absoluteCameraX && x > minViewX && x > 0) {
                        val worldPosWest = new Vector3i(x - 1, y, plane);
                        SceneTile tile = tiles.get(worldPosWest);
                        if (tile != null && tile.aBoolean1323 && (tile.needsRendering
                                || (activeTile.attributes & TileAttributes.RENDER_TILE_WEST) == 0)) {
                            continue;
                        }
                    }

                    if (x >= absoluteCameraX && x < maxViewX - 1 && x < width - 1) {
                        val worldPosEast = new Vector3i(x + 1, y, plane);
                        SceneTile tile = tiles.get(worldPosEast);
                        if (tile != null && tile.aBoolean1323 && (tile.needsRendering
                                || (activeTile.attributes & TileAttributes.RENDER_TILE_EAST) == 0)) {
                            continue;
                        }
                    }

                    if (y <= absoluteCameraY && y > minViewY && y > 0) {
                        val worldPosNorth = new Vector3i(x, y - 1, plane);
                        SceneTile tile = tiles.get(worldPosNorth);
                        if (tile != null && tile.aBoolean1323 && (tile.needsRendering
                                || (activeTile.attributes & TileAttributes.RENDER_TILE_NORTH) == 0)) {
                            continue;
                        }
                    }

                    if (y >= absoluteCameraY && y < maxViewY - 1 && y < length - 1) {
                        val worldPosSouth = new Vector3i(x, y + 1, plane);
                        SceneTile tile = tiles.get(worldPosSouth);
                        if (tile != null && tile.aBoolean1323 && (tile.needsRendering
                                || (activeTile.attributes & TileAttributes.RENDER_TILE_SOUTH) == 0)) {
                            continue;
                        }
                    }
                } else {
                    flag = true;
                }

                activeTile.needsRendering = false;
                if (activeTile.tileBelow != null) {

                    SceneTile tileBelow = activeTile.tileBelow;
                    if (tileBelow.temporarySimpleTile != null) {
                        if (!method320(x, y, 0)) {
                            GameRasterizer.getInstance().currentAlpha = 0;
                            renderPlainTile(tileBelow.temporarySimpleTile, 0, ySine, yCosine, xSine, xCosine, x, y, false,
                                    false, false, false, (byte) 0);
                        }
                    } else if (tileBelow.temporaryShapedTile != null && !method320(x, y, 0)) {
                        renderShapedTile(x, ySine, xSine, tileBelow.temporaryShapedTile, yCosine, y, xCosine, 0,
                                false, false, false, (byte) 0);
                    } else if (tileBelow.simple != null) {
                        if (!method320(x, y, 0)) {
                            GameRasterizer.getInstance().currentAlpha = 0;
                            renderPlainTile(tileBelow.simple, 0, ySine, yCosine, xSine, xCosine, x, y, false,
                                    tileBelow.tileHighlighted, tileBelow.tileSelected, tileBelow.tileBeingSelected, tileBelow.tileFlags);
                        }
                    } else if (tileBelow.shape != null && !method320(x, y, 0)) {
                        renderShapedTile(x, ySine, xSine, tileBelow.shape, yCosine, y, xCosine, 0,
                                tileBelow.simple == null && tileBelow.tileHighlighted, tileBelow.tileSelected, tileBelow.tileBeingSelected, tileBelow.tileFlags);
                    }

                    if (Options.showObjects.get()) {
                        val worldObject = tileBelow.getObject(TypeFilter.wallObjects);
                        if (worldObject instanceof Wall) {
                            Wall wall = (Wall) worldObject;
                            wall.primary.render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
                                    ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
                                    wall.getKey(), plane);
                        }


                        val gameObjects = tileBelow.getObjects(TypeFilter.genericAndRoof);
                        for (DefaultWorldObject defaultWorldObject : gameObjects) {
                            if (defaultWorldObject instanceof GameObject) {
                                val object = (GameObject) defaultWorldObject;
                                object.primary.render(GameRasterizer.getInstance(), object.center.x - xCameraTile,
                                        object.center.y - yCameraTile, object.yaw, ySine, yCosine, xSine, xCosine,
                                        object.getRenderHeight() - zCameraTile, object.getKey(), plane);
                            }
                        }
                    }
                }

                boolean flag1 = Options.showHiddenTiles.get();
                if (activeTile.temporarySimpleTile != null) {
                    if (!method320(x, y, l)) {
                        flag1 = true;
                        GameRasterizer.getInstance().currentAlpha = 0;
                        renderPlainTile(activeTile.temporarySimpleTile, l, ySine, yCosine, xSine, xCosine, x, y, false, false, false, false, (byte) 0);
                    }
                } else if (activeTile.temporaryShapedTile != null && !method320(x, y, l)) {
                    flag1 = true;
                    renderShapedTile(x, ySine, xSine, activeTile.temporaryShapedTile, yCosine, y, xCosine, l, false, false, false, (byte) 0);
                } else if (activeTile.simple != null) {
                    if (!method320(x, y, l)) {
                        flag1 = true;
                        GameRasterizer.getInstance().currentAlpha = 0;
                        renderPlainTile(activeTile.simple, l, ySine, yCosine, xSine, xCosine, x, y, false,
                                activeTile.tileHighlighted, activeTile.tileSelected, activeTile.tileBeingSelected, activeTile.tileFlags);
                    }
                } else if (activeTile.shape != null && !method320(x, y, l)) {
                    flag1 = true;
                    renderShapedTile(x, ySine, xSine, activeTile.shape, yCosine, y, xCosine, l,
                            activeTile.simple == null && activeTile.tileHighlighted, activeTile.tileSelected, activeTile.tileBeingSelected, activeTile.tileFlags);
                } else if (plane == Options.currentHeight.get() && Options.showHiddenTiles.get() &&
                        activeTile.shape == null && activeTile.simple == null &&
                        activeTile.temporaryShapedTile != null && activeTile.temporarySimpleTile != null) {
                    SimpleTile hiddenTile = TileUtils.HIDDEN_TILE;
                    this.renderPlainTile(hiddenTile, plane, ySine, yCosine, xSine, xCosine, x, y, true,
                            activeTile.tileHighlighted, activeTile.tileSelected, activeTile.tileBeingSelected, getMapRegion().tileFlags[plane][x][y]);
                }

                if (Options.showObjects.get()) {
                    int j1 = 0;
                    int j2 = 0;
                    val wallWorldObject = activeTile.getObject(TypeFilter.wallObjects);
                    val decorationWorldObject = activeTile.getObject(TypeFilter.wallDecoration);
                    if (wallWorldObject != null || decorationWorldObject != null) {
                        if (absoluteCameraX == x) {
                            j1++;
                        } else if (absoluteCameraX < x) {
                            j1 += 2;
                        }
                        if (absoluteCameraY == y) {
                            j1 += 3;
                        } else if (absoluteCameraY > y) {
                            j1 += 6;
                        }
                        j2 = anIntArray478[j1];
                        activeTile.anInt1328 = anIntArray480[j1];
                    }
                    if (wallWorldObject instanceof Wall) {

                        val wall = (Wall) wallWorldObject;
                        if ((wall.anInt276 & anIntArray479[j1]) != 0) {
                            if (wall.anInt276 == 16) {
                                activeTile.anInt1325 = 3;
                                activeTile.anInt1326 = anIntArray481[j1];
                                activeTile.anInt1327 = 3 - activeTile.anInt1326;
                            } else if (wall.anInt276 == 32) {
                                activeTile.anInt1325 = 6;
                                activeTile.anInt1326 = anIntArray482[j1];
                                activeTile.anInt1327 = 6 - activeTile.anInt1326;
                            } else if (wall.anInt276 == 64) {
                                activeTile.anInt1325 = 12;
                                activeTile.anInt1326 = anIntArray483[j1];
                                activeTile.anInt1327 = 12 - activeTile.anInt1326;
                            } else {
                                activeTile.anInt1325 = 9;
                                activeTile.anInt1326 = anIntArray484[j1];
                                activeTile.anInt1327 = 9 - activeTile.anInt1326;
                            }
                        } else {
                            activeTile.anInt1325 = 0;
                        }
                        if ((wall.anInt276 & j2) != 0 && !method321(x, y, l, wall.anInt276)
                                && wall.primary != null) {
                            wall.primary.render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
                                    ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
                                    wall.getKey(), plane);
                        }
                        if ((wall.anInt277 & j2) != 0 && !method321(x, y, l, wall.anInt277)
                                && wall.secondary != null) {
                            wall.secondary.render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
                                    ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
                                    wall.getKey(), plane);
                        }
                    }


                    if (decorationWorldObject instanceof WallDecoration) {
                        val decoration = (WallDecoration) decorationWorldObject;
                        if (decoration.primary != null
                                && !method322(l, x, y, decoration.primary.getHeight())) {
                            if ((decoration.getDecorData() & j2) != 0) {
                                decoration.primary.render(GameRasterizer.getInstance(), decoration.getX() - xCameraTile,
                                        decoration.getY() - yCameraTile, decoration.getOrientation(), ySine, yCosine,
                                        xSine, xCosine, decoration.getRenderHeight() - zCameraTile,
                                        decoration.getKey(), plane);
                            } else if ((decoration.getDecorData() & 0b11_0000_0000) != 0) { // type
                                // 6,
                                // 7,
                                // or
                                // 8
                                int dx = decoration.getX() - xCameraTile;
                                int height = decoration.getRenderHeight() - zCameraTile;
                                int dy = decoration.getY() - yCameraTile;
                                int orientation = decoration.getOrientation();
                                int width;

                                if (orientation == 1 || orientation == 2) {
                                    width = -dx;
                                } else {
                                    width = dx;
                                }

                                int length;
                                if (orientation == 2 || orientation == 3) {
                                    length = -dy;
                                } else {
                                    length = dy;
                                }

                                if ((decoration.getDecorData() & 0b1_0000_0000) != 0 && length < width) { // type
                                    // 6
                                    int renderX = dx + anIntArray463[orientation];
                                    int renderY = dy + anIntArray464[orientation];
                                    decoration.primary.render(GameRasterizer.getInstance(), renderX, renderY, orientation * 512 + 256, ySine,
                                            yCosine, xSine, xCosine, height, decoration.getKey(), plane);
                                }

                                if ((decoration.getDecorData() & 0b10_0000_0000) != 0 && length > width) { // type
                                    // 7
                                    int renderX = dx + anIntArray465[orientation];
                                    int renderY = dy + anIntArray466[orientation];
                                    decoration.primary.render(GameRasterizer.getInstance(), renderX, renderY, orientation * 512 + 1280 & 0x7ff,
                                            ySine, yCosine, xSine, xCosine, height, decoration.getKey(), plane);
                                }
                            }
                        }
                    }

                    if (flag1) {
                        val worldObject = activeTile.getObject(TypeFilter.groundDecoration);
                        if (worldObject instanceof GroundDecoration) {

                            val decor = (GroundDecoration) worldObject;
                            if (decor != null && decor.primary != null) {
                                boolean hasFunction = decor.getMinimapFunction() != null;
                                if (hasFunction) {
                                    if (Options.showMinimapFunctionModels.get())
                                        decor.primary.render(GameRasterizer.getInstance(), decor.getX() - xCameraTile, decor.getY() - yCameraTile,
                                                0, ySine, yCosine, xSine, xCosine, decor.getRenderHeight() - zCameraTile,
                                                decor.getKey(), plane);
                                } else {
                                    decor.primary.render(GameRasterizer.getInstance(), decor.getX() - xCameraTile, decor.getY() - yCameraTile,
                                            0, ySine, yCosine, xSine, xCosine, decor.getRenderHeight() - zCameraTile,
                                            decor.getKey(), plane);
                                }

                            }
                        }


                    }
                    int attributes = activeTile.attributes;
                    if (attributes != 0) {
                        if (x < absoluteCameraX && (attributes & TileAttributes.RENDER_TILE_EAST) != 0) {
                            val worldPosEast = new Vector3i(x + 1, y, plane);
                            SceneTile tile = tiles.get(worldPosEast);
                            if (tile != null && tile.aBoolean1323) {
                                tileQueue.addLast(tile);
                            }
                        }

                        if (y < absoluteCameraY && (attributes & TileAttributes.RENDER_TILE_SOUTH) != 0) {
                            val worldPosSouth = new Vector3i(x, y + 1, plane);
                            SceneTile tile = tiles.get(worldPosSouth);
                            if (tile != null && tile.aBoolean1323) {
                                tileQueue.addLast(tile);
                            }
                        }

                        if (x > absoluteCameraX && (attributes & TileAttributes.RENDER_TILE_WEST) != 0) {
                            val worldPosWest = new Vector3i(x - 1, y, plane);
                            SceneTile tile = tiles.get(worldPosWest);
                            if (tile != null && tile.aBoolean1323) {
                                tileQueue.addLast(tile);
                            }
                        }

                        if (y > absoluteCameraY && (attributes & TileAttributes.RENDER_TILE_NORTH) != 0) {
                            val worldPosNorth = new Vector3i(x, y - 1, plane);
                            SceneTile tile = tiles.get(worldPosNorth);
                            if (tile != null && tile.aBoolean1323) {
                                tileQueue.addLast(tile);
                            }
                        }
                    }

                }
            }

            if (!Options.showObjects.get()) {
                continue;
            }


            if (activeTile.anInt1325 != 0) {
                boolean flag2 = true;

                val worldObjects = activeTile.getObjects(TypeFilter.genericAndRoof);
                for (DefaultWorldObject worldObject : worldObjects) {
                    if (worldObject instanceof GameObject) {
                        val gameObject = (GameObject) worldObject;
                        int objAttrib = gameObject.attributes;
                        if (gameObject.lastRenderCycle == currentRenderCycle || (objAttrib & activeTile.anInt1325) != activeTile.anInt1326) {
                            continue;
                        }

                        flag2 = false;
                        break;
                    }
                }

                if (flag2) {
                    val worldObject = activeTile.getObject(TypeFilter.wallObjects);
                    if (worldObject instanceof Wall) {
                        val wall = (Wall) worldObject;
                        if (!method321(x, y, l, wall.anInt276)) {
                            wall.primary.render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
                                    ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
                                    wall.getKey(), plane);
                        }
                    }


                    activeTile.anInt1325 = 0;
                }
            }

            if (activeTile.hasObjects) {
                try {
                    val worldObjects = activeTile.getObjects(TypeFilter.genericAndRoof);

                    activeTile.hasObjects = false;
                    int objectsOnTile = 0;
                    label0:
                    for (DefaultWorldObject worldObject : worldObjects) {

                        if (worldObject instanceof GameObject) {
                            val object = (GameObject) worldObject;

                            int objAttrib = object.attributes;


                            if (object.lastRenderCycle == currentRenderCycle) {
                                continue;
                            }

                            for (int objectX = object.getX(); objectX <= object.maximum.x; objectX++) {
                                for (int objectY = object.getY(); objectY <= object.maximum.y; objectY++) {
                                    val worldPos = new Vector3i(objectX, objectY, activeTile.worldPos.z);
                                    SceneTile objectTile = tiles.get(worldPos);

                                    if (objectTile == null)
                                        continue;

                                    if (objectTile.needsRendering) {
                                        activeTile.hasObjects = true;
                                    } else {
                                        if (objectTile.anInt1325 == 0) {
                                            continue;
                                        }
                                        int l6 = 0;
                                        if (objectX > object.getX()) {
                                            l6++;
                                        }
                                        if (objectX < object.maximum.x) {
                                            l6 += 4;
                                        }
                                        if (objectY > object.getY()) {
                                            l6 += 8;
                                        }
                                        if (objectY < object.maximum.y) {
                                            l6 += 2;
                                        }
                                        if ((l6 & objectTile.anInt1325) != activeTile.anInt1327) {
                                            continue;
                                        }

                                        activeTile.hasObjects = true;
                                    }
                                    continue label0;
                                }

                            }

                            interactables[objectsOnTile++] = object;
                            int distanceFromStartX = absoluteCameraX - object.getX();
                            /*
                             * If object X position is further than the camera x, x is negative
                             */
                            int i6 = object.maximum.x - absoluteCameraX;

                            /*
                             * if the maxX is greater than the x camera position, x is positive
                             */

                            if (i6 > distanceFromStartX) {
                                distanceFromStartX = i6;
                            }

                            int i7 = absoluteCameraY - object.getY();
                            int j8 = object.maximum.y - absoluteCameraY;
                            if (j8 > i7) {
                                object.anInt527 = distanceFromStartX + j8;
                            } else {
                                object.anInt527 = distanceFromStartX + i7;
                            }
                        }
                    }

                    while (objectsOnTile > 0) {
                        int i3 = -50;
                        int l3 = -1;
                        for (int j5 = 0; j5 < objectsOnTile; j5++) {
                            GameObject object = interactables[j5];
                            if (object.lastRenderCycle != currentRenderCycle) {
                                if (object.anInt527 > i3) {
                                    i3 = object.anInt527;
                                    l3 = j5;
                                } else if (object.anInt527 == i3) {
                                    int j7 = object.center.x - xCameraTile;
                                    int k8 = object.center.y - yCameraTile;
                                    int l9 = interactables[l3].center.x - xCameraTile;
                                    int l10 = interactables[l3].center.y - yCameraTile;
                                    if (j7 * j7 + k8 * k8 > l9 * l9 + l10 * l10) {
                                        l3 = j5;
                                    }
                                }
                            }
                        }

                        if (l3 == -1) {
                            break;
                        }

                        GameObject object = interactables[l3];

                        object.lastRenderCycle = currentRenderCycle;
                        if (!method323(l, object.getX(), object.maximum.x, object.getY(), object.maximum.y,
                                object.primary.getHeight())) {
                            object.primary.render(GameRasterizer.getInstance(), object.center.x - xCameraTile,
                                    object.center.y - yCameraTile, object.yaw, ySine, yCosine, xSine, xCosine,
                                    object.getRenderHeight() - zCameraTile, object.getKey(), plane);
                        }

                        for (int objectX = object.getX(); objectX <= object.maximum.x; objectX++) {
                            for (int objectY = object.getY(); objectY <= object.maximum.y; objectY++) {
                                val worldPos = new Vector3i(objectX, objectY, activeTile.worldPos.z);
                                SceneTile objectTile = tiles.get(worldPos);

                                if (objectTile.anInt1325 != 0) {
                                    tileQueue.addLast(objectTile);
                                } else if ((objectX != x || objectY != y) && objectTile.aBoolean1323) {
                                    tileQueue.addLast(objectTile);
                                }
                            }

                        }

                    }
                    if (activeTile.hasObjects) {
                        continue;
                    }
                } catch (Exception _ex) {//TODO
                    _ex.printStackTrace();
                    activeTile.hasObjects = false;
                }
            }

            if (!activeTile.aBoolean1323 || activeTile.anInt1325 != 0) {
                continue;
            }

            if (x <= absoluteCameraX && x > minViewX && x > 0) {
                val worldPosWest = new Vector3i(x - 1, y, plane);
                SceneTile tile = tiles.get(worldPosWest);
                if (tile != null && tile.aBoolean1323) {
                    continue;
                }
            }

            if (x >= absoluteCameraX && x < maxViewX - 1 && x < width - 1) {
                val worldPosEast = new Vector3i(x + 1, y, plane);
                SceneTile tile = tiles.get(worldPosEast);
                if (tile != null && tile.aBoolean1323) {
                    continue;
                }
            }

            if (y <= absoluteCameraY && y > minViewY && y > 0) {
                val worldPosNorth = new Vector3i(x, y - 1, plane);
                SceneTile tile = tiles.get(worldPosNorth);
                if (tile != null && tile.aBoolean1323) {
                    continue;
                }
            }

            if (y >= absoluteCameraY && y < maxViewY - 1 && y < length - 1) {
                val worldPosSouth = new Vector3i(x, y + 1, plane);
                SceneTile tile = tiles.get(worldPosSouth);
                if (tile != null && tile.aBoolean1323) {
                    continue;
                }
            }

            activeTile.aBoolean1323 = false;
            anInt446--;

            if (activeTile.anInt1328 != 0) {
                val decoWorldObject = activeTile.getObject(TypeFilter.wallDecoration);
                if(decoWorldObject instanceof WallDecoration) {
                    val decor = (WallDecoration) decoWorldObject;
                    if (decor.primary != null && !method322(l, x, y, decor.primary.getHeight())) {
                        if ((decor.getDecorData() & activeTile.anInt1328) != 0) {
                            decor.primary.render(GameRasterizer.getInstance(), decor.getX() - xCameraTile, decor.getY() - yCameraTile,
                                    decor.getOrientation(), ySine, yCosine, xSine, xCosine,
                                    decor.getRenderHeight() - zCameraTile, decor.getKey(), plane);
                        } else if ((decor.getDecorData() & 0x300) != 0) {
                            int l2 = decor.getX() - xCameraTile;
                            int j3 = decor.getRenderHeight() - zCameraTile;
                            int i4 = decor.getY() - yCameraTile;
                            int orientation = decor.getOrientation();
                            int j6;
                            if (orientation == 1 || orientation == 2) {
                                j6 = -l2;
                            } else {
                                j6 = l2;
                            }
                            int l7;
                            if (orientation == 2 || orientation == 3) {
                                l7 = -i4;
                            } else {
                                l7 = i4;
                            }
                            if ((decor.getDecorData() & 0x100) != 0 && l7 >= j6) {
                                int i9 = l2 + anIntArray463[orientation];
                                int i10 = i4 + anIntArray464[orientation];
                                decor.primary.render(GameRasterizer.getInstance(), i9, i10, orientation * 512 + 256, ySine, yCosine, xSine,
                                        xCosine, j3, decor.getKey(), plane);
                            }
                            if ((decor.getDecorData() & 0x200) != 0 && l7 <= j6) {
                                int j9 = l2 + anIntArray465[orientation];
                                int j10 = i4 + anIntArray466[orientation];
                                decor.primary.render(GameRasterizer.getInstance(), j9, j10, orientation * 512 + 1280 & 0x7ff, ySine, yCosine,
                                        xSine, xCosine, j3, decor.getKey(), plane);
                            }
                        }
                    }
                }
                val worldObject = activeTile.getObject(TypeFilter.wallObjects);
                if(worldObject instanceof Wall) {
                    Wall wall = (Wall) worldObject;

                    if ((wall.anInt277 & activeTile.anInt1328) != 0 && !method321(x, y, l, wall.anInt277)
                            && wall.secondary != null) {
                        wall.secondary.render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
                                ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
                                wall.getKey(), plane);
                    }
                    if ((wall.anInt276 & activeTile.anInt1328) != 0 && !method321(x, y, l, wall.anInt276)
                            && wall.primary != null) {
                        wall.primary.render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
                                ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
                                wall.getKey(), plane);
                    }
                }
            }
            if (plane < planeCount - 1) {
                val worldPosAbove = new Vector3i(x, y, plane + 1);
                SceneTile above = tiles.get(worldPosAbove);
                if (above != null && above.aBoolean1323) {
                    tileQueue.addLast(above);
                }
            }
            if (x < absoluteCameraX && x < width - 1) {
                val worldPosEast = new Vector3i(x + 1, y, plane);
                SceneTile tile = tiles.get(worldPosEast);
                if (tile != null && tile.aBoolean1323) {
                    tileQueue.addLast(tile);
                }
            }
            if (y < absoluteCameraY && y < length - 1) {
                val worldPosNorth = new Vector3i(x, y + 1, plane);
                SceneTile tile = tiles.get(worldPosNorth);
                if (tile != null && tile.aBoolean1323) {
                    tileQueue.addLast(tile);
                }
            }
            if (x > absoluteCameraX && x > 0) {
                val worldPosWest = new Vector3i(x - 1, y, plane);
                SceneTile tile = tiles.get(worldPosWest);
                if (tile != null && tile.aBoolean1323) {
                    tileQueue.addLast(tile);
                }
            }
            if (y > absoluteCameraY && y > 0) {
                val worldPosSouth = new Vector3i(x, y - 1, plane);
                SceneTile tile = tiles.get(worldPosSouth);
                if (tile != null && tile.aBoolean1323) {
                    tileQueue.addLast(tile);
                }
            }

        } while (true);
    }

    public void reset() {
        tiles.clear();

        if (clusters != null)
            for (int i = 0; i < PLANE_COUNT; i++) {
                for (int j = 0; j < clusterCounts[i]; j++) {
                    clusters[i][j] = null;
                }

                clusterCounts[i] = 0;
            }

        if (shortLivedGameObjects != null)
            for (int k1 = 0; k1 < shortLivedObjectCount; k1++) {
                shortLivedGameObjects[k1] = null;
            }

        shortLivedObjectCount = 0;
        if (interactables != null)
            Arrays.fill(interactables, null);
    }

    public void resetTiles() {
        /*lastModifiedTiles.forEach(tile -> {
            tile.tileHighlighted = false;
            tile.temporaryObject = null;
            tile.temporaryObjectAttributes = 0;
            tile.temporaryShapedTile = null;
            tile.temporarySimpleTile = null;
        });*/
    }

    /**
     * Sets the collision plane of a tile (i.e. the plane that it derives its
     * collision data from).
     *
     * @param x              The x coordinate of the tile.
     * @param y              The y coordinate of the tile.
     * @param plane          The plane of the tile.
     * @param collisionPlane The collision plane of the tile.
     */
    public void setCollisionPlane(int x, int y, int plane, int collisionPlane) {
        SceneTile tile = tiles.get(new Vector3i(x, y, plane));
        if (tile == null)
            return;

        tile.collisionPlane = collisionPlane;
    }

    public void shadeObjects(int drawX, int drawY, int drawZ) {
        //TODO Do this dynamically.

        for (int z = 0; z < planeCount; z++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < this.length; y++) {
                    val worldPos = new Vector3i(x, y, z);
                    SceneTile tile = tiles.get(worldPos);

                    if (tile != null) {
                        val wall = tile.getObject(TypeFilter.wallObjects);

                        if (wall != null && wall.primary instanceof Mesh) {
                            Mesh primary = (Mesh) wall.primary;
                            method307(z, 1, 1, x, y, primary);

                            if (wall.secondary instanceof Mesh) {
                                Mesh secondary = (Mesh) wall.secondary;
                                method307(z, 1, 1, x, y, secondary);
                                mergeNormals(primary, secondary, 0, 0, 0, false);

                                wall.secondary = (secondary.toModelInstance(secondary.ambient, secondary.contrast, drawX, drawY, drawZ));
                            }


                            wall.primary = (primary.toModelInstance(primary.ambient, primary.contrast, drawX, drawY, drawZ));
                        }

                        val worldObjects = tile.getObjects(TypeFilter.genericAndRoof);
                        for (DefaultWorldObject object : worldObjects) {
                            if (object != null && object.primary instanceof Mesh) {
                                Mesh primary = (Mesh) object.primary;
                                method307(z, object.maximum.x - object.getX() + 1, object.maximum.y - object.getY() + 1, x, y, primary);

                                object.primary = (primary.toModelInstance(primary.ambient, primary.contrast, drawX, drawY, drawZ));
                            }
                        }
                        val wallDeco = tile.getObject(TypeFilter.wallDecoration);
                        if (wallDeco != null && wallDeco.primary instanceof Mesh) {
                            Mesh primary = (Mesh) wallDeco.primary;
                            wallDeco.primary = (primary.toModelInstance(primary.ambient, primary.contrast, drawX, drawY, drawZ));
                        }
                        val decoration = tile.getObject(TypeFilter.groundDecoration);
                        if (decoration != null && decoration.primary instanceof Mesh) {
                            Mesh primary = (Mesh) decoration.primary;
                            method306(primary, x, y, z);
                            decoration.primary = (primary.toModelInstance(primary.ambient, primary.contrast, drawX, drawY, drawZ));
                        }
                    }
                }
            }
        }
    }

    public int toChunkTileX(int wTileX) {
        return wTileX/* - offsetX*/;
    }

    public int toChunkTileY(int wTileY) {
        return wTileY/* - offsetY*/;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
        this.offsetX = chunk.offsetX;
        this.offsetY = chunk.offsetY;
    }

    public byte getSelectedUnderlay() {
        int plane = Options.currentHeight.get();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                SceneTile selectedTile = tiles.get(new Vector3i(x, y, plane));
                if (selectedTile != null) {
                    if (selectedTile.tileSelected) {
                        return getMapRegion().underlays[plane][x][y];
                    }

                }
            }
        }
        return -1;
    }

    public byte getSelectedOverlay() {
        int plane = Options.currentHeight.get();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                SceneTile selectedTile = tiles.get(new Vector3i(x, y, plane));
                if (selectedTile != null) {
                    if (selectedTile.tileSelected) {
                        return getMapRegion().overlays[plane][x][y];
                    }

                }
            }
        }
        return -1;
    }

    public BitFlag getSelectedFlag() {
        int plane = Options.currentHeight.get();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                SceneTile selectedTile = tiles.get(new Vector3i(x, y, plane));
                if (selectedTile != null) {
                    if (selectedTile.tileSelected) {
                        return new BitFlag(selectedTile.tileFlags);
                    }

                }
            }
        }
        return new BitFlag();
    }

    public int getSelectedHeight() {
        int plane = Options.currentHeight.get();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                SceneTile selectedTile = tiles.get(new Vector3i(x, y, plane));
                if (selectedTile != null) {
                    if (selectedTile.tileSelected) {
                        if (plane > 0) {
                            return getMapRegion().tileHeights[plane][x][y] - getMapRegion().tileHeights[plane - 1][x][y];
                        } else {
                            return getMapRegion().tileHeights[plane][x][y];
                        }
                    }

                }
            }
        }
        return 1;
    }

    public int getSelectedOverlayShape() {
        int plane = Options.currentHeight.get();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                SceneTile selectedTile = tiles.get(new Vector3i(x, y, plane));
                if (selectedTile != null) {
                    if (selectedTile.tileSelected) {
                        return getMapRegion().overlayShapes[plane][x][y];
                    }

                }
            }
        }
        return -1;
    }

    public Stream<SceneTile> nonNullStream(int z) {
        return tiles.values().stream().filter(Objects::nonNull).filter(tile -> tile.worldPos.z == z);
    }

    public Stream<SceneTile> nonNullStream() {
        return IntStream.range(0, 4).boxed().flatMap(this::nonNullStream);
    }

    public SceneTile getTileOrDefault(Vector3i worldPos, Function<Vector3i, SceneTile> defaultProvider) {
        SceneTile tile = tiles.get(worldPos);
        if (tile == null)
            tile = defaultProvider.apply(worldPos);
        return tile;
    }


    public void raycast(Camera camera) {
        val position = new Vector3f(camera.getPosition().x / 128f, camera.getPosition().y / 128f, camera.getPosition().z / 128f);
        val target = new Vector3f(camera.getLookPosition().x / 128f, camera.getLookPosition().y / 128f, camera.getLookPosition().z / 128f);
        var rayCallback = new CollisionWorld.ClosestRayResultCallback(position, target);
        ToolRegister.getActiveTool().raycastCallback(this, rayCallback, true);
    }


}