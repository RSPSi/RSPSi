package com.rspsi.options;

import java.util.List;

import com.google.common.collect.Lists;

import com.jagex.map.SceneGraph;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyboardState {
	
	private static List<KeyCode> currentlyDown = Lists.newArrayList();

	public static void reset(){
		log.info("Reset!");
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
	
	public static boolean isKeyPressed(KeyCode keyCode) {
		return currentlyDown.contains(keyCode);
	}

	public static boolean nonExclusivePressed(List<KeyCode> list){
		log.info("Currently down: {} | attempting to find {}", currentlyDown, list);
		if(currentlyDown.isEmpty())
			return false;
		return currentlyDown.containsAll(list);
	}

	public static boolean exclusivePressed(List<KeyCode> list) {
		log.info("Currently down: {} | attempting to find {}", currentlyDown, list);
		if(currentlyDown.isEmpty())
			return false;
		return currentlyDown.containsAll(list) && currentlyDown.size() == list.size();
	}

}
