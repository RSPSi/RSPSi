package com.rspsi.plugin;

import com.jagex.Cache;
import com.jagex.Client;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.cache.loader.anim.FrameBaseLoader;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.net.ResourceResponse;
import com.rspsi.plugin.loader.*;
import com.rspsi.plugins.core.ClientPlugin;
import com.displee.cache.index.archive.Archive;

import java.io.IOException;


public class MyClientPlugin implements ClientPlugin {
	
	
	private MyTextureLoader textureLoader;
	private MyObjectDefinitionLoader objLoader;
	private MyFrameLoader frameLoader;
	
	@Override
	public void initializePlugin() {
		objLoader = new MyObjectDefinitionLoader();
		textureLoader = new MyTextureLoader();
		TextureLoader.instance = textureLoader;
		
		ObjectDefinitionLoader.instance = objLoader;
		FloorDefinitionLoader.instance = new MyFloorDefinitionLoader();
		AnimationDefinitionLoader.instance = new MyAnimationDefinitionLoader();
		MapIndexLoader.instance = new MyMapIndexLoader();
		GraphicLoader.instance = new MyGraphicLoader();
		frameLoader = new MyFrameLoader();
		FrameLoader.instance = frameLoader; 
		FrameBaseLoader.instance = new MyFrameBaseLoader();
		VariableBitLoader.instance = new MyVarbitLoader();
	}

	@Override
	public void onGameLoaded(Client client) throws IOException {
			Cache cache = client.getCache();
			Archive config = cache.createArchive(2, "config");
			Archive sound = cache.createArchive(8, "sound");
			objLoader.init(config, sound);
			VariableBitLoader.instance.init(config);
			FloorDefinitionLoader.instance.init(config);
			
			AnimationDefinitionLoader.instance.init(config);

			Archive versionList = cache.createArchive(5, "versionlist");
			MapIndexLoader.instance.init(versionList);
			

			Archive textures = cache.createArchive(6, "textures");
			TextureLoader.instance.init(textures);
	
	}

	@Override
	public void onResourceDelivered(ResourceResponse arg0) {
		// TODO Auto-generated method stub
		
	}

}
