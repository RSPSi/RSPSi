package com.jagex.entity.model;

import com.jagex.io.Buffer;

public class MeshType2 extends Mesh {

    public MeshType2(byte[] var1)
    {
        boolean var2 = false;
        boolean var3 = false;
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var4.setPosition(var1.length - 23);
        int var9 = var4.readUShort();
        int var10 = var4.readUShort();
        int var11 = var4.readUByte();
        int var12 = var4.readUByte();
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
        int var24 = var23 + var9;
        int var25 = var24;
        var24 += var10;
        int var26 = var24;
        if (var13 == 255)
        {
            var24 += var10;
        }

        int var27 = var24;
        if (var15 == 1)
        {
            var24 += var10;
        }

        int var28 = var24;
        if (var12 == 1)
        {
            var24 += var10;
        }

        int var29 = var24;
        var24 += var22;
        int var30 = var24;
        if (var14 == 1)
        {
            var24 += var10;
        }

        int var31 = var24;
        var24 += var21;
        int var32 = var24;
        var24 += var10 * 2;
        int var33 = var24;
        var24 += var11 * 6;
        int var34 = var24;
        var24 += var18;
        int var35 = var24;
        var24 += var19;
        int var10000 = var24 + var20;
        this.numVertices = var9;
        this.numFaces = var10;
        this.numTextures = var11;
        this.verticesX = new int[var9];
        this.verticesY = new int[var9];
        this.verticesZ = new int[var9];
        this.faceIndicesA = new int[var10];
        this.faceIndicesB = new int[var10];
        this.faceIndicesC = new int[var10];
        if (numTextures > 0)
        {
            this.textureRenderTypes = new byte[var11];
            this.textureMappingP = new int[var11];
            this.textureMappingM = new int[var11];
            this.textureMappingN = new int[var11];
        }

        if (var16 == 1)
        {
            this.vertexBones = new int[var9];
        }

        if (var12 == 1)
        {
            this.faceTypes = new int[var10];
            this.faceTextures = new int[var10];
            this.texture_coordinates = new byte[var10];
        }

        if (var13 == 255)
        {
            this.facePriorities = new int[var10];
        }
        else
        {
            this.facePriority = (byte) var13;
        }

        if (var14 == 1)
        {
            this.faceAlphas = new int[var10];
        }

        if (var15 == 1)
        {
            this.faceSkin = new int[var10];
        }

        if (var17 == 1)
        {
            this.animayaGroups = new int[var9][];
            this.animayaScales = new int[var9][];
        }

        this.faceColours = new int[var10];
        var4.setPosition(var23);
        var5.setPosition(var34);
        var6.setPosition(var35);
        var7.setPosition(var24);
        var8.setPosition(var29);
        int var37 = 0;
        int var38 = 0;
        int var39 = 0;

        int var40;
        int var41;
        int var42;
        int var43;
        int var44;
        for (var40 = 0; var40 < var9; ++var40)
        {
            var41 = var4.readUByte();
            var42 = 0;
            if ((var41 & 1) != 0)
            {
                var42 = var5.readSmart();
            }

            var43 = 0;
            if ((var41 & 2) != 0)
            {
                var43 = var6.readSmart();
            }

            var44 = 0;
            if ((var41 & 4) != 0)
            {
                var44 = var7.readSmart();
            }

            this.verticesX[var40] = var37 + var42;
            this.verticesY[var40] = var38 + var43;
            this.verticesZ[var40] = var39 + var44;
            var37 = this.verticesX[var40];
            var38 = this.verticesY[var40];
            var39 = this.verticesZ[var40];
            if (var16 == 1)
            {
                this.vertexBones[var40] = var8.readUByte();
            }
        }

        if (var17 == 1)
        {
            for (var40 = 0; var40 < var9; ++var40)
            {
                var41 = var8.readUByte();
                this.animayaGroups[var40] = new int[var41];
                this.animayaScales[var40] = new int[var41];

                for (var42 = 0; var42 < var41; ++var42)
                {
                    this.animayaGroups[var40][var42] = var8.readUByte();
                    this.animayaScales[var40][var42] = var8.readUByte();
                }
            }
        }

        var4.setPosition(var32);
        var5.setPosition(var28);
        var6.setPosition(var26);
        var7.setPosition(var30);
        var8.setPosition(var27);

        for (var40 = 0; var40 < var10; ++var40)
        {
            this.faceColours[var40] = (short) var4.readUShort();
            if (var12 == 1)
            {
                var41 = var5.readUByte();
                if ((var41 & 1) == 1)
                {
                    this.faceTypes[var40] = 1;
                    var2 = true;
                }
                else
                {
                    this.faceTypes[var40] = 0;
                }

                if ((var41 & 2) == 2)
                {
                    this.texture_coordinates[var40] = (byte) (var41 >> 2);
                    this.faceTextures[var40] = this.faceColours[var40];
                    this.faceColours[var40] = 127;
                    if (this.texture_coordinates[var40] != -1)
                    {
                        var3 = true;
                    }
                }
                else
                {
                    this.faceTextures[var40] = -1;
                    this.texture_coordinates[var40] = -1;
                }
            }

            if (var13 == 255)
            {
                this.facePriorities[var40] = var6.readByte();
            }

            if (var14 == 1)
            {
                this.faceAlphas[var40] = var7.readByte();
                if (this.faceAlphas[var40] < 0) {
                    this.faceAlphas[var40] = (256 + this.faceAlphas[var40]);
                }
            }

            if (var15 == 1)
            {
                this.faceSkin[var40] = var8.readUByte();
            }
        }

        var4.setPosition(var31);
        var5.setPosition(var25);
        var40 = 0;
        var41 = 0;
        var42 = 0;
        var43 = 0;

        int var45;
        int var46;
        for (var44 = 0; var44 < var10; ++var44)
        {
            var45 = var5.readUByte();
            if (var45 == 1)
            {
                var40 = var4.readSmart() + var43;
                var41 = var4.readSmart() + var40;
                var42 = var4.readSmart() + var41;
                var43 = var42;
                this.faceIndicesA[var44] = var40;
                this.faceIndicesB[var44] = var41;
                this.faceIndicesC[var44] = var42;
            }

            if (var45 == 2)
            {
                var41 = var42;
                var42 = var4.readSmart() + var43;
                var43 = var42;
                this.faceIndicesA[var44] = var40;
                this.faceIndicesB[var44] = var41;
                this.faceIndicesC[var44] = var42;
            }

            if (var45 == 3)
            {
                var40 = var42;
                var42 = var4.readSmart() + var43;
                var43 = var42;
                this.faceIndicesA[var44] = var40;
                this.faceIndicesB[var44] = var41;
                this.faceIndicesC[var44] = var42;
            }

            if (var45 == 4)
            {
                var46 = var40;
                var40 = var41;
                var41 = var46;
                var42 = var4.readSmart() + var43;
                var43 = var42;
                this.faceIndicesA[var44] = var40;
                this.faceIndicesB[var44] = var46;
                this.faceIndicesC[var44] = var42;
            }
        }

        var4.setPosition(var33);

        for (var44 = 0; var44 < var11; ++var44)
        {
            this.textureRenderTypes[var44] = 0;
            this.textureMappingP[var44] = (short) var4.readUShort();
            this.textureMappingM[var44] = (short) var4.readUShort();
            this.textureMappingN[var44] = (short) var4.readUShort();
        }

        if (this.texture_coordinates != null)
        {
            boolean var47 = false;

            for (var45 = 0; var45 < var10; ++var45)
            {
                var46 = this.texture_coordinates[var45] & 255;
                if (var46 != 255)
                {
                    if (this.faceIndicesA[var45] == (this.textureMappingP[var46] & '\uffff') && this.faceIndicesB[var45] == (this.textureMappingM[var46] & '\uffff') && this.faceIndicesC[var45] == (this.textureMappingN[var46] & '\uffff'))
                    {
                        this.texture_coordinates[var45] = -1;
                    }
                    else
                    {
                        var47 = true;
                    }
                }
            }

            if (!var47)
            {
                this.texture_coordinates = null;
            }
        }

        if (!var3)
        {
            this.texture_coordinates = null;
        }

        if (!var2)
        {
            this.faceTypes = null;
        }
    }
}
