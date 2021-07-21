package com.rspsi.plugin;

import com.displee.cache.index.archive.Archive;

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
import com.rspsi.plugin.loader.AnimationDefinitionLoader474;
import com.rspsi.plugin.loader.FloorDefinitionLoader474;
import com.rspsi.plugin.loader.FrameBaseLoader474;
import com.rspsi.plugin.loader.FrameLoader474;
import com.rspsi.plugin.loader.GraphicLoader474;
import com.rspsi.plugin.loader.MapIndexLoader474;
import com.rspsi.plugin.loader.ObjectDefinitionLoader474;
import com.rspsi.plugin.loader.TextureLoader474;
import com.rspsi.plugin.loader.VarbitLoader474;
import com.rspsi.plugins.ClientPlugin;


public class Plugin474 implements ClientPlugin {

	private FrameLoader474 frameLoader;
	
	@Override
	public void initializePlugin() {
		ObjectDefinitionLoader.instance = new ObjectDefinitionLoader474();
		FloorDefinitionLoader.instance = new FloorDefinitionLoader474();
		AnimationDefinitionLoader.instance = new AnimationDefinitionLoader474();
		MapIndexLoader.instance = new MapIndexLoader474();
		TextureLoader.instance = new TextureLoader474();
		frameLoader = new FrameLoader474();
		FrameLoader.instance = frameLoader;
		FrameBaseLoader.instance = new FrameBaseLoader474();
		GraphicLoader.instance = new GraphicLoader474();
		VariableBitLoader.instance = new VarbitLoader474();
	}

	@Override
	public void onGameLoaded(Client client) {
			frameLoader.init(2500);
			Archive config = client.getCache().createArchive(2, "config");
			ObjectDefinitionLoader.instance.init(config);
			FloorDefinitionLoader.instance.init(config);
			AnimationDefinitionLoader.instance.init(config);
			GraphicLoader.instance.init(config);
			VariableBitLoader.instance.init(config);
			
			Archive version = client.getCache().createArchive(5, "update list");
			MapIndexLoader.instance.init(version);
			

			Archive textures = client.getCache().createArchive(6, "textures");
			TextureLoader.instance.init(textures);
		
	}

	@Override
	public void onResourceDelivered(ResourceResponse arg0) {
		// TODO Auto-generated method stub
		
	}

}
