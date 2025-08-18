package com.rspsi.core.misc;

public enum BrushType {

	RECTANGLE, CIRCLE, CHECKER;

	@Override
	public String toString() {
		String name = this.name();
		char c = name.charAt(0);
		name = c + name.toLowerCase().substring(1, name.length());
		return name;
	}

}
