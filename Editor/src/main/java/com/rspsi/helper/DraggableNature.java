package com.rspsi.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rspsi.helper.Draggable.Listener;

import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;

public class DraggableNature implements EventHandler<MouseEvent> {
    private double lastMouseX = 0, lastMouseY = 0; // scene coords

    private boolean dragging = false;

    private final boolean enabled = true;
    private final Node eventNode;
    private final List<Node> dragNodes = new ArrayList<>();
    private final List<Listener> dragListeners = new ArrayList<>();
    @Setter @Getter
    private ScrollPane scrollPane;

    public DraggableNature(final Node node) {
        this(node, node);
    }


    public DraggableNature(final Node eventNode, final Node... dragNodes) {
        this.eventNode = eventNode;
        this.dragNodes.addAll(Arrays.asList(dragNodes));
        this.eventNode.addEventHandler(MouseEvent.ANY, this);
    }

    public final boolean addDraggedNode(final Node node) {
        if (!this.dragNodes.contains(node)) {
            return this.dragNodes.add(node);
        }
        return false;
    }

    public final boolean addListener(final Listener listener) {
        return this.dragListeners.add(listener);
    }

    public final void detatch() {
        this.eventNode.removeEventFilter(MouseEvent.ANY, this);
    }

    public final List<Node> getDragNodes() {
        return new ArrayList<>(this.dragNodes);
    }

    public final Node getEventNode() {
        return this.eventNode;
    }

    @Override
    public final void handle(final MouseEvent event) {
        if (MouseEvent.MOUSE_PRESSED == event.getEventType()) {
            if (this.enabled && this.eventNode.contains(event.getX(), event.getY())) {
                this.lastMouseX = event.getSceneX();
                this.lastMouseY = event.getSceneY();
                event.consume();
            }
        } else if (MouseEvent.MOUSE_DRAGGED == event.getEventType()) {
            if (!this.dragging) {
                this.dragging = true;
                for (final Listener listener : this.dragListeners) {
                    listener.accept(this, event, Draggable.DragEvent.DragStart);
                }
            }
            if (this.dragging) {
            	
                double deltaX = event.getSceneX() - this.lastMouseX;
                double deltaY = event.getSceneY() - this.lastMouseY;

                for (final Node dragNode : this.dragNodes) {
                   
                    final double initialTranslateX = dragNode.getTranslateX();
                    final double initialTranslateY = dragNode.getTranslateY();
                    dragNode.setTranslateX(initialTranslateX + deltaX);
                    dragNode.setTranslateY(initialTranslateY + deltaY);

                }

                this.lastMouseX = event.getSceneX();
                this.lastMouseY = event.getSceneY();
                if (scrollPane != null) {
                	   for (final Node dragNode : this.dragNodes) {
                           final double initialTranslateX = dragNode.getTranslateX();
                           final double initialTranslateY = dragNode.getTranslateY();
                           Bounds visibleBounds = getVisibleBounds(dragNode);
                           System.out.println(visibleBounds);
                           if(visibleBounds.getMaxY() < dragNode.prefHeight(0) && scrollPane.getVvalue() < 1.0) {
                        	   System.out.println("OBOUNDS");
                        	   scrollPane.setVvalue(scrollPane.getVvalue() + 0.03);

                               dragNode.setTranslateY(dragNode.getTranslateY() + 0.041 * scrollPane.getHeight());
                           } else if(visibleBounds.getMinY() > 0 && scrollPane.getVvalue() > 0.0) {
                        	   System.out.println("OBOUNDS");
                        	   scrollPane.setVvalue(scrollPane.getVvalue() - 0.03);

                               dragNode.setTranslateY(dragNode.getTranslateY() - (0.041 * scrollPane.getHeight()));
                           }

                       }
                }


                event.consume();
                for (final Listener listener : this.dragListeners) {
                    listener.accept(this, event, Draggable.DragEvent.Drag);
                }
            }
        } else if (MouseEvent.MOUSE_RELEASED == event.getEventType()) {
            if (this.dragging) {
                event.consume();
                this.dragging = false;
                for (final Listener listener : this.dragListeners) {
                    listener.accept(this, event, Draggable.DragEvent.DragEnd);
                }
            }
        }

    }

    public final boolean removeDraggedNode(final Node node) {
        return this.dragNodes.remove(node);
    }

    public final boolean removeListener(final Listener listener) {
        return this.dragListeners.remove(listener);
    }

    /**
     * When the initial mousePressed is missing we can supply the first coordinates programmatically.
     * @param lastMouseX
     * @param lastMouseY
     */
    public final void setLastMouse(final double lastMouseX, final double lastMouseY) {
        this.lastMouseX = lastMouseX;
        this.lastMouseY = lastMouseY;
    }
    
    public static Bounds getVisibleBounds(Node aNode)
    {
        // If node not visible, return empty bounds
        if(!aNode.isVisible()) return new BoundingBox(0,0,-1,-1);
        
        // If node has clip, return clip bounds in node coords
        if(aNode.getClip()!=null) return aNode.getClip().getBoundsInParent();
        
        // If node has parent, get parent visible bounds in node coords
        Bounds bounds = aNode.getParent()!=null? getVisibleBounds(aNode.getParent()) : null;
        if(bounds!=null && !bounds.isEmpty()) bounds = aNode.parentToLocal(bounds);
        return bounds;
    }
}