package com.rspsi.controls;

import java.awt.Color;
import java.io.IOException;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.jagex.Client;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.graphics.Sprite;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.draw.ImageGraphicsBuffer;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.model.Mesh;
import com.jagex.entity.model.MeshLoader;
import com.jagex.entity.model.PreviewModel;
import com.rspsi.datasets.ObjectDataset;
import com.rspsi.game.CanvasPane;
import com.rspsi.game.DisplayCanvas;
import com.rspsi.util.ChangeListenerUtil;
import com.rspsi.util.Threads;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GenericModelView extends VBox {
	
	private GameRasterizer modelRaster;

	private PreviewModel ballModel;
	private PreviewModel nullModel;
	private RotationControl rotationControl = new RotationControl();
	private DisplayCanvas modelCanvas;
	private PreviewModel model;
	private CanvasPane pane;
	private int zoom = 600;
	private Slider slider;

	private ImageGraphicsBuffer buffer;
	
	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public GenericModelView() {
		modelCanvas = new DisplayCanvas(300, 300);
		
		modelRaster = new GameRasterizer();
		
		modelRaster.setBrightness(0.6);
		modelRaster.setTextureBrightness(0.6);
		
		buffer = new ImageGraphicsBuffer(300, 300, modelRaster);
		buffer.initializeRasterizer();
		modelRaster.useViewport();
		
		ChangeListenerUtil.addListener(() -> renderModel(), rotationControl.getRotateY().angleProperty(), rotationControl.getRotateX().angleProperty(), rotationControl.getRotateZ().angleProperty());
		


		pane = new CanvasPane(modelCanvas);

		pane.addEventHandler(MouseEvent.MOUSE_PRESSED, rotationControl);
		pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, rotationControl);
		pane.addEventHandler(MouseEvent.MOUSE_RELEASED, rotationControl);

		VBox.setVgrow(pane, Priority.ALWAYS);
		this.getChildren().add(pane);
		BorderPane hbox = new BorderPane();
		hbox.setLeft(rotationControl);

		slider = new Slider();
		slider.setMajorTickUnit(10);
		slider.setMax(100.0);
		slider.setMin(0);
		slider.setShowTickMarks(true);
		slider.setPrefSize(200, 30);

		slider.valueProperty().addListener((observable, oldVal, newVal) -> {
			zoom = (int) (300 * (100 / (1 + newVal.doubleValue())));
			this.renderModel();
		});

		pane.setOnScroll(event -> {
			double zoomFactor = 1.05;
			double deltaY = event.getDeltaY();
			if (deltaY < 0) {
				zoomFactor = 2.0 - zoomFactor;
			}
			slider.adjustValue(slider.getValue() * zoomFactor);
		});

		slider.setValue(50.0);

		hbox.setCenter(slider);
		BorderPane.setAlignment(slider, Pos.CENTER);
		BorderPane.setAlignment(rotationControl, Pos.CENTER);
		this.getChildren().add(hbox);
		ChangeListenerUtil.addListener(() -> {
			resizeViews();
			renderModel();
		}, this.heightProperty(), this.widthProperty());
		
		ChangeListenerUtil.addListener(true, () -> {
			
			try {
				Mesh ball = MeshLoader.load(Resources.toByteArray(getClass().getResource("/misc/ball.dat")));
				ball.computeSphericalBounds();
				ball.rotateClockwise();
				ball.rotateClockwise();
				try {
					ball.light(64, 768 * 5, -50, -10, -50, true);
				} catch(Exception ex) {
					//hmm...
				}
				this.ballModel = new PreviewModel(ball);
				
				Mesh model = MeshLoader.load(Resources.toByteArray(getClass().getResource("/misc/404.dat")));
				model.computeSphericalBounds();
				model.rotateClockwise();
				model.rotateClockwise();
				try {
					model.light(64, 768 * 5, -50, -10, -50, true);
				} catch(Exception ex) {
					//hmm...
				}
				nullModel = new PreviewModel(model);
				this.model = nullModel;	
			} catch (IOException e) {
				e.printStackTrace();
			}
			

			renderModel();
		}, Client.gameLoaded);
	}


	public DisplayCanvas getModelCanvas() {
		// TODO Auto-generated method stub
		return modelCanvas;
	}

	public void prepareView(int modelId) {
	

		Runnable loadModel = () -> {
			while (!MeshLoader.getSingleton().loaded(modelId)) {
				Threads.sleep(50);
			}
			Mesh m = MeshLoader.getSingleton().lookup(modelId);
			if (m == null) {
				model = nullModel;
				renderModel();
				return;
			}
			model = new PreviewModel(m);

			model.light(64, 768, -50, -10, -50, true);
			renderModel();
		};
		loadModel.run();

	}

	public void renderModel() {
		if (this.getWidth() != modelCanvas.getWidth() || this.getHeight() != modelCanvas.getHeight()) {
			resizeViews();
		}
		buffer.clearPixels(Color.BLACK.getRGB());

		if (model == null) {
			model = this.nullModel;
			return;
		}
		int roll = (int) Math.toDegrees(Math.toRadians(rotationControl.getRotateX().getAngle()) * 5.65) & 0x7ff;
		int yaw = (int) Math.toDegrees(Math.toRadians(rotationControl.getRotateY().getAngle()) * 5.65) & 0x7ff;
		int pitch = (int) Math.toDegrees(Math.toRadians(rotationControl.getRotateZ().getAngle()) * 5.65) & 0x7ff;
		model.computeSphericalBounds();
	//	model.prepareSkeleton();
		model.render(modelRaster, roll, yaw, pitch, 0, 0, 0, zoom, 0);

		buffer.finalize();

		modelCanvas.drawImage(buffer.getFXImage(), 0, 0);
	}

	private void resizeViews() {
		if (this.getWidth() <= 0 || this.getHeight() <= 0)
			return;
		pane.setPrefHeight(this.getHeight());
		pane.setPrefWidth(this.getWidth());
		modelCanvas.resize(this.getWidth(), this.getHeight());
		buffer = new ImageGraphicsBuffer((int) this.getWidth(), (int) this.getHeight(), modelRaster);
		buffer.initializeRasterizer();
		modelRaster.useViewport();
	}


}
