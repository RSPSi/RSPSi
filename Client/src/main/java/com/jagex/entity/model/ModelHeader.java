package com.jagex.entity.model;

public class ModelHeader {

	private int colourDataOffset;

	private byte[] data;
	private int texturedFaces;
	private int texturePointerOffset;
	private int faceAlphaOffset;
	private int faceDataOffset;
	private int facePriorityOffset;
	private int faces;
	private int faceSkinOffset;
	private int faceTypeOffset;
	private int uvMapFaceOffset;
	private int vertexDirectionOffset;
	private int vertexSkinOffset;
	private int vertices;
	private int xDataOffset;
	private int yDataOffset;
	private int zDataOffset;

	public int getColourDataOffset() {
		return colourDataOffset;
	}

	public byte[] getData() {
		return data;
	}

	public int getFaceAlphaOffset() {
		return faceAlphaOffset;
	}

	public int getFaceBoneOffset() {
		return faceSkinOffset;
	}

	public int getFaceCount() {
		return faces;
	}

	public int getFaceDataOffset() {
		return faceDataOffset;
	}

	public int getFacePriorityOffset() {
		return facePriorityOffset;
	}

	public int getFaceTypeOffset() {
		return faceTypeOffset;
	}

	public int getTexturedFaceCount() {
		return texturedFaces;
	}

	public int getTexturePointOffset() {
		return texturePointerOffset;
	}

	public int getUvMapFaceOffset() {
		return uvMapFaceOffset;
	}

	public int getVertexBoneOffset() {
		return vertexSkinOffset;
	}

	public int getVertexDirectionOffset() {
		return vertexDirectionOffset;
	}

	public int getVertices() {
		return vertices;
	}

	public int getXDataOffset() {
		return xDataOffset;
	}

	public int getYDataOffset() {
		return yDataOffset;
	}

	public int getZDataOffset() {
		return zDataOffset;
	}

	public void setColourDataOffset(int colourDataOffset) {
		this.colourDataOffset = colourDataOffset;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setFaceAlphaOffset(int triangleAlphaOffset) {
		faceAlphaOffset = triangleAlphaOffset;
	}

	public void setFaceCount(int triangles) {
		faces = triangles;
	}

	public void setFaceDataOffset(int triangleDataOffset) {
		faceDataOffset = triangleDataOffset;
	}

	public void setFacePriorityOffset(int trianglePriorityOffset) {
		facePriorityOffset = trianglePriorityOffset;
	}

	public void setFaceSkinOffset(int triangleSkinOffset) {
		faceSkinOffset = triangleSkinOffset;
	}

	public void setFaceTypeOffset(int triangleTypeOffset) {
		faceTypeOffset = triangleTypeOffset;
	}

	public void setTexturedFaceCount(int texturedTriangles) {
		texturedFaces = texturedTriangles;
	}

	public void setTexturePointerOffset(int texturePointerOffset) {
		this.texturePointerOffset = texturePointerOffset;
	}

	public void setUvMapFaceOffset(int uvMapTriangleOffset) {
		uvMapFaceOffset = uvMapTriangleOffset;
	}

	public void setVertexDirectionOffset(int vertexDirectionOffset) {
		this.vertexDirectionOffset = vertexDirectionOffset;
	}

	public void setVertexSkinOffset(int vertexSkinOffset) {
		this.vertexSkinOffset = vertexSkinOffset;
	}

	public void setVertices(int vertices) {
		this.vertices = vertices;
	}

	public void setXDataOffset(int xDataOffset) {
		this.xDataOffset = xDataOffset;
	}

	public void setYDataOffset(int yDataOffset) {
		this.yDataOffset = yDataOffset;
	}

	public void setZDataOffset(int zDataOffset) {
		this.zDataOffset = zDataOffset;
	}

}