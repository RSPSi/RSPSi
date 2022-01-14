package com.jagex.entity.model;

public class PreviewModel extends Mesh {

	public PreviewModel(Mesh model) {
		super();
		fitsOnSingleSquare = (model.fitsOnSingleSquare);
		minimumX = (model.minimumX);
		maximumX = (model.maximumX);
		maximumZ = (model.maximumZ);
		minimumZ = (model.minimumZ);
		boundingPlaneRadius = (model.boundingPlaneRadius);
		minimumY = (model.minimumY);
		boundingSphereRadius = (model.boundingSphereRadius);
		boundingCylinderRadius = (model.boundingCylinderRadius);
		anInt1654 = (model.anInt1654);
		shadedFaceColoursX = copyArray(model.shadedFaceColoursX);
		shadedFaceColoursY = copyArray(model.shadedFaceColoursY);
		shadedFaceColoursZ = copyArray(model.shadedFaceColoursZ);
		faceTransparencies = copyArray(model.faceTransparencies);
		faceColours = copyArray(model.faceColours);
		faceTextures = copyArray(model.faceTextures);
		textureCoords = copyArray(model.textureCoords);
		textureRenderTypes = copyArray(model.textureRenderTypes);
		faceGroups = copyArray(model.faceGroups);
		faceRenderPriorities = copyArray(model.faceRenderPriorities);
		faceCount = (model.faceCount);
		packedTransparencyVertexGroups = copyArray(model.packedTransparencyVertexGroups);
		faceIndices1 = copyArray(model.faceIndices1);
		faceIndices2 = copyArray(model.faceIndices2);
		faceIndices3 = copyArray(model.faceIndices3);
		normals = (model.normals);
		priority = (model.priority);
		numTextureFaces = model.numTextureFaces;
		texIndices1 = copyArray(model.texIndices1);
		texIndices2 = copyArray(model.texIndices2);
		texIndices3 = copyArray(model.texIndices3);
		faceRenderTypes = copyArray(model.faceRenderTypes);
		vertexGroups = copyArray(model.vertexGroups);
		packedVertexGroups = copyArray(model.packedVertexGroups);
		vertexX = copyArray(model.vertexX);
		vertexY = copyArray(model.vertexY);
		vertexZ = copyArray(model.vertexZ);
		vertexCount = model.vertexCount;

	}

	@Override
	public int getZVertexMax() {
		return 500;
	}

}
