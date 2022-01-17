package com.rspsi.plugin;

import com.displee.cache.index.Index;

import com.jagex.Client;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.cache.loader.config.RSAreaLoader;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.net.ResourceResponse;
import com.rspsi.cache.CacheFileType;
import com.rspsi.plugin.loader.AnimationDefLoader;
import com.rspsi.plugin.loader.FloorDefLoader;
import com.rspsi.plugin.loader.AnimationSkinLoader;
import com.rspsi.plugin.loader.AnimationFrameLoader;
import com.rspsi.plugin.loader.SpotAnimationLoader;
import com.rspsi.plugin.loader.MapIndexLoaderOSRS;
import com.rspsi.plugin.loader.ObjectDefLoader;
import com.rspsi.plugin.loader.RSAreaLoaderOSRS;
import com.rspsi.plugin.loader.TextureLoaderOSRS;
import com.rspsi.plugin.loader.VarbitLoaderOSRS;
import com.rspsi.plugins.core.ClientPlugin;

//For 718
public class Plugin718 implements ClientPlugin {

	private AnimationFrameLoader frameLoader;
	private FloorDefLoader floorLoader;
	private ObjectDefLoader objLoader;
	private AnimationDefLoader animDefLoader;
	private SpotAnimationLoader graphicLoader;
	private VarbitLoaderOSRS varbitLoader;
	private MapIndexLoaderOSRS mapIndexLoader;
	private TextureLoaderOSRS textureLoader;
	private AnimationSkinLoader skeletonLoader;
	private RSAreaLoaderOSRS areaLoader;
	
	@Override
	public void initializePlugin() {
		objLoader = new ObjectDefLoader();
		floorLoader = new FloorDefLoader();
		frameLoader = new AnimationFrameLoader();
		animDefLoader = new AnimationDefLoader();
		
		mapIndexLoader = new MapIndexLoaderOSRS();
		textureLoader = new TextureLoaderOSRS();
		skeletonLoader = new AnimationSkinLoader();
		graphicLoader = new SpotAnimationLoader();
		varbitLoader = new VarbitLoaderOSRS();
		areaLoader = new RSAreaLoaderOSRS();
		
		MapIndexLoader.instance = mapIndexLoader;
		GraphicLoader.instance = graphicLoader;
		VariableBitLoader.instance = varbitLoader;
		FrameLoader.instance = frameLoader;
		ObjectDefinitionLoader.instance = objLoader;
		com.jagex.cache.loader.floor.FloorDefinitionLoader.instance = floorLoader;
		com.jagex.cache.loader.anim.FrameBaseLoader.instance = skeletonLoader;
		TextureLoader.instance = textureLoader;
		com.jagex.cache.loader.anim.AnimationDefinitionLoader.instance = animDefLoader;
		RSAreaLoader.instance = areaLoader;
	}

	@Override
	public void onGameLoaded(Client client) {
		
		frameLoader.init(2500);

		Index configIndex = client.getCache().getFile(CacheFileType.CONFIG);

		floorLoader.decodeUnderlays(configIndex.archive(1));
		floorLoader.decodeOverlays(configIndex.archive(4));
		varbitLoader.decodeVarbits(client.getCache().getFile(CacheFileType.VARBIT));
		objLoader.decodeObjects(client.getCache().getIndexedFileSystem().index(16));

//		animDefLoader.init(configIndex.getArchive(12));
//		graphicLoader.init(configIndex.getArchive(13));

		areaLoader.init(configIndex.archive(35));

//		Index skeletonIndex = client.getCache().getFile(CacheFileType.SKELETON);
//		skeletonLoader.init(skeletonIndex);

		Index mapIndex = client.getCache().getFile(CacheFileType.MAP);
		mapIndexLoader.init(mapIndex);

	}

	@Override
	public void onResourceDelivered(ResourceResponse arg0) {
		// TODO Auto-generated method stub
		
	}

}
