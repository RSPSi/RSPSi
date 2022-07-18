package com.rspsi.game.listeners;

import com.jagex.Client;
import com.jagex.map.SceneGraph;
import com.rspsi.ui.MainWindow;
import com.rspsi.dialogs.TileDeleteDialog;
import com.rspsi.core.misc.ToolType;
import com.rspsi.options.KeyBindings;
import com.rspsi.options.KeyCombination;
import com.rspsi.options.KeyboardState;
import com.rspsi.options.Options;

import com.rspsi.util.Settings;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GameKeyListener implements EventHandler<InputEvent> {

	private final Client client;

	public GameKeyListener(Client applet) {
		this.client = applet;
	}

	@Override
	public void handle(InputEvent event) {
		// System.out.println(event.getEventType().getName());
		if (event.getEventType() == KeyEvent.KEY_PRESSED || event.getEventType() == KeyEvent.KEY_RELEASED) {
			KeyEvent keyEvent = (KeyEvent) event;

			
			if(keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
				KeyboardState.onKeyDown(keyEvent.getCode());
			}
			
			if (keyEvent.getCode() == KeyCode.W) {
				client.keyStatuses['w'] = event.getEventType() == KeyEvent.KEY_RELEASED ? 0 : 1;
				
				event.consume();
			} 
			if (keyEvent.getCode() == KeyCode.S) {
				client.keyStatuses['s'] = event.getEventType() == KeyEvent.KEY_RELEASED ? 0 : 1;
				
				event.consume();
			}
			
			if (keyEvent.getCode() == KeyCode.A) {
				client.keyStatuses['a'] = event.getEventType() == KeyEvent.KEY_RELEASED ? 0 : 1;
				
				event.consume();
			}
			if (keyEvent.getCode() == KeyCode.D) {
				client.keyStatuses['d'] = event.getEventType() == KeyEvent.KEY_RELEASED ? 0 : 1;
			}
			
			if(keyEvent.getCode() == KeyCode.UP) {
				client.keyStatuses['k'] = event.getEventType() == KeyEvent.KEY_RELEASED ? 0 : 1;
				
				event.consume();
			}
			if(keyEvent.getCode() == KeyCode.DOWN) {
				client.keyStatuses['l'] = event.getEventType() == KeyEvent.KEY_RELEASED ? 0 : 1;
				
				event.consume();
			}
			
			if(keyEvent.getCode() == KeyCode.LEFT) {
				client.keyStatuses[2] = event.getEventType() == KeyEvent.KEY_RELEASED ? 0 : 1;
				event.consume();
			}
			if(keyEvent.getCode() == KeyCode.RIGHT) {
				client.keyStatuses[1] = event.getEventType() == KeyEvent.KEY_RELEASED ? 0 : 1;
				event.consume();//Prevents tabs switching
			}
			
			if(keyEvent.getCode() == KeyCode.PAGE_UP) {
				client.keyStatuses['o'] = event.getEventType() == KeyEvent.KEY_RELEASED ? 0 : 1;
				
				event.consume();
			}
			if(keyEvent.getCode() == KeyCode.PAGE_DOWN) {
				client.keyStatuses['p'] = event.getEventType() == KeyEvent.KEY_RELEASED ? 0 : 1;
				
				event.consume();
			}

			if(keyEvent.isControlDown() && keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.R) {
				Settings.resetSettings();
				System.exit(0);
			}

			if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.Y && event.getEventType() == KeyEvent.KEY_PRESSED) {
				SceneGraph.redo();
				
				event.consume();
			}


			List<KeyCombination> comboActions = KeyBindings.matchedCombination((EventType<KeyEvent>) event.getEventType());

			comboActions.forEach(keyCombination -> {
				log.info("Found {}", keyCombination.requiredKeys().stream().map(String::valueOf).collect(Collectors.joining(",")));
				keyCombination.onValid().ifPresent(consumer -> consumer.accept(keyEvent.getEventType()));
				if (keyCombination.consumesEvent()) {
					event.consume();
				}
			});
				
			if(keyEvent.getEventType() == KeyEvent.KEY_RELEASED){
				KeyboardState.onKeyUp(keyEvent.getCode());
			}


			if(event.isConsumed())
				return;

			if(keyEvent.getCode().isDigitKey() && (keyEvent.getEventType() == KeyEvent.KEY_PRESSED || keyEvent.getEventType() == KeyEvent.KEY_TYPED)) {
				log.info("Key event: {}", keyEvent.getEventType());
				if(Options.currentTool.get() == ToolType.PAINT_OVERLAY){
					int val = keyEvent.getCode().compareTo(KeyCode.DIGIT0);
					MainWindow.getSingleton().overlaySwatch.selectOverlayPair(val - 1);
				}
			}

			if (keyEvent.getCode() == KeyCode.DELETE) {
				if(Options.currentTool.get() == ToolType.SELECT_OBJECT) {
					SceneGraph.onCycleEnd.add(() -> Client.getSingleton().sceneGraph.deleteObjects());
				} else
					TileDeleteDialog.instance.show();
				KeyboardState.onKeyUp(keyEvent.getCode());
			}
		}

	}

}
