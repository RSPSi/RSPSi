package com.rspsi.controls;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.compress.utils.Lists;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.rspsi.ObjectPreviewWindow;
import com.rspsi.controllers.SwatchController;
import com.rspsi.datasets.ObjectDataset;
import com.rspsi.misc.JsonUtil;
import com.rspsi.misc.ToolType;
import com.rspsi.options.Options;
import com.rspsi.swatches.BaseSwatch;
import com.rspsi.swatches.ObjectSwatch;
import com.rspsi.swatches.OverlaySwatch;
import com.rspsi.swatches.SwatchType;
import com.rspsi.swatches.UnderlaySwatch;
import com.rspsi.util.FXUtils;
import com.rspsi.util.FilterMode;
import com.rspsi.util.FontAwesomeUtil;
import com.rspsi.util.RetentionFileChooser;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SwatchControl extends AnchorPane {

	private SwatchController controller = new SwatchController();
	private ToggleGroup group = new ToggleGroup();
	
	private SimpleObjectProperty<BaseSwatch> selectedSwatch = new SimpleObjectProperty<BaseSwatch>(null); 
	
	private Map<ToggleButton, BaseSwatch> swatchDataset = Maps.newHashMap();

	public SwatchControl(SwatchType type) {
		String fxmlFile;
		switch (type) {
		case OVERLAY:
		case UNDERLAY:
			fxmlFile = "/fxml/swatchui2.fxml";
			break;
		default:
			fxmlFile = "/fxml/swatchui.fxml";
			break;
		}
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
		loader.setController(controller);

		try {
			Parent content = (Parent) loader.load();
			this.getChildren().add(content);
			FXUtils.fillAnchorPane(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (controller.getObjectViewBtn() != null) {
			controller.getObjectViewBtn().setOnAction(act -> {
				ObjectPreviewWindow.instance.stage.show();
			});
			 final GlyphFont fontAwesome = FontAwesomeUtil.getFont();
			 Glyph openFolder = fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN)
			                .size(12)
			                .color(Color.WHITE);
			controller.getLoadBtn().setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			controller.getLoadBtn().setGraphic(openFolder);
			controller.getLoadBtn().setOnAction(evt -> {
				File file = RetentionFileChooser.showOpenDialog(FilterMode.SWATCH);
				ObjectMapper mapper = JsonUtil.getDefaultMapper();
				// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				try {
					List<ObjectDataset> dataset = mapper.readValue(file, new TypeReference<List<ObjectDataset>>() {
					});
					ObjectPreviewWindow.instance.loadToSwatches(dataset);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			
			 Glyph saveIcon = fontAwesome.create(FontAwesome.Glyph.SAVE)
		                .size(12)
		                .color(Color.WHITE);
			controller.getSaveBtn().setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			controller.getSaveBtn().setGraphic(saveIcon);
			controller.getSaveBtn().setOnAction(evt -> {
				List<ObjectDataset> objectDataset = Lists.newArrayList();
				for(BaseSwatch data : this.swatchDataset.values()) {
					if(data instanceof ObjectSwatch) {
						ObjectSwatch obj = ((ObjectSwatch)data);
						objectDataset.add(obj.getData());
					}
				}
				File file = RetentionFileChooser.showSaveDialog(FilterMode.SWATCH);
				ObjectMapper mapper = JsonUtil.getDefaultMapper();
				try {
					mapper.writeValue(file, objectDataset);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		this.setCursor(Cursor.DEFAULT);
	}

	public void addSwatch(BaseSwatch data) {
		Platform.runLater(() -> {
			ToggleButton btn = new ToggleButton();
			swatchDataset.put(btn, data);
			btn.setAlignment(Pos.CENTER);
			btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			btn.setAlignment(Pos.CENTER);
			btn.setToggleGroup(group);

			VBox vbox = new VBox();
			vbox.setSpacing(8);
			vbox.setAlignment(Pos.CENTER);
			VBox.setVgrow(data.getGroup(), Priority.ALWAYS);

			switch (data.getType()) {
			case OBJECT:
				btn.setMinSize(75, 75);
				btn.setMaxSize(75, 75);
				Label label = new Label();
				label.setFont(Font.font(btn.getFont().getName(), 9.0));
				label.setText(data.getText());

				vbox.getChildren().addAll(label);
				break;
			case OVERLAY:
			case UNDERLAY:
				btn.setMinSize(34, 34);
				btn.setMaxSize(34, 34);
				break;
			}

			vbox.getChildren().addAll(data.getGroup());
			btn.setGraphic(vbox);
			Tooltip tooltip = new Tooltip(data.getText());
			tooltip.setAutoFix(true);

			btn.setTooltip(tooltip);

			EventHandler<ActionEvent> selectEvent = evt -> {
				switch (data.getType()) {
				case OBJECT:
					Options.currentTool.set(ToolType.SPAWN_OBJECT);
					Options.currentObject.set(((ObjectSwatch) data).getData());
					// Main.client.spawnTemp();

					break;
				case OVERLAY:
					Options.currentTool.set(ToolType.PAINT_OVERLAY);
					Options.overlayPaintId.set(((OverlaySwatch) data).getIndex() + 1);
					break;
				case UNDERLAY:
					Options.currentTool.set(ToolType.PAINT_UNDERLAY);
					Options.underlayPaintId.set(((UnderlaySwatch) data).getIndex() + 1);
					// Main.client.spawnTemp();
					break;
				}

			};

			ContextMenu menu = new ContextMenu();
			MenuItem selectOption = new MenuItem("Select " + data.getText());
			selectOption.setOnAction(selectEvent);
			menu.getItems().add(selectOption);

			if (data.getType() == SwatchType.OBJECT) {
				MenuItem removeOption = new MenuItem("Remove " + data.getText());
				removeOption.setOnAction(evt -> {
					controller.getSwatchFlowPane().getChildren().remove(btn);
				});
				menu.getItems().add(removeOption);
			}

			btn.setContextMenu(menu);
			btn.setOnAction(selectEvent);
			controller.getSwatchFlowPane().getChildren().add(btn);
		});
	}

	public void deselect() {
		group.selectToggle(null);
	}

	public SwatchController getController() {
		return controller;
	}

	public void clear() {
		swatchDataset.clear();
		group.getToggles().clear();
		Platform.runLater(() -> {
			controller.getSwatchFlowPane().getChildren().clear();
		});
	}
	
	public void selectByUnderlay(int underlayId) {
		for(Entry<ToggleButton, BaseSwatch> entry : swatchDataset.entrySet()) {
			if(entry.getValue() instanceof UnderlaySwatch) {
				UnderlaySwatch swatch = (UnderlaySwatch) entry.getValue();
				if(swatch.getIndex() == underlayId) {
					entry.getKey().fire();
					return;
				}
			}
		}
	}

	public void selectByOverlay(int overlayId) {
		for(Entry<ToggleButton, BaseSwatch> entry : swatchDataset.entrySet()) {
			if(entry.getValue() instanceof OverlaySwatch) {
				OverlaySwatch swatch = (OverlaySwatch) entry.getValue();
				if(swatch.getIndex() == overlayId) {
					entry.getKey().fire();
					return;
				}
			}
		}
	}

	public void setOverlayShape(int overlayShape) {
		if(overlayShape >= 0) {
			Object obj = overlayShapeGroup.getProperties().get(overlayShape);
			if(obj != null && obj instanceof ToggleButton) {
				ToggleButton btn = (ToggleButton) obj;
				btn.fire();
			}
		}
	}
	
	private ToggleGroup overlayShapeGroup;

	public void setOverlayShapeGroup(ToggleGroup tg) {
		this.overlayShapeGroup = tg;
		
	}

}
