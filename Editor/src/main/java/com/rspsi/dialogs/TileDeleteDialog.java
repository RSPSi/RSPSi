package com.rspsi.dialogs;

import com.jagex.Client;
import com.jagex.map.SceneGraph;
import com.rspsi.controls.WindowControls;
import com.rspsi.core.misc.DeleteOptions;

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

public class TileDeleteDialog extends Application {

	public static TileDeleteDialog instance;
	
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
	private CheckBox deleteOverlays;

	@FXML
	private CheckBox deleteUnderlays;

	@FXML
	private CheckBox deleteTileHeights;

	@FXML
	private CheckBox deleteGameObjects;

	@FXML
	private CheckBox deleteWalls;

	@FXML
	private CheckBox deleteWallDecorations;

	@FXML
	private CheckBox deleteGroundDecorations;

	@FXML
	private CheckBox deleteTileFlags;

	@FXML
	private CheckBox deleteTilesAbove;

	@FXML
	private Button saveButton;

	public boolean deleteGameObjects() {
		return deleteGameObjects.selectedProperty().get();
	}

	public boolean deleteGroundDecorations() {
		return deleteGroundDecorations.selectedProperty().get();
	}

	public boolean deleteOverlays() {
		return deleteOverlays.selectedProperty().get();
	}

	public boolean deleteTileFlags() {
		return deleteTileFlags.selectedProperty().get();
	}

	public boolean deleteTileHeights() {
		return deleteTileHeights.selectedProperty().get();
	}

	public boolean deleteTilesAbove() {
		return deleteTilesAbove.selectedProperty().get();
	}

	public boolean deleteUnderlays() {
		return deleteUnderlays.selectedProperty().get();
	}

	public boolean deleteWallDecorations() {
		return deleteWallDecorations.selectedProperty().get();
	}

	public boolean deleteWalls() {
		return deleteWalls.selectedProperty().get();
	}

	public void show() {
		stage.show();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		instance = this;
		stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/deletetiles.fxml"));
		loader.setController(this);
		Parent content = loader.load();
		Scene scene = new Scene(content);

		primaryStage.setTitle("Tile Delete Tool");
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(scene);
		primaryStage.setAlwaysOnTop(true);

		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();
		
		// primaryStage.show();
		WindowControls.addUtilityWindowControls(primaryStage, titleBar, controlBox);

		saveButton.setOnAction(act -> {
			SceneGraph.onCycleEnd.add(() -> {
				Client.getSingleton().sceneGraph.deleteSelectedTiles(generateOptions());
			});
		});
	}
	
	
	public DeleteOptions generateOptions() {
		DeleteOptions options = new DeleteOptions();
		
		options.setDeleteGameObjects(this.deleteGameObjects());
		options.setDeleteGroundDecorations(deleteGroundDecorations());
		options.setDeleteOverlays(deleteOverlays());
		options.setDeleteUnderlays(deleteUnderlays());
		options.setDeleteWallDecorations(deleteWallDecorations());
		options.setDeleteWalls(deleteWalls());
		options.setDeleteTileFlags(deleteTileFlags());
		options.setDeleteTilesAbove(deleteTilesAbove());
		
		return options;
	}
	

	@Override
	public void stop() throws Exception {
		super.stop();
		this.stage.close();
	}

}
