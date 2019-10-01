package com.rspsi.plugin;

import com.rspsi.MainWindow;
import com.rspsi.plugins.ApplicationPlugin;

public class MyApplicationPlugin implements ApplicationPlugin {

	@Override
	public void initialize(MainWindow window) {
		//Loads before the game, allows for menus to be built etc.
	}

}
