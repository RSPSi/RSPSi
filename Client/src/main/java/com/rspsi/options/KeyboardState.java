package com.rspsi.options;

import java.util.List;

import com.google.common.collect.Lists;

import com.jagex.map.SceneGraph;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyboardState {
	
	private static List<KeyCode> currentlyDown = Lists.newCopyOnWriteArrayList();

	public static void reset(){
		log.info("Reset!");
		SceneGraph.shiftDown = false;
		SceneGraph.altDown = false;
		SceneGraph.ctrlDown = false;
		currentlyDown.clear();
	}
	public static void onKeyDown(KeyCode keyCode) {
		if(!currentlyDown.contains(keyCode)) {
			currentlyDown.add(keyCode);
		}
	}
	
	public static void onKeyUp(KeyCode keyCode) {
		currentlyDown.remove(keyCode);
	}
	
	public static boolean isKeyDown(KeyCode keyCode) {
		return currentlyDown.contains(keyCode);
	}

	public static boolean onlyDown(List<KeyCode> list) {
		if(currentlyDown.isEmpty())
			return false;
		currentlyDown.stream().filter(key -> !list.contains(key)).forEach(System.out::println);
		return currentlyDown.containsAll(list) && currentlyDown.stream().filter(key -> !list.contains(key)).count() == 0;
	}

}
