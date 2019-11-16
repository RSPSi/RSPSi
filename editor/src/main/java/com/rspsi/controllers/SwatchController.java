package com.rspsi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

@Getter
public class SwatchController {

	@FXML
	private FlowPane swatchFlowPane;

	@FXML
	private ScrollPane swatchScrollPane;

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


}
