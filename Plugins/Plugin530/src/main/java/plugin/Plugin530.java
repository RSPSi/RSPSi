package plugin;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.cache.loader.anim.FrameLoader;
import com.rspsi.jagex.cache.loader.anim.GraphicLoader;
import com.rspsi.jagex.cache.loader.config.VariableBitLoader;
import com.rspsi.jagex.cache.loader.map.MapIndexLoader;
import com.rspsi.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.rspsi.jagex.cache.loader.textures.TextureLoader;
import com.rspsi.jagex.net.ResourceResponse;
import com.rspsi.editor.cache.CacheFileType;
import com.rspsi.plugins.ClientPlugin;
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
		com.rspsi.jagex.cache.loader.floor.FloorDefinitionLoader.instance = floorLoader;
		com.rspsi.jagex.cache.loader.anim.FrameBaseLoader.instance = skeletonLoader;
		TextureLoader.instance = textureLoader;
		com.rspsi.jagex.cache.loader.anim.AnimationDefinitionLoader.instance = animDefLoader;
	}

	@Override
	public void onGameLoaded(Client client) {
		
		frameLoader.init(2500);

		Index configIndex = client.getCache().readFile(CacheFileType.CONFIG);

		floorLoader.decodeUnderlays(configIndex.archive(1));
		floorLoader.decodeOverlays(configIndex.archive(4));
		varbitLoader.decodeVarbits(client.getCache().getIndexedFileSystem().index(22));
		objLoader.decodeObjects(client.getCache().getIndexedFileSystem().index(16));

//		animDefLoader.init(configIndex.archive(12));
//		graphicLoader.init(configIndex.archive(13));

		MapSceneLoader mapSceneLoader = new MapSceneLoader();
		mapSceneLoader.init(client, configIndex.archive(34), client.getCache().readFile(CacheFileType.SPRITE));

//		Index skeletonIndex = client.getCache().readFile(CacheFileType.SKELETON);
//		skeletonLoader.init(skeletonIndex);

		Index mapIndex = client.getCache().readFile(CacheFileType.MAP);
		mapIndexLoader.init(mapIndex);


		textureLoader.init(client.getCache().getIndexedFileSystem().index(9), client.getCache().getIndexedFileSystem().index(8));

	}

	@Override
	public void onResourceDelivered(ResourceResponse arg0) {
		// TODO Auto-generated method stub
		
	}

}
