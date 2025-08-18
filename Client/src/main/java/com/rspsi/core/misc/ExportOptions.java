package com.rspsi.core.misc;

public class ExportOptions {
	
	private boolean exportGameObjects, exportGroundDecorations, exportTileFlags,
					exportTileHeights, exportTilesAbove, exportUnderlays,
					exportOverlays, exportWalls, exportWallDecorations;
	
	public boolean exportGameObjects() {
		return exportGameObjects;
	}

	public boolean exportGroundDecorations() {
		return exportGroundDecorations;
	}

	public boolean exportOverlays() {
		return exportOverlays;
	}

	public boolean exportTileFlags() {
		return exportTileFlags;
	}

	public boolean exportTileHeights() {
		return exportTileHeights;
	}

	public boolean exportTilesAbove() {
		return exportTilesAbove;
	}

	public boolean exportUnderlays() {
		return exportUnderlays;
	}

	public boolean exportWallDecorations() {
		return exportWallDecorations;
	}

	public boolean exportWalls() {
		return exportWalls;
	}

	public void setExportGameObjects(boolean exportGameObjects) {
		this.exportGameObjects = exportGameObjects;
	}

	public void setExportGroundDecorations(boolean exportGroundDecorations) {
		this.exportGroundDecorations = exportGroundDecorations;
	}

	public void setExportTileFlags(boolean exportTileFlags) {
		this.exportTileFlags = exportTileFlags;
	}

	public void setExportTileHeights(boolean exportTileHeights) {
		this.exportTileHeights = exportTileHeights;
	}

	public void setExportTilesAbove(boolean exportTilesAbove) {
		this.exportTilesAbove = exportTilesAbove;
	}

	public void setExportUnderlays(boolean exportUnderlays) {
		this.exportUnderlays = exportUnderlays;
	}

	public void setExportOverlays(boolean exportOverlays) {
		this.exportOverlays = exportOverlays;
	}

	public void setExportWalls(boolean exportWalls) {
		this.exportWalls = exportWalls;
	}

	public void setExportWallDecorations(boolean exportWallDecorations) {
		this.exportWallDecorations = exportWallDecorations;
	}
	
	
}
