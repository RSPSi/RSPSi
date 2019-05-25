package com.rspsi;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.entity.model.PreviewModel;
import com.rspsi.controllers.ObjectPreviewController;
import com.rspsi.controls.ObjectModelView;
import com.rspsi.controls.SwatchControl;
import com.rspsi.controls.WindowControls;
import com.rspsi.datasets.ObjectDataset;
import com.rspsi.misc.NamedValueObject;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.swatches.ObjectSwatch;
import com.rspsi.util.FXUtils;
import com.rspsi.util.FontAwesomeUtil;
import com.rspsi.util.ReflectionUtil;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ObjectPreviewWindow extends Application {

	public static ObjectPreviewWindow instance;
	
	private ObservableList<ObjectDataset> data = FXCollections.observableArrayList();

	public Stage stage;
	private ObjectPreviewController controller = new ObjectPreviewController();
	private ObjectModelView view;
	private SwatchControl objectSwatch;

	public ObjectPreviewWindow(SwatchControl objectSwatch) {
		this.objectSwatch = objectSwatch;
	}

	public void fillList() {
		controller.getDefinitionTable().setOnMouseClicked(mouseEvent -> {
			if(mouseEvent.getButton() == MouseButton.SECONDARY) {
				NamedValueObject obj = controller.getDefinitionTable().getSelectionModel().getSelectedItem();
				if(obj != null) {
					final ClipboardContent content = new ClipboardContent();
				     content.putString(obj.getName() + ": " + obj.getValue());
					Clipboard.getSystemClipboard().setContent(content);
				}
			}
		});
		TreeView<ObjectDataset> treeView = controller.getObjectDefinitionList();
		treeView.setCellFactory(tv -> {
			TreeCell<ObjectDataset> cell = new TreeCell<ObjectDataset>() {
				@Override
				public void updateItem(ObjectDataset item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
					} else if (item.isRoot()) {
						setText("Type: " + item.getType());
					} else {
						setText(item.getId() + ": " + item.getName());
					}
				}

			};
			cell.selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldVal, newVal) -> {
				if (newVal) {
					if (!cell.isEmpty() && !cell.getItem().isRoot()) {
						ObjectDataset cellSelection = cell.getItem();
						
						view.prepareView(cellSelection);
						ObjectDefinition def = ObjectDefinitionLoader.lookup(cellSelection.getId());
						
						controller.getDefinitionTable().getItems().clear();
						controller.getDefinitionTable().getItems().addAll(ReflectionUtil.getValueAsNamedValueList(def));
					}
				}
			});
			return cell;
		});
		
		
		
		for (int i = 0; i < ObjectDefinitionLoader.instance.getCount(); i++) {
			ObjectDefinition def = ObjectDefinitionLoader.lookup(i);
			if (def != null) {
				if (def.getModelTypes() != null) {
					for (int idx = 0; idx < def.getModelTypes().length; idx++) {
						data.add(new ObjectDataset(def.getId(), def.getModelTypes()[idx], def.getName()));
					}
				} else {
					data.add(new ObjectDataset(def.getId(), 10, def.getName()));
				}
			}
		}
		filterList("");
		// branch.setExpanded(true);
	}
	
	public void filterList(String filterString) {

		TreeView<ObjectDataset> treeView = controller.getObjectDefinitionList();
		
		treeView.setRoot(null);
		
		List<TreeItem<ObjectDataset>> leafCells = new ArrayList<>();
		TreeItem<ObjectDataset> branch = new TreeItem<>();
		for (int i = 0; i < 23; i++) {
			TreeItem<ObjectDataset> leafItem = new TreeItem<>(new ObjectDataset(i));
			leafCells.add(leafItem);
		}
		data.stream().forEach(dataset -> {
			if(filterString.equals("") || dataset.toString().toLowerCase().contains(filterString.toLowerCase())) {
				int type = dataset.getType();
				TreeItem<ObjectDataset> leaf = leafCells.get(type);
				leaf.getChildren().add(new TreeItem<ObjectDataset>(dataset));
			}
		});
		for (int i = 0; i < 23; i++) {
			TreeItem<ObjectDataset> leafItem = leafCells.get(i);
			if (!leafItem.getChildren().isEmpty()) {
				branch.getChildren().add(leafItem);
			}
		}
		treeView.setRoot(branch);
		treeView.setShowRoot(false);
	}

	public ObjectPreviewController getController() {
		return controller;
	}

	public ObjectModelView getView() {
		// TODO Auto-generated method stub
		return view;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		instance = this;
		stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/objectview.fxml"));
		loader.setController(controller);
		Parent content = (Parent) loader.load();
		Scene scene = new Scene(content, 800, 600);


		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());
		primaryStage.setTitle("RS2i Object Preview");

		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(scene);

		view = new ObjectModelView();
		FXUtils.setAnchorPane(view, 10, 10, 20, 0);
		
		controller.getObjectViewPane().getChildren().add(view);
		
		controller.getSearchBox().setOnKeyPressed(keyEvt -> {
			if(keyEvt.getCode() == KeyCode.ENTER){
				String filterVal = controller.getSearchBox().getText();
				filterList(filterVal);
			}
		});
		 final GlyphFont fontAwesome = FontAwesomeUtil.getFont();
		 Glyph searchIcon = fontAwesome.create(FontAwesome.Glyph.SEARCH)
		                .size(13)
		                .color(Color.WHITE);
		controller.getSearchButton().setGraphic(searchIcon);
		controller.getSearchButton().setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		controller.getSearchButton().setOnAction(evt -> {
			String filterVal = controller.getSearchBox().getText();
			filterList(filterVal);
		});

		WindowControls.addWindowControls(primaryStage, controller.getTopBar(), controller.getControlBox());

		controller.getAddSwatchBtn().setOnAction(evt -> {
			WritableImage img = view.getModelCanvas().trimmedSnapshot();
			Group g = new Group();
			ImageView imgView = new ImageView(img);
			imgView.setPreserveRatio(true);
			imgView.setSmooth(true);
			
			imgView.setFitHeight(40.0);
			imgView.setFitWidth(62.0);
			imgView.setLayoutX(5);
			imgView.setLayoutY(5);
			
			g.getChildren().add(imgView);
			ObjectDataset dataset = new ObjectDataset(view.getCurrentSelection(), view.getZoom());
			ObjectSwatch data = new ObjectSwatch(dataset, g, dataset.getId() + ": " + dataset.getName());
			
			objectSwatch.addSwatch(data);
		});
		

		view.setZoom(view.getZoom() + 1);//Cheap fix for first added swatches being broken

	}
	
	public void loadToSwatches(List<ObjectDataset> data) {
		PreviewModel oldModel = view.getModel();
		int oldZoom = view.getZoom();
		for(ObjectDataset dataset : data) {
			view.setZoom(dataset.getZoom() == -1 ? 1900 : dataset.getZoom());
			view.prepareView(dataset);
			WritableImage img = view.getModelCanvas().trimmedSnapshot();
			Group g = new Group();
			ImageView imgView = new ImageView(img);
			imgView.setPreserveRatio(true);
			imgView.setSmooth(true);
			
			imgView.setFitHeight(40.0);
			imgView.setFitWidth(62.0);
			imgView.setLayoutX(5);
			imgView.setLayoutY(5);
			
			g.getChildren().add(imgView);
			ObjectSwatch swatch = new ObjectSwatch(dataset, g,
					dataset.getId() + ": " + dataset.getName());

			objectSwatch.addSwatch(swatch);
		}
		view.setModel(oldModel);
		view.renderModel();
		view.setZoom(oldZoom);
	}
	
	public void loadToSwatches(ObjectDataset dataset) {
		PreviewModel oldModel = view.getModel();
		int oldZoom = view.getZoom();
		view.setZoom(dataset.getZoom() == -1 ? 1900 : dataset.getZoom());
			view.prepareView(dataset);
			WritableImage img = view.getModelCanvas().trimmedSnapshot();
			Group g = new Group();
			ImageView imgView = new ImageView(img);
			imgView.setPreserveRatio(true);
			imgView.setSmooth(true);
			
			imgView.setFitHeight(40.0);
			imgView.setFitWidth(62.0);
			imgView.setLayoutX(5);
			imgView.setLayoutY(5);
			
			g.getChildren().add(imgView);
			ObjectSwatch swatch = new ObjectSwatch(dataset, g,
					dataset.getId() + ": " + dataset.getName());

			objectSwatch.addSwatch(swatch);
		
		view.setModel(oldModel);
		view.renderModel();
		view.setZoom(oldZoom);
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		this.stage.close();
	}
}
