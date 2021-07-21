package com.rspsi.jagex.entity.model;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.io.Buffer;

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
        numVertices = first.readUShort();
        numFaces = first.readUShort();
        numTextures = first.readUByte();
        int flags = first.readUByte();
        boolean bool = (~(0x1 & flags)) == -2;
        int facePriorityFlag = first.readUByte();
        int faceAlphaFlag = first.readUByte();
        int faceSkinFlag = first.readUByte();
        int faceTexturesFlag = first.readUByte();
        int var17 = first.readUByte();
        int var18 = first.readUShort();
        int var19 = first.readUShort();
        int var20 = first.readUShort();
        int var21 = first.readUShort();
        int var22 = first.readUShort();
        int var2 = 0;
        int l4 = 0;
        int i5 = 0;
        if (numTextures > 0) {
            textureRenderTypes = new byte[numTextures];
            first.setPosition(0);
            for (int j5 = 0; j5 < numTextures; j5++) {
                byte byte0 = textureRenderTypes[j5] = first.readByte();
                if (byte0 == 0)
                    var2++;
                if (byte0 >= 1 && byte0 <= 3)
                    l4++;
                if (byte0 == 2)
                    i5++;
            }
        }
        int k5 = numTextures;
        int l5 = k5;
        k5 += numVertices;
        int i6 = k5;
        if (flags == 1)
            k5 += numFaces;
        int j6 = k5;
        k5 += numFaces;
        int k6 = k5;
        if (facePriorityFlag == 255)
            k5 += numFaces;
        int l6 = k5;
        if (faceSkinFlag == 1)
            k5 += numFaces;
        int i7 = k5;
        if (var17 == 1)
            k5 += numVertices;
        int j7 = k5;
        if (faceAlphaFlag == 1)
            k5 += numFaces;
        int k7 = k5;
        k5 += var21;
        int l7 = k5;
        if (faceTexturesFlag == 1)
            k5 += numFaces * 2;
        int i8 = k5;
        k5 += var22;
        int j8 = k5;
        k5 += numFaces * 2;
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
        verticesX = new int[numVertices];
        verticesY = new int[numVertices];
        verticesZ = new int[numVertices];
        faceIndicesA = new int[numFaces];
        faceIndicesB = new int[numFaces];
        faceIndicesC = new int[numFaces];
        if (flags == 1) {
            this.faceTypes = new int[numFaces];
        }

        if (var17 == 1)
            vertexBones = new int[numVertices];
        if (bool)
            faceTypes = new int[numFaces];

        if (facePriorityFlag == 255)
            facePriorities = new int[numFaces];
        else
            facePriority = facePriorityFlag;

        if (faceAlphaFlag == 1)
            faceAlphas = new int[numFaces];
        if (faceSkinFlag == 1)
            faceSkin = new int[numFaces];
        if (faceTexturesFlag == 1) {
            faceTextures = new int[numFaces];
        }
        if (faceTexturesFlag == 1 && numTextures > 0) {
            this.textureCoordinates = new byte[numFaces];
        }
        faceColours = new int[numFaces];
        if (numTextures > 0) {

            textureMappingA = new int[numTextures];
            textureMappingB = new int[numTextures];
            textureMappingC = new int[numTextures];
            if (l4 > 0) {
                aShortArray2574 = new int[l4];
                aShortArray2575 = new int[l4];
                aShortArray2586 = new int[l4];
                aShortArray2577 = new int[l4];
                aByteArray2580 = new int[l4];
                aShortArray2578 = new int[l4];
            }
            if (i5 > 0) {
                texturePrimaryColours = new int[i5];
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
        for (int k11 = 0; k11 < numVertices; k11++) {
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
            verticesX[k11] = l10 + j12;
            verticesY[k11] = i11 + l12;
            verticesZ[k11] = j11 + j13;
            l10 = verticesX[k11];
            i11 = verticesY[k11];
            j11 = verticesZ[k11];
            if (vertexBones != null)
                vertexBones[k11] = nc5.readUByte();
        }
        first.setPosition(j8);
        nc2.setPosition(i6);
        nc3.setPosition(k6);
        nc4.setPosition(j7);
        nc5.setPosition(l6);
        nc6.setPosition(l7);
        nc7.setPosition(i8);
        for (int i12 = 0; i12 < numFaces; i12++) {
            faceColours[i12] = first.readUShort();
            if (flags == 1) {
                this.faceTypes[i12] = nc2.readUByte();
            }
            if (facePriorityFlag == 255) {
                facePriorities[i12] = nc3.readByte();
            }
            if (faceAlphaFlag == 1) {
                faceAlphas[i12] = nc4.readByte();
            }
            if (faceSkinFlag == 1)
                faceSkin[i12] = nc5.readUByte();
            if (faceTexturesFlag == 1) {
                faceTextures[i12] = (nc6.readUShort() - 1);

            }

            if (textureCoordinates != null)
                if (faceTextures[i12] != -1)
                    this.textureCoordinates[i12] = (byte) (nc7.readUByte() - 1);
                else
                    this.textureCoordinates[i12] = -1;
        }
        first.setPosition(k7);
        nc2.setPosition(j6);
        int k12 = 0;
        int i13 = 0;
        int k13 = 0;
        int l13 = 0;
        for (int i14 = 0; i14 < numFaces; i14++) {
            int j14 = nc2.readUByte();
            if (j14 == 1) {
                k12 = first.readSmart() + l13;
                l13 = k12;
                i13 = first.readSmart() + l13;
                l13 = i13;
                k13 = first.readSmart() + l13;
                l13 = k13;
                faceIndicesA[i14] = k12 & 0xFFFF;
                faceIndicesB[i14] = i13 & 0xFFFF;
                faceIndicesC[i14] = k13 & 0xFFFF;
            }
            if (j14 == 2) {
                i13 = k13;
                k13 = first.readSmart() + l13;
                l13 = k13;
                faceIndicesA[i14] = k12 & 0xFFFF;
                faceIndicesB[i14] = i13 & 0xFFFF;
                faceIndicesC[i14] = k13 & 0xFFFF;
            }
            if (j14 == 3) {
                k12 = k13;
                k13 = first.readSmart() + l13;
                l13 = k13;
                faceIndicesA[i14] = k12 & 0xFFFF;
                faceIndicesB[i14] = i13 & 0xFFFF;
                faceIndicesC[i14] = k13 & 0xFFFF;
            }
            if (j14 == 4) {
                int l14 = k12;
                k12 = i13;
                i13 = l14;
                k13 = first.readSmart() + l13;
                l13 = k13;
                faceIndicesA[i14] = k12 & 0xFFFF;
                faceIndicesB[i14] = i13 & 0xFFFF;
                faceIndicesC[i14] = k13 & 0xFFFF;
            }
        }
        first.setPosition(j9);
        nc2.setPosition(k9);
        nc3.setPosition(l9);
        nc4.setPosition(i10);
        nc5.setPosition(j10);
        nc6.setPosition(k10);
        for (int k14 = 0; k14 < numTextures; k14++) {
            int i15 = textureRenderTypes[k14] & 0xff;
            if (i15 == 0) {
                textureMappingA[k14] = first.readUShort();
                textureMappingB[k14] = first.readUShort();
                textureMappingC[k14] = first.readUShort();
            }
            if (i15 == 1) {
                textureMappingA[k14] = nc2.readUShort();
                textureMappingB[k14] = nc2.readUShort();
                textureMappingC[k14] = nc2.readUShort();

                aShortArray2574[k14] = nc3.readUShort();
                aShortArray2575[k14] = nc3.readUShort();
                aShortArray2586[k14] = nc3.readUShort();

                aShortArray2577[k14] = nc4.readUShort();
                aByteArray2580[k14] = nc5.readUShort();
                aShortArray2578[k14] = nc6.readUShort();
            }
            if (i15 == 2) {
                textureMappingA[k14] = nc2.readUShort();
                textureMappingB[k14] = nc2.readUShort();
                textureMappingC[k14] = nc2.readUShort();

                aShortArray2574[k14] = nc3.readUShort();
                aShortArray2575[k14] = nc3.readUShort();
                aShortArray2586[k14] = nc3.readUShort();
                aShortArray2577[k14] = nc4.readUShort();
                aByteArray2580[k14] = nc5.readByte();
                aShortArray2578[k14] = nc6.readUShort();
                texturePrimaryColours[k14] = nc6.readUShort();
            }
            if (i15 == 3) {
                textureMappingA[k14] = nc2.readUShort();
                textureMappingB[k14] = nc2.readUShort();
                textureMappingC[k14] = nc2.readUShort();
                aShortArray2574[k14] = nc3.readUShort();
                aShortArray2575[k14] = nc3.readUShort();
                aShortArray2586[k14] = nc3.readUShort();
                aShortArray2577[k14] = nc4.readUShort();
                aByteArray2580[k14] = nc5.readByte();
                aShortArray2578[k14] = nc6.readUShort();
            }
        }

        if (Client.getSingleton().getCache().getIndexedFileSystem().is317()) {
            //scale(32, 32, 32);
        }
        //convertTexturesTo317(textureIds, texTrianglesPoint1, texTrianglesPoint2, texTrianglesPoint3, false);
    }
}
