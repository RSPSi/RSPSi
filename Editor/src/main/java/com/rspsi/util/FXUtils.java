package com.rspsi.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FXUtils {

	private static Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	private static ObservableList<Screen> screens = Screen.getScreens();
	public static void centerStage(Stage primaryStage){

		//log.info("{}", System.getProperty("os.name"));
		if(OSUtil.isUnix() && screens.size() > 1){
			Rectangle2D rec = screens.get(1).getVisualBounds();
			//log.info("Screen: {} | {} ", Toolkit.getToolkit().getPrimaryScreen(), screens);
			// upper left corner of the extended screen
			double offsetX = rec.getMinX();
			double offsetY = rec.getMinY();
			// set relative to this screen
			primaryStage.setX(primaryScreenBounds.getMinX() + offsetX);
			primaryStage.setY(primaryScreenBounds.getMinY() + offsetY);
		}

	}

	public static void fillAnchorPane(Node node) {
		AnchorPane.setLeftAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
	}
	
	public static void setAnchorPane(Node node, double left, double right, double top, double bottom) {
		AnchorPane.setLeftAnchor(node, left);
		AnchorPane.setRightAnchor(node, right);
		AnchorPane.setTopAnchor(node, top);
		AnchorPane.setBottomAnchor(node, bottom);
	}
	
	public static int getRowCount(GridPane pane) {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null){
                    numRows = Math.max(numRows,rowIndex+1);
                }
            }
        }
        return numRows;
    }
	
	public static int getColumnCount(GridPane pane) {
        int numColumns = pane.getColumnConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer columnIndex = GridPane.getColumnIndex(child);
                if(columnIndex != null){
                    numColumns = Math.max(numColumns,columnIndex+1);
                }
            }
        }
        return numColumns;
    }
	
	public static void deleteRow(GridPane grid, final int row) {
	    Set<Node> deleteNodes = new HashSet<>();
	    for (Node child : grid.getChildren()) {
	        // get index from child
	        Integer rowIndex = GridPane.getRowIndex(child);

	        // handle null values for index=0
	        int r = rowIndex == null ? 0 : rowIndex;

	        if (r > row) {
	            // decrement rows for rows after the deleted row
	            GridPane.setRowIndex(child, r-1);
	        } else if (r == row) {
	            // collect matching rows for deletion
	            deleteNodes.add(child);
	        }
	    }

	    // remove nodes from row
	    grid.getChildren().removeAll(deleteNodes);
	}
	
	public static void deleteColumn(GridPane grid, final int column) {
	    Set<Node> deleteNodes = new HashSet<>();
	    for (Node child : grid.getChildren()) {
	        // get index from child
	        Integer columnIndex = GridPane.getColumnIndex(child);

	        // handle null values for index=0
	        int c = columnIndex == null ? 0 : columnIndex;

	        if (c > column) {
	            // decrement rows for rows after the deleted row
	            GridPane.setRowIndex(child, c-1);
	        } else if (c == column) {
	            // collect matching rows for deletion
	            deleteNodes.add(child);
	        }
	    }

	    // remove nodes from row
	    grid.getChildren().removeAll(deleteNodes);
	}

	public static void addSpinnerFocusListeners(Spinner<?>... spinners){
		for(Spinner<?> spinner : spinners){
			TextFormatter formatter = new TextFormatter(spinner.getValueFactory().getConverter(), spinner.getValueFactory().getValue());
			spinner.getEditor().setTextFormatter(formatter);
			spinner.getValueFactory().valueProperty().bindBidirectional(formatter.valueProperty());

		}
	}



}
