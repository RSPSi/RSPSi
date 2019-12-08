package com.jagex.chunk;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

import com.rspsi.cache.CacheFileType;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.common.collect.Lists;
import com.jagex.Client;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.def.RSArea;
import com.jagex.cache.graphics.IndexedImage;
import com.jagex.cache.graphics.Sprite;
import com.jagex.cache.loader.config.RSAreaLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.draw.ImageGraphicsBuffer;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.object.AnimableObject;
import com.jagex.map.MapRegion;
import com.jagex.map.SceneGraph;
import com.jagex.map.object.SpawnedObject;
import com.jagex.net.ResourceResponse;
import com.jagex.util.BitFlag;
import com.jagex.util.ObjectKey;
import com.rspsi.options.Options;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Setter;

public class Chunk {
	
	private static final GameRasterizer rasterizer = new GameRasterizer();
	
	static {
		rasterizer.setBrightness(0.6);
	}
	
	private Client client;
	
	protected Chunk() {
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onResourceResponse(ResourceResponse response) {
		if(response.getRequest().getType() == CacheFileType.MAP) {
			int fileId = response.getRequest().getFile();
			if(fileId == tileMapId) {
				tileMapData = response.decompress();
				resourceDelivered.set(true);
			} else if(fileId == objectMapId) {
				objectMapData = response.decompress();
				resourceDelivered.set(true);
			}
		}
	}

	private final void drawOnMinimap(Sprite sprite, int x, int y, boolean selected) {
		if (sprite == null)
			return;

		sprite.drawSprite(rasterizer, x * 4 - sprite.getResizeWidth() / 2, 256 - (y * 4 + sprite.getResizeHeight() / 2), selected);

	}

	public int tileMapId = -1;
	public int objectMapId = -1;
	public String objectMapName;
	public String tileMapName;
	public int regionHash;
	public byte[] tileMapData;

	public byte[] objectMapData;

	private int mapObjectCount;

	public int offsetX, offsetY;

	public ImageGraphicsBuffer minimapImageBuffer = new ImageGraphicsBuffer(256, 256, rasterizer);
	
	public int regionX, regionY;

	private static int[] mapObjectX = new int[1000];
	private static int[] mapObjectY = new int[1000];
	private static byte[] mapObjectSelected = new byte[1000];
	protected Sprite largeMinimapSprite = new Sprite(256, 256);

	private static Sprite[] mapObjectSprites = new Sprite[1000];
	private ArrayDeque<AnimableObject> incompleteAnimables;

	private ArrayDeque<SpawnedObject> spawns;

	public MapRegion mapRegion;
	
	protected BooleanProperty resourceDelivered = new SimpleBooleanProperty(false); 
	
	public boolean updated = true;
	

	public BooleanProperty resourceHasBeenDelivered() {
		return resourceDelivered;
	}

	private boolean ready = false;

	public void init(Client client) {

		EventBus.getDefault().register(this);
		this.client = client;
		this.scenegraph = client.sceneGraph;
		this.mapRegion = client.mapRegion;
		
		incompleteAnimables = new ArrayDeque<AnimableObject>();
		spawns = new ArrayDeque<SpawnedObject>();
		//sceneGraph = new SceneGraph(this, 64, 64, 4);
	
		
	}
	public Chunk(int hash) {
		this.regionHash = hash;
		this.regionX = (hash >> 8) & 0xff;
		this.regionY = hash & 0xff;
	}
	
	public void drawMinimap() {
		if(!updated)
			return;
		minimapImageBuffer.initializeRasterizer();

		largeMinimapSprite.drawSprite(rasterizer, 0, 0);
		
			for (int j5 = 0; j5 < mapObjectCount; j5++) {
				int k = mapObjectX[j5];
				int i3 = mapObjectY[j5];
				boolean selected = mapObjectSelected[j5] == 1;
				drawOnMinimap(mapObjectSprites[j5], k, i3, selected);
			}

		/*
		 * for(int x = 0;x<512;x+=4){ for(int y = 0;y<512;y+=4){ Raster.drawRectangle(x,
		 * y, 5, 5, Color.black.getRGB()); } }
		 */
		// Raster.fillRectangle(97, 78, 3, 3, 0xffffff);//Player dot
	}
	

	public void drawMinimapScene(int plane) {
		if(!updated)
			return;

        int[] raster = largeMinimapSprite.getRaster();
		int pixels = raster.length;
		for (int i = 0; i < pixels; i++) {
			raster[i] = 0;
		}

		for (int y = 0; y < 64; y++) {
			int i1 = (63 - y) * 256 * 4;
			for (int x = 0; x < 64; x++) {
				if ((mapRegion.tileFlags[plane][offsetX + x][offsetY + y] & 0x18) == 0) {
					scenegraph.drawMinimapTile(raster, offsetX + x, offsetY + y, plane, i1, 256);
				}

				if (plane < 3 && (mapRegion.tileFlags[plane + 1][offsetX + x][offsetY + y] & 8) != 0) {
					scenegraph.drawMinimapTile(raster, offsetX + x, offsetY + y, plane + 1, i1, 256);
				}
				i1 += 4;
			}
		}
		
		int j1 = ((238 + (int) (Math.random() * 0D)) - 10 << 16) + ((238 + (int) (Math.random() * 0D)) - 10 << 8)
				+ ((238 + (int) (Math.random() * 0D)) - 10);
		int l1 = (238 + (int) (Math.random() * 0D)) - 10 << 16;
		largeMinimapSprite.initRaster(rasterizer);
		if(Options.showObjects.get()) {
			for (int y = 0; y < 64; y++) {
				for (int x = 0; x < 64; x++) {
					if ((mapRegion.tileFlags[plane][offsetX + x][offsetY + y] & 0x18) == 0) {
						method50(x, y, plane, j1, l1);
					}
					if (plane < 3 && (mapRegion.tileFlags[plane + 1][offsetX + x][offsetY + y] & 8) != 0) {
						method50(x, y, plane + 1, j1, l1);
					}
				}
			}
	
			mapObjectCount = 0;
			for (int x = 0; x < 64; x++) {
				for (int y = 0; y < 64; y++) {
					ObjectKey key = scenegraph.getFloorDecorationKey(offsetX + x, offsetY +y, plane);
					
					if (key != null) {
						byte selected = (byte) (scenegraph.getTileFloorDecoration(offsetX + x, offsetY + y, plane).isSelected() ? 1 : 0);
						int id = key.getId();
						ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);
						if(definition != null) {
							if(definition.getAreaId() != -1) {
								RSArea area = RSAreaLoader.get(definition.getAreaId());
								int function = area.getSpriteId();
								
								if (function >= 0) {
									int viewportX = x;
									int viewportY = y;
			
									mapObjectSprites[mapObjectCount] = client.getCache().getSprite(function);
									mapObjectSelected[mapObjectCount] = selected;
									mapObjectX[mapObjectCount] = viewportX;
									mapObjectY[mapObjectCount] = viewportY;
									mapObjectCount++;
								}
							} else {

								int function = definition.getMinimapFunction();
			
								if (function >= 0 && function < Client.mapFunctions.length) {
									int viewportX = x;
									int viewportY = y;
			
									mapObjectSprites[mapObjectCount] = Client.mapFunctions[function];
									mapObjectSelected[mapObjectCount] = selected;
									mapObjectX[mapObjectCount] = viewportX;
									mapObjectY[mapObjectCount] = viewportY;
									mapObjectCount++;
								}
							}
						}
					}
				}
			}
		}
	}

	public final void method115() {
		spawns.forEach(spawn -> {
			if (spawn.getLongetivity() > 0) {
				spawn.setLongetivity(spawn.getLongetivity() - 1);
			}

			if (spawn.getLongetivity() == 0) {
				if (spawn.getPreviousId() < 0
						|| MapRegion.objectReady(spawn.getPreviousId(), spawn.getPreviousType())) {
					removeObject(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getGroup(),
							spawn.getPreviousOrientation(), spawn.getPreviousType(), spawn.getPreviousId());
					spawn.unlink();
				}
			} else {
				if (spawn.getDelay() > 0) {
					spawn.setDelay(spawn.getDelay() - 1);
				}
				if (spawn.getDelay() == 0 && spawn.getX() >= 1 && spawn.getY() >= 1 && spawn.getX() <= 102
						&& spawn.getY() <= 102
						&& (spawn.getId() < 0 || MapRegion.objectReady(spawn.getId(), spawn.getType()))) {
					removeObject(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getGroup(), spawn.getOrientation(),
							spawn.getType(), spawn.getId());
					spawn.setDelay(-1);
					if (spawn.getId() == spawn.getPreviousId() && spawn.getPreviousId() == -1) {
						spawn.unlink();
					} else if (spawn.getId() == spawn.getPreviousId()
							&& spawn.getOrientation() == spawn.getPreviousOrientation()
							&& spawn.getType() == spawn.getPreviousType()) {
						spawn.unlink();
					}
				}
			}
		
		});
		
		spawns.clear();

	}

	public void loadChunk() {
	

			scenegraph.setChunk(this);
			incompleteAnimables.clear();
			//scenegraph.reset();
			
		/*	for (int z = 0; z < 4; z++) {
				for (int x = 0; x < 64; x++) {
					for (int y = 0; y < 64; y++) {
						mapRegion.tileFlags[z][x][y] = 0;
					}
				}
			}*/
			System.out.println("Chunk offset " + offsetX + ":" + offsetY);
			// XXX
			if (tileMapData != null) {
				System.out.println("tilemap data not null");
				mapRegion.unpackTiles(tileMapData, offsetX, offsetY, regionX, regionY);

			} /*else if (regionY < 700) {//XXX Figure out why this exists
				mapRegion.method174(0, 0, 64, 64);
			}*/
			if (objectMapData != null) {
				System.out.println("object data not null");
				mapRegion.unpackObjects(scenegraph, objectMapData, offsetX, offsetY);

			}

			

			method63();
			this.loaded = true;

			updated = true;
	}

	public final void method50(int x, int y, int z, int nullColour, int defaultColour) {
		ObjectKey key = scenegraph.getWallKey(offsetX + x, offsetY + y, z);

		if (key != null) {
			int id = key.getId();
			int type = key.getType();
			int orientation = key.getOrientation();

			int colour = nullColour;
			if (key.isInteractive()) {
				colour = defaultColour;
			}

			int[] raster = largeMinimapSprite.getRaster();
			int k4 = x * 4 + (63 - y) * 256 * 4;
			ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);

			if (definition != null && definition.getMapscene() != -1 && definition.getMapscene() < Client.mapScenes.length) {
				Sprite image = Client.mapScenes[definition.getMapscene()];
				if (image != null) {
					int dx = (definition.getWidth() * 4 - image.getWidth()) / 2;
					int dy = (definition.getLength() * 4 - image.getHeight()) / 2;
					image.drawSprite(rasterizer, 48 + x * 4 + dx, 48 + (63 - y - definition.getLength()) * 4 + dy);
				}
			} else {
				if (type == 0 || type == 2) {
					if (orientation == 0) {
						raster[k4] = colour;
						raster[k4 + 256] = colour;
						raster[k4 + 256 * 2] = colour;
						raster[k4 + 256 * 3] = colour;
					} else if (orientation == 1) {
						raster[k4] = colour;
						raster[k4 + 1] = colour;
						raster[k4 + 2] = colour;
						raster[k4 + 3] = colour;
					} else if (orientation == 2) {
						raster[k4 + 3] = colour;
						raster[k4 + 3 + 256] = colour;
						raster[k4 + 3 + 256 * 2] = colour;
						raster[k4 + 3 + 256 * 3] = colour;
					} else if (orientation == 3) {
						raster[k4 + 256 * 3] = colour;
						raster[k4 + 256 * 3 + 1] = colour;
						raster[k4 + 256 * 3 + 2] = colour;
						raster[k4 + 256 * 3 + 3] = colour;
					}
				}
				if (type == 3) {
					if (orientation == 0) {
						raster[k4] = colour;
					} else if (orientation == 1) {
						raster[k4 + 3] = colour;
					} else if (orientation == 2) {
						raster[k4 + 3 + 256 * 3] = colour;
					} else if (orientation == 3) {
						raster[k4 + 256 * 3] = colour;
					}
				}
				if (type == 2) {
					if (orientation == 3) {
						raster[k4] = colour;
						raster[k4 + 256] = colour;
						raster[k4 + 256 * 2] = colour;
						raster[k4 + 256 * 3] = colour;
					} else if (orientation == 0) {
						raster[k4] = colour;
						raster[k4 + 1] = colour;
						raster[k4 + 2] = colour;
						raster[k4 + 3] = colour;
					} else if (orientation == 1) {
						raster[k4 + 3] = colour;
						raster[k4 + 3 + 256] = colour;
						raster[k4 + 3 + 256 * 2] = colour;
						raster[k4 + 3 + 256 * 3] = colour;
					} else if (orientation == 2) {
						raster[k4 + 256 * 3] = colour;
						raster[k4 + 256 * 3 + 1] = colour;
						raster[k4 + 256 * 3 + 2] = colour;
						raster[k4 + 256 * 3 + 3] = colour;
					}
				}
			}
		}

		key = scenegraph.getInteractableObjectKey(offsetX +x, offsetY + y, z);
		if (key != null) {
			int id = key.getId();
			int type = key.getType();
			int orientation = key.getOrientation();
			ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);

			if (definition != null && definition.getMapscene() != -1 && definition.getMapscene() < Client.mapScenes.length) {
				Sprite image = Client.mapScenes[definition.getMapscene()];
				if (image != null) {
					int j5 = (definition.getWidth() * 4 - image.getWidth()) / 2;
					int k5 = (definition.getLength() * 4 - image.getHeight()) / 2;
					image.drawSprite(rasterizer, x * 4 + j5, (63 - y - definition.getLength()) * 4 + k5);
				}
			} else if (type == 9) {
				int colour = 0xeeeeee;
				if (key.isInteractive()) {
					colour = 0xee0000;
				}

                int[] raster = largeMinimapSprite.getRaster();
				int index = x * 4 + (63 - y) * 256 * 4;
				if (orientation == 0 || orientation == 2) {
					raster[index + 256 * 3] = colour;
					raster[index + 256 * 2 + 1] = colour;
					raster[index + 256 + 2] = colour;
					raster[index + 3] = colour;
				} else {
					raster[index] = colour;
					raster[index + 256 + 1] = colour;
					raster[index + 256 *2 + 2] = colour;
					raster[index + 256 * 3 + 3] = colour;
				}
			}
		}

		key = scenegraph.getFloorDecorationKey(offsetX +x, offsetY + y, z);
		if (key != null) {
			int id = key.getId();
			ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);
			if (definition != null && definition.getMapscene() != -1 && definition.getMapscene() < Client.mapScenes.length) {
				Sprite image = Client.mapScenes[definition.getMapscene()];
				if (image != null) {
					int i4 = (definition.getWidth() * 4 - image.getWidth()) / 2;
					int j4 = (definition.getLength() * 4 - image.getHeight()) / 2;
					image.drawSprite(rasterizer, x * 4 + i4, (63 - y - definition.getLength()) * 4 + j4);
				}
			}
		}
	}

	private final void method63() {
		spawns.forEach(spawn -> {
			if (spawn.getLongetivity() == -1) {
				spawn.setDelay(0);
				setPreviousObject(spawn);
			} else {
				spawn.unlink();
			}
		});
		spawns.clear();
	}

	public final void processAnimableObjects() {
		List<AnimableObject> completed = Lists.newArrayList();
		incompleteAnimables.stream()
				.filter(object -> object.getZ() != Options.currentHeight.get() || object.isTransformationCompleted())
				.forEach(object -> completed.add(object));

		incompleteAnimables.removeAll(completed);
		completed.clear();

		incompleteAnimables.forEach(object -> {
			if (Client.pulseTick >= object.getTick()) {
				object.nextAnimationStep(Client.tickDelta);
				if (!object.isTransformationCompleted()) {
					scenegraph.addEntity(object.getX(), object.getY(), object.getZ(), object, 0, null,
							object.getRenderHeight(), 60, false, false);
				}
			}
		});

	}



	public boolean ready() {
		if (ready)
			return true;
		if(newMap)
			return true;
		if (tileMapId != -1 && tileMapData == null) {
			//System.out.println("TILE MAP ID: " + tileMapId + " NULL");
			return false;
		}
		if (objectMapId != -1 && objectMapData == null) {
			//System.out.println("OBJECT MAP ID: " + tileMapId + " NULL");
			return false;
		} else if(objectMapId != -1 && objectMapData != null)
			if (!MapRegion.objectsReady(objectMapData, 0, 0))
				return false;

		loadChunk();
		ready = true;
		return true;
	}

	private final void removeObject(int x, int y, int z, int group, int previousOrientation, int previousType,
			int previousId) {
		if (x >= 1 && y >= 1 && x <= 102 && y <= 102) {

			ObjectKey key = null;
			if (group == 0) {
				key = scenegraph.getWallKey(x, y, z);
			} else if (group == 1) {
				key = scenegraph.getWallDecorationKey(x, y, z);
			} else if (group == 2) {
				key = scenegraph.getInteractableObjectKey(x, y, z);
			} else if (group == 3) {
				key = scenegraph.getFloorDecorationKey(x, y, z);
			}

			if (key != null) {
				int id = key.getId();
				int type = key.getType();
				int orientation = key.getOrientation();

				if (group == 0) {
					scenegraph.removeWall(x, y, z);

				} else if (group == 1) {
					scenegraph.removeWallDecoration(x, y, z);
				} else if (group == 2) {
					scenegraph.removeObject(x, y, z);
					ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);
					if (x + definition.getWidth() > 63 || y + definition.getWidth() > 63
							|| x + definition.getLength() > 63 || y + definition.getLength() > 63)
						return;
					
				} else if (group == 3) {
					scenegraph.removeFloorDecoration(x, y, z);
					
				}
			}

			if (previousId >= 0) {
				int plane = z;
				/*if (plane < 3 && (tileFlags[1][x][y] & 2) == 2) {
					plane++;
				}*/

				mapRegion.spawnObjectToWorld(scenegraph, previousId, x, y, plane, previousType, previousOrientation, false);
			}
		}
	}

	private final void setPreviousObject(SpawnedObject spawn) {
		ObjectKey key = null;
		int id = -1;
		int type = 0;
		int orientation = 0;

		if (spawn.getGroup() == 0) {
			key = scenegraph.getWallKey(spawn.getX(), spawn.getY(), spawn.getZ());
		} else if (spawn.getGroup() == 1) {
			key = scenegraph.getWallDecorationKey(spawn.getX(), spawn.getY(), spawn.getZ());
		} else if (spawn.getGroup() == 2) {
			key = scenegraph.getInteractableObjectKey(spawn.getX(), spawn.getY(), spawn.getZ());
		} else if (spawn.getGroup() == 3) {
			key = scenegraph.getFloorDecorationKey(spawn.getX(), spawn.getY(), spawn.getZ());
		}

		if (key != null) {// TODO update this
			id = key.getId();
			type = key.getType();
			orientation = key.getOrientation();
		}

		spawn.setPreviousId(id);
		spawn.setPreviousType(type);
		spawn.setPreviousOrientation(orientation);
	}
	
	private boolean newMap;
	
	public boolean isNewMap() {
		return newMap;
	}

	public void setNewMap(boolean b) {
		newMap = b;
	}
	
	@Setter
	protected boolean loaded;
	public SceneGraph scenegraph;

	public boolean hasLoaded() {
		return loaded;
	}
	
	public boolean inChunk(int wTileX, int wTileY) {
		return wTileX >= offsetX && wTileX < offsetX + 64 && wTileY >= offsetY
				&& wTileY < offsetY + 64;

	}
	
	public void checkForUpdate() {
		
		for(int x = offsetX;x<offsetX + 64;x++) {
			for(int y = offsetY;y<offsetY + 64;y++) {
				if(scenegraph.getTile(Options.currentHeight.get(), x, y).hasUpdated) {
					this.updated = true;
					break;
				}
			}
		}
	}

	public void clearUpdates() {
		for(int x = offsetX;x<offsetX + 64;x++) {
			for(int y = offsetY;y<offsetY + 64;y++) {
				scenegraph.getTile(Options.currentHeight.get(), x, y).hasUpdated = false;
			}
		}
	}

	public void fillNamesFromIds() {
		if(tileMapName == null || tileMapName.isEmpty()) {
			tileMapName = Integer.toString(tileMapId);
		}
		if(objectMapName == null || objectMapName.isEmpty()) {
			objectMapName = Integer.toString(objectMapId);
		}
	}

}
