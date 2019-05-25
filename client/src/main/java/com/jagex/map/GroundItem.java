package com.jagex.map;

import com.jagex.entity.Renderable;

/**
 * A tile with up to three items drawn on top of it.
 */
public final class GroundItem {

	/**
	 * The plane of this tile.
	 */
	private int plane;

	/**
	 * The plane of the item on this tile.
	 */
	private int itemHeight;

	/**
	 * The key of this tile.
	 */
	private int key;

	/**
	 * The primary (i.e. foremost) renderable on this tile.
	 */
	private Renderable primary;

	/**
	 * The secondary renderable on this tile.
	 */
	private Renderable secondary;

	/**
	 * The tertiary renderable on this tile.
	 */
	private Renderable tertiary;

	/**
	 * The x coordinate of this tile.
	 */
	private int x;

	/**
	 * The y coordinate of this tile.
	 */
	private int y;

	/**
	 * Gets the itemHeight.
	 *
	 * @return The itemHeight.
	 */
	int getItemHeight() {
		return itemHeight;
	}

	/**
	 * Gets the key.
	 *
	 * @return The key.
	 */
	int getKey() {
		return key;
	}

	/**
	 * Gets the plane.
	 *
	 * @return The plane.
	 */
	int getPlane() {
		return plane;
	}

	/**
	 * The primary (i.e. foremost) renderable on this tile.
	 */
	public Renderable getPrimary() {
		return primary;
	}

	/**
	 * The secondary renderable on this tile.
	 */
	public Renderable getSecondary() {
		return secondary;
	}

	/**
	 * The tertiary renderable on this tile.
	 */
	public Renderable getTertiary() {
		return tertiary;
	}

	/**
	 * The x coordinate of this tile.
	 */
	public int getX() {
		return x;
	}

	/**
	 * The y coordinate of this tile.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the itemHeight.
	 *
	 * @param itemHeight
	 *            The itemHeight.
	 */
	void setItemHeight(int itemHeight) {
		this.itemHeight = itemHeight;
	}

	/**
	 * Sets the key.
	 *
	 * @param key
	 *            The key.
	 */
	void setKey(int key) {
		this.key = key;
	}

	/**
	 * Sets the plane.
	 *
	 * @param plane
	 *            The plane.
	 */
	void setPlane(int plane) {
		this.plane = plane;
	}

	public void setPrimary(Renderable primary) {
		this.primary = primary;
	}

	public void setSecondary(Renderable secondary) {
		this.secondary = secondary;
	}

	public void setTertiary(Renderable tertiary) {
		this.tertiary = tertiary;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

}