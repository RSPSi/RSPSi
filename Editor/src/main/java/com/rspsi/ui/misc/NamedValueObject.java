package com.rspsi.ui.misc;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NamedValueObject {

	private StringProperty name, value;

	public NamedValueObject(String name, String value) {

		this.name = new SimpleStringProperty(name);
		this.value = new SimpleStringProperty(value);
	}

	public String getName() {
		return name.get();
	}

	public String getValue() {
		return value.get();
	}

}
