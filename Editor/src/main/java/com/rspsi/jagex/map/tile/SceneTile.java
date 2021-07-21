package com.rspsi.jagex.map.tile;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.link.Linkable;
import com.rspsi.jagex.map.MapRegion;
import com.rspsi.jagex.map.object.DefaultWorldObject;
import com.rspsi.jagex.map.object.GameObject;
import com.rspsi.jagex.map.object.TypeFilter;
import com.rspsi.jagex.util.ObjectKey;
import com.rspsi.renderer.Camera;
import lombok.ToString;
import lombok.val;
import net.runelite.gpu.util.ModelBuffers;
import net.runelite.gpu.util.Perspective;
import org.joml.Vector3i;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString
public final class SceneTile extends Linkable {

    public Vector3i worldPos;
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
    public int collisionPlane;
    public boolean hasUpdated;
    @ToString.Exclude
    public List<DefaultWorldObject> worldObjects;
    //@ToString.Exclude
    // public DefaultWorldObject groundDecoration;
    // @ToString.Exclude
    //public DefaultWorldObject groundItem;
  /*  @ToString.Exclude
    public DefaultWorldObject wall;
    @ToString.Exclude
    public DefaultWorldObject wallDecoration;*/
    public boolean tileHighlighted;
    public boolean tileSelected;
    public boolean tileBeingSelected;

    @ToString.Exclude
    public ShapedTile temporaryShapedTile = null;
    @ToString.Exclude
    public SimpleTile temporarySimpleTile = null;
    public byte tileFlags;

    public long temporaryObjectLastModified = -1;
    public long objectLastModified = -1;
    public long tileLastModified = -1;
    public long temporaryTileLastModified = -1;

    public int attributes;// 4 == TILE_EAST
    //TODO implement these

    public SceneTile(Vector3i worldPos) {
        this.worldPos = new Vector3i(worldPos);
        anInt1310 = worldPos.z;
        this.worldObjects = new ArrayList<>();
    }

    public List<DefaultWorldObject> getExistingObjects() {
        return nonNullStream().collect(Collectors.toList());
    }

    public DefaultWorldObject getObject(Predicate<DefaultWorldObject> predicate) {
        return nonNullStream().filter(predicate).findFirst().orElse(null);
    }

    public DefaultWorldObject getObject(@Nonnull ObjectKey key) {
        return nonNullStream().filter(obj -> obj.getKey() == key).findFirst().orElse(null);
    }

    public boolean retainAll(@Nonnull Collection<ObjectKey> objectKeys) {
        val removed = worldObjects.removeIf(worldObject -> objectKeys.contains(worldObject.key));
        recalculateAttributes();
        return removed;
    }

    public boolean remove(@Nonnull ObjectKey key) {
        return worldObjects.removeIf(worldObject -> worldObject.key == key);
    }


    public int recalculateAttributes() {
        attributes = nonNullStream().mapToInt(DefaultWorldObject::getDecorData).reduce((a, b) -> a | b).orElse(0);
        return attributes;
    }


    public SceneTile copy() {
        SceneTile tile = new SceneTile(worldPos);
        tile.aBoolean1323 = this.aBoolean1323;
        tile.anInt1310 = this.anInt1310;
        tile.anInt1325 = this.anInt1325;
        tile.anInt1326 = this.anInt1326;
        tile.anInt1327 = this.anInt1327;
        tile.anInt1328 = this.anInt1328;
        tile.attributes = this.attributes;
        tile.collisionPlane = this.collisionPlane;
        tile.hasObjects = this.hasObjects;
        tile.shape = this.shape;
        tile.simple = this.simple;
        return tile;
    }

    public boolean contains(ObjectKey key) {
        // TODO Auto-generated method stub
        return getObject(key) != null;
    }

    public boolean contains(int id, int type) {
        return getObject(o -> o.getId() == id && o.getTypeFilter().contains(type)) != null;
    }


    public Stream<DefaultWorldObject> nonNullStream() {
        return worldObjects.stream().filter(Objects::nonNull);
    }


    public void drawTiles(ModelBuffers modelBuffers, Camera camera) {
        int x = worldPos.x * Perspective.LOCAL_TILE_SIZE;
        int y = worldPos.y * Perspective.LOCAL_TILE_SIZE;
        int z = worldPos.z;

        if (shape != null) {
            shape.draw(modelBuffers, x, y, z);
        } else if (simple != null) {
            simple.draw(modelBuffers, x, y, z);
        }


        //nonNullStream().forEachOrdered(defaultWorldObject -> defaultWorldObject.draw(modelBuffers, x,  Client.getSingleton().mapRegion.tileHeights[plane][positionX][positionY], y));


        //modelBuffers.flipVertUv();
    }

    public void drawModels(ModelBuffers modelBuffers, Camera camera) {
        int x = worldPos.x * Perspective.LOCAL_TILE_SIZE;
        int y = worldPos.y * Perspective.LOCAL_TILE_SIZE;
        int z = worldPos.z;


        nonNullStream()
                .forEachOrdered(defaultWorldObject -> {
                    defaultWorldObject.draw(modelBuffers, x, y, Client.getSingleton().mapRegion.tileHeights[worldPos.z][worldPos.x][worldPos.y]);
                });


        //modelBuffers.flipVertUv();
    }


    public int getTileHeight(MapRegion mapRegion) {
        return mapRegion.tileHeights[worldPos.z][worldPos.x][worldPos.y];
    }

    public int objectCount(TypeFilter type) {
        return (int) getObjectsStream(type).count();
    }

    public Stream<DefaultWorldObject> getObjectsStream(TypeFilter type) {
        return nonNullStream().filter(object -> object.getTypeFilter() == type);
    }

    public List<DefaultWorldObject> getObjects(TypeFilter type) {
        return getObjectsStream(type).collect(Collectors.toList());
    }

    public DefaultWorldObject getObject(TypeFilter type) {
        return nonNullStream()
                .filter(object -> object.getTypeFilter() == type)
                .findFirst()
                .orElse(null);
    }

    public DefaultWorldObject getObject(TypeFilter type, Predicate<DefaultWorldObject> additionalFilter) {
        return nonNullStream()
                .filter(object -> object.getTypeFilter() == type)
                .filter(additionalFilter)
                .findFirst()
                .orElse(null);
    }

    public void remove(TypeFilter type) {
        getObjectsStream(type).map(DefaultWorldObject::getKey).forEach(this::remove);
        recalculateAttributes();
    }
}