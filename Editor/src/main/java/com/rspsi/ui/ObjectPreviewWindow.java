package com.rspsi.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;

import com.jagex.cache.config.VariableBits;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.entity.model.PreviewModel;
import com.rspsi.controllers.ObjectPreviewController;
import com.rspsi.controls.ObjectModelView;
import com.rspsi.controls.SwatchControl;
import com.rspsi.controls.WindowControls;
import com.rspsi.datasets.ObjectDataset;
import com.rspsi.ui.misc.NamedValueObject;
import com.rspsi.core.misc.Vector3;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.swatches.ObjectSwatch;
import com.rspsi.util.FXDialogs;
import com.rspsi.util.FXUtils;
import com.rspsi.util.FontAwesomeUtil;
import com.rspsi.util.ReflectionUtil;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
						setText(item.getId() + ": " + item.getName() + " [" + item.getType() + "]");
					}
				}

			};
			cell.selectedProperty().addListener((observable, oldVal, newVal) -> {
				if (newVal) {
					if (!cell.isEmpty() && !cell.getItem().isRoot()) {
						ObjectDataset cellSelection = cell.getItem();
						
						view.prepareView(cellSelection);
						ObjectDefinition def = ObjectDefinitionLoader.lookup(cellSelection.getId());
						
						controller.getDefinitionTable().getItems().clear();
						controller.getDefinitionTable().getItems().addAll(ReflectionUtil.getValueAsNamedValueList(def));
						ContextMenu contextMenu = new ContextMenu();
						MenuItem copyItem = new MenuItem("Copy");
						copyItem.setOnAction(al -> {
							NamedValueObject obj = controller.getDefinitionTable().getSelectionModel().getSelectedItem();
							if(obj != null) {
								final ClipboardContent content = new ClipboardContent();
							     content.putString(obj.getName() + ": " + obj.getValue());
								Clipboard.getSystemClipboard().setContent(content);
							}
						});
						MenuItem showVarbit = new MenuItem("Show varbit");
						showVarbit.setOnAction(al -> {
							Optional<NamedValueObject> optObj = controller.getDefinitionTable().getItems().stream().filter(obj -> obj.getName().equalsIgnoreCase("varbit")).findAny();
							if(optObj.isPresent()) {
								if(!optObj.get().getValue().equalsIgnoreCase("-1")) {
									VariableBits bit = VariableBitLoader.lookup(Integer.parseInt(optObj.get().getValue()));
									FXDialogs.showInformation(stage,"Varbit [" + optObj.get().getValue() + "]", "Setting: " + bit.getSetting() + "\nHigh: " + bit.getHigh() + "\nLow: " + bit.getLow());
								} else {
									FXDialogs.showError(stage,"Error grabbing varbit", "Varbit ID was -1");
								}
								
									
								
							}
						});
						contextMenu.getItems().add(copyItem);
						contextMenu.getItems().add(showVarbit);
						controller.getDefinitionTable().setContextMenu(contextMenu);
					}
				}
			});
			return cell;
		});
		
		
		
		for (int i = 0; i < ObjectDefinitionLoader.getCount(); i++) {
			ObjectDefinition def = ObjectDefinitionLoader.lookup(i);
			if (def != null) {
				if (def.getModelTypes() != null) {
					for (int idx = 0; idx < def.getModelTypes().length; idx++) {
						data.add(new ObjectDataset(def.getId(), def.getModelTypes()[idx], def.getName()));
						if(def.getModelTypes()[idx] == 10)
							data.add(new ObjectDataset(def.getId(), 11, def.getName()));
					}
				} else {
					data.add(new ObjectDataset(def.getId(), 10, def.getName()));
					data.add(new ObjectDataset(def.getId(), 11, def.getName()));
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
		data.forEach(dataset -> {
			if(!filterString.isEmpty() && filterString.startsWith("has:")){
				//String[] defFilter = filterString.replaceFirst("has:", "").trim().split("[(.*)]");
			}
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

	public void openObject(ObjectDataset dataset){
		try {
			Optional<TreeItem<ObjectDataset>> leafOpt = controller.getObjectDefinitionList().getRoot().getChildren().stream().filter(item -> item.getValue().getType() == dataset.getType()).findFirst();
			leafOpt.ifPresent(leaf -> {
				controller.getObjectDefinitionList().getRoot().getChildren().stream().filter(item -> item.getValue().getType() != dataset.getType()).forEach(item -> item.setExpanded(false));
				leaf.setExpanded(true);
				leaf.getChildren().stream().filter(item -> item.getValue().getId() == dataset.getId()).findFirst().ifPresent(item -> {
					controller.getObjectDefinitionList().getSelectionModel().select(item);
					controller.getObjectDefinitionList().scrollTo(controller.getObjectDefinitionList().getSelectionModel().getSelectedIndex());

				});

			});
		} catch(Exception ex){
			ex.printStackTrace();
			FXDialogs.showError(stage,"Error loading object", "Could not load the selected object to view");
		}
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
		Parent content = loader.load();
		Scene scene = new Scene(content, 800, 600);


		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());
		primaryStage.setTitle("RS2i Object Preview");

		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(scene);
		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();

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
			ObjectDataset dataset = new ObjectDataset(view.getCurrentSelection(), view.getZoom(), view.getRotationControl().getAsVector3());
			ObjectSwatch data = new ObjectSwatch(dataset, g, dataset.getId() + ": " + dataset.getName());
			
			objectSwatch.addSwatch(data);
		});
		

		view.setZoom(1500);//Cheap fix for first added swatches being broken

	}
	
	public void loadToSwatches(List<ObjectDataset> data) {
		PreviewModel oldModel = view.getModel();
		int oldZoom = view.getZoom();
		Vector3 oldRotation = view.getRotationControl().getAsVector3();
		view.setZoom(1900);
		for(ObjectDataset dataset : data) {
			loadToSwatches(dataset);
		}
		view.getRotationControl().loadFromVector3(oldRotation);
		view.setZoom(oldZoom);
		view.setModel(oldModel);
		view.renderModel();
	}
	
	public void loadToSwatches(ObjectDataset dataset) {
		PreviewModel oldModel = view.getModel();
		int oldZoom = view.getZoom();
		Vector3 oldRotation = view.getRotationControl().getAsVector3();
		view.setZoom(dataset.getZoom() == -1 ? 1600 : dataset.getZoom());
		if(dataset.getRotation() != null){
			view.getRotationControl().loadFromVector3(dataset.getRotation());
		}
		view.prepareView(dataset);
		WritableImage img = null;
		while(img == null) {
			try {
				img = view.getModelCanvas().trimmedSnapshot();
			} catch (Exception ex) {
				//Image is all black
				ex.printStackTrace();
				view.getRotationControl().getRotateX().setAngle(oldRotation.getX() + 32);
				view.getRotationControl().getRotateZ().setAngle(180);
				view.getRotationControl().getRotateY().setAngle(180);
			}
		}
		Group g = new Group();
		ImageView imgView = new ImageView(img);
		imgView.setPreserveRatio(true);
		imgView.setSmooth(true);

		imgView.setFitHeight(40.0);
		imgView.setFitWidth(62.0);
		imgView.setLayoutX(5);
		imgView.setLayoutY(5);

		g.getChildren().add(imgView);
		if(dataset.getRotation() == null)
			dataset.setRotation(view.getRotationControl().getAsVector3());
		if(dataset.getZoom() < 0)
			dataset.setZoom(view.getZoom());
		if(dataset.getName().contains("[")){
			dataset.setName(dataset.getName().substring(0, dataset.getName().indexOf("[")));
		}
		ObjectSwatch swatch = new ObjectSwatch(dataset, g,dataset.getId() + ": " + dataset.getName() + "[" + dataset.getType() + "]");

		objectSwatch.addSwatch(swatch);

		view.getRotationControl().loadFromVector3(oldRotation);
		view.setZoom(oldZoom);
		view.setModel(oldModel);
		view.renderModel();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		this.stage.close();
	}
}
