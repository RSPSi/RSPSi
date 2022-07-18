//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin.loader;

import com.jagex.cache.anim.Graphic;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

public class GraphicLoaderOSRS extends GraphicLoader {
    private Graphic[] graphics;
    private int count;

    public GraphicLoaderOSRS() {
    }

    public int count() {
        return this.count;
    }

    public Graphic forId(int id) {
        return id >= 0 && id <= this.count ? this.graphics[id] : null;
    }

    public void init(Archive archive) {
        this.graphics = new Graphic[highestId + 1];
        File[] var2 = archive.getFiles();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            File file = var2[var4];

            try {
                this.graphics[file.getId()] = this.decode(new Buffer(file.getData()));
                this.graphics[file.getId()].setId(file.getId());
            } catch (Exception var7) {
            }
        }

    }

    public void init(byte[] data) {
        Buffer buffer = new Buffer(data);
        this.count = buffer.readUShort();
        if (this.graphics == null) {
            this.graphics = new Graphic[this.count];
        }

        for(int id = 0; id < this.count; ++id) {
            try {
                this.graphics[id] = this.decode(buffer);
                this.graphics[id].setId(id);
            } catch (Exception var5) {
            }
        }

    }

    public Graphic decode(Buffer buffer) {
        Graphic graphic = new Graphic();
        int lastOpcode = -1;
        graphic.setOriginalColours(new int[6]);
        graphic.setReplacementColours(new int[6]);
        while(true) {
            int opcode = buffer.readUByte();
            if (opcode == 0) {
                return graphic;
            }
            if (opcode == 1) {
                graphic.setModel(buffer.readUShort());
            } else if (opcode == 2) {
                final int animId = buffer.readUShort();
                if(animId >= 0) {
                    graphic.setAnimation(AnimationDefinitionLoader.getAnimation(animId));
                }
                graphic.setAnimationId(animId);
            } else if (opcode == 4) {
                graphic.setBreadthScale(buffer.readUShort());
            } else if (opcode == 5) {
                graphic.setDepthScale(buffer.readUShort());
            } else if (opcode == 6) {
                graphic.setOrientation(buffer.readUShort());
            } else if (opcode == 7) {
                graphic.setAmbience(buffer.readUByte());
            } else if (opcode == 8) {
                graphic.setModelShadow(buffer.readUByte());
            } else if (opcode >= 40 && opcode < 50) {
                graphic.getOriginalColours()[opcode - 40] = buffer.readUShort();
            } else if (opcode >= 50 && opcode < 60) {
                graphic.getReplacementColours()[opcode - 50] = buffer.readUShort();
            } else {
                System.out.println("Unknown graphic opcode " + opcode + " last " + lastOpcode);
            }
            lastOpcode = opcode;
        }
    }
}
