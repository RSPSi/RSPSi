package com.rspsi.controls;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class RotationControl extends AnchorPane implements EventHandler<MouseEvent> {

	public ColouredCube rotationCube;

	private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
	private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
	private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
	private final double CUBE_SIZE = 3;
	private double mousePosX, mousePosY = 0;

	public RotationControl() {
		this.getChildren().add(createContent());
		this.addMouseEvents();
	}

	public Parent createContent() {
		rotationCube = new ColouredCube(CUBE_SIZE, Color.RED, 0.3);
		rotationCube.getTransforms().addAll(rotateZ, rotateY, rotateX);

		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.getTransforms().add(new Translate(0, 0, -20));

		Group root = new Group();
		root.getChildren().add(camera);
		root.getChildren().add(rotationCube);

		SubScene subScene = new SubScene(root, 50, 50, true, SceneAntialiasing.BALANCED);
		subScene.setFill(Color.TRANSPARENT);
		subScene.setCamera(camera);

		return new Group(subScene);
	}

	public Rotate getRotateX() {
		return rotateX;
	}

	public Rotate getRotateY() {
		return rotateY;
	}

	public Rotate getRotateZ() {
		return rotateZ;
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			mousePosX = event.getSceneX();
			mousePosY = event.getSceneY();
		} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			mousePosX = event.getSceneX();
			mousePosY = event.getSceneY();
		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			double dx = mousePosX - event.getSceneX();
			double dy = mousePosY - event.getSceneY();
			double xRot = rotateX.getAngle() + dy / CUBE_SIZE * 360 * (Math.PI / 180);
			double yRot = rotateY.getAngle() + dx / CUBE_SIZE * -360 * (Math.PI / 180);
			
			rotateX.setAngle(xRot);
			rotateY.setAngle(yRot);
			
			mousePosX = event.getSceneX();
			mousePosY = event.getSceneY();
		}
	}

	private void addMouseEvents() {
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
	}

}
