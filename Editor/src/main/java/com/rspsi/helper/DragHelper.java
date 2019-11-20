package com.rspsi.helper;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.stage.Window;

public class DragHelper {

	private double xOffset, yOffset;

	public DragHelper(Window primaryStage, Node node, MaximizeHelper maximizeHelper) {
		node.setOnMouseDragged(event -> {
			if (event.getClickCount() <= 1) {
				if (maximizeHelper != null && maximizeHelper.isMaximized()) {
					maximizeHelper.setMaximized(false);
					primaryStage.setX(event.getScreenX() - primaryStage.getWidth() / 2);
					primaryStage.setY(event.getScreenY() - 5);
					xOffset = primaryStage.getX() - event.getScreenX();
					yOffset = primaryStage.getY() - event.getScreenY();
				} else {
					primaryStage.setX(event.getScreenX() + xOffset);
					primaryStage.setY(event.getScreenY() + yOffset);
				}
			}

		});

		node.setOnMousePressed(event -> {
			if (maximizeHelper != null && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
				maximizeHelper.setMaximized(!maximizeHelper.isMaximized());

			} else {
				xOffset = primaryStage.getX() - event.getScreenX();
				yOffset = primaryStage.getY() - event.getScreenY();
			}
		});
	}

}
