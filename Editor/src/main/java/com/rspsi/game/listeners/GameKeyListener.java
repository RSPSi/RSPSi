package com.rspsi.game.listeners;

import com.jagex.Client;
import com.jagex.map.SceneGraph;
import com.rspsi.MainWindow;
import com.rspsi.dialogs.TileDeleteDialog;
import com.rspsi.misc.ToolType;
import com.rspsi.options.KeyboardState;
import com.rspsi.options.Options;

import javafx.event.EventHandler;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

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
			} else {
				KeyboardState.onKeyUp(keyEvent.getCode());
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

			if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.Y && event.getEventType() == KeyEvent.KEY_PRESSED) {
				SceneGraph.redo();
				
				event.consume();
			} 

			if (keyEvent.getCode() == KeyCode.E && event.getEventType() == KeyEvent.KEY_RELEASED) {
				Options.rotation.set((Options.rotation.get() + 1) & 3);
				
				event.consume();
				
				// Client.scene.resetTiles(SceneGraph.hoveredTileX, SceneGraph.hoveredTileY,
				// SceneGraph.activePlane);
				// Client.scene.addTemporaryObject(SceneGraph.hoveredTileX,
				// SceneGraph.hoveredTileY, SceneGraph.activePlane);
			} else if (keyEvent.getCode() == KeyCode.Q && event.getEventType() == KeyEvent.KEY_RELEASED) {
				Options.rotation.set((Options.rotation.get() - 1) & 3);
				
				event.consume();
				// Client.scene.resetTiles(SceneGraph.hoveredTileX, SceneGraph.hoveredTileY,
				// SceneGraph.activePlane);
				// Client.scene.addTemporaryObject(SceneGraph.hoveredTileX,
				// SceneGraph.hoveredTileY, SceneGraph.activePlane);
			} 
			
			if (keyEvent.getCode() == KeyCode.SHIFT) {
				SceneGraph.shiftDown = event.getEventType() == KeyEvent.KEY_PRESSED;
			} 

			if (keyEvent.getCode() == KeyCode.CONTROL) {
				SceneGraph.ctrlDown = event.getEventType() == KeyEvent.KEY_PRESSED;
			} 

			if (keyEvent.getCode() == KeyCode.ALT) {
				SceneGraph.altDown = event.getEventType() == KeyEvent.KEY_PRESSED;
			}

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
