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

	
public class AstraeusPlugin implements ClientPlugin {

	private MyFrameLoader frameLoader;
	
	@Override
	public void initializePlugin() {
		ObjectDefinitionLoader.instance = new MyObjectDefinitionLoader();
		FloorDefinitionLoader.instance = new MyFloorDefinitionLoader();
		AnimationDefinitionLoader.instance = new MyAnimationDefinitionLoader();
		MapIndexLoader.instance = new MyMapIndexLoader();
		TextureLoader.instance = new MyTextureLoader();
		frameLoader = new MyFrameLoader();
		FrameLoader.instance = frameLoader;
		FrameBaseLoader.instance = new MyFrameBaseLoader();
		GraphicLoader.instance = new MyGraphicLoader();
		VariableBitLoader.instance = new MyVarbitLoader();
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
