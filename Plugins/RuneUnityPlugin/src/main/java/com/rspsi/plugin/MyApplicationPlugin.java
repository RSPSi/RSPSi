package com.rspsi.plugin;

import com.rspsi.MainWindow;
import com.rspsi.controllers.MainController;
import com.rspsi.options.Options;
import com.rspsi.plugins.ApplicationPlugin;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;

public class MyApplicationPlugin implements ApplicationPlugin {

	@Override
	public void initialize(MainWindow window) {
		MainController controller = window.getController();
		Menu menu = new Menu("RuneUnity");
		CheckMenuItem hdMode = new CheckMenuItem("HD Textures [EXPERIMENTAL]");
		Options.hdTextures.bind(hdMode.selectedProperty());
		hdMode.setSelected(false);
		menu.getItems().add(hdMode);
		
		controller.getToolbar().getMenus().add(controller.getToolbar().getMenus().size() - 1, menu);
	
	}

}
