package com.rspsi.plugin;

import com.displee.cache.index.Index;

import com.jagex.Client;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.cache.loader.anim.FrameBaseLoader;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.cache.loader.config.RSAreaLoader;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.net.ResourceResponse;
import com.rspsi.cache.CacheFileType;
import com.rspsi.plugin.loader.AnimationDefinitionLoaderOSRS;
import com.rspsi.plugin.loader.FloorDefinitionLoaderOSRS;
import com.rspsi.plugin.loader.FrameBaseLoaderOSRS;
import com.rspsi.plugin.loader.FrameLoaderOSRS;
import com.rspsi.plugin.loader.GraphicLoaderOSRS;
import com.rspsi.plugin.loader.MapIndexLoaderOSRS;
import com.rspsi.plugin.loader.ObjectDefinitionLoaderOSRS;
import com.rspsi.plugin.loader.RSAreaLoaderOSRS;
import com.rspsi.plugin.loader.TextureLoaderOSRS;
import com.rspsi.plugin.loader.VarbitLoaderOSRS;
import com.rspsi.plugins.core.ClientPlugin;

public class OSRSPlugin implements ClientPlugin {

	private FrameLoaderOSRS frameLoader;
	private FloorDefinitionLoaderOSRS floorLoader;
	private ObjectDefinitionLoaderOSRS objLoader;
	private AnimationDefinitionLoaderOSRS animDefLoader;
	private GraphicLoaderOSRS graphicLoader;
	private VarbitLoaderOSRS varbitLoader;
	private MapIndexLoaderOSRS mapIndexLoader;
	private TextureLoaderOSRS textureLoader;
	private FrameBaseLoaderOSRS skeletonLoader;
	private RSAreaLoaderOSRS areaLoader;
	
	@Override
	public void initializePlugin() {
		objLoader = new ObjectDefinitionLoaderOSRS();
		floorLoader = new FloorDefinitionLoaderOSRS();
		frameLoader = new FrameLoaderOSRS();
		animDefLoader = new AnimationDefinitionLoaderOSRS();
		
		mapIndexLoader = new MapIndexLoaderOSRS();
		textureLoader = new TextureLoaderOSRS();
		skeletonLoader = new FrameBaseLoaderOSRS();
		graphicLoader = new GraphicLoaderOSRS();
		varbitLoader = new VarbitLoaderOSRS();
		areaLoader = new RSAreaLoaderOSRS();
		
		MapIndexLoader.instance = mapIndexLoader;
		GraphicLoader.instance = graphicLoader;
		VariableBitLoader.instance = varbitLoader;
		FrameLoader.instance = frameLoader;
		ObjectDefinitionLoader.instance = objLoader;
		FloorDefinitionLoader.instance = floorLoader;
		FrameBaseLoader.instance = skeletonLoader;
		TextureLoader.instance = textureLoader;
		AnimationDefinitionLoader.instance = animDefLoader;
		RSAreaLoader.instance = areaLoader;
	}

	@Override
	public void onGameLoaded(Client client) {
		
			frameLoader.init(2500);
			
			Index configIndex = client.getCache().getFile(CacheFileType.CONFIG);

			floorLoader.initOverlays(configIndex.archive(4));
			floorLoader.initUnderlays(configIndex.archive(1));
			
			objLoader.init(configIndex.archive(6));
			animDefLoader.init(configIndex.archive(12));
			graphicLoader.init(configIndex.archive(13));
			varbitLoader.init(configIndex.archive(14));
			areaLoader.init(configIndex.archive(35));

			objLoader.renameMapFunctions(areaLoader);
			
			Index skeletonIndex = client.getCache().getFile(CacheFileType.SKELETON);
			skeletonLoader.init(skeletonIndex);
			
			Index mapIndex = client.getCache().getFile(CacheFileType.MAP);
			mapIndexLoader.init(mapIndex);
			
			Index textureIndex = client.getCache().getFile(CacheFileType.TEXTURE);
			Index spriteIndex = client.getCache().getFile(CacheFileType.SPRITE);
			textureLoader.init(textureIndex.archive(0), spriteIndex);
			

	}

	@Override
	public void onResourceDelivered(ResourceResponse arg0) {
		// TODO Auto-generated method stub
		
	}

}
