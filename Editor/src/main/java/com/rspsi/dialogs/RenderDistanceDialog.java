package com.rspsi.dialogs;

import com.google.common.primitives.Ints;
import com.jagex.Client;
import com.jfoenix.controls.JFXSlider;
import com.rspsi.options.Options;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.util.Settings;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RenderDistanceDialog extends Application {

	private Stage stage;

	boolean okClicked;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/renderdist.fxml"));

		loader.setController(this);
		Parent content = loader.load();
		Scene scene = new Scene(content);



		primaryStage.setTitle("Use slider to select render radius");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());

		primaryStage.setAlwaysOnTop(true);


		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if(event.getCode() == KeyCode.ENTER) {
				primaryStage.hide();
				okClicked = true;
			}
		});

		renderSlider.valueProperty().addListener((observable, oldValue, newValue) -> renderLabel.setText(newValue.intValue() + ""));
		renderSlider.setValue(Options.renderDistance.get());
		okButton.setOnAction(evt -> {
			primaryStage.hide();
			Options.renderDistance.set((int) renderSlider.getValue());
			Settings.putSetting("renderDistance", Options.renderDistance.get());
			if(Client.getSingleton().sceneGraph != null){
				Client.getSingleton().sceneGraph.setRenderDistance();
			}
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
		stage.showAndWait();
		if(!okClicked)
			reset();
	}
	public void reset() {
		okClicked = false;
		renderSlider.setValue(Options.renderDistance.get());
	}

	@FXML
	private Slider renderSlider;

	@FXML
	private Label renderLabel;

	@FXML
	private Button okButton;

	@FXML
	private Button cancelButton;

}
