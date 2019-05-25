package com.jagex.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class ResourceRequest {
	
	private int file;
	private int type;
	private long requestTime;
	
	public ResourceRequest(int file, int type) {
		this.file = file;
		this.type = type;
		this.requestTime = System.currentTimeMillis();
	}

	public long getAge() {
		return System.currentTimeMillis() - requestTime;
	}

	public int getFile() {
		return file;
	}

	public int getType() {
		return type;
	}

	public void setFile(int file) {
		this.file = file;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

}