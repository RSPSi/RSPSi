package com.rspsi.core.misc;

public class CopyOptions {
	
	private boolean copyGameObjects, copyGroundDecorations, copyTileFlags,
					copyTileHeights, copyTilesAbove, copyUnderlays,
					copyOverlays, copyWalls, copyWallDecorations;
	
	public boolean copyGameObjects() {
		return copyGameObjects;
	}

	public boolean copyGroundDecorations() {
		return copyGroundDecorations;
	}

	public boolean copyOverlays() {
		return copyOverlays;
	}

	public boolean copyTileFlags() {
		return copyTileFlags;
	}

	public boolean copyTileHeights() {
		return copyTileHeights;
	}

	public boolean copyTilesAbove() {
		return copyTilesAbove;
	}

	public boolean copyUnderlays() {
		return copyUnderlays;
	}

	public boolean copyWallDecorations() {
		return copyWallDecorations;
	}

	public boolean copyWalls() {
		return copyWalls;
	}

	public void setCopyGameObjects(boolean copyGameObjects) {
		this.copyGameObjects = copyGameObjects;
	}

	public void setCopyGroundDecorations(boolean copyGroundDecorations) {
		this.copyGroundDecorations = copyGroundDecorations;
	}

	public void setCopyTileFlags(boolean copyTileFlags) {
		this.copyTileFlags = copyTileFlags;
	}

	public void setCopyTileHeights(boolean copyTileHeights) {
		this.copyTileHeights = copyTileHeights;
	}

	public void setCopyTilesAbove(boolean copyTilesAbove) {
		this.copyTilesAbove = copyTilesAbove;
	}

	public void setCopyUnderlays(boolean copyUnderlays) {
		this.copyUnderlays = copyUnderlays;
	}

	public void setCopyOverlays(boolean copyOverlays) {
		this.copyOverlays = copyOverlays;
	}

	public void setCopyWalls(boolean copyWalls) {
		this.copyWalls = copyWalls;
	}

	public void setCopyWallDecorations(boolean copyWallDecorations) {
		this.copyWallDecorations = copyWallDecorations;
	}
	
	
}
