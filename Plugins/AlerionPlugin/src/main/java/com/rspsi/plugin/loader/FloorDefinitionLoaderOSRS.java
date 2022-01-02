//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rspsi.plugin.loader;

import com.jagex.Client;
import com.jagex.cache.def.Floor;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.floor.FloorType;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import com.displee.cache.index.archive.Archive;

public class FloorDefinitionLoaderOSRS extends FloorDefinitionLoader {
    private Floor[] overlays;
    private Floor[] underlays;

    public FloorDefinitionLoaderOSRS() {
    }

    public void init(Archive archive) {
        ByteBuffer buffer = ByteBuffer.wrap(archive.file("flo.dat").getData());
        int underlayAmount = buffer.getShort();
        System.out.println("Underlay Floors Loaded: " + underlayAmount);
        this.underlays = new Floor[underlayAmount];

        for(int i = 0; i < underlayAmount; ++i) {
            this.underlays[i] = this.decodeUnderlay(buffer);
            this.underlays[i].generateHsl();
        }

        int overlayAmount = buffer.getShort();
        System.out.println("Overlay Floors Loaded: " + overlayAmount);
        this.overlays = new Floor[overlayAmount];

        for(int i = 0; i < overlayAmount; ++i) {
            this.overlays[i] = this.decodeOverlay(buffer);
            this.overlays[i].generateHsl();
        }

    }

    public void init(byte[] data) {
        ByteBuffer flo = ByteBuffer.wrap(data);
        try {
            ByteBuffer flo2 = ByteBuffer.wrap(Files.readAllBytes(
                    new File(Client.getSingleton().getCache().getIndexedFileSystem().getPath(), "data/flo2.dat").toPath()
            ));
            int underlayAmount = flo.getShort();
            System.out.println("Underlay Floors Loaded: " + underlayAmount);
            this.underlays = new Floor[underlayAmount];

            for(int i = 0; i < underlayAmount; ++i) {
                this.underlays[i] = this.decodeUnderlay(flo);
                this.underlays[i].generateHsl();
            }

            int overlayAmount = flo2.getShort();
            System.out.println("Overlay Floors Loaded: " + overlayAmount);
            this.overlays = new Floor[overlayAmount];

            for(int i = 0; i < overlayAmount; ++i) {
                this.overlays[i] = this.decodeOverlay(flo2);
                this.overlays[i].generateHsl();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Floor decodeUnderlay(ByteBuffer buffer) {
        Floor floor = new Floor();

        floor.setRgb(((buffer.get() & 255) << 16) + ((buffer.get() & 255) << 8) + (buffer.get() & 255));

        /*while(true) {
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
        }*/
        return floor;
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
