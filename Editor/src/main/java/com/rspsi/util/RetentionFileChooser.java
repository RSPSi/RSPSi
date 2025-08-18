package com.rspsi.util;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class RetentionFileChooser {

	private static FileChooser instance = null;
	private static DirectoryChooser directoryInstance = null;
	private static SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();
	

	private static DirectoryChooser getDirectoryInstance() {
		if (directoryInstance == null) {
			directoryInstance = new DirectoryChooser();
			directoryInstance.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
		}
		
		return directoryInstance;
	}

	private static FileChooser getInstance(FilterMode... filterModes) {
		if (instance == null) {
			instance = new FileChooser();
			instance.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
		}
		// Set the filters to those provided
		// You could add check's to ensure that a default filter is included, adding it
		// if need be
		instance.getExtensionFilters()
				.setAll(Arrays.stream(filterModes).map(FilterMode::getExtensionFilter).collect(Collectors.toList()));
		return instance;
	}

	public static File showOpenDialog(FilterMode... filterModes) {
		return showOpenDialog(null, filterModes);
	}

	public static File showOpenDialog(String title, Window ownerWindow, FilterMode... filterModes) {
		getInstance(filterModes).setTitle(title);
		File chosenFile = getInstance(filterModes).showOpenDialog(ownerWindow);
		if (chosenFile != null) {
			lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
		}
		return chosenFile;
	}
	
	public static File showOpenFolderDialog(Window ownerWindow, File initialDirectory) {
		if(initialDirectory != null) {
			getDirectoryInstance().setInitialDirectory(initialDirectory);
		}
		File chosenFile = getDirectoryInstance().showDialog(ownerWindow);
		if (chosenFile != null) {
			lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
		}
		return chosenFile;
	}

	public static File showOpenDialog(Window ownerWindow, FilterMode... filterModes) {
		File chosenFile = getInstance(filterModes).showOpenDialog(ownerWindow);
		if (chosenFile != null) {
			lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
		}
		return chosenFile;
	}
	
	public static File showOpenDialog(Window ownerWindow, String defaultLoc, FilterMode... filterModes) {
		if(!defaultLoc.isEmpty()) {
			File parentDir = new File(defaultLoc).getParentFile();
			if (parentDir != null && parentDir.isDirectory()) {
				getInstance(filterModes).setInitialDirectory(parentDir);
			}
		}
		File chosenFile = getInstance(filterModes).showOpenDialog(ownerWindow);
		if (chosenFile != null) {
			lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
		}
		return chosenFile;
	}

	public static File showSaveDialog(FilterMode... filterModes) {
		return showSaveDialog(null, filterModes);
	}

	public static File showSaveDialog(String title, Window ownerWindow, FilterMode... filterModes) {
		getInstance(filterModes).setTitle(title);
		File chosenFile = getInstance(filterModes).showSaveDialog(ownerWindow);
		if (chosenFile != null) {
			lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
		}
		return chosenFile;
	}
	
	public static File showSaveDialog(String title, Window ownerWindow, String defaultFileName, FilterMode... filterModes) {
		getInstance(filterModes).setTitle(title);
		getInstance(filterModes).setInitialFileName(defaultFileName);
		File chosenFile = getInstance(filterModes).showSaveDialog(ownerWindow);
		if (chosenFile != null) {
			lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
		}

		getInstance(filterModes).setInitialFileName(null);
		return chosenFile;
	}

	public static File showSaveDialog(Window ownerWindow, FilterMode... filterModes) {
		File chosenFile = getInstance(filterModes).showSaveDialog(ownerWindow);
		if (chosenFile != null) {
			lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
		}
		return chosenFile;
	}

	private RetentionFileChooser() {
	}
}