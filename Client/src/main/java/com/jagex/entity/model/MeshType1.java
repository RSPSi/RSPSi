package com.jagex.entity.model;

import com.jagex.io.Buffer;

public class MeshType1 extends Mesh {

    public MeshType1(byte[] var1) {
        Buffer var2 = new Buffer(var1);
        Buffer var3 = new Buffer(var1);
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var2.setPosition(var1.length - 23);
        int var9 = var2.readUShort();
        int var10 = var2.readUShort();
        int var11 = var2.readUByte();
        int var12 = var2.readUByte();
        int var13 = var2.readUByte();
        int var14 = var2.readUByte();
        int var15 = var2.readUByte();
        int var16 = var2.readUByte();
        int var17 = var2.readUByte();
        int var18 = var2.readUShort();
        int var19 = var2.readUShort();
        int var20 = var2.readUShort();
        int var21 = var2.readUShort();
        int var22 = var2.readUShort();
        int var23 = 0;
        int var24 = 0;
        int var25 = 0;
        int var26;
        if (var11 > 0) {
            this.textureMap = new byte[var11];
            var2.setPosition(0);

            for (var26 = 0; var26 < var11; ++var26) {
                byte var27 = this.textureMap[var26] = var2.readByte();
                if (var27 == 0) {
                    ++var23;
                }

                if (var27 >= 1 && var27 <= 3) {
                    ++var24;
                }

                if (var27 == 2) {
                    ++var25;
                }
            }
        }

        var26 = var11 + var9;
        int var56 = var26;
        if (var12 == 1) {
            var26 += var10;
        }

        int var28 = var26;
        var26 += var10;
        int var29 = var26;
        if (var13 == 255) {
            var26 += var10;
        }

        int var30 = var26;
        if (var15 == 1) {
            var26 += var10;
        }

        int var31 = var26;
        if (var17 == 1) {
            var26 += var9;
        }

        int var32 = var26;
        if (var14 == 1) {
            var26 += var10;
        }

        int var33 = var26;
        var26 += var21;
        int var34 = var26;
        if (var16 == 1) {
            var26 += var10 * 2;
        }

        int var35 = var26;
        var26 += var22;
        int var36 = var26;
        var26 += var10 * 2;
        int var37 = var26;
        var26 += var18;
        int var38 = var26;
        var26 += var19;
        int var39 = var26;
        var26 += var20;
        int var40 = var26;
        var26 += var23 * 6;
        int var41 = var26;
        var26 += var24 * 6;
        int var42 = var26;
        var26 += var24 * 6;
        int var43 = var26;
        var26 += var24 * 2;
        int var44 = var26;
        var26 += var24;
        int var45 = var26;
        var26 = var26 + var24 * 2 + var25 * 2;
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

        this.triangleColors = new int[var10];
        if (var11 > 0) {
            this.texIndices1 = new int[var11];
            this.texIndices2 = new int[var11];
            this.texIndices3 = new int[var11];
        }

        var2.setPosition(var11);
        var3.setPosition(var37);
        var4.setPosition(var38);
        var5.setPosition(var39);
        var6.setPosition(var31);
        int var46 = 0;
        int var47 = 0;
        int var48 = 0;

        int var49;
        int var50;
        int var51;
        int var52;
        int var53;
        for (var49 = 0; var49 < var9; ++var49) {
            var50 = var2.readUByte();
            var51 = 0;
            if ((var50 & 1) != 0) {
                var51 = var3.readSmart();
            }

            var52 = 0;
            if ((var50 & 2) != 0) {
                var52 = var4.readSmart();
            }

            var53 = 0;
            if ((var50 & 4) != 0) {
                var53 = var5.readSmart();
            }

            this.vertexX[var49] = var46 + var51;
            this.vertexY[var49] = var47 + var52;
            this.vertexZ[var49] = var48 + var53;
            var46 = this.vertexX[var49];
            var47 = this.vertexY[var49];
            var48 = this.vertexZ[var49];
            if (var17 == 1) {
                this.packedVertexGroups[var49] = var6.readUByte();
            }
        }

        var2.setPosition(var36);
        var3.setPosition(var56);
        var4.setPosition(var29);
        var5.setPosition(var32);
        var6.setPosition(var30);
        var7.setPosition(var34);
        var8.setPosition(var35);

        for (var49 = 0; var49 < var10; ++var49) {
            this.triangleColors[var49] = (short) var2.readUShort();
            if (var12 == 1) {
                this.triangleInfo[var49] = var3.readByte();
            }

            if (var13 == 255) {
                this.faceRenderPriorities[var49] = var4.readByte();
            }

            if (var14 == 1) {
                this.faceTransparencies[var49] = var5.readByte();
                // TODO might still need this
                if (this.faceTransparencies[var49] < 0) {
                    this.faceTransparencies[var49] = (256 + this.faceTransparencies[var40]);
                }
            }

            if (var15 == 1) {
                this.packedTransparencyVertexGroups[var49] = var6.readUByte();
            }

            if (var16 == 1) {
                this.faceMaterial[var49] = (byte) (var7.readUShort() - 1);
            }

            if (this.faceTexture != null && this.faceMaterial[var49] != -1) {
                this.faceTexture[var49] = (byte) (var8.readUByte() - 1);
            }
        }

        var2.setPosition(var33);
        var3.setPosition(var28);
        var49 = 0;
        var50 = 0;
        var51 = 0;
        var52 = 0;

        int var54;
        for (var53 = 0; var53 < var10; ++var53) {
            var54 = var3.readUByte();
            if (var54 == 1) {
                var49 = var2.readSmart() + var52;
                var50 = var2.readSmart() + var49;
                var51 = var2.readSmart() + var50;
                var52 = var51;
                this.faceIndices1[var53] = var49;
                this.faceIndices2[var53] = var50;
                this.faceIndices3[var53] = var51;
            }

            if (var54 == 2) {
                var50 = var51;
                var51 = var2.readSmart() + var52;
                var52 = var51;
                this.faceIndices1[var53] = var49;
                this.faceIndices2[var53] = var50;
                this.faceIndices3[var53] = var51;
            }

            if (var54 == 3) {
                var49 = var51;
                var51 = var2.readSmart() + var52;
                var52 = var51;
                this.faceIndices1[var53] = var49;
                this.faceIndices2[var53] = var50;
                this.faceIndices3[var53] = var51;
            }

            if (var54 == 4) {
                int var55 = var49;
                var49 = var50;
                var50 = var55;
                var51 = var2.readSmart() + var52;
                var52 = var51;
                this.faceIndices1[var53] = var49;
                this.faceIndices2[var53] = var55;
                this.faceIndices3[var53] = var51;
            }
        }

        var2.setPosition(var40);
        var3.setPosition(var41);
        var4.setPosition(var42);
        var5.setPosition(var43);
        var6.setPosition(var44);
        var7.setPosition(var45);

        for (var53 = 0; var53 < var11; ++var53) {
            var54 = this.textureMap[var53] & 255;
            if (var54 == 0) {
                this.texIndices1[var53] = (short) var2.readUShort();
                this.texIndices2[var53] = (short) var2.readUShort();
                this.texIndices3[var53] = (short) var2.readUShort();
            }
        }

        var2.setPosition(var26);
        var53 = var2.readUByte();
        if (var53 != 0) {
            var2.readUShort();
            var2.readUShort();
            var2.readUShort();
            var2.readInt();
        }

        convertTexturesToOldFormat();
    }
}
