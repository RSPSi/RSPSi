package com.rspsi.core.misc;

public class DeleteOptions {
	
	private boolean deleteGameObjects, deleteGroundDecorations, deleteTileFlags,
					deleteTileHeights, deleteTilesAbove, deleteUnderlays,
					deleteOverlays, deleteWalls, deleteWallDecorations;
	
	public boolean deleteGameObjects() {
		return deleteGameObjects;
	}

	public boolean deleteGroundDecorations() {
		return deleteGroundDecorations;
	}

	public boolean deleteOverlays() {
		return deleteOverlays;
	}

	public boolean deleteTileFlags() {
		return deleteTileFlags;
	}

	public boolean deleteTileHeights() {
		return deleteTileHeights;
	}

	public boolean deleteTilesAbove() {
		return deleteTilesAbove;
	}

	public boolean deleteUnderlays() {
		return deleteUnderlays;
	}

	public boolean deleteWallDecorations() {
		return deleteWallDecorations;
	}

	public boolean deleteWalls() {
		return deleteWalls;
	}

	public void setDeleteGameObjects(boolean deleteGameObjects) {
		this.deleteGameObjects = deleteGameObjects;
	}

	public void setDeleteGroundDecorations(boolean deleteGroundDecorations) {
		this.deleteGroundDecorations = deleteGroundDecorations;
	}

	public void setDeleteTileFlags(boolean deleteTileFlags) {
		this.deleteTileFlags = deleteTileFlags;
	}

	public void setDeleteTileHeights(boolean deleteTileHeights) {
		this.deleteTileHeights = deleteTileHeights;
	}

	public void setDeleteTilesAbove(boolean deleteTilesAbove) {
		this.deleteTilesAbove = deleteTilesAbove;
	}

	public void setDeleteUnderlays(boolean deleteUnderlays) {
		this.deleteUnderlays = deleteUnderlays;
	}

	public void setDeleteOverlays(boolean deleteOverlays) {
		this.deleteOverlays = deleteOverlays;
	}

	public void setDeleteWalls(boolean deleteWalls) {
		this.deleteWalls = deleteWalls;
	}

	public void setDeleteWallDecorations(boolean deleteWallDecorations) {
		this.deleteWallDecorations = deleteWallDecorations;
	}
	
	
}
