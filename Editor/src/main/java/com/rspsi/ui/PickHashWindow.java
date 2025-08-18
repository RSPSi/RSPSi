package com.rspsi.ui;

import com.google.common.primitives.Ints;
import com.rspsi.resources.ResourceLoader;

import com.rspsi.util.FXUtils;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PickHashWindow extends Application {

	private Stage stage;
	
	boolean okClicked;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/hash.fxml"));
	
		loader.setController(this);
		Parent content = loader.load();
		Scene scene = new Scene(content);
		
		
		
		primaryStage.setTitle("Please enter region hash");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());

		primaryStage.setAlwaysOnTop(true);

		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();
	
		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if(event.getCode() == KeyCode.ENTER) {
				primaryStage.hide();
				okClicked = true;
			}
		});

		widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1, 1));
		lengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1, 1));

		FXUtils.addSpinnerFocusListeners(widthSpinner, lengthSpinner);

		okButton.setOnAction(evt -> {
			primaryStage.hide();
			okClicked = true;
		});
		cancelButton.setOnAction(evt -> {
			reset();
			primaryStage.hide();
		});
	}
	
	public void show() {
		reset();
		hashText.requestFocus();
		stage.sizeToScene();
		stage.showAndWait();
		if(!okClicked)
			reset();
	}
	
	public int getHash() {
		return Ints.tryParse(hashText.getText());
	}
	
	public boolean valid() {
		return !hashText.getText().isEmpty() && okClicked;
	}
	
	public void reset() {
		okClicked = false;
	}
	

	@FXML
    private TextField hashText;


    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;
    
    @FXML
    private Spinner<Integer> widthSpinner;

    @FXML
    private Spinner<Integer> lengthSpinner;

	public int getWidth() {
		// TODO Auto-generated method stub
		return widthSpinner.valueProperty().get();
	}

	public int getLength() {
		// TODO Auto-generated method stub
		return lengthSpinner.valueProperty().get();
	}

}
