package com.rspsi.editor.game.save.tile.snapshot;

import com.rspsi.editor.game.save.object.state.ObjectSnapshot;
import com.rspsi.jagex.map.SceneGraph;
import lombok.EqualsAndHashCode;
import org.joml.Vector3i;

@EqualsAndHashCode(callSuper=false)
public class ImportTileSnapshot extends TileSnapshot {

	public FlagSnapshot flagState;
	public HeightSnapshot heightState;
	public OverlaySnapshot overlayState;
	public UnderlaySnapshot underlayState;
	public ObjectSnapshot objectState;


	public ImportTileSnapshot(Vector3i position) {
		super(position);
	}


	@Override
	public void preserve(SceneGraph sceneGraph) {
		this.objectState = new ObjectSnapshot(this.position);
		this.flagState = new FlagSnapshot(this.position);
		this.heightState = new HeightSnapshot(this.position);
		this.overlayState = new OverlaySnapshot(this.position);
		this.underlayState = new UnderlaySnapshot(this.position);

		this.flagState.preserve(sceneGraph);
		this.heightState.preserve(sceneGraph);
		this.overlayState.preserve(sceneGraph);
		this.underlayState.preserve(sceneGraph);
		this.objectState.preserve(sceneGraph);
	}

	@Override
	public int getUniqueId() {
		return 22;
	}


}
