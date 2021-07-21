package com.rspsi.editor.game.save.tile.snapshot;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;
import lombok.EqualsAndHashCode;
import org.joml.Vector3i;

@EqualsAndHashCode(callSuper=false)
public class FlagSnapshot extends TileSnapshot {
	
	private byte flag;

	public FlagSnapshot(Vector3i position) {
		super(position);
	}

	public byte getFlag() {
		return flag;
	}

	@Override
	public void preserve(SceneGraph sceneGraph) {
		this.flag = Client.getSingleton().mapRegion.tileFlags[getZ()][getX()][getY()];
	}

	@Override
	public int getUniqueId() {
		return 4;
	}

	
	
}
