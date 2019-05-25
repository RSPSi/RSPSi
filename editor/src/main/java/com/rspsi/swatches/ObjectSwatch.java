package com.rspsi.swatches;

import com.rspsi.datasets.ObjectDataset;

import javafx.scene.Group;

public class ObjectSwatch extends BaseSwatch {

	private ObjectDataset data;

	public ObjectSwatch(ObjectDataset cell, Group group, String text) {
		super(group, text, SwatchType.OBJECT);
		data = cell;
	}

	public ObjectDataset getData() {
		return data;
	}

}
