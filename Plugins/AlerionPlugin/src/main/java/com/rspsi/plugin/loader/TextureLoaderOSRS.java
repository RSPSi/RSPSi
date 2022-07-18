//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rspsi.plugin.loader;

import com.jagex.cache.graphics.IndexedImage;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.textures.PalettedTexture;
import com.jagex.draw.textures.Texture;
import com.rspsi.core.misc.FixedHashMap;
import java.util.Arrays;
import com.displee.cache.index.archive.Archive;

public class TextureLoaderOSRS extends TextureLoader {
    private Texture[] textures = new Texture[50];
    private boolean[] transparent = new boolean[50];
    private double brightness = 0.8D;
    private FixedHashMap<Integer, int[]> textureCache = new FixedHashMap(20);

    public TextureLoaderOSRS() {
    }

    public Texture forId(int arg0) {
        return arg0 >= 0 && arg0 <= this.textures.length ? this.textures[arg0] : null;
    }

    public int[] getPixels(int textureId) {
        Texture texture = this.forId(textureId);
        if (texture == null) {
            return null;
        } else if (this.textureCache.contains(textureId)) {
            return (int[])this.textureCache.get(textureId);
        } else {
            int[] texels = new int[65536];
            texture.setBrightness(this.brightness);
            int l1;
            int k2;
            if (texture.getWidth() == 64) {
                for(l1 = 0; l1 < 128; ++l1) {
                    for(k2 = 0; k2 < 128; ++k2) {
                        texels[k2 + (l1 << 7)] = texture.getPixel((k2 >> 1) + (l1 >> 1 << 6));
                    }
                }
            } else {
                for(l1 = 0; l1 < 16384; ++l1) {
                    texels[l1] = texture.getPixel(l1);
                }
            }

            this.transparent[textureId] = false;

            for(l1 = 0; l1 < 16384; ++l1) {
                texels[l1] &= 16316671;
                k2 = texels[l1];
                if (k2 == 0) {
                    this.transparent[textureId] = true;
                }

                texels[16384 + l1] = k2 - (k2 >>> 3) & 16316671;
                texels[32768 + l1] = k2 - (k2 >>> 2) & 16316671;
                texels[49152 + l1] = k2 - (k2 >>> 2) - (k2 >>> 3) & 16316671;
            }

            this.textureCache.put(textureId, texels);
            return texels;
        }
    }

    public void init(Archive archive) {
        int maxId = 0;

        for(int j = 0; j < 100; ++j) {
            try {
                IndexedImage texture = new IndexedImage(archive, String.valueOf(j), 0);
                texture.resize();
                this.textures[j] = new PalettedTexture(texture);
                maxId = j;
            } catch (Exception var5) {
            }
        }

        this.textures = (Texture[])Arrays.copyOf(this.textures, maxId + 1);
        this.transparent = new boolean[maxId + 1];
    }

    public boolean isTransparent(int arg0) {
        return arg0 >= 0 && arg0 <= this.transparent.length ? this.transparent[arg0] : false;
    }

    public void setBrightness(double arg0) {
        this.textureCache.clear();
        this.brightness = arg0;
    }

    public int count() {
        return this.textures.length;
    }

    public void init(byte[] arg0) {
    }
}
