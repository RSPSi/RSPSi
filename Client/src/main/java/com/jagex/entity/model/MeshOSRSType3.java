package com.jagex.entity.model;

import com.jagex.io.Buffer;

public class MeshOSRSType3 extends Mesh {

	public MeshOSRSType3(byte[] abyte0) {
		Buffer var2 = new Buffer(abyte0);
		Buffer var3 = new Buffer(abyte0);
		Buffer var4 = new Buffer(abyte0);
		Buffer var5 = new Buffer(abyte0);
		Buffer var6 = new Buffer(abyte0);
		Buffer var7 = new Buffer(abyte0);
		Buffer var8 = new Buffer(abyte0);
		var2.setPosition(abyte0.length - 26);
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
			textureRenderTypes = new byte[var11];
			var2.setPosition(0);
			for (int j5 = 0; j5 < var11; j5++) {
				byte var29 = textureRenderTypes[j5] = var2.readByte();
				if (var29 == 0)
					var25++;
				if (var29 >= 1 && var29 <= 3)
					var26++;
				if (var29 == 2)
					var27++;
			}
		}

		var28 = var11 + var9;
		int var30 = var28;
		if (var12 == 1) {
			var28 += var10;
		}

		int var31 = var28;
		var28 += var10;
		int var32 = var28;
		if (var13 == 255) {
			var28 += var10;
		}

		int var33 = var28;
		if (var15 == 1) {
			var28 += var10;
		}

		int var34 = var28;
		var28 += var24;
		int var35 = var28;
		if (var14 == 1) {
			var28 += var10;
		}

		int var36 = var28;
		var28 += var22;
		int var37 = var28;
		if (var16 == 1) {
			var28 += var10 * 2;
		}

		int var38 = var28;
		var28 += var23;
		int var39 = var28;
		var28 += var10 * 2;
		int var40 = var28;
		var28 += var19;
		int var41 = var28;
		var28 += var20;
		int var42 = var28;
		var28 += var21;
		int var43 = var28;
		var28 += var25 * 6;
		int var44 = var28;
		var28 += var26 * 6;
		int var45 = var28;
		var28 += var26 * 6;
		int var46 = var28;
		var28 += var26 * 2;
		int var47 = var28;
		var28 += var26;
		int var48 = var28;
		var28 += var26 * 2 + var27 * 2;

		numVertices = var9;
		numFaces = var10;
		numTextures = var11;

		var28 += var26 + var27 * 2;
		verticesX = new int[var9];
		verticesY = new int[var9];
		verticesZ = new int[var9];
		faceIndicesA = new int[var10];
		faceIndicesB = new int[var10];
		faceIndicesC = new int[var10];
		if (var12 == 1) {
			this.faceTypes = new int[var10];
		}

		if (var17 == 1)
			vertexBones = new int[var9];
		if (var12 == 1)
			faceTypes = new int[var10];
		if (var13 == 255)
			facePriorities = new int[var10];
		else {
			facePriority = (byte) var12;
		}
		if (var14 == 1)
			faceAlphas = new int[var10];
		if (var15 == 1)
			faceSkin = new int[var10];
		if(var16 == 1) {
			faceTextures = new int[var10];
		}
		if (var16 == 1 && var11 > 0) {
			this.texture_coordinates = new byte[var10];
		}
		faceColours = new int[var10];
		if (var11 > 0) {
			this.textureMappingP = new int[var11];
			this.textureMappingM = new int[var11];
			this.textureMappingN = new int[var11];
		}

		var2.setPosition(var11);
		var3.setPosition(var40);
		var4.setPosition(var41);
		var5.setPosition(var42);
		var6.setPosition(var34);
		int var50 = 0;
		int var51 = 0;
		int var52 = 0;

		int var53;
		int var54;
		int var55;
		int var56;
		int var57;
		for (var53 = 0; var53 < var9; ++var53) {
			var54 = var2.readUByte();
			var55 = 0;
			if ((var54 & 1) != 0) {
				var55 = var3.readSmart();
			}

			var56 = 0;
			if ((var54 & 2) != 0) {
				var56 = var4.readSmart();
			}

			var57 = 0;
			if ((var54 & 4) != 0) {
				var57 = var5.readSmart();
			}

			this.verticesX[var53] = var50 + var55;
			this.verticesY[var53] = var51 + var56;
			this.verticesZ[var53] = var52 + var57;
			var50 = this.verticesX[var53];
			var51 = this.verticesY[var53];
			var52 = this.verticesZ[var53];
			if (var17 == 1) {
				this.vertexBones[var53] = var6.readUByte();
			}
		}


		if(var18 == 1) {
			for (var51 = 0; var51 < var9; ++var51)
			{
				var54 = var6.readUByte();
				//def.animayaGroups[var51] = new int[var52];
				//def.animayaScales[var51] = new int[var52];

				for (var53 = 0; var53 < var54; ++var53)
				{
					var6.skip(2);
					//def.animayaGroups[var51][var53] = var6.readUByte();
					//def.animayaScales[var51][var53] = var6.readUByte();
				}
			}
		}
		var2.setPosition(var39);
		var3.setPosition(var30);
		var4.setPosition(var32);
		var5.setPosition(var35);
		var6.setPosition(var33);
		var7.setPosition(var37);
		var8.setPosition(var38);
		for (var53 = 0; var53 < var10; var53++) {
			faceColours[var53] = var2.readUShort();
			if (var12 == 1) {
				this.faceTypes[var53] = var3.readUByte();
			}
			if (var13 == 255) {
				facePriorities[var53] = var4.readByte();
			}
			if (var14 == 1) {
				faceAlphas[var53] = var5.readByte();
			}
			if (var15 == 1)
				faceSkin[var53] = var6.readUByte();
			if (var16 == 1) {
				faceTextures[var53] = (var7.readUShort() - 1);
			}

			if (texture_coordinates != null && faceTextures[var53] != -1)
				this.texture_coordinates[var53] = (byte) (var8.readUByte() - 1);

		}

		var2.setPosition(var36);
		var3.setPosition(var31);

		var53 = 0;
		var54 = 0;
		var55 = 0;
		var56 = 0;
		int var58;
		for (var57 = 0; var57 < var10; ++var57) {
			var58 = var3.readByte();
			if (var58 == 1) {
				var53 = var2.readSmart() + var56;
				var54 = var2.readSmart() + var53;
				var55 = var2.readSmart() + var54;
				var56 = var55;
				this.faceIndicesA[var57] = var53;
				this.faceIndicesB[var57] = var54;
				this.faceIndicesC[var57] = var55;
			}

			if (var58 == 2) {
				var54 = var55;
				var55 = var2.readSmart() + var56;
				var56 = var55;
				this.faceIndicesA[var57] = var53;
				this.faceIndicesB[var57] = var54;
				this.faceIndicesC[var57] = var55;
			}

			if (var58 == 3) {
				var53 = var55;
				var55 = var2.readSmart() + var56;
				var56 = var55;
				this.faceIndicesA[var57] = var53;
				this.faceIndicesB[var57] = var54;
				this.faceIndicesC[var57] = var55;
			}

			if (var58 == 4) {
				int var59 = var53;
				var53 = var54;
				var54 = var59;
				var55 = var2.readSmart() + var56;
				var56 = var55;
				this.faceIndicesA[var57] = var53;
				this.faceIndicesB[var57] = var59;
				this.faceIndicesC[var57] = var55;
			}
		}

		var2.setPosition(var43);
		var3.setPosition(var44);
		var4.setPosition(var45);
		var5.setPosition(var46);
		var6.setPosition(var47);
		var7.setPosition(var48);

		for (int k14 = 0; k14 < var11; k14++) {
			int i15 = textureRenderTypes[k14] & 0xff;
			if (i15 == 0) {
				textureMappingP[k14] = var2.readUShort();
				textureMappingM[k14] = var2.readUShort();
				textureMappingN[k14] = var2.readUShort();
			}
		}
/*
		var2.setPosition(var28);
		var57 = var2.readUByte();
		if (var57 != 0)
		{
			var2.readUShort();
			var2.readUShort();
			var2.readUShort();
			var2.readInt();
		}*/
	}
}
