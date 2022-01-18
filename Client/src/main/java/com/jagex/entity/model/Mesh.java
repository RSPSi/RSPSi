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
import com.rspsi.core.misc.ToolType;
import com.rspsi.options.Options;

import lombok.Getter;
import lombok.Setter;

@Slf4j
public class Mesh extends Renderable {

    // Class30_Sub2_Sub4_Sub6

    public byte[] textureMap;
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
    public int[] shadedtriangleColorsX;
    public int[] shadedtriangleColorsY;
    public int[] shadedtriangleColorsZ;
    public int[] faceTransparencies;
    public int[] triangleColors;
    public int[] faceMaterial;
    public int[][] faceGroups;
    public byte[] faceRenderPriorities;
    public int triangleCount;
    public int[] packedTransparencyVertexGroups;
    public int[] faceIndices1;
    public int[] faceIndices2;
    public int[] faceIndices3;
    public VertexNormal[] normals;
    public byte priority;
    public int numTextureFaces;
    public int[] texIndices1;
    public int[] texIndices2;
    public int[] texIndices3;
    public int[] triangleInfo;
    public int[][] vertexGroups;
    public int[] packedVertexGroups;
    public int[] vertexX;
    public int[] vertexY;
    public int[] vertexZ;
    public int vertexCount;
    private boolean translucent;

    //public List<Vector3f> vertexes;

    protected Mesh() {
    }

    public Mesh(boolean contouredGround, boolean delayShading, Mesh model) {
        vertexCount = model.vertexCount;
        triangleCount = model.triangleCount;
        numTextureFaces = model.numTextureFaces;

        if (contouredGround) {
            vertexY = new int[vertexCount];

            for (int vertex = 0; vertex < vertexCount; vertex++) {
                vertexY[vertex] = model.vertexY[vertex];
            }
        } else {
            vertexY = model.vertexY;
        }

        if (delayShading) {
            shadedtriangleColorsX = new int[triangleCount];
            shadedtriangleColorsY = new int[triangleCount];
            shadedtriangleColorsZ = new int[triangleCount];

            for (int k = 0; k < triangleCount; k++) {
                shadedtriangleColorsX[k] = model.shadedtriangleColorsX[k];
                shadedtriangleColorsY[k] = model.shadedtriangleColorsY[k];
                shadedtriangleColorsZ[k] = model.shadedtriangleColorsZ[k];
            }

            triangleInfo = new int[triangleCount];
            if (model.triangleInfo == null) {
                for (int triangle = 0; triangle < triangleCount; triangle++) {
                    triangleInfo[triangle] = 0;
                }
            } else {
                for (int index = 0; index < triangleCount; index++) {
                    triangleInfo[index] = model.triangleInfo[index];
                }
            }

            super.normals = new VertexNormal[vertexCount];
            for (int index = 0; index < vertexCount; index++) {
                VertexNormal parent = super.normals[index] = new VertexNormal();
                VertexNormal copied = model.getNormal(index);
                parent.setX(copied.getX());
                parent.setY(copied.getY());
                parent.setZ(copied.getZ());
                parent.setMagnitude(copied.getMagnitude());
            }

            normals = model.normals;
        } else {
            shadedtriangleColorsX = model.shadedtriangleColorsX;
            shadedtriangleColorsY = model.shadedtriangleColorsY;
            shadedtriangleColorsZ = model.shadedtriangleColorsZ;
            triangleInfo = model.triangleInfo;
        }

        vertexX = model.vertexX;
        vertexZ = model.vertexZ;
        faceMaterial = model.faceMaterial;
        triangleColors = model.triangleColors;
        faceTransparencies = model.faceTransparencies;
        faceRenderPriorities = model.faceRenderPriorities;
        priority = model.priority;
        faceIndices1 = model.faceIndices1;
        faceIndices2 = model.faceIndices2;
        faceIndices3 = model.faceIndices3;
        faceTexture = model.faceTexture;
        textureMap = model.textureMap;
        texIndices1 = model.texIndices1;
        texIndices2 = model.texIndices2;
        texIndices3 = model.texIndices3;
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
        this.vertexCount = 0;
        this.triangleCount = 0;
        this.priority = 0;
        //this.field1490 = false;
        boolean var3 = false;
        boolean var4 = false;
        boolean var5 = false;
        boolean var6 = false;
        boolean var7 = false;
        boolean var8 = false;
        this.vertexCount = 0;
        this.triangleCount = 0;
        this.numTextureFaces = 0;
        this.priority = -1;

        int var9;
        Mesh var10;
        for (var9 = 0; var9 < modelCount; ++var9) {
            var10 = models[var9];
            if (var10 != null) {
                this.vertexCount += var10.vertexCount;
                this.triangleCount += var10.triangleCount;
                this.numTextureFaces += var10.numTextureFaces;
                if (var10.faceRenderPriorities != null) {
                    var4 = true;
                } else {
                    if (this.priority == -1) {
                        this.priority = var10.priority;
                    }

                    if (this.priority != var10.priority) {
                        var4 = true;
                    }
                }

                var3 |= var10.triangleInfo != null;
                var5 |= var10.faceTransparencies != null;
                var6 |= var10.packedTransparencyVertexGroups != null;
                var7 |= var10.faceMaterial != null;
                var8 |= var10.faceTexture != null;
            }
        }

        this.vertexX = new int[this.vertexCount];
        this.vertexY = new int[this.vertexCount];
        this.vertexZ = new int[this.vertexCount];
        this.packedVertexGroups = new int[this.vertexCount];
        this.faceIndices1 = new int[this.triangleCount];
        this.faceIndices2 = new int[this.triangleCount];
        this.faceIndices3 = new int[this.triangleCount];
        if (var3) {
            this.triangleInfo = new int[this.triangleCount];
        }

        if (var4) {
            this.faceRenderPriorities = new byte[this.triangleCount];
        }

        if (var5) {
            this.faceTransparencies = new int[this.triangleCount];
        }

        if (var6) {
            this.packedTransparencyVertexGroups = new int[this.triangleCount];
        }

        if (var7) {
            this.faceMaterial = new int[this.triangleCount];
        }

        if (var8) {
            this.faceTexture = new byte[this.triangleCount];
        }

        this.triangleColors = new int[this.triangleCount];
        if (this.numTextureFaces > 0) {
            this.textureMap = new byte[this.numTextureFaces];
            this.texIndices1 = new int[this.numTextureFaces];
            this.texIndices2 = new int[this.numTextureFaces];
            this.texIndices3 = new int[this.numTextureFaces];
        }

        this.vertexCount = 0;
        this.triangleCount = 0;
        this.numTextureFaces = 0;

        for (var9 = 0; var9 < modelCount; ++var9) {
            var10 = models[var9];
            if (var10 != null) {
                int var11;
                for (var11 = 0; var11 < var10.triangleCount; ++var11) {
                    if (var3 && var10.triangleInfo != null) {
                        this.triangleInfo[this.triangleCount] = var10.triangleInfo[var11];
                    }

                    if (var4) {
                        if (var10.faceRenderPriorities != null) {
                            this.faceRenderPriorities[this.triangleCount] = var10.faceRenderPriorities[var11];
                        } else {
                            this.faceRenderPriorities[this.triangleCount] = var10.priority;
                        }
                    }

                    if (var5 && var10.faceTransparencies != null) {
                        this.faceTransparencies[this.triangleCount] = var10.faceTransparencies[var11];
                    }

                    if (var6 && var10.packedTransparencyVertexGroups != null) {
                        this.packedTransparencyVertexGroups[this.triangleCount] = var10.packedTransparencyVertexGroups[var11];
                    }

                    if (var7) {
                        if (var10.faceMaterial != null) {
                            this.faceMaterial[this.triangleCount] = var10.faceMaterial[var11];
                        } else {
                            this.faceMaterial[this.triangleCount] = -1;
                        }
                    }

                    if (var8) {
                        if (var10.faceTexture != null && var10.faceTexture[var11] != -1) {
                            this.faceTexture[this.triangleCount] = (byte) (this.numTextureFaces + var10.faceTexture[var11]);
                        } else {
                            this.faceTexture[this.triangleCount] = -1;
                        }
                    }

                    this.triangleColors[this.triangleCount] = var10.triangleColors[var11];
                    this.faceIndices1[this.triangleCount] = this.findMatchingVertex(var10, var10.faceIndices1[var11]);
                    this.faceIndices2[this.triangleCount] = this.findMatchingVertex(var10, var10.faceIndices2[var11]);
                    this.faceIndices3[this.triangleCount] = this.findMatchingVertex(var10, var10.faceIndices3[var11]);
                    ++this.triangleCount;
                }

                for (var11 = 0; var11 < var10.numTextureFaces; ++var11) {
                    byte var12 = this.textureMap[this.numTextureFaces] = var10.textureMap[var11];
                    if (var12 == 0) {
                        this.texIndices1[this.numTextureFaces] = (short) this.findMatchingVertex(var10, var10.texIndices1[var11]);
                        this.texIndices2[this.numTextureFaces] = (short) this.findMatchingVertex(var10, var10.texIndices2[var11]);
                        this.texIndices3[this.numTextureFaces] = (short) this.findMatchingVertex(var10, var10.texIndices3[var11]);
                    }

                    ++this.numTextureFaces;
                }
            }
        }

    }

    public Mesh(Mesh model, boolean shareColours, boolean shareAlphas, boolean shareVertices, boolean shareTextures) {
        vertexCount = model.vertexCount;
        triangleCount = model.triangleCount;
        numTextureFaces = model.numTextureFaces;

        if (shareVertices) {
            vertexX = model.vertexX;
            vertexY = model.vertexY;
            vertexZ = model.vertexZ;
        } else {
            vertexX = copyArray(model.vertexX);
            vertexY = copyArray(model.vertexY);
            vertexZ = copyArray(model.vertexZ);
        }

        if (!shareColours && model.triangleColors != null) {
            triangleColors = copyArray(model.triangleColors);
        } else {
            triangleColors = model.triangleColors;
        }

        if (!shareTextures && model.faceMaterial != null) {
            this.faceMaterial = copyArray(model.faceMaterial);
        } else {
            this.faceMaterial = model.faceMaterial;
        }

        if (shareAlphas) {
            faceTransparencies = model.faceTransparencies;
        } else {
            if (model.faceTransparencies == null) {
                faceTransparencies = new int[triangleCount];
                Arrays.fill(faceTransparencies, 0);
                for (int face = 0; face < triangleCount; face++) {
                    faceTransparencies[face] = 0;
                }
            } else {
                faceTransparencies = copyArray(model.faceTransparencies);
            }
        }

        packedVertexGroups = model.packedVertexGroups;
        packedTransparencyVertexGroups = model.packedTransparencyVertexGroups;
        textureMap = model.textureMap;
        faceTexture = model.faceTexture;
        triangleInfo = model.triangleInfo;
        faceIndices1 = model.faceIndices1;
        faceIndices2 = model.faceIndices2;
        faceIndices3 = model.faceIndices3;
        faceRenderPriorities = model.faceRenderPriorities;
        priority = model.priority;
        texIndices1 = model.texIndices1;
        texIndices2 = model.texIndices2;
        texIndices3 = model.texIndices3;
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

    public void calculateExtreme(int orientation) {
        if (this.extremeX == -1) {
            int var2 = 0;
            int var3 = 0;
            int var4 = 0;
            int var5 = 0;
            int var6 = 0;
            int var7 = 0;
            int var8 = Constants.COSINE[orientation];
            int var9 = Constants.SINE[orientation];

            for (int var10 = 0; var10 < this.vertexCount; ++var10) {
                int var11 = method3027(this.vertexX[var10], this.vertexZ[var10], var8, var9);
                int var12 = this.vertexY[var10];
                int var13 = method3028(this.vertexX[var10], this.vertexZ[var10], var8, var9);
                if (var11 < var2) {
                    var2 = var11;
                }

                if (var11 > var5) {
                    var5 = var11;
                }

                if (var12 < var3) {
                    var3 = var12;
                }

                if (var12 > var6) {
                    var6 = var12;
                }

                if (var13 < var4) {
                    var4 = var13;
                }

                if (var13 > var7) {
                    var7 = var13;
                }
            }

            this.centerX = (var5 + var2) / 2;
            this.centerY = (var6 + var3) / 2;
            this.centerZ = (var7 + var4) / 2;
            this.extremeX = (var5 - var2 + 1) / 2;
            this.extremeY = (var6 - var3 + 1) / 2;
            this.extremeZ = (var7 - var4 + 1) / 2;
            if (this.extremeX < 32) {
                this.extremeX = 32;
            }

            if (this.extremeZ < 32) {
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

        for (int vertex = 0; vertex < vertexCount; vertex++) {
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

        for (int vertex = 0; vertex < vertexCount; vertex++) {
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

        for (int vertex = 0; vertex < vertexCount; vertex++) {
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

        for (int index = 0; index < vertexCount; index++) {
            if (x == vertexX[index] && y == vertexY[index] && z == vertexZ[index]) {
                matched = index;
                break;
            }
        }

        if (matched == -1) {
            vertexX[vertexCount] = x;
            vertexY[vertexCount] = y;
            vertexZ[vertexCount] = z;

            if (model.packedVertexGroups != null) {
                packedVertexGroups[vertexCount] = model.packedVertexGroups[vertex];
            }

            matched = vertexCount++;
        }

        return matched;
    }

    public void invert() {
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertexZ[vertex] = -vertexZ[vertex];
        }

        for (int vertex = 0; vertex < triangleCount; vertex++) {
            int x = faceIndices1[vertex];
            faceIndices1[vertex] = faceIndices3[vertex];
            faceIndices3[vertex] = x;
        }
    }

    public final void light(int lighting, int diffusion, int x, int y, int z, boolean immediateShading) {
        int length = (int) Math.sqrt(x * x + y * y + z * z);
        int k1 = diffusion * length >> 8;

        if (shadedtriangleColorsX == null) {
            shadedtriangleColorsX = new int[triangleCount];
            shadedtriangleColorsY = new int[triangleCount];
            shadedtriangleColorsZ = new int[triangleCount];
        }

        if (super.normals == null) {
            super.normals = new VertexNormal[vertexCount];
            for (int index = 0; index < vertexCount; index++) {
                super.normals[index] = new VertexNormal();
            }
        }

        for (int face = 0; face < triangleCount; face++) {
            int faceX = faceIndices1[face];
            int faceY = faceIndices2[face];
            int faceZ = faceIndices3[face];
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

            if (triangleInfo == null || (triangleInfo[face] & 1) == 0) {
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
                shadedtriangleColorsX[face] = checkedLight(triangleColors[face], l5, triangleInfo[face]);
            }
        }

        if (immediateShading) {
            shade(lighting, k1, x, y, z);
        } else {
            normals = new VertexNormal[vertexCount];
            for (int index = 0; index < vertexCount; index++) {
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
        vertexCount = model.vertexCount;
        triangleCount = model.triangleCount;
        numTextureFaces = model.numTextureFaces;

        if (anIntArray1622.length < vertexCount) {
            anIntArray1622 = new int[vertexCount + 100];
            anIntArray1623 = new int[vertexCount + 100];
            anIntArray1624 = new int[vertexCount + 100];
        }

        vertexX = anIntArray1622;
        vertexY = anIntArray1623;
        vertexZ = anIntArray1624;
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertexX[vertex] = model.vertexX[vertex];
            vertexY[vertex] = model.vertexY[vertex];
            vertexZ[vertex] = model.vertexZ[vertex];
        }

        if (shareAlphas) {
            faceTransparencies = model.faceTransparencies;
        } else {
            if (anIntArray1625.length < triangleCount) {
                anIntArray1625 = new int[triangleCount + 100];
            }
            faceTransparencies = anIntArray1625;

            if (model.faceTransparencies == null) {
                for (int index = 0; index < triangleCount; index++) {
                    faceTransparencies[index] = 0;
                }
            } else {
                for (int index = 0; index < triangleCount; index++) {
                    faceTransparencies[index] = model.faceTransparencies[index];
                }
            }
        }

        triangleInfo = model.triangleInfo;
        textureMap = model.textureMap;
        faceTexture = model.faceTexture;
        triangleColors = model.triangleColors;
        faceMaterial = model.faceMaterial;
        faceRenderPriorities = model.faceRenderPriorities;
        priority = model.priority;
        faceGroups = model.faceGroups;
        vertexGroups = model.vertexGroups;
        faceIndices1 = model.faceIndices1;
        faceIndices2 = model.faceIndices2;
        faceIndices3 = model.faceIndices3;
        shadedtriangleColorsX = model.shadedtriangleColorsX;
        shadedtriangleColorsY = model.shadedtriangleColorsY;
        shadedtriangleColorsZ = model.shadedtriangleColorsZ;
        texIndices1 = model.texIndices1;
        texIndices2 = model.texIndices2;
        texIndices3 = model.texIndices3;
    }

    private static ObjectKey activeKey;

    private void renderFaces(GameRasterizer rasterizer, boolean flag, boolean multiTileFlag, ObjectKey key, int z) {
        for (int j = 0; j < boundingSphereRadius; j++) {
            rasterizer.depthListIndices[j] = 0;
        }

        activeKey = key;

        for (int face = 0; face < triangleCount; face++) {
            if (triangleInfo == null || triangleInfo[face] != -1) {
                int indexX = faceIndices1[face];
                int indexY = faceIndices2[face];
                int indexZ = faceIndices3[face];
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
                                    if (Client.hoveredUID == null)
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
                    if (key != null) {
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
                        if (k5 >= 0 && k5 < rasterizer.faceList.length)
                            rasterizer.faceList[k5][rasterizer.depthListIndices[k5]++] = face;
                    }
                }
            }
        }

        if (faceRenderPriorities == null) {
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
                    int l5 = faceRenderPriorities[l4];
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
        int faceX = faceIndices1[index];
        int faceY = faceIndices2[index];
        int faceZ = faceIndices3[index];
        rasterizer.restrictEdges = rasterizer.cullFaces[index];
        if (selected) {
            rasterizer.currentAlpha = translucent ? 100 : 50;
        }
        if (translucent) {
            rasterizer.currentAlpha = 140;
        } else if (!selected) {
            if (faceTransparencies == null) {
                rasterizer.currentAlpha = 0;
            } else {
                rasterizer.currentAlpha = faceTransparencies[index];
            }
        }
        int type;
        if (triangleInfo == null) {
            type = faceMaterial != null && faceMaterial[index] != -1 ? 2 : 0;
        } else {
            type = triangleInfo[index] & 3;

        }
        boolean ignoreTextures = translucent || selected;

        if (type == 0 && !ignoreTextures) {
            rasterizer.drawShadedTriangle(rasterizer.vertexScreenY[faceX], rasterizer.vertexScreenY[faceY], rasterizer.vertexScreenY[faceZ], rasterizer.vertexScreenX[faceX],
                    rasterizer.vertexScreenX[faceY], rasterizer.vertexScreenX[faceZ], shadedtriangleColorsX[index], shadedtriangleColorsY[index], shadedtriangleColorsZ[index]);
        } else if (type == 1 || ignoreTextures) {
            int colour = selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedtriangleColorsX[index]];
            rasterizer.drawShadedTriangle(rasterizer.vertexScreenY[faceX], rasterizer.vertexScreenY[faceY], rasterizer.vertexScreenY[faceZ], rasterizer.vertexScreenX[faceX],
                    rasterizer.vertexScreenX[faceY], rasterizer.vertexScreenX[faceZ], colour);
        } else if (type == 2 || type == 3) {

            int texFaceX = 0, texFaceY = 0, texFaceZ = 0;
            try {
                if (faceTexture != null && faceTexture[index] != -1) {
                    int k1 = faceTexture[index] & 0xFF;
                    texFaceX = texIndices1[k1];
                    texFaceY = texIndices2[k1];
                    texFaceZ = texIndices3[k1];
                } else {
                    texFaceX = faceX;
                    texFaceY = faceY;
                    texFaceZ = faceZ;

                }

                if (texFaceX >= 4096 || texFaceY >= 4096 || texFaceZ >= 4096) {
                    texFaceX = faceX;
                    texFaceY = faceY;
                    texFaceZ = faceZ;
                }

                int colourX = shadedtriangleColorsX[index];
                int colourY = shadedtriangleColorsX[index];
                int colourZ = shadedtriangleColorsX[index];

                if (type == 2) {
                    colourY = shadedtriangleColorsY[index];
                    colourZ = shadedtriangleColorsZ[index];
                }

                int texId = faceMaterial[index];
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
        int faceX = faceIndices1[index];
        int faceY = faceIndices2[index];
        int faceZ = faceIndices3[index];
        int l1 = rasterizer.camera_vertex_z[faceX];
        int i2 = rasterizer.camera_vertex_z[faceY];
        int j2 = rasterizer.camera_vertex_z[faceZ];
        if (l1 >= 50) {
            rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[faceX];
            rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[faceX];
            rasterizer.anIntArray1680[l++] = shadedtriangleColorsX[index];
        } else {
            int k2 = rasterizer.camera_vertex_x[faceX];
            int k3 = rasterizer.camera_vertex_y[faceX];
            int k4 = shadedtriangleColorsX[index];
            if (j2 >= 50) {
                int k5 = (50 - l1) * Constants.LIGHT_DECAY[j2 - l1];
                rasterizer.anIntArray1678[l] = viewX + (k2 + ((rasterizer.camera_vertex_x[faceZ] - k2) * k5 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (k3 + ((rasterizer.camera_vertex_y[faceZ] - k3) * k5 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = k4 + ((shadedtriangleColorsZ[index] - k4) * k5 >> 16);
            }
            if (i2 >= 50) {
                int l5 = (50 - l1) * Constants.LIGHT_DECAY[i2 - l1];
                rasterizer.anIntArray1678[l] = viewX + (k2 + ((rasterizer.camera_vertex_x[faceY] - k2) * l5 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (k3 + ((rasterizer.camera_vertex_y[faceY] - k3) * l5 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = k4 + ((shadedtriangleColorsY[index] - k4) * l5 >> 16);
            }
        }
        if (i2 >= 50) {
            rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[faceY];
            rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[faceY];
            rasterizer.anIntArray1680[l++] = shadedtriangleColorsY[index];
        } else {
            int l2 = rasterizer.camera_vertex_x[faceY];
            int l3 = rasterizer.camera_vertex_y[faceY];
            int l4 = shadedtriangleColorsY[index];
            if (l1 >= 50) {
                int i6 = (50 - i2) * Constants.LIGHT_DECAY[l1 - i2];
                rasterizer.anIntArray1678[l] = viewX + (l2 + ((rasterizer.camera_vertex_x[faceX] - l2) * i6 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (l3 + ((rasterizer.camera_vertex_y[faceX] - l3) * i6 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = l4 + ((shadedtriangleColorsX[index] - l4) * i6 >> 16);
            }
            if (j2 >= 50) {
                int j6 = (50 - i2) * Constants.LIGHT_DECAY[j2 - i2];
                rasterizer.anIntArray1678[l] = viewX + (l2 + ((rasterizer.camera_vertex_x[faceZ] - l2) * j6 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (l3 + ((rasterizer.camera_vertex_y[faceZ] - l3) * j6 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = l4 + ((shadedtriangleColorsZ[index] - l4) * j6 >> 16);
            }
        }
        if (j2 >= 50) {
            rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[faceZ];
            rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[faceZ];
            rasterizer.anIntArray1680[l++] = shadedtriangleColorsZ[index];
        } else {
            int i3 = rasterizer.camera_vertex_x[faceZ];
            int i4 = rasterizer.camera_vertex_y[faceZ];
            int i5 = shadedtriangleColorsZ[index];
            if (i2 >= 50) {
                int k6 = (50 - j2) * Constants.LIGHT_DECAY[i2 - j2];
                rasterizer.anIntArray1678[l] = viewX + (i3 + ((rasterizer.camera_vertex_x[faceY] - i3) * k6 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (i4 + ((rasterizer.camera_vertex_y[faceY] - i4) * k6 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = i5 + ((shadedtriangleColorsY[index] - i5) * k6 >> 16);
            }
            if (l1 >= 50) {
                int l6 = (50 - j2) * Constants.LIGHT_DECAY[l1 - j2];
                rasterizer.anIntArray1678[l] = viewX + (i3 + ((rasterizer.camera_vertex_x[faceX] - i3) * l6 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (i4 + ((rasterizer.camera_vertex_y[faceX] - i4) * l6 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = i5 + ((shadedtriangleColorsX[index] - i5) * l6 >> 16);
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
                if (triangleInfo == null) {
                    type = faceMaterial != null && faceMaterial[index] != -1 ? 2 : 0;
                } else {
                    type = triangleInfo[index] & 3;
                }

                if (type == 0 && !ignoreTextures) {
                    rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
                            rasterizer.anIntArray1680[2]);
                } else if (type == 1 || ignoreTextures) {
                    rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5,
                            selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedtriangleColorsX[index]]);
                } else if (type == 2) {
                    int texFaceX, texFaceY, texFaceZ;
                    if (faceTexture != null && faceTexture[index] != -1) {
                        int texFaceIndex = faceTexture[index] & 0xFF;
                        texFaceX = texIndices1[texFaceIndex];
                        texFaceY = texIndices2[texFaceIndex];
                        texFaceZ = texIndices3[texFaceIndex];
                    } else {
                        texFaceX = faceX;
                        texFaceY = faceY;
                        texFaceZ = faceZ;
                    }


                    if (texFaceX >= 4096 || texFaceY >= 4096 || texFaceZ >= 4096) {
                        texFaceX = faceX;
                        texFaceY = faceY;
                        texFaceZ = faceZ;
                    }

                    rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
                            rasterizer.anIntArray1680[2], rasterizer.camera_vertex_x[texFaceX], rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ],
                            rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY], rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX],
                            rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ], faceMaterial[index]);
                } else if (type == 3) {
                    int texFaceX, texFaceY, texFaceZ;
                    if (faceTexture != null && faceTexture[index] != -1) {
                        int texFaceIndex = faceTexture[index] & 0xFF;
                        texFaceX = texIndices1[texFaceIndex];
                        texFaceY = texIndices2[texFaceIndex];
                        texFaceZ = texIndices3[texFaceIndex];
                    } else {
                        texFaceX = faceX;
                        texFaceY = faceY;
                        texFaceZ = faceZ;
                    }


                    if (texFaceX >= 4096 || texFaceY >= 4096 || texFaceZ >= 4096) {
                        texFaceX = faceX;
                        texFaceY = faceY;
                        texFaceZ = faceZ;
                    }

                    rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, shadedtriangleColorsX[index], shadedtriangleColorsX[index],
                            shadedtriangleColorsX[index], rasterizer.camera_vertex_x[texFaceX], rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ],
                            rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY], rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX],
                            rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ], faceMaterial[index]);
                }
            }
            if (l == 4) {
                if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > rasterizer.getMaxRight() || j4 > rasterizer.getMaxRight() || j5 > rasterizer.getMaxRight()
                        || rasterizer.anIntArray1678[3] < 0 || rasterizer.anIntArray1678[3] > rasterizer.getMaxRight()) {
                    rasterizer.restrictEdges = true;
                }
                int type;
                if (triangleInfo == null) {
                    type = faceMaterial != null && faceMaterial[index] != -1 ? 2 : 0;
                } else {
                    type = triangleInfo[index] & 3;
                }
                if (type == 0 && !ignoreTextures) {
                    rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
                            rasterizer.anIntArray1680[2]);
                    rasterizer.drawShadedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
                            rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[2], rasterizer.anIntArray1680[3]);
                    return;
                } else if (type == 1 || ignoreTextures) {
                    int l8 = selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedtriangleColorsX[index]];
                    rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, l8);
                    rasterizer.drawShadedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3], l8);
                    return;
                } else if (type == 2) {
                    int texFaceX, texFaceY, texFaceZ;
                    if (faceTexture != null && faceTexture[index] != -1) {
                        int texFaceIndex = faceTexture[index] & 0xFF;
                        texFaceX = texIndices1[texFaceIndex];
                        texFaceY = texIndices2[texFaceIndex];
                        texFaceZ = texIndices3[texFaceIndex];
                    } else {
                        texFaceX = faceX;
                        texFaceY = faceY;
                        texFaceZ = faceZ;
                    }


                    if (texFaceX >= 4096 || texFaceY >= 4096 || texFaceZ >= 4096) {
                        texFaceX = faceX;
                        texFaceY = faceY;
                        texFaceZ = faceZ;
                    }

                    rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
                            rasterizer.anIntArray1680[2], rasterizer.camera_vertex_x[texFaceX], rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ],
                            rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY], rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX],
                            rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ], faceMaterial[index]);
                    rasterizer.drawTexturedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
                            rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[2], rasterizer.anIntArray1680[3], rasterizer.camera_vertex_x[texFaceX],
                            rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ], rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY],
                            rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX], rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ],
                            faceMaterial[index]);
                    return;
                } else if (type == 3) {
                    int texFaceX, texFaceY, texFaceZ;
                    if (faceTexture != null && faceTexture[index] != -1) {
                        int texFaceIndex = faceTexture[index] & 0xFF;
                        texFaceX = texIndices1[texFaceIndex];
                        texFaceY = texIndices2[texFaceIndex];
                        texFaceZ = texIndices3[texFaceIndex];
                    } else {
                        texFaceX = faceX;
                        texFaceY = faceY;
                        texFaceZ = faceZ;
                    }


                    if (texFaceX >= 4096 || texFaceY >= 4096 || texFaceZ >= 4096) {
                        texFaceX = faceX;
                        texFaceY = faceY;
                        texFaceZ = faceZ;
                    }

                    rasterizer.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, shadedtriangleColorsX[index], shadedtriangleColorsX[index],
                            shadedtriangleColorsX[index], rasterizer.camera_vertex_x[texFaceX], rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ],
                            rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY], rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX],
                            rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ], faceMaterial[index]);
                    rasterizer.drawTexturedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
                            shadedtriangleColorsX[index], shadedtriangleColorsX[index], shadedtriangleColorsX[index], rasterizer.camera_vertex_x[texFaceX],
                            rasterizer.camera_vertex_x[texFaceY], rasterizer.camera_vertex_x[texFaceZ], rasterizer.camera_vertex_y[texFaceX], rasterizer.camera_vertex_y[texFaceY],
                            rasterizer.camera_vertex_y[texFaceZ], rasterizer.camera_vertex_z[texFaceX], rasterizer.camera_vertex_z[texFaceY], rasterizer.camera_vertex_z[texFaceZ],
                            faceMaterial[index]);
                }
            }
        }
    }

    public void pitch(int theta) {
        int sin = Constants.SINE[theta];
        int cos = Constants.COSINE[theta];

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            int y = vertexY[vertex] * cos - vertexZ[vertex] * sin >> 16;
            vertexZ[vertex] = vertexY[vertex] * sin + vertexZ[vertex] * cos >> 16;
            vertexY[vertex] = y;
        }
    }

    public void prepareSkeleton() {
        if (packedVertexGroups != null) {
            int[] sizes = new int[256];
            int maximumBoneId = 0;

            for (int vertex = 0; vertex < vertexCount; vertex++) {
                int bone = packedVertexGroups[vertex];
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

            for (int index = 0; index < vertexCount; index++) {
                int bone = packedVertexGroups[index];
                vertexGroups[bone][sizes[bone]++] = index;
            }

            packedVertexGroups = null;
        }

        if (packedTransparencyVertexGroups != null) {
            int[] sizes = new int[256];
            int count = 0;

            for (int index = 0; index < triangleCount; index++) {
                int skin = packedTransparencyVertexGroups[index];
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

            for (int index = 0; index < triangleCount; index++) {
                int skin = packedTransparencyVertexGroups[index];
                faceGroups[skin][sizes[skin]++] = index;
            }

            packedTransparencyVertexGroups = null;
        }
    }

    public void recolour(int oldColour, int newColour) {
        for (int index = 0; index < triangleCount; index++) {
            if (triangleColors[index] == oldColour) {
                triangleColors[index] = newColour;
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
        for (int k4 = 0; k4 < vertexCount; k4++) {
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
            if (numTextureFaces > 0) {
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
                    if (Client.hoveredUID == null)
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

        for (int vertex = 0; vertex < vertexCount; vertex++) {
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

            if (flag || numTextureFaces > 0) {
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
        for (int index = 0; index < vertexCount; index++) {
            int x = vertexX[index];
            vertexX[index] = vertexZ[index];
            vertexZ[index] = -x;
        }
    }

    public void offsetVertices(int x, int y, int z) {
        for (int index = 0; index < vertexCount; index++) {
            vertexX[index] += x;
            vertexX[index] += y;
            vertexZ[index] += z;
        }
    }

    public void scale(int x, int y, int z) {
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertexX[vertex] = vertexX[vertex] * x / 128;
            vertexY[vertex] = vertexY[vertex] * z / 128;
            vertexZ[vertex] = vertexZ[vertex] * y / 128;
        }
    }

    public void scale2(int i) {
        for (int i1 = 0; i1 < vertexCount; i1++) {
            vertexX[i1] = vertexX[i1] / i;
            vertexY[i1] = vertexY[i1] / i;
            vertexZ[i1] = vertexZ[i1] / i;
        }
    }


    public final void shade(int lighting, int j, int x, int y, int z) {
        for (int face = 0; face < triangleCount; face++) {
            int indexX = faceIndices1[face];
            int indexY = faceIndices2[face];
            int indexZ = faceIndices3[face];

            if (triangleInfo == null) {
                int colour = triangleColors[face];
                VertexNormal normal = super.normals[indexX];

                int light = lighting
                        + (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
                shadedtriangleColorsX[face] = checkedLight(colour, light, 0);

                normal = super.normals[indexY];
                light = lighting
                        + (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
                shadedtriangleColorsY[face] = checkedLight(colour, light, 0);

                normal = super.normals[indexZ];
                light = lighting
                        + (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
                shadedtriangleColorsZ[face] = checkedLight(colour, light, 0);
            } else if ((triangleInfo[face] & 1) == 0) {
                int colour = triangleColors[face];
                int point = triangleInfo[face];

                VertexNormal normal = super.normals[indexX];
                int light = lighting
                        + (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
                shadedtriangleColorsX[face] = checkedLight(colour, light, point);
                normal = super.normals[indexY];

                light = lighting
                        + (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
                shadedtriangleColorsY[face] = checkedLight(colour, light, point);
                normal = super.normals[indexZ];

                light = lighting
                        + (x * normal.getX() + y * normal.getY() + z * normal.getZ()) / (j * normal.getMagnitude());
                shadedtriangleColorsZ[face] = checkedLight(colour, light, point);
            }
        }

        super.normals = null;
        normals = null;
        packedVertexGroups = null;
        packedTransparencyVertexGroups = null;
        if (triangleInfo != null) {
            for (int index = 0; index < triangleCount; index++) {
                if ((triangleInfo[index] & 2) == 2)
                    return;
            }
        }

        triangleColors = null;
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
        } else if (transformation == FrameConstants.ALPHA_TRANSFORMATION && faceGroups != null && faceTransparencies != null) {
            for (int index = 0; index < count; index++) {
                int group = groups[index];

                if (group < faceGroups.length) {
                    for (int face : faceGroups[group]) {
                        faceTransparencies[face] += dx * 8;

                        if (faceTransparencies[face] < 0) {
                            faceTransparencies[face] = 0;
                        } else if (faceTransparencies[face] > 255) {
                            faceTransparencies[face] = 255;
                        }
                    }
                }
            }
        }
    }

    public void translate(int x, int y, int z) {
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertexX[vertex] += x;
            vertexY[vertex] += y;
            vertexZ[vertex] += z;
        }
    }

    public void convertTexturesToOldFormat() {

        if (faceMaterial == null || faceTexture == null) {
            return;
        }

        for (int material : faceMaterial) {
            if (material > TextureLoader.instance.count()) {
                return;
            }
        }

        if (triangleInfo == null) {
            triangleInfo = new int[triangleCount];
        }

        for (int i = 0; i < triangleCount; i++) {
            if (faceMaterial[i] != -1 && faceTexture[i] >= 0) {
                int mask = 2 + (faceTexture[i] << 2);
                triangleInfo[i] = mask;
                triangleColors[i] = faceMaterial[i];
            }
        }
    }

    public void convertTexturesTo317(int[] textureIds, int[] texa, int[] texb, int[] texc) {
        int set = 0;
        int set2 = 0;
        int max = TextureLoader.instance.count();
        if (textureIds != null) {
            texIndices1 = new int[triangleCount];
            texIndices2 = new int[triangleCount];
            texIndices3 = new int[triangleCount];

            for (int i = 0; i < triangleCount; i++) {
                if (textureIds[i] == -1 && this.triangleInfo[i] == 2) {
                    this.triangleColors[i] = 65535;
                    triangleInfo[i] = 0;
                }
                if (textureIds[i] >= max || textureIds[i] < 0 || textureIds[i] == 39) {
                    triangleInfo[i] = 0;
                    continue;
                }
                triangleInfo[i] = 2 + set2;
                set2 += 4;
                int a = this.faceIndices1[i];
                int b = faceIndices2[i];
                int c = faceIndices3[i];
                triangleColors[i] = textureIds[i];

                int texture_type = -1;
                if (this.faceTexture != null) {
                    texture_type = faceTexture[i] & 0xff;
                    if (texture_type != 0xff)
                        if (texa[texture_type] >= 4096 || texb[texture_type] >= 4096
                                || texc[texture_type] >= 4096)
                            texture_type = -1;
                }
                if (texture_type == 0xff)
                    texture_type = -1;

                texIndices1[set] = texture_type == -1 ? a : texa[texture_type];
                texIndices2[set] = texture_type == -1 ? b : texb[texture_type];
                texIndices3[set++] = texture_type == -1 ? c : texc[texture_type];

            }
            this.numTextureFaces = set;
        }
    }

    public void filterTriangles() {
        for (int triangleId = 0; triangleId < faceIndices1.length; triangleId++) {
            int l = faceIndices1[triangleId];
            int k1 = faceIndices2[triangleId];
            int j2_ = faceIndices3[triangleId];
            boolean b = true;
            label2:
            for (int triId = 0; triId < faceIndices1.length; triId++) {
                if (triId == triangleId)
                    continue label2;
                if (faceIndices1[triId] == l) {
                    b = false;
                    break label2;
                }
                if (faceIndices2[triId] == k1) {
                    b = false;
                    break label2;
                }
                if (faceIndices3[triId] == j2_) {
                    b = false;
                    break label2;
                }
            }
            if (b) {
                if (triangleInfo != null)
                    // face_render_type[triangleId] = -1;
                    triangleInfo[triangleId] = 255;

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
        mesh.shadedtriangleColorsX = copyArray(model.shadedtriangleColorsX);
        mesh.shadedtriangleColorsY = copyArray(model.shadedtriangleColorsY);
        mesh.shadedtriangleColorsZ = copyArray(model.shadedtriangleColorsZ);
        mesh.faceTransparencies = copyArray(model.faceTransparencies);
        mesh.triangleColors = copyArray(model.triangleColors);
        mesh.faceMaterial = copyArray(model.faceMaterial);
        mesh.faceTexture = copyArray(model.faceTexture);
        mesh.textureMap = copyArray(model.textureMap);
        mesh.faceGroups = copyArray(model.faceGroups);
        mesh.faceRenderPriorities = copyArray(model.faceRenderPriorities);
        mesh.triangleCount = (model.triangleCount);
        mesh.packedTransparencyVertexGroups = copyArray(model.packedTransparencyVertexGroups);
        mesh.faceIndices1 = copyArray(model.faceIndices1);
        mesh.faceIndices2 = copyArray(model.faceIndices2);
        mesh.faceIndices3 = copyArray(model.faceIndices3);
        mesh.normals = (model.normals);
        mesh.priority = (model.priority);
        mesh.numTextureFaces = model.numTextureFaces;
        mesh.texIndices1 = copyArray(model.texIndices1);
        mesh.texIndices2 = copyArray(model.texIndices2);
        mesh.texIndices3 = copyArray(model.texIndices3);
        mesh.triangleInfo = copyArray(model.triangleInfo);
        mesh.vertexGroups = copyArray(model.vertexGroups);
        mesh.packedVertexGroups = copyArray(model.packedVertexGroups);
        mesh.vertexX = copyArray(model.vertexX);
        mesh.vertexY = copyArray(model.vertexY);
        mesh.vertexZ = copyArray(model.vertexZ);
        mesh.vertexCount = model.vertexCount;
        return mesh;
    }

    protected static int[] copyArray(int[] a) {
        if (a == null)
            return null;
        return Arrays.copyOf(a, a.length);
    }

    protected static byte[] copyArray(byte[] a) {
        if (a == null)
            return null;
        return Arrays.copyOf(a, a.length);
    }

    protected static int[][] copyArray(int[][] a) {
        if (a == null)
            return null;
        return Arrays.copyOf(a, a.length);
    }

    public int id;

    public void retexture(int found, int replace) {
        if (faceMaterial != null)
            for (int face = 0; face < faceMaterial.length; face++) {
                if (faceMaterial[face] == found) {
                    log.info("[{}] {} | Replaced {} with {}", id, revision, faceMaterial[face], replace);
                    faceMaterial[face] = replace;
                }
            }

    }

    protected byte[] faceTexture;

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
