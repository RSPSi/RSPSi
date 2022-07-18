//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rspsi.plugin;

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
import com.jagex.io.Buffer;
import com.jagex.net.ResourceResponse;
import com.rspsi.plugin.loader.AnimationDefinitionLoaderOSRS;
import com.rspsi.plugin.loader.FloorDefinitionLoaderOSRS;
import com.rspsi.plugin.loader.FrameBaseLoaderOSRS;
import com.rspsi.plugin.loader.FrameLoaderOSRS;
import com.rspsi.plugin.loader.GraphicLoaderOSRS;
import com.rspsi.plugin.loader.MapIndexLoaderOSRS;
import com.rspsi.plugin.loader.ObjectDefinitionLoaderOSRS;
import com.rspsi.plugin.loader.TextureLoaderOSRS;
import com.rspsi.plugin.loader.VarbitLoaderOSRS;
import com.rspsi.plugins.core.ClientPlugin;
import com.displee.cache.index.archive.Archive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AlerionPlugin implements ClientPlugin {
    private FrameLoaderOSRS frameLoader;

    public AlerionPlugin() {
    }

    public void initializePlugin() {
        ObjectDefinitionLoader.instance = new ObjectDefinitionLoaderOSRS();
        FloorDefinitionLoader.instance = new FloorDefinitionLoaderOSRS();
        AnimationDefinitionLoader.instance = new AnimationDefinitionLoaderOSRS();
        MapIndexLoader.instance = new MapIndexLoaderOSRS();
        TextureLoader.instance = new TextureLoaderOSRS();
        this.frameLoader = new FrameLoaderOSRS();
        FrameLoader.instance = this.frameLoader;
        FrameBaseLoader.instance = new FrameBaseLoaderOSRS();
        GraphicLoader.instance = new GraphicLoaderOSRS();
        VariableBitLoader.instance = new VarbitLoaderOSRS();
    }

    public void onGameLoaded(Client client) {
        this.frameLoader.init(2500);
        final File cacheDir = new File(client.getCache().getIndexedFileSystem().getPath());
        Archive config = client.getCache().createArchive(2, "config");
        try {
            ObjectDefinitionLoader.instance.init(
                    new Buffer(Files.readAllBytes(new File(cacheDir,"data/loc.dat").toPath())),
                    new Buffer(Files.readAllBytes(new File(cacheDir,"data/loc.idx").toPath()))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ObjectDefinitionLoader.instance.init(config);
        try {
            FloorDefinitionLoader.instance.init(
                    Files.readAllBytes(new File(cacheDir,"data/flo.dat").toPath())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        //FloorDefinitionLoader.instance.init(config);
        AnimationDefinitionLoader.instance.init(config);
        GraphicLoader.instance.init(config);
        VariableBitLoader.instance.init(config);
        Archive version = client.getCache().createArchive(5, "update list");
        MapIndexLoader.instance.init(version);
        Archive textures = client.getCache().createArchive(6, "textures");
        TextureLoader.instance.init(textures);
    }

    public void onResourceDelivered(ResourceResponse arg0) {
    }
}
