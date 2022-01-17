package com.jagex.map.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.jagex.link.Linkable;
import com.jagex.map.GroundItem;
import com.jagex.map.object.DefaultWorldObject;
import com.jagex.map.object.GameObject;
import com.jagex.map.object.GroundDecoration;
import com.jagex.map.object.Wall;
import com.jagex.map.object.WallDecoration;
import com.jagex.util.ObjectKey;
import com.rspsi.core.misc.Location;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public final class SceneTile extends Linkable {

	public ShapedTile shape;
	public SimpleTile simple;
	public boolean needsRendering;
	public boolean aBoolean1323;
	public boolean hasObjects;
	public SceneTile tileBelow;
	public int anInt1310;
	public int anInt1325;
	public int anInt1326;
	public int anInt1327;
	public int anInt1328;
	private int attributes;// 4 == TILE_EAST
	public int collisionPlane;
	public boolean hasUpdated;

	@ToString.Exclude
	public GameObject[] gameObjects;
	@ToString.Exclude
	public GroundDecoration groundDecoration;
	@ToString.Exclude
	public GroundItem groundItem;
	public int[] objectAttributes;
	public int objectCount;
	public int plane;
	public int positionX;
	public int positionY;
	@ToString.Exclude
	public Wall wall;
	@ToString.Exclude
	public WallDecoration wallDecoration;
	public boolean tileHighlighted;
	public boolean tileSelected;
	public boolean tileBeingSelected;
	@ToString.Exclude
	public Optional<DefaultWorldObject> temporaryObject = Optional.empty();
	@ToString.Exclude
	public Optional<Integer> temporaryObjectAttributes = Optional.empty();
	@ToString.Exclude
	public Optional<ShapedTile> temporaryShapedTile = Optional.empty();
	@ToString.Exclude
	public Optional<SimpleTile> temporarySimpleTile = Optional.empty();
	public byte tileFlags;
	
	public byte getTileFlags() {
		return tileFlags;
	}
	
	public int attributes() {
		if(temporaryObjectAttributes.isPresent())
			return attributes | temporaryObjectAttributes.get().intValue();
		return attributes;
	}
	
	public void shiftAttributes(int value) {
		attributes |= value;
	}

	public void clearAttributes() {
		attributes = 0;
	}
	
	public void setTileFlags(byte tileFlags) {
		this.tileFlags = tileFlags;
	}

	public SceneTile(int x, int y, int z) {
		gameObjects = new GameObject[5];
		objectAttributes = new int[5];
		anInt1310 = plane = z;
		positionX = x;
		positionY = y;
	}

	public List<DefaultWorldObject> getExistingObjects() {
		List<DefaultWorldObject> list = new ArrayList<>();
		if (wall != null) {
			list.add(wall);
		}
		if (groundDecoration != null) {
			list.add(groundDecoration);
		}
		if (wallDecoration != null) {
			list.add(wallDecoration);
		}
		for (GameObject gameObject : gameObjects)
			if (gameObject != null) {
				list.add(gameObject);
			}
		return list;
	}

	public DefaultWorldObject getObject(ObjectKey key) {
		if (key != null) {

			int type = key.getType();
			if (type >= 0 && type < 4) {
				if (wall != null && wall.getKey() == key)
					return wall;
			} else if (type >= 4 && type < 9) {
				if (wallDecoration != null && wallDecoration.getKey() == key)
					return wallDecoration;
			} else if (type == 22) {
				if (groundDecoration != null && groundDecoration.getKey() == key)
					return groundDecoration;
			} else if (type >= 9) {
				for (GameObject obj : gameObjects) {
					if (obj != null && obj.getKey() == key)
						return obj;
				}
			}
		}
		return null;
	}

	public Optional<DefaultWorldObject> getTemporaryObject() {
		return temporaryObject;
	}

	public boolean hasObjects() {
		return !getExistingObjects().isEmpty();
	}

	public boolean removeByUID(ObjectKey key) {
		if (key != null) {

			int type = key.getType();
			if (type >= 0 && type < 4) {
				if (wall != null && wall.getKey() == key) {
					wall = null;
					return true;
				}
			} else if (type >= 4 && type < 9) {
				if (wallDecoration != null && wallDecoration.getKey() == key) {
					wallDecoration = null;
					return true;
				}
			} else if (type == 22) {
				if (groundDecoration != null && groundDecoration.getKey() == key) {
					groundDecoration = null;
					return true;
				}
			} else if (type >= 9) {
				boolean removed = false;
				for (GameObject obj : gameObjects) {
					if (obj != null && obj.getKey() == key) {
						this.removeGameObject(obj);
						removed = true;
					}
				}

				return removed;
			}
		}
		return false;
	}

	private void removeGameObject(GameObject object) {
		for (int index = 0; index < objectCount; index++) {
			if (gameObjects[index] != object) {
				continue;
			}

			objectCount--;
			for (int remaining = index; remaining < objectCount; remaining++) {
				gameObjects[remaining] = gameObjects[remaining + 1];
				objectAttributes[remaining] = objectAttributes[remaining + 1];
			}

			gameObjects[objectCount] = null;
			break;
		}

		attributes = 0;
		for (int index = 0; index < objectCount; index++) {
			attributes |= objectAttributes[index];
		}
	}

	public SceneTile copy() {
		SceneTile tile = new SceneTile(positionX, positionY, plane);
		tile.aBoolean1323 = this.aBoolean1323;
		tile.anInt1310 = this.anInt1310;
		tile.anInt1325 = this.anInt1325;
		tile.anInt1326 = this.anInt1326;
		tile.anInt1327 = this.anInt1327;
		tile.anInt1328 = this.anInt1328;
		tile.attributes = this.attributes;
		tile.collisionPlane = this.collisionPlane;
		tile.gameObjects = this.gameObjects;
		tile.groundDecoration = this.groundDecoration;
		tile.hasObjects = this.hasObjects;
		tile.objectAttributes = this.objectAttributes;
		tile.objectCount = this.objectCount;
		tile.shape = this.shape;
		tile.simple = this.simple;
		return tile;
	}

	public boolean contains(ObjectKey key) {
		// TODO Auto-generated method stub
		return getObject(key) != null;
	}

	public boolean contains(int id, int type) {
		if (type >= 0 && type < 4) {
            return wall != null && wall.getId() == id;
		} else if (type >= 4 && type < 9) {
            return wallDecoration != null && wallDecoration.getId() == id;
		} else if (type == 22) {
            return groundDecoration != null && groundDecoration.getId() == id;
		} else if (type >= 9) {
			for (GameObject obj : gameObjects) {
				if (obj != null && obj.getId() == id)
					return true;
			}
		}
		return false;
	}

	public Location getSceneLocation() {
		return new Location(positionX, positionY, plane);
	}
	
	public Stream<DefaultWorldObject> nonNullStream(){
		return Stream.concat(Stream.of(gameObjects), Stream.of(wall, wallDecoration, groundDecoration)).filter(Objects::nonNull);
	}

	@Getter @Setter
	private int bufferOffset, bufferUvOffset, bufferLen;

}