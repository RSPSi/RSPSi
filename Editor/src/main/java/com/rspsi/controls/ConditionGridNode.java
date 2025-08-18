package com.rspsi.controls;

import java.io.IOException;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;

import com.rspsi.core.misc.ComparatorOperator;
import com.rspsi.util.FontAwesomeUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ConditionGridNode extends Group {
	
	public ConditionGridNode() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conditiongrid.fxml"));
		
		loader.setController(this);
		Parent content = loader.load();
		this.getChildren().add(content);
		
		gridCondition.getItems().addAll(ComparatorOperator.values());
		gridCondition.setValue(ComparatorOperator.EQUAL_TO);
		requiredValue.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
		requiredValue.setEditable(true);
		 final GlyphFont fontAwesome = FontAwesomeUtil.getFont();
		 Glyph deleteIcon = fontAwesome.create(FontAwesome.Glyph.CLOSE)
		                .size(13)
		                .color(Color.RED);
		deleteBtn.setGraphic(deleteIcon);
		deleteBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}
	

    @FXML
    private ChoiceBox<ComparatorOperator> gridCondition;

    @FXML
    private Spinner<Integer> requiredValue;

    @FXML
    private HBox overlayBox;

    @FXML
    private HBox underlayBox;
    

    @FXML
    private Button deleteBtn;

	public ChoiceBox<ComparatorOperator> getGridCondition() {
		return gridCondition;
	}

	public Spinner<Integer> getRequiredValue() {
		return requiredValue;
	}

	public HBox getOverlayBox() {
		return overlayBox;
	}

	public HBox getUnderlayBox() {
		return underlayBox;
	}

	public Button getDeleteBtn() {
		return deleteBtn;
	}

	public int getOverlay() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getUnderlay() {
		// TODO Auto-generated method stub
		return 0;
	}
    
	
    

}
