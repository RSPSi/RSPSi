package com.rspsi.core.misc;

public enum ComparatorOperator {
	LESS_THAN("<"), LESS_THAN_OR_EQUAL_TO("<="), EQUAL_TO("="), GREATER_THAN_OR_EQUAL_TO(">"), GREATER_THAN(">=");
	
	ComparatorOperator(String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}
	
	private String stringRepresentation;
	
	@Override
	public String toString() {
		return stringRepresentation;
	}

}
