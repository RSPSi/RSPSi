package com.rspsi.game;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.RenderingHints;

import org.jfree.fx.FXGraphics2D;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;

/**
 * A custom {@link Frame} used to draw the game in.
 */

public final class DisplayCanvas extends Canvas {

	private FXGraphics2D fxGraphics;
	
	public DisplayCanvas(int width, int height) {
		this(width, height, true);
	}
	
	public DisplayCanvas(int width, int height, boolean renderingHints) {
		this.widthProperty().set(width);
		this.heightProperty().set(height);
		this.setFocusTraversable(true);
		this.setFocused(true);
		this.addEventFilter(MouseEvent.ANY, (e) -> requestFocus());
		fxGraphics = new FXGraphics2D(this.getGraphicsContext2D());
		if(renderingHints) {
			fxGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
			fxGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			fxGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			fxGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			fxGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		}
	}
	
	
	public GraphicsContext getContext() {
		return getGraphicsContext2D();
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	public Graphics getGraphics() {
		return fxGraphics;
	}
	
	public void clear() {
		this.getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
	}
	
	public WritableImage trimmedSnapshot() {
		WritableImage snapshot = this.snapshot(null, null);
		PixelReader reader = snapshot.getPixelReader();
		int width = (int) snapshot.getWidth();
		int height = (int) snapshot.getHeight();
		int firstXPos = width;
		int firstYPos = height;
		int lastXPos = -1, lastYPos = -1;
		for (int x = width - 1; x > 0; x--) {
			for (int y = height - 1; y > 0; y--) {
				int color = reader.getArgb(x, y) & 0xffffff;
				// System.out.println(color);
				if (color != 0 && firstXPos > x) {
					firstXPos = x;
				}
				if (color != 0 && firstYPos > y) {
					firstYPos = y;
				}
			}
		}

		for (int x = width - 1; x > 0; x--) {
			for (int y = height - 1; y > 0; y--) {
				int color = reader.getArgb(x, y) & 0xffffff;
				// System.out.println(color);
				if (color != 0 && x > lastXPos) {
					lastXPos = x;
				}
				if (color != 0 && y > lastYPos) {
					lastYPos = y;
				}
			}
		}
		//System.out.println(firstXPos + "/" + lastXPos + ":" + firstYPos + "/" + lastYPos + " | " + snapshot.getWidth() + ":"
		//		+ snapshot.getHeight());

		return new WritableImage(reader, firstXPos, firstYPos, lastXPos - firstXPos, lastYPos - firstYPos);
		
	}
	
	
}