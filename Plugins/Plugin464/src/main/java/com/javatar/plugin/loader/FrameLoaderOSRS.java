//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin.loader;

import com.jagex.Client;
import com.jagex.cache.anim.Frame;
import com.jagex.cache.anim.FrameBase;
import com.jagex.cache.loader.anim.FrameBaseLoader;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.io.Buffer;
import com.rspsi.cache.CacheFileType;

public class FrameLoaderOSRS extends FrameLoader {
    private Frame[][] frames;

    public FrameLoaderOSRS() {
    }

    public void init(int size) {
        this.frames = new Frame[size][0];
    }

    protected Frame forId(int index) {
        try {
            String hexString;
            int fileId = Integer.parseInt((hexString = Integer.toHexString(index)).substring(0, hexString.length() - 4), 16);
            index = Integer.parseInt(hexString.substring(hexString.length() - 4), 16);
            if (this.frames[fileId].length == 0) {
                Client.getSingleton().getProvider().requestgetFile(CacheFileType.ANIMATION, fileId);
                return null;
            } else {
                return this.frames[fileId][index];
            }
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public void load(int file, byte[] data) {
        Buffer in = new Buffer(data);
        Buffer buffer = new Buffer(data);
        FrameBase base = FrameBaseLoader.instance.decode(buffer);
        int frameCount = buffer.readUShort();
        this.frames[file] = new Frame[frameCount * 3];
        int[] translationIndices = new int[500];
        int[] transformX = new int[500];
        int[] transformY = new int[500];
        int[] transformZ = new int[500];

        for(int frameIndex = 0; frameIndex < frameCount; ++frameIndex) {
            int id = in.readUShort();
            Frame frame = new Frame();
            this.frames[file][id] = frame;
            frame.setBase(base);
            int transformations = in.readUByte();
            int lastIndex = -1;
            int transformation = 0;

            for(int index = 0; index < transformations; ++index) {
                int attribute = buffer.readUByte();
                if (attribute > 0 && attribute > 0) {
                    int next;
                    if (base.getTransformationType(index) != 0) {
                        for(next = index - 1; next > lastIndex; --next) {
                            if (base.getTransformationType(next) == 0) {
                                translationIndices[transformation] = next;
                                transformX[transformation] = 0;
                                transformY[transformation] = 0;
                                transformZ[transformation] = 0;
                                ++transformation;
                                break;
                            }
                        }
                    }

                    translationIndices[transformation] = index;
                    next = base.getTransformationType(index) == 3 ? 128 : 0;
                    transformX[transformation] = (attribute & 1) != 0 ? buffer.readShort2() : next;
                    transformY[transformation] = (attribute & 2) != 0 ? buffer.readShort2() : next;
                    transformZ[transformation] = (attribute & 4) != 0 ? buffer.readShort2() : next;
                    lastIndex = index;
                    ++transformation;
                    if (base.getTransformationType(index) == 5) {
                        frame.setOpaque(false);
                    }
                }
            }

            frame.setTransformationCount(transformation);
            frame.setTransformationIndices(translationIndices);
            frame.setTransformX(transformX);
            frame.setTransformY(transformY);
            frame.setTransformZ(transformZ);
        }

    }
}
