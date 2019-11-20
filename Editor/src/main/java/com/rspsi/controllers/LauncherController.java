package com.rspsi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class LauncherController {

    @FXML
    private BorderPane topBar;

    @FXML
    private HBox controlBox;

    @FXML
    private ComboBox<String> cacheLocation;

    @FXML
    private Button browseButton;

    @FXML
    private ListView<String> enabledPlugins;
    
    @FXML
    private Button disablePluginButton;

    @FXML
    private Button enablePluginButton;

    @FXML
    private ListView<String> disabledPlugins;

    @FXML
    private Button launchButton;

    @FXML
    private Button cancelButton;
    
    @FXML
    private TitledPane pluginTitlePane;

	public BorderPane getTopBar() {
		return topBar;
	}

	public HBox getControlBox() {
		return controlBox;
	}

	public ComboBox<String> getCacheLocation() {
		return cacheLocation;
	}

	public Button getBrowseButton() {
		return browseButton;
	}

	public ListView<String> getEnabledPlugins() {
		return enabledPlugins;
	}

	public Button getDisablePluginButton() {
		return disablePluginButton;
	}

	public Button getEnablePluginButton() {
		return enablePluginButton;
	}

	public ListView<String> getDisabledPlugins() {
		return disabledPlugins;
	}

	public Button getLaunchButton() {
		return launchButton;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public TitledPane getPluginTitlePane() {
		return pluginTitlePane;
	}
    
    

}
