package com.rspsi.editor.game.save.tile.snapshot;

import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.util.ObjectKey;
import org.joml.Vector3i;

public abstract class TileSnapshot {

	public Vector3i position;

	public TileSnapshot(Vector3i position) {
		this.position = position;
	}

	public TileSnapshot(int x, int y, int z) {
		this.position = new Vector3i(x, y, z);
	}

	public int getX() {
		return position.x;
	}
	public int getY() {
		return position.y;
	}
	public int getZ() {
		return position.z;
	}

	public abstract int getUniqueId();
	public abstract void preserve(SceneGraph sceneGraph);

}
