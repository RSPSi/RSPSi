//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin;

import com.displee.cache.index.Index;
import com.javatar.plugin.loader.*;
import com.rspsi.editor.cache.CacheFileType;
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
import com.rspsi.plugins.ClientPlugin;

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
        Index configIndex = client.getCache().readFile(CacheFileType.CONFIG);
        this.floorLoader.initOverlays(configIndex.archive(4));
        this.floorLoader.initUnderlays(configIndex.archive(1));
        this.objLoader.init(configIndex.archive(6));
        this.animDefLoader.init(configIndex.archive(12));
        this.graphicLoader.init(configIndex.archive(13));
        this.varbitLoader.init(configIndex.archive(14));
        //this.areaLoader.init(configIndex.archive(35));
        //this.objLoader.renameMapFunctions(this.areaLoader);
        Index skeletonIndex = client.getCache().readFile(CacheFileType.SKELETON);
        this.skeletonLoader.init(skeletonIndex);
        Index mapIndex = client.getCache().readFile(CacheFileType.MAP);
        this.mapIndexLoader.init(mapIndex);
        Index textureIndex = client.getCache().readFile(CacheFileType.TEXTURE);
        Index spriteIndex = client.getCache().readFile(CacheFileType.SPRITE);
        this.textureLoader.init(textureIndex.archive(0), spriteIndex);
    }

    public void onResourceDelivered(ResourceResponse arg0) {
    }
}
