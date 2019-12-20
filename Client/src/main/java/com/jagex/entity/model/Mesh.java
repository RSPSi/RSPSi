package com.jagex.entity.model;

import java.util.Arrays;
import java.util.Objects;

import org.major.cache.anim.FrameConstants;

import com.jagex.Client;
import com.jagex.cache.anim.Frame;
import com.jagex.cache.anim.FrameBase;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.Renderable;
import com.jagex.io.Buffer;
import com.jagex.util.Constants;
import com.jagex.util.ObjectKey;
import com.rspsi.misc.ToolType;
import com.rspsi.options.Options;

import lombok.Getter;
import lombok.Setter;

public class Mesh extends Renderable {

	// Class30_Sub2_Sub4_Sub6


	public static boolean aBoolean1684;
	public static int resourceCount;
	public static ObjectKey[] resourceIDTag = new ObjectKey[1000];
	public static Mesh EMPTY_MODEL = new Mesh();
	public static int mouseX;
	public static int mouseY;

	private static int[] anIntArray1622 = new int[2000];
	private static int[] anIntArray1623 = new int[2000];
	private static int[] anIntArray1624 = new int[2000];
	private static int[] anIntArray1625 = new int[2000];
	static int centroidX;
	static int centroidY;
	static int centroidZ;

	public static int checkedLight(int colour, int light, int index) {
		if ((index & 0x2) != 0) {
			if (light < 0) {
				light = 0;
			} else if (light > 127) {
				light = 127;
			}

			return 127 - light;
		}

		light = light * (colour & 0x7f) >> 7;
		if (light < 2) {
			light = 2;
		} else if (light > 126) {
			light = 126;
		}

		return (colour & 0xff80) + light;
	}


	private final static boolean insideTriangle(int x, int y, int k, int l, int i1, int j1, int k1, int l1) {
		if (y < k && y < l && y < i1)
			return false;
		if (y > k && y > l && y > i1)
			return false;
		if (x < j1 && x < k1 && x < l1)
			return false;
		return x <= j1 || x <= k1 || x <= l1;
	}



	public boolean fitsOnSingleSquare;
	public int minimumX;
	public int maximumX;
	public int maximumZ;
	public int minimumZ;
	public int boundingPlaneRadius;
	public int minimumY;
	public int boundingSphereRadius;
	public int boundingCylinderRadius;
	public int anInt1654;
	public int[] shadedFaceColoursX;
	public int[] shadedFaceColoursY;
	public int[] shadedFaceColoursZ;
	public int[] faceAlphas;
	public int[] faceColourOrTextureId;
	public int[][] faceGroups;
	public int[] facePriorities;
	public int faces;
	public int[] faceSkin;
	public int[] faceIndexX;
	public int[] faceIndexY;
	public int[] faceIndexZ;
	public VertexNormal[] normals;
	public int priority;
	public int texturedFaces;
	public int[] texturedFaceIndexX;
	public int[] texturedFaceIndexY;
	public int[] texturedFaceIndexZ;
	public int[] faceTypes;
	public int[][] vertexGroups;
	public int[] vertexBones;
	public int[] vertexX;
	public int[] vertexY;
	public int[] vertexZ;
	public int vertices;
	private boolean translucent;

	//public List<Vector3f> vertexes;

	protected Mesh() {
	}

	public Mesh(boolean contouredGround, boolean delayShading, Mesh model) {
		vertices = model.vertices;
		faces = model.faces;
		texturedFaces = model.texturedFaces;

		if (contouredGround) {
			vertexY = new int[vertices];

			for (int vertex = 0; vertex < vertices; vertex++) {
				vertexY[vertex] = model.vertexY[vertex];
			}
		} else {
			vertexY = model.vertexY;
		}

		if (delayShading) {
			shadedFaceColoursX = new int[faces];
			shadedFaceColoursY = new int[faces];
			shadedFaceColoursZ = new int[faces];

			for (int k = 0; k < faces; k++) {
				shadedFaceColoursX[k] = model.shadedFaceColoursX[k];
				shadedFaceColoursY[k] = model.shadedFaceColoursY[k];
				shadedFaceColoursZ[k] = model.shadedFaceColoursZ[k];
			}

			faceTypes = new int[faces];
			if (model.faceTypes == null) {
				for (int triangle = 0; triangle < faces; triangle++) {
					faceTypes[triangle] = 0;
				}
			} else {
				for (int index = 0; index < faces; index++) {
					faceTypes[index] = model.faceTypes[index];
				}
			}

			super.normals = new VertexNormal[vertices];
			for (int index = 0; index < vertices; index++) {
				VertexNormal parent = super.normals[index] = new VertexNormal();
				VertexNormal copied = model.getNormal(index);
				parent.setX(copied.getX());
				parent.setY(copied.getY());
				parent.setZ(copied.getZ());
				parent.setFaceCount(copied.getFaceCount());
			}

			normals = model.normals;
		} else {
			shadedFaceColoursX = model.shadedFaceColoursX;
			shadedFaceColoursY = model.shadedFaceColoursY;
			shadedFaceColoursZ = model.shadedFaceColoursZ;
			faceTypes = model.faceTypes;
		}

		vertexX = model.vertexX;
		vertexZ = model.vertexZ;
		faceColourOrTextureId = model.faceColourOrTextureId;
		faceAlphas = model.faceAlphas;
		facePriorities = model.facePriorities;
		priority = model.priority;
		faceIndexX = model.faceIndexX;
		faceIndexY = model.faceIndexY;
		faceIndexZ = model.faceIndexZ;
		texturedFaceIndexX = model.texturedFaceIndexX;
		texturedFaceIndexY = model.texturedFaceIndexY;
		texturedFaceIndexZ = model.texturedFaceIndexZ;
		super.modelHeight = model.modelHeight;
		boundingPlaneRadius = model.boundingPlaneRadius;
		boundingCylinderRadius = model.boundingCylinderRadius;
		boundingSphereRadius = model.boundingSphereRadius;
		minimumX = model.minimumX;
		maximumZ = model.maximumZ;
		minimumZ = model.minimumZ;
		maximumX = model.maximumX;
	}


	public Mesh(ModelHeader header) {
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
		int offset = 0;

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

	public Mesh(int modelCount, Mesh[] models) {
		boolean hasTexturePoints = false;
		boolean hasFacePriorities = false;
		boolean hasFaceAlphas = false;
		boolean hasSkinValues = false;
		vertices = 0;
		faces = 0;
		texturedFaces = 0;
		priority = -1;

		for (int index = 0; index < modelCount; index++) {
			Mesh model = models[index];
			if (model != null) {
				vertices += model.vertices;
				faces += model.faces;
				texturedFaces += model.texturedFaces;
				hasTexturePoints |= model.faceTypes != null;

				if (model.facePriorities != null) {
					hasFacePriorities = true;
				} else {
					if (priority == -1) {
						priority = model.priority;
					}
					if (priority != model.priority) {
						hasFacePriorities = true;
					}
				}

				hasFaceAlphas |= model.faceAlphas != null;
				hasSkinValues |= model.faceSkin != null;
			}
		}

		vertexX = new int[vertices];
		vertexY = new int[vertices];
		vertexZ = new int[vertices];
		vertexBones = new int[vertices];
		faceIndexX = new int[faces];
		faceIndexY = new int[faces];
		faceIndexZ = new int[faces];
		texturedFaceIndexX = new int[texturedFaces];
		texturedFaceIndexY = new int[texturedFaces];
		texturedFaceIndexZ = new int[texturedFaces];

		if (hasTexturePoints) {
			faceTypes = new int[faces];
		}
		if (hasFacePriorities) {
			facePriorities = new int[faces];
		}
		if (hasFaceAlphas) {
			faceAlphas = new int[faces];
		}
		if (hasSkinValues) {
			faceSkin = new int[faces];
		}

		faceColourOrTextureId = new int[faces];
		vertices = 0;
		faces = 0;
		texturedFaces = 0;
		int texturedCount = 0;

		for (int index = 0; index < modelCount; index++) {
			Mesh model = models[index];
			if (model != null) {
				for (int face = 0; face < model.faces; face++) {
					if (hasTexturePoints) {
						if (model.faceTypes == null) {
							faceTypes[faces] = 0;
						} else {
							int point = model.faceTypes[face];
							if ((point & 2) == 2) {
								point += texturedCount << 2;
							}

							faceTypes[faces] = point;
						}
					}

					if (hasFacePriorities) {
						if (model.facePriorities == null) {
							facePriorities[faces] = model.priority;
						} else {
							facePriorities[faces] = model.facePriorities[face];
						}
					}

					if (hasFaceAlphas) {
						if (model.faceAlphas == null) {
							faceAlphas[faces] = 0;
						} else {
							faceAlphas[faces] = model.faceAlphas[face];
						}
					}

					if (hasSkinValues && model.faceSkin != null) {
						faceSkin[faces] = model.faceSkin[face];
					}

					faceColourOrTextureId[faces] = model.faceColourOrTextureId[face];
					faceIndexX[faces] = findMatchingVertex(model, model.faceIndexX[face]);
					faceIndexY[faces] = findMatchingVertex(model, model.faceIndexY[face]);
					faceIndexZ[faces] = findMatchingVertex(model, model.faceIndexZ[face]);
					faces++;
				}

				for (int face = 0; face < model.texturedFaces; face++) {
					texturedFaceIndexX[texturedFaces] = findMatchingVertex(model, model.texturedFaceIndexX[face]);
					texturedFaceIndexY[texturedFaces] = findMatchingVertex(model, model.texturedFaceIndexY[face]);
					texturedFaceIndexZ[texturedFaces] = findMatchingVertex(model, model.texturedFaceIndexZ[face]);
					texturedFaces++;
				}

				texturedCount += model.texturedFaces;
			}
		}
	}

	public Mesh(Mesh model, boolean shareColours, boolean shareAlphas, boolean shareVertices) {
		vertices = model.vertices;
		faces = model.faces;
		texturedFaces = model.texturedFaces;

		if (shareVertices) {
			vertexX = model.vertexX;
			vertexY = model.vertexY;
			vertexZ = model.vertexZ;
		} else {
			vertexX = new int[vertices];
			vertexY = new int[vertices];
			vertexZ = new int[vertices];

			for (int index = 0; index < vertices; index++) {
				vertexX[index] = model.vertexX[index];
				vertexY[index] = model.vertexY[index];
				vertexZ[index] = model.vertexZ[index];
			}
		}

		if (shareColours) {
			faceColourOrTextureId = model.faceColourOrTextureId;
		} else {
			faceColourOrTextureId = new int[faces];

			for (int face = 0; face < faces; face++) {
				faceColourOrTextureId[face] = model.faceColourOrTextureId[face];
			}
		}

		if (shareAlphas) {
			faceAlphas = model.faceAlphas;
		} else {
			faceAlphas = new int[faces];

			if (model.faceAlphas == null) {
				for (int face = 0; face < faces; face++) {
					faceAlphas[face] = 0;
				}
			} else {
				for (int face = 0; face < faces; face++) {
					faceAlphas[face] = model.faceAlphas[face];
				}
			}
		}

		vertexBones = model.vertexBones;
		faceSkin = model.faceSkin;
		faceTypes = model.faceTypes;
		faceIndexX = model.faceIndexX;
		faceIndexY = model.faceIndexY;
		faceIndexZ = model.faceIndexZ;
		facePriorities = model.facePriorities;
		priority = model.priority;
		texturedFaceIndexX = model.texturedFaceIndexX;
		texturedFaceIndexY = model.texturedFaceIndexY;
		texturedFaceIndexZ = model.texturedFaceIndexZ;
	}

	public Mesh(Mesh[] models, int modelCount) {
		boolean hasTexturePoints = false;
		boolean hasFacePriorities = false;
		boolean hasFaceAlphas = false;
		boolean hasFaceColours = false;
		vertices = 0;
		faces = 0;
		texturedFaces = 0;
		priority = -1;

		for (int index = 0; index < modelCount; index++) {
			Mesh model = models[index];

			if (model != null) {
				vertices += model.vertices;
				faces += model.faces;
				texturedFaces += model.texturedFaces;
				hasTexturePoints |= model.faceTypes != null;

				if (model.facePriorities != null) {
					hasFacePriorities = true;
				} else {
					if (priority == -1) {
						priority = model.priority;
					}
					if (priority != model.priority) {
						hasFacePriorities = true;
					}
				}
				hasFaceAlphas |= model.faceAlphas != null;
				hasFaceColours |= model.faceColourOrTextureId != null;
			}
		}

		vertexX = new int[vertices];
		vertexY = new int[vertices];
		vertexZ = new int[vertices];
		faceIndexX = new int[faces];
		faceIndexY = new int[faces];
		faceIndexZ = new int[faces];
		shadedFaceColoursX = new int[faces];
		shadedFaceColoursY = new int[faces];
		shadedFaceColoursZ = new int[faces];
		texturedFaceIndexX = new int[texturedFaces];
		texturedFaceIndexY = new int[texturedFaces];
		texturedFaceIndexZ = new int[texturedFaces];

		if (hasTexturePoints) {
			faceTypes = new int[faces];
		}
		if (hasFacePriorities) {
			facePriorities = new int[faces];
		}
		if (hasFaceAlphas) {
			faceAlphas = new int[faces];
		}
		if (hasFaceColours) {
			faceColourOrTextureId = new int[faces];
		}

		vertices = 0;
		faces = 0;
		texturedFaces = 0;
		int i1 = 0;

		for (int id = 0; id < modelCount; id++) {
			Mesh model = models[id];
			if (model != null) {
				int offset = vertices;

				for (int vertex = 0; vertex < model.vertices; vertex++) {
					vertexX[vertices] = model.vertexX[vertex];
					vertexY[vertices] = model.vertexY[vertex];
					vertexZ[vertices] = model.vertexZ[vertex];
					vertices++;
				}

				for (int face = 0; face < model.faces; face++) {
					faceIndexX[faces] = model.faceIndexX[face] + offset;
					faceIndexY[faces] = model.faceIndexY[face] + offset;
					faceIndexZ[faces] = model.faceIndexZ[face] + offset;
					shadedFaceColoursX[faces] = model.shadedFaceColoursX[face];
					shadedFaceColoursY[faces] = model.shadedFaceColoursY[face];
					shadedFaceColoursZ[faces] = model.shadedFaceColoursZ[face];

					if (hasTexturePoints) {
						if (model.faceTypes == null) {
							faceTypes[faces] = 0;
						} else {
							int point = model.faceTypes[face];
							if ((point & 2) == 2) {
								point += i1 << 2;
							}

							faceTypes[faces] = point;
						}
					}

					if (hasFacePriorities) {
						if (model.facePriorities == null) {
							facePriorities[faces] = model.priority;
						} else {
							facePriorities[faces] = model.facePriorities[face];
						}
					}

					if (hasFaceAlphas) {
						if (model.faceAlphas == null) {
							faceAlphas[faces] = 0;
						} else {
							faceAlphas[faces] = model.faceAlphas[face];
						}
					}

					if (hasFaceColours && model.faceColourOrTextureId != null) {
						faceColourOrTextureId[faces] = model.faceColourOrTextureId[face];
					}
					faces++;
				}

				for (int face = 0; face < model.texturedFaces; face++) {
					texturedFaceIndexX[texturedFaces] = model.texturedFaceIndexX[face] + offset;
					texturedFaceIndexY[texturedFaces] = model.texturedFaceIndexY[face] + offset;
					texturedFaceIndexZ[texturedFaces] = model.texturedFaceIndexZ[face] + offset;
					texturedFaces++;
				}

				i1 += model.texturedFaces;
			}
		}

		computeCircularBounds();
	}

	public void apply(int frame) {
		if (vertexGroups == null)
			return;
		else if (frame == -1)
			return;

		Frame animation = FrameLoader.lookup(frame);
		if (animation == null)
			return;

		FrameBase base = animation.getBase();
		centroidX = 0;
		centroidY = 0;
		centroidZ = 0;

		for (int transformation = 0; transformation < animation.getTransformationCount(); transformation++) {
			int group = animation.getTransformationIndex(transformation);
			transform(base.getTransformationType(group), base.getLabels(group), animation.getTransformX(transformation),
					animation.getTransformY(transformation), animation.getTransformZ(transformation));
		}
	}

	public void apply(int primaryId, int secondaryId, int[] interleaveOrder) {
		if (primaryId == -1)
			return;
		else if (interleaveOrder == null || secondaryId == -1) {
			apply(primaryId);
			return;
		}

		Frame primary = FrameLoader.lookup(primaryId);
		if (primary == null)
			return;

		Frame secondary = FrameLoader.lookup(secondaryId);
		if (secondary == null) {
			apply(primaryId);
			return;
		}

		FrameBase base = primary.getBase();
		centroidX = 0;
		centroidY = 0;
		centroidZ = 0;
		int index = 0;

		int next = interleaveOrder[index++];
		for (int transformation = 0; transformation < primary.getTransformationCount(); transformation++) {
			int group;
			for (group = primary
					.getTransformationIndex(transformation); group > next; next = interleaveOrder[index++]) {

			}
			if (group != next || base.getTransformationType(group) == 0) {
				transform(base.getTransformationType(group), base.getLabels(group),
						primary.getTransformX(transformation), primary.getTransformY(transformation),
						primary.getTransformZ(transformation));
			}
		}

		centroidX = 0;
		centroidY = 0;
		centroidZ = 0;
		index = 0;
		next = interleaveOrder[index++];

		for (int transformation = 0; transformation < secondary.getTransformationCount(); transformation++) {
			int group;
			for (group = secondary
					.getTransformationIndex(transformation); group > next; next = interleaveOrder[index++]) {

			}

			if (group == next || base.getTransformationType(group) == 0) {
				transform(base.getTransformationType(group), base.getLabels(group),
						secondary.getTransformX(transformation), secondary.getTransformY(transformation),
						secondary.getTransformZ(transformation));
			}
		}
	}

	private int extremeX = -1, extremeY = -1, extremeZ = -1;
	private int centerX = -1, centerY = -1, centerZ = -1;
	public void calculateExtreme(int orientation){
		if(this.extremeX == -1) {
			int var2 = 0;
			int var3 = 0;
			int var4 = 0;
			int var5 = 0;
			int var6 = 0;
			int var7 = 0;
			int var8 = Constants.COSINE[orientation];
			int var9 = Constants.SINE[orientation];

			for(int var10 = 0; var10 < this.vertices; ++var10) {
				int var11 = method3027(this.vertexX[var10], this.vertexZ[var10], var8, var9);
				int var12 = this.vertexY[var10];
				int var13 = method3028(this.vertexX[var10], this.vertexZ[var10], var8, var9);
				if(var11 < var2) {
					var2 = var11;
				}

				if(var11 > var5) {
					var5 = var11;
				}

				if(var12 < var3) {
					var3 = var12;
				}

				if(var12 > var6) {
					var6 = var12;
				}

				if(var13 < var4) {
					var4 = var13;
				}

				if(var13 > var7) {
					var7 = var13;
				}
			}

			this.centerX = (var5 + var2) / 2;
			this.centerY = (var6 + var3) / 2;
			this.centerZ = (var7 + var4) / 2;
			this.extremeX = (var5 - var2 + 1) / 2;
			this.extremeY = (var6 - var3 + 1) / 2;
			this.extremeZ = (var7 - var4 + 1) / 2;
			if(this.extremeX < 32) {
				this.extremeX = 32;
			}

			if(this.extremeZ < 32) {
				this.extremeZ = 32;
			}

			//TODO: Figure out what this does
			/*if(this.field1672) {
				this.extremeX += 8;
				this.extremeZ += 8;
			}*/

		}
	}

	private static final int method3027(int var0, int var1, int var2, int var3) {
		return var0 * var2 + var3 * var1 >> 16;
	}

	private static final int method3028(int var0, int var1, int var2, int var3) {
		return var2 * var1 - var3 * var0 >> 16;
	}

	public void computeBounds() {
		super.modelHeight = 0;
		boundingPlaneRadius = 0;
		minimumY = 0;
		minimumX = 0xf423f;
		maximumX = 0xfff0bdc1;
		maximumZ = 0xfffe7961;
		minimumZ = 0x1869f;

		for (int vertex = 0; vertex < vertices; vertex++) {
			int x = vertexX[vertex];
			int y = vertexY[vertex];
			int z = vertexZ[vertex];

			if (x < minimumX) {
				minimumX = x;
			}

			if (x > maximumX) {
				maximumX = x;
			}

			if (z < minimumZ) {
				minimumZ = z;
			}

			if (z > maximumZ) {
				maximumZ = z;
			}

			if (-y > super.modelHeight) {
				super.modelHeight = -y;
			}

			if (y > minimumY) {
				minimumY = y;
			}

			int radius = x * x + z * z;
			if (radius > boundingPlaneRadius) {
				boundingPlaneRadius = radius;
			}
		}

		boundingPlaneRadius = (int) Math.sqrt(boundingPlaneRadius);
		boundingCylinderRadius = (int) Math
				.sqrt(boundingPlaneRadius * boundingPlaneRadius + super.modelHeight * super.modelHeight);
		boundingSphereRadius = boundingCylinderRadius
				+ (int) Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + minimumY * minimumY);
	}

	public void computeCircularBounds() {
		super.modelHeight = 0;
		boundingPlaneRadius = 0;
		minimumY = 0;

		for (int vertex = 0; vertex < vertices; vertex++) {
			int x = vertexX[vertex];
			int y = vertexY[vertex];
			int z = vertexZ[vertex];

			if (-y > super.modelHeight) {
				super.modelHeight = -y;
			}

			if (y > minimumY) {
				minimumY = y;
			}

			int radius = x * x + z * z;
			if (radius > boundingPlaneRadius) {
				boundingPlaneRadius = radius;
			}
		}

		boundingPlaneRadius = (int) (Math.sqrt(boundingPlaneRadius) + 0.99D);
		boundingCylinderRadius = (int) (Math
				.sqrt(boundingPlaneRadius * boundingPlaneRadius + super.modelHeight * super.modelHeight) + 0.99D);
		boundingSphereRadius = boundingCylinderRadius
				+ (int) (Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + minimumY * minimumY) + 0.99D);
	}

	public void computeSphericalBounds() {
		super.modelHeight = 0;
		minimumY = 0;

		for (int vertex = 0; vertex < vertices; vertex++) {
			int y = vertexY[vertex];
			if (-y > super.modelHeight) {
				super.modelHeight = -y;
			}

			if (y > minimumY) {
				minimumY = y;
			}
		}

		boundingCylinderRadius = (int) (Math
				.sqrt(boundingPlaneRadius * boundingPlaneRadius + super.modelHeight * super.modelHeight)
				+ 0.98999999999999999D);
		boundingSphereRadius = boundingCylinderRadius
				+ (int) (Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + minimumY * minimumY)
						+ 0.98999999999999999D);
	}

	private final int findMatchingVertex(Mesh model, int vertex) {
		int matched = -1;
		int x = model.vertexX[vertex];
		int y = model.vertexY[vertex];
		int z = model.vertexZ[vertex];

		for (int index = 0; index < vertices; index++) {
			if (x == vertexX[index] && y == vertexY[index] && z == vertexZ[index]) {
				matched = index;
				break;
			}
		}

		if (matched == -1) {
			vertexX[vertices] = x;
			vertexY[vertices] = y;
			vertexZ[vertices] = z;

			if (model.vertexBones != null) {
				vertexBones[vertices] = model.vertexBones[vertex];
			}

			matched = vertices++;
		}

		return matched;
	}

	public void invert() {
		for (int vertex = 0; vertex < vertices; vertex++) {
			vertexZ[vertex] = -vertexZ[vertex];
		}

		for (int vertex = 0; vertex < faces; vertex++) {
			int x = faceIndexX[vertex];
			faceIndexX[vertex] = faceIndexZ[vertex];
			faceIndexZ[vertex] = x;
		}
	}

	public final void light(int lighting, int diffusion, int x, int y, int z, boolean immediateShading) {
		int length = (int) Math.sqrt(x * x + y * y + z * z);
		int k1 = diffusion * length >> 8;

		if (shadedFaceColoursX == null) {
			shadedFaceColoursX = new int[faces];
			shadedFaceColoursY = new int[faces];
			shadedFaceColoursZ = new int[faces];
		}

		if (super.normals == null) {
			super.normals = new VertexNormal[vertices];
			for (int index = 0; index < vertices; index++) {
				super.normals[index] = new VertexNormal();
			}
		}

		for (int face = 0; face < faces; face++) {
			int faceX = faceIndexX[face];
			int faceY = faceIndexY[face];
			int faceZ = faceIndexZ[face];
			int j3 = vertexX[faceY] - vertexX[faceX];
			int k3 = vertexY[faceY] - vertexY[faceX];
			int l3 = vertexZ[faceY] - vertexZ[faceX];
			int i4 = vertexX[faceZ] - vertexX[faceX];
			int j4 = vertexY[faceZ] - vertexY[faceX];
			int k4 = vertexZ[faceZ] - vertexZ[faceX];
			int dx = k3 * k4 - j4 * l3;
			int dy = l3 * i4 - k4 * j3;
			int dz;

			for (dz = j3 * j4 - i4 * k3; dx > 8192 || dy > 8192 || dz > 8192 || dx < -8192 || dy < -8192
					|| dz < -8192; dz >>= 1) {
				dx >>= 1;
				dy >>= 1;
			}

			int deltaLength = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
			if (deltaLength <= 0) {
				deltaLength = 1;
			}

			dx = dx * 256 / deltaLength;
			dy = dy * 256 / deltaLength;
			dz = dz * 256 / deltaLength;

			if (faceTypes == null || (faceTypes[face] & 1) == 0) {
				VertexNormal normal = super.normals[faceX];
				normal.setX(normal.getX() + dx);
				normal.setY(normal.getY() + dy);
				normal.setZ(normal.getZ() + dz);
				normal.setFaceCount(normal.getFaceCount() + 1);
				normal = super.normals[faceY];
				normal.setX(normal.getX() + dx);
				normal.setY(normal.getY() + dy);
				normal.setZ(normal.getZ() + dz);
				normal.setFaceCount(normal.getFaceCount() + 1);
				normal = super.normals[faceZ];
				normal.setX(normal.getX() + dx);
				normal.setY(normal.getY() + dy);
				normal.setZ(normal.getZ() + dz);
				normal.setFaceCount(normal.getFaceCount() + 1);
			} else {
				int l5 = lighting + (x * dx + y * dy + z * dz) / (k1 + k1 / 2);
				shadedFaceColoursX[face] = checkedLight(faceColourOrTextureId[face], l5, faceTypes[face]);
			}
		}

		if (immediateShading) {
			shade(lighting, k1, x, y, z);
		} else {
			normals = new VertexNormal[vertices];
			for (int index = 0; index < vertices; index++) {
				VertexNormal parent = super.normals[index];
				VertexNormal copied = normals[index] = new VertexNormal();
				copied.setX(parent.getX());
				copied.setY(parent.getY());
				copied.setZ(parent.getZ());
				copied.setFaceCount(parent.getFaceCount());
			}
		}

		if (immediateShading) {
			computeCircularBounds();
		} else {
			computeBounds();
		}
	}

	public void method464(Mesh model, boolean shareAlphas) {
		vertices = model.vertices;
		faces = model.faces;
		texturedFaces = model.texturedFaces;

		if (anIntArray1622.length < vertices) {
			anIntArray1622 = new int[vertices + 100];
			anIntArray1623 = new int[vertices + 100];
			anIntArray1624 = new int[vertices + 100];
		}

		vertexX = anIntArray1622;
		vertexY = anIntArray1623;
		vertexZ = anIntArray1624;
		for (int vertex = 0; vertex < vertices; vertex++) {
			vertexX[vertex] = model.vertexX[vertex];
			vertexY[vertex] = model.vertexY[vertex];
			vertexZ[vertex] = model.vertexZ[vertex];
		}

		if (shareAlphas) {
			faceAlphas = model.faceAlphas;
		} else {
			if (anIntArray1625.length < faces) {
				anIntArray1625 = new int[faces + 100];
			}
			faceAlphas = anIntArray1625;

			if (model.faceAlphas == null) {
				for (int index = 0; index < faces; index++) {
					faceAlphas[index] = 0;
				}
			} else {
				for (int index = 0; index < faces; index++) {
					faceAlphas[index] = model.faceAlphas[index];
				}
			}
		}

		faceTypes = model.faceTypes;
		faceColourOrTextureId = model.faceColourOrTextureId;
		facePriorities = model.facePriorities;
		priority = model.priority;
		faceGroups = model.faceGroups;
		vertexGroups = model.vertexGroups;
		faceIndexX = model.faceIndexX;
		faceIndexY = model.faceIndexY;
		faceIndexZ = model.faceIndexZ;
		shadedFaceColoursX = model.shadedFaceColoursX;
		shadedFaceColoursY = model.shadedFaceColoursY;
		shadedFaceColoursZ = model.shadedFaceColoursZ;
		texturedFaceIndexX = model.texturedFaceIndexX;
		texturedFaceIndexY = model.texturedFaceIndexY;
		texturedFaceIndexZ = model.texturedFaceIndexZ;
	}
	
	private static ObjectKey activeKey;

	private void renderFaces(GameRasterizer rasterizer, boolean flag, boolean multiTileFlag, ObjectKey key, int z) {
		for (int j = 0; j < boundingSphereRadius; j++) {
			rasterizer.depthListIndices[j] = 0;
		}
		
		activeKey = key;

		for (int face = 0; face < faces; face++) {
			if (faceTypes == null || faceTypes[face] != -1) {
				int indexX = faceIndexX[face];
				int indexY = faceIndexY[face];
				int indexZ = faceIndexZ[face];
				int i3 = rasterizer.vertexScreenX[indexX];
				int l3 = rasterizer.vertexScreenX[indexY];
				int k4 = rasterizer.vertexScreenX[indexZ];

				if (flag && (i3 == -5000 || l3 == -5000 || k4 == -5000)) {
					rasterizer.cullFacesOther[face] = true;
					int j5 = (rasterizer.vertexScreenZ[indexX] + rasterizer.vertexScreenZ[indexY] + rasterizer.vertexScreenZ[indexZ]) / 3
							+ boundingCylinderRadius;
					rasterizer.faceList[j5][rasterizer.depthListIndices[j5]++] = face;
				} else {
					if (key != null && multiTileFlag) {
						if (insideTriangle(mouseX, mouseY, rasterizer.vertexScreenY[indexX], rasterizer.vertexScreenY[indexY],
								rasterizer.vertexScreenY[indexZ], i3, l3, k4)) {


							boolean correctTool = (Options.currentTool.get() == ToolType.SELECT_OBJECT || Options.currentTool.get() == ToolType.DELETE_OBJECT);
							if (Options.currentHeight.get() == z && correctTool) {
								int type = key.getType();

								int selectionType = Options.objectSelectionType.get() - 1;
								if (selectionType == -1 || type == selectionType) {
									resourceIDTag[resourceCount++] = key;// ObjectKey.closestToCamera(Client.hoveredUID, key, Client.getSingleton().xCameraPos, Client.getSingleton().yCameraPos, Client.getSingleton().zCameraPos);
									if(Client.hoveredUID == null)
										Client.hoveredUID = key;
								}
							}

							multiTileFlag = false;
						} else if (Objects.equals(Client.hoveredUID, key)) {
							Client.hoveredUID = null;
						}
					}

					translucent = false;
					//translucent = false;
					if(key != null) {
						if (Options.currentTool.get() == ToolType.SELECT_OBJECT
							|| Options.currentTool.get() == ToolType.DELETE_OBJECT)
							translucent = Objects.equals(Client.hoveredUID, key) && z == Options.currentHeight.get();
						else
							translucent = false;
					}
					if ((i3 - l3) * (rasterizer.vertexScreenY[indexZ] - rasterizer.vertexScreenY[indexY])
							- (rasterizer.vertexScreenY[indexX] - rasterizer.vertexScreenY[indexY]) * (k4 - l3) > 0) {
						rasterizer.cullFacesOther[face] = false;
						rasterizer.cullFaces[face] = i3 < 0 || l3 < 0 || k4 < 0 || i3 > rasterizer.getMaxRight() || l3 > rasterizer.getMaxRight()
								|| k4 > rasterizer.getMaxRight();
						int k5 = (rasterizer.vertexScreenZ[indexX] + rasterizer.vertexScreenZ[indexY] + rasterizer.vertexScreenZ[indexZ]) / 3
								+ boundingCylinderRadius;
						if(k5 >= 0 && k5 < rasterizer.faceList.length)
						rasterizer.faceList[k5][rasterizer.depthListIndices[k5]++] = face;
					}
				}
			}
		}

		if (facePriorities == null) {
			for (int i1 = boundingSphereRadius - 1; i1 >= 0; i1--) {
				int l1 = rasterizer.depthListIndices[i1];
				if (l1 > 0) {
					int[] ai = rasterizer.faceList[i1];
					for (int j3 = 0; j3 < l1; j3++) {
						renderFace(rasterizer, ai[j3]);
					}
				}
			}

			return;
		}
		for (int j1 = 0; j1 < 12; j1++) {
			rasterizer.anIntArray1673[j1] = 0;
			rasterizer.anIntArray1677[j1] = 0;
		}

		for (int i2 = boundingSphereRadius - 1; i2 >= 0; i2--) {
			int k2 = rasterizer.depthListIndices[i2];
			if (k2 > 0) {
				int[] ai1 = rasterizer.faceList[i2];
				for (int i4 = 0; i4 < k2; i4++) {
					int l4 = ai1[i4];
					int l5 = facePriorities[l4];
					int j6 = rasterizer.anIntArray1673[l5]++;
					rasterizer.anIntArrayArray1674[l5][j6] = l4;
					if (l5 < 10) {
						rasterizer.anIntArray1677[l5] += i2;
					} else if (l5 == 10) {
						rasterizer.anIntArray1675[j6] = i2;
					} else {
						rasterizer.anIntArray1676[j6] = i2;
					}
				}

			}
		}

		int l2 = 0;
		if (rasterizer.anIntArray1673[1] > 0 || rasterizer.anIntArray1673[2] > 0) {
			l2 = (rasterizer.anIntArray1677[1] + rasterizer.anIntArray1677[2]) / (rasterizer.anIntArray1673[1] + rasterizer.anIntArray1673[2]);
		}
		int k3 = 0;
		if (rasterizer.anIntArray1673[3] > 0 || rasterizer.anIntArray1673[4] > 0) {
			k3 = (rasterizer.anIntArray1677[3] + rasterizer.anIntArray1677[4]) / (rasterizer.anIntArray1673[3] + rasterizer.anIntArray1673[4]);
		}
		int j4 = 0;
		if (rasterizer.anIntArray1673[6] > 0 || rasterizer.anIntArray1673[8] > 0) {
			j4 = (rasterizer.anIntArray1677[6] + rasterizer.anIntArray1677[8]) / (rasterizer.anIntArray1673[6] + rasterizer.anIntArray1673[8]);
		}
		int i6 = 0;
		int k6 = rasterizer.anIntArray1673[10];
		int[] ai2 = rasterizer.anIntArrayArray1674[10];
		int[] ai3 = rasterizer.anIntArray1675;
		if (i6 == k6) {
			i6 = 0;
			k6 = rasterizer.anIntArray1673[11];
			ai2 = rasterizer.anIntArrayArray1674[11];
			ai3 = rasterizer.anIntArray1676;
		}
		int i5;
		if (i6 < k6) {
			i5 = ai3[i6];
		} else {
			i5 = -1000;
		}
		for (int l6 = 0; l6 < 10; l6++) {
			while (l6 == 0 && i5 > l2) {
				renderFace(rasterizer, ai2[i6++]);
				if (i6 == k6 && ai2 != rasterizer.anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = rasterizer.anIntArray1673[11];
					ai2 = rasterizer.anIntArrayArray1674[11];
					ai3 = rasterizer.anIntArray1676;
				}
				if (i6 < k6) {
					i5 = ai3[i6];
				} else {
					i5 = -1000;
				}
			}
			while (l6 == 3 && i5 > k3) {
				renderFace(rasterizer, ai2[i6++]);
				if (i6 == k6 && ai2 != rasterizer.anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = rasterizer.anIntArray1673[11];
					ai2 = rasterizer.anIntArrayArray1674[11];
					ai3 = rasterizer.anIntArray1676;
				}
				if (i6 < k6) {
					i5 = ai3[i6];
				} else {
					i5 = -1000;
				}
			}
			while (l6 == 5 && i5 > j4) {
				renderFace(rasterizer, ai2[i6++]);
				if (i6 == k6 && ai2 != rasterizer.anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = rasterizer.anIntArray1673[11];
					ai2 = rasterizer.anIntArrayArray1674[11];
					ai3 = rasterizer.anIntArray1676;
				}
				if (i6 < k6) {
					i5 = ai3[i6];
				} else {
					i5 = -1000;
				}
			}
			int i7 = rasterizer.anIntArray1673[l6];
			int[] ai4 = rasterizer.anIntArrayArray1674[l6];
			for (int j7 = 0; j7 < i7; j7++) {
				renderFace(rasterizer, ai4[j7]);
			}

		}

		while (i5 != -1000) {
			renderFace(rasterizer, ai2[i6++]);
			if (i6 == k6 && ai2 != rasterizer.anIntArrayArray1674[11]) {
				i6 = 0;
				ai2 = rasterizer.anIntArrayArray1674[11];
				k6 = rasterizer.anIntArray1673[11];
				ai3 = rasterizer.anIntArray1676;
			}
			if (i6 < k6) {
				i5 = ai3[i6];
			} else {
				i5 = -1000;
			}
		}
	}

	private void renderFace(GameRasterizer rasterizer, int index) {
		if (rasterizer.cullFacesOther[index]) {
			method485(rasterizer, index);
			return;
		}
		int faceX = faceIndexX[index];
		int faceY = faceIndexY[index];
		int faceZ = faceIndexZ[index];
		rasterizer.restrictEdges = rasterizer.cullFaces[index];
		if (selected) {
			rasterizer.currentAlpha = translucent ? 100 : 50;
		}
		if (translucent) {
			rasterizer.currentAlpha = 140;
		} else if (!selected) {
			if (faceAlphas == null) {
				rasterizer.currentAlpha = 0;
			} else {
				rasterizer.currentAlpha = faceAlphas[index];
			}
		}
		int type;
		if (faceTypes == null) {
			type = 0;
		} else {
			type = faceTypes[index] & 3;
		}
		boolean ignoreTextures = translucent || selected || texturedFaceIndexX == null;

		if (type == 0 && !ignoreTextures) {
			rasterizer.drawShadedTriangle(rasterizer.vertexScreenY[faceX], rasterizer.vertexScreenY[faceY], rasterizer.vertexScreenY[faceZ], rasterizer.vertexScreenX[faceX],
					rasterizer.vertexScreenX[faceY], rasterizer.vertexScreenX[faceZ], shadedFaceColoursX[index], shadedFaceColoursY[index], shadedFaceColoursZ[index]);
		} else if (type == 1 || ignoreTextures) {
			int colour = selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedFaceColoursX[index]];
			rasterizer.drawShadedTriangle(rasterizer.vertexScreenY[faceX], rasterizer.vertexScreenY[faceY], rasterizer.vertexScreenY[faceZ], rasterizer.vertexScreenX[faceX],
					rasterizer.vertexScreenX[faceY], rasterizer.vertexScreenX[faceZ], colour);
		} else if (type == 2 || type == 3) {
			
			try {
			int k1 = faceTypes[index] >> 2;
			int texFaceX = texturedFaceIndexX[k1];
			int texFaceY = texturedFaceIndexY[k1];
			int texFaceZ = texturedFaceIndexZ[k1];

			int colourX = shadedFaceColoursX[index];
			int colourY = shadedFaceColoursX[index];
			int colourZ = shadedFaceColoursX[index];

			if(type == 2) {
				colourY =  shadedFaceColoursY[index];
				colourZ =  shadedFaceColoursZ[index];
			}

			rasterizer.drawTexturedTriangle(rasterizer.vertexScreenY[faceX], rasterizer.vertexScreenY[faceY], rasterizer.vertexScreenY[faceZ], rasterizer.vertexScreenX[faceX],
					rasterizer.vertexScreenX[faceY], rasterizer.vertexScreenX[faceZ], colourX, colourY, colourZ,
					rasterizer.camera_vertex_x[texFaceX], rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ], rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY],
					rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX], rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ], faceColourOrTextureId[index]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}

	private void method485(GameRasterizer rasterizer, int i) {

		boolean ignoreTextures = translucent || selected || texturedFaceIndexX == null;

		int viewX = rasterizer.viewCenter.getX();
		int viewY = rasterizer.viewCenter.getY();
		int l = 0;
		int i1 = faceIndexX[i];
		int j1 = faceIndexY[i];
		int k1 = faceIndexZ[i];
		int l1 = rasterizer.camera_vertex_z[i1];
		int i2 = rasterizer.camera_vertex_z[j1];
		int j2 = rasterizer.camera_vertex_z[k1];
		if (l1 >= 50) {
			rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[i1];
			rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[i1];
			rasterizer.anIntArray1680[l++] = shadedFaceColoursX[i];
		} else {
			int k2 = rasterizer.camera_vertex_x[i1];
			int k3 = rasterizer.camera_vertex_y[i1];
			int k4 = shadedFaceColoursX[i];
			if (j2 >= 50) {
				int k5 = (50 - l1) * Constants.LIGHT_DECAY[j2 - l1];
				rasterizer.anIntArray1678[l] = viewX + (k2 + ((rasterizer.camera_vertex_x[k1] - k2) * k5 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (k3 + ((rasterizer.camera_vertex_y[k1] - k3) * k5 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = k4 + ((shadedFaceColoursZ[i] - k4) * k5 >> 16);
			}
			if (i2 >= 50) {
				int l5 = (50 - l1) * Constants.LIGHT_DECAY[i2 - l1];
				rasterizer.anIntArray1678[l] = viewX + (k2 + ((rasterizer.camera_vertex_x[j1] - k2) * l5 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (k3 + ((rasterizer.camera_vertex_y[j1] - k3) * l5 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = k4 + ((shadedFaceColoursY[i] - k4) * l5 >> 16);
			}
		}
		if (i2 >= 50) {
			rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[j1];
			rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[j1];
			rasterizer.anIntArray1680[l++] = shadedFaceColoursY[i];
		} else {
			int l2 = rasterizer.camera_vertex_x[j1];
			int l3 = rasterizer.camera_vertex_y[j1];
			int l4 = shadedFaceColoursY[i];
			if (l1 >= 50) {
				int i6 = (50 - i2) * Constants.LIGHT_DECAY[l1 - i2];
				rasterizer.anIntArray1678[l] = viewX + (l2 + ((rasterizer.camera_vertex_x[i1] - l2) * i6 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (l3 + ((rasterizer.camera_vertex_y[i1] - l3) * i6 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = l4 + ((shadedFaceColoursX[i] - l4) * i6 >> 16);
			}
			if (j2 >= 50) {
				int j6 = (50 - i2) * Constants.LIGHT_DECAY[j2 - i2];
				rasterizer.anIntArray1678[l] = viewX + (l2 + ((rasterizer.camera_vertex_x[k1] - l2) * j6 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (l3 + ((rasterizer.camera_vertex_y[k1] - l3) * j6 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = l4 + ((shadedFaceColoursZ[i] - l4) * j6 >> 16);
			}
		}
		if (j2 >= 50) {
			rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[k1];
			rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[k1];
			rasterizer.anIntArray1680[l++] = shadedFaceColoursZ[i];
		} else {
			int i3 = rasterizer.camera_vertex_x[k1];
			int i4 = rasterizer.camera_vertex_y[k1];
			int i5 = shadedFaceColoursZ[i];
			if (i2 >= 50) {
				int k6 = (50 - j2) * Constants.LIGHT_DECAY[i2 - j2];
				rasterizer.anIntArray1678[l] = viewX + (i3 + ((rasterizer.camera_vertex_x[j1] - i3) * k6 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (i4 + ((rasterizer.camera_vertex_y[j1] - i4) * k6 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = i5 + ((shadedFaceColoursY[i] - i5) * k6 >> 16);
			}
			if (l1 >= 50) {
				int l6 = (50 - j2) * Constants.LIGHT_DECAY[l1 - j2];
				rasterizer.anIntArray1678[l] = viewX + (i3 + ((rasterizer.camera_vertex_x[i1] - i3) * l6 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (i4 + ((rasterizer.camera_vertex_y[i1] - i4) * l6 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = i5 + ((shadedFaceColoursX[i] - i5) * l6 >> 16);
			}
		}
		int j3 = rasterizer.anIntArray1678[0];
		int j4 = rasterizer.anIntArray1678[1];
		int j5 = rasterizer.anIntArray1678[2];
		int i7 = rasterizer.anIntArray1679[0];
		int j7 = rasterizer.anIntArray1679[1];
		int k7 = rasterizer.anIntArray1679[2];
		if ((j3 - j4) * (k7 - j7) - (i7 - j7) * (j5 - j4) > 0) {
			rasterizer.restrictEdges = false;
			if (l == 3) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > rasterizer.getMaxRight() || j4 > rasterizer.getMaxRight()
						|| j5 > rasterizer.getMaxRight()) {
					rasterizer.restrictEdges = true;
				}
				int l7;
				if (faceTypes == null) {
					l7 = 0;
				} else {
					l7 = faceTypes[i] & 3;
				}

				if (l7 == 0 && !ignoreTextures) {
					rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
							rasterizer.anIntArray1680[2]);
				} else if (l7 == 1 || ignoreTextures) {
					rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5,
							selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedFaceColoursX[i]]);
				} else if (l7 == 2) {
					int j8 = faceTypes[i] >> 2;
					int k9 = texturedFaceIndexX[j8];
					int k10 = texturedFaceIndexY[j8];
					int k11 = texturedFaceIndexZ[j8];
					rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
							rasterizer.anIntArray1680[2], rasterizer.camera_vertex_x[k9], rasterizer.camera_vertex_x[k10], rasterizer.camera_vertex_x[k11],
							rasterizer.camera_vertex_y[k9], rasterizer.camera_vertex_y[k10], rasterizer.camera_vertex_y[k11], rasterizer.camera_vertex_z[k9],
							rasterizer.camera_vertex_z[k10], rasterizer.camera_vertex_z[k11], faceColourOrTextureId[i]);
				} else if (l7 == 3) {
					int k8 = faceTypes[i] >> 2;
					int l9 = texturedFaceIndexX[k8];
					int l10 = texturedFaceIndexY[k8];
					int l11 = texturedFaceIndexZ[k8];
					rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, shadedFaceColoursX[i], shadedFaceColoursX[i],
							shadedFaceColoursX[i], rasterizer.camera_vertex_x[l9], rasterizer.camera_vertex_x[l10], rasterizer.camera_vertex_x[l11],
							rasterizer.camera_vertex_y[l9], rasterizer.camera_vertex_y[l10], rasterizer.camera_vertex_y[l11], rasterizer.camera_vertex_z[l9],
							rasterizer.camera_vertex_z[l10], rasterizer.camera_vertex_z[l11], faceColourOrTextureId[i]);
				}
			}
			if (l == 4) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > rasterizer.getMaxRight() || j4 > rasterizer.getMaxRight() || j5 > rasterizer.getMaxRight()
						|| rasterizer.anIntArray1678[3] < 0 || rasterizer.anIntArray1678[3] > rasterizer.getMaxRight()) {
					rasterizer.restrictEdges = true;
				}
				int i8;
				if (faceTypes == null) {
					i8 = 0;
				} else {
					i8 = faceTypes[i] & 3;
				}
				if (i8 == 0 && !ignoreTextures) {
					rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
							rasterizer.anIntArray1680[2]);
					rasterizer.drawShadedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
							rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[2], rasterizer.anIntArray1680[3]);
					return;
				} else if (i8 == 1 || ignoreTextures) {
					int l8 =  selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedFaceColoursX[i]];
					rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, l8);
					rasterizer.drawShadedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3], l8);
					return;
				} else if (i8 == 2) {
					int i9 = faceTypes[i] >> 2;
					int i10 = texturedFaceIndexX[i9];
					int i11 = texturedFaceIndexY[i9];
					int i12 = texturedFaceIndexZ[i9];
					rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
							rasterizer.anIntArray1680[2], rasterizer.camera_vertex_x[i10], rasterizer.camera_vertex_x[i11], rasterizer.camera_vertex_x[i12],
							rasterizer.camera_vertex_y[i10], rasterizer.camera_vertex_y[i11], rasterizer.camera_vertex_y[i12], rasterizer.camera_vertex_z[i10],
							rasterizer.camera_vertex_z[i11], rasterizer.camera_vertex_z[i12], faceColourOrTextureId[i]);
					rasterizer.drawTexturedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
							rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[2], rasterizer.anIntArray1680[3], rasterizer.camera_vertex_x[i10],
							rasterizer.camera_vertex_x[i11], rasterizer.camera_vertex_x[i12], rasterizer.camera_vertex_y[i10], rasterizer.camera_vertex_y[i11],
							rasterizer.camera_vertex_y[i12], rasterizer.camera_vertex_z[i10], rasterizer.camera_vertex_z[i11], rasterizer.camera_vertex_z[i12],
							faceColourOrTextureId[i]);
					return;
				} else if (i8 == 3) {
					int j9 = faceTypes[i] >> 2;
					int j10 = texturedFaceIndexX[j9];
					int j11 = texturedFaceIndexY[j9];
					int j12 = texturedFaceIndexZ[j9];
					rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, shadedFaceColoursX[i], shadedFaceColoursX[i],
							shadedFaceColoursX[i], rasterizer.camera_vertex_x[j10], rasterizer.camera_vertex_x[j11], rasterizer.camera_vertex_x[j12],
							rasterizer.camera_vertex_y[j10], rasterizer.camera_vertex_y[j11], rasterizer.camera_vertex_y[j12], rasterizer.camera_vertex_z[j10],
							rasterizer.camera_vertex_z[j11], rasterizer.camera_vertex_z[j12], faceColourOrTextureId[i]);
					rasterizer.drawTexturedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
							shadedFaceColoursX[i], shadedFaceColoursX[i], shadedFaceColoursX[i], rasterizer.camera_vertex_x[j10],
							rasterizer.camera_vertex_x[j11], rasterizer.camera_vertex_x[j12], rasterizer.camera_vertex_y[j10], rasterizer.camera_vertex_y[j11],
							rasterizer.camera_vertex_y[j12], rasterizer.camera_vertex_z[j10], rasterizer.camera_vertex_z[j11], rasterizer.camera_vertex_z[j12],
							faceColourOrTextureId[i]);
				}
			}
		}
	}

	public void pitch(int theta) {
		int sin = Constants.SINE[theta];
		int cos = Constants.COSINE[theta];

		for (int vertex = 0; vertex < vertices; vertex++) {
			int y = vertexY[vertex] * cos - vertexZ[vertex] * sin >> 16;
			vertexZ[vertex] = vertexY[vertex] * sin + vertexZ[vertex] * cos >> 16;
			vertexY[vertex] = y;
		}
	}

	public void prepareSkeleton() {
		if (vertexBones != null) {
			int[] sizes = new int[256];
			int maximumBoneId = 0;

			for (int vertex = 0; vertex < vertices; vertex++) {
				int bone = vertexBones[vertex];
				sizes[bone]++;

				if (bone > maximumBoneId) {
					maximumBoneId = bone;
				}
			}

			vertexGroups = new int[maximumBoneId + 1][];
			for (int index = 0; index <= maximumBoneId; index++) {
				vertexGroups[index] = new int[sizes[index]];
				sizes[index] = 0;
			}

			for (int index = 0; index < vertices; index++) {
				int bone = vertexBones[index];
				vertexGroups[bone][sizes[bone]++] = index;
			}

			vertexBones = null;
		}

		if (faceSkin != null) {
			int[] sizes = new int[256];
			int count = 0;

			for (int index = 0; index < faces; index++) {
				int skin = faceSkin[index];
				sizes[skin]++;

				if (skin > count) {
					count = skin;
				}
			}

			faceGroups = new int[count + 1][];
			for (int index = 0; index <= count; index++) {
				faceGroups[index] = new int[sizes[index]];
				sizes[index] = 0;
			}

			for (int index = 0; index < faces; index++) {
				int skin = faceSkin[index];
				faceGroups[skin][sizes[skin]++] = index;
			}

			faceSkin = null;
		}
	}

	public void recolour(int oldColour, int newColour) {
		for (int index = 0; index < faces; index++) {
			if (faceColourOrTextureId[index] == oldColour) {
				faceColourOrTextureId[index] = newColour;
			}
		}
	}

	public void render(GameRasterizer rasterizer, int rotationX, int roll, int yaw, int pitch, int transX, int transY, int transZ, int plane) {
		int viewX = rasterizer.viewCenter.getX();
		int viewY = rasterizer.viewCenter.getY();
		int j2 = Constants.SINE[rotationX];
		int k2 = Constants.COSINE[rotationX];
		int l2 = Constants.SINE[roll];
		int i3 = Constants.COSINE[roll];
		int j3 = Constants.SINE[yaw];
		int k3 = Constants.COSINE[yaw];
		int sinXWorld = Constants.SINE[pitch];
		int cosXWorld = Constants.COSINE[pitch];
		int j4 = transY * sinXWorld + transZ * cosXWorld >> 16;
		for (int k4 = 0; k4 < vertices; k4++) {
			int x = vertexX[k4];
			int y = vertexY[k4];
			int z = vertexZ[k4];
			if (yaw != 0) {
				int k5 = y * j3 + x * k3 >> 16;
				y = y * k3 - x * j3 >> 16;
				x = k5;
			}
			if (rotationX != 0) {
				int l5 = y * k2 - z * j2 >> 16;
				z = y * j2 + z * k2 >> 16;
				y = l5;
			}
			if (roll != 0) {
				int i6 = z * l2 + x * i3 >> 16;
				z = z * i3 - x * l2 >> 16;
				x = i6;
			}
			x += transX;
			y += transY;
			z += transZ;
			int j6 = y * cosXWorld - z * sinXWorld >> 16;
			z = y * sinXWorld + z * cosXWorld >> 16;
			y = j6;
			rasterizer.vertexScreenZ[k4] = z - j4;
			rasterizer.vertexScreenX[k4] = viewX + (x << 9) / z;
			rasterizer.vertexScreenY[k4] = viewY + (y << 9) / z;
			if (texturedFaces > 0) {
				rasterizer.camera_vertex_x[k4] = x;
				rasterizer.camera_vertex_y[k4] = y;
				rasterizer.camera_vertex_z[k4] = z;
			}
		}

		try {
			renderFaces(rasterizer, false, false, null, plane);
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}

	@Override
	public void render(GameRasterizer rasterizer, int x, int y, int orientation, int ySine, int yCosine, int xSine, int xCosine, int height, ObjectKey key, int z) {
		int j2 = y * xCosine - x * xSine >> 16;
		int k2 = height * ySine + j2 * yCosine >> 16;
		int l2 = boundingPlaneRadius * yCosine >> 16;
		int i3 = k2 + l2;

		if (i3 <= 50 || k2 >= 6500)
			return;

		int j3 = y * xSine + x * xCosine >> 16;
		int sceneLowerX = j3 - boundingPlaneRadius << 9;
		if (sceneLowerX / i3 >= rasterizer.getCentreX())
			return;

		int sceneMaximumX = j3 + boundingPlaneRadius << 9;
		if (sceneMaximumX / i3 <= -rasterizer.getCentreX())
			return;

		int i4 = height * yCosine - j2 * ySine >> 16;
		int j4 = boundingPlaneRadius * ySine >> 16;
		int sceneMaximumY = i4 + j4 << 9;

		if (sceneMaximumY / i3 <= -rasterizer.getCentreY())
			return;

		int l4 = j4 + (super.modelHeight * yCosine >> 16);
		int sceneLowerY = i4 - l4 << 9;
		if (sceneLowerY / i3 >= rasterizer.getCentreY())
			return;

		int j5 = l2 + (super.modelHeight * ySine >> 16);
		boolean flag = false;
		if (k2 - j5 <= 50) {
			flag = true;
		}

		boolean flag1 = false;
		if (key != null && aBoolean1684 || true) {
			int k5 = k2 - l2;
			if (k5 <= 50) {
				k5 = 50;
			}

			if (j3 > 0) {
				sceneLowerX /= i3;
				sceneMaximumX /= k5;
			} else {
				sceneMaximumX /= i3;
				sceneLowerX /= k5;
			}

			if (i4 > 0) {
				sceneLowerY /= i3;
				sceneMaximumY /= k5;
			} else {
				sceneMaximumY /= i3;
				sceneLowerY /= k5;
			}

			int mouseSceneX = mouseX - rasterizer.viewCenter.getX();
			int mouseSceneY = mouseY - rasterizer.viewCenter.getY();

			if (mouseSceneX > sceneLowerX && mouseSceneX < sceneMaximumX && mouseSceneY > sceneLowerY && mouseSceneY < sceneMaximumY) {
				if (fitsOnSingleSquare) {
					resourceIDTag[resourceCount++] = key;
					if(Client.hoveredUID == null)
						Client.hoveredUID = key;
				} else {
					flag1 = true;
				}
			}
		}

		int viewX = rasterizer.viewCenter.getX();
		int viewY = rasterizer.viewCenter.getY();
		int sine = 0;
		int cosine = 0;

		if (orientation != 0) {
			sine = Constants.SINE[orientation];
			cosine = Constants.COSINE[orientation];
		}

		for (int vertex = 0; vertex < vertices; vertex++) {
			int xVertex = vertexX[vertex];
			int yVertex = vertexY[vertex];
			int zVertex = vertexZ[vertex];
			if (orientation != 0) {
				int j8 = zVertex * sine + xVertex * cosine >> 16;
				zVertex = zVertex * cosine - xVertex * sine >> 16;
				xVertex = j8;
			}

			xVertex += x;
			yVertex += height;
			zVertex += y;
			int k8 = zVertex * xSine + xVertex * xCosine >> 16;
			zVertex = zVertex * xCosine - xVertex * xSine >> 16;
			xVertex = k8;
			k8 = yVertex * yCosine - zVertex * ySine >> 16;
			zVertex = yVertex * ySine + zVertex * yCosine >> 16;
			yVertex = k8;
			rasterizer.vertexScreenZ[vertex] = zVertex - k2;

			if (zVertex >= getZVertexMax()) {
				rasterizer.vertexScreenX[vertex] = viewX + (xVertex << 9) / zVertex;
				rasterizer.vertexScreenY[vertex] = viewY + (yVertex << 9) / zVertex;
			} else {
				rasterizer.vertexScreenX[vertex] = -5000;
				flag = true;
			}

			if (flag || texturedFaces > 0) {
				rasterizer.camera_vertex_x[vertex] = xVertex;
				rasterizer.camera_vertex_y[vertex] = yVertex;
				rasterizer.camera_vertex_z[vertex] = zVertex;
			}
		}

		try {
			renderFaces(rasterizer, flag, flag1, key, z);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void rotateClockwise() {
		for (int index = 0; index < vertices; index++) {
			int x = vertexX[index];
			vertexX[index] = vertexZ[index];
			vertexZ[index] = -x;
		}
	}

	public void offsetVertices(int x, int y, int z) {
		for (int index = 0; index < vertices; index++) {
			vertexX[index] += x;
			vertexX[index] += y;
			vertexZ[index] += z;
		}
	}

	public void scale(int x, int y, int z) {
		for (int vertex = 0; vertex < vertices; vertex++) {
			vertexX[vertex] = vertexX[vertex] * x / 128;
			vertexY[vertex] = vertexY[vertex] * z / 128;
			vertexZ[vertex] = vertexZ[vertex] * y / 128;
		}
	}

    public void scale2(int i) {
        for (int i1 = 0; i1 < vertices; i1++) {
            vertexX[i1] = vertexX[i1] / i;
            vertexY[i1] = vertexY[i1] / i;
            vertexZ[i1] = vertexZ[i1] / i;
        }
    }


	public final void shade(int lighting, int j, int x, int y, int z) {
		for (int face = 0; face < faces; face++) {
			int indexX = faceIndexX[face];
			int indexY = faceIndexY[face];
			int indexZ = faceIndexZ[face];

			if (faceTypes == null) {
				int colour = faceColourOrTextureId[face];
				VertexNormal normal = super.normals[indexX];

				int light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getFaceCount());
				shadedFaceColoursX[face] = checkedLight(colour, light, 0);

				normal = super.normals[indexY];
				light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getFaceCount());
				shadedFaceColoursY[face] = checkedLight(colour, light, 0);

				normal = super.normals[indexZ];
				light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getFaceCount());
				shadedFaceColoursZ[face] = checkedLight(colour, light, 0);
			} else if ((faceTypes[face] & 1) == 0) {
				int colour = faceColourOrTextureId[face];
				int point = faceTypes[face];

				VertexNormal normal = super.normals[indexX];
				int light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getFaceCount());
				shadedFaceColoursX[face] = checkedLight(colour, light, point);
				normal = super.normals[indexY];

				light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getFaceCount());
				shadedFaceColoursY[face] = checkedLight(colour, light, point);
				normal = super.normals[indexZ];

				light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getFaceCount());
				shadedFaceColoursZ[face] = checkedLight(colour, light, point);
			}
		}

		super.normals = null;
		normals = null;
		vertexBones = null;
		faceSkin = null;
		if (faceTypes != null) {
			for (int index = 0; index < faces; index++) {
				if ((faceTypes[index] & 2) == 2)
					return;
			}
		}

		faceColourOrTextureId = null;
	}

	private void transform(int transformation, int[] groups, int dx, int dy, int dz) {
		int count = groups.length;
		if (transformation == FrameConstants.CENTROID_TRANSFORMATION) {
			int vertices = 0;
			centroidX = 0;
			centroidY = 0;
			centroidZ = 0;

			for (int index = 0; index < count; index++) {
				int group = groups[index];
				if (group < vertexGroups.length) {
					for (int vertex : vertexGroups[group]) {
						centroidX += vertexX[vertex];
						centroidY += vertexY[vertex];
						centroidZ += vertexZ[vertex];
						vertices++;
					}
				}
			}

			if (vertices > 0) {
				centroidX = centroidX / vertices + dx;
				centroidY = centroidY / vertices + dy;
				centroidZ = centroidZ / vertices + dz;
			} else {
				centroidX = dx;
				centroidY = dy;
				centroidZ = dz;
			}
		} else if (transformation == FrameConstants.POSITION_TRANSFORMATION) {
			for (int index = 0; index < count; index++) {
				int group = groups[index];

				if (group < vertexGroups.length) {
					for (int vertex : vertexGroups[group]) {
						vertexX[vertex] += dx;
						vertexY[vertex] += dy;
						vertexZ[vertex] += dz;
					}
				}
			}
		} else if (transformation == FrameConstants.ROTATION_TRANSFORMATION) {
			for (int index = 0; index < count; index++) {
				int group = groups[index];

				if (group < vertexGroups.length) {
					for (int vertex : vertexGroups[group]) {
						vertexX[vertex] -= centroidX;
						vertexY[vertex] -= centroidY;
						vertexZ[vertex] -= centroidZ;
						int pitch = (dx & 0xFF) * 8;
						int roll = (dy & 0xFF) * 8;
						int yaw = (dz & 0xFF) * 8;

						if (yaw != 0) {
							int sin = Constants.SINE[yaw];
							int cos = Constants.COSINE[yaw];
							int x = vertexY[vertex] * sin + vertexX[vertex] * cos >> 16;
							vertexY[vertex] = vertexY[vertex] * cos - vertexX[vertex] * sin >> 16;
							vertexX[vertex] = x;
						}

						if (pitch != 0) {
							int sin = Constants.SINE[pitch];
							int cos = Constants.COSINE[pitch];
							int y = vertexY[vertex] * cos - vertexZ[vertex] * sin >> 16;
							vertexZ[vertex] = vertexY[vertex] * sin + vertexZ[vertex] * cos >> 16;
							vertexY[vertex] = y;
						}

						if (roll != 0) {
							int sin = Constants.SINE[roll];
							int cos = Constants.COSINE[roll];
							int x = vertexZ[vertex] * sin + vertexX[vertex] * cos >> 16;
							vertexZ[vertex] = vertexZ[vertex] * cos - vertexX[vertex] * sin >> 16;
							vertexX[vertex] = x;
						}

						vertexX[vertex] += centroidX;
						vertexY[vertex] += centroidY;
						vertexZ[vertex] += centroidZ;
					}
				}
			}
		} else if (transformation == FrameConstants.SCALE_TRANSFORMATION) {
			for (int index = 0; index < count; index++) {
				int group = groups[index];

				if (group < vertexGroups.length) {
					for (int vertex : vertexGroups[group]) {
						vertexX[vertex] -= centroidX;
						vertexY[vertex] -= centroidY;
						vertexZ[vertex] -= centroidZ;

						vertexX[vertex] = vertexX[vertex] * dx / 128;
						vertexY[vertex] = vertexY[vertex] * dy / 128;
						vertexZ[vertex] = vertexZ[vertex] * dz / 128;

						vertexX[vertex] += centroidX;
						vertexY[vertex] += centroidY;
						vertexZ[vertex] += centroidZ;
					}
				}
			}
		} else if (transformation == FrameConstants.ALPHA_TRANSFORMATION && faceGroups != null && faceAlphas != null) {
			for (int index = 0; index < count; index++) {
				int group = groups[index];

				if (group < faceGroups.length) {
					for (int face : faceGroups[group]) {
						faceAlphas[face] += dx * 8;

						if (faceAlphas[face] < 0) {
							faceAlphas[face] = 0;
						} else if (faceAlphas[face] > 255) {
							faceAlphas[face] = 255;
						}
					}
				}
			}
		}
	}

	public void translate(int x, int y, int z) {
		for (int vertex = 0; vertex < vertices; vertex++) {
			vertexX[vertex] += x;
			vertexY[vertex] += y;
			vertexZ[vertex] += z;
		}
	}

    public void convertTexturesTo317(short[] textureIds, int[] texa, int[] texb, int[] texc, boolean osrs) {
        int set = 0;
        int set2 = 0;
        int max = TextureLoader.instance.count();
        if (textureIds != null) {
            texturedFaceIndexX = new int[faces];
            texturedFaceIndexY = new int[faces];
            texturedFaceIndexZ = new int[faces];

            for (int i = 0; i < faces; i++) {
                if (textureIds[i] == -1 && this.faceTypes[i] == 2) {
                    this.faceColourOrTextureId[i] = 65535;
                    faceTypes[i] = 0;
                }
                if (textureIds[i] >= max || textureIds[i] < 0 || textureIds[i] == 39) {
                	faceTypes[i] = 0;
                    continue;
                }
                faceTypes[i] = 2 + set2;
                set2 += 4;
                int a = this.faceIndexX[i];
                int b = faceIndexY[i];
                int c = faceIndexZ[i];
                faceColourOrTextureId[i] = textureIds[i];

                int texture_type = -1;
                if (this.texture_coordinates != null) {
                    texture_type = texture_coordinates[i] & 0xff;
                    if (texture_type != 0xff)
                        if (texa[texture_type] >= 4096 || texb[texture_type] >= 4096
                                || texc[texture_type] >= 4096)
                            texture_type = -1;
                }
                if (texture_type == 0xff)
                    texture_type = -1;

                texturedFaceIndexX[set] = texture_type == -1 ? a : texa[texture_type];
                texturedFaceIndexY[set] = texture_type == -1 ? b : texb[texture_type];
                texturedFaceIndexZ[set++] = texture_type == -1 ? c : texc[texture_type];

            }
            this.texturedFaces = set;
        }
    }

	public void filterTriangles() {
		for (int triangleId = 0; triangleId < faceIndexX.length; triangleId++) {
			int l = faceIndexX[triangleId];
			int k1 = faceIndexY[triangleId];
			int j2_ = faceIndexZ[triangleId];
			boolean b = true;
			label2: for (int triId = 0; triId < faceIndexX.length; triId++) {
				if (triId == triangleId)
					continue label2;
				if (faceIndexX[triId] == l) {
					b = false;
					break label2;
				}
				if (faceIndexY[triId] == k1) {
					b = false;
					break label2;
				}
				if (faceIndexZ[triId] == j2_) {
					b = false;
					break label2;
				}
			}
			if (b) {
				if (faceTypes != null)
					// face_render_type[triangleId] = -1;
					faceTypes[triangleId] = 255;

			}
		}
	}

    public int getZVertexMax() {
    	return 50;
    }

    @Override
    public Mesh copy() {
    	Mesh mesh = new Mesh();

    	Mesh model = this;
    	mesh.fitsOnSingleSquare = (model.fitsOnSingleSquare);
    	mesh.minimumX = (model.minimumX);
    	mesh.maximumX = (model.maximumX);
    	mesh.maximumZ = (model.maximumZ);
    	mesh.minimumZ = (model.minimumZ);
    	mesh.boundingPlaneRadius = (model.boundingPlaneRadius);
    	mesh.minimumY = (model.minimumY);
    	mesh.boundingSphereRadius = (model.boundingSphereRadius);
    	mesh.boundingCylinderRadius = (model.boundingCylinderRadius);
    	mesh.anInt1654 = (model.anInt1654);
    	mesh.shadedFaceColoursX = copyArray(model.shadedFaceColoursX);
    	mesh.shadedFaceColoursY = copyArray(model.shadedFaceColoursY);
    	mesh.shadedFaceColoursZ = copyArray(model.shadedFaceColoursZ);
    	mesh.faceAlphas = copyArray(model.faceAlphas);
		mesh.faceColourOrTextureId = copyArray(model.faceColourOrTextureId);
		mesh.faceGroups = copyArray(model.faceGroups);
		mesh.facePriorities = copyArray(model.facePriorities);
		mesh.faces = (model.faces);
		mesh.faceSkin = copyArray(model.faceSkin);
		mesh.faceIndexX = copyArray(model.faceIndexX);
		mesh.faceIndexY = copyArray(model.faceIndexY);
		mesh.faceIndexZ = copyArray(model.faceIndexZ);
		mesh.normals = (model.normals);
		mesh.priority = (model.priority);
		mesh.texturedFaces = model.texturedFaces;
		mesh.texturedFaceIndexX = copyArray(model.texturedFaceIndexX);
		mesh.texturedFaceIndexY = copyArray(model.texturedFaceIndexY);
		mesh.texturedFaceIndexZ = copyArray(model.texturedFaceIndexZ);
		mesh.faceTypes = copyArray(model.faceTypes);
		mesh.vertexGroups = copyArray(model.vertexGroups);
		mesh.vertexBones = copyArray(model.vertexBones);
		mesh.vertexX = copyArray(model.vertexX);
		mesh.vertexY = copyArray(model.vertexY);
		mesh.vertexZ = copyArray(model.vertexZ);
		mesh.vertices = model.vertices;
		return mesh;
    }

    protected static int[] copyArray(int[] a) {
    	if(a == null)
    		return null;
       return Arrays.copyOf(a, a.length);
    }
    protected static int[][] copyArray(int[][] a) {
    	if(a == null)
    		return null;
       return Arrays.copyOf(a, a.length);
    }

	public void retexture(int found, int replace) {
		if(faceColourOrTextureId != null)
			for (int face = 0; face < faces; face++) {
				if (faceColourOrTextureId [face] == found) {
					faceColourOrTextureId [face] = replace;
				}
			}

	}

    protected byte[] texture_coordinates;

	/*	public List<Triangle> getTriangles() {
			List<Vertex> vertices = getVertices();
			return IntStream.of(0, faces).mapToObj(index -> new Triangle(vertices.get(this.faceIndexX[index]), vertices.get(this.faceIndexY[index]), vertices.get(this.faceIndexZ[index]))).collect(Collectors.toList());
		}
	
		public List<Vertex> getVertices(){
			return IntStream.range(0, vertices).mapToObj(index -> new Vertex(this.vertexX[index], this.vertexY[index], this.vertexZ[index])).collect(Collectors.toList());
		}
	*/
	@Getter
	@Setter
	private int sceneId;
}