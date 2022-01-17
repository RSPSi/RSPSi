package com.rspsi.ui;

import java.io.File;
import java.util.function.Consumer;

import com.rspsi.resources.ResourceLoader;
import com.rspsi.util.FXUtils;
import com.rspsi.util.FilterMode;
import com.rspsi.util.RetentionFileChooser;

import javafx.application.Application;
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

public class SelectPackWindow extends Application {

	private Stage stage;
	private boolean okClicked;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/selectpackfile.fxml"));
	
		loader.setController(this);
		Parent content = loader.load();
		Scene scene = new Scene(content);
		
		
		
		primaryStage.setTitle("Please select file to load");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());

		primaryStage.centerOnScreen();
		FXUtils.centerStage(primaryStage);
		primaryStage.setAlwaysOnTop(true);
		
		Consumer<TextField> finishBrowse = textField -> {
			File f = RetentionFileChooser.showOpenDialog(stage, FilterMode.PACK);
			if(f != null && f.exists()) {
				textField.setText(f.getAbsolutePath());
			}
		};
		
		landscapeBrowse.setOnAction(evt -> finishBrowse.accept(landscapeText));
		
	
		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if(event.getCode() == KeyCode.ENTER) {
				primaryStage.hide();
				okClicked = true;
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
	}
	
	public void show() {
		reset();
		stage.sizeToScene();
		okButton.requestFocus();
		stage.showAndWait();
		if(!okClicked)
			reset();
	}
	
	public String getPackText() {
		return landscapeText.getText();
	}
	

	public void reset() {
		okClicked = false;
	}
	
	public boolean valid() {
		return okClicked && !landscapeText.getText().isEmpty();
	}

    @FXML
    private TextField landscapeText;

    @FXML
    private Button landscapeBrowse;



    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;
}
