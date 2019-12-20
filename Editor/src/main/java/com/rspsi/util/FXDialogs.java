package com.rspsi.util;

import java.awt.Robot;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class FXDialogs {

	public static final String YES = "Yes";

	public static final String NO = "No";

	public static final String OK = "OK";

	public static final String CANCEL = "Cancel";

	public static String showConfirm(Window parent, String title, String message, String... options) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Choose an option");
		alert.setHeaderText(title);
		alert.setContentText(message);
		alert.initOwner(parent);
		alert.initModality(Modality.APPLICATION_MODAL);


		// To make enter key press the actual focused button, not the first one. Just
		// like pressing "space".
		alert.getDialogPane().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				event.consume();
				try {
					Robot r = new Robot();
					r.keyPress(java.awt.event.KeyEvent.VK_SPACE);
					r.keyRelease(java.awt.event.KeyEvent.VK_SPACE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		if (options == null || options.length == 0) {
			options = new String[] { OK, CANCEL };
		}

		List<ButtonType> buttons = new ArrayList<>();
		for (String option : options) {
			buttons.add(new ButtonType(option));
		}

		alert.getButtonTypes().setAll(buttons);

		Optional<ButtonType> result = alert.showAndWait();
		if (!result.isPresent())
			return CANCEL;
		else
			return result.get().getText();
	}

	public static void showError(Window parent, String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Error");
		alert.setHeaderText(title);
		alert.setContentText(message);

		alert.initOwner(parent);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.showAndWait();
	}

	public static void showException(Window parent, String title, String message, Exception exception) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Exception");
		alert.setHeaderText(title);
		alert.setContentText(message);

		alert.initOwner(parent);
		alert.initModality(Modality.APPLICATION_MODAL);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("Details:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

	public static void showInformation(Window parent, String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Information");
		alert.setHeaderText(title);
		alert.setContentText(message);

		alert.initOwner(parent);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.showAndWait();
	}

	public static String showTextInput(Window parent, String title, String message, String defaultValue) {
		TextInputDialog dialog = new TextInputDialog(defaultValue);
		dialog.initStyle(StageStyle.UTILITY);
		dialog.setTitle("Input");
		dialog.setHeaderText(title);
		dialog.setContentText(message);

		dialog.initOwner(parent);
		dialog.initModality(Modality.APPLICATION_MODAL);
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
			return result.get();
		else
			return null;

	}

	public static void showWarning(Window parent, String title, String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Warning");
		alert.setHeaderText(title);
		alert.setContentText(message);

		alert.initOwner(parent);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.showAndWait();
	}

}
