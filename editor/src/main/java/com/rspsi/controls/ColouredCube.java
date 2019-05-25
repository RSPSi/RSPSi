package com.rspsi.controls;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class ColouredCube extends Group {
	public ColouredCube(double size, Color color, double shade) {

		this.setCursor(Cursor.CLOSED_HAND);
		ColouredCube cube = this;
		EventHandler<MouseEvent> mouseEventHandler = event -> {
			EventType<? extends MouseEvent> eventType = event.getEventType();
			if (eventType == MouseEvent.MOUSE_ENTERED && cube.getCursor() == Cursor.DEFAULT) {
				cube.setCursor(Cursor.OPEN_HAND);
			} else if (eventType == MouseEvent.MOUSE_EXITED) {
				cube.setCursor(Cursor.DEFAULT);
			} else if (eventType == MouseEvent.MOUSE_PRESSED) {
				cube.setCursor(Cursor.CLOSED_HAND);
			} else if (eventType == MouseEvent.MOUSE_RELEASED) {
				cube.setCursor(Cursor.OPEN_HAND);
			}
		};
		cube.setOnMouseClicked(mouseEventHandler);
		cube.setOnMouseReleased(mouseEventHandler);
		cube.setOnMousePressed(mouseEventHandler);
		cube.setOnMouseEntered(mouseEventHandler);

		{
			Rectangle r = new Rectangle();
			r.setWidth(size);
			r.setHeight(size);
			r.setFill(Color.BLUE);
			r.setTranslateX(-0.5 * size);
			r.setTranslateY(-0.5 * size);
			r.setTranslateZ(0.5 * size);
			getChildren().add(r);
		}

		{
			Rectangle r = new Rectangle();
			r.setWidth(size);
			r.setHeight(size);
			r.setFill(Color.RED);
			r.setTranslateX(-0.5 * size);
			r.setTranslateY(0);
			r.setRotationAxis(Rotate.X_AXIS);
			r.setRotate(90);
			getChildren().add(r);
		}

		{
			Rectangle r = new Rectangle();
			r.setWidth(size);
			r.setHeight(size);
			r.setFill(Color.YELLOW);
			r.setTranslateX(-1 * size);
			r.setTranslateY(-0.5 * size);
			r.setRotationAxis(Rotate.Y_AXIS);
			r.setRotate(90);
			getChildren().add(r);
		}

		{
			Rectangle r = new Rectangle();
			r.setWidth(size);
			r.setHeight(size);
			r.setFill(Color.GREEN);
			r.setTranslateX(0);
			r.setTranslateY(-0.5 * size);
			r.setRotationAxis(Rotate.Y_AXIS);
			r.setRotate(90);
			getChildren().add(r);
		}

		{
			Rectangle r = new Rectangle();
			r.setWidth(size);
			r.setHeight(size);
			r.setFill(Color.ORANGE);
			r.setTranslateX(-0.5 * size);
			r.setTranslateY(-1 * size);
			r.setRotationAxis(Rotate.X_AXIS);
			r.setRotate(90);
			getChildren().add(r);
		}

		{
			Rectangle r = new Rectangle();
			r.setWidth(size);
			r.setHeight(size);
			r.setFill(Color.LIGHTBLUE);
			r.setTranslateX(-0.5 * size);
			r.setTranslateY(-0.5 * size);
			r.setTranslateZ(-0.5 * size);
			getChildren().add(r);
		}

	}
}
