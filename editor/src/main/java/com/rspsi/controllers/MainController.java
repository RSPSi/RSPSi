package com.rspsi.controllers;

import java.util.stream.IntStream;

import org.major.map.RenderFlags;

import com.jagex.Client;
import com.jagex.map.SceneGraph;
import com.jagex.util.BitFlag;
import com.rspsi.MainWindow;
import com.rspsi.Testing;
import com.rspsi.controls.SwatchControl;
import com.rspsi.controls.WindowControls;
import com.rspsi.misc.BrushType;
import com.rspsi.misc.ToolType;
import com.rspsi.options.Options;
import com.rspsi.swatches.SwatchType;
import com.rspsi.util.ChangeListenerUtil;
import com.rspsi.util.FXDialogs;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class MainController {
	
    @FXML
    private MenuItem openAsPackBtn;

    @FXML
    private MenuItem saveToCacheBtn;

    @FXML
    private MenuItem saveAsPackFile;

	
	@FXML
	private MenuItem showMapIndexEditor;

	@FXML
	private HBox headerBar;

	@FXML
	private ImageView iconView;

	@FXML
	private HBox controlBox;

	@FXML
	private Label titleLabel;
	
	@FXML
	private Label statusLabel;

	@FXML
	private MenuBar toolbar;

	@FXML
	private MenuItem newMapButton;

	@FXML
	private Menu openRecentMenu;

	@FXML
	private MenuItem openCoordinateButton;

	@FXML
	private MenuItem openHashButton;

	@FXML
	private MenuItem openFileButton;

	@FXML
	private MenuItem saveMenuItem;

	@FXML
	private MenuItem saveAsMenuItem;

	@FXML
	private MenuItem preferencesMenuItem;

	@FXML
	private MenuItem quitMenuItem;

	@FXML
	private MenuItem copySelectedTilesBtn;

	@FXML
	private MenuItem deleteSelectedTilesBtn;

	@FXML
	private MenuItem pasteTilesBtn;

	@FXML
	private MenuItem addObjectToSwatchBtn;

	@FXML
	private CheckMenuItem hiddenTilesCheckItem;

	@FXML
	private CheckMenuItem allHeightsCheckItem;

	@FXML
	private CheckMenuItem disableBlendingCheckItem;

	@FXML
	private CheckMenuItem showObjectsCheckItem;

	@FXML
	private CheckMenuItem showOverlaysCheckItem;

	@FXML
	private MenuItem forceMapUpdateBtn;
	
	@FXML
	private MenuItem importTilesBtn;

	@FXML
	private MenuItem exportTilesBtn;

	@FXML
	private MenuItem showObjectViewBtn;

	@FXML
	private CheckMenuItem showFPSCheckItem;

	@FXML
	private MenuItem openTutorialBtn;

	@FXML
	private MenuItem contactMeBtn;

	@FXML
	private HBox dockContainer;

	@FXML
	private AnchorPane leftBar;

	@FXML
	private ToggleButton selectTileBtn;

	@FXML
	private ToggleGroup toolGroup;

	@FXML
	private ToggleButton selectObjectBtn;

	@FXML
	private ToggleButton paintTileBtn;

	@FXML
	private ToggleButton heightModifyBtn;

	@FXML
	private ToggleButton deleteObjectBtn;

	@FXML
	private ToggleButton paintFillBtn;

	@FXML
	private ToggleButton heightFillBtn;

	@FXML
	private ToggleButton moveObjectBtn;

	@FXML
	private AnchorPane gamePane;

	@FXML
	private AnchorPane mapPane;

	@FXML
	private Slider brushSizeSlider;

	@FXML
	private ComboBox<BrushType> brushTypeSelection;

	@FXML
	private Slider heightLevelSlider;

	@FXML
	private ComboBox<String> objectSelectionType;

	@FXML
	private Spinner<Integer> currentHeightSpinner;

	@FXML
	private AnchorPane rightBar;

	@FXML
	private Tooltip brushSizeTooltip;

	@FXML
	private Tooltip heightLevelTooltip;

	@FXML
	private Button decreaseBrushSizeBtn;

	@FXML
	private Button increaseBrushSizeBtn;
	
	@FXML
	private TabPane toolsTabPane;
	
	@FXML
	private MenuItem undoMenuItem;

	@FXML
	private MenuItem redoMenuItem;

    @FXML
    private CheckMenuItem showBlockedFlag;

    @FXML
    private CheckMenuItem showBridgeFlag;

    @FXML
    private CheckMenuItem showLowestFlag;

    @FXML
    private CheckMenuItem showDisableFlag;
    
    @FXML
    private CheckBox unwalkableCheck;

    @FXML
    private CheckBox bridgeCheck;

    @FXML
    private CheckBox forceLowestCheck;
    
    @FXML
    private ToggleButton setFlagBtn;

	@FXML
	private MenuItem copyTileFlags;

    @FXML
    private CheckBox disableRenderCheck;
    
	@FXML
	private CheckMenuItem showLowerZFlag;

    @FXML
    private CheckBox drawOnLowerZCheck;
    
    @FXML
    private MenuItem copyTileHeights;
    
    @FXML
    private MenuItem setTileHeightMenuItem;

    @FXML
    private MenuItem setFlagsToTiles;

    @FXML
    private MenuItem setHeightsToTiles;
    
    @FXML
    private MenuItem showFullMap;
    
    @FXML
    private MenuItem fixHeightsBtn;
    
    @FXML
    private MenuItem getOverlayFromTile;

    @FXML
    private MenuItem getUnderlayFromTile;
    
    @FXML
    private CheckMenuItem showMapIconObjs;
    
    @FXML
    private CheckMenuItem showAnimsMenuItem;
    
    @FXML
    private TabPane mainTabPane;

	public MenuItem getCopyTileHeights() {
		return copyTileHeights;
	}

	public ToggleButton getSetFlagBtn() {
		return setFlagBtn;
	}

	public MenuItem getCopyTileFlags() {
		return copyTileFlags;
	}

	public CheckMenuItem getShowBlockedFlag() {
		return showBlockedFlag;
	}

	public CheckMenuItem getShowBridgeFlag() {
		return showBridgeFlag;
	}

	public CheckMenuItem getShowLowestFlag() {
		return showLowestFlag;
	}

	public CheckMenuItem getShowDisableFlag() {
		return showDisableFlag;
	}

	public CheckBox getUnwalkableCheck() {
		return unwalkableCheck;
	}

	public CheckBox getBridgeCheck() {
		return bridgeCheck;
	}

	public CheckBox getForceLowestCheck() {
		return forceLowestCheck;
	}

	public CheckBox getDisableRenderCheck() {
		return disableRenderCheck;
	}

	public MenuItem getAddObjectToSwatchBtn() {
		return addObjectToSwatchBtn;
	}

	public CheckMenuItem getAllHeightsCheckItem() {
		return allHeightsCheckItem;
	}
	
	public MenuItem getForceMapUpdateBtn() {
		return forceMapUpdateBtn;
	}

	public Slider getBrushSizeSlider() {
		return brushSizeSlider;
	}

	public Tooltip getBrushSizeTooltip() {
		return brushSizeTooltip;
	}

	public ComboBox<BrushType> getBrushTypeSelection() {
		return brushTypeSelection;
	}
	
	public MenuItem getShowFullMap() {
		return showFullMap;
	}

	public MenuItem getContactMeBtn() {
		return contactMeBtn;
	}

	public HBox getControlBox() {
		return controlBox;
	}

	public MenuItem getCopySelectedTilesBtn() {
		return copySelectedTilesBtn;
	}

	public Spinner<?> getCurrentHeightSpinner() {
		return currentHeightSpinner;
	}

	public Button getDecreaseBrushSizeBtn() {
		return decreaseBrushSizeBtn;
	}

	public ToggleButton getDeleteObjectBtn() {
		return deleteObjectBtn;
	}

	public MenuItem getDeleteSelectedTilesBtn() {
		return deleteSelectedTilesBtn;
	}

	public CheckMenuItem getDisableBlendingCheckItem() {
		return disableBlendingCheckItem;
	}

	public HBox getDockContainer() {
		return dockContainer;
	}

	public MenuItem getExportTilesBtn() {
		return exportTilesBtn;
	}

	public AnchorPane getGamePane() {
		return gamePane;
	}

	public HBox getHeaderBar() {
		return headerBar;
	}

	public ToggleButton getHeightFillBtn() {
		return heightFillBtn;
	}

	public Slider getHeightLevelSlider() {
		return heightLevelSlider;
	}

	public Tooltip getHeightLevelTooltip() {
		return heightLevelTooltip;
	}

	public ToggleButton getHeightModifyBtn() {
		return heightModifyBtn;
	}

	public CheckMenuItem getHiddenTilesCheckItem() {
		return hiddenTilesCheckItem;
	}

	public ImageView getIconView() {
		return iconView;
	}

	public MenuItem getImportTilesBtn() {
		return importTilesBtn;
	}

	public Button getIncreaseBrushSizeBtn() {
		return increaseBrushSizeBtn;
	}

	public AnchorPane getLeftBar() {
		return leftBar;
	}

	public AnchorPane getMapPane() {
		return mapPane;
	}

	public ToggleButton getMoveObjectBtn() {
		return moveObjectBtn;
	}

	public MenuItem getNewMapButton() {
		return newMapButton;
	}

	public ComboBox<String> getObjectSelectionType() {
		return objectSelectionType;
	}

	public MenuItem getOpenCoordinateButton() {
		return openCoordinateButton;
	}

	public MenuItem getOpenFileButton() {
		return openFileButton;
	}

	public MenuItem getOpenHashButton() {
		return openHashButton;
	}

	public Menu getOpenRecentMenu() {
		return openRecentMenu;
	}

	public MenuItem getOpenTutorialBtn() {
		return openTutorialBtn;
	}

	public ToggleButton getPaintFillBtn() {
		return paintFillBtn;
	}

	public ToggleButton getPaintTileBtn() {
		return paintTileBtn;
	}

	public MenuItem getPasteTilesBtn() {
		return pasteTilesBtn;
	}

	public MenuItem getPreferencesMenuItem() {
		return preferencesMenuItem;
	}

	public MenuItem getQuitMenuItem() {
		return quitMenuItem;
	}

	public AnchorPane getRightBar() {
		return rightBar;
	}

	public MenuItem getSaveAsMenuItem() {
		return saveAsMenuItem;
	}

	public MenuItem getSaveMenuItem() {
		return saveMenuItem;
	}

	public ToggleButton getSelectObjectBtn() {
		return selectObjectBtn;
	}

	public ToggleButton getSelectTileBtn() {
		return selectTileBtn;
	}

	public CheckMenuItem getShowFPSCheckItem() {
		return showFPSCheckItem;
	}

	public CheckMenuItem getShowObjectsCheckItem() {
		return showObjectsCheckItem;
	}

	public MenuItem getShowObjectViewBtn() {
		return showObjectViewBtn;
	}

	public CheckMenuItem getShowOverlaysCheckItem() {
		return showOverlaysCheckItem;
	}

	public Label getTitleLabel() {
		return titleLabel;
	}

	public MenuBar getToolbar() {
		return toolbar;
	}

	public ToggleGroup getToolGroup() {
		return toolGroup;
	}
	
	public TabPane getToolsTabPane() {
		return toolsTabPane;
	}

	public MenuItem getUndoMenuItem() {
		return undoMenuItem;
	}

	public MenuItem getRedoMenuItem() {
		return redoMenuItem;
	}

	public void initializeToolButtons() {
		this.toolGroup.selectedToggleProperty().addListener((ChangeListener<Toggle>) (observable, oldVal, newVal) -> {
			if(newVal == null) {
				if(toolGroup.getProperties().get("deselect") == null) {
					oldVal.setSelected(true);
				} else {
					toolGroup.getProperties().remove("deselect");
				}
			}
		});
		
		ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(ToolType.SELECT_TILE);
		}, selectTileBtn.selectedProperty());

		ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(ToolType.MODIFY_HEIGHT);
		}, heightModifyBtn.selectedProperty());

		ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(ToolType.SELECT_OBJECT);
		}, selectObjectBtn.selectedProperty());
		ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(ToolType.DELETE_OBJECT);
		}, deleteObjectBtn.selectedProperty());
		ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(ToolType.SET_FLAGS);
		}, setFlagBtn.selectedProperty());

	}

	public void loadTabs(MainWindow application) {

		for (int i = 0; i < 3; i++) {
			SwatchType swatchType = SwatchType.getById(i);
			SwatchControl swatch = new SwatchControl(swatchType);
			switch(swatchType) {
			case OBJECT:
				application.setObjectSwatch(swatch);
				break;
			case OVERLAY:
			
				FlowPane flowPane = new FlowPane();
				flowPane.setPadding(new Insets(4, 4, 4, 4));
				flowPane.setOrientation(Orientation.HORIZONTAL);
				flowPane.setRowValignment(VPos.CENTER);
				flowPane.setColumnHalignment(HPos.CENTER);
				flowPane.setHgap(6);
				flowPane.setVgap(6);
				ToggleGroup tg = new ToggleGroup();
				for (int type = 0; type < 13; type++) {
					Pane g = Testing.generateImage(type);
					ToggleButton btn = new ToggleButton();
					tg.getProperties().put(type, btn);
					btn.setAlignment(Pos.CENTER);
					btn.setMaxSize(36, 36);
					btn.setPrefSize(36, 36);
					btn.setMinSize(36, 36);
					btn.setToggleGroup(tg);
					btn.setGraphic(g);
					btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					final int shapeType = type;
					btn.setOnAction(evt -> {
				
						Options.overlayPaintShapeId.set(shapeType);
					});
					flowPane.getChildren().add(btn);
					if (type == 0) {
						btn.setSelected(true);
					}
				}
				flowPane.setAlignment(Pos.CENTER);
				swatch.getController().getVboxContainer().setSpacing(10);
				swatch.setOverlayShapeGroup(tg);
				swatch.getController().getVboxContainer().getChildren().add(0, flowPane);
				application.setOverlaySwatch(swatch);
				break;
			case UNDERLAY:
				application.setUnderlaySwatch(swatch);
				break;
			
			}
			
			Tab tab = new Tab(swatchType.toString());
			tab.setContent(swatch);
			toolsTabPane.getTabs().add(i, tab);
		}
		
		toolsTabPane.getSelectionModel().select(SwatchType.OBJECT.getId());

	}

	public void onLoad(MainWindow application) {
		initializeToolButtons();
		loadTabs(application);
		
		
		
		currentHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3, 0));
		windowControls = WindowControls.addWindowControls(application.getStage(), headerBar, controlBox);

		//windowControls.getResizeHelper().setMinWidth(1240);
		objectSelectionType.getItems().add("ALL");
		objectSelectionType.getSelectionModel().select(0);
		IntStream.range(0, 23).forEach(act -> objectSelectionType.getItems().add(String.valueOf(act)));

		Options.objectSelectionType.bind(objectSelectionType.getSelectionModel().selectedIndexProperty());

		brushTypeSelection.getItems().addAll(BrushType.values());
		Options.brushType.bind(brushTypeSelection.valueProperty());
		//brushTypeSelection.get
	//brushTypeSelection.
		brushTypeSelection.getSelectionModel().select(0);
		
		ChangeListenerUtil.addListener(() -> SceneGraph.minimapUpdate = true, mapPane.widthProperty());

		Options.brushSize.bind(brushSizeSlider.valueProperty());
		brushSizeTooltip.textProperty().set(Options.brushSize.get() + 1 + "");
		ChangeListenerUtil.addListener(() -> brushSizeTooltip.textProperty().set(Options.brushSize.get() + 1 + ""), Options.brushSize);
		
		Options.showDebug.bind(showFPSCheckItem.selectedProperty());

		Options.tileHeightLevel.bind(heightLevelSlider.valueProperty());
		ChangeListenerUtil.addListener(() -> heightLevelTooltip.setText(Options.tileHeightLevel.get() + ""), Options.tileHeightLevel);
		
		Options.disableBlending.bind(this.disableBlendingCheckItem.selectedProperty());
		Options.showObjects.bind(this.showObjectsCheckItem.selectedProperty());
		Options.allHeightsVisible.bind(this.allHeightsCheckItem.selectedProperty());
		Options.showOverlay.bind(this.showOverlaysCheckItem.selectedProperty());

		Options.showBlockedFlag.bind(this.showBlockedFlag.selectedProperty());
		Options.showBridgeFlag.bind(this.showBridgeFlag.selectedProperty());
		Options.showForceLowestPlaneFlag.bind(this.showLowestFlag.selectedProperty());
		Options.showDisableRenderFlag.bind(this.showDisableFlag.selectedProperty());
		Options.showLowerZFlag.bind(this.showLowerZFlag.selectedProperty());
		
		Options.showMinimapFunctionModels.bind(this.showMapIconObjs.selectedProperty());
		
		Options.loadAnimations.bind(this.showAnimsMenuItem.selectedProperty());

		decreaseBrushSizeBtn.setOnAction(act -> brushSizeSlider.adjustValue(brushSizeSlider.getValue() - 1));
		increaseBrushSizeBtn.setOnAction(act -> brushSizeSlider.adjustValue(brushSizeSlider.getValue() + 1));
		
		Runnable calculateFlags = () -> {
			BitFlag flag = new BitFlag();
			if(this.unwalkableCheck.isSelected())
				flag.flag(RenderFlags.BLOCKED_TILE);
			if(this.bridgeCheck.isSelected())
				flag.flag(RenderFlags.BRIDGE_TILE);
			if(this.forceLowestCheck.isSelected())
				flag.flag(RenderFlags.FORCE_LOWEST_PLANE);
			if(this.drawOnLowerZCheck.isSelected())
				flag.flag(RenderFlags.RENDER_ON_LOWER_Z);
			if(this.disableRenderCheck.isSelected())
				flag.flag(RenderFlags.DISABLE_RENDERING);
			
			Options.tileFlags.set(flag);
		};
		
		
		this.setTileHeightMenuItem.setOnAction(evt -> {
			String s = FXDialogs.showTextInput("Enter value", "Please enter a value for the height", "0");
			if(s != null) {
				try {
					int val = Integer.parseInt(s);
					if(val < heightLevelSlider.getMin() || val > heightLevelSlider.getMax()) {
						throw new Exception();
					} else 
						heightLevelSlider.setValue(val);
				} catch(Exception ex) {
					FXDialogs.showError("Error while parsing input", "An error occurred while parsing the input, please enter an integer between " + ((int)heightLevelSlider.getMin()) + " and " + ((int)heightLevelSlider.getMax()));
				}
				
			}
		});
		
		ChangeListenerUtil.addListener(calculateFlags, unwalkableCheck.selectedProperty(), bridgeCheck.selectedProperty(), forceLowestCheck.selectedProperty(), drawOnLowerZCheck.selectedProperty(),  disableRenderCheck.selectedProperty());

		Options.showHiddenTiles.bind(hiddenTilesCheckItem.selectedProperty());
		Options.currentHeight.bind(currentHeightSpinner.valueProperty());
		Options.currentTool.addListener((ChangeListener<ToolType>) (observable, oldVal, newVal) -> {
			
			deselectTools();
			if (oldVal == ToolType.PAINT_OVERLAY) {
				Options.overlayPaintId.set(-1);
				application.overlaySwatch.deselect();
			} else if (oldVal == ToolType.PAINT_UNDERLAY) {
				Options.underlayPaintId.set(-1);
				application.underlaySwatch.deselect();
			} else if (oldVal == ToolType.SPAWN_OBJECT) {
				Options.currentObject.set(null);
				application.objectSwatch.deselect();
			}
			
			if(newVal == ToolType.SELECT_OBJECT) {
				this.selectObjectBtn.setSelected(true);
			} else if(newVal == ToolType.DELETE_OBJECT) {
				this.deleteObjectBtn.setSelected(true);
			} else if(newVal == ToolType.MODIFY_HEIGHT) {
				this.heightModifyBtn.setSelected(true);
			} else if(newVal == ToolType.SET_FLAGS) {
				this.setFlagBtn.setSelected(true);
			} else if(newVal == ToolType.SELECT_TILE) {
				this.selectTileBtn.setSelected(true);
			}
			
			
		});

		this.setFlagsToTiles.setOnAction(evt -> SceneGraph.onCycleEnd.add(() -> Client.getSingleton().sceneGraph.setSelectedFlags()));
		this.setHeightsToTiles.setOnAction(evt -> SceneGraph.onCycleEnd.add(() -> Client.getSingleton().sceneGraph.setSelectedHeight()));
	

	}
	
	
	public MenuItem getOpenAsPackBtn() {
		return openAsPackBtn;
	}

	public MenuItem getSaveToCacheBtn() {
		return saveToCacheBtn;
	}

	public MenuItem getSaveAsPackFile() {
		return saveAsPackFile;
	}

	public CheckMenuItem getShowLowerZFlag() {
		return showLowerZFlag;
	}

	public CheckBox getDrawOnLowerZCheck() {
		return drawOnLowerZCheck;
	}

	private void deselectTools() {
		toolGroup.getProperties().put("deselect", true);
		toolGroup.selectToggle(null);
	}
	
	
	
	
	private WindowControls windowControls;
	
	public WindowControls getWindowControls() {
		return windowControls;
	}

	public MenuItem getGetOverlayFromTile() {
		return getOverlayFromTile;
	}

	public MenuItem getGetUnderlayFromTile() {
		return getUnderlayFromTile;
	}

	public TabPane getMainTabPane() {
		return mainTabPane;
	}

	public Label getStatusLabel() {
		return statusLabel;
	}

	public MenuItem getSetTileHeightMenuItem() {
		return setTileHeightMenuItem;
	}

	public MenuItem getSetFlagsToTiles() {
		return setFlagsToTiles;
	}

	public MenuItem getSetHeightsToTiles() {
		return setHeightsToTiles;
	}

	public MenuItem getFixHeightsBtn() {
		return fixHeightsBtn;
	}

	public CheckMenuItem getShowMapIconObjs() {
		return showMapIconObjs;
	}

	public CheckMenuItem getShowAnimsMenuItem() {
		return showAnimsMenuItem;
	}

	public MenuItem getShowMapIndexEditor() {
		return showMapIndexEditor;
	}
	
	
	
	

}
