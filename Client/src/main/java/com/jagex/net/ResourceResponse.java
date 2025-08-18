package com.jagex.net;



import org.displee.util.GZIPUtils;

import java.io.IOException;

public class ResourceResponse {
	
	private ResourceRequest request;
	private byte[] data;
	
	public ResourceResponse(ResourceRequest request, byte[] data) {
		super();
		this.request = request;
		this.data = data;
	}

	public ResourceRequest getRequest() {
		return request;
	}

	public byte[] getData() {
		return data;
	}
	
	public byte[] decompress() {
		byte[] unzipped = GZIPUtils.unzip(data);
		return unzipped == null ? data : unzipped;
	}

}
