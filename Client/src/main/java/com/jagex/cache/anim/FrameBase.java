package com.jagex.cache.anim;

import com.jagex.io.Buffer;

public class FrameBase {

	/**
	 * The amount of transformations.
	 */
	private int count;
	private int[][] vertexGroups;

	/**
	 * The type of each transformation.
	 */
	private int[] transformationType;

	public int[] getLabels(int label) {
		return vertexGroups[label];
	}

	/**
	 * Gets the amount of transformations in this FrameBase.
	 * 
	 * @return The amount of transformations.
	 */
	public int getTransformationCount() {
		return count;
	}

	/**
	 * Gets the transformation type of the transformation at the specified index.
	 * 
	 * @param index
	 *            The index.
	 * @return The transformation type.
	 */
	public int getTransformationType(int index) {
		return transformationType[index];
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int[][] getVertexGroups() {
		return vertexGroups;
	}

	public void setVertexGroups(int[][] vertexGroups) {
		this.vertexGroups = vertexGroups;
	}

	public int[] getTransformationType() {
		return transformationType;
	}

	public void setTransformationType(int[] transformationType) {
		this.transformationType = transformationType;
	}
	
	

}