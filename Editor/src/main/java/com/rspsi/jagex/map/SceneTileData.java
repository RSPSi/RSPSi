package com.rspsi.jagex.map;

import com.google.common.collect.Lists;
import com.rspsi.jagex.map.object.DefaultWorldObject;
import com.rspsi.jagex.map.object.TypeFilter;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.misc.CopyOptions;
import com.rspsi.misc.ExportOptions;
import com.rspsi.misc.Location;
import lombok.val;

import java.util.List;

public class SceneTileData {

    private byte overlayId = -1, underlayId = -1;
    private byte overlayOrientation = -1, overlayType = -1;

    private int tileHeight = -1;

    private int[] gameObjectIds;
    private int[] gameObjectConfigs;

    private int wallId = -1;
    private int wallConfig = -1;

    private int wallDecoId = -1;
    private int wallDecoConfig = -1;

    private int groundDecoId = -1;
    private int groundDecoConfig = -1;

    private byte tileFlag;

    private int x, y, z;

    public SceneTileData() {

    }

    public SceneTileData(SceneTile tile, int tileX, int tileY) {
        val gameObjects = tile.getObjects(TypeFilter.genericAndRoof);
        if (gameObjects.size() > 0) {
            List<Integer> ids = Lists.newArrayList();
            List<Integer> configs = Lists.newArrayList();
            for (DefaultWorldObject worldObject : gameObjects) {
                if (worldObject.getX() == tileX && worldObject.getY() == tileY) {
                    ids.add(worldObject.getId());
                    configs.add(worldObject.getConfig());
                }
            }
            if (!ids.isEmpty()) {
                gameObjectIds = ids.stream().mapToInt(i -> i).toArray();
                gameObjectConfigs = configs.stream().mapToInt(i -> i).toArray();
            }
        }

        val wall = tile.getObject(TypeFilter.wallObjects);
        if (wall != null) {
            wallId = wall.getId();
            wallConfig = wall.getConfig();
        }

        val wallDecoration = tile.getObject(TypeFilter.wallDecoration);
        if (wallDecoration != null) {
            wallDecoId = wallDecoration.getId();
            wallDecoConfig = wallDecoration.getConfig();

        }


        val groundDecoration = tile.getObject(TypeFilter.wallDecoration);
        if (groundDecoration != null) {
            groundDecoId = groundDecoration.getId();
            groundDecoConfig = groundDecoration.getConfig();
        }

    }

    public SceneTileData(ExportOptions exportOptions, SceneTile tile, int tileX, int tileY) {
        if (exportOptions.exportGameObjects()) {
            val gameObjects = tile.getObjects(TypeFilter.genericAndRoof);
            if (gameObjects.size() > 0) {
                List<Integer> ids = Lists.newArrayList();
                List<Integer> configs = Lists.newArrayList();
                for (DefaultWorldObject worldObject : gameObjects) {
                    if (worldObject.getX() == tileX && worldObject.getY() == tileY) {
                        ids.add(worldObject.getId());
                        configs.add(worldObject.getConfig());
                    }
                }
                if (!ids.isEmpty()) {
                    gameObjectIds = ids.stream().mapToInt(i -> i).toArray();
                    gameObjectConfigs = configs.stream().mapToInt(i -> i).toArray();
                }
            }
        }
        if (exportOptions.exportWalls()) {
            val wall = tile.getObject(TypeFilter.wallObjects);
            if (wall != null) {
                wallId = wall.getId();
                wallConfig = wall.getConfig();
            }
        }

        if (exportOptions.exportWallDecorations()) {
            val wallDecoration = tile.getObject(TypeFilter.wallDecoration);
            if (wallDecoration != null) {
                wallDecoId = wallDecoration.getId();
                wallDecoConfig = wallDecoration.getConfig();
            }
        }

        if (exportOptions.exportGroundDecorations()) {

            val groundDecoration = tile.getObject(TypeFilter.wallDecoration);
            if (groundDecoration != null) {
                groundDecoId = groundDecoration.getId();
                groundDecoConfig = groundDecoration.getConfig();
            }
        }
    }

    public SceneTileData(CopyOptions copyWindow, SceneTile tile, int tileX, int tileY) {
        if (copyWindow.copyGameObjects()) {
            val gameObjects = tile.getObjects(TypeFilter.genericAndRoof);
            if (gameObjects.size() > 0) {
                List<Integer> ids = Lists.newArrayList();
                List<Integer> configs = Lists.newArrayList();
                for (DefaultWorldObject worldObject : gameObjects) {
                    if (worldObject.getX() == tileX && worldObject.getY() == tileY) {
                        ids.add(worldObject.getId());
                        configs.add(worldObject.getConfig());
                    }
                }
                if (!ids.isEmpty()) {
                    gameObjectIds = ids.stream().mapToInt(i -> i).toArray();
                    gameObjectConfigs = configs.stream().mapToInt(i -> i).toArray();
                }
            }
        }
        if (copyWindow.copyWalls()) {
            val wall = tile.getObject(TypeFilter.wallObjects);
            if (wall != null) {
                wallId = wall.getId();
                wallConfig = wall.getConfig();
            }
        }

        if (copyWindow.copyWallDecorations()) {
            val wallDecoration = tile.getObject(TypeFilter.wallDecoration);
            if (wallDecoration != null) {
                wallDecoId = wallDecoration.getId();
                wallDecoConfig = wallDecoration.getConfig();
            }
        }

        if (copyWindow.copyGroundDecorations()) {

            val groundDecoration = tile.getObject(TypeFilter.wallDecoration);
            if (groundDecoration != null) {
                groundDecoId = groundDecoration.getId();
                groundDecoConfig = groundDecoration.getConfig();
            }
        }
    }

    public int[] getGameObjectConfigs() {
        return gameObjectConfigs;
    }

    public void setGameObjectConfigs(int[] gameObjectConfigs) {
        this.gameObjectConfigs = gameObjectConfigs;
    }

    public int[] getGameObjectIds() {
        return gameObjectIds;
    }

    public void setGameObjectIds(int[] gameObjectIds) {
        this.gameObjectIds = gameObjectIds;
    }

    public int getGroundDecoConfig() {
        return groundDecoConfig;
    }

    public void setGroundDecoConfig(int groundDecoConfig) {
        this.groundDecoConfig = groundDecoConfig;
    }

    public int getGroundDecoId() {
        return groundDecoId;
    }

    public void setGroundDecoId(int groundDecoId) {
        this.groundDecoId = groundDecoId;
    }

    public byte getOverlayId() {
        return overlayId;
    }

    public void setOverlayId(byte overlayId) {
        this.overlayId = overlayId;
    }

    public byte getOverlayOrientation() {
        return overlayOrientation;
    }

    public void setOverlayOrientation(byte overlayOrientation) {
        this.overlayOrientation = overlayOrientation;
    }

    public byte getOverlayType() {
        return overlayType;
    }

    public void setOverlayType(byte overlayType) {
        this.overlayType = overlayType;
    }

    public byte getTileFlag() {
        return tileFlag;
    }

    public void setTileFlag(byte tileFlag) {
        this.tileFlag = tileFlag;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public byte getUnderlayId() {
        return underlayId;
    }

    public void setUnderlayId(byte underlayId) {
        this.underlayId = underlayId;
    }

    public int getWallConfig() {
        return wallConfig;
    }

    public void setWallConfig(int wallConfig) {
        this.wallConfig = wallConfig;
    }

    public int getWallDecoConfig() {
        return wallDecoConfig;
    }

    public void setWallDecoConfig(int wallDecoConfig) {
        this.wallDecoConfig = wallDecoConfig;
    }

    public int getWallDecoId() {
        return wallDecoId;
    }

    public void setWallDecoId(int wallDecoId) {
        this.wallDecoId = wallDecoId;
    }

    public int getWallId() {
        return wallId;
    }

    public void setWallId(int wallId) {
        this.wallId = wallId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Location getLocation() {
        return new Location(x, y, z);
    }

}
