package com.rspsi.ui;

import java.awt.*;
import java.io.File;

import com.jagex.Client;
import com.jagex.map.SceneGraph;
import com.rspsi.controls.WindowControls;
import com.rspsi.game.CanvasPane;
import com.rspsi.options.Options;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.util.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiRegionMapWindow extends Application {

	private Stage primaryStage;
	
	
    @FXML
    private BorderPane topBar;
    
    @FXML
    private HBox controlBox;

    @FXML
    private HBox dockContainer;

    @FXML
    private AnchorPane mapPane;

    @FXML
    private Button redrawImageBtn;
    
    @FXML
    private Button saveImageBtn;
    

    @FXML
    private Font x312;

    @FXML
    private Color x412;

    @FXML
    private Spinner<?> currentHeightSpinner;
    
    @FXML
    private CheckBox showBordersCheck;

    @FXML
    private CheckBox showCameraCheck;
    
    @FXML
    private CheckBox showFileCheck;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mapview.fxml"));
		loader.setController(this);
		Parent content = loader.load();
		Scene scene = new Scene(content);
		
		
		
		primaryStage.setTitle("RSPSi Multi Region Map");
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());
		
		primaryStage.setOnHiding(evt -> visible.set(false));
		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();
		
		WindowControls.addWindowControls(primaryStage, topBar, controlBox);
		
		redrawImageBtn.setOnAction(evt -> SceneGraph.minimapUpdate = true);
		Options.showBorders.bind(this.showBordersCheck.selectedProperty());
		Options.showCamera.bind(this.showCameraCheck.selectedProperty());
		Options.showMapFileNames.bind(this.showFileCheck.selectedProperty());
		ChangeListenerUtil.addListener(() -> SceneGraph.minimapUpdate = true, Options.showCamera, Options.showBorders, Options.showMapFileNames);
		saveImageBtn.setOnAction(evt -> {
			File f = RetentionFileChooser.showSaveDialog(FilterMode.PNG);
			if(f != null) {
				try {
					System.out.println(f.getAbsolutePath());
					Client.getSingleton().saveMapFullImage(f);
				} catch (Exception e) {
					e.printStackTrace();
					FXDialogs.showError(primaryStage,"Error while loading saving image", "There was a failure while attempting to save\nthe minimap to the selected file.");
					
				}
			}
		});
	}
	
	public void resizeMap() {
		Platform.runLater(() -> {
			Screen screen = Screen.getPrimary();
			primaryStage.setMaxWidth(screen.getBounds().getWidth() - 30);
			primaryStage.setMaxHeight(screen.getBounds().getHeight() - 100);
			mapPane.getChildren().clear();
			int mapHeight = (int) Client.getSingleton().fullMapCanvas.getHeight();
			CanvasPane canvasPane = new CanvasPane(Client.getSingleton().fullMapCanvas);
			mapPane.getChildren().add(canvasPane);
			ContextMenu rightClickMenu = new ContextMenu();
			MenuItem menuItem = new MenuItem("");
			rightClickMenu.getItems().add(menuItem);
			rightClickMenu.setAutoFix(true);
			rightClickMenu.setAutoHide(true);
			canvasPane.setOnContextMenuRequested(evt ->{
					int worldX = (int) evt.getX() / 4;
					int worldY = (int) (mapHeight - evt.getY()) / 4;
					menuItem.setText(String.format("Move camera to {%d, %d}", worldX, worldY));
					menuItem.setOnAction(menuEvnt -> Client.getSingleton().moveCamera(worldX, worldY));
					rightClickMenu.hide();
					rightClickMenu.show(primaryStage, evt.getScreenX(), evt.getScreenY());
			});
			primaryStage.sizeToScene();
		});
	}
    
	public void show() {
		visible.set(true);
		primaryStage.show();
		primaryStage.sizeToScene();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		this.primaryStage.close();
	}

	private SimpleBooleanProperty visible = new SimpleBooleanProperty();
	public SimpleBooleanProperty visibleProperty() {
		return visible;
	}


}
