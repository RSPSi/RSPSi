package org.major.map;

public enum RenderFlags {
	BLOCKED_TILE(1),
	BRIDGE_TILE(2),
	FORCE_LOWEST_PLANE(4),
	RENDER_ON_LOWER_Z(8),
	DISABLE_RENDERING(16)
	;
	
	RenderFlags(int bit) {
		this.bit = bit;
	}
	
	private int bit;
	
	public int getBit() {
		return bit;
	}
	
}
