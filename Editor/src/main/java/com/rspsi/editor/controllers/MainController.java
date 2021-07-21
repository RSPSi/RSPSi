package com.rspsi.editor.controllers;

import com.google.common.primitives.Doubles;
import com.rspsi.editor.MainWindow;
import com.rspsi.editor.Testing;
import com.rspsi.editor.controls.SwatchControl;
import com.rspsi.editor.controls.WindowControls;
import com.rspsi.editor.swatches.SwatchType;
import com.rspsi.editor.tools.BridgeBuilder;
import com.rspsi.editor.tools.ToolNotRegisteredException;
import com.rspsi.editor.tools.ToolRegister;
import com.rspsi.editor.tools.UserInterfaceSupplier;
import com.rspsi.editor.tools.integrated.DeleteObjectTool;
import com.rspsi.editor.tools.integrated.SelectObjectTool;
import com.rspsi.editor.tools.integrated.SelectTilesTool;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.tile.TileShape;
import com.rspsi.jagex.util.BitFlag;
import com.rspsi.jagex.util.RenderFlags;
import com.rspsi.misc.BrushType;
import com.rspsi.misc.ToolType;
import com.rspsi.options.Options;
import com.rspsi.util.AlwaysSelectToggleGroup;
import com.rspsi.util.ChangeListenerUtil;
import com.rspsi.util.FXDialogs;
import com.rspsi.util.Settings;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.converter.IntegerStringConverter;
import lombok.Getter;
import lombok.val;

import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

@Getter
public class MainController {

	@FXML
	private MenuItem returnToLauncherMenuItem;

	@FXML
	private MenuItem changeViewDist;

	@FXML
	private MenuItem openAsPackBtn;

	@FXML
	private MenuItem saveToCacheBtn;

	@FXML
	private MenuItem saveAsPackFile;

	@FXML
	private MenuItem showMapIndexEditor;

	@FXML
	private HBox grabBar;

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
	private CheckMenuItem useGlRendering;

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
	private Button returnToLauncher;


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
	private CheckBox disableRenderCheck;

	@FXML
	private CheckBox drawOnLowerZCheck;
	

	@FXML
	private CheckBox absoluteHeightCheck;

	@FXML
	private ToggleButton setFlagBtn;
	
    @FXML
    private ToggleButton paintOverlayBtn;

    @FXML
    private ToggleButton paintUnderlayBtn;

	@FXML
	private MenuItem copyTileFlags;


	@FXML
	private CheckMenuItem showLowerZFlag;


	@FXML
	private MenuItem copyTileHeights;

	@FXML
	private TextField tileHeightTextBox;

	@FXML
	private Button tileHeightTextButton;

	@FXML
	private MenuItem setFlagsToTiles;

	@FXML
	private MenuItem setHeightsToTiles;

	@FXML
	private MenuItem showFullMap;

	@FXML
	private MenuItem reloadSwatchesBtn;
	@FXML
	private MenuItem reloadModelsBtn;

	@FXML
	private MenuItem fixHeightsBtn;

	@FXML
	private MenuItem getOverlayFromTile;

	@FXML
	private MenuItem getUnderlayFromTile;

	@FXML
	private CheckMenuItem displayOverlayIds;

	@FXML
	private CheckMenuItem displayUnderlayIds;

	@FXML
	private CheckMenuItem displayTileHeights;

	@FXML
	private CheckMenuItem showMapIconObjs;

	@FXML
	private CheckMenuItem showAnimsMenuItem;

	@FXML
	private TabPane mainTabPane;

	@FXML
	private MenuItem setOverlays;

	@FXML
	private MenuItem setUnderlays;

	@FXML
	private CheckMenuItem simulateBridges;

	@FXML
	private MenuItem showRemapperBtn;

	@FXML
	private MenuItem setRelativeHeight;
	
	@FXML
	private MenuItem generateBridgeBtn;
	
	@FXML
	private VBox root;
	

    @FXML
    private Menu fileMenu, editMenu, viewMenu, tilesMenu, toolsMenu, helpMenu, debugMenu;

	public void initializeToolButtons() {
		this.toolGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
			if (newVal == null) {
				if (toolGroup.getProperties().get("deselect") == null) {
					oldVal.setSelected(true);
				} else {
					toolGroup.getProperties().remove("deselect");
				}
			}
		});

		//ChangeListenerUtil.addListener(() -> SceneGraph.mouseWasDown = true, Options.currentTool);

		/*ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(SelectTilesTool.IDENTIFIER);
		}, selectTileBtn.selectedProperty());

		ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(ToolType.MODIFY_HEIGHT);
		}, heightModifyBtn.selectedProperty());

		ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(SelectObjectTool.IDENTIFIER);
		}, selectObjectBtn.selectedProperty());

		
		ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(ToolType.PAINT_OVERLAY);
		}, paintOverlayBtn.selectedProperty());
		ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(ToolType.PAINT_UNDERLAY);
		}, paintUnderlayBtn.selectedProperty());
		
		ChangeListenerUtil.addListener(true, () -> {
			Options.currentTool.set(ToolType.SET_FLAGS);
		}, setFlagBtn.selectedProperty());
		*/

	}

	public void loadTabs(MainWindow application) {

		for (int i = 0; i < 3; i++) {
			SwatchType swatchType = SwatchType.getById(i);
			SwatchControl swatch = new SwatchControl(swatchType);
			switch (swatchType) {
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

				AlwaysSelectToggleGroup.setup(tg);
				ToggleButton[] shapeButtons = new ToggleButton[TileShape.tileShapePoints.length];
				for (int type = 0; type < TileShape.tileShapePoints.length; type++) {
					Pane g = Testing.generateImage(type);
					ToggleButton btn = new ToggleButton();
					shapeButtons[type] = btn;
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
					if (type == 1) {
						btn.setSelected(true);
					}

				}
				flowPane.setAlignment(Pos.CENTER);
				swatch.getController().getVboxContainer().setSpacing(10);
				swatch.setOverlayShapeGroup(tg);
				swatch.getController().getVboxContainer().getChildren().add(0, flowPane);
				application.setOverlaySwatch(swatch);

				//TODO Find a less aids way of doing this

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

		ChangeListenerUtil.addListener(() -> this.tileHeightTextBox.setText(this.heightLevelSlider.getValue() + ""),
				Options.tileHeightLevel);

		currentHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3, 0));
		windowControls = WindowControls.addWindowControls(application.getStage(), grabBar, controlBox);

		// windowControls.getResizeHelper().setMinWidth(1240);
		objectSelectionType.getItems().add("ALL");
		objectSelectionType.getSelectionModel().select(0);
		IntStream.range(0, 23).forEach(act -> objectSelectionType.getItems().add(String.valueOf(act)));

		Options.objectSelectionType.bind(objectSelectionType.getSelectionModel().selectedIndexProperty());

		brushTypeSelection.getItems().addAll(BrushType.values());
		Options.brushType.bindBidirectional(brushTypeSelection.valueProperty());
		// brushTypeSelection.get
		// brushTypeSelection.
		brushTypeSelection.getSelectionModel().select(0);

		ChangeListenerUtil.addListener(() -> SceneGraph.minimapUpdate = true, mapPane.widthProperty());

		Options.brushSize.bindBidirectional(brushSizeSlider.valueProperty());
		brushSizeTooltip.textProperty().set(Options.brushSize.get() + 1 + "");
		ChangeListenerUtil.addListener(() -> brushSizeTooltip.textProperty().set(Options.brushSize.get() + 1 + ""),
				Options.brushSize);

		Options.showDebug.bindBidirectional(showFPSCheckItem.selectedProperty());

		Options.tileHeightLevel.bindBidirectional(heightLevelSlider.valueProperty());

		Options.useGlRendering.bindBidirectional(useGlRendering.selectedProperty());

		if(Settings.getSetting("useGlRendering", false)) {
			useGlRendering.setSelected(true);
		}
		// ChangeListenerUtil.addListener(() ->
		// heightLevelTooltip.setText(Options.tileHeightLevel.get() + ""),
		// Options.tileHeightLevel);

		Options.absoluteHeightProperty.bindBidirectional(this.absoluteHeightCheck.selectedProperty());
		Options.disableBlending.bindBidirectional(this.disableBlendingCheckItem.selectedProperty());
		Options.showObjects.bindBidirectional(this.showObjectsCheckItem.selectedProperty());
		Options.allHeightsVisible.bindBidirectional(this.allHeightsCheckItem.selectedProperty());
		Options.showOverlay.bindBidirectional(this.showOverlaysCheckItem.selectedProperty());

		Options.showOverlayNumbers.bindBidirectional(this.displayOverlayIds.selectedProperty());
		Options.showUnderlayNumbers.bindBidirectional(this.displayUnderlayIds.selectedProperty());
		Options.showTileHeightNumbers.bindBidirectional(this.displayTileHeights.selectedProperty());

		Options.showBlockedFlag.bindBidirectional(this.showBlockedFlag.selectedProperty());
		Options.showBridgeFlag.bindBidirectional(this.showBridgeFlag.selectedProperty());
		Options.showForceLowestPlaneFlag.bindBidirectional(this.showLowestFlag.selectedProperty());
		Options.showDisableRenderFlag.bindBidirectional(this.showDisableFlag.selectedProperty());
		Options.showLowerZFlag.bindBidirectional(this.showLowerZFlag.selectedProperty());

		Options.showMinimapFunctionModels.bindBidirectional(this.showMapIconObjs.selectedProperty());

		Options.loadAnimations.bindBidirectional(this.showAnimsMenuItem.selectedProperty());

		decreaseBrushSizeBtn.setOnAction(act -> brushSizeSlider.adjustValue(brushSizeSlider.getValue() - 1));
		increaseBrushSizeBtn.setOnAction(act -> brushSizeSlider.adjustValue(brushSizeSlider.getValue() + 1));

		Runnable calculateFlags = () -> {
			BitFlag flag = new BitFlag();
			if (this.unwalkableCheck.isSelected())
				flag.flag(RenderFlags.BLOCKED_TILE);
			if (this.bridgeCheck.isSelected())
				flag.flag(RenderFlags.BRIDGE_TILE);
			if (this.forceLowestCheck.isSelected())
				flag.flag(RenderFlags.FORCE_LOWEST_PLANE);
			if (this.drawOnLowerZCheck.isSelected())
				flag.flag(RenderFlags.RENDER_ON_LOWER_Z);
			if (this.disableRenderCheck.isSelected())
				flag.flag(RenderFlags.DISABLE_RENDERING);

			Options.tileFlags.set(flag);
		};
		this.heightLevelSlider.valueProperty()
				.addListener((observable, oldVal, newVal) -> this.tileHeightTextBox.setText(newVal.intValue() + ""));
		this.tileHeightTextButton.setOnAction(
				evt -> { 
					this.heightLevelSlider.adjustValue(Doubles.tryParse(this.tileHeightTextBox.getText()));
					System.out.println(this.heightLevelSlider.valueProperty().get() + " : " + this.heightLevelSlider.valueProperty().intValue());
					this.getTileHeightTextBox().textProperty().set(this.getHeightLevelSlider().valueProperty().intValue() + "");
					this.tileHeightTextButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("notset"), false);
				});
		UnaryOperator<TextFormatter.Change> integerFilter = change -> {
			String newText = change.getControlNewText();
			
			if (newText.matches("-?([0-9]*)?")) {
				return change;
			}
			return null;
		};
		this.tileHeightTextBox.textProperty().addListener((observable, oldVal, newVal) -> {
			int val = Integer.parseInt(newVal);
			if (val != heightLevelSlider.valueProperty().intValue()) {
				this.tileHeightTextButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("notset"), true);
				System.out.println(val + " : " + this.heightLevelSlider.valueProperty().get());
			} else {
				this.tileHeightTextButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("notset"), false);
			}
		});
		tileHeightTextBox.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(),
				heightLevelSlider.valueProperty().intValue(), integerFilter));
		tileHeightTextBox.addEventFilter(KeyEvent.ANY, evt -> {
			if(evt.getCode() == KeyCode.ENTER) {
				tileHeightTextButton.fire();
			}
		});

		ChangeListenerUtil.addListener(calculateFlags, unwalkableCheck.selectedProperty(),
				bridgeCheck.selectedProperty(), forceLowestCheck.selectedProperty(),
				drawOnLowerZCheck.selectedProperty(), disableRenderCheck.selectedProperty());

		Options.showHiddenTiles.bindBidirectional(hiddenTilesCheckItem.selectedProperty());
		Options.currentHeight.bind(currentHeightSpinner.valueProperty());

		Options.simulateBridgesProperty.bindBidirectional(simulateBridges.selectedProperty());
		
		generateBridgeBtn.setOnAction(evt -> {
			try {
				BridgeBuilder.buildBridge();
			} catch (Exception | ToolNotRegisteredException e) {
				FXDialogs.showError(application.getStage().getOwner(),"Error while generating bridge!", "Message: " + e.getMessage());
			}
		});

		/*Options.currentTool.addListener((observable, oldVal, newVal) -> {

			deselectTools();

			if (newVal == ToolType.SELECT_OBJECT) {

			} else if (newVal == ToolType.DELETE_OBJECT) {
				this.deleteObjectBtn.setSelected(true);
			} else if (newVal == ToolType.MODIFY_HEIGHT) {
				this.heightModifyBtn.setSelected(true);
			} else if (newVal == ToolType.SET_FLAGS) {
				this.setFlagBtn.setSelected(true);
			} else if (newVal == ToolType.SELECT_TILE) {
				this.selectTileBtn.setSelected(true);
			} else if(newVal == ToolType.PAINT_OVERLAY) {
				this.paintOverlayBtn.setSelected(true);
			} else if(newVal == ToolType.PAINT_UNDERLAY) {
				this.paintUnderlayBtn.setSelected(true);
			}

		});

		this.setRelativeHeight.setOnAction(evt -> Client.getSingleton().sceneGraph.setAbsoluteHeight());
		this.setFlagsToTiles.setOnAction(
				evt -> SceneGraph.onCycleEnd.add(SceneGraph::setSelectedFlags));
		this.setHeightsToTiles.setOnAction(
				evt -> SceneGraph.onCycleEnd.add(SceneGraph::setSelectedHeight));
		this.setOverlays.setOnAction(
				evt -> SceneGraph.onCycleEnd.add(SceneGraph::setSelectedOverlays));
		this.setUnderlays.setOnAction(
				evt -> SceneGraph.onCycleEnd.add(SceneGraph::setSelectedUnderlays));
*/
	}

	private void deselectTools() {
		toolGroup.getProperties().put("deselect", true);
		toolGroup.selectToggle(null);
	}

	private WindowControls windowControls;

	@FXML
	private VBox toolButtonBar;

	public void addToolButton(ToggleButton button, String identifier) {
		button.setToggleGroup(toolGroup);
		button.setId(identifier);
		toolButtonBar.getChildren().add(button);
	}
}
