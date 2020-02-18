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
		faceAlphas = copyArray(model.faceAlphas);
		faceColours = copyArray(model.faceColours);
		faceTextures = copyArray(model.faceTextures);
		texture_coordinates = copyArray(model.texture_coordinates);
		textureRenderTypes = copyArray(model.textureRenderTypes);
		faceGroups = copyArray(model.faceGroups);
		facePriorities = copyArray(model.facePriorities);
		numFaces = (model.numFaces);
		faceSkin = copyArray(model.faceSkin);
		faceIndicesA = copyArray(model.faceIndicesA);
		faceIndicesB = copyArray(model.faceIndicesB);
		faceIndicesC = copyArray(model.faceIndicesC);
		normals = (model.normals);
		facePriority = (model.facePriority);
		numTextures = model.numTextures;
		textureMappingP = copyArray(model.textureMappingP);
		textureMappingM = copyArray(model.textureMappingM);
		textureMappingN = copyArray(model.textureMappingN);
		faceTypes = copyArray(model.faceTypes);
		vertexGroups = copyArray(model.vertexGroups);
		vertexBones = copyArray(model.vertexBones);
		verticesX = copyArray(model.verticesX);
		verticesY = copyArray(model.verticesY);
		verticesZ = copyArray(model.verticesZ);
		numVertices = model.numVertices;
		
	}
	
	@Override
	public int getZVertexMax() {
		return 500;
	}

}
