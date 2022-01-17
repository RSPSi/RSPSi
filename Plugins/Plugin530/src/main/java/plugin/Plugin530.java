package plugin;

import com.jagex.Client;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.net.ResourceResponse;
import com.rspsi.cache.CacheFileType;
import com.rspsi.plugins.core.ClientPlugin;
import com.displee.cache.index.Index;
import plugin.loader.*;
import plugin.loader.texture.*;
public class Plugin530 implements ClientPlugin {

	//This is needed so the ServiceLoader impl will load them
	private void touchAdditionalClasses(Class... classes){
		for(Class clazz : classes)
			clazz.isArray();
	}

	private AnimationFrameLoader frameLoader;
	private FloorDefLoader floorLoader;
	private ObjectDefLoader objLoader;
	private AnimationDefLoader animDefLoader;
	private SpotAnimationLoader graphicLoader;
	private VarbitLoaderOSRS varbitLoader;
	private MapIndexLoaderOSRS mapIndexLoader;
	private TextureLoaderOSRS textureLoader;
	private AnimationSkinLoader skeletonLoader;
	
	@Override
	public void initializePlugin() {
		touchAdditionalClasses(MapSceneLoader.class, MapSceneLoader.MapScene.class, NewTexture.class, SpriteTextureOperation.class, TextureDefinition.class, TextureOperation.class);
		objLoader = new ObjectDefLoader();
		floorLoader = new FloorDefLoader();
		frameLoader = new AnimationFrameLoader();
		animDefLoader = new AnimationDefLoader();
		
		mapIndexLoader = new MapIndexLoaderOSRS();
		textureLoader = new TextureLoaderOSRS();
		skeletonLoader = new AnimationSkinLoader();
		graphicLoader = new SpotAnimationLoader();
		varbitLoader = new VarbitLoaderOSRS();
		
		MapIndexLoader.instance = mapIndexLoader;
		GraphicLoader.instance = graphicLoader;
		VariableBitLoader.instance = varbitLoader;
		FrameLoader.instance = frameLoader;
		ObjectDefinitionLoader.instance = objLoader;
		com.jagex.cache.loader.floor.FloorDefinitionLoader.instance = floorLoader;
		com.jagex.cache.loader.anim.FrameBaseLoader.instance = skeletonLoader;
		TextureLoader.instance = textureLoader;
		com.jagex.cache.loader.anim.AnimationDefinitionLoader.instance = animDefLoader;
	}

	@Override
	public void onGameLoaded(Client client) {
		
		frameLoader.init(2500);

		Index configIndex = client.getCache().getFile(CacheFileType.CONFIG);

		floorLoader.decodeUnderlays(configIndex.archive(1));
		floorLoader.decodeOverlays(configIndex.archive(4));
		varbitLoader.decodeVarbits(client.getCache().getIndexedFileSystem().index(22));
		objLoader.decodeObjects(client.getCache().getIndexedFileSystem().index(16));

//		animDefLoader.init(configIndex.getArchive(12));
//		graphicLoader.init(configIndex.getArchive(13));

		MapSceneLoader mapSceneLoader = new MapSceneLoader();
		mapSceneLoader.init(client, configIndex.archive(34), client.getCache().getFile(CacheFileType.SPRITE));

//		Index skeletonIndex = client.getCache().getFile(CacheFileType.SKELETON);
//		skeletonLoader.init(skeletonIndex);

		Index mapIndex = client.getCache().getFile(CacheFileType.MAP);
		mapIndexLoader.init(mapIndex);


		textureLoader.init(client.getCache().getIndexedFileSystem().index(9), client.getCache().getIndexedFileSystem().index(8));

	}

	@Override
	public void onResourceDelivered(ResourceResponse arg0) {
		// TODO Auto-generated method stub
		
	}

}
