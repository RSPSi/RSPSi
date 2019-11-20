package com.rspsi.swatches;

import javafx.scene.Group;

public class UnderlaySwatch extends BaseSwatch {

	private int index;

	public UnderlaySwatch(Group group, String text, int underlayId) {
		super(group, text, SwatchType.UNDERLAY);
		index = underlayId;
	}

	public int getIndex() {
		return index;
	}

}
