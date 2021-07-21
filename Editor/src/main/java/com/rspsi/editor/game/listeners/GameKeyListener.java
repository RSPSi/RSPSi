package com.rspsi.editor.game.listeners;

import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.editor.tools.ToolKeyEventHandler;
import com.rspsi.editor.tools.ToolRegister;
import com.rspsi.jagex.Client;
import com.rspsi.options.KeyBindings;
import com.rspsi.options.KeyCombination;
import com.rspsi.options.KeyboardState;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GameKeyListener implements EventHandler<KeyEvent> {

    private final Client client;
    private final UndoRedoSystem undoRedoSystem;

    public GameKeyListener(Client applet, UndoRedoSystem undoRedoSystem) {
        this.client = applet;
        this.undoRedoSystem = undoRedoSystem;
    }

    @Override
    public void handle(KeyEvent event) {
        // System.out.println(event.getEventType().getName());
        if (event.getEventType() == KeyEvent.KEY_PRESSED || event.getEventType() == KeyEvent.KEY_RELEASED) {

            boolean keyReleased = event.getEventType() == KeyEvent.KEY_RELEASED;
            KeyCode keyCode = event.getCode();

            //log.debug("Key event: {} {}", event, keyReleased);

            if(undoRedoSystem.handleKeyEvent(event)) {
                event.consume();
                return;
            }

            val currentTool = ToolRegister.getActiveTool();

            if(currentTool instanceof ToolKeyEventHandler) {
                if(((ToolKeyEventHandler)currentTool).handleKeyEvent(event)) {
                    event.consume();
                    return;
                }
            }

            switch (keyCode) {
                case W:
                    client.keyStatuses['w'] = keyReleased ? 0 : 1;
                    event.consume();
                    break;
                case A:
                    client.keyStatuses['a'] = keyReleased ? 0 : 1;
                    event.consume();
                    break;
                case S:
                    client.keyStatuses['s'] = keyReleased ? 0 : 1;
                    event.consume();
                    break;
                case D:
                    client.keyStatuses['d'] = keyReleased ? 0 : 1;
                    event.consume();
                    break;
                case UP:
                    client.keyStatuses['k'] = keyReleased ? 0 : 1;
                    event.consume();
                    break;
                case DOWN:
                    client.keyStatuses['l'] = keyReleased ? 0 : 1;
                    event.consume();
                    break;
                case LEFT:
                    client.keyStatuses[2] = keyReleased ? 0 : 1;
                    event.consume();
                    break;
                case RIGHT:
                    client.keyStatuses[1] = keyReleased ? 0 : 1;
                    event.consume();
                    break;

                case PAGE_UP:
                    client.keyStatuses['o'] = keyReleased ? 0 : 1;
                    event.consume();
                    break;
                case PAGE_DOWN:
                    client.keyStatuses['p'] = keyReleased ? 0 : 1;
                    event.consume();
                    break;
            }

            if (!keyReleased) {
                KeyboardState.onKeyDown(keyCode);
            }

            if (event.isConsumed())
                return;

            EventType<KeyEvent> keyEvent = keyReleased ? javafx.scene.input.KeyEvent.KEY_RELEASED : javafx.scene.input.KeyEvent.KEY_PRESSED;
            List<KeyCombination> comboActions = KeyBindings.matchedCombination(keyEvent);

            comboActions.forEach(keyCombination -> {
                log.info("Found {}", keyCombination.requiredKeys().stream().map(String::valueOf).collect(Collectors.joining(",")));
                keyCombination.onValid().ifPresent(consumer -> consumer.accept(keyEvent));
                if (keyCombination.consumesEvent()) {
                    event.consume();
                }
            });

            if (keyReleased) {
                KeyboardState.onKeyUp(keyCode);
            }




        }

    }

}
