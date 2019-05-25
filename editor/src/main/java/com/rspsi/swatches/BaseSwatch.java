package com.rspsi.swatches;

import javafx.scene.Group;

public abstract class BaseSwatch {
	
	private Group group;

	private String text;

	private SwatchType type;

	protected BaseSwatch(Group group, String text, SwatchType type) {
		this.group = group;
		this.text = text;
		this.type = type;
	}

	public Group getGroup() {
		return group;
	}

	public String getText() {
		return text;
	}

	public SwatchType getType() {
		return type;
	}
}
