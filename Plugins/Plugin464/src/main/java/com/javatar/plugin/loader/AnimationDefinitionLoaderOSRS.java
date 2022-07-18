//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin.loader;

import com.jagex.cache.anim.Animation;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

public class AnimationDefinitionLoaderOSRS extends AnimationDefinitionLoader {
    private int count;
    private Animation[] animations;

    public AnimationDefinitionLoaderOSRS() {
    }

    public void init(Archive archive) {
        this.animations = new Animation[highestId + 1];
        File[] var2 = archive.getFiles();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            File file = var2[var4];
            if (file != null && file.getData() != null) {
                try {
                    this.animations[file.getId()] = this.decode(new Buffer(file.getData()));
                } catch (Exception e) {
                    System.err.println("Animation " + file.getId());
                    e.printStackTrace();
                }
            }
        }

    }

    public void init(byte[] data) {
        Buffer buffer = new Buffer(data);
        this.count = buffer.readUShort();
        if (this.animations == null) {
            this.animations = new Animation[this.count];
        }

        for (int id = 0; id < this.count; ++id) {
            try {
                this.animations[id] = this.decode(buffer);
            } catch (Exception e) {
                System.err.println("Animation " + id);
                e.printStackTrace();
            }
        }

    }

    protected Animation decode(Buffer buffer) {
        Animation animation = new Animation();
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
                int[] frameLengths = new int[size];
                int[] frameIDs = new int[size];
                int[] secondaryFrames = new int[size];

                for (int i = 0; i < size; i++) {
                    frameLengths[i] = buffer.readUShort();
                }
                for (int i = 0; i < size; i++) {
                    frameIDs[i] = buffer.readUShort();
                    secondaryFrames[i] = -1;
                }
                for (int i = 0; i < size; i++) {
                    frameIDs[i] = (buffer.readUShort() << 16) + frameIDs[i];
                }
                animation.setFrameCount(size);
                animation.setPrimaryFrames(frameIDs);
                animation.setSecondaryFrames(secondaryFrames);
                animation.setDurations(frameLengths);
            } else if (opcode == 2) {
                animation.setLoopOffset(buffer.readUShort());
            } else if (opcode == 3) {
                int size = buffer.readUByte();
                int[] interleave = new int[size + 1];
                for (int i = 0; i < size; i++) {
                    interleave[i] = buffer.readUByte();
                }
                interleave[size] = 9999999;
                animation.setInterleaveOrder(interleave);
            } else if (opcode == 4) {
                animation.setStretches(true);
            } else if (opcode == 5) {
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
                int size = buffer.readUByte();
                for (int i = 0; i < size; i++) {
                    buffer.readUShort();
                }
                for (int i = 0; i < size; i++) {
                    buffer.readUShort();
                }
            } else if (opcode == 13) {
                int size = buffer.readUByte();
                for (int i = 0; i < size; i++) {
                    buffer.skip(3);
                }
            }
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
