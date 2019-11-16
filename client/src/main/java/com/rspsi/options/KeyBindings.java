package com.rspsi.options;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.Maps;

import javafx.scene.input.KeyCode;

public class KeyBindings {
	
	private static Map<KeyActions, List<KeyCode>> mappings = Maps.newConcurrentMap();
	
	static {
		setDefaults();
	}
	
	public static boolean actionValid(KeyActions action) {
		return mappings.containsKey(action) && KeyboardState.onlyDown(mappings.get(action));
	}
	
	public static void addBind(KeyActions action, KeyCode... keys) {
		mappings.put(action, Arrays.asList(keys));
	}
	
	public static void setDefaults() {
		addBind(KeyActions.ADD_TO_SELECTION_OBJECT, KeyCode.SHIFT);
		addBind(KeyActions.ADD_TO_SELECTION_TILE, KeyCode.SHIFT);
		addBind(KeyActions.ADD_TO_SELECTION_TILE_SINGLE, KeyCode.CONTROL);
		addBind(KeyActions.OVERLAY_ONLY_PAINT, KeyCode.SHIFT);
		addBind(KeyActions.INVERSE_FLAG_SET, KeyCode.ALT);
		addBind(KeyActions.OVERLAY_REMOVE, KeyCode.ALT);
	}
	

}
