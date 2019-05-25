package com.rspsi.game.save;

import java.awt.Rectangle;
import java.util.Map;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.google.common.collect.Maps;
import com.rspsi.game.save.tile.state.TileState;

public abstract class TileChange<T extends TileState> {

	private int lowX = 10000, lowY = 10000, highX, highY;
	public TileChange() {
	}
	
	protected Map<TileUniqueIdentifier, T> preservedTileStates = Maps.newHashMap();
	
	/**
	 * Backs up the current tiles state before modification
	 * @param currentState The state of the current tile
	 */
	public void preserveTileState(T currentState) {
		Vector3D l = new Vector3D(currentState.getX(), currentState.getY(), currentState.getZ());
		TileUniqueIdentifier tuid = new TileUniqueIdentifier(l, currentState.getUniqueId());
		if(!preservedTileStates.containsKey(tuid)) {
			preservedTileStates.put(tuid, currentState);
			if(currentState.getX() < lowX)
				lowX = currentState.getX();
			if(currentState.getY() < lowY)
				lowY = currentState.getY();
			if(currentState.getX() > highX)
				highX = currentState.getX();
			if(currentState.getY() > highY)
				highY = currentState.getY();
			
		}
	}
	
	public boolean containsChanges() {
		return !preservedTileStates.isEmpty();
	}
	
	/**
	 * Gets a list of the backed up tiles
	 * @return The map of coordinates and their states
	 */
	public Map<TileUniqueIdentifier, T> getChangedTileStates() {
		return preservedTileStates;
	}
	
	/**
	 * Gets a list of @TileChange that represents the opposite state of this state.
	 * @return A @TileChange class based on this state
	 */
	public abstract TileChange<T> getInverse();
	
	/**
	 * Restores the preserved tile states to the chunk
	 */
	public abstract void restoreStates();
	
	/**
	 * The @ChangeType this 
	 * @return
	 */
	public abstract StateChangeType getType();
	
	public Rectangle getArea() {
		return new Rectangle(lowX, lowY, highX - lowX, highY - lowY);
	}
	
	

}
