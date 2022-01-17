package com.jagex.map;

import java.util.List;

import com.google.common.collect.Lists;
import com.jagex.map.tile.SceneTile;
import com.rspsi.core.misc.CopyOptions;
import com.rspsi.core.misc.ExportOptions;
import com.rspsi.core.misc.Location;

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
		if (tile.objectCount > 0) {
			List<Integer> ids = Lists.newArrayList();
			List<Integer> configs = Lists.newArrayList();
			for (int i = 0; i < tile.objectCount; i++) {
				if (tile.gameObjects[i].getX() == tileX && tile.gameObjects[i].getY() == tileY) {
					ids.add(tile.gameObjects[i].getId());
					configs.add(tile.gameObjects[i].getConfig());
				}
			}
			if (!ids.isEmpty()) {
				gameObjectIds = ids.stream().mapToInt(i -> i).toArray();
				gameObjectConfigs = configs.stream().mapToInt(i -> i).toArray();
			}
		}
		if (tile.wall != null) {
			wallId = tile.wall.getId();
			wallConfig = tile.wall.getConfig();
		}

		if (tile.wallDecoration != null) {
			wallDecoId = tile.wallDecoration.getId();
			wallDecoConfig = tile.wallDecoration.getConfig();
		}

		if (tile.groundDecoration != null) {
			groundDecoId = tile.groundDecoration.getId();
			groundDecoConfig = tile.groundDecoration.getConfig();
		}
	}

	public SceneTileData(ExportOptions exportOptions, SceneTile tile, int tileX, int tileY) {
		if (exportOptions.exportGameObjects())
			if (tile.objectCount > 0) {
				List<Integer> ids = Lists.newArrayList();
				List<Integer> configs = Lists.newArrayList();
				for (int i = 0; i < tile.objectCount; i++) {
					if (tile.gameObjects[i].getX() == tileX && tile.gameObjects[i].getY() == tileY) {
						ids.add(tile.gameObjects[i].getId());
						configs.add(tile.gameObjects[i].getConfig());
					}
				}
				if (!ids.isEmpty()) {
					gameObjectIds = ids.stream().mapToInt(i -> i).toArray();
					gameObjectConfigs = configs.stream().mapToInt(i -> i).toArray();
				}
			}
		if (exportOptions.exportWalls())
			if (tile.wall != null) {
				wallId = tile.wall.getId();
				wallConfig = tile.wall.getConfig();
			}

		if (exportOptions.exportWallDecorations())
			if (tile.wallDecoration != null) {
				wallDecoId = tile.wallDecoration.getId();
				wallDecoConfig = tile.wallDecoration.getConfig();
			}

		if (exportOptions.exportGroundDecorations())
			if (tile.groundDecoration != null) {
				groundDecoId = tile.groundDecoration.getId();
				groundDecoConfig = tile.groundDecoration.getConfig();
			}
	}
	
	public SceneTileData(CopyOptions copyWindow, SceneTile tile, int tileX, int tileY) {
		if (copyWindow.copyGameObjects())
			if (tile.objectCount > 0) {
				List<Integer> ids = Lists.newArrayList();
				List<Integer> configs = Lists.newArrayList();
				for (int i = 0; i < tile.objectCount; i++) {
					if (tile.gameObjects[i].getX() == tileX && tile.gameObjects[i].getY() == tileY) {
						ids.add(tile.gameObjects[i].getId());
						configs.add(tile.gameObjects[i].getConfig());
					}
				}
				if (!ids.isEmpty()) {
					gameObjectIds = ids.stream().mapToInt(i -> i).toArray();
					gameObjectConfigs = configs.stream().mapToInt(i -> i).toArray();
				}
			}
		if (copyWindow.copyWalls())
			if (tile.wall != null) {
				wallId = tile.wall.getId();
				wallConfig = tile.wall.getConfig();
			}

		if (copyWindow.copyWallDecorations())
			if (tile.wallDecoration != null) {
				wallDecoId = tile.wallDecoration.getId();
				wallDecoConfig = tile.wallDecoration.getConfig();
			}

		if (copyWindow.copyGroundDecorations())
			if (tile.groundDecoration != null) {
				groundDecoId = tile.groundDecoration.getId();
				groundDecoConfig = tile.groundDecoration.getConfig();
			}
	}

	public int[] getGameObjectConfigs() {
		return gameObjectConfigs;
	}

	public int[] getGameObjectIds() {
		return gameObjectIds;
	}

	public int getGroundDecoConfig() {
		return groundDecoConfig;
	}

	public int getGroundDecoId() {
		return groundDecoId;
	}

	public byte getOverlayId() {
		return overlayId;
	}

	public byte getOverlayOrientation() {
		return overlayOrientation;
	}

	public byte getOverlayType() {
		return overlayType;
	}

	public byte getTileFlag() {
		return tileFlag;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public byte getUnderlayId() {
		return underlayId;
	}

	public int getWallConfig() {
		return wallConfig;
	}

	public int getWallDecoConfig() {
		return wallDecoConfig;
	}

	public int getWallDecoId() {
		return wallDecoId;
	}

	public int getWallId() {
		return wallId;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public void setGameObjectConfigs(int[] gameObjectConfigs) {
		this.gameObjectConfigs = gameObjectConfigs;
	}

	public void setGameObjectIds(int[] gameObjectIds) {
		this.gameObjectIds = gameObjectIds;
	}

	public void setGroundDecoConfig(int groundDecoConfig) {
		this.groundDecoConfig = groundDecoConfig;
	}

	public void setGroundDecoId(int groundDecoId) {
		this.groundDecoId = groundDecoId;
	}

	public void setOverlayId(byte overlayId) {
		this.overlayId = overlayId;
	}

	public void setOverlayOrientation(byte overlayOrientation) {
		this.overlayOrientation = overlayOrientation;
	}

	public void setOverlayType(byte overlayType) {
		this.overlayType = overlayType;
	}

	public void setTileFlag(byte tileFlag) {
		this.tileFlag = tileFlag;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}

	public void setUnderlayId(byte underlayId) {
		this.underlayId = underlayId;
	}

	public void setWallConfig(int wallConfig) {
		this.wallConfig = wallConfig;
	}

	public void setWallDecoConfig(int wallDecoConfig) {
		this.wallDecoConfig = wallDecoConfig;
	}

	public void setWallDecoId(int wallDecoId) {
		this.wallDecoId = wallDecoId;
	}

	public void setWallId(int wallId) {
		this.wallId = wallId;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public Location getLocation() {
		return new Location(x, y, z);
	}

}
