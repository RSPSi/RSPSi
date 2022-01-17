package com.rspsi.core.misc;

public class TileCondition {

	private int height;
	private ComparatorOperator operator;
	private int overlay = -1;
	private int underlay = -1;
	
	public TileCondition(int height, ComparatorOperator operator, int overlay, int underlay) {
		this.height = height;
		this.operator = operator;
		this.overlay = overlay;
		this.underlay = underlay;
	}

	public int getHeight() {
		return height;
	}

	public ComparatorOperator getOperator() {
		return operator;
	}

	public int getOverlay() {
		return overlay;
	}

	public int getUnderlay() {
		return underlay;
	}
	
	
}
