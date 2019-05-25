package com.jagex.cache.loader.floor;

import com.jagex.cache.def.Floor;
import com.jagex.cache.loader.DataLoaderBase;

public abstract class FloorDefinitionLoader implements DataLoaderBase<Floor>{
	
	public static FloorDefinitionLoader instance;
	
	public static Floor getOverlay(int id) {
		return instance.getFloor(id, FloorType.OVERLAY);
	}
	
	public static Floor getUnderlay(int id) {
		return instance.getFloor(id, FloorType.UNDERLAY);
	}
	
	public static int getUnderlayCount() {
		return instance.getSize(FloorType.UNDERLAY);
	}
	
	public static int getOverlayCount() {
		return instance.getSize(FloorType.OVERLAY);
	}
	
	public abstract Floor getFloor(int id, FloorType type);
	public abstract int getSize(FloorType type);
	
	@Override
	public Floor forId(int id) {
		return null;
	}
	
	@Override
	public int count() {
		return 0;
	}

}
