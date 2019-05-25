package com.rspsi.game;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class CanvasPane extends Pane {

	private Canvas canvas;

	public CanvasPane(Canvas canvas) {
		this.canvas = canvas;
		this.setFocusTraversable(true);
		this.getChildren().add(canvas);
		AnchorPane.setTopAnchor(this, 0.0);
		AnchorPane.setBottomAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 0.0);
		AnchorPane.setRightAnchor(this, 0.0);
	}

	@Override
	protected void layoutChildren() {
		final int top = (int) snappedTopInset();
		final int right = (int) snappedRightInset();
		final int bottom = (int) snappedBottomInset();
		final int left = (int) snappedLeftInset();
		final int w = (int) getWidth() - left - right;
		final int h = (int) getHeight() - top - bottom;
		canvas.setLayoutX(left);
		canvas.setLayoutY(top);
		if (w != canvas.getWidth() || h != canvas.getHeight()) {
			canvas.setWidth(w);
			canvas.setHeight(h);
		}
	}
}
