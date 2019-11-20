package com.jagex.cache.def;

import java.nio.ByteBuffer;

import com.jagex.util.ByteBufferUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class RSArea {
	
	private final int id;

	private int spriteId = -1;
	private int anInt1967 = -1;
	private String name;
	private int anInt1959;
	private int anInt1968 = 0;
	private int[] anIntArray1982;
	private String aString1970;
	private int[] anIntArray1981;
	private int anInt1980;
	private byte[] aByteArray1979;
	private String[] aStringArray1969 = new String[5];

	

}
