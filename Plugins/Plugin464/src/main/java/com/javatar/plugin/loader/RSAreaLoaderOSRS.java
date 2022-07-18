//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin.loader;

import com.jagex.cache.def.RSArea;
import com.jagex.cache.loader.config.RSAreaLoader;
import com.jagex.io.Buffer;
import com.jagex.util.ByteBufferUtils;
import java.nio.ByteBuffer;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

public class RSAreaLoaderOSRS extends RSAreaLoader {
    private RSArea[] areas;

    public RSAreaLoaderOSRS() {
    }

    public RSArea forId(int id) {
        return id >= 0 && id < this.areas.length ? this.areas[id] : null;
    }

    public int count() {
        return this.areas.length;
    }

    public void init(Archive archive) {
        this.areas = new RSArea[highestId + 1];
        File[] var2 = archive.getFiles();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            File file = var2[var4];
            if (file != null && file.getData() != null) {
                RSArea area = this.decode(file.getId(), ByteBuffer.wrap(file.getData()));
                this.areas[file.getId()] = area;
            }
        }

    }

    private RSArea decode(int id, ByteBuffer buffer) {
        RSArea area = new RSArea(id);

        while(true) {
            while(true) {
                int opcode = buffer.get() & 255;
                if (opcode == 0) {
                    return area;
                }

                if (opcode == 1) {
                    area.setSpriteId(ByteBufferUtils.getSmartInt(buffer));
                } else if (opcode == 2) {
                    area.setAnInt1967(ByteBufferUtils.getSmartInt(buffer));
                } else if (opcode == 3) {
                    area.setName(ByteBufferUtils.getOSRSString(buffer));
                } else if (opcode == 4) {
                    area.setAnInt1959(ByteBufferUtils.getMedium(buffer));
                } else if (opcode == 5) {
                    ByteBufferUtils.getMedium(buffer);
                } else if (opcode == 6) {
                    area.setAnInt1968(buffer.get() & 255);
                } else {
                    int size;
                    if (opcode == 7) {
                        size = buffer.get() & 255;
                        if ((size & 1) == 0) {
                        }

                        if ((size & 2) == 2) {
                        }
                    } else if (opcode == 8) {
                        buffer.get();
                    } else if (opcode >= 10 && opcode <= 14) {
                        area.getAStringArray1969()[opcode - 10] = ByteBufferUtils.getOSRSString(buffer);
                    } else if (opcode != 15) {
                        if (opcode == 17) {
                            area.setAString1970(ByteBufferUtils.getOSRSString(buffer));
                        } else if (opcode == 18) {
                            ByteBufferUtils.getSmartInt(buffer);
                        } else if (opcode == 19) {
                            area.setAnInt1980(buffer.getShort() & '\uffff');
                        } else if (opcode == 21) {
                            buffer.getInt();
                        } else if (opcode == 22) {
                            buffer.getInt();
                        } else if (opcode == 23) {
                            buffer.get();
                            buffer.get();
                            buffer.get();
                        } else if (opcode == 24) {
                            buffer.getShort();
                            buffer.getShort();
                        } else if (opcode == 25) {
                            ByteBufferUtils.getSmartInt(buffer);
                        } else if (opcode == 28) {
                            buffer.get();
                        } else if (opcode == 29) {
                            buffer.get();
                        } else if (opcode == 30) {
                            buffer.get();
                        }
                    } else {
                        size = buffer.get() & 255;
                        int[] anIntArray1982 = new int[size * 2];

                        int size2;
                        for(size2 = 0; size2 < size * 2; ++size2) {
                            anIntArray1982[size2] = buffer.getShort();
                        }

                        buffer.getInt();
                        size2 = buffer.get() & 255;
                        int[] anIntArray1981 = new int[size2];

                        for(int i = 0; i < anIntArray1981.length; ++i) {
                            anIntArray1981[i] = buffer.getInt();
                        }

                        byte[] aByteArray1979 = new byte[size];

                        for(int i = 0; i < size; ++i) {
                            aByteArray1979[i] = buffer.get();
                        }

                        area.setAnIntArray1982(anIntArray1982);
                        area.setAnIntArray1981(anIntArray1981);
                        area.setAByteArray1979(aByteArray1979);
                    }
                }
            }
        }
    }

    public void init(Buffer data, Buffer indexBuffer) {
    }
}
