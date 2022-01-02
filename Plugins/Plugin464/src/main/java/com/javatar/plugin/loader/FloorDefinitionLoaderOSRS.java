//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin.loader;

import com.jagex.cache.def.Floor;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.floor.FloorType;
import java.nio.ByteBuffer;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

public class FloorDefinitionLoaderOSRS extends FloorDefinitionLoader {
    private Floor[] overlays;
    private Floor[] underlays;

    public FloorDefinitionLoaderOSRS() {
    }

    public void init(Archive archive) {
    }

    public void init(byte[] data) {
    }

    public void initOverlays(Archive archive) {
        this.overlays = new Floor[highestId + 1];
        File[] var2 = archive.getFiles();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            File file = var2[var4];
            if (file != null && file.getData() != null) {
                Floor floor = this.decodeOverlay(ByteBuffer.wrap(file.getData()));
                floor.generateHsl();
                this.overlays[file.getId()] = floor;
            }
        }

    }

    public void initUnderlays(Archive archive) {
        this.underlays = new Floor[highestId + 1];
        File[] var2 = archive.getFiles();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            File file = var2[var4];
            if (file != null && file.getData() != null) {
                Floor floor = this.decodeUnderlay(ByteBuffer.wrap(file.getData()));
                floor.generateHsl();
                this.underlays[file.getId()] = floor;
            }
        }

    }

    public Floor decodeUnderlay(ByteBuffer buffer) {
        Floor floor = new Floor();

        while(true) {
            int opcode = buffer.get();
            if (opcode == 0) {
                return floor;
            }

            if (opcode == 1) {
                int rgb = ((buffer.get() & 255) << 16) + ((buffer.get() & 255) << 8) + (buffer.get() & 255);
                floor.setRgb(rgb);
            } else {
                System.out.println("Error unrecognised underlay code: " + opcode);
            }
        }
    }

    public Floor decodeOverlay(ByteBuffer buffer) {
        Floor floor = new Floor();

        while(true) {
            int opcode = buffer.get();
            if (opcode == 0) {
                return floor;
            }

            int anotherRgb;
            if (opcode == 1) {
                anotherRgb = ((buffer.get() & 255) << 16) + ((buffer.get() & 255) << 8) + (buffer.get() & 255);
                floor.setRgb(anotherRgb);
            } else if (opcode == 2) {
                anotherRgb = buffer.get() & 255;
                floor.setTexture(anotherRgb);
            } else if (opcode == 5) {
                floor.setShadowed(false);
            } else if (opcode == 7) {
                anotherRgb = ((buffer.get() & 255) << 16) + ((buffer.get() & 255) << 8) + (buffer.get() & 255);
                floor.setAnotherRgb(anotherRgb);
            } else {
                System.out.println("Error unrecognised overlay code: " + opcode);
            }
        }
    }

    public Floor getFloor(int id, FloorType type) {
        return type == FloorType.OVERLAY ? this.overlays[id] : this.underlays[id];
    }

    public int getSize(FloorType type) {
        return type == FloorType.OVERLAY ? this.overlays.length : this.underlays.length;
    }

    public int count() {
        return 0;
    }

    public Floor forId(int arg0) {
        return null;
    }
}
