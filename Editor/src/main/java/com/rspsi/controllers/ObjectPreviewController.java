package com.rspsi.controllers;

import com.rspsi.datasets.ObjectDataset;
import com.rspsi.ui.misc.NamedValueObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class ObjectPreviewController {

	@FXML
	private AnchorPane objectViewPane;

	@FXML
	private MenuItem quitMenuItem;

	@FXML
	private TableView<NamedValueObject> definitionTable;

	@FXML
	private TreeView<ObjectDataset> objectDefinitionList;

	@FXML
	private Button addSwatchBtn;

	@FXML
	private BorderPane topBar;

	@FXML
	private HBox controlBox;

	@FXML
	private AnchorPane leftBar;

	@FXML
	private AnchorPane rightBar;

    @FXML
    private TextField searchBox;

    @FXML
    private Button searchButton;
   

	public Button getAddSwatchBtn() {
		return addSwatchBtn;
	}

	public HBox getControlBox() {
		return controlBox;
	}

	public TableView<NamedValueObject> getDefinitionTable() {
		return definitionTable;
	}

	public AnchorPane getLeftBar() {
		return leftBar;
	}

	public TreeView<ObjectDataset> getObjectDefinitionList() {
		return objectDefinitionList;
	}

	public AnchorPane getObjectViewPane() {
		return objectViewPane;
	}

	public MenuItem getQuitMenuItem() {
		return quitMenuItem;
	}

	public AnchorPane getRightBar() {
		return rightBar;
	}

	public BorderPane getTopBar() {
		// TODO Auto-generated method stub
		return topBar;
	}

	public TextField getSearchBox() {
		return searchBox;
	}

	public Button getSearchButton() {
		return searchButton;
	}
	
	
}
