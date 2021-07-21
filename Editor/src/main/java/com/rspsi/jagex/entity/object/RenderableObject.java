package com.rspsi.jagex.entity.object;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.cache.anim.Animation;
import com.rspsi.jagex.cache.def.ObjectDefinition;
import com.rspsi.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.rspsi.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.rspsi.jagex.entity.Renderable;
import com.rspsi.jagex.entity.model.Mesh;
import com.rspsi.jagex.entity.model.ModelInstance;
import com.rspsi.options.Options;

public class RenderableObject extends Renderable {

	public static Client client;
	private Animation animation;
	private int aY;
	private int cY;
	private int dY;
	private int centre;
	private int currentFrameId;
	private int animId;
	private int currentFrameDuration;
	private int orientation;
	private int type;
	private boolean randomFrame;
	private int mean;

	private ModelInstance modelInstance;

	public RenderableObject(int id, int orientation, int type, int aY, int bY, int cY, int dY, int animationId,
			boolean randomFrame) {
		this.id = id;
		this.type = type;
		this.orientation = orientation;
		this.aY = aY;
		centre = bY;
		this.cY = cY;
		this.dY = dY;
		mean = aY + bY + cY + dY / 4;
		this.animId = animationId;
		this.randomFrame = randomFrame;
		if (animationId != -1) {
			animation = AnimationDefinitionLoader.getAnimation(animationId);
			currentFrameId = 0;
			currentFrameDuration = Client.pulseTick;
			if (randomFrame && animation.getLoopOffset() != -1) {
				currentFrameId = (int) (Math.random() * animation.getFrameCount());
				currentFrameDuration -= (int) (Math.random() * animation.duration(currentFrameId));
			}
		}

	}

	public RenderableObject() {

	}

	@Override
	public final ModelInstance model() {
		if(modelInstance != null)
			return modelInstance;
		int lastFrame = -1;
		if (animation != null && Options.loadAnimations.get()) {
			int tickDelta = Client.pulseTick - currentFrameDuration;
			if (tickDelta > 100 && animation.getLoopOffset() > 0) {
				tickDelta = 100;
			}

			while (tickDelta > animation.duration(currentFrameId)) {
				tickDelta -= animation.duration(currentFrameId);
				currentFrameId++;
				if (currentFrameId < animation.getFrameCount()) {
					continue;
				}
				currentFrameId -= animation.getLoopOffset();
				if (currentFrameId >= 0 && currentFrameId < animation.getFrameCount()) {
					continue;
				}
				animation = null;
				break;
			}

			currentFrameDuration = Client.pulseTick - tickDelta;
			if (animation != null) {
				lastFrame = animation.getPrimaryFrame(currentFrameId);
			}
		}

		if(lastFrame == -1) {
			return modelInstance;
		}

		ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);
		if(definition.getMorphisms() != null)
			definition = morph();
		Renderable model = definition == null ? null
				: definition.modelAt(new int[1][1], type, orientation, aY, centre, cY, dY, lastFrame, mean);//TODO get real tile height
		if(model != null && this.selected) {
			model = model.copy();
			model.selected = true;
		}
		if(model instanceof Mesh) {
			modelInstance = ((Mesh)model).toModelInstance(64, 768, -50, -10, -50);
		} else if(model instanceof ModelInstance) {
			modelInstance = (ModelInstance) model;
		}
		return modelInstance;
	}

	@Override
	public RenderableObject copy() {
		RenderableObject renderableObject = new RenderableObject();

		renderableObject.animation = animation;
		renderableObject.aY = aY;
		renderableObject.cY = cY;
		renderableObject.dY = dY;
		renderableObject.centre = centre;
		renderableObject.currentFrameId = currentFrameId;
		renderableObject.animId = animId;
		renderableObject.currentFrameDuration = currentFrameDuration;
		renderableObject.orientation = orientation;
		renderableObject.type = type;
		renderableObject.randomFrame = randomFrame;
		renderableObject.mean = mean;

		return renderableObject;
	}


	public final ObjectDefinition morph() {
		return ObjectDefinitionLoader.getMorphism(id);
	}
	

}