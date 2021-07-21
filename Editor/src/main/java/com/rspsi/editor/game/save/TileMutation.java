package com.rspsi.editor.game.save;

import com.google.common.collect.Maps;
import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.map.SceneGraph;
import org.joml.Vector3i;

import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.Map;

public abstract class TileMutation {

	private int lowX = 10000, lowY = 10000, highX, highY;
	public TileMutation() {
	}
	
	protected Map<TileUniqueIdentifier, TileSnapshot> preservedTileStates = Maps.newHashMap();

	/**
	 * Backs up the current tiles state before modification
	 * @param currentState The state of the current tile
	 */
	public void storeSnapshot(TileSnapshot currentState) throws InvalidMutationException {
		if(!canPreserve(currentState))
			throw new InvalidMutationException(this, currentState);
		Vector3i l = new Vector3i(currentState.getX(), currentState.getY(), currentState.getZ());
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
	 * Gets a list of @TileMutation that represents the opposite state of this state.
	 * @return A @TileMutation class based on this state
	 * @param sceneGraph
	 */
	public abstract TileMutation getInverse(SceneGraph sceneGraph);
	
	/**
	 * Restores the preserved tile states to the scenegraph
	 */
	public abstract void restoreStates(SceneGraph sceneGraph);
	
	public Rectangle getArea() {
		return new Rectangle(lowX, lowY, highX - lowX, highY - lowY);
	}

    public abstract boolean canPreserve(TileSnapshot snapshot);
}
