package com.jagex.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ObjectKey {
	
	private int x, y;
	private int id;
	private int type;
	private int orientation;
	private boolean solid;
	private boolean interactive;
	

	
}
