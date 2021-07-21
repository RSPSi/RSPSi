package com.rspsi.jagex.net;

import com.rspsi.editor.cache.CacheFileType;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Getter
public class ResourceRequest {
	
	private int file;
	private CacheFileType type;
	@Setter
	private long timeout = TimeUnit.MINUTES.toMillis(1);
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