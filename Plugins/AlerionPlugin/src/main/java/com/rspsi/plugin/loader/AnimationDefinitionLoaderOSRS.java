//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rspsi.plugin.loader;

import com.jagex.cache.anim.Animation;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;

public class AnimationDefinitionLoaderOSRS extends AnimationDefinitionLoader {
    private int count;
    private Animation[] animations;

    public AnimationDefinitionLoaderOSRS() {
    }

    public void init(Archive archive) {
        Buffer buffer = new Buffer(archive.file("seq.dat"));
        this.count = buffer.readUShort();
        if (this.animations == null) {
            this.animations = new Animation[this.count];
        }

        for (int id = 0; id < this.count; ++id) {
            this.animations[id] = this.decode(buffer);
        }

    }

    public void init(byte[] data) {
        Buffer buffer = new Buffer(data);
        this.count = buffer.readUShort();
        if (this.animations == null) {
            this.animations = new Animation[this.count];
        }

        for (int id = 0; id < this.count; ++id) {
            this.animations[id] = this.decode(buffer);
        }

    }

    protected Animation decode(Buffer buffer) {
        Animation animation = new Animation();
        int lastOpcode = -1;
        while (true) {
            int opcode = buffer.readUByte();
            int[] interleaveOrder;
            if (opcode == 0) {
                if (animation.getFrameCount() == 0) {
                    animation.setFrameCount(1);
                    int[] primaryFrames = new int[]{-1};
                    int[] secondaryFrames = new int[]{-1};
                    interleaveOrder = new int[]{-1};
                    animation.setPrimaryFrames(primaryFrames);
                    animation.setSecondaryFrames(secondaryFrames);
                    animation.setDurations(interleaveOrder);
                }

                if (animation.getAnimatingPrecedence() == -1) {
                    animation.setAnimatingPrecedence(animation.getInterleaveOrder() == null ? 0 : 2);
                }

                if (animation.getWalkingPrecedence() == -1) {
                    animation.setWalkingPrecedence(animation.getInterleaveOrder() == null ? 0 : 2);
                }

                return animation;
            }

            if (opcode == 1) {
                int size = buffer.readUShort();

                int[] anIntArray353 = new int[size];
                int[] anIntArray354 = new int[size];
                int[] anIntArray355 = new int[size];
                for (int j = 0; j < size; j++) {
                    anIntArray353[j] = buffer.readInt();
                    anIntArray354[j] = -1;
                }
                for (int j = 0; j < size; j++)
                    anIntArray355[j] = buffer.readUByte();

                animation.setFrameCount(size);
                animation.setPrimaryFrames(anIntArray353);
                animation.setSecondaryFrames(anIntArray354);
                animation.setDurations(anIntArray355);
            } else if (opcode == 2) {
                animation.setLoopOffset(buffer.readUShort());
            } else if (opcode == 3) {
                int len = buffer.readUByte();
                interleaveOrder = new int[len + 1];

                for (int index = 0; index < len; ++index) {
                    interleaveOrder[index] = buffer.readUByte();
                }

                interleaveOrder[len] = 9999999;
                animation.setInterleaveOrder(interleaveOrder);
            } else if (opcode == 4)
                animation.setStretches(true);
            else if (opcode == 5) {
                animation.setPriority(buffer.readUByte());
            } else if (opcode == 6) {
                animation.setPlayerOffhand(buffer.readUShort());
            } else if (opcode == 7) {
                animation.setPlayerMainhand(buffer.readUShort());
            } else if (opcode == 8) {
                animation.setMaximumLoops(buffer.readUByte());
            } else if (opcode == 9) {
                animation.setAnimatingPrecedence(buffer.readUByte());
            } else if (opcode == 10) {
                animation.setWalkingPrecedence(buffer.readUByte());
            } else if (opcode == 11) {
                animation.setReplayMode(buffer.readUByte());
            } else if (opcode == 12) {
                buffer.readInt();
            } else {
                System.out.println("Unknown seq opcode " + opcode + " last " + lastOpcode);
            }
            lastOpcode = opcode;
        }
    }

    public int count() {
        return this.count;
    }

    public Animation forId(int id) {
        if (id < 0 || id > this.animations.length) {
            id = 0;
        }

        return this.animations[id];
    }
}
