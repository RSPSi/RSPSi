package com.jagex.cache.anim;

public class Frame {



	private FrameBase base;
	private int duration;
	private int transformationCount;
	private int[] transformX;
	private int[] transformY;
	private int[] transformZ;
	private int[] transformationIndices;
	private boolean opaque = true;

	/**
	 * Gets the {@link FrameBase} of this Frame.
	 * 
	 * @return The FrameBase.
	 */
	public FrameBase getBase() {
		return base;
	}

	/**
	 * Gets the duration this Frame lasts.
	 * 
	 * @return The duration.
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Gets the amount of transformations in this Frame.
	 * 
	 * @return The amount of transformations.
	 */
	public int getTransformationCount() {
		return transformationCount;
	}

	public int getTransformationIndex(int index) {
		return transformationIndices[index];
	}

	public int getTransformX(int transformation) {
		return transformX[transformation];
	}

	public int getTransformY(int transformation) {
		return transformY[transformation];
	}

	public int getTransformZ(int transformation) {
		return transformZ[transformation];
	}

	public void setBase(FrameBase base) {
		this.base = base;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setTransformationCount(int transformationCount) {
		this.transformationCount = transformationCount;
	}

	public void setTransformX(int[] transformX) {
		this.transformX = transformX;
	}

	public void setTransformY(int[] transformY) {
		this.transformY = transformY;
	}

	public void setTransformZ(int[] transformZ) {
		this.transformZ = transformZ;
	}

	public void setTransformationIndices(int[] transformationIndices) {
		this.transformationIndices = transformationIndices;
	}

	public boolean isOpaque() {
		return opaque;
	}

	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}

	public int[] getTransformX() {
		return transformX;
	}

	public int[] getTransformY() {
		return transformY;
	}

	public int[] getTransformZ() {
		return transformZ;
	}

	public int[] getTransformationIndices() {
		return transformationIndices;
	}
	
	
	
	

}