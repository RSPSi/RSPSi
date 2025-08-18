package com.jagex.entity.model;

import com.jagex.io.Buffer;

public class MeshOSRSType2 extends Mesh {

    public MeshOSRSType2(byte[] var1) {
        boolean var2 = false;
        boolean var3 = false;
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var4.setPosition(var1.length - 23);
        int var9 = var4.readUShort();//var9
        int var10 = var4.readUShort();//var10
        int var11 = var4.readUByte();//var11
        int var12 = var4.readUByte();//var12
        int var13 = var4.readUByte();
        int var14 = var4.readUByte();
        int var15 = var4.readUByte();
        int var16 = var4.readUByte();
        int var17 = var4.readUByte();
        int var18 = var4.readUShort();
        int var19 = var4.readUShort();
        int var20 = var4.readUShort();
        int var21 = var4.readUShort();
        int var22 = var4.readUShort();
        byte var23 = 0;
        int var47 = var23 + var9;
        int var25 = var47;
        var47 += var10;
        int var26 = var47;
        if (var13 == 255) {
            var47 += var10;
        }

        int var27 = var47;
        if (var15 == 1) {
            var47 += var10;
        }

        int var28 = var47;
        if (var12 == 1) {
            var47 += var10;
        }

        int var29 = var47;
        var47 += var22;
        int var30 = var47;
        if (var14 == 1) {
            var47 += var10;
        }

        int var31 = var47;
        var47 += var21;
        int var32 = var47;
        var47 += var10 * 2;
        int var33 = var47;
        var47 += var11 * 6;
        int var34 = var47;
        var47 += var18;
        int var35 = var47;
        var47 += var19;
        int var10000 = var47 + var20;

        vertexCount = var9;
        triangleCount = var10;
        numTextureFaces = var11;

        vertexX = new int[var9];
        vertexY = new int[var9];
        vertexZ = new int[var9];
        faceIndices1 = new int[var10];
        faceIndices2 = new int[var10];
        faceIndices3 = new int[var10];


        if (var11 > 0) {
            textureMap = new byte[var11];
            texIndices1 = new int[var11];
            texIndices2 = new int[var11];
            texIndices3 = new int[var11];
        }


        if (var16 == 1)
            packedVertexGroups = new int[var9];

        if (var12 == 1) {
            this.triangleInfo = new int[var10];
            this.faceTexture = new byte[var10];
            this.faceMaterial = new int[var10];
        }

        if (var13 == 255)
            faceRenderPriorities = new byte[var10];
        else {
            priority = (byte) var12;
        }

        if (var14 == 1)
            faceTransparencies = new int[var10];

        if (var15 == 1)
            packedTransparencyVertexGroups = new int[var10];

        if(var17 == 1){
            for (int var51 = 0; var51 < vertexCount; ++var51)
            {
                int subgroups = var6.readUByte();
                animayaGroups[var51] = new int[subgroups];
                animayaScales[var51] = new int[subgroups];

                for (int var53 = 0; var53 < subgroups; ++var53)
                {
                    animayaGroups[var51][var53] = var6.readUByte();
                    animayaScales[var51][var53] = var6.readUByte();
                }
            }
        }
        triangleColors = new int[var10];

        var4.setPosition(var23);
        var5.setPosition(var34);
        var6.setPosition(var35);
        var7.setPosition(var47);
        var8.setPosition(var29);

        int var37 = 0;
        int var38 = 0;
        int var39 = 0;

        int var40;
        int var41;
        int var42;
        int var43;
        int var44;
        for (var40 = 0; var40 < var9; ++var40) {
            var41 = var4.readUByte();
            var42 = 0;
            if ((var41 & 1) != 0) {
                var42 = var5.readSmart();
            }

            var43 = 0;
            if ((var41 & 2) != 0) {
                var43 = var6.readSmart();
            }

            var44 = 0;
            if ((var41 & 4) != 0) {
                var44 = var7.readSmart();
            }

            this.vertexX[var40] = var37 + var42;
            this.vertexY[var40] = var38 + var43;
            this.vertexZ[var40] = var39 + var44;
            var37 = this.vertexX[var40];
            var38 = this.vertexY[var40];
            var39 = this.vertexZ[var40];
            if (var16 == 1) {
                this.packedVertexGroups[var40] = var8.readUByte();
            }
        }
        if (var17 == 1) {
            for (var40 = 0; var40 < var9; ++var40) {
                var41 = var8.readUByte();

                for (var42 = 0; var42 < var41; ++var42) {
                    var8.skip(2);
                }
            }
        }

        var4.setPosition(var32);
        var5.setPosition(var28);
        var6.setPosition(var26);
        var7.setPosition(var30);
        var8.setPosition(var27);


        for (var40 = 0; var40 < var10; var40++) {
            triangleColors[var40] = var4.readUShort();
            if (var12 == 1) {
               var41 = var5.readUByte();
                if((var41 & 1) == 1) {
                    this.triangleInfo[var40] = 1;
                } else {
                    this.triangleInfo[var40] = 0;
                }

                if((var41 & 2) == 2) {
                    this.faceTexture[var40] = (byte)(var41 >> 2);
                    this.faceMaterial[var40] = this.triangleColors[var40];
                    this.triangleColors[var40] = 127;
                    if (this.faceMaterial[var40] != -1) {
                        var3 = true;
                    }
                } else {
                    this.faceTexture[var40] = (byte) -1;
                    this.faceMaterial[var40] = -1;
                }
            }
            if (var13 == 255) {
                faceRenderPriorities[var40] = var6.readByte();
            }
            if (var14 == 1) {
                faceTransparencies[var40] = var7.readByte();
                if (this.faceTransparencies[var40] < 0) {
                    this.faceTransparencies[var40] = (256 + this.faceTransparencies[var40]);
                }
            }
            if (var15 == 1)
                packedTransparencyVertexGroups[var40] = var8.readUByte();

        }


        var4.setPosition(var31);
        var5.setPosition(var25);

        var40 = 0;
        var41 = 0;
        var42 = 0;
        var43 = 0;

        int var45;
        int var46;
        for (var44 = 0; var44 < var10; ++var44) {
            var45 = var5.readUByte();
            if (var45 == 1) {
                var40 = var4.readSmart() + var43;
                var41 = var4.readSmart() + var40;
                var42 = var4.readSmart() + var41;
                var43 = var42;
                this.faceIndices1[var44] = var40;
                this.faceIndices2[var44] = var41;
                this.faceIndices3[var44] = var42;
            }

            if (var45 == 2) {
                var41 = var42;
                var42 = var4.readSmart() + var43;
                var43 = var42;
                this.faceIndices1[var44] = var40;
                this.faceIndices2[var44] = var41;
                this.faceIndices3[var44] = var42;
            }

            if (var45 == 3) {
                var40 = var42;
                var42 = var4.readSmart() + var43;
                var43 = var42;
                this.faceIndices1[var44] = var40;
                this.faceIndices2[var44] = var41;
                this.faceIndices3[var44] = var42;
            }

            if (var45 == 4) {
                var46 = var40;
                var40 = var41;
                var41 = var46;
                var42 = var4.readSmart() + var43;
                var43 = var42;
                this.faceIndices1[var44] = var40;
                this.faceIndices2[var44] = var46;
                this.faceIndices3[var44] = var42;
            }
        }

        var4.setPosition(var33);

        for (var44 = 0; var44 < var11; ++var44) {
            this.textureMap[var44] = 0;
            this.texIndices1[var44] = (short)var4.readUShort();
            this.texIndices2[var44] = (short)var4.readUShort();
            this.texIndices3[var44] = (short)var4.readUShort();
        }

        if (this.faceTexture != null) {
            boolean var48 = false;

            for (var45 = 0; var45 < var10; ++var45) {
                var46 = this.faceTexture[var45] & 255;
                if (var46 != 255) {
                    if (this.faceIndices1[var45] == (this.texIndices1[var46] & '\uffff') && this.faceIndices2[var45] == (this.texIndices2[var46] & '\uffff') && this.faceIndices3[var45] == (this.texIndices3[var46] & '\uffff')) {
                        this.faceTexture[var45] = -1;
                    } else {
                        var48 = true;
                    }
                }
            }

            if (!var48) {
                this.faceTexture = null;
            }
        }

        if (!var3) {
            this.faceMaterial = null;
        }

        if (!var2) {
            this.triangleInfo = null;
        }
    }
}
