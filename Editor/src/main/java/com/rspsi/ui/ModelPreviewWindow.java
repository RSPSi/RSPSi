package com.rspsi.ui;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;

import com.jagex.entity.model.MeshLoader;
import com.rspsi.controllers.ModelPreviewController;
import com.rspsi.controls.GenericModelView;
import com.rspsi.controls.WindowControls;
import com.rspsi.ui.misc.NamedValueObject;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.util.FXUtils;
import com.rspsi.util.FontAwesomeUtil;
import com.rspsi.util.ReflectionUtil;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ModelPreviewWindow extends Application {

	public static ModelPreviewWindow instance;
	
	private ObservableList<Integer> data = FXCollections.observableArrayList();

	public Stage stage;
	private ModelPreviewController controller = new ModelPreviewController();
	private GenericModelView view;

	public ModelPreviewWindow() {
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
		TreeView<Integer> treeView = controller.getModelIdList();
		treeView.setCellFactory(tv -> {
			TreeCell<Integer> cell = new TreeCell<Integer>() {
				@Override
				public void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
					} else {
						setText("ID: " + item.intValue());
					}
				}

			};
			cell.selectedProperty().addListener((observable, oldVal, newVal) -> {
				if (newVal) {
					if (!cell.isEmpty()) {
						Integer cellSelection = cell.getItem();
						
						view.prepareView(cellSelection);
						
						controller.getDefinitionTable().getItems().clear();
						List<NamedValueObject> obj = ReflectionUtil.getValueAsNamedValueList(MeshLoader.getSingleton().lookup(cellSelection));
					
						controller.getDefinitionTable().getItems().addAll(obj);
					}
				}
			});
			return cell;
		});
		
		

		
		data = FXCollections.observableArrayList(IntStream.range(0, 100000).boxed().collect(Collectors.toList()));
		//TODO increase this to actual model count
		filterList("");
		// branch.setExpanded(true);
	}
	
	public void filterList(String filterString) {

		TreeView<Integer> treeView = controller.getModelIdList();
		
		treeView.setRoot(null);
		
		TreeItem<Integer> branch = new TreeItem<>();

		data.forEach(dataset -> {
			if(filterString.equals("") || dataset.toString().toLowerCase().contains(filterString.toLowerCase())) {
				
				branch.getChildren().add(new TreeItem<Integer>(dataset));
			}
		});
		
		treeView.setRoot(branch);
		treeView.setShowRoot(false);
	}

	public ModelPreviewController getController() {
		return controller;
	}

	public GenericModelView getView() {
		// TODO Auto-generated method stub
		return view;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		instance = this;
		stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modelview.fxml"));
		loader.setController(controller);
		Parent content = loader.load();
		Scene scene = new Scene(content, 800, 600);


		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());
		primaryStage.setTitle("RS2i Model Preview");

		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(scene);
		primaryStage.show();

		view = new GenericModelView();
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



		view.setZoom(view.getZoom() + 1);//Cheap fix for first added swatches being broken
		fillList();
	}
	


	@Override
	public void stop() throws Exception {
		super.stop();
		this.stage.close();
	}
}
