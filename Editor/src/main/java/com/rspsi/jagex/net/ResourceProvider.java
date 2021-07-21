package com.rspsi.jagex.net;

import com.google.common.collect.Lists;
import com.rspsi.editor.cache.CacheFileType;
import com.rspsi.jagex.Cache;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
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

	public final void requestMap(int file, int regionId) {
		if(Lists.newArrayList(requests).stream().anyMatch(node -> node != null && node.getType() == CacheFileType.MAP && node.getFile() == file)) {
			return;
		}
	
		ResourceRequest node = new MapResourceRequest(regionId, file);
		requests.add(node);
	}

	public final void requestFile(CacheFileType type, int file) {
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
		List<ResourceRequest> successfulRequests = Lists.newArrayList();
		for(ResourceRequest request : loopedResources) {
			
			if(request == null) {
				//System.out.println("NODE NULL");
				continue;
			}
			//System.out.println("Grabbing " + request.getType() + ":" + request.getFile());
			try {
				byte[] data;
				if(request.getType() == CacheFileType.MAP) {
					MapResourceRequest mapReq = (MapResourceRequest) request;
					data = cache.readMap(request.getFile(), mapReq.getRegionId());
				} else {
					data = cache.readFile(request.getType(), request.getFile());
				}
				//System.out.println("data null ? " + (data == null));
				if(data != null) {
					successfulRequests.add(request);
					ResourceResponse response = new ResourceResponse(request, data);
					EventBus.getDefault().post(response);
				} else {
					throw new Exception("Fetch Error");
				}
			} catch(Exception ex) {
				if(request.getAge() > request.getTimeout()) {
					requests.remove(request);
					ResourceTimeout timeout = new ResourceTimeout(request, Optional.of(ex));
					EventBus.getDefault().post(timeout);
					log.info("Failed to fetch resource {} from index {}", request.getFile(), request.getType());

				}
			}
		}
		requests.removeAll(successfulRequests);
		
		
	}

	public final int remaining() {
		//synchronized (requests) {
			return requests.size();
		//}
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