package com.rspsi.swatches;

import com.jagex.util.StringUtils;

public enum SwatchType {
	OBJECT(0), OVERLAY(1), UNDERLAY(2),;
	
	private SwatchType(int id) {
		this.id = id;
	}
	
	private int id;
	
	public int getId() {
		return id;
	}

	public static SwatchType getById(int id) {
		for(SwatchType type : SwatchType.values())
			if(type.getId() == id)
				return type;
		return OBJECT;
	}
	
	@Override
	public String toString() {
		return StringUtils.format(name().toLowerCase() + "s");
	}
}