package com.jagex.entity.model;

import com.jagex.io.Buffer;

public class MeshType3 extends Mesh {

    public MeshType3(byte[] var1) {
        Buffer var2 = new Buffer(var1);
        Buffer var3 = new Buffer(var1);
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var2.setPosition(var1.length - 26);
        int var9 = var2.readUShort();
        int var10 = var2.readUShort();
        int var11 = var2.readUByte();
        int var12 = var2.readUByte();
        int var13 = var2.readUByte();
        int var14 = var2.readUByte();
        int var15 = var2.readUByte();
        int var16 = var2.readUByte();
        int var17 = var2.readUByte();
        int var18 = var2.readUByte();
        int var19 = var2.readUShort();
        int var20 = var2.readUShort();
        int var21 = var2.readUShort();
        int var22 = var2.readUShort();
        int var23 = var2.readUShort();
        int var24 = var2.readUShort();
        int var25 = 0;
        int var26 = 0;
        int var27 = 0;
        int var28;


        if (var11 > 0) {
            this.textureMap = new byte[var11];
            var2.setPosition(0);

            for (var28 = 0; var28 < var11; ++var28) {
                byte var29 = this.textureMap[var28] = var2.readByte();
                if (var29 == 0) {
                    ++var25;
                }

                if (var29 >= 1 && var29 <= 3) {
                    ++var26;
                }

                if (var29 == 2) {
                    ++var27;
                }
            }
        }

        var28 = var11 + var9;
        int var58 = var28;
        if (var12 == 1) {
            var28 += var10;
        }

        int var30 = var28;
        var28 += var10;
        int var31 = var28;
        if (var13 == 255) {
            var28 += var10;
        }

        int var32 = var28;
        if (var15 == 1) {
            var28 += var10;
        }

        int var33 = var28;
        var28 += var24;
        int var34 = var28;
        if (var14 == 1) {
            var28 += var10;
        }

        int var35 = var28;
        var28 += var22;
        int var36 = var28;
        if (var16 == 1) {
            var28 += var10 * 2;
        }

        int var37 = var28;
        var28 += var23;
        int var38 = var28;
        var28 += var10 * 2;
        int var39 = var28;
        var28 += var19;
        int var40 = var28;
        var28 += var20;
        int var41 = var28;
        var28 += var21;
        int var42 = var28;
        var28 += var25 * 6;
        int var43 = var28;
        var28 += var26 * 6;
        int var44 = var28;
        var28 += var26 * 6;
        int var45 = var28;
        var28 += var26 * 2;
        int var46 = var28;
        var28 += var26;
        int var47 = var28;
        var28 = var28 + var26 * 2 + var27 * 2;


        this.vertexCount = var9;
        this.triangleCount = var10;
        this.numTextureFaces = var11;
        this.vertexX = new int[var9];
        this.vertexY = new int[var9];
        this.vertexZ = new int[var9];
        this.faceIndices1 = new int[var10];
        this.faceIndices2 = new int[var10];
        this.faceIndices3 = new int[var10];
        if (var17 == 1) {
            this.packedVertexGroups = new int[var9];
        }

        if (var12 == 1) {
            this.triangleInfo = new int[var10];
        }

        if (var13 == 255) {
            this.faceRenderPriorities = new byte[var10];
        } else {
            this.priority = (byte) var13;
        }

        if (var14 == 1) {
            this.faceTransparencies = new int[var10];
        }

        if (var15 == 1) {
            this.packedTransparencyVertexGroups = new int[var10];
        }

        if (var16 == 1) {
            this.faceMaterial = new int[var10];
        }

        if (var16 == 1 && var11 > 0) {
            this.faceTexture = new byte[var10];
        }

        if (var18 == 1) {
            this.animayaGroups = new int[var9][];
            this.animayaScales = new int[var9][];
        }

        this.triangleColors = new int[var10];
        if (var11 > 0) {
            this.texIndices1 = new int[var11];
            this.texIndices2 = new int[var11];
            this.texIndices3 = new int[var11];
        }

        var2.setPosition(var11);
        var3.setPosition(var39);
        var4.setPosition(var40);
        var5.setPosition(var41);
        var6.setPosition(var33);
        int var48 = 0;
        int var49 = 0;
        int var50 = 0;

        int var51;
        int var52;
        int var53;
        int var54;
        int var55;
        for (var51 = 0; var51 < var9; ++var51) {
            var52 = var2.readUByte();
            var53 = 0;
            if ((var52 & 1) != 0) {
                var53 = var3.readSmart();
            }

            var54 = 0;
            if ((var52 & 2) != 0) {
                var54 = var4.readSmart();
            }

            var55 = 0;
            if ((var52 & 4) != 0) {
                var55 = var5.readSmart();
            }

            this.vertexX[var51] = var48 + var53;
            this.vertexY[var51] = var49 + var54;
            this.vertexZ[var51] = var50 + var55;
            var48 = this.vertexX[var51];
            var49 = this.vertexY[var51];
            var50 = this.vertexZ[var51];
            if (var17 == 1) {
                this.packedVertexGroups[var51] = var6.readUByte();
            }
        }

        if (var18 == 1) {
            for (var51 = 0; var51 < var9; ++var51) {
                var52 = var6.readUByte();
                this.animayaGroups[var51] = new int[var52];
                this.animayaScales[var51] = new int[var52];

                for (var53 = 0; var53 < var52; ++var53) {
                    this.animayaGroups[var51][var53] = var6.readUByte();
                    this.animayaScales[var51][var53] = var6.readUByte();
                }
            }
        }

        var2.setPosition(var38);
        var3.setPosition(var58);
        var4.setPosition(var31);
        var5.setPosition(var34);
        var6.setPosition(var32);
        var7.setPosition(var36);
        var8.setPosition(var37);

        for (var51 = 0; var51 < var10; ++var51) {
            this.triangleColors[var51] = (short) var2.readUShort();
            if (var12 == 1) {
                this.triangleInfo[var51] = var3.readByte();
            }

            if (var13 == 255) {
                this.faceRenderPriorities[var51] = var4.readByte();
            }

            if (var14 == 1) {
                this.faceTransparencies[var51] = var5.readByte();
                // TODO might need this
                if (this.faceTransparencies[var51] < 0) {
                    this.faceTransparencies[var51] = (256 + this.faceTransparencies[var51]);
                }
            }

            if (var15 == 1) {
                this.packedTransparencyVertexGroups[var51] = var6.readUByte();
            }

            if (var16 == 1) {
                this.faceMaterial[var51] = (byte) (var7.readUShort() - 1);
            }

            if (this.faceTexture != null && this.faceMaterial[var51] != -1) {
                this.faceTexture[var51] = (byte) (var8.readUByte() - 1);
            }
        }

        var2.setPosition(var35);
        var3.setPosition(var30);
        var51 = 0;
        var52 = 0;
        var53 = 0;
        var54 = 0;

        int var56;
        for (var55 = 0; var55 < var10; ++var55) {
            var56 = var3.readUByte();
            if (var56 == 1) {
                var51 = var2.readSmart() + var54;
                var52 = var2.readSmart() + var51;
                var53 = var2.readSmart() + var52;
                var54 = var53;
                this.faceIndices1[var55] = var51;
                this.faceIndices2[var55] = var52;
                this.faceIndices3[var55] = var53;
            }

            if (var56 == 2) {
                var52 = var53;
                var53 = var2.readSmart() + var54;
                var54 = var53;
                this.faceIndices1[var55] = var51;
                this.faceIndices2[var55] = var52;
                this.faceIndices3[var55] = var53;
            }

            if (var56 == 3) {
                var51 = var53;
                var53 = var2.readSmart() + var54;
                var54 = var53;
                this.faceIndices1[var55] = var51;
                this.faceIndices2[var55] = var52;
                this.faceIndices3[var55] = var53;
            }

            if (var56 == 4) {
                int var57 = var51;
                var51 = var52;
                var52 = var57;
                var53 = var2.readSmart() + var54;
                var54 = var53;
                this.faceIndices1[var55] = var51;
                this.faceIndices2[var55] = var57;
                this.faceIndices3[var55] = var53;
            }
        }

        var2.setPosition(var42);
        var3.setPosition(var43);
        var4.setPosition(var44);
        var5.setPosition(var45);
        var6.setPosition(var46);
        var7.setPosition(var47);

        for (var55 = 0; var55 < var11; ++var55) {
            var56 = this.textureMap[var55] & 255;
            if (var56 == 0) {
                this.texIndices1[var55] = (short) var2.readUShort();
                this.texIndices2[var55] = (short) var2.readUShort();
                this.texIndices3[var55] = (short) var2.readUShort();
            }
        }

        var2.setPosition(var28);
        var55 = var2.readUByte();
        if (var55 != 0) {
            var2.readUShort();
            var2.readUShort();
            var2.readUShort();
            var2.readInt();
        }

        convertTexturesToOldFormat();
    }
}
