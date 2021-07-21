package com.rspsi.editor.game.save.tile.snapshot;

import com.rspsi.jagex.map.SceneGraph;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joml.Vector3i;

@EqualsAndHashCode(callSuper=false)
public class OverlaySnapshot extends TileSnapshot {
	
	private byte id;
	private byte rotation;
	private byte shape;
	
	public OverlaySnapshot(Vector3i position) {
		super(position);
	}

	public byte getId() {
		return id;
	}
	public byte getRotation() {
		return rotation;
	}
	public byte getShape() {
		return shape;
	}

	@Override
	public int getUniqueId() {
		return 2;
	}

	@Override
	public void preserve(SceneGraph sceneGraph) {
		this.id = sceneGraph.getMapRegion().overlays[getZ()][getX()][getY()];
		this.rotation = sceneGraph.getMapRegion().overlayOrientations[getZ()][getX()][getY()];
		this.shape = sceneGraph.getMapRegion().overlayShapes[getZ()][getX()][getY()];
	}


}
