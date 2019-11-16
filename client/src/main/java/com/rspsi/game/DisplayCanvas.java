package com.rspsi.game;

import java.awt.image.BufferedImage;

import javax.swing.event.ChangeListener;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;

public final class DisplayCanvas extends Canvas {

	public DisplayCanvas(int width, int height) {
		this(width, height, true);
	}
	
	public DisplayCanvas(int width, int height, boolean renderingHints) {
		this.setHeight(height);
		this.setWidth(width);
		this.setFocusTraversable(true);
		this.setFocused(true);
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> requestFocus());
		this.addEventFilter(MouseEvent.MOUSE_DRAGGED, (e) -> requestFocus());
		this.addEventFilter(MouseEvent.MOUSE_RELEASED, (e) -> requestFocus());
		/*fxGraphics = new FXGraphics2D(this.getGraphicsContext2D());
		if(renderingHints) {
			fxGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
			fxGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			fxGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			fxGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			fxGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		}
		
		fxGraphics.setRenderingHint(FXHints.KEY_USE_FX_FONT_METRICS, false);*/
	}

	
	
	public GraphicsContext getContext() {
		return getGraphicsContext2D();
	}

	/*public FXGraphics2D getFxGraphics2D(){
		return fxGraphics;
	}*/

	@Override
	public boolean isResizable() {
		return true;
	}

	/*	public Graphics getGraphics() {
			return fxGraphics;
		}*/
	
	public void clear() {
		this.getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
	}
	
	public void drawImage(WritableImage fxImage, int x, int y) {
		getGraphicsContext2D().drawImage(fxImage, x, y);
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
				if (color != 0 && color != 0xFFFFFF && firstXPos > x) {
					firstXPos = x;
				}
				if (color != 0 && color != 0xFFFFFF && firstYPos > y) {
					firstYPos = y;
				}
			}
		}

		for (int x = width - 1; x > 0; x--) {
			for (int y = height - 1; y > 0; y--) {
				int color = reader.getArgb(x, y) & 0xffffff;
				// System.out.println(color);
				if (color != 0 && color != 0xFFFFFF && x > lastXPos) {
					lastXPos = x;
				}
				if (color != 0 && color != 0xFFFFFF && y > lastYPos) {
					lastYPos = y;
				}
			}
		}
		if(lastXPos == -1)
			lastXPos = width;
		if(lastYPos == -1)
			lastYPos = height;
		if(firstXPos == width)
			firstXPos = 0;
		if(firstYPos == height)
			firstYPos = 0;
		//System.out.println(firstXPos + "/" + lastXPos + ":" + firstYPos + "/" + lastYPos + " | " + snapshot.getWidth() + ":"
		//		+ snapshot.getHeight());

		return new WritableImage(reader, firstXPos, firstYPos, lastXPos - firstXPos, lastYPos - firstYPos);
		
	}
	
	
}