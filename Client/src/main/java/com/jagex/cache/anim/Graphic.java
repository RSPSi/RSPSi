package com.jagex.cache.anim;

import com.jagex.entity.model.Mesh;
import com.jagex.entity.model.MeshLoader;
import com.rspsi.core.misc.FixedHashMap;

public class Graphic {
	
	public static FixedHashMap<Integer, Mesh> modelCache = new FixedHashMap<Integer, Mesh>(30);

	private Animation animation;
	private int animationId = -1;
	private int breadthScale = 128;
	private int depthScale = 128;
	private int id;
	/**
	 * The id of the model used by this Graphic.
	 */
	private int model;

	private int ambience;
	private int modelShadow;
	private int orientation;
	private int[] originalColours = new int[6];
	private int[] replacementColours = new int[6];

	

	/**
	 * Gets the {@link Animation } used by this Graphic.
	 *
	 * @return The Animation.
	 */
	public Animation getAnimation() {
		return animation;
	}

	/**
	 * Gets the breadth scale.
	 *
	 * @return The breadth scale.
	 */
	public int getBreadthScale() {
		return breadthScale;
	}

	/**
	 * Gets the depth scale.
	 *
	 * @return The depth scale.
	 */
	public int getDepthScale() {
		return depthScale;
	}

	/**
	 * Gets the id of this Graphic.
	 *
	 * @return The id.
	 */
	public int getId() {
		return id;
	}

	public Mesh getModel() {
		Mesh model = modelCache.get(id);
		if (model != null)
			return model;

		model = MeshLoader.getSingleton().lookup(this.model);
		if (model == null)
			return null;
		model = model.copy();

		for (int part = 0; part < 6; part++) {
			if (originalColours[0] != 0) {
				model.recolour(originalColours[part], replacementColours[part]);
			}
		}

		modelCache.put(id, model);
		return model;
	}

	/**
	 * Gets the model brightness.
	 *
	 * @return The model brightness.
	 */
	public int getModelBrightness() {
		return ambience;
	}

	/**
	 * Gets the id of the model used by this Graphic.
	 *
	 * @return The model id.
	 */
	public int getModelId() {
		return model;
	}

	/**
	 * Gets the model shadow.
	 *
	 * @return The model shadow.
	 */
	public int getModelShadow() {
		return modelShadow;
	}

	/**
	 * Gets the orientation.
	 *
	 * @return The orientation.
	 */
	public int getOrientation() {
		return orientation;
	}

	public FixedHashMap<Integer, Mesh> getModelCache() {
		return modelCache;
	}

	public void setModelCache(FixedHashMap<Integer, Mesh> modelCache) {
		Graphic.modelCache = modelCache;
	}

	public int getAnimationId() {
		return animationId;
	}

	public void setAnimationId(int animationId) {
		this.animationId = animationId;
	}

	public int getAmbience() {
		return ambience;
	}

	public void setAmbience(int ambience) {
		this.ambience = ambience;
	}

	public int[] getOriginalColours() {
		return originalColours;
	}

	public void setOriginalColours(int[] originalColours) {
		this.originalColours = originalColours;
	}

	public int[] getReplacementColours() {
		return replacementColours;
	}

	public void setReplacementColours(int[] replacementColours) {
		this.replacementColours = replacementColours;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void setBreadthScale(int breadthScale) {
		this.breadthScale = breadthScale;
	}

	public void setDepthScale(int depthScale) {
		this.depthScale = depthScale;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public void setModelShadow(int modelShadow) {
		this.modelShadow = modelShadow;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	
	

}