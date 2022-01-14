package com.jagex.entity.model;

import java.util.Arrays;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.major.cache.anim.FrameConstants;

import com.jagex.Client;
import com.jagex.cache.anim.Frame;
import com.jagex.cache.anim.FrameBase;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.Renderable;
import com.jagex.util.Constants;
import com.jagex.util.ObjectKey;
import com.rspsi.misc.ToolType;
import com.rspsi.options.Options;

import lombok.Getter;
import lombok.Setter;

@Slf4j
public class Mesh extends Renderable {

	// Class30_Sub2_Sub4_Sub6

	public byte[] textureRenderTypes;
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
	public MeshRevision revision;
	protected int[][] animayaGroups;
	protected int[][] animayaScales;

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
	public int[] faceColours;
	public int[] faceTextures;
	public int[][] faceGroups;
	public int[] facePriorities;
	public int numFaces;
	public int[] faceSkin;
	public int[] faceIndicesA;
	public int[] faceIndicesB;
	public int[] faceIndicesC;
	public VertexNormal[] normals;
	public int facePriority;
	public int numTextures;
	public int[] textureMappingP;
	public int[] textureMappingM;
	public int[] textureMappingN;
	public int[] faceTypes;
	public int[][] vertexGroups;
	public int[] vertexBones;
	public int[] verticesX;
	public int[] verticesY;
	public int[] verticesZ;
	public int numVertices;
	private boolean translucent;

	//public List<Vector3f> vertexes;

	protected Mesh() {
	}

	public Mesh(boolean contouredGround, boolean delayShading, Mesh model) {
		numVertices = model.numVertices;
		numFaces = model.numFaces;
		numTextures = model.numTextures;

		if (contouredGround) {
			verticesY = new int[numVertices];

			for (int vertex = 0; vertex < numVertices; vertex++) {
				verticesY[vertex] = model.verticesY[vertex];
			}
		} else {
			verticesY = model.verticesY;
		}

		if (delayShading) {
			shadedFaceColoursX = new int[numFaces];
			shadedFaceColoursY = new int[numFaces];
			shadedFaceColoursZ = new int[numFaces];

			for (int k = 0; k < numFaces; k++) {
				shadedFaceColoursX[k] = model.shadedFaceColoursX[k];
				shadedFaceColoursY[k] = model.shadedFaceColoursY[k];
				shadedFaceColoursZ[k] = model.shadedFaceColoursZ[k];
			}

			faceTypes = new int[numFaces];
			if (model.faceTypes == null) {
				for (int triangle = 0; triangle < numFaces; triangle++) {
					faceTypes[triangle] = 0;
				}
			} else {
				for (int index = 0; index < numFaces; index++) {
					faceTypes[index] = model.faceTypes[index];
				}
			}

			super.normals = new VertexNormal[numVertices];
			for (int index = 0; index < numVertices; index++) {
				VertexNormal parent = super.normals[index] = new VertexNormal();
				VertexNormal copied = model.getNormal(index);
				parent.setX(copied.getX());
				parent.setY(copied.getY());
				parent.setZ(copied.getZ());
				parent.setMagnitude(copied.getMagnitude());
			}

			normals = model.normals;
		} else {
			shadedFaceColoursX = model.shadedFaceColoursX;
			shadedFaceColoursY = model.shadedFaceColoursY;
			shadedFaceColoursZ = model.shadedFaceColoursZ;
			faceTypes = model.faceTypes;
		}

		verticesX = model.verticesX;
		verticesZ = model.verticesZ;
		faceTextures = model.faceTextures;
		faceColours = model.faceColours;
		faceAlphas = model.faceAlphas;
		facePriorities = model.facePriorities;
		facePriority = model.facePriority;
		faceIndicesA = model.faceIndicesA;
		faceIndicesB = model.faceIndicesB;
		faceIndicesC = model.faceIndicesC;
		texture_coordinates = model.texture_coordinates;
		textureRenderTypes = model.textureRenderTypes;
		textureMappingP = model.textureMappingP;
		textureMappingM = model.textureMappingM;
		textureMappingN = model.textureMappingN;
		super.modelHeight = model.modelHeight;
		boundingPlaneRadius = model.boundingPlaneRadius;
		boundingCylinderRadius = model.boundingCylinderRadius;
		boundingSphereRadius = model.boundingSphereRadius;
		minimumX = model.minimumX;
		maximumZ = model.maximumZ;
		minimumZ = model.minimumZ;
		maximumX = model.maximumX;
	}

	public Mesh(int modelCount, Mesh[] models) {
		this.numVertices = 0;
		this.numFaces = 0;
		this.facePriority = 0;
		//this.field1490 = false;
		boolean var3 = false;
		boolean var4 = false;
		boolean var5 = false;
		boolean var6 = false;
		boolean var7 = false;
		boolean var8 = false;
		this.numVertices = 0;
		this.numFaces = 0;
		this.numTextures = 0;
		this.facePriority = -1;

		int var9;
		Mesh var10;
		for(var9 = 0; var9 < modelCount; ++var9) {
			var10 = models[var9];
			if(var10 != null) {
				this.numVertices += var10.numVertices;
				this.numFaces += var10.numFaces;
				this.numTextures += var10.numTextures;
				if(var10.facePriorities != null) {
					var4 = true;
				} else {
					if(this.facePriority == -1) {
						this.facePriority = var10.facePriority;
					}

					if(this.facePriority != var10.facePriority) {
						var4 = true;
					}
				}

				var3 |= var10.faceTypes != null;
				var5 |= var10.faceAlphas != null;
				var6 |= var10.faceSkin != null;
				var7 |= var10.faceTextures != null;
				var8 |= var10.texture_coordinates != null;
			}
		}

		this.verticesX = new int[this.numVertices];
		this.verticesY = new int[this.numVertices];
		this.verticesZ = new int[this.numVertices];
		this.vertexBones = new int[this.numVertices];
		this.faceIndicesA = new int[this.numFaces];
		this.faceIndicesB = new int[this.numFaces];
		this.faceIndicesC = new int[this.numFaces];
		if(var3) {
			this.faceTypes = new int[this.numFaces];
		}

		if(var4) {
			this.facePriorities = new int[this.numFaces];
		}

		if(var5) {
			this.faceAlphas = new int[this.numFaces];
		}

		if(var6) {
			this.faceSkin = new int[this.numFaces];
		}

		if(var7) {
			this.faceTextures = new int[this.numFaces];
		}

		if(var8) {
			this.texture_coordinates = new byte[this.numFaces];
		}

		this.faceColours = new int[this.numFaces];
		if(this.numTextures > 0) {
			this.textureRenderTypes = new byte[this.numTextures];
			this.textureMappingP = new int[this.numTextures];
			this.textureMappingM = new int[this.numTextures];
			this.textureMappingN = new int[this.numTextures];
		}

		this.numVertices = 0;
		this.numFaces = 0;
		this.numTextures = 0;

		for(var9 = 0; var9 < modelCount; ++var9) {
			var10 = models[var9];
			if(var10 != null) {
				int var11;
				for(var11 = 0; var11 < var10.numFaces; ++var11) {
					if(var3 && var10.faceTypes != null) {
						this.faceTypes[this.numFaces] = var10.faceTypes[var11];
					}

					if(var4) {
						if(var10.facePriorities != null) {
							this.facePriorities[this.numFaces] = var10.facePriorities[var11];
						} else {
							this.facePriorities[this.numFaces] = var10.facePriority;
						}
					}

					if(var5 && var10.faceAlphas != null) {
						this.faceAlphas[this.numFaces] = var10.faceAlphas[var11];
					}

					if(var6 && var10.faceSkin != null) {
						this.faceSkin[this.numFaces] = var10.faceSkin[var11];
					}

					if(var7) {
						if(var10.faceTextures != null) {
							this.faceTextures[this.numFaces] = var10.faceTextures[var11];
						} else {
							this.faceTextures[this.numFaces] = -1;
						}
					}

					if(var8) {
						if(var10.texture_coordinates != null && var10.texture_coordinates[var11] != -1) {
							this.texture_coordinates[this.numFaces] = (byte)(this.numTextures + var10.texture_coordinates[var11]);
						} else {
							this.texture_coordinates[this.numFaces] = -1;
						}
					}

					this.faceColours[this.numFaces] = var10.faceColours[var11];
					this.faceIndicesA[this.numFaces] = this.findMatchingVertex(var10, var10.faceIndicesA[var11]);
					this.faceIndicesB[this.numFaces] = this.findMatchingVertex(var10, var10.faceIndicesB[var11]);
					this.faceIndicesC[this.numFaces] = this.findMatchingVertex(var10, var10.faceIndicesC[var11]);
					++this.numFaces;
				}

				for(var11 = 0; var11 < var10.numTextures; ++var11) {
					byte var12 = this.textureRenderTypes[this.numTextures] = var10.textureRenderTypes[var11];
					if(var12 == 0) {
						this.textureMappingP[this.numTextures] = (short)this.findMatchingVertex(var10, var10.textureMappingP[var11]);
						this.textureMappingM[this.numTextures] = (short)this.findMatchingVertex(var10, var10.textureMappingM[var11]);
						this.textureMappingN[this.numTextures] = (short)this.findMatchingVertex(var10, var10.textureMappingN[var11]);
					}

					++this.numTextures;
				}
			}
		}

	}

	public Mesh(Mesh model, boolean shareColours, boolean shareAlphas, boolean shareVertices, boolean shareTextures) {
		numVertices = model.numVertices;
		numFaces = model.numFaces;
		numTextures = model.numTextures;

		if (shareVertices) {
			verticesX = model.verticesX;
			verticesY = model.verticesY;
			verticesZ = model.verticesZ;
		} else {
			verticesX = copyArray(model.verticesX);
			verticesY = copyArray(model.verticesY);
			verticesZ = copyArray(model.verticesZ);
		}

		if (!shareColours && model.faceColours != null) {
			faceColours = copyArray(model.faceColours);
		} else {
			faceColours = model.faceColours;
		}

		if(!shareTextures && model.faceTextures != null) {
			this.faceTextures = copyArray(model.faceTextures);
		} else {
			this.faceTextures = model.faceTextures;
		}

		if (shareAlphas) {
			faceAlphas = model.faceAlphas;
		} else {
			if (model.faceAlphas == null) {
				faceAlphas = new int[numFaces];
				Arrays.fill(faceAlphas, 0);
				for (int face = 0; face < numFaces; face++) {
					faceAlphas[face] = 0;
				}
			} else {
				faceAlphas = copyArray(model.faceAlphas);
			}
		}

		vertexBones = model.vertexBones;
		faceSkin = model.faceSkin;
		textureRenderTypes = model.textureRenderTypes;
		texture_coordinates = model.texture_coordinates;
		faceTypes = model.faceTypes;
		faceIndicesA = model.faceIndicesA;
		faceIndicesB = model.faceIndicesB;
		faceIndicesC = model.faceIndicesC;
		facePriorities = model.facePriorities;
		facePriority = model.facePriority;
		textureMappingP = model.textureMappingP;
		textureMappingM = model.textureMappingM;
		textureMappingN = model.textureMappingN;
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

			for(int var10 = 0; var10 < this.numVertices; ++var10) {
				int var11 = method3027(this.verticesX[var10], this.verticesZ[var10], var8, var9);
				int var12 = this.verticesY[var10];
				int var13 = method3028(this.verticesX[var10], this.verticesZ[var10], var8, var9);
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

		for (int vertex = 0; vertex < numVertices; vertex++) {
			int x = verticesX[vertex];
			int y = verticesY[vertex];
			int z = verticesZ[vertex];

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

		for (int vertex = 0; vertex < numVertices; vertex++) {
			int x = verticesX[vertex];
			int y = verticesY[vertex];
			int z = verticesZ[vertex];

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

		for (int vertex = 0; vertex < numVertices; vertex++) {
			int y = verticesY[vertex];
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
			int x = model.verticesX[vertex];
			int y = model.verticesY[vertex];
			int z = model.verticesZ[vertex];

			for (int index = 0; index < numVertices; index++) {
				if (x == verticesX[index] && y == verticesY[index] && z == verticesZ[index]) {
					matched = index;
					break;
				}
			}

			if (matched == -1) {
				verticesX[numVertices] = x;
				verticesY[numVertices] = y;
				verticesZ[numVertices] = z;

				if (model.vertexBones != null) {
					vertexBones[numVertices] = model.vertexBones[vertex];
				}

				matched = numVertices++;
			}

		return matched;
	}

	public void invert() {
		for (int vertex = 0; vertex < numVertices; vertex++) {
			verticesZ[vertex] = -verticesZ[vertex];
		}

		for (int vertex = 0; vertex < numFaces; vertex++) {
			int x = faceIndicesA[vertex];
			faceIndicesA[vertex] = faceIndicesC[vertex];
			faceIndicesC[vertex] = x;
		}
	}

	public final void light(int lighting, int diffusion, int x, int y, int z, boolean immediateShading) {
		int length = (int) Math.sqrt(x * x + y * y + z * z);
		int k1 = diffusion * length >> 8;

		if (shadedFaceColoursX == null) {
			shadedFaceColoursX = new int[numFaces];
			shadedFaceColoursY = new int[numFaces];
			shadedFaceColoursZ = new int[numFaces];
		}

		if (super.normals == null) {
			super.normals = new VertexNormal[numVertices];
			for (int index = 0; index < numVertices; index++) {
				super.normals[index] = new VertexNormal();
			}
		}

		for (int face = 0; face < numFaces; face++) {
			int faceX = faceIndicesA[face];
			int faceY = faceIndicesB[face];
			int faceZ = faceIndicesC[face];
			int j3 = verticesX[faceY] - verticesX[faceX];
			int k3 = verticesY[faceY] - verticesY[faceX];
			int l3 = verticesZ[faceY] - verticesZ[faceX];
			int i4 = verticesX[faceZ] - verticesX[faceX];
			int j4 = verticesY[faceZ] - verticesY[faceX];
			int k4 = verticesZ[faceZ] - verticesZ[faceX];
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
				normal.setMagnitude(normal.getMagnitude() + 1);
				normal = super.normals[faceY];
				normal.setX(normal.getX() + dx);
				normal.setY(normal.getY() + dy);
				normal.setZ(normal.getZ() + dz);
				normal.setMagnitude(normal.getMagnitude() + 1);
				normal = super.normals[faceZ];
				normal.setX(normal.getX() + dx);
				normal.setY(normal.getY() + dy);
				normal.setZ(normal.getZ() + dz);
				normal.setMagnitude(normal.getMagnitude() + 1);
			} else {
				int l5 = lighting + (x * dx + y * dy + z * dz) / (k1 + k1 / 2);
				shadedFaceColoursX[face] = checkedLight(faceColours[face], l5, faceTypes[face]);
			}
		}

		if (immediateShading) {
			shade(lighting, k1, x, y, z);
		} else {
			normals = new VertexNormal[numVertices];
			for (int index = 0; index < numVertices; index++) {
				VertexNormal parent = super.normals[index];
				VertexNormal copied = normals[index] = new VertexNormal();
				copied.setX(parent.getX());
				copied.setY(parent.getY());
				copied.setZ(parent.getZ());
				copied.setMagnitude(parent.getMagnitude());
			}
		}

		if (immediateShading) {
			computeCircularBounds();
		} else {
			computeBounds();
		}
	}

	public void method464(Mesh model, boolean shareAlphas) {
		numVertices = model.numVertices;
		numFaces = model.numFaces;
		numTextures = model.numTextures;

		if (anIntArray1622.length < numVertices) {
			anIntArray1622 = new int[numVertices + 100];
			anIntArray1623 = new int[numVertices + 100];
			anIntArray1624 = new int[numVertices + 100];
		}

		verticesX = anIntArray1622;
		verticesY = anIntArray1623;
		verticesZ = anIntArray1624;
		for (int vertex = 0; vertex < numVertices; vertex++) {
			verticesX[vertex] = model.verticesX[vertex];
			verticesY[vertex] = model.verticesY[vertex];
			verticesZ[vertex] = model.verticesZ[vertex];
		}

		if (shareAlphas) {
			faceAlphas = model.faceAlphas;
		} else {
			if (anIntArray1625.length < numFaces) {
				anIntArray1625 = new int[numFaces + 100];
			}
			faceAlphas = anIntArray1625;

			if (model.faceAlphas == null) {
				for (int index = 0; index < numFaces; index++) {
					faceAlphas[index] = 0;
				}
			} else {
				for (int index = 0; index < numFaces; index++) {
					faceAlphas[index] = model.faceAlphas[index];
				}
			}
		}

		faceTypes = model.faceTypes;
		textureRenderTypes = model.textureRenderTypes;
		texture_coordinates = model.texture_coordinates;
		faceColours = model.faceColours;
		faceTextures = model.faceTextures;
		facePriorities = model.facePriorities;
		facePriority = model.facePriority;
		faceGroups = model.faceGroups;
		vertexGroups = model.vertexGroups;
		faceIndicesA = model.faceIndicesA;
		faceIndicesB = model.faceIndicesB;
		faceIndicesC = model.faceIndicesC;
		shadedFaceColoursX = model.shadedFaceColoursX;
		shadedFaceColoursY = model.shadedFaceColoursY;
		shadedFaceColoursZ = model.shadedFaceColoursZ;
		textureMappingP = model.textureMappingP;
		textureMappingM = model.textureMappingM;
		textureMappingN = model.textureMappingN;
	}

	private static ObjectKey activeKey;

	private void renderFaces(GameRasterizer rasterizer, boolean flag, boolean multiTileFlag, ObjectKey key, int z) {
		for (int j = 0; j < boundingSphereRadius; j++) {
			rasterizer.depthListIndices[j] = 0;
		}

		activeKey = key;

		for (int face = 0; face < numFaces; face++) {
			if (faceTypes == null || faceTypes[face] != -1) {
				int indexX = faceIndicesA[face];
				int indexY = faceIndicesB[face];
				int indexZ = faceIndicesC[face];
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
		int faceX = faceIndicesA[index];
		int faceY = faceIndicesB[index];
		int faceZ = faceIndicesC[index];
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
			type = faceTextures != null && faceTextures[index] != -1 ? 2 : 0;
		} else {
			type = faceTypes[index] & 3;

		}
		boolean ignoreTextures = translucent || selected;

		if (type == 0 && !ignoreTextures) {
			rasterizer.drawShadedTriangle(rasterizer.vertexScreenY[faceX], rasterizer.vertexScreenY[faceY], rasterizer.vertexScreenY[faceZ], rasterizer.vertexScreenX[faceX],
					rasterizer.vertexScreenX[faceY], rasterizer.vertexScreenX[faceZ], shadedFaceColoursX[index], shadedFaceColoursY[index], shadedFaceColoursZ[index]);
		} else if (type == 1 || ignoreTextures) {
			int colour = selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedFaceColoursX[index]];
			rasterizer.drawShadedTriangle(rasterizer.vertexScreenY[faceX], rasterizer.vertexScreenY[faceY], rasterizer.vertexScreenY[faceZ], rasterizer.vertexScreenX[faceX],
					rasterizer.vertexScreenX[faceY], rasterizer.vertexScreenX[faceZ], colour);
		} else if (type == 2 || type == 3) {

			int texFaceX = 0, texFaceY = 0, texFaceZ = 0;
			try {
			if(texture_coordinates != null &&  texture_coordinates[index] != -1) {
				int k1 = texture_coordinates[index] & 0xFF;
				texFaceX = textureMappingP[k1];
				texFaceY = textureMappingM[k1];
				texFaceZ = textureMappingN[k1];
			} else {
				texFaceX = faceX;
				texFaceY = faceY;
				texFaceZ = faceZ;

			}

			if(texFaceX >= 4096  || texFaceY >= 4096 || texFaceZ >= 4096 ){
				texFaceX = faceX;
				texFaceY = faceY;
				texFaceZ = faceZ;
			}

			int colourX = shadedFaceColoursX[index];
			int colourY = shadedFaceColoursX[index];
			int colourZ = shadedFaceColoursX[index];

			if(type == 2) {
				colourY =  shadedFaceColoursY[index];
				colourZ =  shadedFaceColoursZ[index];
			}

			int texId = faceTextures[index];
			//texId = 23;
			rasterizer.drawTexturedTriangle(
					rasterizer.vertexScreenY[faceX],
					rasterizer.vertexScreenY[faceY],
					rasterizer.vertexScreenY[faceZ],
					rasterizer.vertexScreenX[faceX],
					rasterizer.vertexScreenX[faceY],
					rasterizer.vertexScreenX[faceZ],
					colourX, colourY, colourZ,
					rasterizer.camera_vertex_x[texFaceX],
					rasterizer.camera_vertex_x[texFaceY],
					rasterizer.camera_vertex_x[texFaceZ],
					rasterizer.camera_vertex_y[texFaceX],
					rasterizer.camera_vertex_y[texFaceY],
					rasterizer.camera_vertex_y[texFaceZ],
					rasterizer.camera_vertex_z[texFaceX],
					rasterizer.camera_vertex_z[texFaceY],
					rasterizer.camera_vertex_z[texFaceZ],
					texId);
		} catch (Exception e) {
			e.printStackTrace();
				log.info("{} {} {} | {} {} {}", faceX, faceY, faceZ, texFaceX, texFaceY, texFaceZ);
		}
		}
	}

	private void method485(GameRasterizer rasterizer, int index) {

		boolean ignoreTextures = translucent || selected;

		int viewX = rasterizer.viewCenter.getX();
		int viewY = rasterizer.viewCenter.getY();
		int l = 0;
		int faceX = faceIndicesA[index];
		int faceY = faceIndicesB[index];
		int faceZ = faceIndicesC[index];
		int l1 = rasterizer.camera_vertex_z[faceX];
		int i2 = rasterizer.camera_vertex_z[faceY];
		int j2 = rasterizer.camera_vertex_z[faceZ];
		if (l1 >= 50) {
			rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[faceX];
			rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[faceX];
			rasterizer.anIntArray1680[l++] = shadedFaceColoursX[index];
		} else {
			int k2 = rasterizer.camera_vertex_x[faceX];
			int k3 = rasterizer.camera_vertex_y[faceX];
			int k4 = shadedFaceColoursX[index];
			if (j2 >= 50) {
				int k5 = (50 - l1) * Constants.LIGHT_DECAY[j2 - l1];
				rasterizer.anIntArray1678[l] = viewX + (k2 + ((rasterizer.camera_vertex_x[faceZ] - k2) * k5 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (k3 + ((rasterizer.camera_vertex_y[faceZ] - k3) * k5 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = k4 + ((shadedFaceColoursZ[index] - k4) * k5 >> 16);
			}
			if (i2 >= 50) {
				int l5 = (50 - l1) * Constants.LIGHT_DECAY[i2 - l1];
				rasterizer.anIntArray1678[l] = viewX + (k2 + ((rasterizer.camera_vertex_x[faceY] - k2) * l5 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (k3 + ((rasterizer.camera_vertex_y[faceY] - k3) * l5 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = k4 + ((shadedFaceColoursY[index] - k4) * l5 >> 16);
			}
		}
		if (i2 >= 50) {
			rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[faceY];
			rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[faceY];
			rasterizer.anIntArray1680[l++] = shadedFaceColoursY[index];
		} else {
			int l2 = rasterizer.camera_vertex_x[faceY];
			int l3 = rasterizer.camera_vertex_y[faceY];
			int l4 = shadedFaceColoursY[index];
			if (l1 >= 50) {
				int i6 = (50 - i2) * Constants.LIGHT_DECAY[l1 - i2];
				rasterizer.anIntArray1678[l] = viewX + (l2 + ((rasterizer.camera_vertex_x[faceX] - l2) * i6 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (l3 + ((rasterizer.camera_vertex_y[faceX] - l3) * i6 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = l4 + ((shadedFaceColoursX[index] - l4) * i6 >> 16);
			}
			if (j2 >= 50) {
				int j6 = (50 - i2) * Constants.LIGHT_DECAY[j2 - i2];
				rasterizer.anIntArray1678[l] = viewX + (l2 + ((rasterizer.camera_vertex_x[faceZ] - l2) * j6 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (l3 + ((rasterizer.camera_vertex_y[faceZ] - l3) * j6 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = l4 + ((shadedFaceColoursZ[index] - l4) * j6 >> 16);
			}
		}
		if (j2 >= 50) {
			rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[faceZ];
			rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[faceZ];
			rasterizer.anIntArray1680[l++] = shadedFaceColoursZ[index];
		} else {
			int i3 = rasterizer.camera_vertex_x[faceZ];
			int i4 = rasterizer.camera_vertex_y[faceZ];
			int i5 = shadedFaceColoursZ[index];
			if (i2 >= 50) {
				int k6 = (50 - j2) * Constants.LIGHT_DECAY[i2 - j2];
				rasterizer.anIntArray1678[l] = viewX + (i3 + ((rasterizer.camera_vertex_x[faceY] - i3) * k6 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (i4 + ((rasterizer.camera_vertex_y[faceY] - i4) * k6 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = i5 + ((shadedFaceColoursY[index] - i5) * k6 >> 16);
			}
			if (l1 >= 50) {
				int l6 = (50 - j2) * Constants.LIGHT_DECAY[l1 - j2];
				rasterizer.anIntArray1678[l] = viewX + (i3 + ((rasterizer.camera_vertex_x[faceX] - i3) * l6 >> 16) << 9) / 50;
				rasterizer.anIntArray1679[l] = viewY + (i4 + ((rasterizer.camera_vertex_y[faceX] - i4) * l6 >> 16) << 9) / 50;
				rasterizer.anIntArray1680[l++] = i5 + ((shadedFaceColoursX[index] - i5) * l6 >> 16);
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
				int type;
				if (faceTypes == null) {
					type = faceTextures != null && faceTextures[index] != -1 ? 2 : 0;
				} else {
					type = faceTypes[index] & 3;
				}

				if (type == 0 && !ignoreTextures) {
					rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
							rasterizer.anIntArray1680[2]);
				} else if (type == 1 || ignoreTextures) {
					rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5,
							selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedFaceColoursX[index]]);
				} else if (type == 2) {
					int texFaceX, texFaceY, texFaceZ;
					if(texture_coordinates != null && texture_coordinates[index] != -1) {
						int texFaceIndex = texture_coordinates[index] & 0xFF;
						texFaceX = textureMappingP[texFaceIndex];
						texFaceY = textureMappingM[texFaceIndex];
						texFaceZ = textureMappingN[texFaceIndex];
					} else {
						texFaceX = faceX;
						texFaceY = faceY;
						texFaceZ = faceZ;
					}


					if(texFaceX >= 4096  || texFaceY >= 4096 || texFaceZ >= 4096 ){
						texFaceX = faceX;
						texFaceY = faceY;
						texFaceZ = faceZ;
					}

					rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
							rasterizer.anIntArray1680[2], rasterizer.camera_vertex_x[texFaceX], rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ],
							rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY], rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX],
							rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ], faceTextures[index]);
				} else if (type == 3) {
					int texFaceX, texFaceY, texFaceZ;
					if(texture_coordinates != null && texture_coordinates[index] != -1) {
						int texFaceIndex = texture_coordinates[index] & 0xFF;
						texFaceX = textureMappingP[texFaceIndex];
						texFaceY = textureMappingM[texFaceIndex];
						texFaceZ = textureMappingN[texFaceIndex];
					} else {
						texFaceX = faceX;
						texFaceY = faceY;
						texFaceZ = faceZ;
					}


					if(texFaceX >= 4096  || texFaceY >= 4096 || texFaceZ >= 4096 ){
						texFaceX = faceX;
						texFaceY = faceY;
						texFaceZ = faceZ;
					}

					rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, shadedFaceColoursX[index], shadedFaceColoursX[index],
							shadedFaceColoursX[index], rasterizer.camera_vertex_x[texFaceX], rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ],
							rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY], rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX],
							rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ], faceTextures[index]);
				}
			}
			if (l == 4) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > rasterizer.getMaxRight() || j4 > rasterizer.getMaxRight() || j5 > rasterizer.getMaxRight()
						|| rasterizer.anIntArray1678[3] < 0 || rasterizer.anIntArray1678[3] > rasterizer.getMaxRight()) {
					rasterizer.restrictEdges = true;
				}
				int type;
				if (faceTypes == null) {
					type = faceTextures != null && faceTextures[index] != -1 ? 2 : 0;
				} else {
					type = faceTypes[index] & 3;
				}
				if (type == 0 && !ignoreTextures) {
					rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
							rasterizer.anIntArray1680[2]);
					rasterizer.drawShadedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
							rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[2], rasterizer.anIntArray1680[3]);
					return;
				} else if (type == 1 || ignoreTextures) {
					int l8 =  selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedFaceColoursX[index]];
					rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, l8);
					rasterizer.drawShadedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3], l8);
					return;
				} else if (type == 2) {
					int texFaceX, texFaceY, texFaceZ;
					if(texture_coordinates != null && texture_coordinates[index] != -1) {
						int texFaceIndex = texture_coordinates[index] & 0xFF;
						texFaceX = textureMappingP[texFaceIndex];
						texFaceY = textureMappingM[texFaceIndex];
						texFaceZ = textureMappingN[texFaceIndex];
					} else {
						texFaceX = faceX;
						texFaceY = faceY;
						texFaceZ = faceZ;
					}


					if(texFaceX >= 4096  || texFaceY >= 4096 || texFaceZ >= 4096 ){
						texFaceX = faceX;
						texFaceY = faceY;
						texFaceZ = faceZ;
					}

					rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
							rasterizer.anIntArray1680[2], rasterizer.camera_vertex_x[texFaceX], rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ],
							rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY], rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX],
							rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ], faceTextures[index]);
					rasterizer.drawTexturedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
							rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[2], rasterizer.anIntArray1680[3], rasterizer.camera_vertex_x[texFaceX],
							rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ], rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY],
							rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX], rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ],
							faceTextures[index]);
					return;
				} else if (type == 3) {
					int texFaceX, texFaceY, texFaceZ;
					if(texture_coordinates != null && texture_coordinates[index] != -1) {
						int texFaceIndex = texture_coordinates[index] & 0xFF;
						texFaceX = textureMappingP[texFaceIndex];
						texFaceY = textureMappingM[texFaceIndex];
						texFaceZ = textureMappingN[texFaceIndex];
					} else {
						texFaceX = faceX;
						texFaceY = faceY;
						texFaceZ = faceZ;
					}


					if(texFaceX >= 4096  || texFaceY >= 4096 || texFaceZ >= 4096 ){
						texFaceX = faceX;
						texFaceY = faceY;
						texFaceZ = faceZ;
					}

					rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, shadedFaceColoursX[index], shadedFaceColoursX[index],
							shadedFaceColoursX[index], rasterizer.camera_vertex_x[texFaceX], rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ],
							rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY], rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX],
							rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ], faceTextures[index]);
					rasterizer.drawTexturedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
							shadedFaceColoursX[index], shadedFaceColoursX[index], shadedFaceColoursX[index], rasterizer.camera_vertex_x[texFaceX],
							rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ], rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY],
							rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX], rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ],
							faceTextures[index]);
				}
			}
		}
	}

	public void pitch(int theta) {
		int sin = Constants.SINE[theta];
		int cos = Constants.COSINE[theta];

		for (int vertex = 0; vertex < numVertices; vertex++) {
			int y = verticesY[vertex] * cos - verticesZ[vertex] * sin >> 16;
			verticesZ[vertex] = verticesY[vertex] * sin + verticesZ[vertex] * cos >> 16;
			verticesY[vertex] = y;
		}
	}

	public void prepareSkeleton() {
		if (vertexBones != null) {
			int[] sizes = new int[256];
			int maximumBoneId = 0;

			for (int vertex = 0; vertex < numVertices; vertex++) {
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

			for (int index = 0; index < numVertices; index++) {
				int bone = vertexBones[index];
				vertexGroups[bone][sizes[bone]++] = index;
			}

			vertexBones = null;
		}

		if (faceSkin != null) {
			int[] sizes = new int[256];
			int count = 0;

			for (int index = 0; index < numFaces; index++) {
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

			for (int index = 0; index < numFaces; index++) {
				int skin = faceSkin[index];
				faceGroups[skin][sizes[skin]++] = index;
			}

			faceSkin = null;
		}
	}

	public void recolour(int oldColour, int newColour) {
		for (int index = 0; index < numFaces; index++) {
			if (faceColours[index] == oldColour) {
				faceColours[index] = newColour;
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
		for (int k4 = 0; k4 < numVertices; k4++) {
			int x = verticesX[k4];
			int y = verticesY[k4];
			int z = verticesZ[k4];
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
			if (numTextures > 0) {
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

		for (int vertex = 0; vertex < numVertices; vertex++) {
			int xVertex = verticesX[vertex];
			int yVertex = verticesY[vertex];
			int zVertex = verticesZ[vertex];
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

			if (flag || numTextures > 0) {
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
		for (int index = 0; index < numVertices; index++) {
			int x = verticesX[index];
			verticesX[index] = verticesZ[index];
			verticesZ[index] = -x;
		}
	}

	public void offsetVertices(int x, int y, int z) {
		for (int index = 0; index < numVertices; index++) {
			verticesX[index] += x;
			verticesX[index] += y;
			verticesZ[index] += z;
		}
	}

	public void scale(int x, int y, int z) {
		for (int vertex = 0; vertex < numVertices; vertex++) {
			verticesX[vertex] = verticesX[vertex] * x / 128;
			verticesY[vertex] = verticesY[vertex] * z / 128;
			verticesZ[vertex] = verticesZ[vertex] * y / 128;
		}
	}

    public void scale2(int i) {
        for (int i1 = 0; i1 < numVertices; i1++) {
            verticesX[i1] = verticesX[i1] / i;
            verticesY[i1] = verticesY[i1] / i;
            verticesZ[i1] = verticesZ[i1] / i;
        }
    }


	public final void shade(int lighting, int j, int x, int y, int z) {
		for (int face = 0; face < numFaces; face++) {
			int indexX = faceIndicesA[face];
			int indexY = faceIndicesB[face];
			int indexZ = faceIndicesC[face];

			if (faceTypes == null) {
				int colour = faceColours[face];
				VertexNormal normal = super.normals[indexX];

				int light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
				shadedFaceColoursX[face] = checkedLight(colour, light, 0);

				normal = super.normals[indexY];
				light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
				shadedFaceColoursY[face] = checkedLight(colour, light, 0);

				normal = super.normals[indexZ];
				light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
				shadedFaceColoursZ[face] = checkedLight(colour, light, 0);
			} else if ((faceTypes[face] & 1) == 0) {
				int colour = faceColours[face];
				int point = faceTypes[face];

				VertexNormal normal = super.normals[indexX];
				int light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
				shadedFaceColoursX[face] = checkedLight(colour, light, point);
				normal = super.normals[indexY];

				light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
				shadedFaceColoursY[face] = checkedLight(colour, light, point);
				normal = super.normals[indexZ];

				light = lighting
						+ (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
				shadedFaceColoursZ[face] = checkedLight(colour, light, point);
			}
		}

		super.normals = null;
		normals = null;
		vertexBones = null;
		faceSkin = null;
		if (faceTypes != null) {
			for (int index = 0; index < numFaces; index++) {
				if ((faceTypes[index] & 2) == 2)
					return;
			}
		}

		faceColours = null;
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
						centroidX += verticesX[vertex];
						centroidY += verticesY[vertex];
						centroidZ += verticesZ[vertex];
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
						verticesX[vertex] += dx;
						verticesY[vertex] += dy;
						verticesZ[vertex] += dz;
					}
				}
			}
		} else if (transformation == FrameConstants.ROTATION_TRANSFORMATION) {
			for (int index = 0; index < count; index++) {
				int group = groups[index];

				if (group < vertexGroups.length) {
					for (int vertex : vertexGroups[group]) {
						verticesX[vertex] -= centroidX;
						verticesY[vertex] -= centroidY;
						verticesZ[vertex] -= centroidZ;
						int pitch = (dx & 0xFF) * 8;
						int roll = (dy & 0xFF) * 8;
						int yaw = (dz & 0xFF) * 8;

						if (yaw != 0) {
							int sin = Constants.SINE[yaw];
							int cos = Constants.COSINE[yaw];
							int x = verticesY[vertex] * sin + verticesX[vertex] * cos >> 16;
							verticesY[vertex] = verticesY[vertex] * cos - verticesX[vertex] * sin >> 16;
							verticesX[vertex] = x;
						}

						if (pitch != 0) {
							int sin = Constants.SINE[pitch];
							int cos = Constants.COSINE[pitch];
							int y = verticesY[vertex] * cos - verticesZ[vertex] * sin >> 16;
							verticesZ[vertex] = verticesY[vertex] * sin + verticesZ[vertex] * cos >> 16;
							verticesY[vertex] = y;
						}

						if (roll != 0) {
							int sin = Constants.SINE[roll];
							int cos = Constants.COSINE[roll];
							int x = verticesZ[vertex] * sin + verticesX[vertex] * cos >> 16;
							verticesZ[vertex] = verticesZ[vertex] * cos - verticesX[vertex] * sin >> 16;
							verticesX[vertex] = x;
						}

						verticesX[vertex] += centroidX;
						verticesY[vertex] += centroidY;
						verticesZ[vertex] += centroidZ;
					}
				}
			}
		} else if (transformation == FrameConstants.SCALE_TRANSFORMATION) {
			for (int index = 0; index < count; index++) {
				int group = groups[index];

				if (group < vertexGroups.length) {
					for (int vertex : vertexGroups[group]) {
						verticesX[vertex] -= centroidX;
						verticesY[vertex] -= centroidY;
						verticesZ[vertex] -= centroidZ;

						verticesX[vertex] = verticesX[vertex] * dx / 128;
						verticesY[vertex] = verticesY[vertex] * dy / 128;
						verticesZ[vertex] = verticesZ[vertex] * dz / 128;

						verticesX[vertex] += centroidX;
						verticesY[vertex] += centroidY;
						verticesZ[vertex] += centroidZ;
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
		for (int vertex = 0; vertex < numVertices; vertex++) {
			verticesX[vertex] += x;
			verticesY[vertex] += y;
			verticesZ[vertex] += z;
		}
	}

    public void convertTexturesTo317(short[] textureIds, int[] texa, int[] texb, int[] texc, boolean osrs) {
        int set = 0;
        int set2 = 0;
        int max = TextureLoader.instance.count();
        if (textureIds != null) {
            textureMappingP = new int[numFaces];
            textureMappingM = new int[numFaces];
            textureMappingN = new int[numFaces];

            for (int i = 0; i < numFaces; i++) {
                if (textureIds[i] == -1 && this.faceTypes[i] == 2) {
                    this.faceColours[i] = 65535;
                    faceTypes[i] = 0;
                }
                if (textureIds[i] >= max || textureIds[i] < 0 || textureIds[i] == 39) {
                	faceTypes[i] = 0;
                    continue;
                }
                faceTypes[i] = 2 + set2;
                set2 += 4;
                int a = this.faceIndicesA[i];
                int b = faceIndicesB[i];
                int c = faceIndicesC[i];
                faceColours[i] = textureIds[i];

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

                textureMappingP[set] = texture_type == -1 ? a : texa[texture_type];
                textureMappingM[set] = texture_type == -1 ? b : texb[texture_type];
                textureMappingN[set++] = texture_type == -1 ? c : texc[texture_type];

            }
            this.numTextures = set;
        }
    }

	public void filterTriangles() {
		for (int triangleId = 0; triangleId < faceIndicesA.length; triangleId++) {
			int l = faceIndicesA[triangleId];
			int k1 = faceIndicesB[triangleId];
			int j2_ = faceIndicesC[triangleId];
			boolean b = true;
			label2: for (int triId = 0; triId < faceIndicesA.length; triId++) {
				if (triId == triangleId)
					continue label2;
				if (faceIndicesA[triId] == l) {
					b = false;
					break label2;
				}
				if (faceIndicesB[triId] == k1) {
					b = false;
					break label2;
				}
				if (faceIndicesC[triId] == j2_) {
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
	    mesh.faceColours = copyArray(model.faceColours);
	    mesh.faceTextures = copyArray(model.faceTextures);
		mesh.texture_coordinates = copyArray(model.texture_coordinates);
		mesh.textureRenderTypes = copyArray(model.textureRenderTypes);
		mesh.faceGroups = copyArray(model.faceGroups);
		mesh.facePriorities = copyArray(model.facePriorities);
		mesh.numFaces = (model.numFaces);
		mesh.faceSkin = copyArray(model.faceSkin);
		mesh.faceIndicesA = copyArray(model.faceIndicesA);
		mesh.faceIndicesB = copyArray(model.faceIndicesB);
		mesh.faceIndicesC = copyArray(model.faceIndicesC);
		mesh.normals = (model.normals);
		mesh.facePriority = (model.facePriority);
		mesh.numTextures = model.numTextures;
		mesh.textureMappingP = copyArray(model.textureMappingP);
		mesh.textureMappingM = copyArray(model.textureMappingM);
		mesh.textureMappingN = copyArray(model.textureMappingN);
		mesh.faceTypes = copyArray(model.faceTypes);
		mesh.vertexGroups = copyArray(model.vertexGroups);
		mesh.vertexBones = copyArray(model.vertexBones);
		mesh.verticesX = copyArray(model.verticesX);
		mesh.verticesY = copyArray(model.verticesY);
		mesh.verticesZ = copyArray(model.verticesZ);
		mesh.numVertices = model.numVertices;
		return mesh;
    }

	protected static int[] copyArray(int[] a) {
		if(a == null)
			return null;
		return Arrays.copyOf(a, a.length);
	}

	protected static byte[] copyArray(byte[] a) {
		if(a == null)
			return null;
		return Arrays.copyOf(a, a.length);
	}

    protected static int[][] copyArray(int[][] a) {
    	if(a == null)
    		return null;
       return Arrays.copyOf(a, a.length);
    }

    public int id;
	public void retexture(int found, int replace) {
		if(faceTextures != null)
			for (int face = 0; face < faceTextures.length; face++) {
				if (faceTextures[face] == found) {
					log.info("[{}] {} | Replaced {} with {}", id, revision, faceTextures[face], replace);
					faceTextures[face] = replace;
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
