package com.rspsi.editor;

import com.google.common.collect.Lists;
import com.jogamp.nativewindow.util.Rectangle;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.opengl.util.Animator;
import com.rspsi.editor.resources.ResourceLoader;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.joml.Vector3i;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ConsoleWindow extends Application {

    @FXML
    private BorderPane pane;

    @FXML
    private TextArea consoleOutput;

    @FXML
    private TextArea consoleInput;


    Parent content;
    private List<String> recentCommands = Lists.newArrayList();
    private int commandPtr = 0;
    private String tempCommand = "";

    @Override
    public void start(Stage primaryStage) throws IOException {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/console.fxml"));

            loader.setController(this);
            content = loader.load();
            Scene scene = new Scene(content);

            primaryStage.setTitle("RSPSi Console");
           // primaryStage.initStyle(StageStyle.DECORATED);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());

            primaryStage.show();

            log.info("Loaded console!");

            consoleInput.addEventHandler(KeyEvent.ANY, (evt) -> {
                if (evt.getCode() == KeyCode.ENTER) {
                    String textInput = consoleInput.getText();
                    if(textInput.isEmpty())
                        return;
                    parseInput(textInput);
                    recentCommands.add(textInput);
                    commandPtr = recentCommands.size() - 1;
                    tempCommand = "";
                    consoleInput.clear();
                } else if(evt.getCode() == KeyCode.UP){
                    if(commandPtr == 0)
                        return;
                    commandPtr--;
                    tempCommand = consoleInput.getText();
                    consoleInput.setText(recentCommands.get(commandPtr));

                } else if(evt.getCode() == KeyCode.DOWN){
                    if(commandPtr == recentCommands.size()){
                        return;
                    }
                    commandPtr++;
                    if(commandPtr == recentCommands.size() && !tempCommand.isEmpty()){
                        consoleInput.setText(tempCommand);
                        return;
                    }
                    consoleInput.setText(recentCommands.get(commandPtr));
                }
            });
    }

    public void parseInput(String commandEntry){
        commandEntry = commandEntry.trim();
        log.info("CMD: {}", commandEntry);
        if(commandEntry.equalsIgnoreCase("clear")){
            consoleOutput.clear();
        } else if(commandEntry.equalsIgnoreCase("ping")){
            appendConsole("Pong!");
        } else if(commandEntry.equalsIgnoreCase("setsize")){
            MainWindow.glEditorWindow.window.setSize(300, 300);
            appendConsole("Set size!");
        } else if(commandEntry.equalsIgnoreCase("visible")){
            MainWindow.glEditorWindow.window.setVisible(true, true);
            appendConsole("Set visible!");
        } else if(commandEntry.equalsIgnoreCase("print")){
            appendConsole(Objects.toString(MainWindow.glEditorWindow.window));
           // consoleOutput.appendText(ReflectionUtil.getValues(MainWindow.getSingleton().glEditorWindow.window).entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining("\n")));
        } else if(commandEntry.equalsIgnoreCase("viewport")){
            MainWindow.glEditorWindow.camera.setViewport(new Rectangle(0, 0, 300, 300));
        } else if(commandEntry.equalsIgnoreCase("animout")){
            Animator animator = MainWindow.glEditorWindow.animator;

            animator.setUpdateFPSFrames(120, System.out);

        } else if(commandEntry.equalsIgnoreCase("animnone")){
            Animator animator = MainWindow.glEditorWindow.animator;

            animator.setUpdateFPSFrames(120, null);

        }  else if(commandEntry.equalsIgnoreCase("addwindow")){
            NewtCanvasJFX newtCanvasJFX = new NewtCanvasJFX(MainWindow.glEditorWindow.window);
            pane.getChildren().add(newtCanvasJFX);
            BorderPane.setAlignment(newtCanvasJFX, Pos.TOP_CENTER);

            appendConsole("Added window to children!");


        }  else if(commandEntry.equalsIgnoreCase("printobjs")){
            val tile = MainWindow.getSingleton().getClientInstance().sceneGraph.tiles.values().stream().filter(t -> !t.worldObjects.isEmpty()).findAny().orElse(null);

            if(tile != null) {
                appendConsole(tile.worldPos.toString());
                tile.worldObjects.forEach(worldObject -> {
                    appendConsole(worldObject.toString());
                });
            }

        }  else if(commandEntry.equalsIgnoreCase("textures")){
            MainWindow.glEditorWindow.uploadTextures();
            appendConsole("Uploaded textures!");


        } else {
            appendConsole("Command " + commandEntry + " not found!");
        }
    }

    public void appendConsole(String text){
        consoleOutput.appendText(text + "\n");
    }
}


