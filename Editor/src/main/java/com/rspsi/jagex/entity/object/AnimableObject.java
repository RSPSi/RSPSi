package com.rspsi.jagex.entity.object;

import com.rspsi.jagex.cache.anim.Graphic;
import com.rspsi.jagex.cache.loader.anim.FrameLoader;
import com.rspsi.jagex.cache.loader.anim.GraphicLoader;
import com.rspsi.jagex.entity.Renderable;
import com.rspsi.jagex.entity.model.Mesh;
import com.rspsi.jagex.entity.model.ModelInstance;
import org.joml.Vector3i;

/**
 * An object that plays a graphic when it's spawned, such as the doors on
 * waterbirth island.
 */
public final class AnimableObject extends Renderable {

	private final int tick;
	private int duration;
	private int elapsedFrames;
	private final Graphic graphic;
	private final int renderHeight;
	private boolean transformationCompleted = false;
	private final int x;
	private final int y;
	private final int z;

	public AnimableObject(int x, int y, int z, int renderHeight, int graphic, int delay, int currentTime) {
		this.graphic = GraphicLoader.lookup(graphic);
		this.z = z;
		this.x = x;
		this.y = y;
		this.renderHeight = renderHeight;
		tick = currentTime + delay;
	}

	/**
	 * Gets the render height.
	 *
	 * @return The render height.
	 */
	public int getRenderHeight() {
		return renderHeight;
	}

	/**
	 * Gets the tick.
	 *
	 * @return The tick.
	 */
	public int getTick() {
		return tick;
	}

	/**
	 * Gets the x coordinate.
	 *
	 * @return The x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y coordinate.
	 *
	 * @return The y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the z coordinate.
	 *
	 * @return The z coordinate.
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Gets the transformationCompleted.
	 *
	 * @return The transformationCompleted.
	 */
	public boolean isTransformationCompleted() {
		return transformationCompleted;
	}

	@Override
	public final ModelInstance model() {
		Mesh graphicModel = graphic.getModel();
		if (graphicModel == null)
			return null;

		int frame = graphic.getAnimation().getPrimaryFrame(elapsedFrames);
		Mesh model = new Mesh(graphicModel, true, FrameLoader.isInvalid(frame), false, true);

		if (!transformationCompleted) {
			model.prepareSkeleton();
			model.apply(frame);
			model.faceGroups = null;
			model.vertexGroups = null;
		}

		if (graphic.getBreadthScale() != 128 || graphic.getDepthScale() != 128) {
			model.scale(graphic.getBreadthScale(), graphic.getBreadthScale(), graphic.getDepthScale());
		}

		if (graphic.getOrientation() == 90) {
			model.rotateClockwise();
		} else if (graphic.getOrientation() == 180) {
			model.rotateClockwise();
			model.rotateClockwise();
		} else if (graphic.getOrientation() == 270) {
			model.rotateClockwise();
			model.rotateClockwise();
			model.rotateClockwise();
		}
		return model.toModelInstance(64 + graphic.getModelBrightness(), 850 + graphic.getModelShadow(), -30, -50, -30);
	}

	@Override
	public <T extends Renderable> T copy() {
		return null;
	}

	public final void nextAnimationStep(int elapsedTime) {
		for (duration += elapsedTime; duration > graphic.getAnimation().duration(elapsedFrames);) {
			duration -= graphic.getAnimation().duration(elapsedFrames) + 1;
			elapsedFrames++;
			if (elapsedFrames >= graphic.getAnimation().getFrameCount()
					&& (elapsedFrames < 0 || elapsedFrames >= graphic.getAnimation().getFrameCount())) {
				elapsedFrames = 0;
				transformationCompleted = true;
			}
		}
	}
}