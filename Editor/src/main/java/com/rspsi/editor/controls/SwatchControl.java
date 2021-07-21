package com.rspsi.editor.controls;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.rspsi.editor.MainWindow;
import com.rspsi.editor.ObjectPreviewWindow;
import com.rspsi.editor.controllers.SwatchController;
import com.rspsi.editor.datasets.ObjectDataset;
import com.rspsi.editor.helper.Draggable;
import com.rspsi.editor.helper.DraggableNature;
import com.rspsi.editor.swatches.*;
import com.rspsi.editor.tools.ToolRegister;
import com.rspsi.editor.tools.integrated.PaintOverlayTool;
import com.rspsi.editor.tools.integrated.PaintUnderlayTool;
import com.rspsi.editor.tools.integrated.SpawnObjectTool;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.misc.JsonUtil;
import com.rspsi.misc.ToolType;
import com.rspsi.options.KeyboardState;
import com.rspsi.options.Options;
import com.rspsi.util.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Slf4j
public class SwatchControl extends AnchorPane {

    private SwatchController controller = new SwatchController();
    private ToggleGroup group = new ToggleGroup();

    private SimpleObjectProperty<BaseSwatch> selectedSwatch = new SimpleObjectProperty<BaseSwatch>(null);

    private Map<ToggleButton, BaseSwatch> swatchDataset = Maps.newLinkedHashMap();
    private ToggleGroup overlayShapeGroup;

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

        AlwaysSelectToggleGroup.setup(group);

        try {
            Parent content = loader.load();
            this.getChildren().add(content);
            FXUtils.fillAnchorPane(content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (controller.getObjectViewBtn() != null) {
            controller.getObjectViewBtn().setOnAction(act -> ObjectPreviewWindow.instance.stage.show());

            controller.getLoadBtn().setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            controller.getLoadBtn().setGraphic(GlyphConstants.OPEN_FOLDER_GLYPH);
            ContextMenu menu = new ContextMenu();
            menu.setAutoHide(true);
            menu.setAutoFix(true);
            MenuItem selectOption = new MenuItem("Clear all");
            selectOption.setOnAction(evt -> {
                controller.getSwatchFlowPane().getChildren().clear();
                swatchDataset.clear();

            });
            menu.getItems().add(selectOption);
            controller.getSwatchFlowPane().setOnContextMenuRequested(evt -> {
                menu.show(MainWindow.getSingleton().getStage(), evt.getScreenX(), evt.getScreenY());
            });

            controller.getLoadBtn().setOnAction(evt -> {
                if (!Client.gameLoaded.get()) {
                    FXDialogs.showError(MainWindow.getSingleton().getStage().getOwner(), "Error", "Please wait until the plugin has fully loaded before doing this!");
                    return;
                }
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

            controller.getSaveBtn().setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            controller.getSaveBtn().setGraphic(GlyphConstants.SAVE_GLYPH);
            controller.getSaveBtn().setOnAction(evt -> {
                List<ObjectDataset> objectDataset = Lists.newArrayList();
                for (BaseSwatch data : this.swatchDataset.values()) {
                    if (data instanceof ObjectSwatch) {
                        ObjectSwatch obj = ((ObjectSwatch) data);
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

    public List<BaseSwatch> getSwatches() {
        return this.swatchDataset.values().stream().collect(Collectors.toList());
    }

    public void addSwatch(BaseSwatch data) {
        Platform.runLater(() -> {
            ToggleButton btn = new ToggleButton();
            swatchDataset.put(btn, data);
            btn.setAlignment(Pos.CENTER);
            btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btn.setAlignment(Pos.CENTER);
            btn.setToggleGroup(group);
            btn.setGraphicTextGap(0);
            btn.setTextAlignment(TextAlignment.CENTER);
            btn.setOpaqueInsets(Insets.EMPTY);

            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            VBox.setVgrow(data.getGroup(), Priority.ALWAYS);
            vbox.setSpacing(8);
            switch (data.getType()) {
                case OBJECT:
                    btn.setMinSize(75, 75);
                    btn.setMaxSize(75, 75);
                    Label label = new Label();
                    label.setFont(Font.font(btn.getFont().getName(), 9.0));
                    label.setText(data.getText());
                    ((ObjectSwatch) data).setLabel(label);
                    vbox.getChildren().addAll(label);
                    controller.getSwatchScrollPane().addEventHandler(MouseEvent.ANY, evt -> {
                        if (KeyboardState.isKeyPressed(KeyCode.CONTROL)) {

                        }
                        log.info("evt {} ", evt.getEventType());
                        //log.info("Movement: {}", evt.getDeltaY());
                    });
                    DraggableNature nature = new DraggableNature(btn);
                    nature.setScrollPane(controller.getSwatchScrollPane());
                    nature.addListener((dragNature, mouseEvent, event) -> {
                        if (event == Draggable.DragEvent.DragStart) {
                            controller.getSwatchFlowPane().getChildren().stream().filter(node -> !dragNature.getDragNodes().contains(node))
                                    .forEach(node -> node.setOpacity(0.2));
                        }
                        if (event == Draggable.DragEvent.DragEnd) {
                            double baseX = 0, baseY = 0;
                            baseX = dragNature.getDragNodes().get(0).getBoundsInParent().getMinX();
                            baseY = dragNature.getDragNodes().get(0).getBoundsInParent().getMinY();
                            for (Node node : controller.getSwatchFlowPane().getChildren()) {
                                node.setOpacity(1);
                                node.setTranslateX(0);
                                node.setTranslateY(0);
                            }


                            for (Node swatch : controller.getSwatchFlowPane().getChildren()) {
                                if (dragNature.getDragNodes().contains(swatch))
                                    continue;
                                if (swatch.getBoundsInParent().contains(baseX, baseY)) {
                                    int oldIndex = controller.getSwatchFlowPane().getChildren().indexOf(dragNature.getDragNodes().get(0));
                                    int index = controller.getSwatchFlowPane().getChildren().indexOf(swatch);
                                    controller.getSwatchFlowPane().getChildren().removeAll(dragNature.getDragNodes());
                                    //index += (oldIndex < index ? 0 : 1);
                                    if (index < 0)
                                        index = 0;

                                    for (Node node : dragNature.getDragNodes())
                                        if (index >= controller.getSwatchFlowPane().getChildren().size())
                                            controller.getSwatchFlowPane().getChildren().add(node);
                                        else
                                            controller.getSwatchFlowPane().getChildren().add(index, node);
                                    break;
                                }
                            }
                        }
                    });
                    break;
                case OVERLAY:
                case UNDERLAY:
                    btn.setMinSize(37, 37);
                    btn.setPrefSize(37, 37);
                    btn.setMaxSize(37, 37);
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
                        Options.currentTool.set(SpawnObjectTool.IDENTIFIER);
                        Options.currentObject.set(((ObjectSwatch) data).getData());


                        break;
                    case OVERLAY:
                        Options.currentTool.set(PaintOverlayTool.IDENTIFIER);
                        Options.overlayPaintId.set(((OverlaySwatch) data).getIndex() + 1);
                        break;
                    case UNDERLAY:
                        Options.currentTool.set(PaintUnderlayTool.IDENTIFIER);
                        Options.underlayPaintId.set(((UnderlaySwatch) data).getIndex() + 1);

                        break;
                }


            };

            ContextMenu menu = new ContextMenu();
            MenuItem selectOption = new MenuItem("Select " + data.getText());
            selectOption.setOnAction(selectEvent);
            menu.getItems().add(selectOption);

            if (data.getType() == SwatchType.OBJECT) {
                ObjectSwatch objSwatch = ((ObjectSwatch) data);
                ObjectDataset dataset = objSwatch.getData();
                selectOption.setText(selectOption.getText() + " [" + dataset.getType() + "]");
                MenuItem renameOption = new MenuItem("Set nickname");
                renameOption.setOnAction(evt -> {
                    String newName = FXDialogs.showTextInput(MainWindow.getSingleton().getStage(), "Rename swatch", "Enter nickname for swatch", dataset.getName());
                    if (newName == null)
                        return;
                    dataset.setName(newName);
                    objSwatch.getLabel().setText(dataset.getId() + ": " + newName + "[" + dataset.getType() + "]");
                });
                menu.getItems().add(renameOption);
                MenuItem loadOption = new MenuItem("Load to object viewer");
                loadOption.setOnAction(evt -> {
                    ObjectPreviewWindow.instance.openObject(dataset);
                });
                menu.getItems().add(loadOption);
                MenuItem removeOption = new MenuItem("Remove");
                removeOption.setOnAction(evt -> {
                    swatchDataset.remove(btn);
                    controller.getSwatchFlowPane().getChildren().remove(btn);
                });
                menu.getItems().add(removeOption);
            } else if (data.getType() == SwatchType.OVERLAY) {
                if (((OverlaySwatch) data).getIndex() == 0) {
                    btn.setSelected(true);
                }
            } else if (data.getType() == SwatchType.UNDERLAY) {
                if (((UnderlaySwatch) data).getIndex() == 0) {
                    btn.setSelected(true);
                }
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
        for (Entry<ToggleButton, BaseSwatch> entry : swatchDataset.entrySet()) {
            if (entry.getValue() instanceof UnderlaySwatch) {
                UnderlaySwatch swatch = (UnderlaySwatch) entry.getValue();
                if (swatch.getIndex() == underlayId) {
                    entry.getKey().fire();
                    return;
                }
            }
        }
    }

    public void selectByOverlay(int overlayId) {
        for (Entry<ToggleButton, BaseSwatch> entry : swatchDataset.entrySet()) {
            if (entry.getValue() instanceof OverlaySwatch) {
                OverlaySwatch swatch = (OverlaySwatch) entry.getValue();
                if (swatch.getIndex() == overlayId) {
                    entry.getKey().fire();
                    return;
                }
            }
        }
    }

    public void selectOverlayPair(int pairNum) {

        int[][] pairs = {{0, 1}, {2}, {3, 4}, {5, 6}, {7}, {8, 9}, {10, 11}, {12}};
        int[] pair = pairs[pairNum];

        ToggleButton btn1 = (ToggleButton) overlayShapeGroup.getToggles().get(pair[0]);
        if (pair.length == 1) {
            btn1.fire();
        } else {
            ToggleButton btn2 = (ToggleButton) overlayShapeGroup.getToggles().get(pair[1]);
            log.info("Selected: {}", overlayShapeGroup.getSelectedToggle());
            if (overlayShapeGroup.getSelectedToggle() == btn1) {
                btn2.fire();
            } else {
                btn1.fire();
            }
        }

    }

    public void setOverlayShape(int overlayShape) {
        if (overlayShape >= 0) {
            Object obj = overlayShapeGroup.getProperties().get(overlayShape);
            if (obj instanceof ToggleButton) {
                ToggleButton btn = (ToggleButton) obj;
                if (overlayShapeGroup.getSelectedToggle() == btn) {
                    btn = (ToggleButton) btn.getProperties().getOrDefault("otherButton", btn);
                }
                btn.fire();
            }
        }
    }

    public void setOverlayShapeGroup(ToggleGroup tg) {
        this.overlayShapeGroup = tg;

    }

}
