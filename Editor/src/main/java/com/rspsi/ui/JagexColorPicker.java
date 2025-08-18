package com.rspsi.ui;

import com.jagex.util.ColourUtils;
import com.jfoenix.controls.JFXTextField;
import com.rspsi.controls.WindowControls;
import com.rspsi.resources.ResourceLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class JagexColorPicker extends Application {

	protected static Stage stage;

	boolean okClicked;

	protected static JagexColorPicker colorPickerInstance;

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		colorPickerInstance = this;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/colourpicker.fxml"));

		loader.setController(this);
		Parent content = loader.load();
		Scene scene = new Scene(content);


		primaryStage.setTitle("RSPSi ColourPicker");
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		scene.setFill(null);
		primaryStage.setScene(scene);

		WindowControls.addUtilityWindowControls(primaryStage, dragBar, controls);

		Platform.setImplicitExit(true);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());
		primaryStage.show();

		pickerBox.getValue();
		pickerBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if(oldValue != newValue) {
				hexText.setText(ColourUtils.colorToHex(newValue));
				jagHSL.setText(ColourUtils.rgbToHslStr(newValue.hashCode()));
				colourIndex.setText("" + ColourUtils.rgbToJagHsl(newValue.hashCode()));
			}
		});
		hexText.addEventHandler(KeyEvent.ANY, (keyEvent) -> {
			if(keyEvent.getCode() == KeyCode.ENTER)
				try {
					pickerBox.setValue(Color.web(hexText.getText()));
				} catch(Exception ex){
					hexText.setText(ColourUtils.colorToHex(pickerBox.getValue()));
				}
		});

		hexText.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue.booleanValue()){
				try {
					pickerBox.setValue(Color.web(hexText.getText()));
				} catch(Exception ex){
					hexText.setText(ColourUtils.colorToHex(pickerBox.getValue()));
				}
			}
		});

		EyeDropWindow eyeDropScreenshot = new EyeDropWindow();
		eyeDropScreenshot.start(new Stage());

		pickerButton.setOnAction(evt -> eyeDropScreenshot.show());

		primaryStage.setOnHiding(evt -> {
			Platform.runLater(() -> {
				try {
					eyeDropScreenshot.stop();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.exit(0);
			});
		});


		colorCanvas.getGraphicsContext2D().setTextBaseline(VPos.CENTER);
		colorCanvas.getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);



	}

	@FXML
	private AnchorPane dragBar;

	@FXML
	private HBox controls;

	@FXML
	private TextField hexText;

	@FXML
	protected ColorPicker pickerBox;

	@FXML
	private Button pickerButton;

	@FXML
	private JFXTextField jagHSL;

	@FXML
	private JFXTextField colourIndex;

	@FXML
	protected Canvas colorCanvas;


	public static void main(String[] args){
		Application.launch(args);
	}
}

@Slf4j
class EyeDropWindow extends Application {

	private Stage pStage;

	private Robot robot;
	@Override
	public void start(Stage primaryStage) throws Exception {
		robot = new Robot();
		pStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/whatthefuck.fxml"));

		loader.setController(this);
		Canvas content = loader.load();
		content.setPickOnBounds(true);
		content.setCursor(Cursor.CROSSHAIR);
		Scene scene = new Scene(new Pane(content));
		scene.setFill(null);

		scene.setCursor(Cursor.CROSSHAIR);
		content.setMouseTransparent(false);

		primaryStage.setScene(scene);

		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setMinWidth(Toolkit.getDefaultToolkit().getScreenSize().width);
		primaryStage.setMinHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
		primaryStage.sizeToScene();


		primaryStage.addEventHandler(KeyEvent.ANY, keyEvent -> primaryStage.hide());
		canvas.addEventHandler(MouseEvent.ANY, mouseEvent -> {
			log.info("ME: {}", mouseEvent.getEventType());
			if(mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED){
				canvas.getGraphicsContext2D().drawImage(screenGrab, 0, 0);
				WritableImage subImg = new WritableImage(screenGrab.getPixelReader(), (int) mouseEvent.getScreenX() - 4, (int) mouseEvent.getScreenY() - 4, 8, 8);

				canvas.getGraphicsContext2D().drawImage(subImg, mouseEvent.getScreenX() - 32, mouseEvent.getScreenY() - 32, 64,  64);
				canvas.getGraphicsContext2D().setStroke(Color.RED);
				canvas.getGraphicsContext2D().setFill(Color.RED);

				canvas.getGraphicsContext2D().strokeLine(mouseEvent.getScreenX(), mouseEvent.getScreenY() - 18, mouseEvent.getScreenX(), mouseEvent.getScreenY() - 2);
				canvas.getGraphicsContext2D().strokeLine(mouseEvent.getScreenX(), mouseEvent.getScreenY() + 2, mouseEvent.getScreenX(), mouseEvent.getScreenY() + 18);

				canvas.getGraphicsContext2D().strokeLine(mouseEvent.getScreenX() - 18, mouseEvent.getScreenY(), mouseEvent.getScreenX() - 2, mouseEvent.getScreenY());
				canvas.getGraphicsContext2D().strokeLine(mouseEvent.getScreenX() + 2, mouseEvent.getScreenY(), mouseEvent.getScreenX() + 18, mouseEvent.getScreenY());
				Color colorAtCenter = screenGrab.getPixelReader().getColor((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
				GraphicsContext colorPickerGraphics = JagexColorPicker.colorPickerInstance.colorCanvas.getGraphicsContext2D();
				colorPickerGraphics.setFill(colorAtCenter);
				colorPickerGraphics.fillRect(0, 0, 128, 128);
				colorPickerGraphics.setFill(Color.WHITE);

				colorPickerGraphics.setFontSmoothingType(FontSmoothingType.LCD);
				colorPickerGraphics.setLineWidth(1.5);
				colorPickerGraphics.strokeText(ColourUtils.colorToHex(colorAtCenter), 63, 64);
				colorPickerGraphics.fillText(ColourUtils.colorToHex(colorAtCenter), 64, 64);
			} else if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
				Color colorAtCenter = screenGrab.getPixelReader().getColor((int) mouseEvent.getScreenX(), (int) mouseEvent.getScreenY());
				GraphicsContext colorPickerGraphics = JagexColorPicker.colorPickerInstance.colorCanvas.getGraphicsContext2D();
				colorPickerGraphics.setFill(Color.TRANSPARENT);
				colorPickerGraphics.clearRect(0, 0, 128, 128);
				JagexColorPicker.colorPickerInstance.pickerBox.setValue(colorAtCenter);
				primaryStage.hide();
			}
		});


		primaryStage.setOnHiding(evt -> {
			JagexColorPicker.stage.setAlwaysOnTop(false);
		});
	}

	private WritableImage screenGrab;

	public void show() {
		JagexColorPicker.stage.setAlwaysOnTop(true);
		BufferedImage img = robot.createScreenCapture(new Rectangle(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height));
		screenGrab = new WritableImage(img.getWidth(), img.getHeight());
		SwingFXUtils.toFXImage(img, screenGrab);
		pStage.show();
		pStage.setMinWidth(Toolkit.getDefaultToolkit().getScreenSize().width);
		pStage.setMinHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
		pStage.setWidth(Toolkit.getDefaultToolkit().getScreenSize().width);
		pStage.setHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
		canvas.setWidth(Toolkit.getDefaultToolkit().getScreenSize().width);
		canvas.setHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
		canvas.getGraphicsContext2D().drawImage(screenGrab, 0 ,0);
		pStage.setX(0);
		pStage.setY(0);
		pStage.requestFocus();
	}


	@FXML
	private Canvas canvas;

};
