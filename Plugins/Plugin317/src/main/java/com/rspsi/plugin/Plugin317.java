package com.rspsi.plugin;

import com.displee.cache.index.archive.Archive;

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
import com.rspsi.plugin.loader.AnimationDefinitionLoader317;
import com.rspsi.plugin.loader.FloorDefinitionLoader317;
import com.rspsi.plugin.loader.FrameBaseLoader317;
import com.rspsi.plugin.loader.FrameLoader317;
import com.rspsi.plugin.loader.GraphicLoader317;
import com.rspsi.plugin.loader.MapIndexLoader317;
import com.rspsi.plugin.loader.ObjectDefinitionLoader317;
import com.rspsi.plugin.loader.TextureLoader317;
import com.rspsi.plugin.loader.VarbitLoader317;
import com.rspsi.plugins.core.ClientPlugin;


public class Plugin317 implements ClientPlugin {

	private FrameLoader317 frameLoader;
	
	@Override
	public void initializePlugin() {
		ObjectDefinitionLoader.instance = new ObjectDefinitionLoader317();
		FloorDefinitionLoader.instance = new FloorDefinitionLoader317();
		AnimationDefinitionLoader.instance = new AnimationDefinitionLoader317();
		MapIndexLoader.instance = new MapIndexLoader317();
		TextureLoader.instance = new TextureLoader317();
		frameLoader = new FrameLoader317();
		FrameLoader.instance = frameLoader;
		FrameBaseLoader.instance = new FrameBaseLoader317();
		GraphicLoader.instance = new GraphicLoader317();
		VariableBitLoader.instance = new VarbitLoader317();
	}

	@Override
	public void onGameLoaded(Client client) {
			frameLoader.init(3500);
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
