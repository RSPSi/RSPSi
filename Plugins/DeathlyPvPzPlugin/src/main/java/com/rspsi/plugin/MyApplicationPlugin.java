package com.rspsi.plugin;

import com.rspsi.ui.MainWindow;
import com.rspsi.controllers.MainController;
import com.rspsi.plugins.ui.ApplicationPlugin;

public class MyApplicationPlugin implements ApplicationPlugin {

	@Override
	public void initialize(MainWindow window) {
		MainController controller = window.getController();
		/*Menu menu = new Menu("Debug");

		CheckMenuItem minimap = new CheckMenuItem("Show MF debug models");
		Options.showMinimapFunctionModels.bind(minimap.selectedProperty());
		minimap.setSelected(false);
		menu.getItems().add(minimap);
		
		controller.getToolbar().getMenus().add(controller.getToolbar().getMenus().size() - 1, menu);*/
	
	}

}
