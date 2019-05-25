package com.rspsi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class SwatchController {

	@FXML
	private FlowPane swatchFlowPane;

	@FXML
	private VBox vboxContainer;

	@FXML
	private Button saveBtn;

	@FXML
	private Button loadBtn;

	@FXML
	private BorderPane titleContainer;

	@FXML
	private BorderPane mainBorder;

	@FXML
	private Button objectViewBtn;

	public Button getLoadBtn() {
		return loadBtn;
	}

	public BorderPane getMainBorder() {
		return mainBorder;
	}

	public Button getObjectViewBtn() {
		return objectViewBtn;
	}

	public Button getSaveBtn() {
		return saveBtn;
	}

	public FlowPane getSwatchFlowPane() {
		return swatchFlowPane;
	}

	public BorderPane getTitleContainer() {
		return titleContainer;
	}

	public VBox getVboxContainer() {
		return vboxContainer;
	}

}
