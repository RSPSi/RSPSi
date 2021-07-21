package com.rspsi.jagex.map.tile;



import lombok.Data;
import lombok.ToString;
import net.runelite.gpu.GpuIntBuffer;
import net.runelite.gpu.util.ModelBuffers;

import java.nio.IntBuffer;

@ToString
@Data
public final class SimpleTile {

	private int bufferOffset = -1;
	private int uvBufferOffset = -1;
	private int sceneBufferOffset = -1;
	private int bufferLen = -1;
	private int pickerType = -1;
	int centreColour;
	int eastColour;
	int northEastColour;
	int northColour;
	int overlayColour;
	boolean flat;
	int overlayTextureId;
	
	public boolean textured;
	int underlayColour;

	public SimpleTile(int underlayColour, int overlayTextureId, int overlayColour, int centreColour, int eastColour, int northEastColour, int northColour, boolean flat) {
		this.centreColour = centreColour;
		this.eastColour = eastColour;
		this.northEastColour = northEastColour;
		this.northColour = northColour;
		this.overlayTextureId = overlayTextureId;
		this.overlayColour = overlayColour;
		this.flat = flat;
		this.underlayColour = underlayColour;

		this.textured = false;//TODO
	}

	public void draw(ModelBuffers modelBuffers, int x, int y, int z) {

		GpuIntBuffer b = modelBuffers.getModelBufferUnordered();
		modelBuffers.incUnorderedModels();

		b.ensureCapacity(8);
		IntBuffer buffer = b.getBuffer();
		buffer.put(getBufferOffset());
		buffer.put(getUvBufferOffset());
		buffer.put(2);
		buffer.put(modelBuffers.getTargetBufferOffset());
		buffer.put(ModelBuffers.FLAG_SCENE_BUFFER);
		buffer.put(x).put(z).put(y);

		setSceneBufferOffset(modelBuffers.getTargetBufferOffset());
		modelBuffers.addTargetBufferOffset(6);
	}
}