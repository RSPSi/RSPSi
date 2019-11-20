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
		
		vertices = header.getVertices();
		faces = header.getFaceCount();
		texturedFaces = header.getTexturedFaceCount();
		vertexX = new int[vertices];
		vertexY = new int[vertices];
		vertexZ = new int[vertices];
		faceIndexX = new int[faces];
		faceIndexY = new int[faces];
		faceIndexZ = new int[faces];
		texturedFaceIndexX = new int[texturedFaces];
		texturedFaceIndexY = new int[texturedFaces];
		texturedFaceIndexZ = new int[texturedFaces];

		if (header.getVertexBoneOffset() >= 0) {
			vertexBones = new int[vertices];
		}

		if (header.getTexturePointOffset() >= 0) {
			faceTypes = new int[faces];
		}

		if (header.getFacePriorityOffset() >= 0) {
			facePriorities = new int[faces];
		} else {
			priority = -header.getFacePriorityOffset() - 1;
		}

		if (header.getFaceAlphaOffset() >= 0) {
			faceAlphas = new int[faces];
		}

		if (header.getFaceBoneOffset() >= 0) {
			faceSkin = new int[faces];
		}

		faceColourOrTextureId = new int[faces];
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

		for (int vertex = 0; vertex < vertices; vertex++) {
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

			vertexX[vertex] = baseX + x;
			vertexY[vertex] = baseY + y;
			vertexZ[vertex] = baseZ + z;
			baseX = vertexX[vertex];
			baseY = vertexY[vertex];
			baseZ = vertexZ[vertex];

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

		for (int face = 0; face < faces; face++) {
			faceColourOrTextureId[face] = colours.readUShort();
			if (faceTypes != null) {
				faceTypes[face] = points.readUByte();
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

		for (int vertex = 0; vertex < faces; vertex++) {
			int type = types.readUByte();

			if (type == 1) {
				faceX = faceData.readSmart() + offset;
				offset = faceX;
				faceY = faceData.readSmart() + offset;
				offset = faceY;
				faceZ = faceData.readSmart() + offset;
				offset = faceZ;

				faceIndexX[vertex] = faceX;
				faceIndexY[vertex] = faceY;
				faceIndexZ[vertex] = faceZ;
			} else if (type == 2) {
				faceY = faceZ;
				faceZ = faceData.readSmart() + offset;
				offset = faceZ;

				faceIndexX[vertex] = faceX;
				faceIndexY[vertex] = faceY;
				faceIndexZ[vertex] = faceZ;
			} else if (type == 3) {
				faceX = faceZ;
				faceZ = faceData.readSmart() + offset;
				offset = faceZ;

				faceIndexX[vertex] = faceX;
				faceIndexY[vertex] = faceY;
				faceIndexZ[vertex] = faceZ;
			} else if (type == 4) {
				int temp = faceX;
				faceX = faceY;
				faceY = temp;

				faceZ = faceData.readSmart() + offset;
				offset = faceZ;

				faceIndexX[vertex] = faceX;
				faceIndexY[vertex] = faceY;
				faceIndexZ[vertex] = faceZ;
			}
		}

		Buffer maps = directions;
		maps.setPosition(header.getUvMapFaceOffset());

		for (int index = 0; index < texturedFaces; index++) {
			texturedFaceIndexX[index] = maps.readUShort();
			texturedFaceIndexY[index] = maps.readUShort();
			texturedFaceIndexZ[index] = maps.readUShort();
		}

	}
}
