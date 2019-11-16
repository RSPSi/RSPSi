package com.rspsi.util;

import javafx.stage.FileChooser.ExtensionFilter;

public enum FilterMode {
	// Setup supported filters
	JMAP("JMAP files (*.jmap)", "*.jmap"), 
	PACK("PACK files (*.pack)", "*.pack"), 
	DAT("dat files (*.dat)", "*.dat"), 
	GZIP("gzip files (*.gz)", "*.gz"),
	SWATCH ("Swatch files (*.jswatch)" , "*.jswatch"), 
	PNG("Image files (*.png)", "*.png"),
	JSON("JSON Files (*.json)", "*.json"),
	NONE(" (*.)", "*."),
	
	;

	private ExtensionFilter extensionFilter;

	FilterMode(String extensionDisplayName, String... extensions) {
		extensionFilter = new ExtensionFilter(extensionDisplayName, extensions);
	}

	public ExtensionFilter getExtensionFilter() {
		return extensionFilter;
	}
}