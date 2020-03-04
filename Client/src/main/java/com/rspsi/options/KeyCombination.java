package com.rspsi.options;

import com.google.common.collect.Lists;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


@Slf4j
@Builder
@Getter
@Accessors(fluent = true)
public class KeyCombination {

	@Builder.Default
	private List<KeyCode> requiredKeys = Lists.newArrayList();
	@Builder.Default
	private List<EventType<KeyEvent>> validEvents = Lists.newArrayList();

	@Builder.Default
	private Optional<Consumer<EventType<KeyEvent>>> onValid = Optional.empty();
	private boolean consumesEvent;
	private boolean exclusivelyPressed;

	public KeyCombination onValid(Consumer<EventType<KeyEvent>> consumer){
		onValid = Optional.ofNullable(consumer);
		return this;
	}

	public KeyCombination consumesEvent(boolean consumesEvent){
		this.consumesEvent = consumesEvent;
		return this;
	}


	public boolean hasEvents(){
		return !validEvents.isEmpty();
	}

	public boolean valid(EventType<KeyEvent> event){
		boolean hasEventFilter = !validEvents.isEmpty();
		if(!requiredKeys.isEmpty())
			log.info("Checking for event | {} | {} | {} | {} | {}", event, validEvents.isEmpty(), exclusivelyPressed, validEvents.contains(event), requiredKeys);
		if(!hasEventFilter) {
			return exclusivelyPressed ? KeyboardState.exclusivePressed(requiredKeys) : KeyboardState.nonExclusivePressed(requiredKeys);
		} else {
			return validEvents.contains(event) && (exclusivelyPressed ? KeyboardState.exclusivePressed(requiredKeys) : KeyboardState.nonExclusivePressed(requiredKeys));
		}
	}
}
