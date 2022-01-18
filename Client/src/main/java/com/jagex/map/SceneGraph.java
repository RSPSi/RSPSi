package com.jagex.map;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.rspsi.options.*;
import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.major.map.RenderFlags;
import org.major.map.TileAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.jagex.Client;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.chunk.Chunk;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.Renderable;
import com.jagex.entity.model.Mesh;
import com.jagex.entity.model.VertexNormal;
import com.jagex.entity.object.ObjectGroup;
import com.jagex.io.Buffer;
import com.jagex.map.object.DefaultWorldObject;
import com.jagex.map.object.GameObject;
import com.jagex.map.object.GroundDecoration;
import com.jagex.map.object.Wall;
import com.jagex.map.object.WallDecoration;
import com.jagex.map.object.WorldObjectType;
import com.jagex.map.tile.SceneTile;
import com.jagex.map.tile.ShapedTile;
import com.jagex.map.tile.SimpleTile;
import com.jagex.map.tile.TileUtils;
import com.jagex.util.BitFlag;
import com.jagex.util.Constants;
import com.jagex.util.ObjectKey;
import com.rspsi.datasets.ObjectDataset;
import com.rspsi.game.save.StateChangeType;
import com.rspsi.game.save.TileChange;
import com.rspsi.game.save.object.DeleteObject;
import com.rspsi.game.save.object.SpawnObject;
import com.rspsi.game.save.object.state.ObjectState;
import com.rspsi.game.save.tile.FlagChange;
import com.rspsi.game.save.tile.HeightChange;
import com.rspsi.game.save.tile.ImportChange;
import com.rspsi.game.save.tile.OverlayChange;
import com.rspsi.game.save.tile.UnderlayChange;
import com.rspsi.game.save.tile.state.FlagState;
import com.rspsi.game.save.tile.state.HeightState;
import com.rspsi.game.save.tile.state.ImportTileState;
import com.rspsi.game.save.tile.state.OverlayState;
import com.rspsi.game.save.tile.state.UnderlayState;
import com.rspsi.core.misc.BrushType;
import com.rspsi.core.misc.CopyOptions;
import com.rspsi.core.misc.DeleteOptions;
import com.rspsi.core.misc.ExportOptions;
import com.rspsi.core.misc.JsonUtil;
import com.rspsi.core.misc.Location;
import com.rspsi.core.misc.TileArea;
import com.rspsi.core.misc.ToolType;
import com.rspsi.core.misc.Vector2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
	public static List<Runnable> onCycleEnd = new ArrayList<>();
	public static int mouseButton = -1;
	public static boolean minimapUpdate;
	public static Optional<TileChange<?>> currentState = Optional.empty();
	public static ObservableList<TileChange<?>> undoList = FXCollections.observableList(Lists.newLinkedList());
	public static ObservableList<TileChange<?>> redoList = FXCollections.observableList(Lists.newLinkedList());
	public static int hoveredTileX = -1;
	public static int hoveredTileY = -1;
	public static int hoveredTileZ = -1;
	public static int anInt475;
	public static boolean lowMemory = true;
	public static int clickStartX = -1;
	public static int activePlane;
	public static boolean shiftDown;
	public static boolean ctrlDown;
	public static boolean mouseWasDown;
	public static boolean altDown;
	static boolean mouseIsDown;
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
	public ArrayDeque<SceneTile> tileQueue;
	public int absoluteCameraX;
	public int absoluteCameraY;
	public int length;
	public SceneTile[][][] tiles;
	public int width;
	public Chunk chunk;
	public int offsetX, offsetY;
	public List<DefaultWorldObject> selectedObjects;
	boolean[][] aBooleanArrayArray492;
	boolean[][][][] aBooleanArrayArrayArrayArray491;
	SceneCluster[] aClass47Array476;
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
	GameObject[] shortLivedGameObjects;//This can probably be removed
	int shortLivedObjectCount;
	int anInt488;
	int[] anIntArray486;
	int[] anIntArray487;
	// int getMapRegion().tileHeights[][][];
	int[][][] anIntArrayArrayArray445;
	int planeCount;
	private List<SceneTile> lastHighightedTiles = Lists.newArrayList();
	private List<SceneTile> absoluteLast = Lists.newArrayList();
	private List<SceneTile> lastModifiedTiles = Lists.newArrayList();
	public SceneGraph(int width, int length, int planes) {
		shortLivedGameObjects = new GameObject[5000];
		anIntArray486 = new int[10000];
		anIntArray487 = new int[10000];
		planeCount = planes;
		this.width = width;
		this.length = length;
		tiles = new SceneTile[planes][width][length];
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < length; y++) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}
		}
		anIntArrayArrayArray445 = new int[planes][width + 1][length + 1];
		selectedObjects = Lists.newArrayList();
		interactables = new GameObject[100];
		clusters = new SceneCluster[PLANE_COUNT][500];
		clusterCounts = new int[PLANE_COUNT];
		aBooleanArrayArrayArrayArray491 = new boolean[24][32][(Options.renderDistance.get() * 2) + 1][(Options.renderDistance.get() * 2) + 1];
		aClass47Array476 = new SceneCluster[500];
		interactables = new GameObject[100];
		tileQueue = new ArrayDeque<SceneTile>();
		reset();
	}

	public static void clearStates() {
		currentState = Optional.empty();
		undoList.clear();
		redoList.clear();
		minimapUpdate = true;
		System.out.println("STATE CLEARED");
	}

	public static void undo() {
		if (undoList.isEmpty())
			return;
		System.out.println("UNDO");
		TileChange<?> change = undoList.get(0);
		undoList.remove(0);


		switch (change.getType()) {
			case OBJECT_SPAWN:
			case OBJECT_DELETE:
			case OVERLAY:
			case TILE_FLAG:
			case TILE_HEIGHT:
			case UNDERLAY:
			case IMPORT:
				TileChange<?> currState = ((TileChange<?>) change).getInverse();
				redoList.add(0, currState);
				System.out.println("UNDO BACKUP");
				break;
			default:
				break;

		}
		change.restoreStates();
		System.out.println("UNDO STATE LOAD " + change.getType());
		onCycleEnd.add(() -> {
			Client.getSingleton().sceneGraph.tileQueue.clear();
			Client.getSingleton().sceneGraph.getMapRegion().updateTiles();
			Client.getSingleton().sceneGraph.shadeObjects(64, -50, -10, -50, 768);
			minimapUpdate = true;
		});
	}

	public static void redo() {
		if (redoList.isEmpty())
			return;
		System.out.println("REDO");
		TileChange<?> change = redoList.get(0);
		redoList.remove(0);


		switch (change.getType()) {
			case OBJECT_SPAWN:
			case OBJECT_DELETE:
			case OVERLAY:
			case TILE_FLAG:
			case TILE_HEIGHT:
			case UNDERLAY:
			case IMPORT:
				TileChange<?> currState = ((TileChange<?>) change).getInverse();
				undoList.add(0, currState);
				System.out.println("REDO BACKUP");
				break;
			default:
				break;

		}

		change.restoreStates();
		System.out.println("REDO STATE LOAD " + change.getType());

		onCycleEnd.add(() -> {
			Client.getSingleton().sceneGraph.tileQueue.clear();
			Client.getSingleton().sceneGraph.getMapRegion().updateTiles();
			Client.getSingleton().sceneGraph.shadeObjects(64, -50, -10, -50, 768);
			minimapUpdate = true;
		});
	}

	public static void commitChanges() {
		if (!currentState.isPresent())
			return;

		TileChange<?> change = currentState.get();
		currentState = Optional.empty();
		if (change.containsChanges()) {
			undoList.add(0, change);
			redoList.clear();
			System.out.println("COMMIT");
			minimapUpdate = true;
		}
	}

	private static DefaultWorldObject getTemporaryOrDefault(SceneTile tile, WorldObjectType type) {
		if (temporaryTypeExists(tile, type))
			return tile.temporaryObject.get();
		switch (type) {
			case GROUND_DECORATION:
				return tile.groundDecoration;
			case WALL:
				return tile.wall;
			case WALL_DECORATION:
				return tile.wallDecoration;
			default:
				return null;

		}
	}

	public static void setMouseIsDown(boolean clicked) {
		if (!clicked) {
			commitChanges();
		}
		if (SceneGraph.mouseIsDown && !clicked) {
			SceneGraph.clickStartX = -1;
			SceneGraph.clickStartY = -1;
			SceneGraph.mouseWasDown = true;
		} else if (SceneGraph.mouseIsDown && clicked) {

		}
		SceneGraph.mouseIsDown = clicked;
	}

	public static void setMousePos(int i, int j) {
		clickX = j;
		clickY = i;
	}

	private static boolean temporaryTypeExists(SceneTile tile, WorldObjectType type) {
		return tile.temporaryObject.isPresent() && tile.temporaryObject.get().getType() == type;
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

	public boolean currentStateCorrect() {
		switch (Options.currentTool.get()) {
			case SELECT_OBJECT:
			case SELECT_TILE:
				return true;
			default:
				return currentState.isPresent() && currentState.get().getType().getTool() == Options.currentTool.get();

		}
	}

	public void initChanges() {
		System.out.println("INIT CHANGES");
		switch (Options.currentTool.get()) {
			case IMPORT_SELECTION:
				currentState = Optional.of(new ImportChange());
				break;
			case MODIFY_HEIGHT:
				currentState = Optional.of(new HeightChange());
				break;
			case SET_FLAGS:
				currentState = Optional.of(new FlagChange());
				break;
			case PAINT_OVERLAY:
				currentState = Optional.of(new OverlayChange());
				break;
			case PAINT_UNDERLAY:
				currentState = Optional.of(new UnderlayChange());
				break;
			case SPAWN_OBJECT:
				currentState = Optional.of(new SpawnObject());
				break;
			case DELETE_OBJECT:
				currentState = Optional.of(new DeleteObject());
				break;

			default:
				break;

		}
	}

	public boolean addEntity(int x, int y, int plane, Renderable renderable, int yaw, ObjectKey key, int renderHeight,
	                         int delta, boolean accountForYaw, boolean temporary) {
		if (renderable == null)
			return true;

		int minX = x - delta;
		int minY = y - delta;
		int maxX = x + delta;
		int maxY = y + delta;

		if (accountForYaw) {
			if (yaw > 640 && yaw < 1408) {
				maxY += 128;
			}
			if (yaw > 1152 && yaw < 1920) {
				maxX += 128;
			}
			if (yaw > 1664 || yaw < 384) {
				minY -= 128;
			}
			if (yaw > 128 && yaw < 896) {
				minX -= 128;
			}
		}

		minX /= 128;
		minY /= 128;
		maxX /= 128;
		maxY /= 128;
		return addRenderable(plane, minX, minY, maxX - minX + 1, maxY - minY + 1, x, y, renderHeight, renderable, yaw,
				true, key, temporary);
	}

	public GroundDecoration addFloorDecoration(int x, int y, int z, Renderable renderable, ObjectKey key, int meanY, boolean temporary) {
		if (renderable == null)
			return null;
		GroundDecoration decoration = new GroundDecoration(key, (x) * 128 + 64,
				(y) * 128 + 64, meanY);
		decoration.setPrimary(renderable);
		decoration.setPlane(z);

		if (!temporary && currentState.isPresent() && currentState.get().getType() == StateChangeType.OBJECT_SPAWN) {
			ObjectState state = new ObjectState(x, y, z);
			state.setKey(key);
			((SpawnObject) currentState.get()).preserveTileState(state);
		}

		SceneTile tile = getTile(z, x, y);

		if (temporary) {
			tile.temporaryObject = Optional.of(decoration);
			lastModifiedTiles.add(tile);
			System.out.println("Spawned ground deco " + decoration.toString());
			//tileQueue.push(tile);
		} else {
			tile.groundDecoration = decoration;
			tile.hasUpdated = true;
		}
		return decoration;
	}

	public void addGroundItem(int x, int y, int z, int key, Renderable primary, Renderable secondary,
	                          Renderable tertiary, int plane) {
		GroundItem item = new GroundItem();
		item.setPrimary(primary);
		item.setX(x * 128 + 64);
		item.setY(y * 128 + 64);
		item.setPlane(plane);
		item.setKey(key);
		item.setTertiary(secondary);
		item.setSecondary(tertiary);
		int itemHeight = 0;
		SceneTile tile = getTile(z, x, y);

		if (tile != null) {
			for (int i = 0; i < tile.objectCount; i++) {
				if (tile.gameObjects[i].getPrimary() instanceof Mesh) {
					int l1 = ((Mesh) tile.gameObjects[i].getPrimary()).anInt1654;
					if (l1 > itemHeight) {
						itemHeight = l1;
					}
				}
			}

		}
		item.setItemHeight(itemHeight);
		if (tiles[z][x][y] == null) {
			tiles[z][x][y] = new SceneTile(x, y, z);
		}
		tiles[z][x][y].groundItem = item;
	}

	public ObjectKey addObject(int tileX, int tileY, int plane, int objectId, int objectType, int rotation,
	                           boolean temporary) {
		if (tiles[plane][tileX][tileY] == null) {
			tiles[plane][tileX][tileY] = new SceneTile(tileX, tileY, plane);
		}

		/*
		 * DefaultWorldObject object = objectTool.get(); tile.temporaryObject =
		 * Optional.of(object); tileQueue.pushFront(tile);
		 */
		return getMapRegion().spawnObjectToWorld(this, objectId, tileX, tileY, plane, objectType,
				rotation, temporary);


	}

	public boolean addObject(int x, int y, int plane, int width, int length, Renderable renderable, ObjectKey key, int yaw,
	                         int j, boolean temporary) {
		if (renderable == null)
			return true;

		int absoluteX = (x) * 128 + 64 * width;
		int absoluteY = (y) * 128 + 64 * length;
		return addRenderable(plane, x, y, width, length, absoluteX, absoluteY, j, renderable, yaw, false, key,
				temporary);
	}

	private boolean addRenderable(int plane, int minX, int minY, int deltaX, int deltaY, int centreX, int centreY,
	                              int renderHeight, Renderable renderable, int yaw, boolean flag, ObjectKey key, boolean temporary) {


		for (int x = minX; x < minX + deltaX; x++) {
			for (int y = minY; y < minY + deltaY; y++) {
				if (x < 0 || y < 0 || x >= width || y >= length)
					return false;
				SceneTile tile = tiles[plane][x][y];
				if (!temporary && tile != null && tile.objectCount >= 5)
					return false;

			}
		}


		GameObject object = new GameObject(key, minX, minY, plane);
		object.setPlane(plane);
		object.centreX = centreX;
		object.centreY = centreY;
		object.setRenderHeight(renderHeight);
		object.setPrimary(renderable);
		object.yaw = yaw;
		object.maxX = minX + deltaX - 1;
		object.maxY = minY + deltaY - 1;
		object.minX = minX;
		object.minY = minY;


		if (!temporary) {
			if (currentState.isPresent()) {
				TileChange change = currentState.get();
				StateChangeType type = change.getType();
				if(type == StateChangeType.OBJECT_SPAWN) {
					for (int x = minX; x < minX + deltaX; x++) {
						for (int y = minY; y < minY + deltaY; y++) {

							ObjectState state = new ObjectState(x, y, plane);
							state.setKey(key);
							((SpawnObject) change).preserveTileState(state);
						}
					}
				}
			}
		}

		for (int x = minX; x < minX + deltaX; x++) {
			for (int y = minY; y < minY + deltaY; y++) {
				int attributes = 0;

				if (x > minX) {
					attributes++;// EAST
				}
				if (y < minY + deltaY - 1) {
					attributes += 0b10;// NORTH
				}
				if (x < minX + deltaX - 1) {
					attributes |= 0b100;// WEST
				}
				if (y > minY) {
					attributes += 0b1000;// SOUTH
				}

				for (int z = plane; z >= 0; z--) {
					if (tiles[z][x][y] == null) {
						tiles[z][x][y] = new SceneTile(x, y, z);
					}
				}


				SceneTile tile = tiles[plane][x][y];
				if (temporary) {
					tile.temporaryObject = Optional.of(object);
					tile.temporaryObjectAttributes = Optional.of(attributes);
					lastModifiedTiles.add(tile);
					//tileQueue.push(tile);
				} else {
					tile.gameObjects[tile.objectCount] = object;
					tile.objectAttributes[tile.objectCount] = attributes;
					tile.hasUpdated = true;
					tile.shiftAttributes(attributes);
					tile.objectCount++;
				}
			}

		}

		if (flag) {
			shortLivedGameObjects[shortLivedObjectCount++] = object;
		}

		return true;
	}

	public boolean addRenderable(int plane, int worldY, Renderable renderable, int orientation, int i1, int j1,
	                             int renderHeight, int minX, int i2, ObjectKey key, int minY, boolean temporary) {
		if (renderable == null)
			return true;

		return addRenderable(plane, minX, minY, i2 - minX + 1, i1 - minY + 1, j1, worldY, renderHeight, renderable,
				orientation, true, key, temporary);
	}

	public void addTemporaryObject(int tileX, int tileY, int plane) {
		if (Options.currentObject.get() != null) {


			highlightTile(tileX, tileY, plane);
			/*
			 * DefaultWorldObject object = objectTool.get(); tile.temporaryObject =
			 * Optional.of(object); tileQueue.pushFront(tile);
			 */
			getMapRegion().spawnObjectToWorld(this, Options.currentObject.get().getId(), tileX,
					tileY, plane, Options.currentObject.get().getType(), Options.rotation.get(), true);

		}

	}

	/*
	 * public int getConfig(int x, int y, int z, int key) { SceneTile tile =
	 * tiles[z][x][y];
	 *
	 * if (tile == null) return -1; else if (tile.wall != null && tile.wall.getId()
	 * == key) return tile.wall.getConfig() & 0xff; else if (tile.wallDecoration !=
	 * null && tile.wallDecoration.getId() == key) return
	 * tile.wallDecoration.getConfig() & 0xff; else if (tile.groundDecoration !=
	 * null && tile.groundDecoration.getId() == key) return
	 * tile.groundDecoration.getConfig() & 0xff;
	 *
	 * for (int index = 0; index < tile.gameObjects.length; index++) { if
	 * (tile.gameObjects[index] != null && tile.gameObjects[index].getId() == key)
	 * return tile.gameObjects[index].getConfig() & 0xff; }
	 *
	 * return -1; }
	 */

	public void addTile(int plane, int x, int y, int type, int orientation, int texture, int centreZ, int eastZ,
	                    int northEastZ, int northZ, int centreUnderColour, int eastUnderColour, int neUnderColour,
	                    int northUnderColour, int centreOverColour, int eastOverColour, int neOverColour, int northOverColour,
	                    int underlayColour, int textureColour, int colour, int copy_texture, int copy_color, boolean tex, byte flags) {
		if (type == 434 && copy_texture != 24) {
			SimpleTile tile = new SimpleTile(centreUnderColour, eastUnderColour, neUnderColour, northUnderColour, texture,
					underlayColour, centreZ == eastZ && centreZ == northEastZ && centreZ == northZ, colour, tex);
			for (int z = plane; z >= 0; z--) {
				if (tiles[z][x][y] == null) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}
			tiles[plane][x][y].shape = null;
			tiles[plane][x][y].simple = tile;
			tiles[plane][x][y].tileFlags = flags;
			tiles[plane][x][y].hasUpdated = true;
		} else if (type == 0) {
			SimpleTile tile = new SimpleTile(centreUnderColour, eastUnderColour, neUnderColour, northUnderColour, -1,
					underlayColour, false, textureColour, tex);
			for (int z = plane; z >= 0; z--) {
				if (tiles[z][x][y] == null) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}
			tiles[plane][x][y].shape = null;
			tiles[plane][x][y].simple = tile;
			tiles[plane][x][y].tileFlags = flags;
			tiles[plane][x][y].hasUpdated = true;
		} else if (type == 1) {
			SimpleTile tile = new SimpleTile(centreOverColour, eastOverColour, neOverColour, northOverColour, texture,
					textureColour, centreZ == eastZ && centreZ == northEastZ && centreZ == northZ, colour, tex);
			for (int z = plane; z >= 0; z--) {
				if (tiles[z][x][y] == null) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}

			tiles[plane][x][y].shape = null;
			tiles[plane][x][y].simple = tile;
			tiles[plane][x][y].tileFlags = flags;
			tiles[plane][x][y].hasUpdated = true;
		} else {
			ShapedTile tile = new ShapedTile(y, centreOverColour, northUnderColour, northEastZ, texture,
					neOverColour, orientation, centreUnderColour, underlayColour, neUnderColour, northZ, eastZ, centreZ,
					type, northOverColour, eastOverColour, eastUnderColour, x, textureColour, copy_texture, copy_color, tex);
			for (int z = plane; z >= 0; z--) {
				if (tiles[z][x][y] == null) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}
			tiles[plane][x][y].simple = null;
			tiles[plane][x][y].shape = tile;
			tiles[plane][x][y].tileFlags = flags;
			tiles[plane][x][y].hasUpdated = true;
		}
	}

	public void addTemporaryTile(int plane, int x, int y, int type, int orientation, int texture, int underlayColour, int textureColour) {

		int centreZ = getMapRegion().tileHeights[plane][x][y];
		int eastZ = getMapRegion().tileHeights[plane][x + 1][y];
		int northEastZ = getMapRegion().tileHeights[plane][x + 1][y + 1];
		int northZ = getMapRegion().tileHeights[plane][x][y + 1];
		if (type == 0) {
			SimpleTile tile = new SimpleTile(underlayColour, underlayColour, underlayColour, underlayColour, -1,
					underlayColour, false, 0, false);
			for (int z = plane; z >= 0; z--) {
				if (tiles[z][x][y] == null) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}
			tiles[plane][x][y].temporaryShapedTile = Optional.empty();
			tiles[plane][x][y].temporarySimpleTile = Optional.of(tile);
		} else if (type == 1) {
			SimpleTile tile = new SimpleTile(textureColour, textureColour, textureColour, textureColour, texture,
					textureColour, centreZ == eastZ && centreZ == northEastZ && centreZ == northZ, 0, false);
			for (int z = plane; z >= 0; z--) {
				if (tiles[z][x][y] == null) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}

			tiles[plane][x][y].temporaryShapedTile = Optional.empty();
			tiles[plane][x][y].temporarySimpleTile = Optional.of(tile);
		} else {
			ShapedTile tile = new ShapedTile(y, textureColour, underlayColour, northEastZ, texture,
					textureColour, orientation, underlayColour, underlayColour, underlayColour, northZ, eastZ, centreZ,
					type, textureColour, textureColour, underlayColour, x, textureColour, -1, -1, false);
			for (int z = plane; z >= 0; z--) {
				if (tiles[z][x][y] == null) {
					tiles[z][x][y] = new SceneTile(x, y, z);
				}
			}
			tiles[plane][x][y].temporarySimpleTile = Optional.empty();
			tiles[plane][x][y].temporaryShapedTile = Optional.of(tile);
		}

		lastModifiedTiles.add(tiles[plane][x][y]);
	}

	public void addWall(ObjectKey key, int x, int y, int plane, int i, Renderable primary, Renderable secondary, int height,
	                    int j1, boolean temporary) {
		if (primary == null && secondary == null)
			return;

		Wall wall = new Wall(key, (x) * 128 + 64, (y) * 128 + 64, height);

		wall.setPrimary(primary);
		wall.setSecondary(secondary);
		wall.setPlane(plane);
		wall.anInt276 = i;
		wall.anInt277 = j1;

		for (int z = plane; z >= 0; z--) {
			if (tiles[z][x][y] == null) {
				tiles[z][x][y] = new SceneTile(x, y, z);
			}
		}
		if (!temporary && currentState.isPresent() && currentState.get().getType() == StateChangeType.OBJECT_SPAWN) {
			ObjectState state = new ObjectState(x, y, plane);
			state.setKey(key);
			((SpawnObject) currentState.get()).preserveTileState(state);
		}
		SceneTile tile = tiles[plane][x][y];
		if (temporary) {
			tile.temporaryObject = Optional.of(wall);
			lastModifiedTiles.add(tile);
			//tileQueue.push(tile);
		} else {
			//System.out.println("Set tile wall " + plane + ":" + x + ":" + y);
			tile.wall = wall;
			tile.hasUpdated = true;
		}
	}

	public void addWallDecoration(ObjectKey key, int y, int orientation, int plane, int xDisplacement, int height,
	                              Renderable renderable, int x, int yDisplacement, int attributes, boolean temporary) {
		if (renderable == null)
			return;
		WallDecoration decoration = new WallDecoration(key, (x) * 128 + 64 + xDisplacement,
				(y) * 128 + 64 + yDisplacement, height);
		decoration.setPrimary(renderable);
		decoration.setAttributes(attributes);
		decoration.setOrientation(orientation);
		decoration.setPlane(plane);

		for (int z = plane; z >= 0; z--) {
			if (tiles[z][x][y] == null) {
				tiles[z][x][y] = new SceneTile(x, y, z);
			}
		}
		if (!temporary && currentState.isPresent() && currentState.get().getType() == StateChangeType.OBJECT_SPAWN) {
			ObjectState state = new ObjectState(x, y, plane);
			state.setKey(key);
			((SpawnObject) currentState.get()).preserveTileState(state);
		}
		SceneTile tile = tiles[plane][x][y];
		if (temporary) {
			tile.temporaryObject = Optional.of(decoration);
			lastModifiedTiles.add(tile);
			//tileQueue.push(tile);
		} else {
			tile.wallDecoration = decoration;
			tile.hasUpdated = true;
		}
	}

	public void clearGroundItem(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null)
			return;

		tile.groundItem = null;
	}

	public void deleteObjects() {
		for (DefaultWorldObject obj : Lists.newArrayList(selectedObjects)) {
			ObjectKey key = obj.getKey();

			int x = key.getX();
			int y = key.getY();
			int z = obj.getPlane();

			obj.setSelected(false);

			SceneTile tile = tiles[z][x][y];
			if (tile != null) {
				tile.removeByUID(key);
			}

		}
		this.selectedObjects.clear();
	}

	private Rectangle getRectPredicate(Predicate<SceneTile> condition) {
		int lowestX = width;
		int lowestY = length;
		int highestX = 0;
		int highestY = 0;
		int plane = Options.currentHeight.get();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < length; y++) {
				SceneTile tile = this.getTile(plane, x, y);
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

	public void copyObjects() {
		List<SceneTileData> selectedTiles = Lists.newArrayList();
		int minX = width;
		int minY = length;
		for (DefaultWorldObject obj : Lists.newArrayList(selectedObjects)) {
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


		for (DefaultWorldObject obj : Lists.newArrayList(selectedObjects)) {
			ObjectKey key = obj.getKey();

			int x = key.getX();
			int y = key.getY();
			int z = obj.getPlane();

			SceneTileData sceneTileData = new SceneTileData();

			if (obj.getType() == WorldObjectType.GAME_OBJECT) {
				sceneTileData.setGameObjectIds(new int[]{obj.getId()});
				sceneTileData.setGameObjectConfigs(new int[]{obj.getConfig()});
			} else if (obj.getType() == WorldObjectType.GROUND_DECORATION) {
				sceneTileData.setGroundDecoId(obj.getId());
				sceneTileData.setGroundDecoConfig(obj.getConfig());
			} else if (obj.getType() == WorldObjectType.WALL) {
				sceneTileData.setWallId(obj.getId());
				sceneTileData.setWallConfig(obj.getConfig());
			} else if (obj.getType() == WorldObjectType.WALL_DECORATION) {
				sceneTileData.setWallDecoId(obj.getId());
				sceneTileData.setWallDecoConfig(obj.getConfig());
			}

			sceneTileData.setX(x - minX);
			sceneTileData.setY(y - minY);
			sceneTileData.setZ(z);
			selectedTiles.add(sceneTileData);
			obj.setSelected(false);

		}
		selectedObjects.clear();
		Lists.reverse(selectedTiles);
		Options.importData = selectedTiles;
	}

	public void copyTiles(CopyOptions copyOptions) {
		List<SceneTile> selectedTiles = Lists.newArrayList();

		int minX = width;
		int minY = length;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < length; y++) {
				if (tiles[Options.currentHeight.get()][x][y] != null && tiles[Options.currentHeight.get()][x][y].tileSelected) {

					if (copyOptions.copyTilesAbove()) {
						for (int z = Options.currentHeight.get(); z < 4; z++) {
							if (tiles[z][x][y] == null) {
								tiles[z][x][y] = new SceneTile(x, y, z);
							}
							selectedTiles.add(tiles[z][x][y]);
						}
					} else {
						selectedTiles.add(tiles[Options.currentHeight.get()][x][y]);
					}
					if (x < minX) {
						minX = x;
					}
					if (y < minY) {
						minY = y;
					}

				}
			}
		}

		int fMinY = minY;
		int fMinX = minX;

		List<SceneTileData> data = selectedTiles.stream().map(sceneTile -> {
			int z = sceneTile.getSceneLocation().getZ();
			int x = sceneTile.getSceneLocation().getX();
			int y = sceneTile.getSceneLocation().getY();
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
		Options.importData = data;
	}

	public void deleteSelectedTiles(DeleteOptions deleteOptions) {
		for (int z = Options.currentHeight.get(); z <= (deleteOptions.deleteTilesAbove() ? 3 : Options.currentHeight.get()); z++)
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < length; y++) {
					if (tiles[z][x][y] != null
							&& tiles[Options.currentHeight.get()][x][y].tileSelected) {

						SceneTile tile = tiles[z][x][y];
						if (deleteOptions.deleteGameObjects()) {
							tile.gameObjects = new GameObject[5];
							tile.clearAttributes();
							tile.hasObjects = false;
							tile.objectCount = 0;
						}
						if (deleteOptions.deleteGroundDecorations()) {
							tile.groundDecoration = null;
						}
						if (deleteOptions.deleteWallDecorations()) {
							tile.wallDecoration = null;
						}
						if (deleteOptions.deleteWalls()) {
							tile.wall = null;
						}

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

					}
				}
			}
		chunk.mapRegion.updateTiles();
	}

	public void displaceWallDecor(int x, int y, int z, int displacement) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null)
			return;

		WallDecoration decoration = tile.wallDecoration;
		if (decoration == null)
			return;

		int absX = (x) * 128 + 64;
		int absY = (y) * 128 + 64;
		decoration.setX(absX + (decoration.getX() - absX) * displacement / 16);
		decoration.setY(absY + (decoration.getY() - absY) * displacement / 16);
	}

	public void drawMinimapTile(int[] raster, int x, int y, int plane, int scanStart, int scanLength) {
		SceneTile tile = tiles[plane][x][y];
		if (tile == null)
			return;

		SimpleTile simple = tile.simple;
		if (simple != null) {

			int colour = simple.getTileColour();
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

	public void exportSelectedTiles(ExportOptions exportOptions, File file)
			throws IOException {
		LinkedList<SceneTileData> selectedTiles = Lists.newLinkedList();
		int minX = width;
		int minY = length;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < length; y++) {
				if (tiles[Options.currentHeight.get()][x][y] != null && tiles[Options.currentHeight.get()][x][y].tileSelected) {
					if (x < minX) {
						minX = x;
					}
					if (y < minY) {
						minY = y;
					}

				}
			}
		}

		for (int z = Options.currentHeight.get(); z < (exportOptions.exportTilesAbove() ? 4 : Options.currentHeight.get() + 1); z++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < length; y++) {
					if (tiles[Options.currentHeight.get()][x][y] != null
							&& tiles[Options.currentHeight.get()][x][y].tileSelected) {

						SceneTileData sceneTileData;
						if (tiles[z][x][y] == null) {
							sceneTileData = new SceneTileData();
						} else {
							sceneTileData = new SceneTileData(exportOptions, tiles[z][x][y], x, y);
						}

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
						sceneTileData.setX(x - minX);
						sceneTileData.setY(y - minY);
						sceneTileData.setZ(z);
						selectedTiles.add(sceneTileData);
					}
				}
			}
		}
		Lists.reverse(selectedTiles);
		ObjectMapper mapper = JsonUtil.getDefaultMapper();
		mapper.writeValue(file, selectedTiles);

	}

	public void fill(int plane) {
		//activePlane = plane;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < length; y++) {
				if (tiles[plane][x][y] == null) {
					tiles[plane][x][y] = new SceneTile(x, y, plane);
				}
			}
		}
	}

	public GameObject firstGameObject(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null)
			return null;

		for (int index = 0; index < tile.objectCount; index++) {
			GameObject interactable = tile.gameObjects[index];
			if (interactable.getKey().isSolid() && interactable.getX() == x && interactable.getY() == y)
				return interactable;
		}
		return null;
	}

	public ObjectKey getFloorDecorationKey(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null || tile.groundDecoration == null)
			return null;

		return tile.groundDecoration.getKey();
	}

	public ObjectKey getInteractableObjectKey(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null)
			return null;

		for (int index = 0; index < tile.objectCount; index++) {
			GameObject object = tile.gameObjects[index];

			if (object != null && (object.getKey().isSolid()) && object.getX() == x && object.getY() == y)
				return object.getKey();
		}

		return null;
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

	public SceneTile getTile(int plane, int x, int y) {
		if (plane < 0) {
			plane = 0;
		}
		if (plane > 3) {
			plane = 3;
		}
		if (x < 0) {
			x = 0;
		}
		if (x >= width) {
			x = width - 1;
		}
		if (y < 0) {
			y = 0;
		}
		if (y >= length) {
			y = length - 1;
		}

		if (tiles[plane][x][y] == null) {
			tiles[plane][x][y] = new SceneTile(x, y, plane);
		}
		return tiles[plane][x][y];
	}

	public GroundDecoration getTileFloorDecoration(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null || tile.groundDecoration == null)
			return null;

		return tile.groundDecoration;
	}

	public Wall getTileWall(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null)
			return null;

		return tile.wall;
	}

	public WallDecoration getTileWallDecoration(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null)
			return null;

		return tile.wallDecoration;
	}

	public ObjectKey getWallDecorationKey(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null || tile.wallDecoration == null)
			return null;

		return tile.wallDecoration.getKey();
	}

	public ObjectKey getWallKey(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null || tile.wall == null)
			return null;

		return tile.wall.getKey();
	}

	public void handleMouseInObject() {
		final ObjectKey key = Client.hoveredUID;
		if (key == null)
			return;
		int plane = Options.currentHeight.get();


		if (mouseIsDown && !this.currentStateCorrect()) {
			commitChanges();
			initChanges();

		}
		switch (Options.currentTool.get()) {
			case SELECT_OBJECT:
				if (mouseIsDown) {

					int id = key.getId();
					int y = key.getY();
					int x = key.getX();

					SceneTile selectedTile = getTile(plane, x, y);
					DefaultWorldObject newSelection = selectedTile.getObject(key);
					if (newSelection != null) {
						if (KeyBindings.actionValid(KeyActions.ADD_TO_SELECTION_OBJECT)) {
							if (selectedObjects.contains(newSelection)) {
								newSelection.setSelected(false);
								selectedObjects.remove(newSelection);
							} else {
								newSelection.setSelected(true);
								selectedObjects.add(newSelection);
							}
						} else {

							if (selectedObjects.contains(newSelection)) {
								selectedObjects.clear();
								newSelection.setSelected(false);
							} else {
								for (DefaultWorldObject deselectObj : selectedObjects) {
									deselectObj.setSelected(false);
								}
								selectedObjects.clear();
								newSelection.setSelected(true);
								selectedObjects.add(newSelection);
							}

						}

					}
					SceneGraph.minimapUpdate = true;
					mouseIsDown = false;
					commitChanges();
				}
				break;
		}

		switch (Options.currentTool.get()) {
			case DELETE_OBJECT:
				resetLastHighlightedTiles();
				if (mouseIsDown) {

					if (key != null) {
						int y = key.getY();
						int x = key.getX();
						highlightTile(x, y, plane);
						int id = key.getId();
						int orientation = key.getOrientation();
						ObjectDefinition def = ObjectDefinitionLoader.lookup(id);
						if (def == null)
							return;
						int width;
						int length;

						if (orientation == 1 || orientation == 3) {
							width = def.getLength();
							length = def.getWidth();
						} else {
							width = def.getWidth();
							length = def.getLength();
						}

						int maxX = x + width;
						int maxY = y + length;

						if (this.currentStateCorrect()) {
							ObjectState tileState = new ObjectState(x, y, plane);
							tileState.setKey(key);
							((TileChange<ObjectState>) currentState.get()).preserveTileState(tileState);


						}
						for (int xPos = x; xPos < maxX; xPos++) {
							for (int yPos = y; yPos < maxY; yPos++) {
								SceneTile selectedTile = getTile(plane, xPos, yPos);
								if (selectedTile != null) {
									boolean removed = selectedTile.removeByUID(key);
									if (removed) {
										getMapRegion().shading[plane][xPos][yPos] = 0;
										System.out.println("removed " + def.getId());
										//tileQueue.push(selectedTile);
									}

								}
							}
						}

						Client.hoveredUID = null;
					}


					SceneGraph.minimapUpdate = true;
				}

				break;


		}
	}

	public void forceMouseInTile() {
		this.handleMouseInTile(hoveredTileX, hoveredTileY, hoveredTileZ, true);
	}

	public void handleMouseInTile(int tileX, int tileY, int plane) {
		this.handleMouseInTile(tileX, tileY, plane, false);
	}

	public void handleMouseInTile(int tileX, int tileY, int plane, boolean forceUpdate) {// TODO Make this external
		if (plane != Options.currentHeight.get())
			return;
		if (tileX < 0 || tileY < 0 || plane < 0 || tileX > this.width - 1 || tileY > this.length - 1 || plane > 3)
			return;

		if (Options.currentTool.get() == ToolType.SELECT_TILE) {
			if (!mouseIsDown && mouseWasDown) {
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < length; y++) {
						SceneTile tile = tiles[plane][x][y];
						if (tile != null && tile.tileBeingSelected) {
							tile.tileBeingSelected = false;
							tile.tileSelected = !(ctrlDown && shiftDown);
						}
					}
				}
			}
		}
		if (hoveredTileX != tileX || hoveredTileY != tileY || hoveredTileZ != plane || mouseIsDown || forceUpdate) {
			int lastTileX = hoveredTileX;
			int lastTileY = hoveredTileY;
			hoveredTileX = tileX;
			hoveredTileY = tileY;
			hoveredTileZ = plane;

			if (mouseIsDown && mouseWasDown) {
				if (!KeyBindings.actionValid(KeyActions.ADD_TO_SELECTION_TILE) && !KeyBindings.actionValid(KeyActions.ADD_TO_SELECTION_TILE_SINGLE)) {
					this.resetTileSelections();
				}
				clickStartX = tileX;
				clickStartY = tileY;
				mouseWasDown = false;
			}


			if (Options.currentTool.get() == ToolType.SELECT_TILE) {//this.resetTiles();
				removeHighlight(lastTileX, lastTileY, plane);
				highlightTile(tileX, tileY, plane);
				if (mouseIsDown) {
					if (KeyBindings.actionValid(KeyActions.ADD_TO_SELECTION_TILE_SINGLE)) {
						selectTile(tileX, tileY, plane, !tiles[plane][tileX][tileY].tileSelected, false);

						SceneGraph.setMouseIsDown(false);
					} else if (clickStartX != -1) {

						TileArea rect;
						if (KeyboardState.isKeyPressed(KeyCode.ALT)) {
							rect = new TileArea(clickStartX, clickStartY, tileX, tileY);
						} else {
							rect = new TileArea(clickStartX, clickStartY, tileX, tileY);
						}

						for (int x = 0; x < width; x++) {
							for (int y = 0; y < length; y++) {
								selectTile(x, y, plane, rect.contains(x, y), false);
							}
						}

					}

					SceneGraph.minimapUpdate = true;
				}


				return;
			}


			if (!this.currentStateCorrect()) {
				initChanges();
			}

			double brushSize = Options.brushSize.get();

			switch (Options.currentTool.get()) {
				case SET_FLAGS: {

					brushSelection(brushSize, true, (absX, absY) -> {
						mouseWasDown = true;
						if (currentState.isPresent()) {
							FlagState tileState = new FlagState(absX, absY, plane);
							tileState.preserve();
							((TileChange<FlagState>) currentState.get()).preserveTileState(tileState);
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
				break;

				case PAINT_UNDERLAY:
				case PAINT_OVERLAY: {
					this.resetTiles();

					brushSelection(brushSize, Options.currentTool.get() == ToolType.PAINT_UNDERLAY,
							(absX, absY) -> {
								if (Options.currentTool.get() == ToolType.PAINT_OVERLAY) {
									if (currentState.isPresent()) {
										OverlayState tileState = new OverlayState(absX, absY, plane);
										tileState.preserve();
										((TileChange<OverlayState>) currentState.get()).preserveTileState(tileState);
									}
									if (Options.overlayPaintShapeId.get() == 0 || KeyBindings.actionValid(KeyActions.OVERLAY_REMOVE)) {
										this.getMapRegion().overlays[plane][absX][absY] = (byte) 0;
									} else {
										if (!KeyBindings.actionValid(KeyActions.OVERLAY_ONLY_PAINT)) {
											this.getMapRegion().overlays[plane][absX][absY] = (byte) Options.overlayPaintId
													.get();
											this.getMapRegion().overlayShapes[plane][absX][absY] = (byte) (Options.overlayPaintShapeId
													.get() - 1);
											this.getMapRegion().overlayOrientations[plane][absX][absY] = (byte) Options.rotation.get();
										} else {
											if (this.getMapRegion().overlays[plane][absX][absY] > 0) {
												this.getMapRegion().overlays[plane][absX][absY] = (byte) Options.overlayPaintId
														.get();
											}
										}
									}
									this.tiles[plane][absX][absY].hasUpdated = true;
								} else {
									if (currentState.isPresent()) {
										UnderlayState tileState = new UnderlayState(absX, absY, plane);
										tileState.preserve();
										((TileChange<UnderlayState>) currentState.get()).preserveTileState(tileState);
									}
									this.getMapRegion().underlays[plane][absX][absY] = (byte) Options.underlayPaintId
											.get();
									this.tiles[plane][absX][absY].hasUpdated = true;
								}

							},
							(absX, absY) -> {

								if (Options.currentTool.get() == ToolType.PAINT_OVERLAY) {
									if (KeyBindings.actionValid(KeyActions.OVERLAY_REMOVE)) {
										int existing = getMapRegion().overlays[plane][absX][absY];
										int shape = getMapRegion().overlayShapes[plane][absX][absY];
										int rotation = getMapRegion().overlayOrientations[plane][absX][absY];
										if (existing > 0) {
											this.addTemporaryTile(plane, absX, absY, shape + 1, rotation, -1, 0, 62000);//TODO Make this reflect the tile colour
										}
									} else if (KeyBindings.actionValid(KeyActions.OVERLAY_ONLY_PAINT)) {
										int existing = getMapRegion().overlays[plane][absX][absY];
										int shape = getMapRegion().overlayShapes[plane][absX][absY];
										int rotation = getMapRegion().overlayOrientations[plane][absX][absY];
										if (existing > 0) {
											this.addTemporaryTile(plane, absX, absY, shape + 1, rotation, -1, 0, 9997965);//TODO Make this reflect the tile colour
										}
									} else {
										this.addTemporaryTile(plane, absX, absY, Options.overlayPaintShapeId.get(), Options.rotation.get(), -1, 0, 9997965);//TODO Make this reflect the tile colour
									}

								}
							}, () -> {
								if (mouseIsDown) {
									getMapRegion().updateTiles();
								}
							});


				}
				break;

				case IMPORT_SELECTION:

					this.resetTiles();
					if (Options.importData == null) {
						System.out.println("import tool data is null");
						return;
					}
					int lowestPlane = 4;
					for (SceneTileData data : Options.importData)
						if (data.getZ() < lowestPlane)
							lowestPlane = data.getZ();
					if (mouseIsDown) {
						//Set tile flags and heights first
						Function<Location, Optional<SceneTileData>> getTileAt = (loc) -> {
							return Options.importData.stream().filter(data -> data.getLocation().equals(loc)).findFirst();
						};
						for (SceneTileData data : Options.importData) {
							int savedX = tileX + data.getX();
							int savedY = tileY + data.getY();
							int zPos = Options.currentHeight.get() + (data.getZ() - lowestPlane);
							if (zPos >= 4) continue;

							int angle = 90 * Options.rotation.get();
							Point2D result = new Point2D.Double();
							AffineTransform rotation = new AffineTransform();
							double angleInRadians = angle * Math.PI / 180;
							rotation.rotate(angleInRadians, tileX, tileY);
							rotation.transform(new Point2D.Double(savedX, savedY), result);
							int xPos = (int) result.getX();

							int yPos = (int) result.getY();

							if (xPos > width - 1 || xPos < 0 || yPos > length - 1 || yPos < 0) {
								continue;
							}
							if (tiles[zPos][xPos][yPos] == null) {
								tiles[zPos][xPos][yPos] = new SceneTile(xPos, yPos, zPos);
							}

							tiles[zPos][xPos][yPos].hasUpdated = true;
							if (currentState.isPresent()) {
								ImportTileState tileState = new ImportTileState(xPos, yPos, plane);
								tileState.preserve();
								((ImportChange) currentState.get()).preserveTileState(tileState);
							}

							if (data.getOverlayId() != -1) {
								getMapRegion().overlays[zPos][xPos][yPos] = data.getOverlayId();
								getMapRegion().overlayShapes[zPos][xPos][yPos] = data.getOverlayType();
								getMapRegion().overlayOrientations[zPos][xPos][yPos] = (byte) ((data.getOverlayOrientation()
										- Options.rotation.get()) & 3);
							}

							if (data.getUnderlayId() != -1) {
								getMapRegion().underlays[zPos][xPos][yPos] = data.getUnderlayId();
							}
							if (data.getTileHeight() != -1) {
								if (zPos == data.getZ()) {
									getMapRegion().tileHeights[zPos][xPos][yPos] = data.getTileHeight();
								} else if (data.getZ() <= 0) {
									getMapRegion().tileHeights[zPos][xPos][yPos] = getMapRegion().tileHeights[zPos - 1][xPos][yPos] + data.getTileHeight();
								} else {
									Optional<SceneTileData> optionalData = getTileAt.apply(new Location(xPos, yPos, data.getZ() - 1));

									optionalData.ifPresent(originalDataBelow -> {
										getMapRegion().tileHeights[zPos][xPos][yPos] = originalDataBelow.getTileHeight() - data.getTileHeight();
									});
								}

								getMapRegion().manualTileHeight[zPos][xPos][yPos] = 1;

							}
							if (data.getTileFlag() != -1) {
								getMapRegion().tileFlags[zPos][xPos][yPos] = data.getTileFlag();
							}

						}

						commitChanges();
						tileQueue.clear();
						getMapRegion().updateTiles();
						//Then spawn the objects
						Options.currentTool.set(ToolType.SPAWN_OBJECT);
						this.initChanges();
						for (SceneTileData data : Options.importData) {
							int savedX = tileX + data.getX();
							int savedY = tileY + data.getY();
							int zPos = Options.currentHeight.get() + (data.getZ() - lowestPlane);
							if (zPos >= 4) continue;

							boolean heightDifferent = data.getZ() != Options.currentHeight.get();
							int angle = 90 * (Options.rotation.get() & 3);
							Point2D result = new Point2D.Double();
							AffineTransform rotation = new AffineTransform();
							double angleInRadians = angle * Math.PI / 180;
							rotation.rotate(angleInRadians, tileX, tileY);
							rotation.transform(new Point2D.Double(savedX, savedY), result);
							int xPos = (int) result.getX();

							int yPos = (int) result.getY();
							if (xPos > width - 1 || xPos < 0 || yPos > length - 1 || yPos < 0) {
								continue;
							}
							if (tiles[zPos][xPos][yPos] == null) {
								tiles[zPos][xPos][yPos] = new SceneTile(xPos, yPos, zPos);
							}
							tiles[zPos][xPos][yPos].hasUpdated = true;


							if (data.getGameObjectIds() != null) {
								for (int i = 0; i < data.getGameObjectIds().length; i++) {

									this.addObject(xPos, yPos, zPos, data.getGameObjectIds()[i],
											data.getGameObjectConfigs()[i] >> 2,
											((data.getGameObjectConfigs()[i] & 0xff) - (Options.rotation.get())) & 3, false);

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
							}

						}
						commitChanges();
						Options.currentTool.set(ToolType.IMPORT_SELECTION);
						SceneGraph.setMouseIsDown(false);
						this.shadeObjects(64, -50, -10, -50, 768);

					} else {
						//System.out.println("Found? " + (Options.importData.stream().anyMatch(data -> data.getGroundDecoration())));
						for (SceneTileData data : Options.importData) {
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

						}
					}

					break;

				case SELECT_OBJECT:
					break;
				case SPAWN_OBJECT:
					ObjectDataset currentObject = Options.currentObject.get();
					if (currentObject == null)
						return;
					if (mouseIsDown) {
						this.resetTiles();
						if (!getTile(plane, tileX, tileY).contains(currentObject.getId(), currentObject.getType()))
							getMapRegion().spawnObjectToWorld(this, currentObject.getId(),
									tileX, tileY, plane, currentObject.getType(),
									Options.rotation.get(), false);
						else {
							System.out.println("OBJEXISTS");
						}
						SceneGraph.minimapUpdate = true;
						setMouseIsDown(false);
					} else {
						this.resetTiles();
						this.highlightTile(tileX, tileY, plane);
						getMapRegion().spawnObjectToWorld(this, currentObject.getId(),
								tileX, tileY, plane, currentObject.getType(),
								Options.rotation.get(), true);
					}

					break;
				case DELETE_OBJECT:
					break;
				case MODIFY_HEIGHT:
					if (!ctrlDown && Config.HEIGHT_SMOOTHING) {
						brushSize += 1;//For smoothing
					}
					final int[][] oldHeights = getMapRegion().tileHeights[plane];
					brushSelection(brushSize, true,
							(absX, absY) -> {
								if (currentState.isPresent() && currentState.get().getType() == StateChangeType.TILE_HEIGHT) {
									for (int z = plane; z < 4; z++) {
										HeightState state = new HeightState(absX, absY, z);
										state.preserve();
										((TileChange<HeightState>) currentState.get()).preserveTileState(state);
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
								/*
								 * if(this.getMapRegion().tileHeights[plane][absX][absY] < -480) {
								 * this.getMapRegion().tileHeights[plane][absX][absY] = -480; }
								 */

								if (getMapRegion().tileHeights[plane][absX][absY] > 0)
									getMapRegion().tileHeights[plane][absX][absY] = 0;

								for (int z = 1; z < 4; z++) {
									this.tiles[z][absX][absY].hasUpdated = true;
									if (this.getMapRegion().tileHeights[z][absX][absY] > this.getMapRegion().tileHeights[z - 1][absX][absY]) {
										this.getMapRegion().tileHeights[z][absX][absY] = this.getMapRegion().tileHeights[z - 1][absX][absY];//Not sure on this
									} else if(this.getMapRegion().tileHeights[z - 1][absX][absY] < this.getMapRegion().tileHeights[z][absX][absY]){
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
						this.shadeObjects(64, -50, -10, -50, 768);
						getMapRegion().updateTiles();
						mouseWasDown = false;
					}
					if (mouseIsDown) {
						mouseWasDown = true;
						brushSize += 1;
						this.updateHeights(tileX - brushSize - 3, tileY - brushSize - 3, (brushSize * 2) + 3, (brushSize * 2) + 3);

					}
				default:
					break;

			}

		}
	}

	public void brushSelection(double brushSize, boolean doHighlight, BiConsumer<Integer, Integer> onMouseDown, BiConsumer<Integer, Integer> onHighlight, Runnable onEnd) {
		int tileX = hoveredTileX;
		int tileY = hoveredTileY;
		int plane = Options.currentHeight.get();
		this.resetLastHighlightedTiles();
		double mod = 0;
		double rSq = brushSize * brushSize;
		for (double yPos = tileY - brushSize; yPos <= tileY + brushSize; yPos++) {
			double ySq = (yPos - mod - tileY) * (yPos - mod - tileY);
			for (double xPos = tileX - brushSize; xPos <= tileX + brushSize; xPos++) {
				double xSq = (xPos - mod - tileX) * (xPos - mod - tileX);

				int absX = (int) (xPos + mod);
				int absY = (int) (yPos + mod);
				if (Options.brushType.get() == BrushType.RECTANGLE ||
						Options.brushType.get() == BrushType.CIRCLE && xSq + ySq <= rSq
						|| Options.brushType.get() == BrushType.CHECKER && ((absX + absY) % 2 == (Options.rotation.get() % 2))) {
					if (absX >= 0 && absY >= 0 && absX < width && absY < length) {
						if (onHighlight != null)
							onHighlight.accept(absX, absY);
						if (doHighlight)
							highlightTile(absX, absY, plane);

						if (mouseIsDown) {
							mouseWasDown = true;
							if (onMouseDown != null)
								onMouseDown.accept(absX, absY);
						}


					}
				}
			}
		}

		if (!mouseIsDown && mouseWasDown) {
			tileQueue.clear();
			getMapRegion().updateTiles();
			mouseWasDown = false;
		}

		if (onEnd != null)
			onEnd.run();
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
						SceneTile selectedTile = tiles[z][absX][absY];
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
								int orientation = key.getOrientation();
								if (type == 4) {
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

								if (type == 2 && obj.getType() == WorldObjectType.WALL) {
									int corner = orientation + 1 & 3;
									obj.setPrimary(def.modelAt(type, 4 + orientation, centre, east,
											northEast, north, -1));
									obj.setSecondary(
											def.modelAt(type, corner, centre, east, northEast, north, -1));

								} else if (type != 11) {
									obj.setPrimary(def.modelAt(type, orientation, centre, east,
											northEast, north, -1));
								}

								obj.setRenderHeight(mean);

								// shadeObjectsOnTile(absX, absY, plane, 64, -50, -10, -50, 768);

							}
							// }

						}
					}
				}
			}
		}
	}

	private void removeHighlight(int tileX, int tileY, int plane) {
		SceneTile selectedTile = getTile(plane, tileX, tileY);
		selectedTile.tileHighlighted = false;
		////tileQueue.push(selectedTile);
	}

	private void highlightTile(int tileX, int tileY, int plane) {
		SceneTile selectedTile = getTile(plane, tileX, tileY);
		selectedTile.tileHighlighted = true;
		lastHighightedTiles.add(selectedTile);
		////tileQueue.push(selectedTile);
	}

	public void resetLastHighlightedTiles() {
		lastHighightedTiles.forEach(tile -> tile.tileHighlighted = false);
		lastHighightedTiles.clear();
	}

	public void importSelection(File file) throws IOException {

		ObjectMapper mapper = JsonUtil.getDefaultMapper();
		// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Options.importData = mapper.readValue(file, new TypeReference<LinkedList<SceneTileData>>() {
		});
		transformInput();
		Options.currentTool.set(ToolType.IMPORT_SELECTION);

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

	private void mergeNormals(Mesh first, Mesh second, int dx, int dy, int dz, boolean flag) {
		anInt488++;
		int count = 0;
		int[] secondX = second.vertexX;
		int secondVertices = second.vertexCount;

		for (int vertexA = 0; vertexA < first.vertexCount; vertexA++) {
			VertexNormal parentNormalA = first.getNormal(vertexA);
			VertexNormal normalA = first.normals[vertexA];

			if (normalA.getMagnitude() != 0) {
				int y = first.vertexY[vertexA] - dy;
				if (y <= second.minimumY) {
					int x = first.vertexX[vertexA] - dx;

					if (x >= second.minimumX && x <= second.maximumX) {
						int z = first.vertexZ[vertexA] - dz;

						if (z >= second.minimumZ && z <= second.maximumZ) {
							for (int vertexB = 0; vertexB < secondVertices; vertexB++) {
								VertexNormal parentNormalB = second.getNormal(vertexB);
								VertexNormal normalB = second.normals[vertexB];

								if (x == secondX[vertexB] && z == second.vertexZ[vertexB]
										&& y == second.vertexY[vertexB] && normalB.getMagnitude() != 0) {
									parentNormalA.setX(parentNormalA.getX() + normalB.getX());
									parentNormalA.setY(parentNormalA.getY() + normalB.getY());
									parentNormalA.setZ(parentNormalA.getZ() + normalB.getZ());
									parentNormalA.setMagnitude(parentNormalA.getMagnitude() + normalB.getMagnitude());

									parentNormalB.setX(parentNormalB.getX() + normalA.getX());
									parentNormalB.setY(parentNormalB.getY() + normalA.getY());
									parentNormalB.setZ(parentNormalB.getZ() + normalA.getZ());
									parentNormalB.setMagnitude(parentNormalB.getMagnitude() + normalA.getMagnitude());

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

		if (count < 3 || !flag)
			return;

		for (int k1 = 0; k1 < first.triangleCount; k1++) {
			if (anIntArray486[first.faceIndices1[k1]] == anInt488 && anIntArray486[first.faceIndices2[k1]] == anInt488
					&& anIntArray486[first.faceIndices3[k1]] == anInt488) {
				first.triangleInfo[k1] = -1;
			}
		}

		for (int l1 = 0; l1 < second.triangleCount; l1++) {
			if (anIntArray487[second.faceIndices1[l1]] == anInt488 && anIntArray487[second.faceIndices2[l1]] == anInt488
					&& anIntArray487[second.faceIndices3[l1]] == anInt488) {
				second.triangleInfo[l1] = -1;
			}
		}
	}

	public void method276(int x, int y) {
		SceneTile tile = tiles[0][x][y];
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
		tiles[3][x][y] = null;
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
		for (int index = 0; index < shortLivedObjectCount; index++) {
			GameObject object = shortLivedGameObjects[index];
			removeInteractable(object);
			shortLivedGameObjects[index] = null;
		}

		shortLivedObjectCount = 0;
	}

	private void method306(Mesh model, int x, int y, int z) {
		if (x < width - 1) {
			SceneTile tile = tiles[z][x + 1][y];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.primaryHasNormals()) {
				mergeNormals(model, tile.groundDecoration.getPrimary().asMesh(), 128, 0, 0, true);
			}
		}

		if (y < length - 1) {
			SceneTile tile = tiles[z][x][y + 1];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.primaryHasNormals()) {
				mergeNormals(model, tile.groundDecoration.getPrimary().asMesh(), 0, 0, 128, true);
			}
		}

		if (x < width - 1 && y < length - 1) {
			SceneTile tile = tiles[z][x + 1][y + 1];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.primaryHasNormals()) {
				mergeNormals(model, tile.groundDecoration.getPrimary().asMesh(), 128, 0, 128, true);
			}
		}

		if (x < width - 1 && y > 0) {
			SceneTile tile = tiles[z][x + 1][y - 1];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.primaryHasNormals()) {
				mergeNormals(model, tile.groundDecoration.getPrimary().asMesh(), 128, 0, -128, true);
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
								SceneTile tile = tiles[z][x][y];
								if (tile != null) {
									int averageTileHeight = (getMapRegion().tileHeights[z][x][y]
											+ getMapRegion().tileHeights[z][x + 1][y]
											+ getMapRegion().tileHeights[z][x][y + 1]
											+ getMapRegion().tileHeights[z][x + 1][y + 1]) / 4
											- (getMapRegion().tileHeights[plane][startX][startY]
											+ getMapRegion().tileHeights[plane][startX + 1][startY]
											+ getMapRegion().tileHeights[plane][startX][startY + 1]
											+ getMapRegion().tileHeights[plane][startX + 1][startY + 1]) / 4;

									Wall wall = (Wall) SceneGraph.getTemporaryOrDefault(tile, WorldObjectType.WALL);

									if (wall != null) {
										if (wall.primaryHasNormals())
											mergeNormals(model, wall.getPrimary().asMesh(), (x - startX) * 128 + (1 - sizeX) * 64,
													averageTileHeight, (y - startY) * 128 + (1 - sizeY) * 64, flag);

										if (wall.secondaryHasNormals())
											mergeNormals(model, wall.getSecondary().asMesh(), (x - startX) * 128 + (1 - sizeX) * 64,
													averageTileHeight, (y - startY) * 128 + (1 - sizeY) * 64, flag);

									}


									for (int index = 0; index < tile.objectCount; index++) {
										GameObject object = tile.gameObjects[index];
										if (object != null && object.primaryHasNormals()) {
											int k3 = object.maxX - object.getX() + 1;
											int l3 = object.maxY - object.getY() + 1;
											mergeNormals(model, object.getPrimary().asMesh(),
													(object.getX() - startX) * 128 + (k3 - sizeX) * 64, averageTileHeight,
													(object.getY() - startY) * 128 + (l3 - sizeY) * 64, flag);
										}
									}

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
		for (int x = 0; x < width; x++)
			for (int y = 0; y < length; y++)
				this.getTile(Options.currentHeight.get(), x, y).hasUpdated = false;
	}

	public void renderScene(int cameraTileX, int cameraTileY, int k, int cameraTileZ, int cameraPlane, int j1) {
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

		if (j1 < 0) {
			j1 = 0;
		}

		currentRenderCycle++;

		ySine = Constants.SINE[j1];
		yCosine = Constants.COSINE[j1];
		xSine = Constants.SINE[k];
		xCosine = Constants.COSINE[k];

		aBooleanArrayArray492 = aBooleanArrayArrayArrayArray491[j1 / 32][k / 64];


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
			SceneTile[][] tiles = this.tiles[z];

			for (int x = minViewX; x < maxViewX; x++) {
				for (int y = minViewY; y < maxViewY; y++) {
					if (inChunk(x, y)) {
						SceneTile tile = tiles[x][y];
						if (tile != null) {
							if (tile.collisionPlane > cameraPlane || !aBooleanArrayArray492[x - minViewX][y - minViewY]
									&& chunk.mapRegion.tileHeights[z][x][y] - cameraTileZ < 50) {
								tile.needsRendering = false;
								tile.aBoolean1323 = false;
								tile.anInt1325 = 0;
							} else {
								tile.needsRendering = true;
								tile.aBoolean1323 = true;
								tile.hasObjects = tile.getTemporaryObject().isPresent() || tile.objectCount > 0;
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
				SceneTile[][] tiles = this.tiles[z];
				for (int dx = -Options.renderDistance.get(); dx <= 0; dx++) {
					int tileXNeg = absoluteCameraX + dx;
					int tileXPos = absoluteCameraX - dx;
					if (tileXNeg >= minViewX || tileXPos < maxViewX) {
						for (int dy = -Options.renderDistance.get(); dy <= 0; dy++) {
							int tileYNeg = absoluteCameraY + dy;
							int tileYPos = absoluteCameraY - dy;

							if (inChunk(tileXNeg, tileYNeg)) {
								SceneTile tile = tiles[toChunkTileX(tileXNeg)][toChunkTileY(tileYNeg)];
								if (tile != null && tile.needsRendering) {
									renderTile(tile, flag);
								}
							}

							if (inChunk(tileXNeg, tileYPos)) {
								SceneTile tile = tiles[toChunkTileX(tileXNeg)][toChunkTileY(tileYPos)];
								if (tile != null && tile.needsRendering) {
									renderTile(tile, flag);
								}
							}

							if (inChunk(tileXPos, tileYNeg)) {
								SceneTile tile = tiles[toChunkTileX(tileXPos)][toChunkTileY(tileYNeg)];
								if (tile != null && tile.needsRendering) {
									renderTile(tile, flag);
								}
							}

							if (inChunk(tileXPos, tileYPos)) {
								SceneTile tile = tiles[toChunkTileX(tileXPos)][toChunkTileY(tileYPos)];
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

		SceneTile[][] tiles = this.tiles[Options.currentHeight.get()];
		for (int dx = -Options.renderDistance.get(); dx <= 0; dx++) {
			int tileXNeg = absoluteCameraX + dx;
			int tileXPos = absoluteCameraX - dx;
			if (tileXNeg >= minViewX || tileXPos < maxViewX) {
				for (int dy = -Options.renderDistance.get(); dy <= 0; dy++) {
					int tileYNeg = absoluteCameraY + dy;
					int tileYPos = absoluteCameraY - dy;

					if (inChunk(tileXNeg, tileYNeg)) {
						SceneTile tile = tiles[toChunkTileX(tileXNeg)][toChunkTileY(tileYNeg)];
						if (tile != null) {
							renderAfterCycle(tile);
						}
					}

					if (inChunk(tileXNeg, tileYPos)) {
						SceneTile tile = tiles[toChunkTileX(tileXNeg)][toChunkTileY(tileYPos)];
						if (tile != null) {
							renderAfterCycle(tile);
						}
					}

					if (inChunk(tileXPos, tileYNeg)) {
						SceneTile tile = tiles[toChunkTileX(tileXPos)][toChunkTileY(tileYNeg)];
						if (tile != null) {
							renderAfterCycle(tile);
						}
					}

					if (inChunk(tileXPos, tileYPos)) {
						SceneTile tile = tiles[toChunkTileX(tileXPos)][toChunkTileY(tileYPos)];
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

	public void renderAfterCycle(SceneTile activeTile) {
		Vector2 screenPos = Client.getSingleton().getScreenPos(activeTile.positionX * 128 + 64, activeTile.positionY * 128 + 64, 64);

		if(screenPos.getX() > 0 && screenPos.getY() > 0) {
			if (Options.showMinimapFunctionModels.get()) {
				GroundDecoration decor = (GroundDecoration) SceneGraph.getTemporaryOrDefault(activeTile, WorldObjectType.GROUND_DECORATION);
				if (decor != null && decor.getMinimapFunction() != null) {
					decor.getMinimapFunction().drawSprite((int) screenPos.getX(), (int) screenPos.getY());

				}
			}
			if (Options.showUnderlayNumbers.get()) {
				if (activeTile != null) {
					try {
						int underlayId = getMapRegion().underlays[activeTile.plane][activeTile.positionX][activeTile.positionY] - 1;
						if (underlayId > 0 && screenPos.getX() > 0 && screenPos.getY() > 0)
							Client.getSingleton().robotoFont.drawString("" + underlayId, (int) screenPos.getX(), (int) screenPos.getY(), 0xffff00);
					} catch (Exception e) {
					}
				}
			}
			if (Options.showOverlayNumbers.get()) {
				if (activeTile != null) {
					try {
						int overlayId = getMapRegion().overlays[activeTile.plane][activeTile.positionX][activeTile.positionY] - 1;
						if (overlayId > 0 && screenPos.getX() > 0 && screenPos.getY() > 0)
							Client.getSingleton().robotoFont.drawString("" + overlayId, (int) screenPos.getX(), (int) screenPos.getY(), 0xffff00);
					} catch (Exception e) {
					}
				}
			}

			if (Options.showTileHeightNumbers.get()) {
				if (activeTile != null) {
					try {
						int tileHeight = getMapRegion().tileHeights[activeTile.plane][activeTile.positionX][activeTile.positionY];
						if (activeTile.plane > 0 && !Options.absoluteHeightProperty.get())
							tileHeight -= getMapRegion().tileHeights[activeTile.plane - 1][activeTile.positionX][activeTile.positionY];
						if (screenPos.getX() > 0 && screenPos.getY() > 0)
							Client.getSingleton().robotoFont.drawString("" + (-tileHeight), (int) screenPos.getX(), (int) screenPos.getY(), 0xffff00);
					} catch (Exception e) {
					}

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
		anInt475 = 0;
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
				aClass47Array476[anInt475++] = cluster;
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
				aClass47Array476[anInt475++] = cluster;
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
							aClass47Array476[anInt475++] = cluster;
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
		for (int l = 0; l < anInt475; l++) {
			SceneCluster cluster = aClass47Array476[l];

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

	public void removeFloorDecoration(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null)
			return;
		tile.groundDecoration = null;
	}

	private void removeInteractable(GameObject object) {
		for (int x = object.getX(); x <= object.maxX; x++) {
			for (int y = object.getY(); y <= object.maxY; y++) {
				SceneTile tile = tiles[object.getPlane()][x][y];

				if (tile != null) {
					for (int index = 0; index < tile.objectCount; index++) {
						if (tile.gameObjects[index] != object) {
							continue;
						}

						tile.objectCount--;
						for (int remaining = index; remaining < tile.objectCount; remaining++) {
							tile.gameObjects[remaining] = tile.gameObjects[remaining + 1];
							tile.objectAttributes[remaining] = tile.objectAttributes[remaining + 1];
						}

						tile.gameObjects[tile.objectCount] = null;
						break;
					}


					tile.clearAttributes();
					for (int index = 0; index < tile.objectCount; index++) {
						tile.shiftAttributes(tile.objectAttributes[index]);
					}
				}
			}
		}
	}

	public void removeObject(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null)
			return;

		for (int index = 0; index < tile.objectCount; index++) {
			GameObject object = tile.gameObjects[index];
			if ((object.getKey().isSolid()) && object.getX() == x && object.getY() == y) {
				removeInteractable(object);
				return;
			}
		}
	}

	public void removeTemporaryObject(int tileX, int tileY, int plane) {
		SceneTile tile = getTile(plane, tileX, tileY);
		tile.tileHighlighted = false;
		tile.temporaryObject = Optional.empty();
		tile.temporaryObjectAttributes = Optional.empty();

	}

	public void removeWall(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null)
			return;
		tile.wall = null;
	}

	public void removeWallDecoration(int x, int y, int z) {
		SceneTile tile = tiles[z][x][y];
		if (tile == null)
			return;
		tile.wallDecoration = null;
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
				} else if (!lowMemory && tile.getTexture() != -1) {

					if (tile.getNorthEastColour() != 0xbc614e) {
						if (tile.isFlat()) {
							GameRasterizer.getInstance().render_texture_triangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
									yD, yC, yB,
									tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(),
									xA, xB, xC,
									centreHeight, eastHeight, northHeight, yA, yB, yC, !lowMemory || tile.getTileColour() == -1 ? tile.getTexture() : -1, tile.getColour(), true, true);
						} else {
							GameRasterizer.getInstance().render_texture_triangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
									yD, yC, yB,
									tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xD, xC, xB,
									northEastHeight, northHeight, eastHeight, yD, yC, yB, !lowMemory || tile.getTileColour() == -1 ? tile.getTexture() : -1, tile.getColour(), true, true);
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
				} else if (tile.getTexture() == -1) {
					if (tile.getNorthEastColour() != 0xbc614e) {
						GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
								tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
					}
				} else if (!lowMemory) {
					if (tile.isFlat()) {
						GameRasterizer.getInstance().drawTexturedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
								tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xA, xB, xC,
								centreHeight, eastHeight, northHeight, yA, yB, yC, tile.getTexture());
					} else {
						GameRasterizer.getInstance().drawTexturedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
								tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xD, xC, xB,
								northEastHeight, northHeight, eastHeight, yD, yC, yB, tile.getTexture());
					}
				} else {
					int textureColour = TEXTURE_COLOURS[tile.getTexture()];
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
			} else if (tile.getTexture() == -1) {
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
								centreHeight, eastHeight, northHeight, yA, yB, yC, !lowMemory || tile.getTileColour() == -1 ? tile.getTexture() : -1, tile.getColour(), true, true);
					} else if (tile.getCentreColour() != 0xbc614e) {
						GameRasterizer.getInstance().drawTexturedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
								tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour(), xA, xB, xC,
								centreHeight, eastHeight, northHeight, yA, yB, yC, tile.getTexture());
					}
				} else if (!Options.hdTextures.get()) {
					int j7 = TEXTURE_COLOURS[tile.getTexture()];
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
			if (mouseInTriangle(clickX, clickY, screenYD, screenYC, screenYB, screenXD, screenXC, screenXB)) {
				handleMouseInTile(tileX, tileY, plane);
			}

			if (Options.hdTextures.get()) {

				if (hiddenTile) {
					GameRasterizer.getInstance().currentAlpha = 170;
					GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
							tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
					GameRasterizer.getInstance().currentAlpha = 0;
				} else if (!lowMemory && tile.getTexture() != -1) {

					if (tile.getNorthEastColour() != 0xbc614e) {
						if (tile.isFlat()) {
							GameRasterizer.getInstance().render_texture_triangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
									yD, yC, yB,
									tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(),
									xA, xB, xC,
									centreHeight, eastHeight, northHeight, yA, yB, yC, !lowMemory || tile.getTileColour() == -1 ? tile.getTexture() : -1, tile.getColour(), true, true);
						} else {
							GameRasterizer.getInstance().render_texture_triangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
									yD, yC, yB,
									tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xD, xC, xB,
									northEastHeight, northHeight, eastHeight, yD, yC, yB, !lowMemory || tile.getTileColour() == -1 ? tile.getTexture() : -1, tile.getColour(), true, true);
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
				} else if (tile.getTexture() == -1) {
					if (tile.getNorthEastColour() != 0xbc614e) {
						GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
								tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour());
					} else if(Options.showHiddenTiles.get()){
						GameRasterizer.getInstance().drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
								GameRasterizer.getInstance().getFuchsia(), 	GameRasterizer.getInstance().getFuchsia(), 	GameRasterizer.getInstance().getFuchsia());
					}
				} else if (!lowMemory) {
					if (tile.isFlat()) {
						GameRasterizer.getInstance().drawTexturedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
								tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xA, xB, xC,
								centreHeight, eastHeight, northHeight, yA, yB, yC, tile.getTexture());
					} else {
						GameRasterizer.getInstance().drawTexturedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB,
								tile.getNorthEastColour(), tile.getNorthColour(), tile.getEastColour(), xD, xC, xB,
								northEastHeight, northHeight, eastHeight, yD, yC, yB, tile.getTexture());
					}
				} else {
					int textureColour = TEXTURE_COLOURS[tile.getTexture()];
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
			if (mouseInTriangle(clickX, clickY, screenYA, screenYB, screenYC, screenXA, screenXB, screenXC)) {
				this.handleMouseInTile(tileX, tileY, plane);
			}
			if (hiddenTile) {
				GameRasterizer.getInstance().currentAlpha = 170;
				GameRasterizer.getInstance().drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
						tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour());
				GameRasterizer.getInstance().currentAlpha = 0;
			} else if (tile.getTexture() == -1) {
				if (tile.getCentreColour() != 0xbc614e) {
					GameRasterizer.getInstance().drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
							tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour());
				} else if(Options.showHiddenTiles.get()){
					GameRasterizer.getInstance().drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
							GameRasterizer.getInstance().getFuchsia(), 	GameRasterizer.getInstance().getFuchsia(), 	GameRasterizer.getInstance().getFuchsia());

				}
			} else {
				if (!lowMemory) {
					if (Options.hdTextures.get() && tile.getCentreColour() != 0xbc614e) {

						GameRasterizer.getInstance().render_texture_triangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
								yA, yB, yC,
								tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour(), xA, xB, xC,
								centreHeight, eastHeight, northHeight, yA, yB, yC, !lowMemory || tile.getTileColour() == -1 ? tile.getTexture() : -1, tile.getColour(), true, true);
					} else if (tile.getCentreColour() != 0xbc614e) {
						GameRasterizer.getInstance().drawTexturedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
								tile.getCentreColour(), tile.getEastColour(), tile.getNorthColour(), xA, xB, xC,
								centreHeight, eastHeight, northHeight, yA, yB, yC, tile.getTexture());
					}
				} else if (!Options.hdTextures.get()) {
					int j7 = TEXTURE_COLOURS[tile.getTexture()];
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
			if (tile.getTriangleTexture() != null) {
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
				if (mouseInTriangle(clickX, clickY, sYA, sYB, sYC, sXA, sXB, sXC)) {
					handleMouseInTile(tileX, tileY, plane);
				}

				if (Options.hdTextures.get()) {
					if (tile.getTriangleTexture() == null || tile.getTriangleTexture()[triangleIndex] == -1) {
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
									!lowMemory || tile.displayColor[triangleIndex] == -1 ? tile.getTriangleTexture()[triangleIndex] : -1, tile.displayColor[triangleIndex], true, true);
						} else {
							GameRasterizer.getInstance().render_texture_triangle(sYA, sYB, sYC, sXA, sXB, sXC, sZA, sZB, sZC,
									tile.getTriangleHslA()[triangleIndex], tile.getTriangleHslB()[triangleIndex],
									tile.getTriangleHslC()[triangleIndex], ShapedTile.viewSpaceX[indexA],
									ShapedTile.viewSpaceX[indexB], ShapedTile.viewSpaceX[indexC],
									ShapedTile.viewSpaceY[indexA], ShapedTile.viewSpaceY[indexB],
									ShapedTile.viewSpaceY[indexC], ShapedTile.viewSpaceZ[indexA],
									ShapedTile.viewSpaceZ[indexB], ShapedTile.viewSpaceZ[indexC],
									!lowMemory || tile.displayColor[triangleIndex] == -1 ? tile.getTriangleTexture()[triangleIndex] : -1, tile.displayColor[triangleIndex], true, true);
						}
					}
				} else {
					if (tile.getTriangleTexture() == null || tile.getTriangleTexture()[triangleIndex] == -1) {
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
									tile.getTriangleTexture()[triangleIndex]);
						} else {
							GameRasterizer.getInstance().drawTexturedTriangle(sYA, sYB, sYC, sXA, sXB, sXC,
									tile.getTriangleHslA()[triangleIndex], tile.getTriangleHslB()[triangleIndex],
									tile.getTriangleHslC()[triangleIndex], ShapedTile.viewSpaceX[indexA],
									ShapedTile.viewSpaceX[indexB], ShapedTile.viewSpaceX[indexC],
									ShapedTile.viewSpaceY[indexA], ShapedTile.viewSpaceY[indexB],
									ShapedTile.viewSpaceY[indexC], ShapedTile.viewSpaceZ[indexA],
									ShapedTile.viewSpaceZ[indexB], ShapedTile.viewSpaceZ[indexC],
									tile.getTriangleTexture()[triangleIndex]);
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
		this.handleMouseInObject();
		do {
			SceneTile activeTile;

			do {
				activeTile = tileQueue.poll();
				if (activeTile == null)
					return;
			} while (!activeTile.aBoolean1323);

			int x = activeTile.positionX;
			int y = activeTile.positionY;
			int wX = x;
			int wY = y;
			int plane = activeTile.plane;
			int l = activeTile.anInt1310;
			if (plane > 3) {
				continue;
			}
			SceneTile[][] planeTiles = tiles[plane];


			if (activeTile.needsRendering) {
				if (flag) {
					if (plane > 0) {
						SceneTile tile = tiles[plane - 1][x][y];
						if (tile != null && tile.aBoolean1323) {
							continue;
						}
					}

					if (wX <= absoluteCameraX && wX > minViewX && x > 0) {
						SceneTile tile = planeTiles[x - 1][y];
						if (tile != null && tile.aBoolean1323 && (tile.needsRendering
								|| (activeTile.attributes() & TileAttributes.RENDER_TILE_WEST) == 0)) {
							continue;
						}
					}

					if (wX >= absoluteCameraX && wX < maxViewX - 1 && x < width - 1) {
						SceneTile tile = planeTiles[x + 1][y];
						if (tile != null && tile.aBoolean1323 && (tile.needsRendering
								|| (activeTile.attributes() & TileAttributes.RENDER_TILE_EAST) == 0)) {
							continue;
						}
					}

					if (wY <= absoluteCameraY && wY > minViewY && y > 0) {
						SceneTile tile = planeTiles[x][y - 1];
						if (tile != null && tile.aBoolean1323 && (tile.needsRendering
								|| (activeTile.attributes() & TileAttributes.RENDER_TILE_NORTH) == 0)) {
							continue;
						}
					}

					if (wY >= absoluteCameraY && wY < maxViewY - 1 && y < length - 1) {
						SceneTile tile = planeTiles[x][y + 1];
						if (tile != null && tile.aBoolean1323 && (tile.needsRendering
								|| (activeTile.attributes() & TileAttributes.RENDER_TILE_SOUTH) == 0)) {
							continue;
						}
					}
				} else {
					flag = true;
				}

				activeTile.needsRendering = false;
				if (activeTile.tileBelow != null) {

					SceneTile tileBelow = activeTile.tileBelow;
					if (tileBelow.temporarySimpleTile.isPresent()) {
						if (!method320(x, y, 0)) {
							GameRasterizer.getInstance().currentAlpha = 0;
							renderPlainTile(tileBelow.temporarySimpleTile.get(), 0, ySine, yCosine, xSine, xCosine, x, y, false,
									false, false, false, (byte) 0);
						}
					} else if (tileBelow.temporaryShapedTile.isPresent() && !method320(x, y, 0)) {
						renderShapedTile(x, ySine, xSine, tileBelow.temporaryShapedTile.get(), yCosine, y, xCosine, 0,
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
						Wall wall = (Wall) SceneGraph.getTemporaryOrDefault(tileBelow, WorldObjectType.WALL);
						if (wall != null) {
							wall.getPrimary().render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
									ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
									wall.getKey(), plane);
						}

						for (int index = 0; index < tileBelow.objectCount; index++) {
							GameObject object = tileBelow.gameObjects[index];

							if (object != null) {
								object.getPrimary().render(GameRasterizer.getInstance(), object.centreX - xCameraTile,
										object.centreY - yCameraTile, object.yaw, ySine, yCosine, xSine, xCosine,
										object.getRenderHeight() - zCameraTile, object.getKey(), plane);
							}
						}

						GameObject object = (GameObject) SceneGraph.getTemporaryOrDefault(tileBelow, WorldObjectType.GAME_OBJECT);

						if (object != null) {
							object.getPrimary().render(GameRasterizer.getInstance(), object.centreX - xCameraTile,
									object.centreY - yCameraTile, object.yaw, ySine, yCosine, xSine, xCosine,
									object.getRenderHeight() - zCameraTile, object.getKey(), plane);
						}
					}
				}

				boolean flag1 = Options.showHiddenTiles.get();
				if (activeTile.temporarySimpleTile.isPresent()) {
					if (!method320(x, y, l)) {
						flag1 = true;
						GameRasterizer.getInstance().currentAlpha = 0;
						renderPlainTile(activeTile.temporarySimpleTile.get(), l, ySine, yCosine, xSine, xCosine, x, y, false, false, false, false, (byte) 0);
					}
				} else if (activeTile.temporaryShapedTile.isPresent() && !method320(x, y, l)) {
					flag1 = true;
					renderShapedTile(x, ySine, xSine, activeTile.temporaryShapedTile.get(), yCosine, y, xCosine, l, false, false, false, (byte) 0);
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
						!activeTile.temporaryShapedTile.isPresent() && !activeTile.temporarySimpleTile.isPresent()) {
					SimpleTile hiddenTile = TileUtils.HIDDEN_TILE;
					this.renderPlainTile(hiddenTile, plane, ySine, yCosine, xSine, xCosine, x, y, true,
							activeTile.tileHighlighted, activeTile.tileSelected, activeTile.tileBeingSelected, getMapRegion().tileFlags[plane][x][y]);
				}

				if (Options.showObjects.get()) {
					int j1 = 0;
					int j2 = 0;
					Wall wall = (Wall) SceneGraph.getTemporaryOrDefault(activeTile, WorldObjectType.WALL);
					WallDecoration decoration = (WallDecoration) SceneGraph.getTemporaryOrDefault(activeTile,
							WorldObjectType.WALL_DECORATION);

					if (wall != null || decoration != null) {
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

					if (wall != null) {
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
								&& wall.getPrimary() != null) {
							wall.getPrimary().render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
									ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
									wall.getKey(), plane);
						}
						if ((wall.anInt277 & j2) != 0 && !method321(x, y, l, wall.anInt277)
								&& wall.getSecondary() != null) {
							wall.getSecondary().render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
									ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
									wall.getKey(), plane);
						}
					}

					if (decoration != null && decoration.getPrimary() != null
							&& !method322(l, x, y, decoration.getPrimary().getModelHeight())) {
						if ((decoration.getAttributes() & j2) != 0) {
							decoration.getPrimary().render(GameRasterizer.getInstance(), decoration.getX() - xCameraTile,
									decoration.getY() - yCameraTile, decoration.getOrientation(), ySine, yCosine,
									xSine, xCosine, decoration.getRenderHeight() - zCameraTile,
									decoration.getKey(), plane);
						} else if ((decoration.getAttributes() & 0b11_0000_0000) != 0) { // type
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

							if ((decoration.getAttributes() & 0b1_0000_0000) != 0 && length < width) { // type
								// 6
								int renderX = dx + anIntArray463[orientation];
								int renderY = dy + anIntArray464[orientation];
								decoration.getPrimary().render(GameRasterizer.getInstance(), renderX, renderY, orientation * 512 + 256, ySine,
										yCosine, xSine, xCosine, height, decoration.getKey(), plane);
							}

							if ((decoration.getAttributes() & 0b10_0000_0000) != 0 && length > width) { // type
								// 7
								int renderX = dx + anIntArray465[orientation];
								int renderY = dy + anIntArray466[orientation];
								decoration.getPrimary().render(GameRasterizer.getInstance(), renderX, renderY, orientation * 512 + 1280 & 0x7ff,
										ySine, yCosine, xSine, xCosine, height, decoration.getKey(), plane);
							}
						}
					}

					if (flag1) {
						GroundDecoration decor = (GroundDecoration) SceneGraph.getTemporaryOrDefault(activeTile,
								WorldObjectType.GROUND_DECORATION);
						if (decor != null && decor.getPrimary() != null) {
							boolean hasFunction = decor.getMinimapFunction() != null;
							if (hasFunction) {
								if (Options.showMinimapFunctionModels.get())
									decor.getPrimary().render(GameRasterizer.getInstance(), decor.getX() - xCameraTile, decor.getY() - yCameraTile,
											0, ySine, yCosine, xSine, xCosine, decor.getRenderHeight() - zCameraTile,
											decor.getKey(), plane);
							} else {
								decor.getPrimary().render(GameRasterizer.getInstance(), decor.getX() - xCameraTile, decor.getY() - yCameraTile,
										0, ySine, yCosine, xSine, xCosine, decor.getRenderHeight() - zCameraTile,
										decor.getKey(), plane);
							}

						}

					}
					int attributes = activeTile.attributes();
					if (attributes != 0) {
						if (wX < absoluteCameraX && (attributes & TileAttributes.RENDER_TILE_EAST) != 0) {
							SceneTile tile = planeTiles[x + 1][y];
							if (tile != null && tile.aBoolean1323) {
								tileQueue.addLast(tile);
							}
						}

						if (wY < absoluteCameraY && (attributes & TileAttributes.RENDER_TILE_SOUTH) != 0) {
							SceneTile tile = planeTiles[x][y + 1];
							if (tile != null && tile.aBoolean1323) {
								tileQueue.addLast(tile);
							}
						}

						if (wX > absoluteCameraX && (attributes & TileAttributes.RENDER_TILE_WEST) != 0) {
							SceneTile tile = planeTiles[x - 1][y];
							if (tile != null && tile.aBoolean1323) {
								tileQueue.addLast(tile);
							}
						}

						if (wY > absoluteCameraY && (attributes & TileAttributes.RENDER_TILE_NORTH) != 0) {
							SceneTile tile = planeTiles[x][y - 1];
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

				GameObject tempObj = (GameObject) getTemporaryOrDefault(activeTile, WorldObjectType.GAME_OBJECT);
				int count = activeTile.objectCount;
				if (tempObj != null)
					count++;
				for (int index = 0; index < count; index++) {
					GameObject obj = index == activeTile.objectCount ? tempObj : activeTile.gameObjects[index];
					int objAttrib = index == activeTile.objectCount ? activeTile.temporaryObjectAttributes.get() : activeTile.objectAttributes[index];
					if (obj.lastRenderCycle == currentRenderCycle || (objAttrib & activeTile.anInt1325) != activeTile.anInt1326) {
						continue;
					}

					flag2 = false;
					break;
				}


				if (flag2) {
					Wall wall = (Wall) SceneGraph.getTemporaryOrDefault(activeTile, WorldObjectType.WALL);
					if (wall != null && !method321(x, y, l, wall.anInt276)) {
						wall.getPrimary().render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
								ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
								wall.getKey(), plane);
					}

					activeTile.anInt1325 = 0;
				}
			}

			if (activeTile.hasObjects) {
				try {
					GameObject tempObj = (GameObject) getTemporaryOrDefault(activeTile, WorldObjectType.GAME_OBJECT);
					int count = activeTile.objectCount;
					if (tempObj != null)
						count++;

					activeTile.hasObjects = false;
					int objectsOnTile = 0;
					label0:
					for (int index = 0; index < count; index++) {
						GameObject object = index == activeTile.objectCount ? tempObj : activeTile.gameObjects[index];
						int objAttrib = index == activeTile.objectCount ? activeTile.temporaryObjectAttributes.get() : activeTile.objectAttributes[index];


						if (object.lastRenderCycle == currentRenderCycle) {
							continue;
						}

						for (int objectX = object.getX(); objectX <= object.maxX; objectX++) {
							for (int objectY = object.getY(); objectY <= object.maxY; objectY++) {
								SceneTile objectTile = planeTiles[objectX][objectY];

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
									if (objectX < object.maxX) {
										l6 += 4;
									}
									if (objectY > object.getY()) {
										l6 += 8;
									}
									if (objectY < object.maxY) {
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
						int i6 = object.maxX - absoluteCameraX;

						/*
						 * if the maxX is greater than the x camera position, x is positive
						 */

						if (i6 > distanceFromStartX) {
							distanceFromStartX = i6;
						}

						int i7 = absoluteCameraY - object.getY();
						int j8 = object.maxY - absoluteCameraY;
						if (j8 > i7) {
							object.anInt527 = distanceFromStartX + j8;
						} else {
							object.anInt527 = distanceFromStartX + i7;
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
									int j7 = object.centreX - xCameraTile;
									int k8 = object.centreY - yCameraTile;
									int l9 = interactables[l3].centreX - xCameraTile;
									int l10 = interactables[l3].centreY - yCameraTile;
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
						if (!method323(l, object.getX(), object.maxX, object.getY(), object.maxY,
								object.getPrimary().getModelHeight())) {
							object.getPrimary().render(GameRasterizer.getInstance(), object.centreX - xCameraTile,
									object.centreY - yCameraTile, object.yaw, ySine, yCosine, xSine, xCosine,
									object.getRenderHeight() - zCameraTile, object.getKey(), plane);
						}

						for (int k7 = object.getX(); k7 <= object.maxX; k7++) {
							for (int l8 = object.getY(); l8 <= object.maxY; l8++) {
								SceneTile class30_sub3_22 = planeTiles[k7][l8];

								if (class30_sub3_22.anInt1325 != 0) {
									tileQueue.addLast(class30_sub3_22);
								} else if ((k7 != x || l8 != y) && class30_sub3_22.aBoolean1323) {
									tileQueue.addLast(class30_sub3_22);
								}
							}

						}

					}
					if (activeTile.hasObjects) {
						continue;
					}
				} catch (Exception _ex) {
					activeTile.hasObjects = false;
				}
			}

			if (!activeTile.aBoolean1323 || activeTile.anInt1325 != 0) {
				continue;
			}

			if (wX <= absoluteCameraX && wX > minViewX && x > 0) {
				SceneTile class30_sub3_8 = planeTiles[x - 1][y];
				if (class30_sub3_8 != null && class30_sub3_8.aBoolean1323) {
					continue;
				}
			}

			if (wX >= absoluteCameraX && wX < maxViewX - 1 && x < width - 1) {
				SceneTile class30_sub3_9 = planeTiles[x + 1][y];
				if (class30_sub3_9 != null && class30_sub3_9.aBoolean1323) {
					continue;
				}
			}

			if (wY <= absoluteCameraY && wY > minViewY && y > 0) {
				SceneTile class30_sub3_10 = planeTiles[x][y - 1];
				if (class30_sub3_10 != null && class30_sub3_10.aBoolean1323) {
					continue;
				}
			}

			if (wY >= absoluteCameraY && wY < maxViewY - 1 && y < length - 1) {
				SceneTile class30_sub3_11 = planeTiles[x][y + 1];
				if (class30_sub3_11 != null && class30_sub3_11.aBoolean1323) {
					continue;
				}
			}

			activeTile.aBoolean1323 = false;
			anInt446--;

			if (activeTile.anInt1328 != 0) {
				WallDecoration decor = (WallDecoration) SceneGraph.getTemporaryOrDefault(activeTile,
						WorldObjectType.WALL_DECORATION);
				if (decor != null && decor.getPrimary() != null
						&& !method322(l, x, y, decor.getPrimary().getModelHeight())) {
					if ((decor.getAttributes() & activeTile.anInt1328) != 0) {
						decor.getPrimary().render(GameRasterizer.getInstance(), decor.getX() - xCameraTile, decor.getY() - yCameraTile,
								decor.getOrientation(), ySine, yCosine, xSine, xCosine,
								decor.getRenderHeight() - zCameraTile, decor.getKey(), plane);
					} else if ((decor.getAttributes() & 0x300) != 0) {
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
						if ((decor.getAttributes() & 0x100) != 0 && l7 >= j6) {
							int i9 = l2 + anIntArray463[orientation];
							int i10 = i4 + anIntArray464[orientation];
							decor.getPrimary().render(GameRasterizer.getInstance(), i9, i10, orientation * 512 + 256, ySine, yCosine, xSine,
									xCosine, j3, decor.getKey(), plane);
						}
						if ((decor.getAttributes() & 0x200) != 0 && l7 <= j6) {
							int j9 = l2 + anIntArray465[orientation];
							int j10 = i4 + anIntArray466[orientation];
							decor.getPrimary().render(GameRasterizer.getInstance(), j9, j10, orientation * 512 + 1280 & 0x7ff, ySine, yCosine,
									xSine, xCosine, j3, decor.getKey(), plane);
						}
					}
				}
				Wall wall = (Wall) SceneGraph.getTemporaryOrDefault(activeTile, WorldObjectType.WALL);
				if (wall != null) {
					if ((wall.anInt277 & activeTile.anInt1328) != 0 && !method321(x, y, l, wall.anInt277)
							&& wall.getSecondary() != null) {
						wall.getSecondary().render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
								ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
								wall.getKey(), plane);
					}
					if ((wall.anInt276 & activeTile.anInt1328) != 0 && !method321(x, y, l, wall.anInt276)
							&& wall.getPrimary() != null) {
						wall.getPrimary().render(GameRasterizer.getInstance(), wall.getX() - xCameraTile, wall.getY() - yCameraTile, 0,
								ySine, yCosine, xSine, xCosine, wall.getRenderHeight() - zCameraTile,
								wall.getKey(), plane);
					}
				}
			}
			if (plane < planeCount - 1) {
				SceneTile above = tiles[plane + 1][x][y];
				if (above != null && above.aBoolean1323) {
					tileQueue.addLast(above);
				}
			}
			if (wX < absoluteCameraX && x < width - 1) {
				SceneTile east = planeTiles[x + 1][y];
				if (east != null && east.aBoolean1323) {
					tileQueue.addLast(east);
				}
			}
			if (wY < absoluteCameraY && y < length - 1) {
				SceneTile north = planeTiles[x][y + 1];
				if (north != null && north.aBoolean1323) {
					tileQueue.addLast(north);
				}
			}
			if (wX > absoluteCameraX && x > 0) {
				SceneTile west = planeTiles[x - 1][y];
				if (west != null && west.aBoolean1323) {
					tileQueue.addLast(west);
				}
			}
			if (wY > absoluteCameraY && y > 0) {
				SceneTile south = planeTiles[x][y - 1];
				if (south != null && south.aBoolean1323) {
					tileQueue.addLast(south);
				}
			}

		} while (true);
	}

	public void reset() {
		if (tiles != null)
			for (int z = 0; z < planeCount; z++) {
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < length; y++) {
						tiles[z][x][y] = null;
					}
				}
			}

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
			for (int l1 = 0; l1 < interactables.length; l1++) {
				interactables[l1] = null;
			}
	}

	public void resetTiles(int skipX, int skipY) {
		if (true)
			return;
		if (skipX < 0 || skipY < 0)
			return;
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < length; y++) {
					SceneTile tile = tiles[z][x][y];
					if (skipX == x && skipY == y && z == Options.currentHeight.get() || tile == null) {
						continue;
					}
					boolean tileModified = tile.tileHighlighted;
					tile.tileHighlighted = false;

					if (tile.temporaryObject.isPresent()) {
						tileModified = true;
						tile.temporaryObject = Optional.empty();
						tile.temporaryObjectAttributes = Optional.empty();

					}

					if (tile.temporaryShapedTile.isPresent()) {
						tileModified = true;
						tile.temporaryShapedTile = Optional.empty();

					}

					if (tile.temporarySimpleTile.isPresent()) {
						tileModified = true;
						tile.temporarySimpleTile = Optional.empty();

					}

					if (tileModified) {
						////tileQueue.push(tile);
					}
				}
			}
		}
	}

	public void resetTiles() {
		lastModifiedTiles.forEach(tile -> {
			tile.tileHighlighted = false;
			if (tile.temporaryObject.isPresent()) {
				tile.temporaryObject = Optional.empty();
				tile.temporaryObjectAttributes = Optional.empty();

			}

			if (tile.temporaryShapedTile.isPresent()) {
				tile.temporaryShapedTile = Optional.empty();

			}

			if (tile.temporarySimpleTile.isPresent()) {
				tile.temporarySimpleTile = Optional.empty();
			}

		});
	}

	public void resetTileSelections() {

		clickStartX = -1;
		clickStartY = -1;
		lastSelectedZ = -1;
		for (int z = 0; z < 4; z++)
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < length; y++) {
					SceneTile tile = getTile(z, x, y);
					if (tile.tileSelected) {
						tile.tileSelected = false;
						//tileQueue.push(tile);

					}
				}
			}
	}

	public byte[] saveObjects(Chunk chunk) {//TODO Expand this
		TreeMap<Integer, ObjectGroup> objectGroupMap = new TreeMap<>();
		for (int z = 0; z < 4; z++) {
			for (int x = chunk.offsetX; x < chunk.offsetX + 64; x++) {
				for (int y = chunk.offsetY; y < chunk.offsetY + 64; y++) {
					List<DefaultWorldObject> objs = new ArrayList<>();
					SceneTile tile = tiles[z][x][y];
					if (tile != null) {
						for (GameObject object : tile.gameObjects)
							if (object != null) {
								if(object.getX() == x && object.getY() == y)
								objs.add(object);
							}
						if (tile.groundDecoration != null) {
							objs.add(tile.groundDecoration);
						}
						if (tile.wallDecoration != null) {
							objs.add(tile.wallDecoration);
						}
						if (tile.wall != null) {
							objs.add(tile.wall);
						}

					}
					for (DefaultWorldObject object : objs) {
						int objectId = object.getKey().getId();
						object.setPlane(z);// XXX?
						ObjectGroup objectGroup = objectGroupMap.getOrDefault(objectId, new ObjectGroup(objectId));
						objectGroup.addObject(object);
						objectGroupMap.put(objectId, objectGroup);
					}
				}
			}
		}
		Buffer buff = new Buffer(new byte[131072]);

		int lastObjectId = -1;
		for (Entry<Integer, ObjectGroup> entry : objectGroupMap.entrySet().stream().sorted(Comparator.comparingInt(Entry::getKey)).collect(Collectors.toList())) {

			int objectId = entry.getKey();
			ObjectGroup group = entry.getValue();
			if (group.getObjects().size() <= 0)
				System.out.println("WARNING: 0 objects for id " + objectId);
			int newObj = objectId - lastObjectId;

			buff.writeUSmartInt(newObj);
			group.sort();
			int previousLocHash = 0;
			for (DefaultWorldObject obj : group.getObjects()) {

				int locHash = obj.getLocHash();

				int newLocHash = locHash - previousLocHash + 1;
				if (previousLocHash != locHash) {
					buff.writeUSmartInt(newLocHash);
				} else {
					buff.writeUSmartInt(1);
				}
				buff.writeByte(obj.getConfig());
				previousLocHash = locHash;
			}
			buff.writeUSmart(0);
			lastObjectId = objectId;
		}

		buff.writeUSmartInt(0);
		byte[] data = Arrays.copyOf(buff.getPayload(), buff.getPosition());

		return data;
	}

	public boolean sceneVisible(int cameraX, int cameraY) {
		cameraX /= 128;
		cameraY /= 128;

		return cameraX >= offsetX - Options.renderDistance.get() && cameraX <= offsetX + 64 + Options.renderDistance.get() && cameraY >= offsetY - Options.renderDistance.get()
				&& cameraY <= offsetY + 64 + Options.renderDistance.get();
	}

	private void selectTile(int tileX, int tileY, int plane, boolean selected, boolean updateSelected) {
		SceneTile selectedTile = getTile(plane, tileX, tileY);
		if (updateSelected) {
			clickStartX = tileX;
			clickStartY = tileY;
			lastSelectedZ = plane;
		}
		selectedTile.tileBeingSelected = selected;
		selectedTile.hasUpdated = true;
		//tileQueue.push(selectedTile);
	}

	private void deselectTile(int tileX, int tileY, int plane) {
		SceneTile selectedTile = getTile(plane, tileX, tileY);

		selectedTile.tileBeingSelected = false;
		selectedTile.tileSelected = false;
		selectedTile.hasUpdated = true;
		//tileQueue.push(selectedTile);
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
		SceneTile tile = tiles[plane][x][y];
		if (tile == null)
			return;

		tiles[plane][x][y].collisionPlane = collisionPlane;
	}

	public void shadeObjects(int lighting, int drawX, int drawY, int drawZ, int l) {
		//TODO Do this dynamically.
		int length = (int) Math.sqrt(drawX * drawX + drawY * drawY + drawZ * drawZ);
		int k1 = l * length >> 8;

		for (int z = 0; z < planeCount; z++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < this.length; y++) {
					SceneTile tile = tiles[z][x][y];

					if (tile != null) {
						Wall wall = tile.wall;

						if (wall != null && wall.getPrimary() != null && wall.getPrimary().hasNormals()) {
							Mesh primary = wall.getPrimary().asMesh();
							method307(z, 1, 1, x, y, primary);

							if (wall.getSecondary() != null && wall.getSecondary().hasNormals()) {
								Mesh secondary = wall.getSecondary().asMesh();
								method307(z, 1, 1, x, y, secondary);
								mergeNormals(primary, secondary, 0, 0, 0, false);
								secondary.shade(lighting, k1, drawX, drawY, drawZ);
							}
							primary.shade(lighting, k1, drawX, drawY, drawZ);
						}

						for (int index = 0; index < tile.objectCount; index++) {
							GameObject object = tile.gameObjects[index];

							if (object != null && object.getPrimary() != null
									&& object.getPrimary().hasNormals()) {
								Mesh primary = object.getPrimary().asMesh();
								method307(z, object.maxX - object.getX() + 1, object.maxY - object.getY() + 1, x, y, primary);
								primary.shade(lighting, k1, drawX, drawY, drawZ);
							}
						}

						GroundDecoration decoration = tile.groundDecoration;
						if (decoration != null && decoration.getPrimary() != null && decoration.getPrimary().hasNormals()) {
							Mesh primary = decoration.getPrimary().asMesh();
							method306(primary, x, y, z);
							primary.shade(lighting, k1, drawX, drawY, drawZ);
						}
					}
				}
			}
		}
	}

	public void shadeObjectsOnTile(int x, int y, int z, int lighting, int drawX, int drawY, int drawZ, int l) {
		int length = (int) Math.sqrt(drawX * drawX + drawY * drawY + drawZ * drawZ);
		int k1 = l * length >> 8;

		SceneTile tile = tiles[z][x][y];

		if (tile != null) {
			Wall wall = tile.wall;

			if (wall != null && wall.getPrimary() != null && wall.getPrimary().hasNormals()) {
				Mesh primary = wall.getPrimary().asMesh();
				method307(z, 1, 1, x, y, primary);

				if (wall.getSecondary() != null && wall.getSecondary().hasNormals()) {
					Mesh secondary = wall.getSecondary().asMesh();
					method307(z, 1, 1, x, y, secondary);
					mergeNormals(primary, secondary, 0, 0, 0, false);
					secondary.shade(lighting, k1, drawX, drawY, drawZ);
				}
				primary.shade(lighting, k1, drawX, drawY, drawZ);
			}

			for (int index = 0; index < tile.objectCount; index++) {
				GameObject object = tile.gameObjects[index];

				if (object != null && object.getPrimary() != null
						&& object.getPrimary().hasNormals()) {
					Mesh primary = object.getPrimary().asMesh();
					method307(z, object.maxX - object.getX() + 1, object.maxY - object.getY() + 1, x, y, primary);
					primary.shade(lighting, k1, drawX, drawY, drawZ);
				}
			}

			GroundDecoration decoration = tile.groundDecoration;
			if (decoration != null && decoration.getPrimary() != null && decoration.getPrimary().hasNormals()) {
				Mesh primary = decoration.getPrimary().asMesh();
				method306(primary, x, y, z);
				primary.shade(lighting, k1, drawX, drawY, drawZ);
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
				SceneTile selectedTile = getTile(plane, x, y);
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
				SceneTile selectedTile = getTile(plane, x, y);
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
				SceneTile selectedTile = getTile(plane, x, y);
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
				SceneTile selectedTile = getTile(plane, x, y);
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
				SceneTile selectedTile = getTile(plane, x, y);
				if (selectedTile != null) {
					if (selectedTile.tileSelected) {
						return getMapRegion().overlayShapes[plane][x][y];
					}

				}
			}
		}
		return -1;
	}


	public SceneTile getTile(Vector2D mouseLocation) {
		//TODO
		//Screen to world conversion.
		return null;
	}

	public List<SceneTile> getSelectedTiles() {
		return Stream.of(tiles[Options.currentHeight.get()]).flatMap(Stream::of).filter(tile -> tile.tileSelected).collect(Collectors.toList());
	}

	public void setSelectedHeight() {
		setTileHeights(getSelectedTiles(), false);
	}

	public void setSelectedUnderlays() {
		setTileUnderlays(getSelectedTiles());
	}

	public void setSelectedOverlays() {
		setTileOverlays(getSelectedTiles());
	}

	public void setSelectedFlags() {
		setTileFlags(getSelectedTiles());
	}

	public void setAbsoluteHeight() {
		setTileHeights(getSelectedTiles(), true);
	}

	public void setTileHeights(List<SceneTile> tiles, boolean absolute) {
		ToolType currentTool = Options.currentTool.get();
		Options.currentTool.set(ToolType.MODIFY_HEIGHT);
		if (!this.currentStateCorrect()) {
			initChanges();
		}
		int lowestX = tiles.stream().mapToInt(tile -> tile.positionX).min().getAsInt();
		int lowestY = tiles.stream().mapToInt(tile -> tile.positionY).min().getAsInt();
		int highestX = tiles.stream().mapToInt(tile -> tile.positionX).max().getAsInt();
		int highestY = tiles.stream().mapToInt(tile -> tile.positionY).max().getAsInt();
		tiles.stream().forEach(tile -> {
			int plane = tile.plane;
			int x = tile.positionX;
			int y = tile.positionY;
			getMapRegion().manualTileHeight[plane][x][y] = 1;
			if (absolute) {

				getMapRegion().tileHeights[plane][x][y] = -Options.tileHeightLevel.get();
			} else {
				getMapRegion().tileHeights[plane][x][y] = (plane == 0 ? 0 - Options.tileHeightLevel.get()
						: getMapRegion().tileHeights[plane - 1][x][y] - Options.tileHeightLevel.get());
			}
			for (int z = 1; z < 4; z++) {
				if (this.getMapRegion().tileHeights[z][x][y] > this.getMapRegion().tileHeights[z - 1][x][y]) {
					this.getMapRegion().tileHeights[z][x][y] = this.getMapRegion().tileHeights[z - 1][x][y];// Not sure
					// on this
				}
			}

		});

		getMapRegion().setHeights();//For beyond edge updates

		tileQueue.clear();
		this.shadeObjects(64, -50, -10, -50, 768);
		getMapRegion().updateTiles();

		this.updateHeights(lowestX - 3, lowestY - 3, highestX - lowestX + 3, highestY - lowestY + 3);
		SceneGraph.commitChanges();
		Options.currentTool.set(currentTool);
	}


	public void setTileUnderlays(List<SceneTile> tiles) {
		ToolType currentTool = Options.currentTool.get();
		Options.currentTool.set(ToolType.PAINT_UNDERLAY);
		if (!this.currentStateCorrect()) {
			initChanges();
		}
		tiles.stream().forEach(tile -> {
			int plane = tile.plane;
			int x = tile.positionX;
			int y = tile.positionY;
			if (currentState.isPresent()) {
				UnderlayState tileState = new UnderlayState(x, y, plane);
				tileState.preserve();
				((TileChange<UnderlayState>) currentState.get()).preserveTileState(tileState);
			}
			this.getMapRegion().underlays[plane][x][y] = (byte) Options.underlayPaintId.get();
			this.tiles[plane][x][y].hasUpdated = true;

		});

		getMapRegion().updateTiles();

		SceneGraph.commitChanges();
		Options.currentTool.set(currentTool);
	}

	public void setTileOverlays(List<SceneTile> tiles) {
		ToolType currentTool = Options.currentTool.get();
		Options.currentTool.set(ToolType.PAINT_OVERLAY);
		if (!this.currentStateCorrect()) {
			initChanges();
		}

		tiles.stream().forEach(tile -> {
			int plane = tile.plane;
			int x = tile.positionX;
			int y = tile.positionY;
			if (currentState.isPresent()) {
				OverlayState tileState = new OverlayState(x, y, plane);
				tileState.preserve();
				((TileChange<OverlayState>) currentState.get()).preserveTileState(tileState);
			}
			if (Options.overlayPaintShapeId.get() == 0) {
				this.getMapRegion().overlays[plane][x][y] = (byte) 0;
			} else {
				this.getMapRegion().overlays[plane][x][y] = (byte) Options.overlayPaintId
						.get();
				this.getMapRegion().overlayShapes[plane][x][y] = (byte) (Options.overlayPaintShapeId
						.get() - 1);
				this.getMapRegion().overlayOrientations[plane][x][y] = (byte) Options.rotation.get();
			}
			this.tiles[plane][x][y].hasUpdated = true;

		});


		getMapRegion().updateTiles();

		SceneGraph.commitChanges();
		Options.currentTool.set(currentTool);
	}


	public void setTileFlags(List<SceneTile> tiles) {
		ToolType currentTool = Options.currentTool.get();
		Options.currentTool.set(ToolType.MODIFY_HEIGHT);
		if (!this.currentStateCorrect()) {
			initChanges();
		}
		int flag = Options.tileFlags.get().encode();
		tiles.stream().forEach(tile -> getMapRegion().tileFlags[tile.plane][tile.positionX][tile.positionY] = (byte) flag);

		getMapRegion().updateTiles();

		SceneGraph.commitChanges();
		Options.currentTool.set(currentTool);
	}


	public Stream<SceneTile> nonNullStream(int z) {
		return Stream.of(tiles[z]).filter(Objects::nonNull).flatMap(Stream::of);
	}

	public Stream<SceneTile> nonNullStream() {
		return IntStream.range(0, 4).boxed().flatMap(this::nonNullStream);
	}

	public SceneTile getTileOrDefault(int x, int y, int z, Function<Location, SceneTile> defaultProvider){
		SceneTile tile = tiles[z][x][y];
		if(tile == null)
			tile = defaultProvider.apply(Location.of(x, y, z));
		return tile;
	}

	private SceneTile createNewSceneTile(Location location){
		return new SceneTile(location.getX(), location.getY(), location.getZ());
	}
	public void removeAndReplace(DefaultWorldObject object){
		SceneTile tile = getTileOrDefault(object.getX(), object.getY(), object.getPlane(), this::createNewSceneTile);
		if(tile.contains(object.getKey())){
			tile.removeByUID(object.getKey());

		}

	}

	public void rotateSelectedObjects(int rotateDir) {
		log.info("Requested to rotate object {}", rotateDir);
		if(!selectedObjects.isEmpty()){
			selectedObjects.stream().sorted().forEach(this::removeAndReplace);
		}
	}
}
