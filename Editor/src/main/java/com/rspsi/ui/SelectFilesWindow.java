package com.rspsi.ui;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.Maps;
import com.jagex.chunk.Chunk;
import com.rspsi.controls.SelectFilesNode;
import com.rspsi.core.misc.Location;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.util.FXUtils;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Slf4j
public class SelectFilesWindow extends Application {

	private Stage stage;
	private boolean okClicked;

	private Map<Location, SelectFilesNode> cachedNodes = Maps.newConcurrentMap();

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/selectfiles.fxml"));

		loader.setController(this);
		Parent content = loader.load();
		Scene scene = new Scene(content);



		primaryStage.setTitle("Please select files to load");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());

		primaryStage.setAlwaysOnTop(true);
		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();

		widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1, 1));
		lengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1, 1));
		RowConstraints defaultRow = new RowConstraints();

		Consumer<SelectFilesNode> cacheNode = (node) -> {
			Location location = new Location(GridPane.getColumnIndex(node), GridPane.getRowIndex(node), 0);
			cachedNodes.put(location, node);
		};

		widthSpinner.getValueFactory().valueProperty().addListener((observable, oldVal, newVal) -> {
			log.info("Width resize from {} to {}", oldVal, newVal);
				if(oldVal < newVal) {//Increase

					int diff = newVal - oldVal;
					for(int index = 0;index<diff;index++){
						int fIndex = oldVal + index;
						List<SelectFilesNode> nodes = generateSelectNodes(FXUtils.getRowCount(gridPane), fIndex, false);
						gridPane.addColumn(fIndex, nodes.toArray(new SelectFilesNode[nodes.size()]));
						log.info("Added column {}", fIndex);
					}
				} else if(oldVal > newVal) {
					int diff = oldVal - newVal;
					for(int index = 0;index<diff;index++){
						int fIndex = oldVal - index - 1;
						gridPane.getChildrenUnmodifiable()
								.stream()
								.filter(node -> node instanceof SelectFilesNode)
								.filter(node -> GridPane.getColumnIndex(node) == fIndex)
								.map(node -> (SelectFilesNode) node)
								.forEach(cacheNode);
						FXUtils.deleteColumn(gridPane, fIndex);
						log.info("Deleted column {}", fIndex);
					}
				}

				stage.sizeToScene();
		});

		lengthSpinner.getValueFactory().valueProperty().addListener((observable, oldVal, newVal) -> {

			log.info("Length resize from {} to {}", oldVal, newVal);
				if(oldVal < newVal) {//Increase
					int diff = newVal - oldVal;
					for(int index = 0;index<diff;index++){
						int fIndex = oldVal + index;
						List<SelectFilesNode> nodes = generateSelectNodes(FXUtils.getColumnCount(gridPane), fIndex, true);
						gridPane.addRow(fIndex, nodes.toArray(new SelectFilesNode[nodes.size()]));
						log.info("Added row {}", fIndex);
					}
				} else if(oldVal > newVal) {
					int diff = oldVal - newVal;
					for(int index = 0;index<diff;index++){
						int fIndex = oldVal - index - 1;
						gridPane.getChildrenUnmodifiable()
						.stream()
						.filter(node -> node instanceof SelectFilesNode)
						.filter(node -> GridPane.getRowIndex(node) == fIndex)
						.map(node -> (SelectFilesNode) node)
						.forEach(cacheNode);
						FXUtils.deleteRow(gridPane, fIndex);
						log.info("Deleted row {}", fIndex);
					}
				}
			stage.sizeToScene();

		});


		FXUtils.addSpinnerFocusListeners(widthSpinner, lengthSpinner);

		try {
			gridPane.add(new SelectFilesNode(stage), 0, 0);
		} catch(Exception ex) {

		}

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if(event.getCode() == KeyCode.ENTER) {
                primaryStage.hide();
                okClicked = true;
            }
        });

		okButton.setOnAction(evt -> {
			primaryStage.hide();
			okClicked = true;
		});
		cancelButton.setOnAction(evt -> {
			reset();
			primaryStage.hide();
		});
	}

	private List<SelectFilesNode> generateSelectNodes(int count, int index, boolean row) {

		List<SelectFilesNode> nodes = Lists.newArrayList();
		for(int i = 0;i<count;i++)
			try {
				Location location = new Location(row ? i : index, row ? index : i, 0);

				nodes.add(cachedNodes.getOrDefault(location, new SelectFilesNode(stage)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return nodes;
	}
	public void show() {
		reset();
		stage.sizeToScene();
		okButton.requestFocus();
		stage.showAndWait();
		if(!okClicked)
			reset();
	}

	public List<Chunk> prepareChunks(){
		List<Chunk> chunks = Lists.newArrayList();

		int defaultObjId = 0;
		int defaultLandscapeId = 1;
		for(SelectFilesNode selectNode : getSelectNodes()){
			if(!selectNode.valid())
				continue;
			int positionX = GridPane.getColumnIndex(selectNode);
			int positionY =	(FXUtils.getRowCount(gridPane) - 1) - GridPane.getRowIndex(selectNode);
			int hash = (positionX << 8) + positionY;
			Chunk chunk = new Chunk(hash);

			chunk.offsetX = 64 * positionX;
			chunk.offsetY = 64 * positionY;

			chunk.objectMapData = selectNode.getObjectMapData();
			chunk.tileMapData = selectNode.getLandscapeMapData();
			chunk.objectMapId = selectNode.tryGetObjectMapId(defaultObjId+=2);
			chunk.tileMapId = selectNode.tryGetLandscapeMapId(defaultLandscapeId+=2);
			chunks.add(chunk);
		}


		return chunks;

	}

	private List<SelectFilesNode> getSelectNodes(){
		List<SelectFilesNode> nodes = Lists.newArrayList();
		for(Node node : gridPane.getChildren()){
			if(node instanceof SelectFilesNode) {
				SelectFilesNode selectNode = (SelectFilesNode) node;
				nodes.add(selectNode);
			}
		}
		return nodes;
	}

	public void reset() {
		cachedNodes.clear();
		okClicked = false;
	}

	public boolean valid() {
	    if(!okClicked)
	        return false;
	    
		boolean valid = true;
		for(SelectFilesNode selectNode : getSelectNodes()){
			if(!selectNode.valid()) {
				valid = false;
				break;
			}

		}
		return valid;
	}


    @FXML
    private Spinner<Integer> widthSpinner;

    @FXML
    private Spinner<Integer> lengthSpinner;

	@FXML
	private GridPane gridPane;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;
}
