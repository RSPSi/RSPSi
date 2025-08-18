package com.rspsi.controls;

import com.google.common.collect.Maps;
import com.jagex.chunk.Chunk;
import com.jagex.io.Buffer;
import com.rspsi.resources.ResourceLoader;
import com.rspsi.util.FXUtils;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.jagex.map.MapRegion.calculateHeight;

public class ConvertLandscapeTool extends Application {

	private Stage stage;
	private boolean okClicked;


	public byte[][][] manualTileHeight;
	public byte[][][] overlayShapes;
	private int[] saturations;
	public byte[][][] shading;
	public byte[][][] tileFlags;
	public int[][][] tileHeights;
	public short[][][] overlays;
	public byte[][][] overlayOrientations;
	public short[][][] underlays;

	int width = 64;
	int length = 64;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/convertlandscape.fxml"));
		
		loader.setController(this);
		Parent content = loader.load();

		Scene scene = new Scene(content);
		
		primaryStage.setTitle("Convert Landscape Tool");
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
		return !mapFolderText.getText().isEmpty();
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

    public void reset() {
    	mapFolderText.setText("");
    	okClicked = false;
    	oldToNew.clear();
    }

    private Map<Integer, Integer> oldToNew = Maps.newConcurrentMap();

	public void doRencode() {
		File mapsFolder = new File(mapFolderText.getText());
		File outputDir = new File(mapsFolder, "converted");
		if (mapsFolder.exists() && mapsFolder.isDirectory()) {
			outputDir.mkdir();
			Stream.of(mapsFolder.listFiles())
					.filter(file -> !file.isDirectory())
					.filter(FileUtils::isMapFile)
					.forEach(file -> {
						try {
							byte[] data = Files.readAllBytes(file.toPath());
							File outputFile = new File(outputDir, file.getName());
							if (FileUtils.isDatOrGzFile(file)) {
								if (FileUtils.isGzFile(file)) {
									data = GZIPUtils.unzip(data);
								}

								Chunk chunk = new Chunk(0);
								chunk.tileMapData = data;
								createData();

								unpackTiles(chunk.tileMapData, chunk.offsetX, chunk.offsetY, chunk.regionX, chunk.regionY);

								byte[] tileMap = saveTerrainBlock(chunk);

								if (file.getName().endsWith(".gz")) {
									tileMap = GZIPUtils.gzipBytes(tileMap);
								}

								Files.write(outputFile.toPath(), tileMap);

							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
		}
	}

	public void createData() {
		tileHeights = new int[4][width + 1][length + 1];
		tileFlags = new byte[4][width][length];
		underlays = new short[4][width][length];
		overlays = new short[4][width][length];
		manualTileHeight = new byte[4][width][length];
		overlayShapes = new byte[4][width][length];
		overlayOrientations = new byte[4][width][length];
		shading = new byte[4][width + 1][length + 1];
		saturations = new int[length];
	}

	public final void decodeMapData(Buffer buffer, int x, int y, int z, int regionX, int regionY, int orientation) {// XXX
		if (x >= 0 && x < width && y >= 0 && y < length) {
			tileFlags[z][x][y] = 0;
			do {
				int type = buffer.readUShort();

				if (type == 0) {
					manualTileHeight[z][x][y] = 0;
					if (z == 0) {
						tileHeights[0][x][y] = -calculateHeight(0xe3b7b + x + regionX, 0x87cce + y + regionY) * 8;
					} else {
						tileHeights[z][x][y] = tileHeights[z - 1][x][y] - 240;
					}

					return;
				} else if (type == 1) {
					manualTileHeight[z][x][y] = 1;
					int height = buffer.readUByte();
					if (height == 1) {
						height = 0;
					}
					if (z == 0) {
						tileHeights[0][x][y] = -height * 8;
					} else {
						tileHeights[z][x][y] = tileHeights[z - 1][x][y] - height * 8;
					}

					return;
				} else if (type <= 49) {
					overlays[z][x][y] = (short) buffer.readShort();
					overlayShapes[z][x][y] = (byte) ((type - 2) / 4);
					overlayOrientations[z][x][y] = (byte) (type - 2 + orientation & 3);
				} else if (type <= 81) {
					tileFlags[z][x][y] = (byte) (type - 49);
				} else {
					underlays[z][x][y] = (short) (type - 81);
				}
			} while (true);
		}

		do {
			int in = buffer.readUShort();
			if (in == 0) {
				break;
			} else if (in == 1) {
				buffer.readUByte();
				return;
			} else if (in <= 49) {
				buffer.readUShort();
			}
		} while (true);
	}

	public final void decodeOldMapData(Buffer buffer, int x, int y, int z, int regionX, int regionY, int orientation) {// XXX
		if (x >= 0 && x < width && y >= 0 && y < length) {
			tileFlags[z][x][y] = 0;
			manualTileHeight[z][x][y] = 0;
			tileHeights[z][x][y] = 0;
			overlays[z][x][y] = 0;
			overlayShapes[z][x][y] = 0;
			overlayOrientations[z][x][y] = 0;
			underlays[z][x][y] = 0;
			do {
				int type = buffer.readUByte();

				if (type == 0) {
					manualTileHeight[z][x][y] = 0;
					if (z == 0) {
						tileHeights[0][x][y] = -calculateHeight(0xe3b7b + x + regionX, 0x87cce + y + regionY) * 8;
					} else {
						tileHeights[z][x][y] = tileHeights[z - 1][x][y] - 240;
					}

					return;
				} else if (type == 1) {
					manualTileHeight[z][x][y] = 1;
					int height = buffer.readUByte();
					if (height == 1) {
						height = 0;
					}
					if (z == 0) {
						tileHeights[0][x][y] = -height * 8;
					} else {
						tileHeights[z][x][y] = tileHeights[z - 1][x][y] - height * 8;
					}

					return;
				} else if (type <= 49) {
					overlays[z][x][y] = (short) (buffer.readByte() & 0xFF);
					overlayShapes[z][x][y] = (byte) ((type - 2) / 4);
					overlayOrientations[z][x][y] = (byte) (type - 2 + orientation & 3);
				} else if (type <= 81) {
					tileFlags[z][x][y] = (byte) (type - 49);
				} else {
					underlays[z][x][y] = (short) (type - 81);
				}
			} while (true);
		}

		do {
			int in = buffer.readUByte();
			if (in == 0) {
				break;
			} else if (in == 1) {
				buffer.readUByte();
				return;
			} else if (in <= 49) {
				buffer.readUByte();
			}
		} while (true);
	}



	public final void unpackTiles(byte[] data, int dX, int dY, int regionX, int regionY) {

		Buffer buffer = new Buffer(data);
		final int position = buffer.position;

		try {
			for (int z = 0; z < 4; z++) {
				for (int localX = 0; localX < 64; localX++) {
					for (int localY = 0; localY < 64; localY++) {
						decodeMapData(buffer, localX + dX, localY + dY, z, regionX, regionY, 0);
					}
				}
			}
		} catch (Exception ignored) {
			buffer.position = position;
			for (int z = 0; z < 4; z++) {
				for (int localX = 0; localX < 64; localX++) {
					for (int localY = 0; localY < 64; localY++) {
						decodeOldMapData(buffer, localX + dX, localY + dY, z, regionX, regionY, 0);
					}
				}
			}
		}

		this.setHeights();// XXX Fix for ending of region sloping down

	}

	public void setHeights() {
		for(int z = 0;z<4;z++) {
			for(int y = 0;y<=length;y++) {
				tileHeights[z][width][y] = tileHeights[z][width - 1][y];
			}


			for(int x = 0;x<=width;x++) {
				tileHeights[z][x][length] = tileHeights[z][x][length - 1];
			}

		}

	}


	public byte[] saveTerrainBlock(Chunk chunk) {
		Buffer buffer = new Buffer(new byte[131072]);
		for (int level = 0; level < 4; level++) {
			for (int x = chunk.offsetX; x < chunk.offsetX + 64; x++) {
				for (int y = chunk.offsetY; y < chunk.offsetY + 64; y++) {
					saveTerrainTile(level, x, y, buffer);
				}

			}

		}

		byte[] data = Arrays.copyOf(buffer.getPayload(), buffer.getPosition());
		return data;
	}

	private void saveTerrainTile(int level, int x, int y, Buffer buffer) {
		if (overlays[level][x][y] != 0) {
			buffer.writeShort(overlayShapes[level][x][y] * 4 + (overlayOrientations[level][x][y] & 3) + 2);
			buffer.writeShort(overlays[level][x][y] & 0x7FFF);
		}
		if (tileFlags[level][x][y] != 0) {
			buffer.writeShort(tileFlags[level][x][y] + 49);
		}
		if (underlays[level][x][y] != 0) {
			buffer.writeShort((underlays[level][x][y] & 0x7FFF) + 81);
		}
		if (manualTileHeight[level][x][y] == 1 || level == 0) {
			buffer.writeShort(1);
			if (level == 0) {
				buffer.writeByte(-tileHeights[level][x][y] / 8);
			} else {
				buffer.writeByte(-(tileHeights[level][x][y] - tileHeights[level - 1][x][y]) / 8);
			}
		} else {
			buffer.writeShort(0);
		}
	}

}
