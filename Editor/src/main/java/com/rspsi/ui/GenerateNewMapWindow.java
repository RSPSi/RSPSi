package com.rspsi.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.rspsi.util.*;
import org.apache.commons.compress.utils.Lists;

import com.jagex.util.Constants;
import com.rspsi.controls.ConditionGridNode;
import com.rspsi.core.misc.TileCondition;
import com.rspsi.resources.ResourceLoader;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GenerateNewMapWindow extends Application {

	private Stage stage;

	boolean okClicked;

	private int[][] heights;
	
	private WritableImage blurredImage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newregion.fxml"));

		loader.setController(this);
		Parent content = loader.load();
		Scene scene = new Scene(content);



		primaryStage.setTitle("Please select region options");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());

		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();
		primaryStage.setAlwaysOnTop(true);


	

		widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
		lengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));


		FXUtils.addSpinnerFocusListeners(widthSpinner, lengthSpinner);

		waterDistanceMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 128, 0));
		waterDistanceMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 128, 0));

		heightsMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 800, 550));
		heightsMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 800, 120));

		setLinkedMinMax(waterDistanceMin, waterDistanceMax);
		setLinkedMinMax(heightsMin, heightsMax);


		okButton.setOnAction(evt -> {
			primaryStage.hide();
			okClicked = true;
		});
		cancelButton.setOnAction(evt -> {
			reset();
			primaryStage.hide();
		});
		addConditionBtn.setOnAction(evt -> {
			generateCondition();
		});
		
		browseBtn.setOnAction(evt -> {
			File f = RetentionFileChooser.showOpenDialog(primaryStage, FilterMode.PNG);
			if(f != null) {
				try {
					Image image = new Image(new FileInputStream(f));
					
					tileHeightImageView.setFitHeight(lengthSpinner.getValue() * 64);
					tileHeightImageView.setFitWidth(widthSpinner.getValue() * 64);
					tileHeightImageView.setImage(image);
					ColorAdjust desaturate = new ColorAdjust();
			        desaturate.setSaturation(-1);
					tileHeightImageView.setEffect(desaturate);
					primaryStage.sizeToScene();
					Platform.runLater(() -> {
						blurredImage = tileHeightImageView.snapshot(new SnapshotParameters(), null);
						
						setWaterEdges();
					});
				} catch (Exception e) {
					FXDialogs.showError(primaryStage,"Error while loading image", "There was an error while attempting to load the selected image.");
				}
			}
		});
		generateBtn.setOnAction(evt -> {
			int width = widthSpinner.getValue() * 64;
			int height = lengthSpinner.getValue() * 64;
			int randomX = (int) (Math.random() * 100000);

			WritableImage image = new WritableImage(width, height);
			//float[][] smoothNoise = SimplexNoise.generateOctavedSimplexNoise(width, height,  3, 0.4f, 0.005f);//, 14, (long) Math.random() * Long.MAX_VALUE));
			for(int w = 0;w<width;w++) {
				for(int h = 0;h<height;h++) {
					float tileColor = calculateHeight(randomX + w, randomX + h) / (1.0f * heightsMax.getValue());
					Color color = new Color(tileColor, tileColor, tileColor, 1.0f);
					image.getPixelWriter().setColor(w, h, color);
					//heights[w][h] = (int) -((tileColor * heightsMax.getValue())  * 8);
				}
			}

			
				/*List<Point> points = Lists.newArrayList();
				for(int x = min;x<=width - min;x+=width / 8) {
					Point p = new Point(x, (height - min) - (r.nextInt(max - min)));
					points.add(p);

				}


				for(int y = height - min;y>=min;y-= width / 8) {
					Point p = new Point(width - min - (r.nextInt(max - min)), y);
					points.add(p);

				}


				for(int x = width - min;x>=min;x-=width / 8) {
					Point p = new Point(x, min + r.nextInt(max - min));
					points.add(p);

				}

				for(int y = min;y<=height - min;y+=height / 8) {
					Point p = new Point(min + r.nextInt(max - min), y);
					points.add(p);

				}
				System.out.println("Generated " + points.size());
				for(Point p : points) {
					canvas.getChildren().add();
				}*/

				/*for(int i = 0;i<points.size();i++) {
					Point p = points.get(i);
					Point p2 = i == points.size() - 1 ? points.get(0) : points.get(i + 1);

					if(p.x > 0 && p.x < width && p.y > 0 && p.y < height) {
						QuadCurve curve = new QuadCurve();
						curve.setStartX(p.getX());
						curve.setStartY(p.getY());
						curve.setEndX(p2.getX());
						curve.setEndY(p.getY());
						double controlX = Math.max(p.getX(), p2.getX()) - ((Math.max(p.getX(), p2.getX()) - Math.min(p.getX(), p2.getX())) / 2);
						double controlY = Math.max(p.getY(), p2.getY()) - ((Math.max(p.getY(), p2.getY()) - Math.min(p.getY(), p2.getY())) / 2);

						curve.setControlX(controlX);
						curve.setControlY(controlY);
						canvas.getChildren().add(curve);
					}
				}*/
			
			tileHeightImageView.setFitHeight(height);
			tileHeightImageView.setFitWidth(width);
			tileHeightImageView.setSmooth(true);
			tileHeightImageView.setImage(image);
			//tileHeightImageView.setEffect(gaussianBlur);
			
			primaryStage.sizeToScene();
			Platform.runLater(() -> {
				blurredImage = tileHeightImageView.snapshot(new SnapshotParameters(), null);
				setWaterEdges();
			});
		});


		primaryStage.sizeToScene();
	}
	
	private void setWaterEdges() {
		tileHeightImageView.setEffect(null);
		int waterMin = waterDistanceMin.getValue();
		int waterMax = waterDistanceMax.getValue();

		if(waterMin > 0 && waterMax > 0) {
			for(int w = 0;w<blurredImage.getWidth();w++) {
				for(int h = 0;h<blurredImage.getHeight();h++) {
					if(w <= waterMin || h <= waterMin || w >= blurredImage.getWidth() - waterMin || h >= blurredImage.getHeight() - waterMin) {
						blurredImage.getPixelWriter().setColor(w, h, Color.BLACK);
						//heights[w][h] = 0;
					}
				}
			}
			}

		tileHeightImageView.setImage(blurredImage);
	}
	

	private void setLinkedMinMax(Spinner<Integer> minSpinner, Spinner<Integer> maxSpinner) {

		minSpinner.setEditable(true);
		maxSpinner.setEditable(true);

		minSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				minSpinner.increment(0); // won't change value, but will commit editor
			}
		});
		maxSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				maxSpinner.increment(0); // won't change value, but will commit editor
			}
		});
	/*	minSpinner.getEditor().textProperty().addListener( (observable, oldValue, nv) ->
		{
			// let the user clear the field without complaining
			if(!nv.isEmpty()) {
				Integer newValue = minSpinner.getValue();
				try {
					newValue = minSpinner.getValueFactory().getConverter().fromString(nv);
				} catch (Exception e) {  user typed an illegal character  } 
				minSpinner.getValueFactory().setValue(newValue);
			}
		});
		maxSpinner.getEditor().textProperty().addListener( (observable, oldValue, nv) ->
		{
			// let the user clear the field without complaining
			if(!nv.isEmpty()) {
				Integer newValue = maxSpinner.getValue();
				try {
					newValue = maxSpinner.getValueFactory().getConverter().fromString(nv);
				} catch (Exception e) {  user typed an illegal character  } 
				maxSpinner.getValueFactory().setValue(newValue);
			}
		});*/
		ChangeListenerUtil.addListener(() -> {
			if(minSpinner.getValue() > maxSpinner.getValue()) {
				minSpinner.getValueFactory().setValue(maxSpinner.getValue());
			}
		}, minSpinner.getValueFactory().valueProperty());

		ChangeListenerUtil.addListener(() -> {
			if(maxSpinner.getValue() < minSpinner.getValue()) {
				maxSpinner.getValueFactory().setValue(minSpinner.getValue());
			}
		}, maxSpinner.getValueFactory().valueProperty());
	}

	private Optional<ConditionGridNode> generateCondition() {
		try {
			ConditionGridNode gridNode = new ConditionGridNode();
			this.overlayUnderlayBox.getChildren().add(gridNode);
			gridNode.getDeleteBtn().setOnAction(evt2 -> {
				overlayUnderlayBox.getChildren().remove(gridNode);
			});
			return Optional.ofNullable(gridNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Optional.empty();
	}
	public void setHeights() {
		Image image = this.tileHeightImageView.getImage();
		if(image != null) {
			
				heights = new int[((widthSpinner.getValue() * 64) + 1)][((lengthSpinner.getValue() * 64) + 1)];
				for(int w = 0;w<image.getWidth();w++) {
					for(int h = 0;h<image.getHeight();h++) {
						int tileColor = image.getPixelReader().getArgb(w, h);
					//	double avgColor = (tileColor.getRed() + tileColor.getBlue() + tileColor.getGreen()) / 3;
	
						int max = heightsMax.getValue();
						int min = heightsMin.getValue();
						//System.out.println(avgColor);
						//System.out.println(-avgColor * max);
						heights[w][h] = (int) -( min + (((tileColor & 0xff) / 255.0) * (max - min)));
					}
				}
			
		} else {
			heights = new int[(widthSpinner.getValue() * 64) + 1][(lengthSpinner.getValue() * 64) + 1];
			for(int x = 0;x<heights.length;x++)
				Arrays.fill(heights[x], -10);
		}
	}

	private Polygon createRegularPolygon() {
		Polygon polygon = new Polygon();  

		//Adding coordinates to the polygon 
		polygon.getPoints().addAll(20.0, 5.0,
				40.0, 5.0,
				45.0, 15.0,
				40.0, 25.0,
				20.0, 25.0,
				15.0, 15.0);
		return polygon;
	}

	private int calculateHeight(int x, int y) {
		int height = interpolatedNoise(x + 45365, y + 0x16713, 4) - 128
				+ (interpolatedNoise(x + 10294, y + 37821, 2) - 128 >> 1) + (interpolatedNoise(x, y, 1) - 128 >> 2);
		height = (int) (height * 0.3D) + 35;
		height *= 8;

		int min = heightsMin.getValue();
		int max = heightsMax.getValue();
		if (height < min) {
			height = min;
		} else if (height > max) {
			height = max;
		}

		return height;
	}

	private int interpolate(int a, int b, int angle, int frequencyReciprocal) {
		int cosine = 0x10000 - Constants.COSINE[angle * 1024 / frequencyReciprocal] >> 1;
		return (a * (0x10000 - cosine) >> 16) + (b * cosine >> 16);
	}

	private int interpolatedNoise(int x, int y, int frequencyReciprocal) {
		int adj_x = x / frequencyReciprocal;
		int i1 = x & frequencyReciprocal - 1;
		int adj_y = y / frequencyReciprocal;
		int k1 = y & frequencyReciprocal - 1;
		int l1 = smoothNoise(adj_x, adj_y);
		int i2 = smoothNoise(adj_x + 1, adj_y);
		int j2 = smoothNoise(adj_x, adj_y + 1);
		int k2 = smoothNoise(adj_x + 1, adj_y + 1);
		int l2 = interpolate(l1, i2, i1, frequencyReciprocal);
		int i3 = interpolate(j2, k2, i1, frequencyReciprocal);
		return interpolate(l2, i3, k1, frequencyReciprocal);
	}

	private int perlinNoise(int x, int y) {
		int n = x + y * 57;
		n = n << 13 ^ n;
		n = n * (n * n * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
		return n >> 19 & 0xff;
	}

	private int smoothNoise(int x, int y) {
		int corners = perlinNoise(x - 1, y - 1) + perlinNoise(x + 1, y - 1) + perlinNoise(x - 1, y + 1)
		+ perlinNoise(x + 1, y + 1);
		int sides = perlinNoise(x - 1, y) + perlinNoise(x + 1, y) + perlinNoise(x, y - 1) + perlinNoise(x, y + 1);
		int center = perlinNoise(x, y);
		return corners / 16 + sides / 8 + center / 4;
	}

	public void show() {
		reset();
		stage.sizeToScene();
		stage.showAndWait();
		if(!okClicked)
			reset();
	}

	public List<TileCondition> buildTileConditions(){
		List<TileCondition> conditions = Lists.newArrayList();
		for(Node n : this.overlayUnderlayBox.getChildren()) {
			if(n instanceof ConditionGridNode) {
				ConditionGridNode grid = (ConditionGridNode) n;
				conditions.add(new TileCondition(grid.getRequiredValue().getValue(), grid.getGridCondition().getSelectionModel().getSelectedItem(), grid.getOverlay(), grid.getUnderlay()));
			}
		}
		return conditions;
	}

	public int[][] getHeights() {
		//int[][] heights = new int[(widthSpinner.getValue() * 64) + 1][(lengthSpinner.getValue() * 64) + 1];
		if(heights == null) {
			setHeights();
		}/* else {
			return heights;
		}
		if(tileHeightImageView.getImage() != null) {
			Image image = tileHeightImageView.getImage();
			for(int x = 0;x<image.getWidth();x++) {
				for(int y = 0;y<image.getHeight();y++) {
					heights[x][y] = (int) (image.getPixelReader().getColor(x, y).getBlue() * 60);
				}
			}
		} else {

		}*/
		return heights;
	}


	public void reset() {
		okClicked = false;
		heights = null;
		//this.tileHeightImageView.setImage(null);
	}



	@FXML
	private Pane overlayPane;

	@FXML
	private Pane underlayPane;

	@FXML
	private TitledPane mapTileHeightBox;

	@FXML
	private ImageView tileHeightImageView;

	@FXML
	private Button generateBtn;

	@FXML
	private Button browseBtn;

	@FXML
	private Button okButton;

	@FXML
	private Button addConditionBtn;

	@FXML
	private Button cancelButton;

	@FXML
	private Spinner<Integer> widthSpinner;

	@FXML
	private Spinner<Integer> lengthSpinner;

	@FXML
	private Spinner<Integer> waterDistanceMin;

	@FXML
	private Spinner<Integer> waterDistanceMax;

	@FXML
	private Spinner<Integer> heightsMin;

	@FXML
	private Spinner<Integer> heightsMax;

	@FXML
	private VBox overlayUnderlayBox;

	public int getWidth() {
		// TODO Auto-generated method stub
		return widthSpinner.valueProperty().get();
	}

	public int getLength() {
		// TODO Auto-generated method stub
		return lengthSpinner.valueProperty().get();
	}
}
