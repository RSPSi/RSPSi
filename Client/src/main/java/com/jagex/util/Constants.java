package com.jagex.util;

public class Constants {
	

	public static int[] COSINE, SINE, SHADOW_DECAY, LIGHT_DECAY;

	static {
		SHADOW_DECAY = new int[512];
		LIGHT_DECAY = new int[2048];
		SINE = new int[2048];
		COSINE = new int[2048];
	
		for (int i = 1; i < 512; i++) {
			SHADOW_DECAY[i] = 32768 / i;
		}
	
		for (int j = 1; j < 2048; j++) {
			LIGHT_DECAY[j] = 0x10000 / j;
		}
	
		for (int theta = 0; theta < 2048; theta++) {
			SINE[theta] = (int) (65536D * Math.sin(theta * 0.0030679614999999999D));
			COSINE[theta] = (int) (65536D * Math.cos(theta * 0.0030679614999999999D));
		}
	}

	
}
