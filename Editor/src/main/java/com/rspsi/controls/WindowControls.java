package com.rspsi.controls;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;

import com.rspsi.helper.DragHelper;
import com.rspsi.helper.MaximizeHelper;
import com.rspsi.helper.ResizeHelper;
import com.rspsi.helper.ResizeHelper.ResizeListener;
import com.rspsi.ui.misc.SGVConstants;
import com.rspsi.util.FontAwesomeUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class WindowControls {

	public static WindowControls addUtilityWindowControls(Stage primaryStage, Node dragBar, HBox container) {
		WindowControls controls = new WindowControls(primaryStage, container, dragBar, false);
		container.getChildren().removeAll(controls.minimizeButton, controls.maximizeButton);

		return controls;
	}

	public static WindowControls addWindowControls(Stage primaryStage, Node dragBar, HBox container) {
		return new WindowControls(primaryStage, container, dragBar);
	}
	
	public static WindowControls addWindowControlsFixed(Stage primaryStage, Node dragBar, HBox container) {
		WindowControls controls = new WindowControls(primaryStage, container, dragBar, false);
		container.getChildren().removeAll(controls.maximizeButton);
		
		return controls;
	}


	private MaximizeHelper maximizeHelper;

	private DragHelper dragHelper;
	
	private ResizeListener resizeHelper;

	@FXML
	private Button minimizeButton;

	@FXML
	private Button maximizeButton;

	@FXML
	private Button closeButton;

	private WindowControls(Stage primaryStage, HBox insertInto, Node dragBar) {

		this(primaryStage, insertInto, dragBar, true);
	}

	private WindowControls(Stage primaryStage, HBox insertInto, Node dragBar, boolean resizable) {

		if (resizable) {

			maximizeHelper = new MaximizeHelper(primaryStage);
			resizeHelper = ResizeHelper.addResizeListener(primaryStage);
		}

		dragHelper = new DragHelper(primaryStage, dragBar, maximizeHelper);

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/scenecontrols.fxml"));
			loader.setController(this);
			loader.load();

			 final GlyphFont fontAwesome = FontAwesomeUtil.getFont();
			 Glyph closeIcon = fontAwesome.create(FontAwesome.Glyph.CLOSE)
		                .size(10)
		                .color(Color.WHITE);
			closeButton.setGraphic(closeIcon);
			closeButton.setOnAction(evt -> {
				primaryStage.close();
			});
			
			minimizeButton.setGraphic(SGVConstants.getMinimize());
			minimizeButton.setOnAction(evt -> {
				primaryStage.setIconified(true);
			});
			if (maximizeHelper != null) {
				maximizeHelper.addButton(maximizeButton);
			}
			insertInto.getChildren().addAll(minimizeButton, maximizeButton, closeButton);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public Button getCloseButton() {
		return closeButton;
	}

	public DragHelper getDragHelper() {
		return dragHelper;
	}

	public Button getMaximizeButton() {
		return maximizeButton;
	}

	public MaximizeHelper getMaximizeHelper() {
		return maximizeHelper;
	}

	public Button getMinimizeButton() {
		return minimizeButton;
	}

	public ResizeListener getResizeHelper() {
		return resizeHelper;
	}

}
