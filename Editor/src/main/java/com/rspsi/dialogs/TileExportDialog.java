package com.rspsi.dialogs;

import java.io.File;
import java.io.IOException;

import com.google.common.collect.Lists;
import com.jagex.Client;
import com.jagex.chunk.Chunk;
import com.rspsi.controls.WindowControls;
import com.rspsi.core.misc.ExportOptions;
import com.rspsi.util.FXDialogs;
import com.rspsi.util.FXUtils;
import com.rspsi.util.FilterMode;
import com.rspsi.util.RetentionFileChooser;

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

public class TileExportDialog extends Application {

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
	private CheckBox exportOverlays;

	@FXML
	private CheckBox exportUnderlays;

	@FXML
	private CheckBox exportTileHeights;

	@FXML
	private CheckBox exportGameObjects;

	@FXML
	private CheckBox exportWalls;

	@FXML
	private CheckBox exportWallDecorations;

	@FXML
	private CheckBox exportGroundDecorations;

	@FXML
	private CheckBox exportTileFlags;

	@FXML
	private CheckBox exportTilesAbove;

	@FXML
	private Button saveButton;

	public boolean exportGameObjects() {
		return exportGameObjects.selectedProperty().get();
	}

	public boolean exportGroundDecorations() {
		return exportGroundDecorations.selectedProperty().get();
	}

	public boolean exportOverlays() {
		return exportOverlays.selectedProperty().get();
	}

	public boolean exportTileFlags() {
		return exportTileFlags.selectedProperty().get();
	}

	public boolean exportTileHeights() {
		return exportTileHeights.selectedProperty().get();
	}

	public boolean exportTilesAbove() {
		return exportTilesAbove.selectedProperty().get();
	}

	public boolean exportUnderlays() {
		return exportUnderlays.selectedProperty().get();
	}

	public boolean exportWallDecorations() {
		return exportWallDecorations.selectedProperty().get();
	}

	public boolean exportWalls() {
		return exportWalls.selectedProperty().get();
	}

	public void show() {
		stage.show();
	}
	
	
	public ExportOptions generateOptions() {
		ExportOptions options = new ExportOptions();
		
		options.setExportGameObjects(this.exportGameObjects());
		options.setExportGroundDecorations(exportGroundDecorations());
		options.setExportOverlays(exportOverlays());
		options.setExportTileFlags(exportTileFlags());
		options.setExportTileHeights(exportTileHeights());
		options.setExportTilesAbove(exportTilesAbove());
		options.setExportUnderlays(exportUnderlays());
		options.setExportWallDecorations(exportWallDecorations());
		options.setExportWalls(exportWalls());
		
		return options;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/exporttile.fxml"));
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
			for (Chunk chunk : Lists.newArrayList(Client.getSingleton().chunks)) {
				File f = RetentionFileChooser.showSaveDialog(primaryStage, FilterMode.JMAP);
				if (f != null) {
					try {
						Client.getSingleton().sceneGraph.exportSelectedTiles(generateOptions(), f);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						FXDialogs.showError(primaryStage, "Error while saving prefab!",
								"There was an error while saving the selected file.");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						FXDialogs.showError(primaryStage, "Error while allocating prefab!",
								"There was an error while allocating to the selected file.");
					}

				}
			}
		});
	}
	

	@Override
	public void stop() throws Exception {
		super.stop();
		this.stage.close();
	}

}
