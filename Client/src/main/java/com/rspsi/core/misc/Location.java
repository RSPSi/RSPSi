package com.rspsi.core.misc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {
	
	private int x, y, z;

	public static Location of(int x, int y, int z) {
		return new Location(x, y, z);
	}

	public static Location of(int x, int y){
		return of(x, y, 0);
	}
}
