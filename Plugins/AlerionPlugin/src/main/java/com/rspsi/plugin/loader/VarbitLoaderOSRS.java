//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rspsi.plugin.loader;

import com.jagex.cache.config.VariableBits;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;

public class VarbitLoaderOSRS extends VariableBitLoader {
    private int count;
    private VariableBits[] bits;

    public VarbitLoaderOSRS() {
    }

    public int count() {
        return this.count;
    }

    public VariableBits forId(int id) {
        return id >= 0 && id <= this.bits.length ? this.bits[id] : null;
    }

    public void init(Archive archive) {
        Buffer buffer = new Buffer(archive.file("varbit.dat"));
        this.count = buffer.readUShort();
        if (this.bits == null) {
            this.bits = new VariableBits[this.count];
        }

        for(int i = 0; i < this.count; ++i) {
            if (this.bits[i] == null) {
                this.bits[i] = new VariableBits();
            }

            this.bits[i] = this.decode(buffer);
        }

        if (buffer.getPosition() != buffer.getPayload().length) {
            System.out.println("varbit load mismatch");
        }

    }

    private VariableBits decode(Buffer buffer) {
        VariableBits bit = new VariableBits();
        int setting = buffer.readUShort();
        int low = buffer.readUByte();
        int high = buffer.readUByte();
        bit.setSetting(setting);
        bit.setHigh(high);
        bit.setLow(low);
        return bit;
    }

    public void init(Buffer arg0, Buffer arg1) {
    }
}
