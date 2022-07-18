package com.rspsi.ui;

import com.google.common.primitives.Ints;
import com.rspsi.game.map.RegionView;
import com.rspsi.resources.ResourceLoader;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EditRegionsWindow extends Application {

	private Stage stage;
	
	boolean okClicked;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editregions.fxml"));
	
		loader.setController(this);
		Parent content = loader.load();
		Scene scene = new Scene(content);
		
		
		
		primaryStage.setTitle("Editing region [NULL, NULL]");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());

		primaryStage.setAlwaysOnTop(true);
		
	
		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if(event.getCode() == KeyCode.ENTER) {
				primaryStage.hide();
				okClicked = true;
			} else if(event.getCode() == KeyCode.COMMA) {
				if(landscapeId.isFocused()) {
					objectsId.requestFocus();
					event.consume();
				}
			}
		});
		
	
		okButton.setOnAction(evt -> {
			primaryStage.hide();
			okClicked = true;
		});
		cancelButton.setOnAction(evt -> {
			reset();
			primaryStage.hide();
		});
		primaryStage.sizeToScene();
	}
	
	public void show(RegionView view) {
		reset();
		stage.setTitle("Editing region [" + view.getRegionX() + ", " + view.getRegionY() + "]");
		landscapeId.setText(Integer.toString(view.getLandscapeId()));
		objectsId.setText(Integer.toString(view.getObjectsId()));
		stage.sizeToScene();
		stage.showAndWait();
		if(!okClicked)
			reset();
	}
	
	public int getLandscapeId() {
		return Ints.tryParse(landscapeId.getText());
	}
	
	public int getObjectId() {
		return Ints.tryParse(objectsId.getText());
	}
	
	public boolean valid() {
		return !landscapeId.getText().isEmpty() && !objectsId.getText().isEmpty();
	}
	
	public void reset() {
		landscapeId.setText("");
		objectsId.setText("");
		okClicked = false;
	}
	
	private BooleanProperty completed = new SimpleBooleanProperty(false);
	
    public BooleanProperty getCompleted() {
		return completed;
	}

	@FXML
    private TextField landscapeId;

    @FXML
    private TextField objectsId;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

}
