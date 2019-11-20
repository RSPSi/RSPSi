package com.jagex.net;

import com.rspsi.cache.CacheFileType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MapResourceRequest extends ResourceRequest {
	
	public MapResourceRequest(int regionId, int file) {
		super(file, CacheFileType.MAP);
		this.regionId = regionId;
	}

	private int regionId;

	
	

}