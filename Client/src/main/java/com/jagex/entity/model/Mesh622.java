package com.jagex.entity.model;

import com.jagex.io.Buffer;

public class Mesh622 extends Mesh {
	
	private boolean newFormat;

	public Mesh622(byte[] abyte0) {
        Buffer nc1 = new Buffer(abyte0);
        Buffer nc2 = new Buffer(abyte0);
        Buffer nc3 = new Buffer(abyte0);
        Buffer nc4 = new Buffer(abyte0);
        Buffer nc5 = new Buffer(abyte0);
        Buffer nc6 = new Buffer(abyte0);
        Buffer nc7 = new Buffer(abyte0);
        nc1.setPosition(abyte0.length - 23);
        int vertexCount = nc1.readUShort();
        int numTriangles = nc1.readUShort();
        int numTexTriangles = nc1.readUByte();
        
        
        int l1 = nc1.readUByte();
        boolean bool = (0x1 & l1 ^ 0xffffffff) == -2;
        boolean bool_26_ = (0x8 & l1) == 8;
        
        int newformat = 0;
        if (bool_26_) {
            nc1.setPosition(nc1.getPosition() - 7);
            newformat = nc1.readUByte();
            nc1.setPosition(nc1.getPosition() + 6);
        }
        if (newformat == 15)
        	this.newFormat = true;
        int i2 = nc1.readUByte();
        int j2 = nc1.readUByte();
        int k2 = nc1.readUByte();
        int l2 = nc1.readUByte();
        int i3 = nc1.readUByte();
        int j3 = nc1.readUShort();
        int k3 = nc1.readUShort();
        int l3 = nc1.readUShort();
        int i4 = nc1.readUShort();
        int j4 = nc1.readUShort();
        int k4 = 0;
        int l4 = 0;
        int i5 = 0;
        byte[] textureCoordinates = null;
        byte[] O = null;
        byte[] J = null;
        byte[] F = null;
        byte[] cb = null;
        byte[] gb = null;
        byte[] lb = null;
        int[] kb = null;
        int[] y = null;
        int[] N = null;
        int[] textureIds = null;
        int[] triangleColors2 = new int[numTriangles];
        if (numTexTriangles > 0) {
            O = new byte[numTexTriangles];
            nc1.setPosition(0);
            for (int j5 = 0; j5 < numTexTriangles; j5++) {
                byte byte0 = O[j5] = nc1.readByte();
                if (byte0 == 0)
                    k4++;
                if (byte0 >= 1 && byte0 <= 3)
                    l4++;
                if (byte0 == 2)
                    i5++;
            }
        }
        int k5 = numTexTriangles;
        int l5 = k5;
        k5 += vertexCount;
        int i6 = k5;
        if (bool)
            k5 += numTriangles;
        if (l1 == 1)
            k5 += numTriangles;
        int j6 = k5;
        k5 += numTriangles;
        int k6 = k5;
        if (i2 == 255)
            k5 += numTriangles;
        int l6 = k5;
        if (k2 == 1)
            k5 += numTriangles;
        int i7 = k5;
        if (i3 == 1)
            k5 += vertexCount;
        int j7 = k5;
        if (j2 == 1)
            k5 += numTriangles;
        int k7 = k5;
        k5 += i4;
        int l7 = k5;
        if (l2 == 1)
            k5 += numTriangles * 2;
        int i8 = k5;
        k5 += j4;
        int j8 = k5;
        k5 += numTriangles * 2;
        int k8 = k5;
        k5 += j3;
        int l8 = k5;
        k5 += k3;
        int i9 = k5;
        k5 += l3;
        int j9 = k5;
        k5 += k4 * 6;
        int k9 = k5;
        k5 += l4 * 6;
        int i_59_ = 6;
        if (newformat != 14) {
            if (newformat >= 15)
                i_59_ = 9;
        } else
            i_59_ = 7;
        int l9 = k5;
        k5 += i_59_ * l4;
        int i10 = k5;
        k5 += l4;
        int j10 = k5;
        k5 += l4;
        int k10 = k5;
        k5 += l4 + i5 * 2;
        int[] vertexX = new int[vertexCount];
        int[] vertexY = new int[vertexCount];
        int[] vertexZ = new int[vertexCount];
        int[] facePoint1 = new int[numTriangles];
        int[] facePoint2 = new int[numTriangles];
        int[] facePoint3 = new int[numTriangles];
        packedVertexGroups = new int[vertexCount];
        triangleInfo = new int[numTriangles];
        faceRenderPriorities = new byte[numTriangles];
        packedTransparencyVertexGroups = new int[numTriangles];
        if (i3 == 1)
            packedVertexGroups = new int[vertexCount];
        if (bool)
            triangleInfo = new int[numTriangles];
        if (i2 == 255)//Change this
            faceRenderPriorities = new byte[numTriangles];
        else {
        }
        if (j2 == 1)
            faceTransparencies = new int[numTriangles];
        if (k2 == 1)
            packedTransparencyVertexGroups = new int[numTriangles];
        if (l2 == 1)
            textureIds = new int[numTriangles];
        if (l2 == 1 && numTexTriangles > 0) {
            textureCoordinates = faceTexture = new byte[numTriangles];
        }
        triangleColors2 = new int[numTriangles];
        int[] texTrianglesPoint1 = null;
        int[] texTrianglesPoint2 = null;
        int[] texTrianglesPoint3 = null;
        if (numTexTriangles > 0) {
            texTrianglesPoint1 = new int[numTexTriangles];
            texTrianglesPoint2 = new int[numTexTriangles];
            texTrianglesPoint3 = new int[numTexTriangles];
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
        nc1.setPosition(l5);
        nc2.setPosition(k8);
        nc3.setPosition(l8);
        nc4.setPosition(i9);
        nc5.setPosition(i7);
        int l10 = 0;
        int i11 = 0;
        int j11 = 0;
        for (int k11 = 0; k11 < vertexCount; k11++) {
            int l11 = nc1.readUByte();
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
        nc1.setPosition(j8);
        nc2.setPosition(i6);
        nc3.setPosition(k6);
        nc4.setPosition(j7);
        nc5.setPosition(l6);
        nc6.setPosition(l7);
        nc7.setPosition(i8);
        for (int i12 = 0; i12 < numTriangles; i12++) {
            triangleColors2[i12] = nc1.readUShort();
            if (l1 == 1) {
                triangleInfo[i12] = nc2.readByte();
                if (triangleInfo[i12] == 2)
                    triangleColors2[i12] = 65535;
                triangleInfo[i12] = 0;
            }
            if (i2 == 255) {
                faceRenderPriorities[i12] = nc3.readByte();
            }
            if (j2 == 1) {
                faceTransparencies[i12] = nc4.readByte();
                if (faceTransparencies[i12] < 0)
                    faceTransparencies[i12] = (256 + faceTransparencies[i12]);
            }
            if (k2 == 1)
                packedTransparencyVertexGroups[i12] = nc5.readUByte();
            if (l2 == 1)
                textureIds[i12] = (short) (nc6.readUShort() - 1);
            if (textureCoordinates != null)
                if (textureIds[i12] != -1)
                    textureCoordinates[i12] = faceTexture[i12] = (byte) (nc7.readUByte() - 1);
                else
                    textureCoordinates[i12] = faceTexture[i12] = -1;
        }
        nc1.setPosition(k7);
        nc2.setPosition(j6);
        int k12 = 0;
        int i13 = 0;
        int k13 = 0;
        int l13 = 0;
        for (int i14 = 0; i14 < numTriangles; i14++) {
            int j14 = nc2.readUByte();
            if (j14 == 1) {
                k12 = nc1.readSmart() + l13;
                l13 = k12;
                i13 = nc1.readSmart() + l13;
                l13 = i13;
                k13 = nc1.readSmart() + l13;
                l13 = k13;
                facePoint1[i14] = k12;
                facePoint2[i14] = i13;
                facePoint3[i14] = k13;
            }
            if (j14 == 2) {
                i13 = k13;
                k13 = nc1.readSmart() + l13;
                l13 = k13;
                facePoint1[i14] = k12;
                facePoint2[i14] = i13;
                facePoint3[i14] = k13;
            }
            if (j14 == 3) {
                k12 = k13;
                k13 = nc1.readSmart() + l13;
                l13 = k13;
                facePoint1[i14] = k12;
                facePoint2[i14] = i13;
                facePoint3[i14] = k13;
            }
            if (j14 == 4) {
                int l14 = k12;
                k12 = i13;
                i13 = l14;
                k13 = nc1.readSmart() + l13;
                l13 = k13;
                facePoint1[i14] = k12;
                facePoint2[i14] = i13;
                facePoint3[i14] = k13;
            }
        }
        nc1.setPosition(j9);
        nc2.setPosition(k9);
        nc3.setPosition(l9);
        nc4.setPosition(i10);
        nc5.setPosition(j10);
        nc6.setPosition(k10);
        for (int k14 = 0; k14 < numTexTriangles; k14++) {
            int i15 = O[k14] & 0xff;
            if (i15 == 0) {
                texTrianglesPoint1[k14] = nc1.readUShort();
                texTrianglesPoint2[k14] = nc1.readUShort();
                texTrianglesPoint3[k14] = nc1.readUShort();
            }
            if (i15 == 1) {
                texTrianglesPoint1[k14] = nc2.readUShort();
                texTrianglesPoint2[k14] = nc2.readUShort();
                texTrianglesPoint3[k14] = nc2.readUShort();
                if (newformat < 15) {
                    kb[k14] = nc3.readUShort();
                    if (newformat >= 14)
                        N[k14] = nc3.readUTriByte();
                    else
                        N[k14] = nc3.readUShort();
                    y[k14] = nc3.readUShort();
                } else {
                    kb[k14] = nc3.readUTriByte();
                    N[k14] = nc3.readUTriByte();
                    y[k14] = nc3.readUTriByte();
                }
                gb[k14] = nc4.readByte();
                lb[k14] = nc5.readByte();
                F[k14] = nc6.readByte();
            }
            if (i15 == 2) {
                texTrianglesPoint1[k14] = nc2.readUShort();
                texTrianglesPoint2[k14] = nc2.readUShort();
                texTrianglesPoint3[k14] = nc2.readUShort();
                if (newformat >= 15) {
                    kb[k14] = nc3.readUTriByte();
                    N[k14] = nc3.readUTriByte();
                    y[k14] = nc3.readUTriByte();
                } else {
                    kb[k14] = nc3.readUShort();
                    if (newformat < 14)
                        N[k14] = nc3.readUShort();
                    else
                        N[k14] = nc3.readUTriByte();
                    y[k14] = nc3.readUShort();
                }
                gb[k14] = nc4.readByte();
                lb[k14] = nc5.readByte();
                F[k14] = nc6.readByte();
                cb[k14] = nc6.readByte();
                J[k14] = nc6.readByte();
            }
            if (i15 == 3) {
                texTrianglesPoint1[k14] = nc2.readUShort();
                texTrianglesPoint2[k14] = nc2.readUShort();
                texTrianglesPoint3[k14] = nc2.readUShort();
                if (newformat < 15) {
                    kb[k14] = nc3.readUShort();
                    if (newformat < 14)
                        N[k14] = nc3.readUShort();
                    else
                        N[k14] = nc3.readUTriByte();
                    y[k14] = nc3.readUShort();
                } else {
                    kb[k14] = nc3.readUTriByte();
                    N[k14] = nc3.readUTriByte();
                    y[k14] = nc3.readUTriByte();
                }
                gb[k14] = nc4.readByte();
                lb[k14] = nc5.readByte();
                F[k14] = nc6.readByte();
            }
        }
        if (i2 != 255) {
            for (int i12 = 0; i12 < numTriangles; i12++)
                faceRenderPriorities[i12] = (byte) i2;
        }
        this.triangleColors = triangleColors2;
        this.vertexCount = vertexCount;
        this.triangleCount = numTriangles;
        this.vertexX = vertexX;
        this.vertexY = vertexY;
        this.vertexZ = vertexZ;
        faceIndices1 = facePoint1;
        faceIndices2 = facePoint2;
        faceIndices3 = facePoint3;
        filterTriangles();
        scale2(4);
        convertTexturesTo317(textureIds, texTrianglesPoint1, texTrianglesPoint2, texTrianglesPoint3);
	}
}
