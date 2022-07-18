//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin;

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
import com.javatar.plugin.loader.*;
import com.rspsi.cache.CacheFileType;
import com.rspsi.plugins.core.ClientPlugin;
import com.displee.cache.index.Index;

public class Plugin464 implements ClientPlugin {
    private FrameLoaderOSRS frameLoader;
    private FloorDefinitionLoaderOSRS floorLoader;
    private ObjectDefinitionLoaderOSRS objLoader;
    private AnimationDefinitionLoaderOSRS animDefLoader;
    private GraphicLoaderOSRS graphicLoader;
    private VarbitLoaderOSRS varbitLoader;
    private MapIndexLoaderOSRS mapIndexLoader;
    private TextureLoaderOSRS textureLoader;
    private FrameBaseLoaderOSRS skeletonLoader;

    public Plugin464() {
    }

    public void initializePlugin() {
        this.objLoader = new ObjectDefinitionLoaderOSRS();
        this.floorLoader = new FloorDefinitionLoaderOSRS();
        this.frameLoader = new FrameLoaderOSRS();
        this.animDefLoader = new AnimationDefinitionLoaderOSRS();
        this.mapIndexLoader = new MapIndexLoaderOSRS();
        this.textureLoader = new TextureLoaderOSRS();
        this.skeletonLoader = new FrameBaseLoaderOSRS();
        this.graphicLoader = new GraphicLoaderOSRS();
        this.varbitLoader = new VarbitLoaderOSRS();
        //this.areaLoader = new RSAreaLoaderOSRS();
        MapIndexLoader.instance = this.mapIndexLoader;
        GraphicLoader.instance = this.graphicLoader;
        VariableBitLoader.instance = this.varbitLoader;
        FrameLoader.instance = this.frameLoader;
        ObjectDefinitionLoader.instance = this.objLoader;
        FloorDefinitionLoader.instance = this.floorLoader;
        FrameBaseLoader.instance = this.skeletonLoader;
        TextureLoader.instance = this.textureLoader;
        AnimationDefinitionLoader.instance = this.animDefLoader;
        //RSAreaLoader.instance = this.areaLoader;
    }

    public void onGameLoaded(Client client) {
        this.frameLoader.init(2500);
        Index configIndex = client.getCache().getFile(CacheFileType.CONFIG);
        this.floorLoader.initOverlays(configIndex.getArchive(4));
        this.floorLoader.initUnderlays(configIndex.getArchive(1));
        this.objLoader.init(configIndex.getArchive(6));
        this.animDefLoader.init(configIndex.getArchive(12));
        this.graphicLoader.init(configIndex.getArchive(13));
        this.varbitLoader.init(configIndex.getArchive(14));
        //this.areaLoader.init(configIndex.getArchive(35));
        //this.objLoader.renameMapFunctions(this.areaLoader);
        Index skeletonIndex = client.getCache().getFile(CacheFileType.SKELETON);
        this.skeletonLoader.init(skeletonIndex);
        Index mapIndex = client.getCache().getFile(CacheFileType.MAP);
        this.mapIndexLoader.init(mapIndex);
        Index textureIndex = client.getCache().getFile(CacheFileType.TEXTURE);
        Index spriteIndex = client.getCache().getFile(CacheFileType.SPRITE);
        this.textureLoader.init(textureIndex.getArchive(0), spriteIndex);
    }

    public void onResourceDelivered(ResourceResponse arg0) {
    }
}
