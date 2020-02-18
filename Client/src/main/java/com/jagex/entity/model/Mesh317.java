package com.jagex.entity.model;

import com.jagex.io.Buffer;

public class Mesh317 extends Mesh {

	public Mesh317(byte[] data) {

		Buffer buffer = new Buffer(data);
		buffer.setPosition(data.length - 18);
		ModelHeader header = new ModelHeader();
		header.setData(data);
		header.setVertices(buffer.readUShort());
		header.setFaceCount(buffer.readUShort());
		header.setTexturedFaceCount(buffer.readUByte());

		int useTextures = buffer.readUByte();
		int useFacePriority = buffer.readUByte();
		int useTransparency = buffer.readUByte();
		int useFaceSkinning = buffer.readUByte();
		int useVertexSkinning = buffer.readUByte();
		int xDataOffset = buffer.readUShort();
		int yDataOffset = buffer.readUShort();
		int zDataOffset = buffer.readUShort();
		int faceDataLength = buffer.readUShort();

		int offset = 0;
		header.setVertexDirectionOffset(offset);
		offset += header.getVertices();

		header.setFaceTypeOffset(offset);
		offset += header.getFaceCount();

		header.setFacePriorityOffset(offset);

		if (useFacePriority == 255) {
			offset += header.getFaceCount();
		} else {
			header.setFacePriorityOffset(-useFacePriority - 1);
		}

		header.setFaceSkinOffset(offset);
		if (useFaceSkinning == 1) {
			offset += header.getFaceCount();
		} else {
			header.setFaceSkinOffset(-1);
		}

		header.setTexturePointerOffset(offset);
		if (useTextures == 1) {
			offset += header.getFaceCount();
		} else {
			header.setTexturePointerOffset(-1);
		}

		header.setVertexSkinOffset(offset);
		if (useVertexSkinning == 1) {
			offset += header.getVertices();
		} else {
			header.setVertexSkinOffset(-1);
		}

		header.setFaceAlphaOffset(offset);
		if (useTransparency == 1) {
			offset += header.getFaceCount();
		} else {
			header.setFaceAlphaOffset(-1);
		}

		header.setFaceDataOffset(offset);
		offset += faceDataLength;

		header.setColourDataOffset(offset);
		offset += header.getFaceCount() * 2;

		header.setUvMapFaceOffset(offset);
		offset += header.getTexturedFaceCount() * 6;

		header.setXDataOffset(offset);
		offset += xDataOffset;

		header.setYDataOffset(offset);
		offset += yDataOffset;

		header.setZDataOffset(offset);
		offset += zDataOffset;
		
		numVertices = header.getVertices();
		numFaces = header.getFaceCount();
		numTextures = header.getTexturedFaceCount();
		verticesX = new int[numVertices];
		verticesY = new int[numVertices];
		verticesZ = new int[numVertices];
		faceIndicesA = new int[numFaces];
		faceIndicesB = new int[numFaces];
		faceIndicesC = new int[numFaces];

		if(numTextures > 0){
			textureRenderTypes = new byte[numTextures];
			textureMappingP = new int[numTextures];
			textureMappingM = new int[numTextures];
			textureMappingN = new int[numTextures];
		}
		if(useTextures == 1){
			faceTypes = new int[numFaces];
			faceTextures = new int[numFaces];
			texture_coordinates = new byte[numFaces];
		}

		if (header.getVertexBoneOffset() >= 0) {
			vertexBones = new int[numVertices];
		}


		if (header.getFacePriorityOffset() >= 0) {
			facePriorities = new int[numFaces];
		} else {
			facePriority = -header.getFacePriorityOffset() - 1;
		}

		if (header.getFaceAlphaOffset() >= 0) {
			faceAlphas = new int[numFaces];
		}

		if (header.getFaceBoneOffset() >= 0) {
			faceSkin = new int[numFaces];
		}

		faceColours = new int[numFaces];
		Buffer directions = new Buffer(header.getData());
		directions.setPosition(header.getVertexDirectionOffset());

		Buffer verticesX = new Buffer(header.getData());
		verticesX.setPosition(header.getXDataOffset());

		Buffer verticesY = new Buffer(header.getData());
		verticesY.setPosition(header.getYDataOffset());

		Buffer verticesZ = new Buffer(header.getData());
		verticesZ.setPosition(header.getZDataOffset());

		Buffer bones = new Buffer(header.getData());
		bones.setPosition(header.getVertexBoneOffset());

		int baseX = 0;
		int baseY = 0;
		int baseZ = 0;

		for (int vertex = 0; vertex < numVertices; vertex++) {
			int mask = directions.readUByte();
			int x = 0;
			if ((mask & 1) != 0) {
				x = verticesX.readSmart();
			}

			int y = 0;
			if ((mask & 2) != 0) {
				y = verticesY.readSmart();
			}

			int z = 0;
			if ((mask & 4) != 0) {
				z = verticesZ.readSmart();
			}

			this.verticesX[vertex] = baseX + x;
			this.verticesY[vertex] = baseY + y;
			this.verticesZ[vertex] = baseZ + z;
			baseX = this.verticesX[vertex];
			baseY = this.verticesY[vertex];
			baseZ = this.verticesZ[vertex];

			if (vertexBones != null) {
				vertexBones[vertex] = bones.readUByte();
			}
		}

		Buffer colours = directions;
		colours.setPosition(header.getColourDataOffset());

		Buffer points = verticesX;
		points.setPosition(header.getTexturePointOffset());

		Buffer priorities = verticesY;
		priorities.setPosition(header.getFacePriorityOffset());

		Buffer alphas = verticesZ;
		alphas.setPosition(header.getFaceAlphaOffset());

		bones.setPosition(header.getFaceBoneOffset());

		for (int face = 0; face < numFaces; face++) {
			faceColours[face] = colours.readUShort();
			if (useTextures == 1) {
				int type = points.readUByte();
				faceTypes[face] = type;
				if((type & 2) == 2){
					this.texture_coordinates[face] = (byte)(type >> 2);
					this.faceTextures[face] = this.faceColours[face];
					this.faceColours[face] = 127;
				} else {
					this.faceTextures[face] = -1;
					this.texture_coordinates[face] = -1;
				}
			}
			if (facePriorities != null) {
				facePriorities[face] = priorities.readUByte();
			}
			if (faceAlphas != null) {
				faceAlphas[face] = alphas.readUByte();
			}
			if (faceSkin != null) {
				faceSkin[face] = bones.readUByte();
			}
		}

		Buffer faceData = directions;
		faceData.setPosition(header.getFaceDataOffset());

		Buffer types = verticesX;
		types.setPosition(header.getFaceTypeOffset());

		int faceX = 0;
		int faceY = 0;
		int faceZ = 0;
		offset = 0;

		for (int vertex = 0; vertex < numFaces; vertex++) {
			int type = types.readUByte();

			if (type == 1) {
				faceX = faceData.readSmart() + offset;
				offset = faceX;
				faceY = faceData.readSmart() + offset;
				offset = faceY;
				faceZ = faceData.readSmart() + offset;
				offset = faceZ;

				faceIndicesA[vertex] = faceX;
				faceIndicesB[vertex] = faceY;
				faceIndicesC[vertex] = faceZ;
			} else if (type == 2) {
				faceY = faceZ;
				faceZ = faceData.readSmart() + offset;
				offset = faceZ;

				faceIndicesA[vertex] = faceX;
				faceIndicesB[vertex] = faceY;
				faceIndicesC[vertex] = faceZ;
			} else if (type == 3) {
				faceX = faceZ;
				faceZ = faceData.readSmart() + offset;
				offset = faceZ;

				faceIndicesA[vertex] = faceX;
				faceIndicesB[vertex] = faceY;
				faceIndicesC[vertex] = faceZ;
			} else if (type == 4) {
				int temp = faceX;
				faceX = faceY;
				faceY = temp;

				faceZ = faceData.readSmart() + offset;
				offset = faceZ;

				faceIndicesA[vertex] = faceX;
				faceIndicesB[vertex] = faceY;
				faceIndicesC[vertex] = faceZ;
			}
		}

		Buffer maps = directions;
		maps.setPosition(header.getUvMapFaceOffset());

		for (int index = 0; index < numTextures; index++) {
			textureRenderTypes[index] = 0;
			textureMappingP[index] = maps.readUShort();
			textureMappingM[index] = maps.readUShort();
			textureMappingN[index] = maps.readUShort();
		}

		if(this.texture_coordinates != null) {
			boolean var46 = false;

			for(int var43 = 0; var43 < numFaces; ++var43) {
				int var44 = this.texture_coordinates[var43] & 255;
				if(var44 != 255) {
					if(this.faceIndicesA[var43] == (this.textureMappingP[var44] & '\uffff') && this.faceIndicesB[var43] == (this.textureMappingM[var44] & '\uffff') && this.faceIndicesC[var43] == (this.textureMappingN[var44] & '\uffff')) {
						this.texture_coordinates[var43] = -1;
					} else {
						var46 = true;
					}
				}
			}

			if(!var46) {
				this.texture_coordinates = null;
			}
		}

	}
}
