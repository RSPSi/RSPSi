package com.rspsi.plugin;

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
import com.rspsi.plugin.loader.*;
import com.rspsi.plugins.ClientPlugin;
import com.displee.cache.index.archive.Archive;


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
	public void onGameLoaded(Client client) {

			Archive config = client.getCache().createArchive(2, "config");
			objLoader.init(config);
			
			FloorDefinitionLoader.instance.init(config);

			VariableBitLoader.instance.init(config);
			AnimationDefinitionLoader.instance.init(config);

			Archive versionList = client.getCache().createArchive(5, "versionlist");
			MapIndexLoader.instance.init(versionList);
			

			Archive textures = client.getCache().createArchive(6, "textures");
			TextureLoader.instance.init(textures);
			
		
	}

}
