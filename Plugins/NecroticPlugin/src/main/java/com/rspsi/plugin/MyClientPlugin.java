package com.rspsi.plugin;

import com.displee.cache.index.archive.Archive;

import com.rspsi.jagex.Cache;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.rspsi.jagex.cache.loader.anim.FrameBaseLoader;
import com.rspsi.jagex.cache.loader.anim.FrameLoader;
import com.rspsi.jagex.cache.loader.anim.GraphicLoader;
import com.rspsi.jagex.cache.loader.config.VariableBitLoader;
import com.rspsi.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.rspsi.jagex.cache.loader.map.MapIndexLoader;
import com.rspsi.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.rspsi.jagex.cache.loader.textures.TextureLoader;
import com.rspsi.jagex.net.ResourceResponse;
import com.rspsi.plugin.loader.MyAnimationDefinitionLoader;
import com.rspsi.plugin.loader.MyFloorDefinitionLoader;
import com.rspsi.plugin.loader.MyFrameBaseLoader;
import com.rspsi.plugin.loader.MyFrameLoader;
import com.rspsi.plugin.loader.MyGraphicLoader;
import com.rspsi.plugin.loader.MyMapIndexLoader;
import com.rspsi.plugin.loader.MyObjectDefinitionLoader;
import com.rspsi.plugin.loader.MyTextureLoader;
import com.rspsi.plugin.loader.MyVarbitLoader;
import com.rspsi.plugins.ClientPlugin;

	
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
		VariableBitLoader.instance = new MyVarbitLoader();
		FrameBaseLoader.instance = new MyFrameBaseLoader();
	}

	@Override
	public void onGameLoaded(Client client) {
		try {
			Cache cache = client.getCache();
			Archive config = cache.createArchive(2, "config");
			Archive sound = cache.createArchive(8, "sound");
			objLoader.init(config);
			VariableBitLoader.instance.init(config);
			
			FloorDefinitionLoader.instance.init(config);
			
			AnimationDefinitionLoader.instance.init(config);

			Archive versionList = cache.createArchive(5, "versionlist");
			MapIndexLoader.instance.init(versionList);
			

			Archive textures = cache.createArchive(6, "textures");
			TextureLoader.instance.init(textures);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResourceDelivered(ResourceResponse arg0) {
		// TODO Auto-generated method stub
		
	}

}
