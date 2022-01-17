package com.rspsi.helper;

import com.rspsi.ui.misc.SGVConstants;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.geometry.BoundingBox;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Window;

public class MaximizeHelper {

	private SimpleBooleanProperty maximized = new SimpleBooleanProperty(false);
	

	private PseudoClass maximizedPseudoClass = PseudoClass.getPseudoClass("maximized");

	private BoundingBox originalBox;
	private BoundingBox maximizedBox;
	private Button button;
	private Node mainPane;
	
	public MaximizeHelper(Window stage) {
		mainPane = stage.getScene().lookup("#main-pane");
		
		originalBox = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
		maximized.addListener((observable, oldVal, newVal) -> {
			if(mainPane != null) {
				mainPane.pseudoClassStateChanged(maximizedPseudoClass, newVal);
			}
			if (!oldVal) {
				// store original bounds
				originalBox = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
				// get the max stage bounds
				Screen screen = Screen
						.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).get(0);
				Rectangle2D bounds = screen.getVisualBounds();
				maximizedBox = new BoundingBox(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(),
						bounds.getHeight());
				// maximized the stage
				stage.setX(maximizedBox.getMinX());
				stage.setY(maximizedBox.getMinY());
				stage.setWidth(maximizedBox.getWidth());
				stage.setHeight(maximizedBox.getHeight());
				stage.getScene().getProperties().put("maximized", true);
				if (button != null) {
					button.setGraphic(SGVConstants.getResizeMin());
				}
			} else {
				// restore stage to its original size
				stage.setX(originalBox.getMinX());
				stage.setY(originalBox.getMinY());
				stage.setWidth(originalBox.getWidth());
				stage.setHeight(originalBox.getHeight());
				originalBox = null;
				stage.getScene().getProperties().remove("maximized");
				if (button != null) {
					button.setGraphic(SGVConstants.getMaximize());
				}
			}

		});
	}

	public void addButton(Button button) {
		this.button = button;
		button.setGraphic(SGVConstants.getMaximize());
		button.setOnAction(evt -> {
			boolean newVal = !maximized.get();
			maximized.set(newVal);
		});
	}

	public boolean isMaximized() {
		return maximized.get();
	}

	public void setMaximized(boolean maximized) {
		this.maximized.set(maximized);
	}

}
