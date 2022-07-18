package com.rspsi.plugin;

import com.displee.cache.index.archive.Archive;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.jagex.Client;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.cache.loader.anim.FrameBaseLoader;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.net.ResourceResponse;
import com.rspsi.plugin.loader.MyAnimationDefinitionLoader;
import com.rspsi.plugin.loader.MyFloorDefinitionLoader;
import com.rspsi.plugin.loader.MyFrameBaseLoader;
import com.rspsi.plugin.loader.MyFrameLoader;
import com.rspsi.plugin.loader.MyGraphicLoader;
import com.rspsi.plugin.loader.MyMapIndexLoader;
import com.rspsi.plugin.loader.MyObjectDefinitionLoader;
import com.rspsi.plugin.loader.MyTextureLoader;
import com.rspsi.plugins.core.ClientPlugin;
	
public class MyClientPlugin implements ClientPlugin {

	@Override
	public void initializePlugin() {
		//Initialize all the instances here
		ObjectDefinitionLoader.instance = new MyObjectDefinitionLoader();
		FloorDefinitionLoader.instance = new MyFloorDefinitionLoader();
		MapIndexLoader.instance = new MyMapIndexLoader();
		
		AnimationDefinitionLoader.instance = new MyAnimationDefinitionLoader();
		
		FrameBaseLoader.instance = new MyFrameBaseLoader();
		FrameLoader.instance = new MyFrameLoader();
		
		GraphicLoader.instance = new MyGraphicLoader();
		
		TextureLoader.instance = new MyTextureLoader();
		
	}

	@Override
	public void onGameLoaded(Client client) {
		Archive config = client.getCache().createArchive(2, "config");
		ObjectDefinitionLoader.instance.init(config);
		// Init your instances here

	}

	@Override
	public void onResourceDelivered(ResourceResponse arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//OR
	
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onResourceResponse(ResourceResponse response) {
		//This can be called in any class
		//Provided you register it with 
		//EventBus.getDefault().register(this);
	}
		
	


}
