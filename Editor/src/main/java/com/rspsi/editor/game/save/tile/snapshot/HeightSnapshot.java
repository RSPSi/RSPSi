package com.rspsi.editor.game.save.tile.snapshot;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;
import lombok.EqualsAndHashCode;
import org.joml.Vector3i;

@EqualsAndHashCode(callSuper=false)
public class HeightSnapshot extends TileSnapshot {
	
	private int height;

	public HeightSnapshot(Vector3i position) {
		super(position);
	}

	public int getHeight() {
		return height;
	}

	@Override
	public void preserve(SceneGraph sceneGraph) {
		this.height = Client.getSingleton().mapRegion.tileHeights[getZ()][getX()][getY()];
	}

	@Override
	public int getUniqueId() {
		return 3;
	}

	
	
}
