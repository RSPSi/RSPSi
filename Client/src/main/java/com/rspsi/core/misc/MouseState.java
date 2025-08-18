package com.rspsi.core.misc;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import javafx.beans.property.SimpleBooleanProperty;

public class MouseState {

	private Vector2D mousePosition, mouseClickStart, mouseClickEnd;

	private SimpleBooleanProperty mouseDown = new SimpleBooleanProperty(false);

	public Vector2D getMousePosition() {
		return mousePosition;
	}

	public void setMousePosition(Vector2D mousePosition) {
		this.mousePosition = mousePosition;
	}

	public Vector2D getMouseClickStart() {
		return mouseClickStart;
	}

	public void setMouseClickStart(Vector2D mouseClickStart) {
		this.mouseClickStart = mouseClickStart;
	}

	public Vector2D getMouseClickEnd() {
		return mouseClickEnd;
	}

	public void setMouseClickEnd(Vector2D mouseClickEnd) {
		this.mouseClickEnd = mouseClickEnd;
	}

	public SimpleBooleanProperty getMouseDown() {
		return mouseDown;
	}
	
	public boolean isMouseDown() {
		return mouseDown.get();
	}


	public void setMouseDown(boolean mouseDown) {
		this.mouseDown.set(mouseDown);
	}
	
	
	
}
