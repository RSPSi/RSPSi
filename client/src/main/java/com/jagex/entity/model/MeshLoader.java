package com.jagex.entity.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jagex.net.ResourceProvider;
import com.jagex.net.ResourceResponse;

public class MeshLoader {
	
	public MeshLoader(ResourceProvider provider) throws Exception {
		if(singleton != null)
			throw new Exception("MeshLoader.class already loaded!");
		this.provider = provider;
		EventBus.getDefault().register(this);
		singleton = this;
	}
	
	private Map<Integer, Mesh> loadedMeshes = Collections.synchronizedMap(Maps.newHashMap());
	private List<Integer> awaitingLoad = Collections.synchronizedList(Lists.newArrayList());
	private ResourceProvider provider;
	

	public void clear(int id) {
		loadedMeshes.remove(id);
	}

	public void dispose() {
		loadedMeshes.clear();
	}
	
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onResourceResponse(ResourceResponse response) {
		if(response.getRequest().getType() == 0) {
			load(response.decompress(), response.getRequest().getFile());
		}
	}
	
	public static Mesh load(byte[] data) {
		MeshRevision revision = MeshUtils.getRevision(data);
		switch(revision) {
		case REVISION_525:
			return new Mesh525(data);
		case REVISION_622:
			return new Mesh622(data);
		case REVISION_317:
		default:
			return new Mesh317(data);
		
		}
	}
	
	public Mesh load(byte[] data, int id) {
		MeshRevision revision = MeshUtils.getRevision(data);
		//System.out.println("Attempting to load model " + id + " revision " + revision.name());
		Mesh mesh = null;
		switch(revision) {
		case REVISION_525:
			mesh = new Mesh525(data);
			break;
		case REVISION_622:
			mesh = new Mesh622(data);
			break;
			
		default:
		case REVISION_317:
			mesh = new Mesh317(data);
			break;
		
		}
		
		loadedMeshes.put(id, mesh);
		awaitingLoad.remove(Integer.valueOf(id));
		
		return mesh;
	}
	
	public boolean loaded(int id) {
		if(loadedMeshes.containsKey(id))
			return true;

		boolean alreadyLoading = awaitingLoad.contains(id);
		if (!alreadyLoading) {
			awaitingLoad.add(id);
			System.out.println("Requested model " + id);
			provider.requestFile(0, id);
			return false;
		}
		return false;
	}

	public Mesh lookup(int id) {
		if(loaded(id)) {
			return loadedMeshes.get(id).clone();
		} 
		return null;
	}
	
	public void requestMesh(int id) {
		provider.requestFile(0, id);
	}


	public static MeshLoader getSingleton() {
		return singleton;
	}

	private static MeshLoader singleton; 
}
