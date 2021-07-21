//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin.loader;

import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.google.common.collect.Maps;
import com.rspsi.jagex.cache.anim.FrameBase;
import com.rspsi.jagex.cache.loader.anim.FrameBaseLoader;
import com.rspsi.jagex.io.Buffer;

import java.util.Map;

public class FrameBaseLoaderOSRS extends FrameBaseLoader {
    private Map<Integer, FrameBase> skeletons = Maps.newConcurrentMap();

    public FrameBaseLoaderOSRS() {
    }

    public FrameBase get(int id) {
        return (FrameBase)this.skeletons.get(id);
    }

    public FrameBase decode(Buffer buffer) {
        FrameBase base = new FrameBase();
        int count = buffer.readUShort();
        int[] transformationType = new int[count];
        int[][] vertexGroups = new int[count][];

        int label;
        for(label = 0; label < count; ++label) {
            transformationType[label] = buffer.readUShort();
        }

        for(label = 0; label < count; ++label) {
            vertexGroups[label] = new int[buffer.readUShort()];
        }

        for(label = 0; label < count; ++label) {
            for(int index = 0; index < vertexGroups[label].length; ++index) {
                vertexGroups[label][index] = buffer.readUShort();
            }
        }

        base.setCount(count);
        base.setTransformationType(transformationType);
        base.setVertexGroups(vertexGroups);
        return base;
    }

    public void init(Index skeletonIndex) {
        Archive[] var2 = skeletonIndex.archives();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Archive archive = var2[var4];
            if (archive != null && archive.containsData()) {
                FrameBase base = this.decode(new Buffer(archive.file(0).getData()));
                this.skeletons.put(archive.getId(), base);
            }
        }

    }
}
