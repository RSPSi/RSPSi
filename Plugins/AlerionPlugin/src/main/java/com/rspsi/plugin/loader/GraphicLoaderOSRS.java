//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rspsi.plugin.loader;

import com.jagex.cache.anim.Graphic;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;

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
        Buffer buffer = new Buffer(archive.file("spotanim.dat"));
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

        while(true) {
            int opcode = buffer.readUByte();
            if (opcode == 0) {
                return graphic;
            }

            if (opcode == 1) {
                graphic.setModel(buffer.readUShort());
            } else {
                int len;
                if (opcode == 2) {
                    len = buffer.readUShort();
                    if (len >= 0) {
                        graphic.setAnimation(AnimationDefinitionLoader.getAnimation(len));
                    }

                    graphic.setAnimationId(len);
                } else if (opcode == 4) {
                    graphic.setBreadthScale(buffer.readUShort());
                } else if (opcode == 5) {
                    graphic.setDepthScale(buffer.readUShort());
                } else if (opcode == 6) {
                    graphic.setOrientation(buffer.readUShort());
                } else if (opcode == 7) {
                    graphic.setAmbience(buffer.readUShort());
                } else if (opcode == 8) {
                    graphic.setModelShadow(buffer.readUShort());
                } else if (opcode == 40) {
                    len = buffer.readUByte();
                    int[] originalColours = new int[len];
                    int[] replacementColours = new int[len];

                    for(int i = 0; i < len; ++i) {
                        originalColours[i] = buffer.readUShort();
                        replacementColours[i] = buffer.readUShort();
                    }

                    graphic.setOriginalColours(originalColours);
                    graphic.setReplacementColours(replacementColours);
                } else {
                    System.out.println("Error unrecognised spotanim config code: " + opcode + " last: " + lastOpcode);
                }
            }

            lastOpcode = opcode;
        }
    }
}
