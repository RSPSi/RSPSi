package com.rspsi.jagex.net;

import com.rspsi.editor.cache.CacheFileType;
import lombok.Getter;

@Getter
public class MapResourceRequest extends ResourceRequest {
	
	public MapResourceRequest(int regionId, int file) {
		super(file, CacheFileType.MAP);
		this.regionId = regionId;
	}

	private int regionId;

	
	

}