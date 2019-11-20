package com.rspsi.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * Generalised implementation of 'Draggability' of a {@link Node}. The Draggable class is used as a 'namespace' for the internal
 * class/interfaces/enum.
 * @author phill
 *
 */
public class Draggable {
    public enum DragEvent {
        None, DragStart, Drag, DragEnd
    }

    /**
     * Marker for an entity that has draggable nature.
     * @author phill
     */
    public interface Interface {
    	DraggableNature getDraggableNature();
    }

    public interface Listener {
        void accept(DraggableNature draggableNature, MouseEvent event, DragEvent dragEvent);
    }

    /**
     * Class that encapsulates the draggable nature of a node.
     * <ul>
     * <li>EventNode: the event that receives the drag events</li>
     * <li>One or more DragNodes: that move in response to the drag events. The EventNode is usually (but not always) a
     * DragNode</li>
     * <li>Listeners: listen for the drag events</li>
     * </ul>
     * @author phill
     *
     */
    
}