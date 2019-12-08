package com.jagex.map.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jagex.chunk.Chunk;
import com.jagex.entity.Renderable;
import com.jagex.util.ObjectKey;
import lombok.ToString;

@ToString
public abstract class DefaultWorldObject implements WorldObject {

	private int x;
	private int y;
	private int renderHeight;
	private int plane;

	private ObjectKey key;

	private boolean selected;

	private Renderable primary;

	private Renderable secondary;

	public DefaultWorldObject(ObjectKey key, int x, int y, int z) {
		this.key = key;
		this.x = x;
		this.y = y;
		renderHeight = z;
	}

	public int getConfig() {
		int rotation = key.getOrientation();
		int type = key.getType();
		return type << 2 | rotation;
	}

	public int getId() {
		return key.getId();
	}

	@Override
	@JsonIgnore
	public ObjectKey getKey() {
		return key;
	}

	@JsonIgnore
	public int getLocHash() {

		int y = key.getY() & 63;
		int x = key.getX() & 63;
		return plane << 12 | x << 6 | y;
	}

	@Override
	@JsonIgnore
	public int getPlane() {
		return plane;
	}

	@JsonIgnore
	public Renderable getPrimary() {
		return primary;
	}

	@Override
	@JsonIgnore
	public int getRenderHeight() {
		return renderHeight;
	}

	/**
	 * Gets the secondary.
	 *
	 * @return The secondary.
	 */
	@JsonIgnore
	public Renderable getSecondary() {
		return secondary;
	}

	public abstract WorldObjectType getType();

	@Override
	@JsonIgnore
	public int getX() {
		return x;
	}

	@Override
	@JsonIgnore
	public int getY() {
		return y;
	}

	@JsonIgnore
	public boolean isSelected() {
		return selected;
	}

	public void setKey(ObjectKey key) {
		this.key = key;
	}

	public void setPlane(int plane) {
		this.plane = plane;
	}

	public void setPrimary(Renderable renderable) {
		this.primary = renderable;
	}
	
	public boolean primaryExists() {
		return primary != null;
	}
	
	public boolean secondaryExists() {
		return secondary != null;
	}
	
	public boolean primaryHasNormals() {
		return primaryExists() && primary.hasNormals();
	}
	
	public boolean secondaryHasNormals() {
		return secondaryExists() && secondary.hasNormals();
	}

	public void setRenderHeight(int z) {
		renderHeight = z;
	}

	/**
	 * Sets the secondary.
	 *
	 * @param secondary
	 *            The secondary.
	 */
	public void setSecondary(Renderable secondary) {
		this.secondary = secondary;
	}

	public void setSelected(boolean selected) {
		if (primary != null) {
			primary = primary.copy();
			primary.selected = selected;
		}
		if (secondary != null) {
			secondary = secondary.copy();
			secondary.selected = selected;
		}
		this.selected = selected;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

}
