package com.rspsi.options;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;

import com.google.common.collect.Maps;

import javafx.scene.input.KeyCode;

import static java.util.Arrays.asList;
public class KeyBindings {
	
	private static Map<KeyActions, KeyCombination> mappings = Maps.newConcurrentMap();
	
	static {
		setDefaults();
	}

	public static boolean actionValid(KeyActions action) {
		return mappings.containsKey(action) && mappings.get(action).valid(null);
	}


	public static boolean actionValid(KeyActions action, EventType<KeyEvent> keyEvent) {
		return mappings.containsKey(action) && mappings.get(action).valid(keyEvent);
	}


	public static List<KeyCombination> matchedCombination(EventType<KeyEvent> keyEvent) {
		return mappings.values().stream().filter(keyCombination -> keyCombination.hasEvents()).filter(keyCombination -> keyCombination.valid(keyEvent)).collect(Collectors.toList());
	}

	public static KeyCombination addBind(KeyActions action, KeyCode... keys) {
		return addBind(action, Lists.newArrayList(), keys);
	}

	public static KeyCombination addBind(KeyActions action, EventType<KeyEvent> event, KeyCode... keys) {
		return addBind(action, asList(event), keys);
	}

	public static KeyCombination addBind(KeyActions action, List<EventType<KeyEvent> > events, KeyCode... keys) {
		KeyCombination keyCombination = KeyCombination.builder().requiredKeys(asList(keys)).validEvents(events).build();
		mappings.put(action, keyCombination);
		return keyCombination;
	}

	
	public static void setDefaults() {
		//Tools
		addBind(KeyActions.ADD_TO_SELECTION_OBJECT, KeyCode.SHIFT);
		addBind(KeyActions.ADD_TO_SELECTION_TILE, KeyCode.SHIFT);
		addBind(KeyActions.ADD_TO_SELECTION_TILE_SINGLE, KeyCode.CONTROL);
		addBind(KeyActions.OVERLAY_ONLY_PAINT, KeyCode.SHIFT);
		addBind(KeyActions.INVERSE_FLAG_SET, KeyCode.ALT);
		addBind(KeyActions.OVERLAY_REMOVE, KeyCode.ALT);


		addBind(KeyActions.ROTATE_COUNTERCLOCKWISE, KeyEvent.KEY_RELEASED, KeyCode.Q)
				.consumesEvent(true)
				.onValid(keyEvent ->  {
					Options.rotation.set((Options.rotation.get() - 1) & 3);
				});
		addBind(KeyActions.ROTATE_CLOCKWISE, KeyEvent.KEY_RELEASED, KeyCode.E)
				.consumesEvent(true)
				.onValid(keyEvent ->  {
					Options.rotation.set((Options.rotation.get() + 1) & 3);
				});
	}
	

}
