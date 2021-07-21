package com.rspsi.editor.game.save.tile.snapshot;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joml.Vector3i;

@Data
@EqualsAndHashCode(callSuper=false)
public class UnderlaySnapshot extends TileSnapshot {
	
	private byte id;


	public UnderlaySnapshot(Vector3i position) {
		super(position);
	}

	public byte getId() {
		return id;
	}

	@Override
	public void preserve(SceneGraph sceneGraph) {
		this.id = Client.getSingleton().mapRegion.underlays[getZ()][getX()][getY()];
	}

	@Override
	public int getUniqueId() {
		return 1;
	}


	
	
}
