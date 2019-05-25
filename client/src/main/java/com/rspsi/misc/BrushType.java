package com.rspsi.misc;

public enum BrushType {

	RECTANGLE, CIRCLE;

	@Override
	public String toString() {
		String name = this.name();
		char c = name.charAt(0);
		name = c + name.toLowerCase().substring(1, name.length());
		return name;
	}

}
