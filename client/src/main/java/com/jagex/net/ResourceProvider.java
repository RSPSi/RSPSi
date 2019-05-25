package com.jagex.net;

import java.util.Collections;
import java.util.List;

import org.greenrobot.eventbus.EventBus;

import com.google.common.collect.Lists;
import com.jagex.Cache;
import com.jagex.io.Buffer;

import io.nshusa.rsam.binary.Archive;

public class ResourceProvider implements Runnable {

	private int[] frames;

	private byte[] models;
	private int[] musicPriorities;
	private List<ResourceRequest> requests = Collections.synchronizedList(Lists.newArrayList());

	private int[][] versions = new int[4][];
	
	private Cache cache;
	
	public ResourceProvider(Cache cache) {
		this.cache = cache;
	}

	public final int frameCount() {
		return frames.length;
	}

	public final int getCount(int type) {
		return versions[type].length;
	}

	public final int getModelAttributes(int file) {
		return models[file];
	}

	public final boolean highPriorityMusic(int file) {
		return musicPriorities[file] == 1;
	}

	public final void init(Archive archive) {
	
	}

	public final void requestFile(int type, int file) {
		if(Lists.newArrayList(requests).stream().anyMatch(node -> node != null && node.getType() == type && node.getFile() == file)) {
			return;
		}
	
		ResourceRequest node = new ResourceRequest(file, type);
		requests.add(node);
	}
	
	public final void handleRequests() {//TODO Add a thread for this
		if(requests.isEmpty())
			return;
		//System.out.println("Grabbing from cache " + (cache == null));
		List<ResourceRequest> loopedResources = Lists.newArrayList(requests);
		
		for(ResourceRequest request : loopedResources) {
			if(request == null) {
				//System.out.println("NODE NULL");
				continue;
			}
			//System.out.println("Grabbing " + request.getType() + ":" + request.getFile());
			try {
				byte[] data = cache.getFile(request.getType(), request.getFile());
				//System.out.println("data null ? " + (data == null));
				if(data != null) {
					ResourceResponse response = new ResourceResponse(request, data);
					EventBus.getDefault().post(response);
				} else {
					throw new Exception("Fetch Error");
				}
			} catch(Exception ex) {
				System.out.println("Failed to fetch resource " + request.getFile() + " from index " + request.getType());
			}
		}
		requests.removeAll(loopedResources);
		
		
	}

	public final int remaining() {
		synchronized (requests) {
			return requests.size();
		}
	}
	

	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try {
			handleRequests();
				Thread.sleep(50);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}