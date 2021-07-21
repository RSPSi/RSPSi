package com.rspsi.editor.game.save.object.state;

import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.util.ObjectKey;
import lombok.EqualsAndHashCode;
import org.joml.Vector3i;

import javax.vecmath.Vector3f;

@EqualsAndHashCode(callSuper=false)
public class ObjectSnapshot extends TileSnapshot {

	public ObjectSnapshot(Vector3i position) {
		super(position);
	}

	public ObjectSnapshot(int x, int y, int z) {
		super(x, y, z);
	}


	/**
	 * The key of the object on this tile being preserved
	 */
	public ObjectKey key;


	/**
	 * The previous shading on this tile
	 */
	public byte shading = -1;//XXX UNUSED
	

	@Override
	public void preserve(SceneGraph sceneGraph) {
		this.shading = Client.getSingleton().mapRegion.shading[getZ()][getX()][getY()];
	}


	@Override
	public int getUniqueId() {
		return key.hashCode() + this.hashCode();
	}
}
