package com.rspsi.dialogs;

import com.jagex.Client;
import com.jagex.map.SceneGraph;
import com.rspsi.controls.WindowControls;
import com.rspsi.core.misc.CopyOptions;

import com.rspsi.util.FXUtils;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TileCopyDialog extends Application {

	private Stage stage;

	@FXML
	private BorderPane titleBar;

	@FXML
	private HBox controlBox;

	@FXML
	private HBox dockContainer;

	@FXML
	private AnchorPane leftBar;

	@FXML
	private VBox vboxList;

	@FXML
	private CheckBox copyOverlays;

	@FXML
	private CheckBox copyUnderlays;

	@FXML
	private CheckBox copyTileHeights;

	@FXML
	private CheckBox copyGameObjects;

	@FXML
	private CheckBox copyWalls;

	@FXML
	private CheckBox copyWallDecorations;

	@FXML
	private CheckBox copyGroundDecorations;

	@FXML
	private CheckBox copyTileFlags;

	@FXML
	private CheckBox copyTilesAbove;

	@FXML
	private Button saveButton;

	public boolean copyGameObjects() {
		return copyGameObjects.selectedProperty().get();
	}

	public boolean copyGroundDecorations() {
		return copyGroundDecorations.selectedProperty().get();
	}

	public boolean copyOverlays() {
		return copyOverlays.selectedProperty().get();
	}

	public boolean copyTileFlags() {
		return copyTileFlags.selectedProperty().get();
	}

	public boolean copyTileHeights() {
		return copyTileHeights.selectedProperty().get();
	}

	public boolean copyTilesAbove() {
		return copyTilesAbove.selectedProperty().get();
	}

	public boolean copyUnderlays() {
		return copyUnderlays.selectedProperty().get();
	}

	public boolean copyWallDecorations() {
		return copyWallDecorations.selectedProperty().get();
	}

	public boolean copyWalls() {
		return copyWalls.selectedProperty().get();
	}

	public void show() {
		stage.show();
	}
	
	public CopyOptions generateOptions() {
		CopyOptions options = new CopyOptions();
		
		options.setCopyGameObjects(this.copyGameObjects());
		options.setCopyGroundDecorations(copyGroundDecorations());
		options.setCopyOverlays(copyOverlays());
		options.setCopyTileFlags(copyTileFlags());
		options.setCopyTileHeights(copyTileHeights());
		options.setCopyTilesAbove(copyTilesAbove());
		options.setCopyUnderlays(copyUnderlays());
		options.setCopyWallDecorations(copyWallDecorations());
		options.setCopyWalls(copyWalls());
		
		return options;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/copytiles.fxml"));
		loader.setController(this);
		Parent content = loader.load();
		Scene scene = new Scene(content);

		primaryStage.setTitle("Tile Export Tool");
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(scene);
		primaryStage.setAlwaysOnTop(true);

		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();

		// primaryStage.show();
		WindowControls.addUtilityWindowControls(primaryStage, titleBar, controlBox);

		saveButton.setOnAction(act -> {
			SceneGraph.onCycleEnd.add(() -> {
				Client.getSingleton().sceneGraph.copyTiles(generateOptions());
			});
		});
	}
	

	@Override
	public void stop() throws Exception {
		super.stop();
		this.stage.close();
	}

}
