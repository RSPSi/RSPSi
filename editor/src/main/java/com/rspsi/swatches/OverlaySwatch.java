package com.rspsi.swatches;

import javafx.scene.Group;

public class OverlaySwatch extends BaseSwatch {

	private int index;

	public OverlaySwatch(Group group, String text, int overlayId) {
		super(group, text, SwatchType.OVERLAY);
		index = overlayId;
	}

	public int getIndex() {
		return index;
	}

}
