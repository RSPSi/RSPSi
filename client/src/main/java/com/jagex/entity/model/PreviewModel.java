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
		 this.faceColourOrTextureId = copyArray(model.faceColourOrTextureId);
		faceGroups = (model.faceGroups);
		facePriorities = copyArray(model.facePriorities);
		faces = (model.faces);
		faceSkin = copyArray(model.faceSkin);
		faceIndexX = copyArray(model.faceIndexX);
		faceIndexY = copyArray(model.faceIndexY);
		faceIndexZ = copyArray(model.faceIndexZ);
		normals = (model.normals);
		priority = (model.priority);
		texturedFaces = (model.texturedFaces);
		texturedFaceIndexX = copyArray(model.texturedFaceIndexX);
		texturedFaceIndexY = copyArray(model.texturedFaceIndexY);
		texturedFaceIndexZ = copyArray(model.texturedFaceIndexZ);
		faceTypes = copyArray(model.faceTypes);
		vertexGroups = (model.vertexGroups);
		vertexBones = copyArray(model.vertexBones);
		vertexX = copyArray(model.vertexX);
		vertexY = copyArray(model.vertexY);
		vertexZ = copyArray(model.vertexZ);
		vertices = (model.vertices);
	}
	
	@Override
	public int getZVertexMax() {
		return 500;
	}

}
