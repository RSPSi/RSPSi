package com.rspsi.options;

import java.util.List;

import com.google.common.collect.Lists;
import com.jagex.map.SceneTileData;
import com.jagex.util.BitFlag;
import com.rspsi.datasets.ObjectDataset;
import com.rspsi.core.misc.BrushType;
import com.rspsi.core.misc.ToolType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Options {

	public static BooleanProperty showOverlayNumbers = new SimpleBooleanProperty(false);
	public static BooleanProperty showUnderlayNumbers = new SimpleBooleanProperty(false);
	public static BooleanProperty showTileHeightNumbers = new SimpleBooleanProperty(false);

	public static BooleanProperty showHiddenTiles = new SimpleBooleanProperty(false);
	public static BooleanProperty showObjects = new SimpleBooleanProperty(true);
	public static BooleanProperty disableBlending = new SimpleBooleanProperty(false);
	public static BooleanProperty showOverlay = new SimpleBooleanProperty(false);
	public static BooleanProperty allHeightsVisible = new SimpleBooleanProperty(false);

	public static BooleanProperty simulateBridgesProperty = new SimpleBooleanProperty(false);
	
	public static BooleanProperty absoluteHeightProperty = new SimpleBooleanProperty(false);

	public static BooleanProperty showBlockedFlag = new SimpleBooleanProperty(false);
	public static BooleanProperty showBridgeFlag = new SimpleBooleanProperty(false);
	public static BooleanProperty showForceLowestPlaneFlag = new SimpleBooleanProperty(false);
	public static BooleanProperty showDisableRenderFlag = new SimpleBooleanProperty(false);
	public static BooleanProperty showLowerZFlag = new SimpleBooleanProperty(false);
	
	public static BooleanProperty showMinimapFunctionModels = new SimpleBooleanProperty(false);

	public static BooleanProperty showDebug = new SimpleBooleanProperty(false);

	public static IntegerProperty currentHeight = new SimpleIntegerProperty(0);
	public static IntegerProperty tileHeightLevel = new SimpleIntegerProperty(50);
	public static IntegerProperty brushSize = new SimpleIntegerProperty(1);
	public static IntegerProperty objectSelectionType = new SimpleIntegerProperty(0);
	
	public static ObjectProperty<BitFlag> tileFlags = new SimpleObjectProperty<BitFlag>(new BitFlag());
	
	public static IntegerProperty rotation = new SimpleIntegerProperty(0);

	public static ObjectProperty<ToolType> currentTool = new SimpleObjectProperty<ToolType>(ToolType.SELECT_TILE);

	public static ObjectProperty<ObjectDataset> currentObject = new SimpleObjectProperty<ObjectDataset>();

	public static IntegerProperty overlayPaintId = new SimpleIntegerProperty(0);
	public static IntegerProperty overlayPaintShapeId = new SimpleIntegerProperty(1);
	public static IntegerProperty underlayPaintId = new SimpleIntegerProperty(0);
	public static ObjectProperty<BrushType> brushType = new SimpleObjectProperty<BrushType>(BrushType.RECTANGLE);
	
	public static BooleanProperty hdTextures = new SimpleBooleanProperty(false);

	public static List<SceneTileData> importData = Lists.newArrayList();

	public static BooleanProperty hdMap = new SimpleBooleanProperty(false);
	
	public static BooleanProperty loadAnimations = new SimpleBooleanProperty(false);

	public static BooleanProperty unsavedChanges = new SimpleBooleanProperty(false);
	
	public static IntegerProperty renderDistance = new SimpleIntegerProperty(30);
	public static IntegerProperty mapRegionSize = new SimpleIntegerProperty(256);
	

	public static BooleanProperty showCamera = new SimpleBooleanProperty(false);
	public static BooleanProperty showBorders = new SimpleBooleanProperty(false);
	public static BooleanProperty showMapFileNames = new SimpleBooleanProperty(false);

	public static BooleanProperty rememberEditorSize = new SimpleBooleanProperty(true);
	public static BooleanProperty rememberEditorLocation = new SimpleBooleanProperty(false);

}
