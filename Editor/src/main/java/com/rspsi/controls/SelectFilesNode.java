package com.rspsi.controls;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

import com.jagex.util.IntParser;
import com.rspsi.util.FilterMode;
import com.rspsi.util.RetentionFileChooser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.displee.util.GZIPUtils;

public class SelectFilesNode extends Group {
	
	public SelectFilesNode(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/selectfilesingle.fxml"));
		
		loader.setController(this);
		Parent content = loader.load();
		this.getChildren().add(content);
		
		
		Consumer<TextField> finishBrowse = textField -> {
			File f = RetentionFileChooser.showOpenDialog(stage, FilterMode.GZIP, FilterMode.DAT);
			if(f != null && f.exists()) {
				textField.setText(f.getAbsolutePath());
			}
		};
		
		objectBrowse.setOnAction(evt -> finishBrowse.accept(objectText));
		landscapeBrowse.setOnAction(evt -> finishBrowse.accept(landscapeText));
		
		
	}
	
	public boolean valid() {
		return !landscapeText.getText().isEmpty() && !objectText.getText().isEmpty();
	}

    @FXML
    private TextField landscapeText;

    @FXML
    private Button landscapeBrowse;

    @FXML
    private TextField objectText;

    @FXML
    private Button objectBrowse;

	public TextField getLandscapeText() {
		return landscapeText;
	}

	public Button getLandscapeBrowse() {
		return landscapeBrowse;
	}

	public TextField getObjectText() {
		return objectText;
	}

	public Button getObjectBrowse() {
		return objectBrowse;
	}

	public byte[] getObjectMapData() {
		try {
			byte[] data = Files.readAllBytes(new File(objectText.getText()).toPath());
			if(objectText.getText().endsWith(".gz")) {
				data = GZIPUtils.unzip(data);
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] getLandscapeMapData() {
		try {
			byte[] data = Files.readAllBytes(new File(landscapeText.getText()).toPath());
			if(landscapeText.getText().endsWith(".gz")) {
				data = GZIPUtils.unzip(data);
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int tryGetObjectMapId(int defaultVal) {
		return IntParser.parseInt(getFormattedFileName(objectText.getText()), defaultVal);
	}

	public int tryGetLandscapeMapId(int defaultVal) {
		return IntParser.parseInt(getFormattedFileName(landscapeText.getText()), defaultVal);
	}
	
	private String getFormattedFileName(String text) {
		return new File(text).getName().replace(".gz", "").replace(".dat", "").trim();
	}

}
