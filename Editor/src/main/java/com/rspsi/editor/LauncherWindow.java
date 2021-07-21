package com.rspsi.editor;

import com.google.common.io.Files;
import com.rspsi.editor.controllers.LauncherController;
import com.rspsi.editor.controls.WindowControls;
import com.rspsi.editor.resources.ResourceLoader;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.options.Config;
import com.rspsi.util.ChangeListenerUtil;
import com.rspsi.util.FXUtils;
import com.rspsi.util.RetentionFileChooser;
import com.rspsi.util.Settings;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.compress.utils.Lists;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Getter
public class LauncherWindow extends Application {

	private static boolean loadInstant;
	private static boolean runConsole;
	private static boolean loadDefault;

	@Getter
	private static LauncherWindow singleton;

	private Stage primaryStage;
	
	private LauncherController controller;
	private List<String> oldCachePaths;

	@Override
	public void start(Stage primaryStage) throws IOException {

		singleton = this;
		this.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loadscreen.fxml"));
		controller = new LauncherController();
		loader.setController(controller);
		Parent content = loader.load();
		Scene scene = new Scene(content);

		scene.setFill(Color.TRANSPARENT);
		
		primaryStage.setTitle("RSPSi Map Editor Launcher");
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());

		primaryStage.show();
		primaryStage.sizeToScene();
		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();


		Settings.loadSettings();

		String cacheLoc = Settings.getSetting("cacheLocation", Config.cacheLocation.get());
	
		oldCachePaths = Settings.getSetting("oldCache", Lists.newArrayList());
		fillOldPaths();
		
		controller.getCacheLocation().getEditor().setText(new File(cacheLoc).getAbsolutePath() + File.separator);
		
		ChangeListenerUtil.addListener(primaryStage::sizeToScene, controller.getPluginTitlePane().expandedProperty());
		
		
		controller.getDisablePluginButton().setOnAction(evt -> {
			String pluginName = controller.getEnabledPlugins().getFocusModel().getFocusedItem();
			if(pluginName != null) {
				File oldPluginFile = new File(PLUGINS_PATH + "active" + File.separator + pluginName + ".jar");
				File newPluginFile = new File(PLUGINS_PATH + "inactive" + File.separator + pluginName + ".jar");
				try {
					Files.copy(oldPluginFile, newPluginFile);
					oldPluginFile.delete();
					populatePlugins();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		controller.getCancelButton().setOnAction(evt -> primaryStage.hide());
		
		controller.getEnablePluginButton().setOnAction(evt -> {
			String pluginName = controller.getDisabledPlugins().getFocusModel().getFocusedItem();
			if(pluginName != null) {

				File inactiveFolder = new File(PLUGINS_PATH + "inactive" + File.separator);
				File activeFolder = new File(PLUGINS_PATH + "active" + File.separator);
				
				File oldPluginFile = new File(inactiveFolder,  pluginName + ".jar");
				File newPluginFile = new File(activeFolder, pluginName + ".jar");
				try {
					if(newPluginFile.exists()){
						newPluginFile.delete();
					}
					if(!activeFolder.exists())
						activeFolder.mkdir();
					for(File active : activeFolder.listFiles()){
						Files.move(active, new File(inactiveFolder, active.getName()));
					}
					inactiveFolder.mkdirs();
					activeFolder.mkdirs();
					Files.copy(oldPluginFile, newPluginFile);
					oldPluginFile.delete();
					populatePlugins();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		controller.getBrowseButton().setOnAction(evt -> {
			File f = RetentionFileChooser.showOpenFolderDialog(primaryStage, null);
			if(f != null) {
				String oldPath = controller.getCacheLocation().getEditor().getText();
				String newPath = f.getAbsolutePath() + File.separator;

				putOldPath(oldPath);
				putOldPath(newPath);
				
				controller.getCacheLocation().getEditor().setText(newPath);
				
			}
		});

		controller.getLaunchButton().setOnAction(evt -> {
			try {
				launchMainWindow();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		
		populatePlugins();
		WindowControls controls = WindowControls.addWindowControlsFixed(primaryStage, controller.getTopBar(), controller.getControlBox());
		primaryStage.sizeToScene();

		if(loadInstant){
			launchMainWindow();
		}

	}

	public void launchMainWindow() throws IOException {
		if(runConsole){

			ConsoleWindow consoleWindow = new ConsoleWindow();
			consoleWindow.start(new Stage());
		}

		updateCacheLocation();
		primaryStage.hide();
		MainWindow window = new MainWindow();
		Stage otherStage = new Stage();
		otherStage.setX(primaryStage.getX());
		otherStage.setY(primaryStage.getY());
		window.start(otherStage);
	}

	private void updateCacheLocation() {
		String cacheLoc = Settings.getSetting("cacheLocation", Config.cacheLocation.get());
		Config.cacheLocation.set(controller.getCacheLocation().getEditor().getText());
		Settings.properties.put("cacheLocation", Config.cacheLocation.get());
		Settings.properties.put("lastCacheLocation", cacheLoc);
	}

	private void putOldPath(String path) {
		if(!oldCachePaths.contains(path)) {
			oldCachePaths.add(0, path);
			Settings.putSetting("oldCache", oldCachePaths);
			fillOldPaths();
		}
	}
	
	private void fillOldPaths() {
		controller.getCacheLocation().getItems().clear();
		controller.getCacheLocation().getItems().addAll(oldCachePaths);
	}
	
	public void populatePlugins() {
		controller.getEnabledPlugins().getItems().clear();
		controller.getDisabledPlugins().getItems().clear();
		
		controller.getEnabledPlugins().getItems().addAll(getPlugins("active"));
		controller.getDisabledPlugins().getItems().addAll(getPlugins("inactive"));
	}
	
	private static final String PLUGINS_PATH = "plugins" + File.separator;
	
	private static List<String> getPlugins(String folderName){
		List<String> list = Lists.newArrayList();
		File folder = new File(PLUGINS_PATH + folderName);
		if(folder.exists()) {
			for(File f : folder.listFiles()) {
				list.add(f.getName().replaceAll(".jar", "").trim());
			}
		}
		list.sort(Comparator.naturalOrder());
		return list;
	}

	public static void main(String[] args) {

		try {
			Options options = new Options();
			options.addOption("i", "instant", false, "Loads all the previous configuration and launches into the editor instantly");

			options.addOption("c", "console", false, "Launches a console window to interact with the editor");

			options.addOption("lc", "load_coordinates", true, "Loads the specified coordinates on editor load");

			options.addOption("ls", "load_size", true, "Defines the default X and Y size for the load_coordinates argument");

			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse( options, args);

			if(cmd.hasOption("instant")){
				loadInstant = true;
 			}
			if(cmd.hasOption("console")){
				runConsole = true;
			}
			int sizeX = 1;
			int sizeY = 1;

			if(cmd.hasOption("load_size")){
				String[] xy = cmd.getOptionValue("load_size").split(",");
				if(xy.length < 2) {
					log.warn("Invalid arguments for default coordinate size.");
					log.warn("Format is load_size=width,height");
				} else {
					sizeX = Integer.parseInt(xy[0]);
					sizeY = Integer.parseInt(xy[1]);
				}
			}
			if(cmd.hasOption("load_coordinates")){
				String[] xy = cmd.getOptionValue("load_coordinates").split(",");

				if(xy.length < 2) {
					log.warn("Invalid arguments for default coordinate loading.");
					log.warn("Format is load_coordinates=x,y");
				} else {
					int finalSizeX = sizeX;
					int finalSizeY = sizeY;
					Client.runLater.add(() ->
							Client.getSingleton().loadCoordinates(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), finalSizeX, finalSizeY)
					);
				}


			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		launch(args);
	}

}
