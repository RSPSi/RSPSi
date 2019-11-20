package com.rspsi.helper;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Util class to handle window resizing when a stage style set to
 * StageStyle.UNDECORATED. Created on 8/15/17.
 *
 * @author Evgenii Kanivets
 */
public class ResizeHelper {

	public static class ResizeListener implements EventHandler<MouseEvent> {
		private Stage stage;
		private Cursor cursorEvent = Cursor.DEFAULT;
		private int border = 4;
		private double startX = 0;
		private double startY = 0;

		// Max and min sizes for controlled stage
		private double minWidth;
		private double maxWidth;
		private double minHeight;
		private double maxHeight;

		private boolean resizing;

		public ResizeListener(Stage stage) {
			this.stage = stage;
		}

		@Override
		public void handle(MouseEvent mouseEvent) {
			EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
			Scene scene = stage.getScene();
			
			if((boolean) scene.getProperties().getOrDefault("maximized", false)) {
				return;
			}

			double mouseEventX = mouseEvent.getSceneX(), mouseEventY = mouseEvent.getSceneY(),
					sceneWidth = scene.getWidth(), sceneHeight = scene.getHeight();

			if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {
				if (mouseEventX < border && mouseEventY < border) {
					cursorEvent = Cursor.NW_RESIZE;
				} else if (mouseEventX < border && mouseEventY > sceneHeight - border) {
					cursorEvent = Cursor.SW_RESIZE;
				} else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
					cursorEvent = Cursor.NE_RESIZE;
				} else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
					cursorEvent = Cursor.SE_RESIZE;
				} else if (mouseEventX < border) {
					cursorEvent = Cursor.W_RESIZE;
				} else if (mouseEventX > sceneWidth - border) {
					cursorEvent = Cursor.E_RESIZE;
				} else if (mouseEventY < border) {
					cursorEvent = Cursor.N_RESIZE;
				} else if (mouseEventY > sceneHeight - border) {
					cursorEvent = Cursor.S_RESIZE;
				} else {
					cursorEvent = Cursor.DEFAULT;
				}
				scene.setCursor(cursorEvent);
			} else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType)
					|| MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
				scene.setCursor(Cursor.DEFAULT);
				// resizing = false;
			} else if (MouseEvent.MOUSE_RELEASED.equals(mouseEventType)) {
				// System.out.println("MOUSE RELEASED");
				resizing = false;
			} else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
				startX = stage.getWidth() - mouseEventX;
				startY = stage.getHeight() - mouseEventY;
				if (!Cursor.DEFAULT.equals(cursorEvent)) {
					resizing = true;
					// System.out.println("MOUSE PRESSED");
				}
			} else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {
				if (!Cursor.DEFAULT.equals(cursorEvent)) {
					if (!Cursor.W_RESIZE.equals(cursorEvent) && !Cursor.E_RESIZE.equals(cursorEvent)) {
						double minHeight = stage.getMinHeight() > border * 2 ? stage.getMinHeight() : border * 2;
						if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.N_RESIZE.equals(cursorEvent)
								|| Cursor.NE_RESIZE.equals(cursorEvent)) {
							if (stage.getHeight() > minHeight || mouseEventY < 0) {
								if (setStageHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight())) {
									stage.setY(mouseEvent.getScreenY());
								}
							}
						} else {
							if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
								setStageHeight(mouseEventY + startY);
							}
						}
					}

					if (!Cursor.N_RESIZE.equals(cursorEvent) && !Cursor.S_RESIZE.equals(cursorEvent)) {
						double minWidth = stage.getMinWidth() > border * 2 ? stage.getMinWidth() : border * 2;
						if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.W_RESIZE.equals(cursorEvent)
								|| Cursor.SW_RESIZE.equals(cursorEvent)) {
							if (stage.getWidth() > minWidth || mouseEventX < 0) {
								if (setStageWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth())) {
									stage.setX(mouseEvent.getScreenX());
								}
							}
						} else {
							if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
								setStageWidth(mouseEventX + startX);
							}
						}
					}
				}
				
				if(resizing)
					mouseEvent.consume();

			}
		}

		public boolean isResizing() {
			return resizing;
		}

		public void setMaxHeight(double maxHeight) {
			this.maxHeight = maxHeight;
		}

		public void setMaxWidth(double maxWidth) {
			this.maxWidth = maxWidth;
		}

		public void setMinHeight(double minHeight) {
			this.minHeight = minHeight;
		}

		public void setMinWidth(double minWidth) {
			this.minWidth = minWidth;
		}

		private boolean setStageHeight(double height) {
			double oldHeight = stage.getHeight();
			height = Math.min(height, maxHeight);
			height = Math.max(height, minHeight);
			stage.setHeight(height);
			return stage.getHeight() != oldHeight;
		}

		private boolean setStageWidth(double width) {
			double oldWidth = stage.getHeight();
			width = Math.min(width, maxWidth);
			width = Math.max(width, minWidth);
			stage.setWidth(width);
			return stage.getWidth() != oldWidth;
		}

	}

	private static void addListenerDeeply(Node node, EventHandler<MouseEvent> listener) {
		node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
		node.addEventHandler(MouseEvent.MOUSE_RELEASED, listener);
		node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
		node.addEventHandler(MouseEvent.MOUSE_EXITED, listener);
		node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, listener);
		if (node instanceof Parent) {
			Parent parent = (Parent) node;
			ObservableList<Node> children = parent.getChildrenUnmodifiable();
			for (Node child : children) {
				addListenerDeeply(child, listener);
			}
		} else if (node instanceof TabPane) {
			TabPane tp = (TabPane) node;
			for (Tab t : tp.getTabs()) {
				if (t.contentProperty().get() != null) {
					addListenerDeeply(t.contentProperty().get(), listener);
				}
			}
		} else if (node instanceof SplitPane) {
			SplitPane sp = (SplitPane) node;
			for (Node n : sp.getItems()) {
				if (n != null) {
					addListenerDeeply(n, listener);
				}
			}
		}
	}

	public static ResizeListener addResizeListener(Stage stage) {
		return addResizeListener(stage, 0, 0, Double.MAX_VALUE, Double.MAX_VALUE);
	}

	public static ResizeListener addResizeListener(Window stage, double minWidth, double minHeight, double maxWidth,
			double maxHeight) {
		ResizeListener resizeListener = new ResizeListener((Stage) stage);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_RELEASED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);

		resizeListener.setMinWidth(minWidth);
		resizeListener.setMinHeight(minHeight);
		resizeListener.setMaxWidth(maxWidth);
		resizeListener.setMaxHeight(maxHeight);

		ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
		for (Node child : children) {
			addListenerDeeply(child, resizeListener);
		}
		return resizeListener;
	}
}