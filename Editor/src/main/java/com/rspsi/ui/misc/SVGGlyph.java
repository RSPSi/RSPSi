package com.rspsi.ui.misc;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

/**
 * Node that is used to show svg images
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class SVGGlyph extends Pane {
	private static final String DEFAULT_STYLE_CLASS = "jfx-svg-glyph";

	private static final int DEFAULT_PREF_SIZE = 64;
	private final int glyphId;
	private final String name;
	private final String pathContent;
	private double widthHeightRatio = 1;
	private ObjectProperty<Paint> fill = new SimpleObjectProperty<>();

	/**
	 * Constructs SVGGlyph node for a specified svg content and color <b>Note:</b>
	 * name and glyphId is not needed when creating a single SVG image, they have
	 * been used in {@link SVGGlyphLoader} to load icomoon svg font.
	 *
	 * @param glyphId
	 *            integer represents the glyph id
	 * @param name
	 *            glyph name
	 * @param svgPathContent
	 *            svg content
	 * @param fill
	 *            svg color
	 */
	public SVGGlyph(int glyphId, String name, String svgPathContent, Paint fill) {
		this.glyphId = glyphId;
		this.name = name;
		pathContent = svgPathContent;
		getStyleClass().add(DEFAULT_STYLE_CLASS);
		this.fill.addListener((observable, oldValue,
				newValue) -> setBackground(new Background(new BackgroundFill(newValue, null, null))));

		SVGPath shape = new SVGPath();
		shape.setContent(svgPathContent);
		setShape(shape);
		setFill(fill);
		widthHeightRatio = shape.prefWidth(-1) / shape.prefHeight(-1);
		setPrefSize(DEFAULT_PREF_SIZE, DEFAULT_PREF_SIZE);
	}

	public SVGGlyph copy() {
		// TODO Auto-generated method stub
		SVGGlyph glyph = new SVGGlyph(glyphId, name, pathContent, getFill());
		glyph.setSize(this.getPrefWidth(), this.getPrefHeight());
		glyph.setTranslateX(this.getTranslateX());
		glyph.setTranslateY(this.getTranslateY());
		glyph.setTranslateZ(this.getTranslateZ());
		return glyph;
	}

	public ObjectProperty<Paint> fillProperty() {
		return fill;
	}

	public Paint getFill() {
		return fill.getValue();
	}

	/**
	 * @return current svg id
	 */
	public int getGlyphId() {
		return glyphId;
	}

	/**
	 * @return current svg name
	 */
	public String getName() {
		return name;
	}

	/**
	 * svg color property
	 */
	public void setFill(Paint fill) {
		this.fill.setValue(fill);
	}

	/**
	 * resize the svg to a certain width and height
	 *
	 * @param width
	 * @param height
	 */
	public void setSize(double width, double height) {
		this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		this.setPrefSize(width, height);
		this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
	}

	/**
	 * resize the svg to this size while keeping the width/height ratio
	 * 
	 * @param size
	 *            in pixel
	 */
	public void setSizeRatio(double size) {
		double width = widthHeightRatio * size;
		double height = size / widthHeightRatio;
		if (width <= size) {
			setSize(width, size);
		} else if (height <= size) {
			setSize(size, height);
		} else {
			setSize(size, size);
		}
	}
}
