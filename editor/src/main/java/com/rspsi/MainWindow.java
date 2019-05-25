package com.rspsi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.major.map.RenderFlags;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Ints;
import com.jagex.Client;
import com.jagex.cache.config.VariableBits;
import com.jagex.cache.def.Floor;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.config.VariableBitLoader;
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
import com.jagex.util.GZIPUtils;
import com.jagex.util.MultiMapEncoder;
import com.jagex.util.ObjectKey;
import com.rspsi.controllers.MainController;
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
import com.rspsi.game.save.SaveAction;
import com.rspsi.game.save.TileChange;
import com.rspsi.misc.StatusUpdate;
import com.rspsi.misc.ToolType;
import com.rspsi.options.Config;
import com.rspsi.options.Options;
import com.rspsi.plugins.ApplicationPluginLoader;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.swatches.BaseSwatch;
import com.rspsi.swatches.OverlaySwatch;
import com.rspsi.swatches.UnderlaySwatch;
import com.rspsi.util.ChangeListenerUtil;
import com.rspsi.util.FXDialogs;
import com.rspsi.util.FilterMode;
import com.rspsi.util.RetentionFileChooser;
import com.rspsi.util.Settings;

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
			Object tooltipBehavior = constructor.newInstance(new Duration(250), // open
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


	private static Client clientInstance;

	public static MainWindow getSingleton() {
		return singleton;
	}


	private Scene scene;

	private Stage stage;

	private MainController controller;

	public SwatchControl objectSwatch, overlaySwatch, underlaySwatch;

	private ObjectPreviewWindow objectPreviewWindow;
	private PickCoordinatesWindow pickCoords;
	private PickHashWindow pickHash;
	private MultiRegionMapWindow fullMapView;
	private SelectFilesWindow selectFiles;
	private SelectPackWindow selectPack;

	private Mesh errorMesh;

	public void fillSwatches() {

		for (int idx = 0; idx < FloorDefinitionLoader.getUnderlayCount(); idx++) {
			Floor floor = FloorDefinitionLoader.getUnderlay(idx);

			Group g = new Group();
			String label = "";
			label = "rgb(" + ColourUtils.getRed(floor.getRgb()) + "," + ColourUtils.getGreen(floor.getRgb()) + ","
					+ ColourUtils.getBlue(floor.getRgb()) + ")";
			Rectangle rect = new Rectangle();
			rect.setWidth(32);
			rect.setHeight(32);
			Color c = ColourUtils.getColor(floor.getRgb());
			// c = c.deriveColor(floor.getWeightedHue(), floor.getSaturation() / 256.0,
			// floor.getLuminance() / 256.0, 1.0);
			rect.setFill(c);
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
			if (floor.getTexture() == -1) {
				continue;
			} else {
				label = "texture(" + floor.getTexture() + ")";
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
			if (floor.getTexture() == -1 || floor.getTexture() > 50 && !Options.hdTextures.get()) {
				label = "rgb(" + ColourUtils.getRed(floor.getRgb()) + "," + ColourUtils.getGreen(floor.getRgb()) + ","
						+ ColourUtils.getBlue(floor.getRgb()) + ")";
				Rectangle rect = new Rectangle();
				rect.setWidth(32);
				rect.setHeight(32);
				Color c = ColourUtils.getColor(floor.getRgb());
				// c = c.deriveColor(floor.getWeightedHue(), floor.getSaturation() / 256.0,
				// floor.getLuminance() / 256.0, 1.0);
				rect.setFill(c);
				g.getChildren().add(rect);
			} else {
				continue;
			}
			BaseSwatch data = new OverlaySwatch(g, label, idx);
			overlaySwatch.addSwatch(data);
		}

	}

	public ObjectPreviewWindow getObjectPreviewWindow() {
		return objectPreviewWindow;
	}

	public SwatchControl getObjectSwatch() {
		return objectSwatch;
	}

	public SwatchControl getOverlaySwatch() {
		return overlaySwatch;
	}

	public Stage getStage() {
		return stage;
	}

	public SwatchControl getUnderlaySwatch() {
		return underlaySwatch;
	}

	public MainController getController() {
		return controller;
	}



	public void setObjectSwatch(SwatchControl objectSwatch) {
		this.objectSwatch = objectSwatch;
	}

	public void setOverlaySwatch(SwatchControl overlaySwatch) {
		this.overlaySwatch = overlaySwatch;
	}

	public void setUnderlaySwatch(SwatchControl underlaySwatch) {
		this.underlaySwatch = underlaySwatch;
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
			Parent content = (Parent) loader.load();
			scene = new Scene(content, 1240, 800);

			scene.setFill(Color.TRANSPARENT);

			//controller.getTitleLabel().textProperty().bind(primaryStage.titleProperty());
			primaryStage.setTitle("RSPSi Map Editor 1.15.2");
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());
			primaryStage.show();

			controller.onLoad(this);

			boolean shutdownCorrectly = Settings.getSetting("shutdown", false);
			Settings.clearSetting("shutdown");

			boolean loadAutosave = false;
			File autosavePath = Paths.get(System.getProperty("user.home"), ".rspsi", "autosave").toFile();

			if(!shutdownCorrectly) {
				//TODO Offer to load autosaved data
				System.out.println("CRASH DETECTED!");

				if(autosavePath.exists() && autosavePath.list().length > 0) {

					//Just incase there was a crash mid autosave
					File objectFile = new File(autosavePath, "objects.autosave");
					File landscapeFile = new File(autosavePath, "landscape.autosave");

					File objectFileBackup =  new File(autosavePath, "objects.bk");
					File landscapeFileBackup = new File(autosavePath, "landscape.bk");

					//restore backups
					if(objectFileBackup.exists()) {
						Files.copy(objectFileBackup.toPath(), objectFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}

					if(landscapeFileBackup.exists()) {
						Files.copy(landscapeFileBackup.toPath(), landscapeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}

					String response = FXDialogs.showConfirm("Application did not shut down correctly!", 
							"We have detected that your last shutdown did not complete correctly.\nWould you like to load the last autosave?",
							"Yes", "No");
					if(response.equalsIgnoreCase("yes")) {
						loadAutosave = true;
					}
				}
			}

			MapView mapView = new MapView();

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
			
		/*	ModelPreviewWindow modelPrev = new ModelPreviewWindow();
			modelPrev.start(new Stage());*/

			fullMapView = new MultiRegionMapWindow();
			fullMapView.start(new Stage());

			controller.getShowFullMap().setOnAction(evt -> fullMapView.show());

			controller.getExportTilesBtn().setOnAction(evt -> export.show());

			controller.getShowObjectViewBtn().setOnAction(evt -> objectPreviewWindow.stage.show());


			clientInstance = Client.initialize(controller.getGamePane().widthProperty().intValue(),
					controller.getGamePane().heightProperty().intValue());

			clientInstance.loadCache(Paths.get(Config.cacheLocation.get()));

			CanvasPane gamePane = new CanvasPane(clientInstance.getGameCanvas());

			clientInstance.getGameCanvas().addEventHandler(MouseEvent.ANY, new GameMouseListener(clientInstance));
			clientInstance.getGameCanvas().addEventHandler(ScrollEvent.ANY, new GameMouseListener(clientInstance));
			clientInstance.getGameCanvas().addEventHandler(KeyEvent.ANY, new GameKeyListener(clientInstance));

			SceneGraph.setMouseIsDown(true);
			SceneGraph.setMouseIsDown(false);

			controller.getGamePane().getChildren().add(gamePane);
			controller.getMapPane().getChildren().add(new CanvasPane(clientInstance.mapCanvas));
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
						FXDialogs.showError("Error while loading saving image", "There was a failure while attempting to save\nthe minimap to the selected file.");

					}
				}
			});
			menu.getItems().addAll(item);
			controller.getMapPane().setOnContextMenuRequested(evt -> {
				menu.show(controller.getMapPane(), evt.getScreenX(), evt.getScreenY());
			});

			controller.getFixHeightsBtn().setOnAction(evt -> {
				String result = FXDialogs.showConfirm("Are you sure?", "This fix will set all heights on plane 1 and above based on "
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

					}
				}
				Platform.exit();
				System.exit(0);
			});

			ChangeListenerUtil.addListener(() -> {
				SceneGraph.onCycleEnd.add(() -> Client.getSingleton().sceneGraph.forceMouseInTile());
			}, Options.rotation);

			controller.getCopyTileFlags().setOnAction(evt -> {
				BitFlag flag = clientInstance.getCurrentChunk().getSelectedFlag();

				controller.getUnwalkableCheck().setSelected(flag.flagged(RenderFlags.BLOCKED_TILE));
				controller.getBridgeCheck().setSelected(flag.flagged(RenderFlags.BRIDGE_TILE));
				controller.getForceLowestCheck().setSelected(flag.flagged(RenderFlags.FORCE_LOWEST_PLANE));
				controller.getDrawOnLowerZCheck().setSelected(flag.flagged(RenderFlags.RENDER_ON_LOWER_Z));
				controller.getDisableRenderCheck().setSelected(flag.flagged(RenderFlags.DISABLE_RENDERING));

			});

			controller.getCopyTileHeights().setOnAction(evt -> {
				int height = clientInstance.getCurrentChunk().getSelectedHeight();
				System.out.println(height);
				if(height <= 0) {
					controller.getHeightLevelSlider().setValue(-height);
				} else {
					FXDialogs.showError("Error while loading tile height", "There was a failure while attempting to grab\ntile height from the selected tile.");
				}
			});

			//XXX Remove this
			Runnable r = () -> {
				Scanner scanner = new Scanner(System.in);
				String lastCommand = "";
				System.out.println("Starting scanner");
				while(true) {
					try {
						if(scanner.hasNextLine()) {
							String s = scanner.nextLine();
							System.out.println(s);
							if(s.startsWith("stc")) {
								SaveAction saveAction = new SaveAction(clientInstance.getCurrentChunk());
								saveAction.saveToCache(clientInstance.getCache());
							} else if(s.startsWith("chunks")) {
								Client.skipOrdering = !Client.skipOrdering;
								System.out.println("ordering turned " + (Client.skipOrdering ? "off" : "on"));
							} else if(s.startsWith("config")) {
								s = s.replaceAll("config", "").trim();
								String parts[] = s.split(" ");
								int i = Ints.tryParse(parts[0]);
								int i2 = Ints.tryParse(parts[1]);
								clientInstance.settings[i] = i2;
							} else if(s.startsWith("bitmask")) {
								System.out.println();
								for(int bitmask : Client.BIT_MASKS) {
									System.out.print(bitmask +", ");
								}
								System.out.println();
							} else if(s.startsWith("varbit")) {
								s = s.replaceAll("varbit", "").trim();
								int i = Ints.tryParse(s);
								VariableBits bit = VariableBitLoader.lookup(i);
								System.out.println("IDX: " + i + " setting: " + bit.getSetting() + " low: " + bit.getLow() + " high: " + bit.getHigh());
								System.out.println("Bitmask: " + Client.BIT_MASKS[bit.getHigh() - bit.getLow()]);
							}
						} else {
							Thread.sleep(100);

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			Thread t = new Thread(r);
			t.start();

			controller.getGetOverlayFromTile().setOnAction(evt -> {
				Chunk chunk = clientInstance.getCurrentChunk();
				if(chunk != null) {
					int overlayId = chunk.getSelectedOverlay();
					int overlayShape = chunk.getSelectedOverlayShape();
					if(overlayId > 0 && overlayShape > 0) {
						overlaySwatch.setOverlayShape(overlayShape + 1);
						overlaySwatch.selectByOverlay(overlayId - 1);
					}
				}
			});

			controller.getGetUnderlayFromTile().setOnAction(evt -> {
				Chunk chunk = clientInstance.getCurrentChunk();
				if(chunk != null) {
					int underlayId = chunk.getSelectedUnderlay();
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
						FXDialogs.showError("Error while loading prefab!",
								"There was an error while reading the selected file.");
					} catch (Exception e) {
						e.printStackTrace();
						FXDialogs.showError("Error while parsing prefab!",
								"There was an error while parsing the selected file.");
					}
				}
			});



			ChangeListenerUtil.addListener(() -> {
				Client.updateChunkTiles();
				System.out.println("updating tiles");
				SceneGraph.minimapUpdate = true;
			}, Options.showHiddenTiles);

			ChangeListenerUtil.addListener(() -> {
				Client.updateChunkTiles();
				SceneGraph.onCycleEnd.add(() -> {
					Client.getSingleton().sceneGraph.resetTiles();
				});
				SceneGraph.minimapUpdate = true;
				System.out.println("updating tiles");
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
					File landscapeFile = new File(autosavePath, "map.autosave");
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
							FXDialogs.showError("Error while loading map!",
									"There was an error while loading or parsing the autosave data.");
						}
					}
				}
				Platform.runLater(() -> {
					objectPreviewWindow.fillList();
					//modelPrev.fillList();
					/*Client.deliveredResource.addListener((ChangeListener<Resource>) (observable, oldVal, newVal) -> {
						if(newVal != null && newVal.getType() == 3) {
							for(MapTile tile : Lists.newArrayList(MapTile.tiles)) {
								tile.deliverMap(newVal);
							}
						}
					});
					RSMapView view = new RSMapView();
					try {
						view.start(new Stage());
					} catch(Exception ex) {
						ex.printStackTrace();
					}*/
					/*MapView mapView = new MapView();
					try {
						mapView.start(new Stage());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mapView.initTiles();*/
				});

			}, Client.gameLoaded);

			int autosaveSeconds = Settings.getSetting("autosaveSeconds", 60);
			Settings.putSetting("autosaveSeconds", autosaveSeconds);

			StdSchedulerFactory fact = new StdSchedulerFactory();
			fact.initialize(this.getClass().getResourceAsStream("/config/quartz.properties"));
			Scheduler scheduler = fact.getScheduler();

			scheduler.startDelayed(10);
			JobDataMap jdm = new JobDataMap();
			jdm.put("client", clientInstance);
			JobDetail job = JobBuilder.newJob().
					ofType(AutoSaveJob.class)
					.withIdentity("saveJob")
					.setJobData(jdm)
					.build();

			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("timedAutosave")
					.startNow()
					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(autosaveSeconds))
					.build();
			// scheduler.
			scheduler.scheduleJob(job, trigger);
			EventBus.getDefault().register(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				FXDialogs.showError("Error while saving map!", "There was an error while writing packed maps file.");
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
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						FXDialogs.showError("Error while saving map!",
								"There was an error while writing map file.");
						return;
					}
				}
				if (objectFile.getName().endsWith(".gz")) {
					try {
						objectMap = GZIPUtils.gzipBytes(objectMap);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						FXDialogs.showError("Error while saving map!",
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
					FXDialogs.showError("Error while saving map!", "There was an error while writing map file.");
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
					FXDialogs.showError("Error while creating new map", "There was a failure while attempting to initialize\na new map.");
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
				FXDialogs.showError("Error while loading map!",
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
			int hash = (x << 0x39b8d2e8) + y;
			int width = pickCoords.getWidth();
			int length = pickCoords.getLength();
			Client.runLater.add(() -> { 
				clientInstance.loadCoordinates((hash >> 8) * 64, (hash & 0xff) * 64, width, length);
				fullMapView.resizeMap();
			});

		});
	}

}
