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
			textureMap = new byte[var11];
			var2.setPosition(0);
			for (int j5 = 0; j5 < var11; j5++) {
				byte var29 = textureMap[j5] = var2.readByte();
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

		vertexCount = var9;
		triangleCount = var10;
		numTextureFaces = var11;

		var28 += var26 + var27 * 2;
		vertexX = new int[var9];
		vertexY = new int[var9];
		vertexZ = new int[var9];
		faceIndices1 = new int[var10];
		faceIndices2 = new int[var10];
		faceIndices3 = new int[var10];

		if (var17 == 1)
			packedVertexGroups = new int[var9];
		if (var12 == 1)
			triangleInfo = new int[var10];
		if (var13 == 255)
			faceRenderPriorities = new byte[var10];
		else {
			priority = (byte) var12;
		}
		if (var14 == 1)
			faceTransparencies = new int[var10];
		if (var15 == 1)
			packedTransparencyVertexGroups = new int[var10];
		if(var16 == 1) {
			faceMaterial = new int[var10];
		}
		if (var16 == 1 && var11 > 0) {
			this.faceTexture = new byte[var10];
		}

		if(var18 == 1) {
			this.animayaGroups = new int[vertexCount][];
			this.animayaScales = new int[vertexCount][];
		}

		triangleColors = new int[var10];
		if (var11 > 0) {
			this.texIndices1 = new int[var11];
			this.texIndices2 = new int[var11];
			this.texIndices3 = new int[var11];
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

			this.vertexX[var53] = var50 + var55;
			this.vertexY[var53] = var51 + var56;
			this.vertexZ[var53] = var52 + var57;
			var50 = this.vertexX[var53];
			var51 = this.vertexY[var53];
			var52 = this.vertexZ[var53];
			if (var17 == 1) {
				this.packedVertexGroups[var53] = var6.readUByte();
			}
		}


		if(var18 == 1) {
			for (var53 = 0; var53 < vertexCount; ++var53)
			{
				var54 = var6.readUByte();
				animayaGroups[var53] = new int[var54];
				animayaScales[var53] = new int[var54];

				for (var55 = 0; var55 < var54; ++var55)
				{
					animayaGroups[var53][var55] = var6.readUByte();
					animayaScales[var53][var55] = var6.readUByte();
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
			triangleColors[var53] = var2.readUShort();
			if (var12 == 1) {
				this.triangleInfo[var53] = var3.readUByte();
			}
			if (var13 == 255) {
				faceRenderPriorities[var53] = var4.readByte();
			}
			if (var14 == 1) {
				faceTransparencies[var53] = var5.readByte();
				if(faceTransparencies[var53] < 0){
					faceTransparencies[var53] += 256;
				}
			}
			if (var15 == 1)
				packedTransparencyVertexGroups[var53] = var6.readUByte();
			if (var16 == 1) {
				faceMaterial[var53] = (var7.readUShort() - 1);
			}

			if (faceTexture != null && faceMaterial[var53] != -1)
				this.faceTexture[var53] = (byte) (var8.readUByte() - 1);

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
				this.faceIndices1[var57] = var53;
				this.faceIndices2[var57] = var54;
				this.faceIndices3[var57] = var55;
			}

			if (var58 == 2) {
				var54 = var55;
				var55 = var2.readSmart() + var56;
				var56 = var55;
				this.faceIndices1[var57] = var53;
				this.faceIndices2[var57] = var54;
				this.faceIndices3[var57] = var55;
			}

			if (var58 == 3) {
				var53 = var55;
				var55 = var2.readSmart() + var56;
				var56 = var55;
				this.faceIndices1[var57] = var53;
				this.faceIndices2[var57] = var54;
				this.faceIndices3[var57] = var55;
			}

			if (var58 == 4) {
				int var59 = var53;
				var53 = var54;
				var54 = var59;
				var55 = var2.readSmart() + var56;
				var56 = var55;
				this.faceIndices1[var57] = var53;
				this.faceIndices2[var57] = var59;
				this.faceIndices3[var57] = var55;
			}
		}

		var2.setPosition(var43);
		var3.setPosition(var44);
		var4.setPosition(var45);
		var5.setPosition(var46);
		var6.setPosition(var47);
		var7.setPosition(var48);

		for (int k14 = 0; k14 < var11; k14++) {
			int i15 = textureMap[k14] & 0xff;
			if (i15 == 0) {
				texIndices1[k14] = var2.readUShort();
				texIndices2[k14] = var2.readUShort();
				texIndices3[k14] = var2.readUShort();
			}
		}

		var2.setPosition(var30);
		var57 = var2.readUByte();
		if (var57 != 0)
		{
			var2.readUShort();
			var2.readUShort();
			var2.readUShort();
			var2.readInt();
		}
	}
}
