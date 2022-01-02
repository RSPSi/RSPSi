//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin.loader;

import com.jagex.cache.config.VariableBits;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

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
        this.count = highestId + 1;
        this.bits = new VariableBits[this.count];
        File[] var2 = archive.getFiles();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            File file = var2[var4];
            if (file != null && file.getData() != null) {
                this.bits[file.getId()] = this.decode(new Buffer(file.getData()));
            }
        }

    }

    private VariableBits decode(Buffer buffer) {
        VariableBits bit = new VariableBits();

        while(true) {
            int opcode = buffer.readUByte();
            if (opcode == 0) {
                return bit;
            }

            if (opcode == 1) {
                int setting = buffer.readUShort();
                int low = buffer.readUByte();
                int high = buffer.readUByte();
                bit.setSetting(setting);
                bit.setHigh(high);
                bit.setLow(low);
            }
        }
    }

    public void init(Buffer arg0, Buffer arg1) {
    }
}
