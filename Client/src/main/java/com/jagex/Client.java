package com.jagex;

import com.displee.cache.index.archive.Archive;
import com.jagex.map.SceneGraph;
import com.jagex.map.tile.SceneTile;
import com.rspsi.options.KeyboardState;
import javafx.scene.input.KeyCode;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.displee.util.GZIPUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jagex.cache.anim.Graphic;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.graphics.Sprite;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.chunk.Chunk;
import com.jagex.draw.ImageGraphicsBuffer;
import com.jagex.draw.font.RSFont;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.model.Mesh;
import com.jagex.entity.model.MeshLoader;
import com.jagex.entity.object.RenderableObject;
import com.jagex.map.MapRegion;
import com.jagex.net.ResourceProvider;
import com.jagex.net.ResourceResponse;
import com.jagex.util.Constants;
import com.jagex.util.ObjectKey;
import com.jagex.util.TextRenderUtils;
import com.rspsi.cache.CacheFileType;
import com.rspsi.game.DisplayCanvas;
import com.rspsi.core.misc.Vector2;
import com.rspsi.options.Options;
import com.rspsi.plugins.core.ClientPluginLoader;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public final class Client implements Runnable {


	public RSFont robotoFont;
	public Font jetBrainsMono;

	public SceneGraph sceneGraph;
	
	public boolean cameraMoved = true;
	
	// client
	public static HashMap<Consumer<Client>, Long> timedConsumers = Maps.newHashMap();
	
	public static ObjectProperty<ResourceResponse> lastDeliveredResource = new SimpleObjectProperty<ResourceResponse>();
	
	private Cache cache;
	
	public static BooleanProperty gameLoaded = new SimpleBooleanProperty(false);
	
	public static ObjectProperty<Exception> lastThrownException = new SimpleObjectProperty<Exception>();

	public static final int[] BIT_MASKS;

	public static int pulseTick;
	static boolean displayFps;
	static int drawTick;
	
	private static Client singleton;
	
	public static Client getSingleton() {
		return singleton;
	}
	
	private static boolean clientLoaded;
	private static boolean lowMemory;

	static {

		BIT_MASKS = new int[32];
		for (int index = 0; index < 32; index++) {
			BIT_MASKS[index] = (1 << index) - 1;
		}
	}

	public static Sprite[] mapFunctions = new Sprite[0];

	public static Sprite[] mapScenes = new Sprite[100];

	public static int plane;

	public static int tickDelta;

	public static ObjectKey hoveredUID = null;
	
	public static List<Runnable> runLater = new ArrayList<>();
	

	public static Client initialize(int width, int height) {


		clientLoaded = false;

		setHighMemory();

		Client client = new Client();
		client.initFrame(height, width);
		return client;
	}
	
	private ReentrantLock cacheLoadingLock = new ReentrantLock();
	public void loadCache(Path path) {
		cacheLoadingLock.lock();
		try {
			this.cache = new Cache(path);
			Thread t = new Thread(getProvider());
			t.start();
	
			EventBus.getDefault().register(this);
				new MeshLoader(cache.getProvider());
		} catch (Exception e) {
			errorMessage = "There was an error loading the specified cache!";
			error = true;
			log.error("Could not load cache! {}", e.getMessage());
		}
		cacheLoadingLock.unlock();
	}
	
	public Cache getCache() {
		return cache;
	}

	public final static int method120() {
		if (!Options.allHeightsVisible.get())
			return Options.currentHeight.get();
		else
			return 3;
	}

	public final static int method121() {
		if (Options.allHeightsVisible.get())
			return 3;
		return Options.currentHeight.get();
	}

	public static final void reset() {
		unlinkCaches();

		
	}

	public static void setHighMemory() {
		SceneGraph.lowMemory = false;
		lowMemory = false;
		MapRegion.lowMemory = false;
		ObjectDefinition.lowMemory = false;
	}

	public final static void unlinkCaches() {
		ObjectDefinition.baseModels.clear();
		ObjectDefinition.models.clear();
		Graphic.modelCache.clear();
	}

	public boolean loggedIn;
	public int[] settings = new int[10000];
	long aLong1220;
	int lastMouseX;
	int lastMouseY;
	boolean wasFocused = true;
	private boolean gameImageBufferNeedsInit;
	private boolean gameScreenReinitialized;
	private volatile boolean aBoolean831;
	private volatile boolean aBoolean962;
	public ImageGraphicsBuffer gameImageBuffer;
	private int anInt1014;
	private int anInt1015;
	private int anInt1131;
	public int cameraRotationX;
	public int cameraRotationZ;
	private int anInt1278;
	public int xCameraPos = 1 * 32 * 128;
	public int yCameraPos = 1 * 32 * 128;
	public int xCameraCurve = (int) (Math.random() * 20D) - 10 & 0x7ff;
	public int zCameraPos = -540;
	public int yCameraCurve = 128;
	private int anInt896;
	private int anInt916;
	private int anInt917;
	private int anInt984;
	private int anInt985 = -1;
	public int cameraRoll = 128;
	public int cameraYaw;
	private boolean error;
	private boolean gameAlreadyLoaded;
	private long loadingStartTime;

	private int timeoutCounter;

	private boolean unableToLoad;

	private int resizeHeight = -1, resizeWidth = -1;

	public List<Chunk> chunks = new ArrayList<>();

	public Client() {
		singleton = this;
	}
	public static boolean skipOrdering = true;

	/**
	 * Orders current chunks based on distance from camera position
	 * @return An ordered list of @Chunk
	 */
	/*	public void sortChunks() {
			cameraMoved = false;
			if(skipOrdering) {
				this.orderedChunks = chunks;
				return;
			}
			//TODO Redo this. Should search from the current camera position outwards
			LinkedList<Chunk> reverse = new LinkedList<>();
			int cameraView = (xCameraCurve / 2048) * 360;
			final int globalX = xCameraPos / 128;
			final int globalY = yCameraPos / 128;
			reverse.addAll(chunks.parallelStream().filter(chunk -> !chunk.inChunk(globalX, globalY)).collect(Collectors.toList()));
			//System.out.println("Added " + reverse.size() + "/" + chunks.size() + " chunks to list");
			Comparator<Chunk> comparator = (Chunk chunk1, Chunk chunk2) -> {
				double distance1 = Math.hypot(chunk1.offsetX - globalX, chunk1.offsetY - globalY);
				double distance2 = Math.hypot(chunk2.offsetX - globalX, chunk2.offsetY - globalY);
				return distance1 > distance2 ? 1 : -1;
			};
			reverse.sort(comparator);
			
			Optional<Chunk> currentChunk = chunks.parallelStream().filter(chunk -> chunk.inChunk(globalX, globalY)).findFirst();
			
			if(currentChunk.isPresent())
				reverse.addLast(currentChunk.get());
			
			this.orderedChunks = reverse;
		}*/

	public BooleanProperty errorDisplayed = new SimpleBooleanProperty(false);
	
	public final void displayErrorMessage() {
		errorDisplayed.set(true);
		GraphicsContext context = gameCanvas.getGraphicsContext2D();
	//	Graphics graphics = gameCanvas.getGraphics();
		context.setFill(Color.BLACK);
		context.clearRect(0, 0, getCanvasWidth(), canvasHeight);
		//graphics.setColor(Color.black);
		//graphics.fillRect(0, 0, this.getCanvasWidth(), this.getCanvasHeight());
		resetTimeDelta();

		if (error) {
			aBoolean831 = false;
			context.setFont(javafx.scene.text.Font.font("JetBrains Mono", 16));
			context.setFill(Color.YELLOW);
			context.setTextAlign(TextAlignment.CENTER);
			context.setTextBaseline(VPos.CENTER);
			
			
			context.fillText("An error has occured while booting the map editor", this.getCanvasWidth() / 2, this.getCanvasHeight() / 2);
			if(!errorMessage.isEmpty()) {
				context.fillText(errorMessage, this.getCanvasWidth() / 2, this.getCanvasHeight() / 2 + 20);
			}
		}

		if (unableToLoad) {
			aBoolean831 = false;
			context.setFont(javafx.scene.text.Font.font("JetBrains Mono", 20));
			context.setFill(Color.WHITE);
			context.setTextAlign(TextAlignment.CENTER);
			context.setTextBaseline(VPos.CENTER);
			context.fillText("Error - unable to load editor!", this.getCanvasWidth() / 2, this.getCanvasHeight() / 2);
		}
	}

	private String errorMessage = "";
	public boolean visible = true;
	public final void draw() {
		if(errorDisplayed.get())
			return;
		if (gameAlreadyLoaded || error || unableToLoad) {
			Platform.runLater(this::displayErrorMessage);
			return;
		}
		drawTick++;

		if(visible) {
			drawGameScreen();
		}
		handleResize();

	}

	public int getPlane() {
		return Options.currentHeight.get();
	}

	public void moveCamera(int worldX, int worldY) {
		this.xCameraPos = worldX * 128;
		this.yCameraPos = worldY * 128;
		this.cameraMoved = true;
	}

	public static enum LoadState {
		CLIENT_INIT, WAITING_INPUT, LOADING_MAP, ACTIVE, ERROR
	}
	
	public LoadState loadState = LoadState.CLIENT_INIT;

	public final void drawGameScreen() {
		if (gameScreenReinitialized) {
			gameScreenReinitialized = false;
			gameImageBufferNeedsInit = true;
		}

		if (loadState == LoadState.WAITING_INPUT) {

			gameImageBuffer.initializeRasterizer();
			gameImageBuffer.clear(0);
			TextRenderUtils.renderCenter(gameImageBuffer.getGraphics(), 
					"Please select a map to load...",  
					gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2 - 20,
					0xffffff);
			drawDebugOverlay();
			gameImageBuffer.finalize();
		}
		
		if (loadState == LoadState.ERROR) {

			gameImageBuffer.initializeRasterizer();
			gameImageBuffer.clear(0);
			TextRenderUtils.renderCenter(gameImageBuffer.getGraphics(), 
					"There was an error loading the specified map!", gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2 - 20, 0xFFFFFF);
			
			drawDebugOverlay();
			gameImageBuffer.finalize();
		}

		if (loadState == LoadState.ACTIVE) {
			renderView();
		}

		
		
		drawGameImage();
		if (loadState == LoadState.ACTIVE && SceneGraph.minimapUpdate) {
			if(System.currentTimeMillis() - lastMinimapUpdate > 100) {
				lastMinimapUpdate = System.currentTimeMillis();
				this.drawMinimapFullImage();
				System.out.println("MINIMAP");
				Chunk chunk = this.getCurrentChunk();
				if(chunk != null) {
					chunk.drawMinimapScene(Options.currentHeight.get());
					chunk.drawMinimap();
					chunk.minimapImageBuffer.finalize();
					
					drawMinimapImage();
					SceneGraph.minimapUpdate = false;
					gameImageBufferNeedsInit = true;
				}
			}
		}

		if (gameImageBufferNeedsInit) {
			gameImageBufferNeedsInit = false;
			gameImageBuffer.initializeRasterizer();
		}
		tickDelta = 0;
	}
	
	private static long lastMinimapUpdate;

	public Chunk getCurrentChunk() {
		for (int i = chunks.size() - 1; i >= 0; i--) {
			if (chunks.get(i).inChunk(xCameraPos / 128, yCameraPos / 128))
				return chunks.get(i);
		}
		return chunks.isEmpty() ? null : chunks.get(0);
	}

	public ResourceProvider getProvider() {
		return cache.getProvider();
	}

	public void handleResize() {
		int h = (int) gameCanvas.getHeight();
		int w = (int) gameCanvas.getWidth();
		if (h == resizeHeight && w == resizeWidth || h <= 0 || w <= 0 || h != gameCanvas.getHeight()
				|| w != gameCanvas.getWidth())
			return;

		resizeHeight = h;
		resizeWidth = w;
		gameCanvas.resize(w, h);

		gameImageBuffer = new ImageGraphicsBuffer(w, h, GameRasterizer.getInstance());
		int[] ai = new int[64];
		for (int i8 = 0; i8 < 64; i8++) {
			int theta = i8 * 32 + 15;
			int l8 = 600 + theta * 3;
			int i9 = Constants.SINE[theta];
			ai[i8] = l8 * i9 >> 16;
		}

		gameImageBuffer.initializeRasterizer();
		GameRasterizer.getInstance().setBounds(0, 0, w, h);
		GameRasterizer.getInstance().useViewport();
		gameImageBuffer.getGraphics().setFont(jetBrainsMono);
		
		if(sceneGraph != null)
			sceneGraph.method310(500, 800, w, h, ai);

	}

	public final void load() {

		if(cacheLoadingLock.isLocked()) {
			log.info("Waiting for cache to load!");
			cacheLoadingLock.lock();
			log.info("Cache finished loading!");
		}
		
		if(!errorMessage.isEmpty()) {
			clientLoaded = true;
			return;
		}
		drawLoadingText(20, "Starting up");
	
		if (clientLoaded) {
			gameAlreadyLoaded = true;
			return;
		}

		clientLoaded = true;

	
		
		ClientPluginLoader.loadPlugins();
		try {




			if(cache.getIndexedFileSystem().is317()) {

				Archive graphics = cache.createArchive(4, "2d graphics");


				Sprite[] scenes = new Sprite[1000];
				Sprite[] functions = new Sprite[1000];
				int lastIdx = 0;
				try {

					for (int scene = 0; scene < 93; scene++) {
						scenes[scene] = new Sprite(graphics, "mapscene", scene);
						lastIdx = scene;
					}
				} catch (Exception ex) {
					//ex.printStackTrace();
				}
				mapScenes = Arrays.copyOf(scenes, lastIdx + 1);

				lastIdx = 0;

				try {
					for (int function = 0; function < functions.length; function++) {
						functions[function] = new Sprite(graphics, "mapfunction", function);
						lastIdx = function;
					}
				} catch (Exception ex) {
					//ex.printStackTrace();
				}

				mapFunctions = Arrays.copyOf(functions, lastIdx + 1);
			} else {
				try {
					mapScenes = Sprite.unpackAndDecode(ByteBuffer.wrap(cache.getFile(CacheFileType.SPRITE).archive("mapscene").file(0).getData()));
				} catch (Exception e) {
					mapScenes = new Sprite[0];
				}
				try {
					int lastIdx = 0;

					Archive graphics = cache.createArchive(4, "2d graphics");
					Sprite[] functions = new Sprite[1000];
					try {
						for (int function = 0; function < functions.length; function++) {
							functions[function] = new Sprite(graphics, "mapfunction", function);
							lastIdx = function;
						}
					} catch (Exception ex) {
						//ex.printStackTrace();
						lastIdx = -1;
					}

					mapFunctions = lastIdx == -1 ? new Sprite[0] : Arrays.copyOf(functions, lastIdx + 1);
				} catch(Exception ex){
					mapFunctions = new Sprite[0];
				}
			}

			drawLoadingText(65, "Loading plugins...");
			
			ClientPluginLoader.forEach(plugin -> {
				try {
					plugin.onGameLoaded(this);
				} catch (Exception e) {
					e.printStackTrace();
					error = true;
					errorMessage = "The selected plugin was unable to load";
				}
			});
			
			if(!errorMessage.isEmpty())
				throw new IllegalStateException(errorMessage);
			


			log.info("Loaded {} map functions.", mapFunctions.length);
			log.info("Loaded {} map scenes.", mapScenes.length);
			drawLoadingText(100, "Preparing game engine");
			GameRasterizer.getInstance().setBounds(0, 0, (int)gameCanvas.getWidth(), (int)gameCanvas.getHeight());
			GameRasterizer.getInstance().useViewport();
			int[] ai = new int[64];
			for (int i8 = 0; i8 < 64; i8++) {
				int theta = i8 * 32 + 15;
				int l8 = 600 + theta * 3;
				int i9 = Constants.SINE[theta];
				ai[i8] = l8 * i9 >> 16;
			}

			RenderableObject.client = this;
			ObjectDefinition.client = this;
			gameFinishedLoading();

			GameRasterizer.getInstance().setBrightness(0.6);
			GameRasterizer.getInstance().setTextureBrightness(0.6);
			gameLoaded.set(true);
		} catch (Exception exception) {
			errorMessage = "There was an error during initialization!";
			error = true;
			exception.printStackTrace();
			//TODO Throw error
		}
	}

	@Getter
	private int baseX, baseY;
	
	private Chunk lastChunk;

	public final void loadCoordinates(int wX, int wY, int chunkXLength, int chunkYLength) {
		baseX = wX;
		baseY = wY;

		fullMapCanvas = new DisplayCanvas(chunkXLength * Options.mapRegionSize.get(), chunkYLength * Options.mapRegionSize.get(), false);
		chunks.clear();
		
		gameImageBuffer.initializeRasterizer();
		gameImageBuffer.clear(0);
		TextRenderUtils.renderCenter(gameImageBuffer.getGraphics(), 
				"Loading map, this may take a few seconds...", gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2 - 20, 0xFFFFFF);
		// frameFont.renderCentre(256, 150, "Loading - please wait.", 0xffffff);
		gameImageBuffer.finalize();
		drawGameImage();
		xCameraPos = ((int)(Math.ceil(chunkXLength / 2)) * 8192) + 4096;
		yCameraPos = ((int)(Math.ceil(chunkYLength / 2)) * 8192) + 4096;
		sceneGraph = new SceneGraph(64 * (chunkXLength), 64 * (chunkYLength), 4);
		mapRegion = new MapRegion(sceneGraph, 64 * (chunkXLength), 64 * (chunkYLength));
		for (int chunkX = 0; chunkX < chunkXLength; chunkX++) {
			for (int chunkY = 0; chunkY < chunkYLength; chunkY++) {
			//	try {
					anInt984 = 0;
					int cX = (wX + (64 * chunkX)) / 64;
					int cY = (wY + (64 * chunkY)) / 64;
					int hash = (cX << 8) + cY;
					Chunk chunk = new Chunk(hash);
					chunk.offsetX = (64 * chunkX);
					chunk.offsetY = (64 * chunkY);

					chunk.init(this);
					/*
					 * if (this.regionX == regionX && this.regionY == regionY && loadingStage ==
					 * 2)//XXX return;
					 */

					// Each -6 is -0.5 in loop, for a total of +1 loop
					int landscapeMapId = MapIndexLoader.resolve(cX, cY, MapType.LANDSCAPE);
					chunk.tileMapId = landscapeMapId;
					chunk.tileMapName = MapIndexLoader.getName(cX, cY, MapType.LANDSCAPE);
					if (landscapeMapId != -1) {
						getProvider().requestMap(landscapeMapId, hash);
						System.out.println("Requesting landscape map " + landscapeMapId);
					}

					int objectMapId = MapIndexLoader.resolve(cX, cY, MapType.OBJECT);
					chunk.objectMapId = objectMapId;
					chunk.objectMapName = MapIndexLoader.getName(cX, cY, MapType.OBJECT);
					if (objectMapId != -1) {
						getProvider().requestMap(objectMapId, hash);
						System.out.println("Requesting object map " + objectMapId);
					}
					log.info("Added chunk, obj/landscape {}/{}", objectMapId, landscapeMapId);
					pendingChunks.add(chunk);
				//} catch (Exception exception) {
				//	break;
				//}
			}
		}
		int width = (int) gameCanvas.getWidth();
		int height = (int) gameCanvas.getHeight();
		int[] ai = new int[64];
		for (int i8 = 0; i8 < 64; i8++) {
			int theta = i8 * 32 + 15;
			int l8 = 600 + theta * 3;
			int i9 = Constants.SINE[theta];
			ai[i8] = l8 * i9 >> 16;
		}
		sceneGraph.method310(500, 800, width, height, ai);
		loadState = LoadState.LOADING_MAP;
		loadingStartTime = System.currentTimeMillis();
	}
	
	public final void loadNew(int chunkXLength, int chunkYLength, int[][] heights) {

		baseX = 0;
		baseY = 0;
		fullMapCanvas = new DisplayCanvas(chunkXLength * Options.mapRegionSize.get(), chunkYLength * Options.mapRegionSize.get(), false);
		chunks.clear();
		
		gameImageBuffer.initializeRasterizer();
		gameImageBuffer.clear(0);
		TextRenderUtils.renderCenter(gameImageBuffer.getGraphics(), 
				"Loading map, this may take a few seconds...", gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2 - 20, 0xFFFFFF);
		// frameFont.renderCentre(256, 150, "Loading - please wait.", 0xffffff);
		gameImageBuffer.finalize();
		drawGameImage();
		xCameraPos = 0;
		yCameraPos = 0;
		sceneGraph = new SceneGraph(64 * (chunkXLength), 64 * (chunkYLength), 4);
		mapRegion = new MapRegion(sceneGraph, 64 * (chunkXLength), 64 * (chunkYLength));
		mapRegion.tileHeights[0] = heights;
		for(int x = 0;x<mapRegion.underlays[0].length;x++)
			Arrays.fill(mapRegion.underlays[0][x], (byte)1);
		for(int x = 0;x<mapRegion.manualTileHeight[0].length;x++)
			Arrays.fill(mapRegion.manualTileHeight[0][x], (byte)1);
		mapRegion.setHeights();
		int fileId = 0;
		for (int chunkX = 0; chunkX < chunkXLength; chunkX++) {
			for (int chunkY = 0; chunkY < chunkYLength; chunkY++) {
					anInt984 = 0;
					int cX = 1000;
					int cY = 1000;
					int hash = (cX << 8) + cY;
					Chunk chunk = new Chunk(hash);
					chunk.offsetX = 64 * chunkX;
					chunk.offsetY = 64 * chunkY;
					chunk.setNewMap(true);
					
					chunk.tileMapId = fileId++;
					chunk.objectMapId = fileId++;
					
					chunk.fillNamesFromIds();

					chunk.init(this);
					pendingChunks.add(chunk);
			}
		}
		int width = (int) gameCanvas.getWidth();
		int height = (int) gameCanvas.getHeight();
		int[] ai = new int[64];
		for (int i8 = 0; i8 < 64; i8++) {
			int theta = i8 * 32 + 15;
			int l8 = 600 + theta * 3;
			int i9 = Constants.SINE[theta];
			ai[i8] = l8 * i9 >> 16;
		}
		sceneGraph.method310(500, 800, width, height, ai);
		loadState = LoadState.LOADING_MAP;
		loadingStartTime = System.currentTimeMillis();
	}
	

	public final void loadChunks(List<Chunk> chunks) {
		this.chunks.clear();

		baseX = 0;
		baseY = 0;
		gameImageBuffer.initializeRasterizer();
		gameImageBuffer.clear(0);
		TextRenderUtils.renderCenter(gameImageBuffer.getGraphics(), 
				"Loading map, this may take a few seconds...", gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2 - 20, 0xFFFFFF);
		// frameFont.renderCentre(256, 150, "Loading - please wait.", 0xffffff);
		gameImageBuffer.finalize();
		drawGameImage();
		xCameraPos = 0;
		yCameraPos = 0;
		int chunkXLength = 0;
		int chunkYLength = 0;
		for(Chunk chunk : chunks) {
			int chunkX = chunk.offsetX / 64;
			int chunkY = chunk.offsetY / 64;
			
			if(chunkX > chunkXLength)
				chunkXLength = chunkX;
			if(chunkY > chunkYLength)
				chunkYLength = chunkY;
			
			
		}
		chunkXLength += 1;
		chunkYLength += 1;
		sceneGraph = new SceneGraph(64 * (chunkXLength), 64 * (chunkYLength), 4);
		mapRegion = new MapRegion(sceneGraph, 64 * (chunkXLength), 64 * (chunkYLength));
		fullMapCanvas = new DisplayCanvas(chunkXLength * Options.mapRegionSize.get(), chunkYLength * Options.mapRegionSize.get(), false);
		for(Chunk chunk : chunks) {
			chunk.init(this);

			chunk.fillNamesFromIds();
			this.pendingChunks.add(chunk);
		}
		int width = (int) gameCanvas.getWidth();
		int height = (int) gameCanvas.getHeight();
		int[] ai = new int[64];
		for (int i8 = 0; i8 < 64; i8++) {
			int theta = i8 * 32 + 15;
			int l8 = 600 + theta * 3;
			int i9 = Constants.SINE[theta];
			ai[i8] = l8 * i9 >> 16;
		}
		sceneGraph.method310(500, 800, width, height, ai);
		loadState = LoadState.LOADING_MAP;
		loadingStartTime = System.currentTimeMillis();
	}

	public final void loadFiles(byte[] landscapeBytes, byte[] objectBytes, int regionX, int regionY) {
		chunks.clear();

		baseX = 0;
		baseY = 0;
		gameImageBuffer.initializeRasterizer();
		gameImageBuffer.clear(0);
		TextRenderUtils.renderCenter(gameImageBuffer.getGraphics(), 
				"Loading map, this may take a few seconds...", gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2 - 20, 0xFFFFFF);
		// frameFont.renderCentre(256, 150, "Loading - please wait.", 0xffffff);
		gameImageBuffer.finalize();
		drawGameImage();
		xCameraPos = 0;
		yCameraPos = 0;

		sceneGraph = new SceneGraph(64, 64, 4);
		mapRegion = new MapRegion(sceneGraph, 64, 64);
		fullMapCanvas = new DisplayCanvas(Options.mapRegionSize.get(), Options.mapRegionSize.get(), false);
		for (int chunkX = 0; chunkX < 1; chunkX++) {
			for (int chunkY = 0; chunkY < 1; chunkY++) {
					anInt984 = 0;
					int cX = (0 + 64 * chunkX) / 64;
					int cY = (0 + 64 * chunkY) / 64;

					int hash = (cX << 8) + cY;
					Chunk chunk = new Chunk(hash);
					chunk.offsetX = 64 * chunkX;
					chunk.offsetY = 64 * chunkY;

					chunk.init(this);
					/*
					 * if (this.regionX == regionX && this.regionY == regionY && loadingStage ==
					 * 2)//XXX return;
					 */

					// Each -6 is -0.5 in loop, for a total of +1 loop
					chunk.tileMapId = 0;
					chunk.objectMapId = 1;
					
					chunk.tileMapData = landscapeBytes;
					chunk.objectMapData = objectBytes;

					pendingChunks.add(chunk);
			}
		}
		int width = (int) gameCanvas.getWidth();
		int height = (int) gameCanvas.getHeight();
		int[] ai = new int[64];
		for (int i8 = 0; i8 < 64; i8++) {
			int theta = i8 * 32 + 15;
			int l8 = 600 + theta * 3;
			int i9 = Constants.SINE[theta];
			ai[i8] = l8 * i9 >> 16;
		}
		sceneGraph.method310(500, 800, width, height, ai);
		loadState = LoadState.LOADING_MAP;
		loadingStartTime = System.currentTimeMillis();
	}

	public final void loadNextRegion() {
		try { 
		if (loadState == LoadState.LOADING_MAP) {
			boolean j = method54();
			if (!j && System.currentTimeMillis() - loadingStartTime > 0x57e40) {
				//TODO throw error
				loadingStartTime = System.currentTimeMillis();
			}
		}
		} catch(Exception ex) {
			ex.printStackTrace();
			this.chunks.clear();
		
			loadState = LoadState.ERROR;
		
		}
		if (loadState == LoadState.ACTIVE && plane != anInt985) {
			
			anInt985 = plane;
			gameImageBuffer.initializeRasterizer();
		}
	}

	public final void gameFinishedLoading() {
		loadState = LoadState.WAITING_INPUT;

	}
	
	public void scrollOut() {
		keyStatuses['s'] = 1;
		timedConsumers.put(client -> {
			client.keyStatuses['s'] = 0;
		}, System.currentTimeMillis() + 120);
	}
	
	public void scrollIn() {
		keyStatuses['w'] = 1;
		timedConsumers.put(client -> {
			client.keyStatuses['w'] = 0;
		}, System.currentTimeMillis() + 120);
	}
	
	public final void handleKeyInputs(int speedMultiplier) {
		try {
			int j = 0 + anInt1278;
			int k = 0 + anInt1131;

			if (anInt1014 - j < -500 || anInt1014 - j > 500 || anInt1015 - k < -500 || anInt1015 - k > 500) {
				anInt1014 = j;
				anInt1015 = k;
			}

			if (anInt1014 != j) {
				anInt1014 += (j - anInt1014) / 16;
			}

			if (anInt1015 != k) {
				anInt1015 += (k - anInt1015) / 16;
			}
			

			if (keyStatuses['w'] == 1) {
				xCameraPos -= Constants.SINE[xCameraCurve] >> speedMultiplier;
				yCameraPos += Constants.COSINE[xCameraCurve] >> speedMultiplier;
				if (yCameraCurve < 0) {
					zCameraPos -= Constants.SINE[-yCameraCurve] >> speedMultiplier;
				} else {
					zCameraPos += Constants.SINE[yCameraCurve] >> speedMultiplier;
				}
				this.cameraMoved = true;
			} else if (keyStatuses['s'] == 1) {
				xCameraPos += Constants.SINE[xCameraCurve] >> speedMultiplier;
				yCameraPos -= Constants.COSINE[xCameraCurve] >> speedMultiplier;

				if (yCameraCurve < 0) {
					zCameraPos += Constants.SINE[-yCameraCurve] >> speedMultiplier;
				} else {
					zCameraPos -= Constants.SINE[yCameraCurve] >> speedMultiplier;
				}

				this.cameraMoved = true;
			} 

			if (keyStatuses['a'] == 1) {
				xCameraPos -= Constants.SINE[xCameraCurve + 512 & 0x7ff] >> speedMultiplier;
				yCameraPos += Constants.COSINE[xCameraCurve + 512 & 0x7ff] >> speedMultiplier;

				this.cameraMoved = true;
			} else if (keyStatuses['d'] == 1) {
				xCameraPos -= Constants.SINE[xCameraCurve - 512 & 0x7ff] >> speedMultiplier;
				yCameraPos += Constants.COSINE[xCameraCurve - 512 & 0x7ff] >> speedMultiplier;

				this.cameraMoved = true;
			}

			if (keyStatuses['o'] == 1) {
				zCameraPos -= 14;
			} else if (keyStatuses['p'] == 1) {
				zCameraPos += 14;
			}

			if (keyStatuses['l'] == 1) {
				xCameraPos += Constants.SINE[xCameraCurve] >> speedMultiplier;
				yCameraPos -= Constants.COSINE[xCameraCurve] >> speedMultiplier;
				this.cameraMoved = true;
			} else if (keyStatuses['k'] == 1) {
				xCameraPos -= Constants.SINE[xCameraCurve] >> speedMultiplier;
				yCameraPos += Constants.COSINE[xCameraCurve] >> speedMultiplier;
				this.cameraMoved = true;
			}
			
			if (keyStatuses[1] == 1) {
				cameraRotationX += (-24 - cameraRotationX) / 2;
			} else if (keyStatuses[2] == 1) {
				cameraRotationX += (24 - cameraRotationX) / 2;
			} else {
				cameraRotationX /= 2;
			}

			if (keyStatuses[3] == 1) {
				cameraRotationZ += (12 - cameraRotationZ) / 2;
			} else if (keyStatuses[4] == 1) {
				cameraRotationZ += (-12 - cameraRotationZ) / 2;
			} else {
				cameraRotationZ /= 2;
			}
			
			cameraYaw = cameraYaw + cameraRotationX / 2 & 0x7ff;
			
			cameraRoll += cameraRotationZ / 2;
			
			if (cameraRoll < -110) {
				cameraRoll = -110;
			}
			if (cameraRoll > 383) {
				cameraRoll = 383;
			}
			
		
			int j2 = 0 ;
			if (j2 > 0x17f00) {
				j2 = 0x17f00;
			}
			if (j2 < 32768) {
				j2 = 32768;
			}
			if (j2 > anInt984) {
				anInt984 += (j2 - anInt984) / 24;
				return;
			}
			if (j2 < anInt984) {
				anInt984 += (j2 - anInt984) / 80;
				return;
			}
		} catch (Exception _ex) {
			_ex.printStackTrace();
			//TODO Throw error
			throw new RuntimeException("eek");
		}
	}

	public final void drawDebugOverlay() {

		if (Options.showDebug.get()) {
			int c = (int) gameCanvas.getWidth() - 20;
			int k = 40;
			int i1 = 0xffff00;
			if (fps < 15) {
				i1 = 0xff0000;
			}
			if(this.getCurrentChunk() != null) {
				Chunk chunk = this.getCurrentChunk();
				k += TextRenderUtils.renderLeft(gameImageBuffer, "WorldX: " + (chunk.regionX * 64) + " WorldY: " + (chunk.regionY * 64), c, k, i1);
			}

			k += TextRenderUtils.renderLeft(gameImageBuffer, "Fps: " + fps, c, k, i1);
			Runtime runtime = Runtime.getRuntime();
			int memory = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024);
			i1 = 0xffff00;
			k += TextRenderUtils.renderLeft(gameImageBuffer, "Mem: " + memory / 1024 + "MB", c, k, 0xffff00);

			k += TextRenderUtils.renderLeft(gameImageBuffer, "Chunk map files:  "  + getCurrentChunk().tileMapName + " " + getCurrentChunk().objectMapName + " ", c, k, 0xffff00);

			k += TextRenderUtils.renderLeft(gameImageBuffer, "Mouse: " + mouseEventX + "," + mouseEventY + "", c, k, 0xffff00);

			k += TextRenderUtils.renderLeft(gameImageBuffer, "Mouse Tile: " + sceneGraph.hoveredTileX + "," + sceneGraph.hoveredTileY + "", c, k,
					0xffff00);

			k += TextRenderUtils.renderLeft(gameImageBuffer, "Height: " + Options.currentHeight.get() + " Pos:" + xCameraPos / 128 + ","
					+ yCameraPos / 128 + "," + zCameraPos + "", c, k, 0xffff00);

			k += TextRenderUtils.renderLeft(gameImageBuffer, "Camera: " + xCameraCurve + "," + yCameraCurve + "," + cameraRoll + "," + cameraYaw + "",
					c, k, 0xffff00);

			k += TextRenderUtils.renderLeft(gameImageBuffer, "Tool: " + Options.currentTool.get().name() + "", c, k, 0xffff00);

			k += TextRenderUtils.renderLeft(gameImageBuffer, "Hover UID: " + hoveredUID + "", c, k, 0xffff00);

			if(sceneGraph.tiles[Options.currentHeight.get()][sceneGraph.hoveredTileX][sceneGraph.hoveredTileY] != null) {
				SceneTile tile = sceneGraph.tiles[Options.currentHeight.get()][sceneGraph.hoveredTileX][sceneGraph.hoveredTileY];
				k += TextRenderUtils.renderLeft(gameImageBuffer, "Simple Data: " + (tile.simple != null ? tile.simple.toString() : "") , c, k, 0xffff00);

				k += TextRenderUtils.renderLeft(gameImageBuffer, "Shaped Data: "+ (tile.shape != null ? tile.shape.toString() : "null"), c, k, 0xffff00);

			}


			if (hoveredUID != null) {
				ObjectKey key = hoveredUID;
				int id = key.getId();
				int type = key.getType();
				int orientation = key.getOrientation();

				int y = key.getY();
				int x = key.getX();
				ObjectDefinition def = ObjectDefinitionLoader.lookup(id);
				c += 10;
				k += TextRenderUtils.renderLeft(gameImageBuffer, "Name: " + def.getName(), c, k, 0xffff00);

				k += TextRenderUtils.renderLeft(gameImageBuffer, "ID: " + id + "", c, k, 0xffff00);

				k += TextRenderUtils.renderLeft(gameImageBuffer, "Type: " + type + " | Rot: " + orientation, c, k, 0xffff00);

				k += TextRenderUtils.renderLeft(gameImageBuffer, "Pos: " + x + ", " + y, c, k,  0xffff00);
			}

		}
	}

	public final void method118() {
		aBoolean831 = false;
		while (aBoolean962) {
			aBoolean831 = false;
			try {
				Thread.sleep(50L);
			} catch (Exception ex) {
			}
		}
	}

	public final void method144(int j, int k, int j1) {
		int l1 = 2048 - k & 0x7ff;
		int i2 = 2048 - j1 & 0x7ff;
		int j2 = 0;
		int k2 = 0;
		int l2 = j;

		if (l1 != 0) {
			int sin = Constants.SINE[l1];
			int cos = Constants.COSINE[l1];
			int i4 = k2 * cos - l2 * sin >> 16;
			l2 = k2 * sin + l2 * cos >> 16;
			k2 = i4;
		}

		if (i2 != 0) {
			int sin = Constants.SINE[i2];
			int cos = Constants.COSINE[i2];
			int j4 = l2 * sin + j2 * cos >> 16;
			l2 = l2 * cos - j2 * sin >> 16;
			j2 = j4;
		}

		// xCameraPos = l - j2;
		// zCameraPos = i1 - k2;
		// yCameraPos = k1 - l2;
		yCameraCurve = k;
		xCameraCurve = j1;
	}

	public final void renderView() {
		for (Chunk chunk : chunks) {
			chunk.processAnimableObjects();
		}

			int i = cameraRoll;
			/*
			 * if (anInt984 / 256 > i) { i = anInt984 / 256; }
			 */
			/*if (aBooleanArray876[4] && anIntArray1203[4] > i) {
				i = anIntArray1203[4];
			}*/
			int k = cameraYaw + anInt896 & 0x7ff;
			method144(600 + i * 3, i, k);
		

		int currentPlane = method120();
	

/*		int l = xCameraPos;
		int i1 = zCameraPos;
		int j1 = yCameraPos;
		int k1 = yCameraCurve;
		int l1 = xCameraCurve;*/

			Mesh.aBoolean1684 = true;
			Mesh.mouseX = mouseEventX;
			Mesh.mouseY = mouseEventY;
			gameImageBuffer.initializeRasterizer();
			GameRasterizer.getInstance().reset();
			if (cameraMoved) {
				if (Options.showCamera.get()) {
					SceneGraph.minimapUpdate = true;
				}
				Chunk current = this.getCurrentChunk();
				if (current != this.lastChunk) {
					lastChunk = current;
					SceneGraph.minimapUpdate = true;
				}
				cameraMoved = false;
			}
		Mesh.resourceCount = 0;
			for (Chunk chunk : chunks) {
				try {
					sceneGraph.setChunk(chunk);
					sceneGraph.renderScene(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, currentPlane, yCameraCurve);
					// xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve

				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// break;
			}
		if(Mesh.resourceCount > 0)
			hoveredUID = Mesh.resourceIDTag[Mesh.resourceCount - 1];
		else
			hoveredUID = null;
		
		for (Runnable r : Lists.newArrayList(SceneGraph.onCycleEnd)) {
			if(r != null) {
				try {
					r.run();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			SceneGraph.onCycleEnd.remove(r);
		}
		sceneGraph.cleanUpShortLivedObjects();
		drawDebugOverlay();
		gameImageBuffer.finalize();
	/*	xCameraPos = l;
		zCameraPos = i1;
		yCameraPos = j1;
		yCameraCurve = k1;
		xCameraCurve = l1;*/
	}
	
	public void drawGameImage() {
		gameImageBuffer.finalize();
		WritableImage finalImg = gameImageBuffer.finalImage;
		Platform.runLater(() -> {
			drawImage(finalImg);
		});
	}
	
	public void drawImage(WritableImage finalImg) {
		gameCanvas.drawImage(finalImg, 0, 0);
	}
	
	public void saveMinimapImage(File file) throws Exception {
		final Chunk chunk = this.getCurrentChunk();
		ImageIO.write(chunk.minimapImageBuffer.getImage(), "png", file);
	}
	
	public void saveMapFullImage(File file) throws Exception {
		
		
		ImageIO.write(SwingFXUtils.fromFXImage(fullMapCanvas.trimmedSnapshot(), null), "png", file);
	}
	
	public void drawMinimapImage() {
		final Chunk chunk = this.getCurrentChunk();
		chunk.minimapImageBuffer.finalize();
		Platform.runLater(() -> {
				mapCanvas.drawImage(chunk.minimapImageBuffer.getFXImage(), 0, 0);
	
		});
	}
	
	/*public void saveFullMapImage(File file) throws Exception {
		final Chunk chunk = this.getCurrentChunk();
		ImageIO.write(fullMapCanvas.getGraphics()., "png", file);
	}
	*/
	
	
	
	public SimpleBooleanProperty fullMapVisible = new SimpleBooleanProperty();

	public void drawMinimapFullImage() {
		if (!fullMapVisible.get())
			return;
		System.out.println("RENDER FULL MAP");
		Platform.runLater(() -> {
			for (Chunk chunk : chunks) {
				try {
					chunk.checkForUpdate();
					chunk.clearUpdates();
					chunk.drawMinimapScene(Options.currentHeight.get());
					chunk.drawMinimap();
					chunk.updated = false;
					chunk.minimapImageBuffer.finalize();

					int mapScale = Options.mapRegionSize.get() / 64;
					int xPos = chunk.offsetX * mapScale;
					int yPos = (int) (fullMapCanvas.getHeight() - Options.mapRegionSize.get()
							- (chunk.offsetY * mapScale));
					fullMapCanvas.drawImage(chunk.minimapImageBuffer.getFXImage(), xPos, yPos);
					if (Options.showBorders.get()) {
						// XXX
						fullMapCanvas.getGraphicsContext2D().setStroke(Color.RED);
						fullMapCanvas.getGraphicsContext2D().strokeRect(xPos, yPos, Options.mapRegionSize.get(), Options.mapRegionSize.get());
					}
					
					if(Options.showMapFileNames.get()) {
						fullMapCanvas.getGraphicsContext2D().setStroke(Color.BLACK);

						fullMapCanvas.getGraphicsContext2D().setFill(Color.YELLOW);
						fullMapCanvas.getGraphicsContext2D().setFont(javafx.scene.text.Font.font("JetBrains Mono", FontWeight.BOLD, 14));
						fullMapCanvas.getGraphicsContext2D().strokeText(chunk.tileMapName, xPos + 256 - 51, yPos + 21);
						fullMapCanvas.getGraphicsContext2D().strokeText(chunk.objectMapName, xPos + 256 - 51, yPos + 38);
						fullMapCanvas.getGraphicsContext2D().fillText(chunk.tileMapName, xPos + 256 - 51, yPos + 21);
						fullMapCanvas.getGraphicsContext2D().fillText(chunk.objectMapName, xPos + 256 - 51, yPos + 38);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Draw only the current chunk minimap

			}
			if (Options.showBorders.get()) {
				Chunk chunk = Client.getSingleton().getCurrentChunk();
				int mapScale = Options.mapRegionSize.get() / 64;
				int xPos = chunk.offsetX * mapScale;
				int yPos = (int) (fullMapCanvas.getHeight() - Options.mapRegionSize.get() - (chunk.offsetY * mapScale));
				fullMapCanvas.getGraphicsContext2D().setStroke(Color.BLUE);
				fullMapCanvas.getGraphicsContext2D().strokeRect(xPos, yPos, Options.mapRegionSize.get(), Options.mapRegionSize.get());
			}
			if (Options.showCamera.get()) {
				int mapScale = Options.mapRegionSize.get() / 64;
				int xCam = (Client.getSingleton().xCameraPos / 128) * mapScale;
				int yCam = (int) (fullMapCanvas.getHeight() - ((Client.getSingleton().yCameraPos / 128) * mapScale));
				fullMapCanvas.getGraphicsContext2D().setStroke(Color.YELLOW);
				fullMapCanvas.getGraphicsContext2D().strokeRect(xCam, yCam, mapScale, mapScale);
			}
		});
	}

	public final void loadChunks() {
		anInt985 = -1;
		unlinkCaches();
		
		SceneGraph.clearStates();
		sceneGraph.reset();

	
		for (Chunk chunk : chunks) {
			try {
				chunk.loadChunk();
			} catch (Exception exception) {
				exception.printStackTrace();
				chunks.clear();
			}
		}
		mapRegion.method171(sceneGraph);

		for(int z = 0;z<4;z++)
			sceneGraph.fill(z);
		SceneGraph.activePlane = 0;
		
		//ObjectDefinition.baseModels.clear();
		

	}
	
	public final void method51() {

		if (!aBoolean831) {
			aBoolean831 = true;
		}
	}

	public final boolean method54() {
		boolean ready = true;

		for (Chunk chunk : chunks) {
			boolean b = chunk.ready();
			if (!b) {
				ready = false;
			}
		}

		if (!ready)
			return false;

		loadState = LoadState.ACTIVE;
		loadChunks();
		return true;
	}

	public void mouseWheelDragged(int i, int j) {
		if (!mouseWheelDown)
			return;
		cameraRotationX += i * 3;
		cameraRotationZ += j << 1;
	}

	public final void prepareGameScreen() {
		if (gameImageBuffer != null)
			return;
		method118();
		gameImageBuffer = new ImageGraphicsBuffer((int) gameCanvas.getWidth(), (int) gameCanvas.getHeight(), GameRasterizer.getInstance());

		gameScreenReinitialized = true;
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public final void processLoadedResources(ResourceResponse response) {
		byte[] unzipped;
		try {
				unzipped = GZIPUtils.unzip(response.getData());
			
			if(unzipped == null) {
				unzipped = response.getData();
			}
			
			CacheFileType type = response.getRequest().getType();
			int file = response.getRequest().getFile();
			
			//System.out.println("UNZIPPED " + type + ":" + file + " ATTEMPTING TO DELIVER");
			lastDeliveredResource.set(response);
			ClientPluginLoader.forEach(plugin -> plugin.onResourceDelivered(response));
			
			if (type == CacheFileType.ANIMATION) {//TODO Fix animations
				if(Options.loadAnimations.get())
					try {
						FrameLoader.instance.load(file, unzipped);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final void pulse() {
		if (gameAlreadyLoaded || error || unableToLoad)
			return;
		pulseTick++;

		pulseGame();

		if (!runLater.isEmpty()) {
			new ArrayList<>(runLater).forEach(c -> {
				try {
					c.run();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			});
			runLater.clear();

		}
		
	}
	
	private List<Chunk> pendingChunks = Lists.newArrayList();

	public Vector2 getScreenPos(int worldX, int worldY, int height){
		int z = Options.currentHeight.get();
		int offsetHeight = getHeightAdjusted(worldX, worldY, z) - height;
		worldX -= this.xCameraPos;
		worldY -= this.yCameraPos;
		offsetHeight -= this.zCameraPos;
		if(this.yCameraCurve < 0)
			this.yCameraCurve = 0;

		int sineY = Constants.SINE[this.yCameraCurve];
		int sineX = Constants.SINE[this.xCameraCurve];
		int cosineY = Constants.COSINE[this.yCameraCurve];
		int cosineX = Constants.COSINE[this.xCameraCurve];
		int j2 = worldY * sineX + worldX * cosineX >> 16;
		worldY = worldY * cosineX - worldX * sineX >> 16;
		worldX = j2;
		j2 = offsetHeight * cosineY - worldY * sineY >> 16;
		worldY = offsetHeight * sineY + worldY * cosineY >> 16;
		offsetHeight = j2;

		if(worldY >= 50){
			GameRasterizer rasterizer = GameRasterizer.getInstance();
			int x = rasterizer.viewCenter.getX() + worldX * 512 / worldY;
			int y = rasterizer.viewCenter.getY() + offsetHeight * 512 / worldY;
			return new Vector2(x , y);
		} else {
			return new Vector2(-1, -1);
		}
	}

	public int getHeightAdjusted(int x, int y, int z){
		int groundX = x >> 7;
		int groundY = y >> 7;
		int k1 = x & 0x7f;
		int l1 = y & 0x7f;
		int i2 = mapRegion.tileHeights[z][groundX][groundY] * (128 - k1)
				+ mapRegion.tileHeights[z][groundX + 1][groundY] * k1 >> 7;
		int j2 = mapRegion.tileHeights[z][groundX][groundY + 1] * (128 - k1)
				+ mapRegion.tileHeights[z][groundX + 1][groundY + 1] * k1 >> 7;
		return i2 * (128 - l1) + j2 * l1 >> 7;
	}

	public final void pulseGame() {
		if (loadState == LoadState.CLIENT_INIT)
			return;

		if (lastMetaModifier != 0) {
			int time = (int) ((lastMouseClick - aLong1220) / 50);
			if (time > 4095) {
				time = 4095;
			}

			aLong1220 = lastMouseClick;
			int y = lastClickY;
			if (y < 0) {
				y = 0;
			} else if (y > gameCanvas.getHeight()) {
				y = (int) gameCanvas.getHeight();
			}

			int x = lastClickX;
			if (x < 0) {
				x = 0;
			} else if (x > gameCanvas.getWidth()) {
				x = (int) gameCanvas.getWidth();
			}

			if (lastMetaModifier == 2) {

			}

		}
		if(!pendingChunks.isEmpty()) {
			chunks.addAll(pendingChunks);
			pendingChunks.clear();
		}
		loadNextRegion();
		for (Chunk chunk : chunks) {
			chunk.method115();
		}
		
	

		tickDelta++;
		if (anInt917 != 0) {
			anInt916 += 20;
			if (anInt916 >= 400) {
				anInt917 = 0;
			}
		}

		
	
		
	
		for (Entry<Consumer<Client>, Long> entry : Lists.newArrayList(timedConsumers.entrySet())) {
			if(System.currentTimeMillis() >= entry.getValue()) {
				if(entry.getKey() != null) {
					entry.getKey().accept(this);
				}
				timedConsumers.remove(entry.getKey());
			}
		}
		timeoutCounter++;
		if (timeoutCounter > 3000) {
			//

			
			timeoutCounter = 0;
		}
	}

	public final void shutdown() {
		try {
			cache.close();
			singleton = null;
			mapScenes = null;
			mapFunctions = null;
			reset();
			hoveredUID = null;
			gameLoaded.set(false);
			runLater.clear();
			method118();
			MeshLoader.getSingleton().dispose();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final int tileHeight(int x, int y, int z) {
		int worldX = x >> 7;
		int worldY = y >> 7;
		for (Chunk chunk : chunks) {
			if (worldX >= chunk.offsetX && worldX <= chunk.offsetX + 64 && worldY >= chunk.offsetY
					&& worldY <= chunk.offsetY + 64) {
				worldX %= 64;
				worldY %= 64;
				int plane = z;
				/*if (plane < 3 && (chunk.tileFlags[1][worldX][worldY] & MapRegion.BRIDGE_TILE) != 0) {
					plane++;
				}*/

				int sizeX = x & 0x7f;
				int sizeY = y & 0x7f;
				int i2 = chunk.mapRegion.tileHeights[plane][worldX][worldY] * (128 - sizeX)
						+ chunk.mapRegion.tileHeights[plane][worldX + 1][worldY] * sizeX >> 7;
				int j2 = chunk.mapRegion.tileHeights[plane][worldX][worldY + 1] * (128 - sizeX)
						+ chunk.mapRegion.tileHeights[plane][worldX + 1][worldY + 1] * sizeX >> 7;

				return i2 * (128 - sizeY) + j2 * sizeY >> 7;
			}
		}

		return 0;
	}

	public static void updateChunkTiles() {
		SceneGraph.onCycleEnd.add(()-> {
				for(Chunk chunk : Client.getSingleton().chunks) {
					chunk.updated = true;
				}
				Client.getSingleton().sceneGraph.tileQueue.clear();
				Client.getSingleton().mapRegion.updateTiles();

				SceneGraph.minimapUpdate = true;
				System.out.println("UPDATED TILES");
		});
	}
	
	public DisplayCanvas gameCanvas;
	public boolean debug = false;
	public int fps;
	public DisplayCanvas mapCanvas;
	public DisplayCanvas fullMapCanvas;
	public int canvasHeight;
	public int canvasWidth;
	private Graphics graphics;

	public boolean hasFocus = true;
	public int[] keyStatuses = new int[128];
	public int lastClickX;
	public int lastClickY;
	public int lastMetaModifier;
	public long lastMouseClick;
	public int metaModifierHeld;
	public int metaModifierPressed;
	public int minimumSleepTime = 1;
	public long mouseClickTime;
	public int mouseEventX;
	public int mouseEventY;
	public boolean paintBlack = true;
	public int pressedX;
	public int pressedY;
	private long[] aLongArray7 = new long[10];
	private int lastProcessedKey;
	private int[] pressedKeys = new int[128];
	private int state;
	private int timeDelta = 20;
	private int unprocessedKeyCount;

	public int mouseWheelX;

	public int mouseWheelY;

	public boolean mouseWheelDown;
	public MapRegion mapRegion;

	public void drawLoadingText(int x, String string) {
		if (graphics == null) {
			prepareGameScreen();
			graphics = gameImageBuffer.getGraphics();
		}
		gameImageBuffer.initializeRasterizer();
		gameImageBuffer.clear(0);
		FontMetrics font = graphics.getFontMetrics(jetBrainsMono);
		if (paintBlack) {
			graphics.setColor(java.awt.Color.black);
			graphics.fillRect(0, 0, canvasWidth, canvasHeight);
			paintBlack = false;
		}
		java.awt.Color color = new java.awt.Color(140, 17, 17);
		int y = canvasHeight / 2 - 18;
		graphics.setColor(color);
		graphics.drawRect(canvasWidth / 2 - 152, y, 304, 34);
		graphics.fillRect(canvasWidth / 2 - 150, y + 2, x * 3, 31);
		graphics.setColor(java.awt.Color.black);
		graphics.fillRect(canvasWidth / 2 - 150 + x * 3, y + 2, 300 - x * 3, 31);
		graphics.setFont(jetBrainsMono);
		graphics.setColor(java.awt.Color.white);
		graphics.drawString(string, (canvasWidth - font.stringWidth(string)) / 2, y + 22);
		
		gameImageBuffer.finalize();
		drawGameImage();
	}

	public final void exit() {
		state = -2;
		shutdown();
		
	}

	public int getCanvasHeight() {
		return canvasHeight;
	}

	public int getCanvasWidth() {
		return canvasWidth;
	}

	public int getFps() {
		return fps;
	}

	public DisplayCanvas getGameCanvas() {
		return gameCanvas;
	}

	public Graphics getGraphics() {
		return gameImageBuffer.getGraphics();
	}

	public int[] getKeyStatuses() {
		return keyStatuses;
	}

	public int getLastClickX() {
		return lastClickX;
	}

	public int getLastClickY() {
		return lastClickY;
	}

	public int getLastMetaModifier() {
		return lastMetaModifier;
	}

	public long getLastMouseClick() {
		return lastMouseClick;
	}

	public int getMetaModifierHeld() {
		return metaModifierHeld;
	}

	public int getMetaModifierPressed() {
		return metaModifierPressed;
	}

	public int getMinimumSleepTime() {
		return minimumSleepTime;
	}

	public long getMouseClickTime() {
		return mouseClickTime;
	}

	public int getMouseEventX() {
		return mouseEventX;
	}

	public int getMouseEventY() {
		return mouseEventY;
	}

	public int getPressedX() {
		return pressedX;
	}

	public int getPressedY() {
		return pressedY;
	}

	public final void initFrame(int height, int width) {
		canvasWidth = width;
		canvasHeight = height;
		gameCanvas = new DisplayCanvas(canvasWidth, canvasHeight);
		mapCanvas = new DisplayCanvas(canvasWidth, canvasHeight, false);

		try (InputStream stream = Client.class.getResourceAsStream("/font/JetBrainsMono-Regular.ttf")) {

			javafx.scene.text.Font.loadFont(stream, -1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try (InputStream stream = Client.class.getResourceAsStream("/font/JetBrainsMono-Regular.ttf")) {

			Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
			robotoFont = new RSFont(new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB).createGraphics(),
					font.deriveFont(12.0f), true);
			jetBrainsMono = font.deriveFont(12.0f);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Thread t = new Thread(this);
		Thread keyInputs = new Thread(() -> {
			while (true) {
				keyInputLoop();
			}
		});
		keyInputs.setPriority(Thread.NORM_PRIORITY);
		keyInputs.start();
		t.setPriority(Thread.NORM_PRIORITY);
		t.start();
		return;
	}

	public void keyInputLoop() {
		if (loadState == LoadState.ACTIVE) {
			int speedMultiplier = KeyboardState.isKeyPressed(KeyCode.SHIFT) ? 10 : KeyboardState.isKeyPressed(KeyCode.CONTROL) ? 12 : 11;
			handleKeyInputs(speedMultiplier);
			

		}
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final int nextPressedKey() {
		int key = -1;
		if (unprocessedKeyCount != lastProcessedKey) {
			key = pressedKeys[lastProcessedKey];
			lastProcessedKey = lastProcessedKey + 1 & 0x7f;
		}

		return key;
	}

	public final void resetTimeDelta() {
		timeDelta = 1000;
	}

	@Override
	public void run() {

		if(GameRasterizer.getInstance() == null) {
			GameRasterizer.setInstance(new GameRasterizer());
		}
		drawLoadingText(0, "Loading...");
		load();

		int currentFrame = 0;
		int ratio = 256;
		int delay = 1;
		int cycle = 0;
		int exceptions = 0;

		for (int n = 0; n < 10; n++) {
			aLongArray7[n] = System.currentTimeMillis();
		}

		long currentTime = System.currentTimeMillis();
		while (state >= 0) {
			if (state > 0) {
				state--;

				if (state == 0) {
					exit();
					return;
				}
			}

			int lastRatio = ratio;
			int lastDelay = delay;
			ratio = 300;
			delay = 1;
			currentTime = System.currentTimeMillis();

			if (aLongArray7[currentFrame] == 0L) {
				ratio = lastRatio;
				delay = lastDelay;
			} else if (currentTime > aLongArray7[currentFrame]) {
				ratio = (int) (2560 * timeDelta / (currentTime - aLongArray7[currentFrame]));
			}

			if (ratio < 25) {
				ratio = 25;
			} else if (ratio > 256) {
				ratio = 256;
				delay = (int) (timeDelta - (currentTime - aLongArray7[currentFrame]) / 10L);
			}

			if (delay > timeDelta) {
				delay = timeDelta;
			}

			aLongArray7[currentFrame] = currentTime;
			currentFrame = (currentFrame + 1) % 10;

			if (delay > 1) {
				for (int k2 = 0; k2 < 10; k2++) {
					if (aLongArray7[k2] != 0L) {
						aLongArray7[k2] += delay;
					}
				}
			}

			if (delay < minimumSleepTime) {
				delay = minimumSleepTime;
			}

			try {
				Thread.sleep(delay);
			} catch (InterruptedException _ex) {
				exceptions++;
			}

			//for (; cycle < 256; cycle += ratio) {
				lastMetaModifier = metaModifierPressed;
				lastClickX = pressedX;
				lastClickY = pressedY;
				lastMouseClick = mouseClickTime;
				metaModifierPressed = 0;
				pulse();
				lastProcessedKey = unprocessedKeyCount;
			//}

			cycle &= 0xff;
			if (timeDelta > 0) {
				fps = 1000 * ratio / (timeDelta * 256);
			}

			draw();
			if (debug) {
				System.out.println("ntime:" + currentTime);
				for (int l2 = 0; l2 < 10; l2++) {
					int i3 = (currentFrame - l2 - 1 + 20) % 10;
					System.out.println("otim" + i3 + ":" + aLongArray7[i3]);
				}

				System.out.println("fps:" + fps + " ratio:" + ratio + " count:" + cycle);
				System.out.println("del:" + delay + " deltime:" + timeDelta + " mindel:" + minimumSleepTime);
				System.out.println("intex:" + exceptions + " opos:" + currentFrame);
				debug = false;
				exceptions = 0;
			}
		}

		if (state == -1) {
			exit();
		}
	}


}