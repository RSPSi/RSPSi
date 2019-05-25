package com.jagex.net;

import java.io.IOException;

import com.jagex.util.GZIPUtils;

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
		try {
			return GZIPUtils.unzip(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
