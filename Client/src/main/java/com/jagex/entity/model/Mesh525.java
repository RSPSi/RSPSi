package com.jagex.entity.model;

import com.jagex.io.Buffer;

public class Mesh525 extends Mesh {

    public Mesh525(byte[] abyte0) {
        Buffer first = new Buffer(abyte0);
        Buffer nc2 = new Buffer(abyte0);
        Buffer nc3 = new Buffer(abyte0);
        Buffer nc4 = new Buffer(abyte0);
        Buffer nc5 = new Buffer(abyte0);
        Buffer nc6 = new Buffer(abyte0);
        Buffer nc7 = new Buffer(abyte0);
        first.setPosition(abyte0.length - 23);
        vertexCount = first.readUShort();
        triangleCount = first.readUShort();
        numTextureFaces = first.readUByte();
        int flags = first.readUByte();
        boolean bool = (~(0x1 & flags)) == -2;
        int priorityFlag = first.readUByte();
        int faceAlphaFlag = first.readUByte();
        int packedTransparencyVertexGroupsFlag = first.readUByte();
        int faceMaterialFlag = first.readUByte();
        int var17 = first.readUByte();
        int var18 = first.readUShort();
        int var19 = first.readUShort();
        int var20 = first.readUShort();
        int var21 = first.readUShort();
        int var22 = first.readUShort();
        int var2 = 0;
        int l4 = 0;
        int i5 = 0;
        byte[] J = null;
        byte[] F = null;
        byte[] cb = null;
        byte[] gb = null;
        byte[] lb = null;
        int[] kb = null;
        int[] y = null;
        int[] N = null;
        triangleColors = new int[triangleCount];

        if (numTextureFaces > 0) {
            textureMap = new byte[numTextureFaces];
            first.setPosition(0);
            for (int j5 = 0; j5 < numTextureFaces; j5++) {
                byte byte0 = textureMap[j5] = first.readByte();
                if (byte0 == 0)
                    var2++;
                if (byte0 >= 1 && byte0 <= 3)
                    l4++;
                if (byte0 == 2)
                    i5++;
            }
        }
        int k5 = numTextureFaces;
        int l5 = k5;
        k5 += vertexCount;
        int i6 = k5;
        if (flags == 1)
            k5 += triangleCount;
        int j6 = k5;
        k5 += triangleCount;
        int k6 = k5;
        if (priorityFlag == 255)
            k5 += triangleCount;
        int l6 = k5;
        if (packedTransparencyVertexGroupsFlag == 1)
            k5 += triangleCount;
        int i7 = k5;
        if (var17 == 1)
            k5 += vertexCount;
        int j7 = k5;
        if (faceAlphaFlag == 1)
            k5 += triangleCount;
        int k7 = k5;
        k5 += var21;
        int l7 = k5;
        if (faceMaterialFlag == 1)
            k5 += triangleCount * 2;
        int i8 = k5;
        k5 += var22;
        int j8 = k5;
        k5 += triangleCount * 2;
        int k8 = k5;
        k5 += var18;
        int l8 = k5;
        k5 += var19;
        int i9 = k5;
        k5 += var20;
        int j9 = k5;
        k5 += var2 * 6;
        int k9 = k5;
        k5 += l4 * 6;
        int l9 = k5;
        k5 += l4 * 6;
        int i10 = k5;
        k5 += l4;
        int j10 = k5;
        k5 += l4;
        int k10 = k5;
        k5 += l4 + i5 * 2;
        vertexX = new int[vertexCount];
        vertexY = new int[vertexCount];
        vertexZ = new int[vertexCount];
        faceIndices1 = new int[triangleCount];
        faceIndices2 = new int[triangleCount];
        faceIndices3 = new int[triangleCount];
        if (flags == 1) {
            this.triangleInfo = new int[triangleCount];
        }

        if (var17 == 1)
            packedVertexGroups = new int[vertexCount];
        if (bool)
            triangleInfo = new int[triangleCount];
        if (priorityFlag == 255)//Change this
            faceRenderPriorities = new byte[triangleCount];
        else {
        }
        if (faceAlphaFlag == 1)
            faceTransparencies = new int[triangleCount];
        if (packedTransparencyVertexGroupsFlag == 1)
            packedTransparencyVertexGroups = new int[triangleCount];
        if (faceMaterialFlag == 1) {
            faceMaterial = new int[triangleCount];
        }
        if (faceMaterialFlag == 1 && numTextureFaces > 0) {
            this.faceTexture = new byte[triangleCount];
        }
        triangleColors = new int[triangleCount];
        if (numTextureFaces > 0) {

            texIndices1 = new int[numTextureFaces];
            texIndices2 = new int[numTextureFaces];
            texIndices3 = new int[numTextureFaces];
            if (l4 > 0) {
                kb = new int[l4];
                N = new int[l4];
                y = new int[l4];
                gb = new byte[l4];
                lb = new byte[l4];
                F = new byte[l4];
            }
            if (i5 > 0) {
                cb = new byte[i5];
                J = new byte[i5];
            }
        }
        first.setPosition(l5);
        nc2.setPosition(k8);
        nc3.setPosition(l8);
        nc4.setPosition(i9);
        nc5.setPosition(i7);
        int l10 = 0;
        int i11 = 0;
        int j11 = 0;
        for (int k11 = 0; k11 < vertexCount; k11++) {
            int l11 = first.readUByte();
            int j12 = 0;
            if ((l11 & 1) != 0)
                j12 = nc2.readSmart();
            int l12 = 0;
            if ((l11 & 2) != 0)
                l12 = nc3.readSmart();
            int j13 = 0;
            if ((l11 & 4) != 0)
                j13 = nc4.readSmart();
            vertexX[k11] = l10 + j12;
            vertexY[k11] = i11 + l12;
            vertexZ[k11] = j11 + j13;
            l10 = vertexX[k11];
            i11 = vertexY[k11];
            j11 = vertexZ[k11];
            if (packedVertexGroups != null)
                packedVertexGroups[k11] = nc5.readUByte();
        }
        first.setPosition(j8);
        nc2.setPosition(i6);
        nc3.setPosition(k6);
        nc4.setPosition(j7);
        nc5.setPosition(l6);
        nc6.setPosition(l7);
        nc7.setPosition(i8);
        for (int i12 = 0; i12 < triangleCount; i12++) {
            triangleColors[i12] = first.readUShort();
            if (flags == 1) {
                this.triangleInfo[i12] = nc2.readUByte();
            }
            if (priorityFlag == 255) {
                faceRenderPriorities[i12] = nc3.readByte();
            }
            if (faceAlphaFlag == 1) {
                faceTransparencies[i12] = nc4.readByte();
            }
            if (packedTransparencyVertexGroupsFlag == 1)
                packedTransparencyVertexGroups[i12] = nc5.readUByte();
            if (faceMaterialFlag == 1) {
                faceMaterial[i12] = (nc6.readUShort() - 1);

            }

            if (faceTexture != null)
                if (faceMaterial[i12] != -1)
                    this.faceTexture[i12] = (byte) (nc7.readUByte() - 1);
                else
                    this.faceTexture[i12] = -1;
        }
        first.setPosition(k7);
        nc2.setPosition(j6);
        int k12 = 0;
        int i13 = 0;
        int k13 = 0;
        int l13 = 0;
        for (int i14 = 0; i14 < triangleCount; i14++) {
            int j14 = nc2.readUByte();
            if (j14 == 1) {
                k12 = first.readSmart() + l13;
                l13 = k12;
                i13 = first.readSmart() + l13;
                l13 = i13;
                k13 = first.readSmart() + l13;
                l13 = k13;
                faceIndices1[i14] = k12 & 0xFFFF;
                faceIndices2[i14] = i13 & 0xFFFF;
                faceIndices3[i14] = k13 & 0xFFFF;
            }
            if (j14 == 2) {
                i13 = k13;
                k13 = first.readSmart() + l13;
                l13 = k13;
                faceIndices1[i14] = k12 & 0xFFFF;
                faceIndices2[i14] = i13 & 0xFFFF;
                faceIndices3[i14] = k13 & 0xFFFF;
            }
            if (j14 == 3) {
                k12 = k13;
                k13 = first.readSmart() + l13;
                l13 = k13;
                faceIndices1[i14] = k12 & 0xFFFF;
                faceIndices2[i14] = i13 & 0xFFFF;
                faceIndices3[i14] = k13 & 0xFFFF;
            }
            if (j14 == 4) {
                int l14 = k12;
                k12 = i13;
                i13 = l14;
                k13 = first.readSmart() + l13;
                l13 = k13;
                faceIndices1[i14] = k12 & 0xFFFF;
                faceIndices2[i14] = i13 & 0xFFFF;
                faceIndices3[i14] = k13 & 0xFFFF;
            }
        }
        first.setPosition(j9);
        nc2.setPosition(k9);
        nc3.setPosition(l9);
        nc4.setPosition(i10);
        nc5.setPosition(j10);
        nc6.setPosition(k10);
        for (int k14 = 0; k14 < numTextureFaces; k14++) {
            int i15 = textureMap[k14] & 0xff;
            if (i15 == 0) {
                texIndices1[k14] = first.readUShort();
                texIndices2[k14] = first.readUShort();
                texIndices3[k14] = first.readUShort();
            }
            if (i15 == 1) {
                texIndices1[k14] = nc2.readUShort();
                texIndices2[k14] = nc2.readUShort();
                texIndices3[k14] = nc2.readUShort();
                kb[k14] = nc3.readUShort();
                N[k14] = nc3.readUShort();
                y[k14] = nc3.readUShort();
                gb[k14] = nc4.readByte();
                lb[k14] = nc5.readByte();
                F[k14] = nc6.readByte();
            }
            if (i15 == 2) {
                texIndices1[k14] = nc2.readUShort();
                texIndices2[k14] = nc2.readUShort();
                texIndices3[k14] = nc2.readUShort();
                kb[k14] = nc3.readUShort();
                N[k14] = nc3.readUShort();
                y[k14] = nc3.readUShort();
                gb[k14] = nc4.readByte();
                lb[k14] = nc5.readByte();
                F[k14] = nc6.readByte();
                cb[k14] = nc6.readByte();
                J[k14] = nc6.readByte();
            }
            if (i15 == 3) {
                texIndices1[k14] = nc2.readUShort();
                texIndices2[k14] = nc2.readUShort();
                texIndices3[k14] = nc2.readUShort();
                kb[k14] = nc3.readUShort();
                N[k14] = nc3.readUShort();
                y[k14] = nc3.readUShort();
                gb[k14] = nc4.readByte();
                lb[k14] = nc5.readByte();
                F[k14] = nc6.readByte();
            }
        }
        //convertTexturesTo317(textureIds, texTrianglesPoint1, texTrianglesPoint2, texTrianglesPoint3, false);
    }
}
