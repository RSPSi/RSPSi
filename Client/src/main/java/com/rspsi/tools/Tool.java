package com.rspsi.tools;

import com.rspsi.misc.Location;

public abstract class Tool {
	
	public abstract void onMouseMove();
	public abstract void onMouseClick();
	public abstract String name();
	
	public Location getMouseToTile() {
		
		return null;
	}

}
