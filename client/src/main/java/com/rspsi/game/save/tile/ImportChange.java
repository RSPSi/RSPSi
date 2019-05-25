package com.rspsi.game.save.tile;

import com.jagex.Client;
import com.jagex.chunk.Chunk;
import com.rspsi.game.save.StateChangeType;
import com.rspsi.game.save.TileChange;
import com.rspsi.game.save.tile.state.ImportTileState;

import java.awt.Rectangle;

public class ImportChange extends TileChange<ImportTileState> {

	public ImportChange() {
		super();
	}

	@Override
	public TileChange<ImportTileState> getInverse() {
		try {
			ImportChange change = new ImportChange();
			preservedTileStates.values().forEach(state -> {
				try {
					ImportTileState newState = new ImportTileState(state.getX(), state.getY(), state.getZ());
					newState.preserve();
					change.preserveTileState(newState);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			return change;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void restoreStates() {
		int minX = 1000;
		int minY = 1000;
		int maxX = 0;
		int maxY = 0;
		
		//TODO Object spawning/despawning
		for(ImportTileState state : preservedTileStates.values()) {
			int x = state.getX();
			int y = state.getY();
			int z = state.getZ();

			Client.getSingleton().mapRegion.tileFlags[z][x][y] = state.getFlagState().getFlag();
			

			Client.getSingleton().mapRegion.overlayOrientations[z][x][y] = state.getOverlayState().getRotation();
			Client.getSingleton().mapRegion.overlays[z][x][y] = state.getOverlayState().getId();
			Client.getSingleton().mapRegion.overlayShapes[z][x][y] = state.getOverlayState().getShape();
			

			Client.getSingleton().mapRegion.underlays[z][x][y] = state.getUnderlayState().getId();
			

			Client.getSingleton().mapRegion.tileHeights[z][x][y] = state.getHeightState().getHeight();
			if(x > maxX)
				maxX = x;
			if(y > maxY)
				maxY = y;
			if(x < minX)
				minX = x;
			if(y < minY)
				minY = y;
		}

		Client.getSingleton().sceneGraph.updateHeights(minX - 6, minY - 6, (maxX - minX) + 6, (maxY - minY) + 6);
	}

	@Override
	public StateChangeType getType() {
		// TODO Auto-generated method stub
		return StateChangeType.IMPORT;
	}

}
