//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin.loader;

import com.jagex.cache.graphics.Sprite;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.textures.SpriteTexture;
import com.jagex.draw.textures.Texture;
import com.jagex.io.Buffer;
import com.rspsi.misc.FixedHashMap;
import java.nio.ByteBuffer;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextureLoaderOSRS extends TextureLoader {
    private static final Logger log = LoggerFactory.getLogger(TextureLoaderOSRS.class);
    private Texture[] textures;
    private boolean[] transparent;
    private double brightness = 0.8D;
    private FixedHashMap<Integer, int[]> textureCache = new FixedHashMap(20);

    public TextureLoaderOSRS() {
    }

    public Texture forId(int arg0) {
        return arg0 >= 0 && arg0 < this.textures.length ? this.textures[arg0] : null;
    }

    public int[] getPixels(int textureId) {
        Texture texture = this.forId(textureId);
        if (texture == null) {
            log.info("Texture {} was not found!", textureId);
            return null;
        } else if (this.textureCache.contains(textureId)) {
            return (int[])this.textureCache.get(textureId);
        } else {
            int[] texels = new int[65536];
            texture.setBrightness(this.brightness);
            int y;
            int x;
            if (texture.getWidth() == 64) {
                for(y = 0; y < 128; ++y) {
                    for(x = 0; x < 128; ++x) {
                        texels[x + (y << 7)] = texture.getPixel((x >> 1) + (y >> 1 << 6));
                    }
                }
            } else {
                for(y = 0; y < 16384; ++y) {
                    texels[y] = texture.getPixel(y);
                }
            }

            for(y = 0; y < 16384; ++y) {
                texels[y] &= 16316671;
                x = texels[y];
                texels[16384 + y] = x - (x >>> 3) & 16316671;
                texels['耀' + y] = x - (x >>> 2) & 16316671;
                texels['쀀' + y] = x - (x >>> 2) - (x >>> 3) & 16316671;
            }

            this.textureCache.put(textureId, texels);
            return texels;
        }
    }

    public void init(Archive archive, Index spriteIndex) {
        this.textures = new Texture[highestId + 1];
        this.transparent = new boolean[highestId + 1];
        File[] var3 = archive.getFiles();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            File file = var3[var5];
            if (file != null && file.getData() != null) {
                log.info("Loading texture {}", file.getId());
                Buffer buffer = new Buffer(file.getData());
                buffer.skip(3);
                int count = buffer.readUByte();
                int[] texIds = new int[count];

                for(int i = 0; i < count; ++i) {
                    texIds[i] = buffer.readUShort();
                }

                Sprite sprite = Sprite.decode(ByteBuffer.wrap(spriteIndex.getArchive(texIds[0]).file(0)));
                if (sprite.getWidth() != 128 || sprite.getHeight() != 128) {
                    sprite.resize(128, 128);
                }

                Texture texture = new SpriteTexture(sprite);
                this.textures[file.getId()] = texture;
                this.transparent[file.getId()] = texture.supportsAlpha();
            }
        }

    }

    public boolean isTransparent(int arg0) {
        return arg0 >= 0 && arg0 < this.transparent.length ? this.transparent[arg0] : false;
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

    public void init(Archive archive) {
    }
}
