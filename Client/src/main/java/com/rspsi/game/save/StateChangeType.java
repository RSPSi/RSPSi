package com.rspsi.game.save;

import com.rspsi.core.misc.ToolType;

public enum StateChangeType {
	
	OVERLAY(ToolType.PAINT_OVERLAY),
	UNDERLAY(ToolType.PAINT_UNDERLAY), 
	TILE_HEIGHT(ToolType.MODIFY_HEIGHT), 
	TILE_FLAG(ToolType.SET_FLAGS), 
	OBJECT_SPAWN(ToolType.SPAWN_OBJECT), 
	OBJECT_DELETE(ToolType.DELETE_OBJECT),
	IMPORT(ToolType.IMPORT_SELECTION)
	
	;

	StateChangeType(ToolType tool) {
		this.tool = tool;
	}
	
	/**
	 * The expected tool for this state change type to be valid.
	 */
	private ToolType tool;
	
	public ToolType getTool() {
		return tool;
	}
}
