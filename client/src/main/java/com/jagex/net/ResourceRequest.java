package com.jagex.net;

import com.rspsi.cache.CacheFileType;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Getter
public class ResourceRequest {
	
	private int file;
	private CacheFileType type;
	private long requestTime;
	
	public ResourceRequest(int file, CacheFileType type) {
		this.file = file;
		this.type = type;
		this.requestTime = System.currentTimeMillis();
	}

	public long getAge() {
		return System.currentTimeMillis() - requestTime;
	}

	
	

}