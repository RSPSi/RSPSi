package com.rspsi.util;

import javafx.scene.control.ToggleGroup;

public class AlwaysSelectToggleGroup {
	
	public static void setup(ToggleGroup group) {
		group.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
			if (newVal == null) {
				if (group.getProperties().get("deselect") == null) {
					oldVal.setSelected(true);
				} else {
					group.getProperties().remove("deselect");
				}
			}
		});
	}

}
