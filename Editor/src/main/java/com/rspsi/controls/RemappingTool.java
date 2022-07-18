package com.rspsi.controls;

import com.rspsi.util.FXUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.chunk.Chunk;
import com.jagex.entity.object.ObjectGroup;
import com.jagex.io.Buffer;
import com.jagex.map.MapRegion;
import com.jagex.map.SceneGraph;
import com.jagex.map.object.DefaultWorldObject;
import com.jagex.map.object.GameObject;
import com.jagex.util.MapObjectData;
import com.jagex.util.MultiMapEncoder;
import com.jagex.util.ObjectKey;
import com.rspsi.core.misc.Location;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.util.FileUtils;
import com.rspsi.util.FilterMode;
import com.rspsi.util.RetentionFileChooser;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.displee.util.GZIPUtils;

public class RemappingTool extends Application {

	private Stage stage;
	private boolean okClicked;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/remapper.fxml"));
		
		loader.setController(this);
		Parent content = loader.load();

		Scene scene = new Scene(content);
		
		primaryStage.setTitle("Remapping Tool");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(ResourceLoader.getSingleton().getLogo64());

		primaryStage.setAlwaysOnTop(true);
		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();
		
		Consumer<TextField> finishBrowse = textField -> {
			File f = RetentionFileChooser.showOpenDialog(stage, FilterMode.JSON);
			if(f != null && f.exists()) {
				textField.setText(f.getAbsolutePath());
			}
		};
		Consumer<TextField> folderBrowse = textField -> {
			File f = RetentionFileChooser.showOpenFolderDialog(stage, null);
			if(f != null && f.exists()) {
				textField.setText(f.getAbsolutePath());
			}
		};
		
		mapsFolderBrowse.setOnAction(evt -> folderBrowse.accept(mapFolderText));
		jsonBrowse.setOnAction(evt -> finishBrowse.accept(jsonFileText));
		
		okButton.setOnAction(evt -> {
			stage.hide();
			okClicked = true;
		});
		cancelButton.setOnAction(evt -> {
			reset();
			stage.hide();
		});
	}
	
	public boolean valid() {
		return !mapFolderText.getText().isEmpty() && !jsonFileText.getText().isEmpty();
	}
	
	public void show() {
		reset();
		stage.sizeToScene();
		okButton.requestFocus();
		stage.showAndWait();
		if(!okClicked)
			reset();
	}

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;
    
    @FXML
    private TextField mapFolderText;

    @FXML
    private Button mapsFolderBrowse;

    @FXML
    private TextField jsonFileText;

    @FXML
    private Button jsonBrowse;
    
    public void reset() {
    	mapFolderText.setText("");
    	jsonFileText.setText("");
    	okClicked = false;
    	oldToNew.clear();
    }

    private Map<Integer, Integer> oldToNew = Maps.newConcurrentMap();

	public void doRemap() {
		Gson gson = new Gson();
		try(FileReader fr = new FileReader(jsonFileText.getText())){
			oldToNew.putAll(gson.fromJson(fr, new TypeToken<Map<Integer, Integer>>(){}.getType()));
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		File mapsFolder = new File(mapFolderText.getText());
		File outputDir = new File(mapsFolder, "remapped");
		if(mapsFolder.exists() && mapsFolder.isDirectory()) {
			outputDir.mkdir();
			List<Integer> validObjectFileIds = IntStream.range(0, 260).flatMap(x -> IntStream.range(0, 260).map(y -> MapIndexLoader.resolve(x, y, MapType.OBJECT))).filter(val -> val != -1).boxed().collect(Collectors.toList());
			
			Stream
			.of(mapsFolder.listFiles())
			.filter(file -> !file.isDirectory())
			.filter(FileUtils::isMapFile)
			.forEach(file -> {
				try {
				byte[] data = Files.readAllBytes(file.toPath());
				File outputFile = new File(outputDir, file.getName());
				if(FileUtils.isDatOrGzFile(file)) {
					
						if(FileUtils.isGzFile(file)) {
							data = GZIPUtils.unzip(data);
						}
						
						if(validObjectFileIds.contains(FileUtils.getNameAsInteger(file))) {
							Chunk chunk = new Chunk(0);
							chunk.objectMapData = data;
							chunk.objectMapId = FileUtils.getNameAsInteger(file);
							modifyChunks(outputFile, chunk);
						}
						
					} else {
						modifyChunks(outputFile , MultiMapEncoder.decode(data).toArray(new Chunk[0]));
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	public final void modifyChunks(File out, Chunk... chunks) {
		
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
		SceneGraph sceneGraph = new SceneGraph(64 * (chunkXLength), 64 * (chunkYLength), 4);
		MapRegion mapRegion = new MapRegion(sceneGraph, 64 * (chunkXLength), 64 * (chunkYLength));

		try(FileWriter fw = new FileWriter(out)){
			for(Chunk chunk : chunks) {
				chunk.scenegraph = sceneGraph;
				chunk.mapRegion = mapRegion;
				chunk.setLoaded(true);
				chunk.objectMapData = this.saveObjects(unpackAndRemap(chunk.objectMapData, chunk.offsetX, chunk.offsetY, oldToNew));


			}
			if(FileUtils.isDatOrGzFile(out)) {

				for(Chunk chunk : chunks) {

					byte[] data = chunk.objectMapData;
					System.out.println("data size " + data.length);
					if (FileUtils.isGzFile(out)) {
						data = GZIPUtils.gzipBytes(data);
						System.out.println("gz size " + data.length);
					}
					Files.write(out.toPath(), data);
				}
			} else {
				byte[] data = MultiMapEncoder.encodeShallow(Lists.newArrayList(chunks));
				Files.write(out.toPath(), data);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public final Map<Location, List<MapObjectData>> unpackAndRemap(byte[] data, int localX, int localY, Map<Integer, Integer> remapping) {
		//System.out.println("Width: " + width + " Length: " + length);
		Map<Location, List<MapObjectData>> map = Maps.newConcurrentMap();
		decoding: {
			Buffer buffer = new Buffer(data);
			int id = -1;

			do {
				int idOffset = buffer.readUSmart();
				if (idOffset == 0) {
					break decoding;
				}

				id += idOffset;
				int position = 0;

				do {
					int offset = buffer.readUSmart();
					if (offset == 0) {
						break;
					}

					position += offset - 1;
					int yOffset = position & 0x3f;
					int xOffset = position >> 6 & 0x3f;
					int z = position >> 12;

					if (z >= 4) {
						z = 3;
					}
					int config = buffer.readUByte();
					int type = config >> 2;
					int orientation = config & 3;
					int x = xOffset + localX;
					int y = yOffset + localY;

					int newId = remapping.getOrDefault(id, id);
					Location location = new Location(x, y, z);
					MapObjectData dataObj = new MapObjectData(newId, x, y, z, type, orientation);
					List<MapObjectData> tileObjects = map.getOrDefault(location, Lists.newArrayList());
					tileObjects.add(dataObj);
					map.put(location, tileObjects);
					
				} while (true);
			} while (true);
		}
		return map;
	}
	
	public byte[] saveObjects(Map<Location, List<MapObjectData>> map) {//TODO Expand this
		TreeMap<Integer, ObjectGroup> objectGroupMap = new TreeMap<>();
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 64; x++) {
				for (int y = 0; y < 64; y++) {
					List<MapObjectData> tileObjs = map.get(new Location(x, y, z));
					if(tileObjs == null)
						continue;
					for (MapObjectData object : tileObjs) {
						int objectId = object.getId();
						
						ObjectGroup objectGroup = new ObjectGroup(objectId);
						if (objectGroupMap.get(objectId) != null) {
							objectGroup = objectGroupMap.get(objectId);
						}
						DefaultWorldObject defaultWorldObject = new GameObject(new ObjectKey(object.getX(), object.getY(), object.getId(), object.getType(), object.getOrientation(), false, false), object.getX(), object.getY(), object.getZ());
						defaultWorldObject.setPlane(z);
						objectGroup.addObject(defaultWorldObject);
						objectGroupMap.put(objectId, objectGroup);
					}
				}
			}
		}
		Buffer buff = new Buffer(new byte[131072]);

		int lastObjectId = -1;
		for (Entry<Integer, ObjectGroup> entry : objectGroupMap.entrySet()) {

			int objectId = entry.getKey();
			ObjectGroup group = entry.getValue();
			if (group != null) {
				if(group.getObjects().size() <= 0)
					System.out.println("WARNING: 0 objects for id " + objectId);
				int newObj = objectId - lastObjectId;

				buff.writeUSmart(newObj);
				group.sort();
				int previousLocHash = 0;
				for (DefaultWorldObject obj : group.getObjects()) {

					int locHash = obj.getLocHash();

					int newLocHash = locHash - previousLocHash + 1;
				//	System.out.println("NEW LOC " + obj.getLocHash());
					if (previousLocHash != locHash) {
						buff.writeUSmart(newLocHash);
					} else {
						buff.writeUSmart(1);
					}
					buff.writeByte(obj.getConfig());
					previousLocHash = locHash;
				}
				buff.writeUSmart(0);
				lastObjectId = objectId;
			}

		}

		buff.writeUSmart(0);
		byte[] data = Arrays.copyOf(buff.getPayload(), buff.getPosition());

		return data;
	}

}
