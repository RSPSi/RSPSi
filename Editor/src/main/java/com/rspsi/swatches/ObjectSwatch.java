package com.rspsi.swatches;

import com.rspsi.datasets.ObjectDataset;
import javafx.scene.Group;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectSwatch extends BaseSwatch {

	private ObjectDataset data;
	private Label label;

	public ObjectSwatch(ObjectDataset cell, Group group, String text) {
		super(group, text, SwatchType.OBJECT);
		data = cell;
	}


}
