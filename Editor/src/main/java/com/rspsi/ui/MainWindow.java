package com.rspsi.ui;

import com.rspsi.dialogs.RenderDistanceDialog;
import com.rspsi.options.KeyboardState;
import com.rspsi.util.*;
import javafx.beans.value.ChangeListener;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.displee.util.GZIPUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.major.map.RenderFlags;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.jagex.Client;
import com.jagex.cache.def.Floor;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.chunk.Chunk;
import com.jagex.draw.textures.Texture;
import com.jagex.entity.model.Mesh;
import com.jagex.entity.model.MeshLoader;
import com.jagex.map.SceneGraph;
import com.jagex.map.object.DefaultWorldObject;
import com.jagex.util.BitFlag;
import com.jagex.util.ColourUtils;
import com.jagex.util.MultiMapEncoder;
import com.jagex.util.ObjectKey;
import com.rspsi.controllers.MainController;
import com.rspsi.controls.RemappingTool;
import com.rspsi.controls.SwatchControl;
import com.rspsi.datasets.ObjectDataset;
import com.rspsi.dialogs.TileCopyDialog;
import com.rspsi.dialogs.TileDeleteDialog;
import com.rspsi.dialogs.TileExportDialog;
import com.rspsi.game.CanvasPane;
import com.rspsi.game.listeners.GameKeyListener;
import com.rspsi.game.listeners.GameMouseListener;
import com.rspsi.game.map.MapView;
import com.rspsi.game.save.AutoSaveJob;
import com.rspsi.game.save.TileChange;
import com.rspsi.core.misc.StatusUpdate;
import com.rspsi.core.misc.ToolType;
import com.rspsi.core.misc.XTEAManager;
import com.rspsi.options.Config;
import com.rspsi.options.Options;
import com.rspsi.plugins.ui.ApplicationPluginLoader;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.swatches.BaseSwatch;
import com.rspsi.swatches.OverlaySwatch;
import com.rspsi.swatches.UnderlaySwatch;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MainWindow extends Application {

	private static MainWindow singleton;

	static {

		//Faster tooltips
		try {
			Tooltip obj = new Tooltip();
			Class<?> clazz = obj.getClass().getDeclaredClasses()[0];
			Constructor<?> constructor = clazz.getDeclaredConstructor(Duration.class, Duration.class, Duration.class,
					boolean.class);
			constructor.setAccessible(true);
			Object tooltipBehavior = constructor.newInstance(new Duration(50), // open
					new Duration(5000), // visible
					new Duration(200), // close
					false);
			Field fieldBehavior = obj.getClass().getDeclaredField("BEHAVIOR");
			fieldBehavior.setAccessible(true);
			fieldBehavior.set(obj, tooltipBehavior);
		} catch (Exception e) {
			// Logger.error(e);
		}
	}


	private Client clientInstance;

	private Scene scene;

	private Stage stage;

	private MainController controller;

	@Setter
	public SwatchControl objectSwatch, overlaySwatch, underlaySwatch;

	private ObjectPreviewWindow objectPreviewWindow;
	private PickCoordinatesWindow pickCoords;
	private PickHashWindow pickHash;
	private MultiRegionMapWindow fullMapView;
	private SelectFilesWindow selectFiles;
	private SelectPackWindow selectPack;
	private SelectXTEAWindow selectXTEA;
	private RemappingTool remappingTool;

	private Mesh errorMesh;

	public void fillSwatches() {

		for (int idx = 0; idx < FloorDefinitionLoader.getUnderlayCount(); idx++) {
			Floor floor = FloorDefinitionLoader.getUnderlay(idx);
			if(floor == null)
				continue;
			Group g = new Group();
			String label = "";
			label = "[" + idx + "] rgb(" + ColourUtils.getRed(floor.getRgb()) + "," + ColourUtils.getGreen(floor.getRgb()) + ","
					+ ColourUtils.getBlue(floor.getRgb()) + ")";
			Rectangle rect = new Rectangle();
			rect.setWidth(32);
			rect.setHeight(32);
			Color c = ColourUtils.getColor(floor.getRgb());
			// c = c.deriveColor(floor.getWeightedHue(), floor.getSaturation() / 256.0,
			// floor.getLuminance() / 256.0, 1.0);
			rect.setFill(c);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(1);
			g.getChildren().add(rect);

			BaseSwatch data = new UnderlaySwatch(g, label, idx);
			underlaySwatch.addSwatch(data);
		}
		for (int idx = 0; idx < FloorDefinitionLoader.getOverlayCount(); idx++) {
			Floor floor = FloorDefinitionLoader.getOverlay(idx);

			if(floor == null)
				continue;
			Group g = new Group();
			String label = "";
			if (floor.getTexture() == -1 || floor.getTexture() > TextureLoader.instance.count()) {
				continue;
			} else {
				label = "[" + idx + "] texture(" + floor.getTexture() + ")";
				Texture texture = TextureLoader.getTexture(floor.getTexture());
				if(texture == null)
					continue;
				ImageView imgView = new ImageView(texture.getAsFXImage());
				imgView.setPreserveRatio(true);
				imgView.setSmooth(true);
				imgView.setFitHeight(32);
				imgView.setFitWidth(32);
				g.getChildren().add(imgView);
			}
			BaseSwatch data = new OverlaySwatch(g, label, idx);
			overlaySwatch.addSwatch(data);
		}
		for (int idx = 0; idx < FloorDefinitionLoader.getOverlayCount(); idx++) {

			Floor floor = FloorDefinitionLoader.getOverlay(idx);

			if(floor == null)
				continue;
			Group g = new Group();
			String label = "";
			if (floor.getTexture() == -1 || floor.getTexture() >= TextureLoader.instance.count()) {
				label = "[" + idx + "] rgb(" + ColourUtils.getRed(floor.getRgb()) + "," + ColourUtils.getGreen(floor.getRgb()) + "," + ColourUtils.getBlue(floor.getRgb()) + ")";
				Rectangle rect = new Rectangle();
				rect.setWidth(32);
				rect.setHeight(32);
				Color c = ColourUtils.getColor(floor.getRgb());
				rect.setFill(c);
				rect.setStroke(Color.BLACK);
				rect.setStrokeWidth(1);
				g.getChildren().add(rect);
			} else {
				continue;
			}
			BaseSwatch data = new OverlaySwatch(g, label, idx);
			overlaySwatch.addSwatch(data);
		}

	}

	@Override
	public void start(Stage primaryStage) {
		try {
			singleton = this;
			stage = primaryStage;
			Platform.setImplicitExit(true);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_test4.fxml"));
			controller = new MainController();
			loader.setController(controller);
			Parent content = loader.load();
			double windowWidth = (Double) Settings.properties.getOrDefault("window_width",1240.0);
			double windowHeight = (Double) Settings.properties.getOrDefault("window_height",800.0);
			scene = new Scene(content,windowWidth,windowHeight);

			scene.setFill(Color.TRANSPARENT);

			primaryStage.setTitle("RSPSi Map Editor 1.16.1");
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setScene(scene);
			primaryStage.getIcons().addAll(ResourceLoader.getSingleton().getIcons());

			ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
				if((boolean) Settings.properties.getOrDefault("remember_size",true) == true) {
					Settings.properties.put("window_width", primaryStage.getWidth());
					Settings.properties.put("window_height", primaryStage.getHeight());
					Settings.saveSettings();
				}
			};

			ChangeListener<Number> stageLocationListener = (observable, oldValue, newValue) -> {
				if((boolean) Settings.properties.getOrDefault("remember_location",false) == true) {
					Settings.properties.put("windowLocationWidth", primaryStage.getX());
					Settings.properties.put("windowLocationHeight", primaryStage.getY());
					Settings.saveSettings();
				}
			};

			double windowLocationWidth = (Double) Settings.properties.getOrDefault("windowLocationWidth",0.0);
			double windowLocationHeight = (Double) Settings.properties.getOrDefault("windowLocationHeight",0.0);

			if(windowLocationWidth == 0.0 && windowLocationHeight == 0.0) {
				primaryStage.centerOnScreen();
			} else {
				int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
				int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
				if (windowLocationWidth <= screenWidth && windowLocationHeight <= screenHeight) {
					primaryStage.setX(windowLocationWidth);
					primaryStage.setY(windowLocationHeight);
				} else {
					primaryStage.centerOnScreen();
				}
			}

			primaryStage.widthProperty().addListener(stageSizeListener);
			primaryStage.heightProperty().addListener(stageSizeListener);
			primaryStage.xProperty().addListener(stageLocationListener);
			primaryStage.yProperty().addListener(stageLocationListener);

			primaryStage.show();

			FXUtils.centerStage(primaryStage);

			controller.onLoad(this);

			boolean shutdownCorrectly = Settings.getSetting("shutdown", false);
			Settings.clearSetting("shutdown");

			int renderDistance = Settings.getSetting("renderDistance", Options.renderDistance.get());

			Options.renderDistance.set(renderDistance);

			boolean loadAutosave = false;
			File autosavePath = Paths.get(System.getProperty("user.home"), ".rspsi", "autosave").toFile();

			String lastCacheLoc = Settings.getSetting("lastCacheLocation", "");
			if(!shutdownCorrectly) {
				System.out.println("CRASH DETECTED!");

				if(autosavePath.exists() && autosavePath.list().length > 0) {

					//Just incase there was a crash mid autosave
					File packFile = new File(autosavePath, "autosave.pack");

					File objectFileBackup =  new File(autosavePath, "autosave.pack.bk");

					//restore backups
					if(objectFileBackup.exists()) {
						Files.copy(objectFileBackup.toPath(), packFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}


					String response = FXDialogs.showConfirm(primaryStage,"Application did not shut down correctly!",
							"We have detected that your last shutdown did not complete correctly.\nWould you like to load the last autosave?",
							"Yes", "No");
					if(response.equalsIgnoreCase("yes")) {
						loadAutosave = true;
					}
				}
			}

			MapView mapView = new MapView();
			
			selectXTEA = new SelectXTEAWindow();
			selectXTEA.start(new Stage());

			TileExportDialog export = new TileExportDialog();
			export.start(new Stage());

			TileDeleteDialog deleteWindow = new TileDeleteDialog();
			deleteWindow.start(new Stage());

			TileCopyDialog copyWindow = new TileCopyDialog();
			copyWindow.start(new Stage());

			pickCoords = new PickCoordinatesWindow();
			pickCoords.start(new Stage());

			pickHash = new PickHashWindow();
			pickHash.start(new Stage());

			selectFiles = new SelectFilesWindow();
			selectFiles.start(new Stage());

			selectPack = new SelectPackWindow();
			selectPack.start(new Stage());

			ContactMeWindow contactMe = new ContactMeWindow();
			contactMe.start(new Stage());

			controller.getContactMeBtn().setOnAction(evt -> {
				contactMe.showAndWait();
			});

			controller.getShowMapIndexEditor().setOnAction(evt -> {
				mapView.setVisible(true);
				mapView.initTiles();
			});



			ChangeListenerUtil.addRangeListener(Options.rotation, 0, 3, true);

			ChangeListenerUtil.addListener(() -> {
				clientInstance.getCurrentChunk().mapRegion.updateTiles();
				Client.updateChunkTiles();
				SceneGraph.minimapUpdate = true;
			}, Options.disableBlending, Options.showOverlay, Options.showObjects);

			controller.getReloadSwatchesBtn().setOnAction(evt -> {
				overlaySwatch.clear();
				underlaySwatch.clear();
				fillSwatches();
			});
			controller.getReloadModelsBtn().setOnAction(evt -> MeshLoader.getSingleton().clearAll());
			controller.getCopySelectedTilesBtn().setOnAction(evt -> {
				if(Options.currentTool.get() == ToolType.SELECT_OBJECT) {
					SceneGraph.onCycleEnd.add(() -> {
						Client.getSingleton().sceneGraph.copyObjects();
					});

					controller.getPasteTilesBtn().setDisable(false);
				} else {
					copyWindow.show();
					controller.getPasteTilesBtn().setDisable(false);
				}
			});

			controller.getPasteTilesBtn().setOnAction(evt -> {

				SceneGraph scene = clientInstance.sceneGraph;
				scene.resetTiles();
				Options.currentTool.set(ToolType.IMPORT_SELECTION);

			});

			SceneGraph.undoList.addListener((ListChangeListener<TileChange>) listener -> {
				controller.getUndoMenuItem().disableProperty().set(SceneGraph.undoList.isEmpty());
			});

			SceneGraph.redoList.addListener((ListChangeListener<TileChange>) listener -> {
				controller.getRedoMenuItem().disableProperty().set(SceneGraph.redoList.isEmpty());
			});

			controller.getUndoMenuItem().setOnAction(evt -> SceneGraph.undo());
			controller.getRedoMenuItem().setOnAction(evt -> SceneGraph.redo());

			controller.getDeleteSelectedTilesBtn().setOnAction(evt -> TileDeleteDialog.instance.show());

			RenderDistanceDialog renderDistanceDialog = new RenderDistanceDialog();
			renderDistanceDialog.start(new Stage());
			controller.getChangeViewDist().setOnAction(evt -> {
				renderDistanceDialog.show();
			});

			controller.getAddObjectToSwatchBtn().setOnAction(evt ->{
				
				if(!clientInstance.sceneGraph.selectedObjects.isEmpty()) {
					for(DefaultWorldObject selectedObject : clientInstance.sceneGraph.selectedObjects) {
						ObjectKey key = selectedObject.getKey();
						int id = key.getId();
						int type = key.getType();
						ObjectDefinition def = ObjectDefinitionLoader.lookup(id);

						ObjectDataset set = new ObjectDataset(id, type, def.getName());
						ObjectPreviewWindow.instance.loadToSwatches(set);
					}
				}
			});
			objectPreviewWindow = new ObjectPreviewWindow(objectSwatch);
			objectPreviewWindow.start(new Stage());
			
			//ModelPreviewWindow modelPrev = new ModelPreviewWindow();
			//modelPrev.start(new Stage());

			fullMapView = new MultiRegionMapWindow();
			fullMapView.start(new Stage());
			
			
			remappingTool = new RemappingTool();
			remappingTool.start(new Stage());
			
			controller.getShowRemapperBtn().setOnAction(evt -> {
				remappingTool.show();
				if(remappingTool.valid()) {
					remappingTool.doRemap();
				}
			});

			controller.getShowFullMap().setOnAction(evt -> { 
				fullMapView.show();
				
				SceneGraph.minimapUpdate = true;
			});

			controller.getExportTilesBtn().setOnAction(evt -> export.show());

			controller.getShowObjectViewBtn().setOnAction(evt -> objectPreviewWindow.stage.show());


			clientInstance = Client.initialize(controller.getGamePane().widthProperty().intValue(),
					controller.getGamePane().heightProperty().intValue());

			clientInstance.loadCache(Paths.get(Config.cacheLocation.get()));

			CanvasPane gamePane = new CanvasPane(clientInstance.getGameCanvas());

			GameKeyListener gameKeyListener = new GameKeyListener(clientInstance);
			clientInstance.getGameCanvas().addEventHandler(MouseEvent.ANY, new GameMouseListener(clientInstance));
			clientInstance.getGameCanvas().addEventHandler(ScrollEvent.ANY, new GameMouseListener(clientInstance));
			clientInstance.getGameCanvas().addEventHandler(KeyEvent.ANY, gameKeyListener);

			clientInstance.fullMapVisible.bind(fullMapView.visibleProperty());
		
			
			if(clientInstance.getCache() != null) {
				if(!clientInstance.getCache().getIndexedFileSystem().is317()) {
					String xteaLocation = Settings.getSetting("xteaLoc", "");
					Consumer<Boolean> pickXTEA = (showError) -> {
						String currentXTEALoc = Settings.getSetting("xteaLoc", "");
						selectXTEA.setLocation(currentXTEALoc);
						selectXTEA.show();
						if(selectXTEA.valid()) {
							XTEAManager.loadFromJSON(new File(selectXTEA.getJsonLocation()));
							Settings.putSetting("xteaLoc", selectXTEA.getJsonLocation());
							log.info("Loaded {} XTEAs", XTEAManager.getMaps().size());
						} else if(showError) {
							FXDialogs.showError(primaryStage,"Error loading XTEAS", "You need to select an XTEA json file otherwise maps may fail to load!");
						}
					};
	
					if(xteaLocation.isEmpty() || !lastCacheLoc.equals(Config.cacheLocation.get())) {
						pickXTEA.accept(true);
					} else {
						XTEAManager.loadFromJSON(new File(xteaLocation));
					}
					
					MenuItem changeXTEALoc = new MenuItem("Change XTEAs");
					changeXTEALoc.setOnAction(evt -> pickXTEA.accept(false));
					controller.getFileMenu().getItems().add(controller.getFileMenu().getItems().size() - 2, changeXTEALoc);
				}
			}
			primaryStage.addEventHandler(KeyEvent.ANY, gameKeyListener);
			primaryStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
				if(!newValue){
					log.info("Lost focus!");
					KeyboardState.reset();
					SceneGraph.setMouseIsDown(false);
					Arrays.fill(clientInstance.keyStatuses, 0);
					//clientInstance.visible = false;
				} else {
					log.info("Gained focus!");
					//clientInstance.visible = true;
				}
			});


			SceneGraph.setMouseIsDown(true);
			SceneGraph.setMouseIsDown(false);

			clientInstance.visible = true;

			controller.getGamePane().getChildren().add(0, gamePane);
			controller.getMapPane().getChildren().add(new CanvasPane(clientInstance.mapCanvas));
			clientInstance.errorDisplayed.addListener((observable, oldValue, newValue) -> controller.getReturnToLauncher().setVisible(newValue.booleanValue()));
			controller.getReturnToLauncher().setOnAction(evt -> {
				LauncherWindow.getSingleton().getPrimaryStage().show();
				LauncherWindow.getSingleton().populatePlugins();
				singleton = null;
				if (clientInstance != null) {
					try {
						clientInstance.exit();
					} catch(Exception ex) {
						ex.printStackTrace();

					}
				}
				primaryStage.close();
			});
			ContextMenu menu = new ContextMenu();
			menu.autoHideProperty().set(true);
			MenuItem item = new MenuItem("Save to file");
			item.setOnAction(evt -> {
				File f = RetentionFileChooser.showSaveDialog(FilterMode.PNG);
				if(f != null) {
					try {
						System.out.println(f.getAbsolutePath());
						clientInstance.saveMinimapImage(f);
					} catch (Exception e) {
						e.printStackTrace();
						FXDialogs.showError(primaryStage, "Error while loading saving image", "There was a failure while attempting to save\nthe minimap to the selected file.");

					}
				}
			});
			menu.getItems().addAll(item);
			controller.getMapPane().setOnContextMenuRequested(evt -> {
				menu.show(getStage(), evt.getScreenX(), evt.getScreenY());
			});

			controller.getFixHeightsBtn().setOnAction(evt -> {
				String result = FXDialogs.showConfirm(primaryStage, "Are you sure?", "This fix will set all heights on plane 1 and above based on "
						+ "the tile height at z = 0. This may cause a few issues for some tiles you will have to fix yourself. \n\nWould you like to continue?", 
						"Yes", "No");
				if(result.equalsIgnoreCase("Yes")) {
					for(int plane = 1;plane<4;plane++) {
						for(int absX = 0;absX<clientInstance.sceneGraph.width;absX++) {
							for(int absY = 0;absY<clientInstance.sceneGraph.length;absY++) {
								clientInstance.mapRegion.tileHeights[plane][absX][absY] = clientInstance.mapRegion.tileHeights[plane - 1][absX][absY] - 240;
							}
						}
					}
					//chunk.mapRegion.tileHeights = newHeights;


					clientInstance.sceneGraph.updateHeights(0, 0, clientInstance.sceneGraph.width, clientInstance.sceneGraph.length);
				}
			});
			controller.getForceMapUpdateBtn().setOnAction(evt -> {
				int positionX = clientInstance.xCameraPos;
				int positionY = clientInstance.yCameraPos;
				
				byte[] packData = MultiMapEncoder.encode(Lists.newArrayList(clientInstance.chunks));
				Client.runLater.add(() ->{
					clientInstance.loadChunks(MultiMapEncoder.decode(packData));
					fullMapView.resizeMap();
					clientInstance.xCameraPos = positionX;
					clientInstance.yCameraPos = positionY;
				});
			});
			primaryStage.setOnHiding((we) -> {

				Settings.putSetting("shutdown", true);
				if (clientInstance != null) {
					try {
						clientInstance.exit();
					} catch(Exception ex) {
						ex.printStackTrace();

					}
				}
				if(singleton != null) {
					Platform.exit();
					System.exit(0);
				}
			});

			ChangeListenerUtil.addListener((oldVal, newVal) -> {
				if(Options.currentTool.get() == ToolType.SELECT_OBJECT){
					clientInstance.sceneGraph.rotateSelectedObjects(oldVal - newVal);
				}
				SceneGraph.onCycleEnd.add(() -> Client.getSingleton().sceneGraph.forceMouseInTile());
			}, Options.rotation);

			controller.getCopyTileFlags().setOnAction(evt -> {
				BitFlag flag = clientInstance.sceneGraph.getSelectedFlag();

				controller.getUnwalkableCheck().setSelected(flag.flagged(RenderFlags.BLOCKED_TILE));
				controller.getBridgeCheck().setSelected(flag.flagged(RenderFlags.BRIDGE_TILE));
				controller.getForceLowestCheck().setSelected(flag.flagged(RenderFlags.FORCE_LOWEST_PLANE));
				controller.getDrawOnLowerZCheck().setSelected(flag.flagged(RenderFlags.RENDER_ON_LOWER_Z));
				controller.getDisableRenderCheck().setSelected(flag.flagged(RenderFlags.DISABLE_RENDERING));

			});

			controller.getCopyTileHeights().setOnAction(evt -> {
				int height = clientInstance.sceneGraph.getSelectedHeight();
				System.out.println(height);
				if(height <= 0) {
					controller.getHeightLevelSlider().setValue(-height);
				} else {
					FXDialogs.showError(primaryStage,"Error while loading tile height", "There was a failure while attempting to grab\ntile height from the selected tile.");
				}
			});

			controller.getGetOverlayFromTile().setOnAction(evt -> {
				if(clientInstance.sceneGraph != null) {
					int overlayId = clientInstance.sceneGraph.getSelectedOverlay();
					int overlayShape = clientInstance.sceneGraph.getSelectedOverlayShape();
					log.info("id {} shape {}", overlayId, overlayShape);
					if(overlayId > 0) {

						overlaySwatch.setOverlayShape(overlayShape + 1);
						overlaySwatch.selectByOverlay(overlayId - 1);
					}
				}
			});

			controller.getGetUnderlayFromTile().setOnAction(evt -> {
				if(clientInstance.sceneGraph != null) {
					int underlayId = clientInstance.sceneGraph.getSelectedUnderlay();
					if(underlayId > 0) {
						underlaySwatch.selectByUnderlay(underlayId - 1);
					}
				}
			});



			controller.getImportTilesBtn().setOnAction(evt -> {
				File f = RetentionFileChooser.showOpenDialog(primaryStage, FilterMode.JMAP);
				if (f != null) {
					try {
						clientInstance.sceneGraph.importSelection(f);
					} catch (IOException e) {
						e.printStackTrace();
						FXDialogs.showError(primaryStage,"Error while loading prefab!",
								"There was an error while reading the selected file.");
					} catch (Exception e) {
						e.printStackTrace();
						FXDialogs.showError(primaryStage,"Error while parsing prefab!",
								"There was an error while parsing the selected file.");
					}
				}
			});



			ChangeListenerUtil.addListener(() -> {
				SceneGraph.onCycleEnd.add(() -> {
					Client.updateChunkTiles();
					SceneGraph.minimapUpdate = true;
				});
			}, Options.showHiddenTiles);

			ChangeListenerUtil.addListener(() -> {
				SceneGraph.onCycleEnd.add(() -> {
					Client.updateChunkTiles();
					Client.getSingleton().sceneGraph.resetTiles();
					SceneGraph.minimapUpdate = true;
				});
			}, Options.currentHeight);



			ApplicationPluginLoader.loadPlugins(this);
			ChangeListenerUtil.addListener(() -> {
				if(Client.gameLoaded.get()) {
					underlaySwatch.clear();
					overlaySwatch.clear();
					fillSwatches();
				}
			}, Options.hdTextures);
			
			this.setupOpenOptions();
			this.setupSaveOptions();

			final boolean reloadSaved = loadAutosave;

			ChangeListenerUtil.addListener(true, () -> {
				fillSwatches();

				try {
					byte[] modelData = ByteStreams.toByteArray(getClass().getResourceAsStream("/misc/mapfunction.dat"));

					MeshLoader.getSingleton().load(modelData, 111);
					
					
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				

				if(reloadSaved) {
					File landscapeFile = new File(autosavePath, "autosave.pack");
					if(landscapeFile.exists()) {
						try {
							byte[] landscapeData = Files.readAllBytes(landscapeFile.toPath());

							final byte[] fLandscape = landscapeData;
							Client.runLater.add(() -> {
								clientInstance.loadChunks(MultiMapEncoder.decode(fLandscape));
								fullMapView.resizeMap();
							});//TODO
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							FXDialogs.showError(primaryStage,"Error while loading map!", "There was an error while loading or parsing the autosave data.");
						}
					}
				}
				Platform.runLater(() -> {
					objectPreviewWindow.fillList();
				});
				try {
					setupAutoSave();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, Client.gameLoaded);

			EventBus.getDefault().register(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		primaryStage.sizeToScene();
	}
	
	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
	private void setupAutoSave(){

		int autosaveSeconds = Settings.getSetting("autosaveSeconds", 60);
		Settings.putSetting("autosaveSeconds", autosaveSeconds);

		service.scheduleAtFixedRate(() -> AutoSaveJob.execute(clientInstance), 5, 5, TimeUnit.MINUTES);
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onStatusUpdate(StatusUpdate update) {
		//Platform.runLater(() -> controller.getStatusLabel().setText(update.getText()));
	}

	public void setupSaveOptions() {

		controller.getSaveAsPackFile().setOnAction(act -> {

			File landscapeFile = RetentionFileChooser.showSaveDialog("Enter a name for packed maps file...", stage, "",
					FilterMode.PACK);
			if (landscapeFile == null)
				return;

			byte[] tileMap = MultiMapEncoder.encode(Lists.newArrayList(clientInstance.chunks));



			try {

				Files.write(landscapeFile.toPath(), tileMap);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				FXDialogs.showError(stage,"Error while saving map!", "There was an error while writing packed maps file.");
			}


		});
		controller.getSaveMenuItem().setOnAction(act -> {
			int startX = clientInstance.xCameraPos;
			int startY = clientInstance.yCameraPos;

			for(Chunk chunk : clientInstance.chunks) {
				clientInstance.xCameraPos = (chunk.offsetX + 32) * 128;
				clientInstance.yCameraPos = (chunk.offsetY + 32) * 128;
				File landscapeFile = RetentionFileChooser.showSaveDialog("Enter a name for tiles...", stage, chunk.tileMapId + "",
						FilterMode.DAT, FilterMode.GZIP);
				if (landscapeFile == null)
					return;
				File objectFile = RetentionFileChooser.showSaveDialog("Enter a name for objects...", stage, chunk.objectMapId + "",
						FilterMode.DAT, FilterMode.GZIP);

				if (objectFile == null)
					return;

				byte[] objectMap = clientInstance.sceneGraph.saveObjects(chunk);
				byte[] tileMap = chunk.mapRegion.save_terrain_block(chunk);

				if (landscapeFile.getName().endsWith(".gz")) {
					try {
						tileMap = GZIPUtils.gzipBytes(tileMap);
						if(tileMap == null)
							throw new IOException("GZIP error");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						FXDialogs.showError(stage,"Error while saving map!",
								"There was an error while writing map file.");
						return;
					}
				}
				if (objectFile.getName().endsWith(".gz")) {
					try {
						objectMap = GZIPUtils.gzipBytes(objectMap);
						if(objectMap == null)
							throw new IOException("GZIP error");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						FXDialogs.showError(stage,"Error while saving map!",
								"There was an error while writing map file.");
						return;
					}
				}

				try {

					Files.write(objectFile.toPath(), objectMap);
					Files.write(landscapeFile.toPath(), tileMap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					FXDialogs.showError(stage,"Error while saving map!", "There was an error while writing map file.");
				}

			}


			clientInstance.xCameraPos = startX;
			clientInstance.yCameraPos = startY;

		});
	}

	public void setupOpenOptions() throws Exception {

		GenerateNewMapWindow genNew = new GenerateNewMapWindow();
		
		genNew.start(new Stage());
	
		controller.getNewMapButton().setOnAction(evt -> {
			/*try {
				byte[] landscape = ByteStreams.toByteArray(getClass().getResourceAsStream("/misc/blank_region.dat"));
				byte[] object = ByteStreams.toByteArray(getClass().getResourceAsStream("/misc/blank_regionO.dat"));

				Client.runLater.add(() -> {
					clientInstance.loadFiles(landscape, object, 0, 0);
					fullMapView.resizeMap();
				});
			} catch(Exception ex) {
				FXDialogs.showError("Error while creating new map", "There was a failure while attempting to initialize\na new map.");
				ex.printStackTrace();
			}*/
			
			genNew.show();
			if(genNew.okClicked) {
				int chunkWidth = genNew.getWidth();
				int chunkHeight = genNew.getLength();
				try {

					Client.runLater.add(() -> {
						clientInstance.loadNew(chunkWidth, chunkHeight, genNew.getHeights());
						fullMapView.resizeMap();
					});
				} catch(Exception ex) {
					FXDialogs.showError(stage,"Error while creating new map", "There was a failure while attempting to initialize\na new map.");
					ex.printStackTrace();
				}
			}
		
		});

		controller.getOpenAsPackBtn().setOnAction(act -> {

			selectPack.show();

			if(!selectPack.valid())
				return;

			File packFile = new File(selectPack.getPackText());


			try {
				final byte[] packData = Files.readAllBytes(packFile.toPath());


				Client.runLater.add(() ->{
					clientInstance.loadChunks(MultiMapEncoder.decode(packData));
					fullMapView.resizeMap();
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				FXDialogs.showError(stage,"Error while loading map!",
						"There was an error while loading or parsing the selected file.");
			}

		});



		controller.getOpenFileButton().setOnAction(act -> {

			selectFiles.show();

			if(!selectFiles.valid())
				return;
			
			Client.runLater.add(() -> {
				clientInstance.loadChunks(selectFiles.prepareChunks());
				fullMapView.resizeMap();
			});

		});

		controller.getOpenHashButton().setOnAction(evt -> {

			pickHash.show();
			if(!pickHash.valid())
				return;
			int hash = pickHash.getHash();
			int width = pickHash.getWidth();
			int length = pickHash.getLength();
			Client.runLater.add(() -> { 
				clientInstance.loadCoordinates((hash >> 8) * 64, (hash & 0xff) * 64, width, length);
				fullMapView.resizeMap();
			});
		});

		controller.getOpenCoordinateButton().setOnAction(evt -> {
			/*String value = FXDialogs.showTextInput("Load from coordinates", "Please enter the regions coordinates in the format x,y: ", "");
			if(value != null && !value.equals("")) {
				String[] split = value.replaceAll(" ", "").split(",");
				int x = Integer.valueOf(split[0]);
				int y = Integer.valueOf(split[1]);
				x /= 64;
				y /= 64;
				int hash = (x << 0x39b8d2e8) + y;
				Client.runLater.add(() -> clientInstance.loadCoordinates((hash >> 8) * 64, (hash & 0xff) * 64, 1, 1));
			}*/

			pickCoords.show();
			if(!pickCoords.valid())
				return;
			int x = pickCoords.getXCoordinate();
			int y = pickCoords.getYCoordinate();	
			x /= 64;
			y /= 64;
			int hash = (x << 8) + y;
			int width = pickCoords.getWidth();
			int length = pickCoords.getLength();
			Client.runLater.add(() -> { 
				clientInstance.loadCoordinates((hash >> 8) * 64, (hash & 0xff) * 64, width, length);
				fullMapView.resizeMap();
			});

		});
	}

	public static MainWindow getSingleton() {
		return singleton;
	}

}
