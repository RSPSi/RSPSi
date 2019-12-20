package com.rspsi.controls;

import java.awt.Color;
import java.io.IOException;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.jagex.Client;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.def.RSArea;
import com.jagex.cache.graphics.Sprite;
import com.jagex.cache.loader.config.RSAreaLoader;
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
import lombok.Getter;
import lombok.Setter;

public class ObjectModelView extends VBox {
	
	private GameRasterizer modelRaster;

	private PreviewModel ballModel;
	private PreviewModel nullModel;

	@Getter
	private int zoom = 600;
	@Getter
	private RotationControl rotationControl = new RotationControl();

	private DisplayCanvas modelCanvas;
    @Setter
    @Getter
	private PreviewModel model;
	private Sprite sprite;
	private CanvasPane pane;

	private ObjectDataset currentSelection;
	private Slider slider;

	private ImageGraphicsBuffer buffer;

	public ObjectModelView() {
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
			Mesh model;
			try {
				model = MeshLoader.load(Resources.toByteArray(getClass().getResource("/misc/404.dat")));
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

	public ObjectDataset getCurrentSelection() {
		return currentSelection;
	}

	public DisplayCanvas getModelCanvas() {
		// TODO Auto-generated method stub
		return modelCanvas;
	}
	
	int modelTries = 0;

	public void prepareView(ObjectDataset cell) {
		currentSelection = cell;
		if(cell.getZoom() != -1) {
			slider.setValue(cell.getZoom());
		}
		ObjectDefinition origDef = ObjectDefinitionLoader.lookup(cell.getId());
		ObjectDefinition morphed = origDef.getMorphisms() != null ? ObjectDefinitionLoader.getMorphism(cell.getId()) : null;
		int type = cell.getType();
		if(type == 11)
			type = 10;
		final ObjectDefinition def = morphed != null ? morphed : origDef;
		//Runnable loadModel = () -> {
		try {
			while (!def.readyOrThrow(type)) {
				Threads.sleep(5);
			}
			Mesh m = def.modelAt(cell.getType() == 11 ? 10 : type, 1, 0, 0, 0, 0, -1);

			if (m == null || def.getModelIds()[0] == 111 && (def.getAreaId() != -1 || def.getMinimapFunction() != -1)) {
				if(def.getAreaId() != -1){
					RSArea area = RSAreaLoader.get(def.getAreaId());
					int func = area.getSpriteId();
					sprite = Client.getSingleton().getCache().getSprite(func);
				} else if(def.getMinimapFunction() != -1) {
					sprite =  Client.mapFunctions[def.getMinimapFunction()];
				} else {
					model = nullModel;
					sprite = null;
				}
				renderModel();
				return;
			}
			model = new PreviewModel(m);
			sprite = null;
			renderModel();
		} catch (Exception e) {
			e.printStackTrace();
			model = nullModel;
			sprite = null;
			renderModel();
		}
		//};
		//loadModel.run();

	}

	public void renderModel() {
		if (this.getWidth() != modelCanvas.getWidth() || this.getHeight() != modelCanvas.getHeight()) {
			resizeViews();
		}
		buffer.clearPixels(Color.BLACK.getRGB());
		if(sprite != null) {
			int xPos = (int) (this.getWidth() / 2);
			int yPos = (int) (this.getHeight() / 2);
			double zoom = this.zoom / 100.0;
			xPos -= (sprite.getWidth() * zoom) / 2;
			yPos -= (sprite.getHeight() * zoom) / 2;
			sprite.drawSprite(modelRaster, xPos, yPos, zoom);
			buffer.finalize();

			modelCanvas.drawImage(buffer.getFXImage(), 0, 0);
			return;
		}
		if (model == null) {
			model = this.nullModel;
			return;
		}
		int roll = (int) Math.toDegrees(Math.toRadians(rotationControl.getRotateX().getAngle()) * 5.65) & 0x7ff;
		int yaw = (int) Math.toDegrees(Math.toRadians(rotationControl.getRotateY().getAngle()) * 5.65) & 0x7ff;
		int pitch = (int) Math.toDegrees(Math.toRadians(rotationControl.getRotateZ().getAngle()) * 5.65) & 0x7ff;
		model.computeSphericalBounds();
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

	public void setZoom(int zoom){
	    this.zoom = zoom;
	    double adjustedZoom = zoom / 30;//300 * (100 / (1 + newVal.doubleValue())));
	    this.slider.setValue(adjustedZoom);
    }
}
