package com.rspsi.jagex.entity.model;

import com.rspsi.jagex.cache.anim.Frame;
import com.rspsi.jagex.cache.anim.FrameBase;
import com.rspsi.jagex.cache.loader.anim.FrameLoader;
import com.rspsi.jagex.cache.loader.textures.TextureLoader;
import com.rspsi.jagex.draw.raster.GameRasterizer;
import com.rspsi.jagex.entity.Renderable;
import com.rspsi.jagex.util.Constants;
import com.rspsi.jagex.util.FrameConstants;
import com.rspsi.jagex.util.ObjectKey;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3i;

import java.util.Arrays;

import static com.rspsi.misc.ArrayUtil.copyArray;

@Slf4j
public class Mesh extends Renderable {

    // Class30_Sub2_Sub4_Sub6

    public static boolean aBoolean1684;
    public static int resourceCount;
    public static ObjectKey[] resourceIDTag = new ObjectKey[1000];
    public static Mesh EMPTY_MODEL = new Mesh();
    public static int mouseX;
    public static int mouseY;
    static int centroidX;
    static int centroidY;
    static int centroidZ;
    private static int[] anIntArray1622 = new int[2000];
    private static int[] anIntArray1623 = new int[2000];
    private static int[] anIntArray1624 = new int[2000];
    private static int[] anIntArray1625 = new int[2000];
    private static ObjectKey activeKey;
    public byte[] textureRenderTypes;
    public MeshRevision revision;
    public boolean fitsOnSingleSquare;
    public int minimumX;
    public int maximumX;
    public int maximumZ;
    public int minimumZ;
    public int minimumY;
    public int anInt1654;
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
    public VertexNormal[] field1821;
    public Vector3i[] faceNormals;
    public int facePriority;
    public int numTextures;
    public int[] textureMappingA;
    public int[] textureMappingB;
    public int[] textureMappingC;

    public int[] aShortArray2574;
    public int[] aShortArray2575;
    public int[] aShortArray2586;
    public int[] aShortArray2577;
    public int[] aShortArray2578;
    public int[] aByteArray2580;
    public int[] texturePrimaryColours;

    public int[] faceTypes;
    public int[][] vertexGroups;
    public int[] vertexBones;
    public int[] verticesX;
    public int[] verticesY;
    public int[] verticesZ;
    public int numVertices;
    public int ambient;
    public int contrast;
    protected byte[] textureCoordinates;
    private boolean hasAlpha;
    private boolean translucent;



    protected Mesh() {
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
        for (var9 = 0; var9 < modelCount; ++var9) {
            var10 = models[var9];
            if (var10 != null) {
                this.numVertices += var10.numVertices;
                this.numFaces += var10.numFaces;
                this.numTextures += var10.numTextures;
                if (var10.facePriorities != null) {
                    var4 = true;
                } else {
                    if (this.facePriority == -1) {
                        this.facePriority = var10.facePriority;
                    }

                    if (this.facePriority != var10.facePriority) {
                        var4 = true;
                    }
                }

                var3 |= var10.faceTypes != null;
                var5 |= var10.faceAlphas != null;
                var6 |= var10.faceSkin != null;
                var7 |= var10.faceTextures != null;
                var8 |= var10.textureCoordinates != null;
            }
        }

        this.verticesX = new int[this.numVertices];
        this.verticesY = new int[this.numVertices];
        this.verticesZ = new int[this.numVertices];
        this.vertexBones = new int[this.numVertices];
        this.faceIndicesA = new int[this.numFaces];
        this.faceIndicesB = new int[this.numFaces];
        this.faceIndicesC = new int[this.numFaces];
        if (var3) {
            this.faceTypes = new int[this.numFaces];
        }

        if (var4) {
            this.facePriorities = new int[this.numFaces];
        }

        if (var5) {
            this.faceAlphas = new int[this.numFaces];
            this.hasAlpha = true;
        }

        if (var6) {
            this.faceSkin = new int[this.numFaces];
        }

        if (var7) {
            this.faceTextures = new int[this.numFaces];
        }

        if (var8) {
            this.textureCoordinates = new byte[this.numFaces];
        }

        this.faceColours = new int[this.numFaces];
        if (this.numTextures > 0) {
            this.textureRenderTypes = new byte[this.numTextures];
            this.textureMappingA = new int[this.numTextures];
            this.textureMappingB = new int[this.numTextures];
            this.textureMappingC = new int[this.numTextures];
        }

        this.numVertices = 0;
        this.numFaces = 0;
        this.numTextures = 0;

        for (var9 = 0; var9 < modelCount; ++var9) {
            var10 = models[var9];
            if (var10 != null) {
                int var11;
                for (var11 = 0; var11 < var10.numFaces; ++var11) {
                    if (var3 && var10.faceTypes != null) {
                        this.faceTypes[this.numFaces] = var10.faceTypes[var11];
                    }

                    if (var4) {
                        if (var10.facePriorities != null) {
                            this.facePriorities[this.numFaces] = var10.facePriorities[var11];
                        } else {
                            this.facePriorities[this.numFaces] = var10.facePriority;
                        }
                    }

                    if (var5 && var10.faceAlphas != null) {
                        this.faceAlphas[this.numFaces] = var10.faceAlphas[var11];
                        this.hasAlpha |= var10.hasAlpha;
                    }

                    if (var6 && var10.faceSkin != null) {
                        this.faceSkin[this.numFaces] = var10.faceSkin[var11];
                    }

                    if (var7) {
                        if (var10.faceTextures != null) {
                            this.faceTextures[this.numFaces] = var10.faceTextures[var11];
                        } else {
                            this.faceTextures[this.numFaces] = -1;
                        }
                    }

                    if (var8) {
                        if (var10.textureCoordinates != null && var10.textureCoordinates[var11] != -1) {
                            this.textureCoordinates[this.numFaces] = (byte) (this.numTextures + var10.textureCoordinates[var11]);
                        } else {
                            this.textureCoordinates[this.numFaces] = -1;
                        }
                    }

                    this.faceColours[this.numFaces] = var10.faceColours[var11];
                    this.faceIndicesA[this.numFaces] = this.findMatchingVertex(var10, var10.faceIndicesA[var11]);
                    this.faceIndicesB[this.numFaces] = this.findMatchingVertex(var10, var10.faceIndicesB[var11]);
                    this.faceIndicesC[this.numFaces] = this.findMatchingVertex(var10, var10.faceIndicesC[var11]);
                    ++this.numFaces;
                }

                for (var11 = 0; var11 < var10.numTextures; ++var11) {
                    byte var12 = this.textureRenderTypes[this.numTextures] = var10.textureRenderTypes[var11];
                    if (var12 == 0) {
                        this.textureMappingA[this.numTextures] = (short) this.findMatchingVertex(var10, var10.textureMappingA[var11]);
                        this.textureMappingB[this.numTextures] = (short) this.findMatchingVertex(var10, var10.textureMappingB[var11]);
                        this.textureMappingC[this.numTextures] = (short) this.findMatchingVertex(var10, var10.textureMappingC[var11]);
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

        if (!shareTextures && model.faceTextures != null) {
            this.faceTextures = copyArray(model.faceTextures);
        } else {
            this.faceTextures = model.faceTextures;
        }

        if (shareAlphas) {
            faceAlphas = model.faceAlphas;
            this.hasAlpha = model.hasAlpha;
        } else {
            if (model.faceAlphas == null) {
                faceAlphas = new int[numFaces];
                Arrays.fill(faceAlphas, 0);
                this.hasAlpha = false;
            } else {
                faceAlphas = copyArray(model.faceAlphas);
                this.hasAlpha = model.hasAlpha;
            }
        }

        vertexBones = model.vertexBones;
        faceSkin = model.faceSkin;
        textureRenderTypes = model.textureRenderTypes;
        textureCoordinates = model.textureCoordinates;
        faceTypes = model.faceTypes;
        faceIndicesA = model.faceIndicesA;
        faceIndicesB = model.faceIndicesB;
        faceIndicesC = model.faceIndicesC;
        facePriorities = model.facePriorities;
        facePriority = model.facePriority;
        textureMappingA = model.textureMappingA;
        textureMappingB = model.textureMappingB;
        textureMappingC = model.textureMappingC;
        ambient = model.ambient;
        contrast = model.contrast;
    }

    public static int checkedLight(int colour, int light) {
        light = light * (colour & 0x7f) >> 7;
        if (light < 2) {
            light = 2;
        } else if (light > 126) {
            light = 126;
        }

        return (colour & 0xff80) + light;
    }

    public static int clampLight(int light) {
        if (light < 2) {
            light = 2;
        } else if (light > 126) {
            light = 126;
        }

        return light;
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

    public void calculateFaceTypes() {
        if (faceTypes == null) {
            faceTypes = new int[numFaces];
        }
    }

    public final ModelInstance toModelInstance(int lighting, int diffusion, int x, int y, int z) {
        calculateNormals();
        int length = (int) Math.sqrt(x * x + y * y + z * z);
        int k1 = diffusion * length >> 8;


        ModelInstance modelInstance = new ModelInstance();

        modelInstance.shadedFaceColoursX = new int[numFaces];
        modelInstance.shadedFaceColoursY = new int[numFaces];
        modelInstance.shadedFaceColoursZ = new int[numFaces];

        if(numTextures > 0 && textureCoordinates != null) {
            int[] var9 = new int[this.numTextures];

            for(int i = 0;i<this.numFaces;i++) {
                if(this.textureCoordinates[i] != -1) {
                    var9[this.textureCoordinates[i] & 255]++;
                }
            }


            modelInstance.numTextures = 0;

            for(int i = 0;i<this.numTextures;i++) {
                if(var9[i] > 0 && this.textureRenderTypes[i] == 0) {
                    modelInstance.numTextures++;
                }
            }

            modelInstance.textureMappingA = new int[modelInstance.numTextures];
            modelInstance.textureMappingB = new int[modelInstance.numTextures];
            modelInstance.textureMappingC = new int[modelInstance.numTextures];

            int var10 = 0;
            for (int i = 0; i < numTextures; i++) {

                if(var9[i] > 0 && this.textureRenderTypes[i] == 0) {
                    modelInstance.textureMappingA[var10] = this.textureMappingA[i] & 0xFFFF;
                    modelInstance.textureMappingB[var10] = this.textureMappingB[i] & 0xFFFF;
                    modelInstance.textureMappingC[var10] = this.textureMappingC[i] & 0xFFFF;
                    var9[i] = var10++;
                } else {
                    var9[i] = -1;
                }
            }

            modelInstance.textureCoordinates = new byte[numFaces];

            modelInstance.textureRenderTypes = textureRenderTypes;
            for (int i = 0; i < numFaces; i++) {
                if(textureCoordinates[i] != -1) {
                    modelInstance.textureCoordinates[i] = (byte) var9[this.textureCoordinates[i] & 255];
                }
            }
        }


        for (int face = 0; face < numFaces; face++) {
            int indexX = faceIndicesA[face];
            int indexY = faceIndicesB[face];
            int indexZ = faceIndicesC[face];

            int type = faceTypes != null ? faceTypes[face] : 0;
            int texture = faceTextures != null ? faceTextures[face] : -1;
            int alpha = faceAlphas != null ? faceAlphas[face] : 0;

            if (alpha == -2) {
                type = 3;
            } else if (alpha == -1) {
                type = 2;
            }

            if (texture == -1) {
                if (type != 0) {
                    if (type == 1) {
                        Vector3i normal = faceNormals[face];
                        int light = (y * normal.y + z * normal.z + x * normal.x) / (k1 / 2 + k1) + lighting; // L: 1095
                        modelInstance.shadedFaceColoursX[face] = checkedLight(faceColours[face], light);
                        modelInstance.shadedFaceColoursZ[face] = -1;
                    } else if (type == 3) {
                        modelInstance.shadedFaceColoursX[face] = 128;
                        modelInstance.shadedFaceColoursZ[face] = -1;
                    } else {
                        modelInstance.shadedFaceColoursZ[face] = -2;
                    }
                } else {
                    int colour = faceColours[face];

                    VertexNormal normal = vertexNormals[indexX];
                    if(field1821 != null && field1821[indexX] != null)
                        normal = field1821[indexX];

                    normal = normal.normalize();

                    int light = (x * normal.position.x + y * normal.position.y + z * normal.position.z) / (k1 * normal.magnitude) + lighting;
                    modelInstance.shadedFaceColoursX[face] = checkedLight(colour, light);

                    normal = vertexNormals[indexY];
                    if(field1821 != null && field1821[indexY] != null)
                        normal = field1821[indexY];

                    normal = normal.normalize();
                    light = (x * normal.position.x + y * normal.position.y + z * normal.position.z) / (k1 * normal.magnitude) + lighting;
                    modelInstance.shadedFaceColoursY[face] = checkedLight(colour, light);

                    normal = vertexNormals[indexZ];
                    if(field1821 != null && field1821[indexZ] != null)
                        normal = field1821[indexZ];

                    normal = normal.normalize();

                    light = (x * normal.position.x + y * normal.position.y + z * normal.position.z) / (k1 * normal.magnitude) + lighting;
                    modelInstance. shadedFaceColoursZ[face] = checkedLight(colour, light);
                }
            } else if (type != 0) {
                if (type == 1) {
                    Vector3i normal = faceNormals[face];

                    int light = (y * normal.y + z * normal.z + x * normal.x) / (k1 / 2 + k1) + lighting; // L: 1095
                    modelInstance.shadedFaceColoursX[face] = clampLight(light);
                    modelInstance.shadedFaceColoursZ[face] = -1;
                } else {
                    log.info("Type: {}", type);
                    modelInstance.shadedFaceColoursZ[face] = -2;
                }
            } else {

                VertexNormal normal = vertexNormals[indexX];
                if(field1821 != null && field1821[indexX] != null)
                    normal = field1821[indexX];

                int light = (x * normal.position.x + y * normal.position.y + z * normal.position.z) / (k1 * normal.magnitude) + lighting;
                modelInstance.shadedFaceColoursX[face] = clampLight(light);

                normal = vertexNormals[indexY];
                if(field1821 != null && field1821[indexY] != null)
                    normal = field1821[indexY];

                light = (x * normal.position.x + y * normal.position.y + z * normal.position.z) / (k1 * normal.magnitude) + lighting;
                modelInstance.shadedFaceColoursY[face] = clampLight(light);

                normal = vertexNormals[indexZ];
                if(field1821 != null && field1821[indexZ] != null)
                    normal = field1821[indexZ];

                light = (x * normal.position.x + y * normal.position.y + z * normal.position.z) / (k1 * normal.magnitude) + lighting;
                modelInstance. shadedFaceColoursZ[face] = clampLight(light);
            }

        }

        modelInstance.numVertices = numVertices;
        modelInstance.verticesX = verticesX;
        modelInstance.verticesY = verticesY;
        modelInstance.verticesZ = verticesZ;

        modelInstance.numFaces = numFaces;
        modelInstance.faceIndicesA = faceIndicesA;
        modelInstance.faceIndicesB = faceIndicesB;
        modelInstance.faceIndicesC = faceIndicesC;

        modelInstance.facePriorities = facePriorities;
        modelInstance.faceAlphas = faceAlphas;
        modelInstance.facePriority = facePriority;

        modelInstance.vertexBones = vertexBones;
        modelInstance.vertexGroups = vertexGroups;
        modelInstance.faceSkin = faceSkin;

        modelInstance.faceTextures = faceTextures;
        modelInstance.fillBuffers();
        return modelInstance;
    }


    public VertexNormal[] vertexNormals;

    public void calculateNormals() {
        if (vertexNormals != null)
            return;

        vertexNormals = new VertexNormal[numVertices];
        for (int index = 0; index < numVertices; index++) {
            vertexNormals[index] = new VertexNormal();
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

            int type = faceTypes != null ? faceTypes[face] & 1 : 0;

            if (type == 0) {
                VertexNormal normal = vertexNormals[faceX];
                normal.position.add(dx, dy, dz);
                normal.magnitude++;

                normal = vertexNormals[faceY];
                normal.position.add(dx, dy, dz);
                normal.magnitude++;

                normal = vertexNormals[faceZ];
                normal.position.add(dx, dy, dz);
                normal.magnitude++;
            } else if (type == 1) {
                initFaceNormals();
                faceNormals[face] = new Vector3i(dx, dy, dz);
            }
        }
    }

    private void initFaceNormals() {
        if (this.faceNormals == null) {
            this.faceNormals = new Vector3i[numFaces];
            /*for (int idx = 0; idx < numFaces; idx++) {
                this.faceNormals[idx] = new Vector3i();
            }*/
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
            this.hasAlpha = model.hasAlpha;
        } else {
            if (anIntArray1625.length < numFaces) {
                anIntArray1625 = new int[numFaces + 100];
            }
            faceAlphas = anIntArray1625;

            if (model.faceAlphas == null) {
                for (int index = 0; index < numFaces; index++) {
                    faceAlphas[index] = 0;
                }
                this.hasAlpha = false;
            } else {
                for (int index = 0; index < numFaces; index++) {
                    faceAlphas[index] = model.faceAlphas[index];
                }
                this.hasAlpha = model.hasAlpha;
            }
        }

        faceTypes = model.faceTypes;
        textureRenderTypes = model.textureRenderTypes;
        textureCoordinates = model.textureCoordinates;
        faceColours = model.faceColours;
        faceTextures = model.faceTextures;
        facePriorities = model.facePriorities;
        facePriority = model.facePriority;
        faceGroups = model.faceGroups;
        vertexGroups = model.vertexGroups;
        faceIndicesA = model.faceIndicesA;
        faceIndicesB = model.faceIndicesB;
        faceIndicesC = model.faceIndicesC;
        textureMappingA = model.textureMappingA;
        textureMappingB = model.textureMappingB;
        textureMappingC = model.textureMappingC;
        ambient = model.ambient;
        contrast = model.contrast;
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
            textureMappingA = new int[numFaces];
            textureMappingB = new int[numFaces];
            textureMappingC = new int[numFaces];

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
                if (this.textureCoordinates != null) {
                    texture_type = textureCoordinates[i] & 0xff;
                    if (texture_type != 0xff)
                        if (texa[texture_type] >= 4096 || texb[texture_type] >= 4096
                                || texc[texture_type] >= 4096)
                            texture_type = -1;
                }
                if (texture_type == 0xff)
                    texture_type = -1;

                textureMappingA[set] = texture_type == -1 ? a : texa[texture_type];
                textureMappingB[set] = texture_type == -1 ? b : texb[texture_type];
                textureMappingC[set++] = texture_type == -1 ? c : texc[texture_type];

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
            label2:
            for (int triId = 0; triId < faceIndicesA.length; triId++) {
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
                    faceTypes[triangleId] = -1;

            }
        }
    }


    public Mesh copy() {
        Mesh mesh = new Mesh();

        Mesh model = this;
        mesh.fitsOnSingleSquare = (model.fitsOnSingleSquare);
        mesh.minimumX = (model.minimumX);
        mesh.maximumX = (model.maximumX);
        mesh.maximumZ = (model.maximumZ);
        mesh.minimumZ = (model.minimumZ);
        mesh.minimumY = (model.minimumY);
        mesh.anInt1654 = (model.anInt1654);
        mesh.ambient = model.ambient;
        mesh.contrast = model.contrast;
        mesh.faceAlphas = copyArray(model.faceAlphas);
        mesh.faceColours = copyArray(model.faceColours);
        mesh.faceTextures = copyArray(model.faceTextures);
        mesh.textureCoordinates = copyArray(model.textureCoordinates);
        mesh.textureRenderTypes = copyArray(model.textureRenderTypes);
        mesh.faceGroups = copyArray(model.faceGroups);
        mesh.facePriorities = copyArray(model.facePriorities);
        mesh.numFaces = (model.numFaces);
        mesh.faceSkin = copyArray(model.faceSkin);
        mesh.faceIndicesA = copyArray(model.faceIndicesA);
        mesh.faceIndicesB = copyArray(model.faceIndicesB);
        mesh.faceIndicesC = copyArray(model.faceIndicesC);

        if(model.vertexNormals != null) {
            mesh.vertexNormals = new VertexNormal[model.vertexNormals.length];
            for (int i = 0; i < mesh.vertexNormals.length; i++) {
                mesh.vertexNormals[i] = new VertexNormal(model.vertexNormals[i]);
            }

        }
        if(model.field1821 != null) {
            mesh.field1821 = new VertexNormal[model.field1821.length];
            for (int i = 0; i < mesh.field1821.length; i++) {
                mesh.field1821[i] = new VertexNormal(model.field1821[i]);
            }
        }
        if(model.faceNormals != null) {
            mesh.faceNormals = new Vector3i[model.faceNormals.length];
            for (int i = 0; i < mesh.faceNormals.length; i++) {
                mesh.faceNormals[i] = model.faceNormals[i] == null ? null : new Vector3i(model.faceNormals[i]);//XXX
            }
        }

        mesh.facePriority = (model.facePriority);
        mesh.numTextures = model.numTextures;
        mesh.textureMappingA = copyArray(model.textureMappingA);
        mesh.textureMappingB = copyArray(model.textureMappingB);
        mesh.textureMappingC = copyArray(model.textureMappingC);
        mesh.faceTypes = copyArray(model.faceTypes);
        mesh.vertexGroups = copyArray(model.vertexGroups);
        mesh.vertexBones = copyArray(model.vertexBones);
        mesh.verticesX = copyArray(model.verticesX);
        mesh.verticesY = copyArray(model.verticesY);
        mesh.verticesZ = copyArray(model.verticesZ);
        mesh.numVertices = model.numVertices;
        return mesh;

    }

    public void retexture(int found, int replace) {
        if (faceTextures != null)
            for (int face = 0; face < faceTextures.length; face++) {
                if (faceTextures[face] == found) {
                    //log.info("[{}] {} | Replaced {} with {}", id, revision, faceTextures[face], replace);
                    faceTextures[face] = replace;
                }
            }

    }

    public Mesh method3326() {
        Mesh mesh = new Mesh();
        if (this.faceTypes != null) {
            mesh.faceTypes = new int[this.numFaces];

            for (int var2 = 0; var2 < this.numFaces; ++var2) {
                mesh.faceTypes[var2] = this.faceTypes[var2];
            }
        }

        mesh.numVertices = numVertices;
        mesh.numFaces = numFaces;
        mesh.numTextures = numTextures;
        mesh.verticesX = verticesX;
        mesh.verticesY = verticesY;
        mesh.verticesZ = verticesZ;
        mesh.faceIndicesA = faceIndicesA;
        mesh.faceIndicesB = faceIndicesB;
        mesh.faceIndicesC = faceIndicesC;
        mesh.faceAlphas = this.faceAlphas; // L: 665
        mesh.textureCoordinates = this.textureCoordinates; // L: 666
        mesh.faceColours = this.faceColours; // L: 667
        mesh.faceTextures = this.faceTextures; // L: 668
        mesh.facePriority = this.facePriority; // L: 669
        mesh.textureRenderTypes = this.textureRenderTypes; // L: 670
        mesh.textureMappingB = this.textureMappingB; // L: 671
        mesh.textureMappingA = this.textureMappingA; // L: 671
        mesh.textureMappingC = this.textureMappingC; // L: 671
        mesh.vertexBones = vertexBones;
        mesh.faceSkin = faceSkin;
        mesh.vertexGroups = vertexGroups;
        mesh.faceAlphas = faceAlphas;
        mesh.faceGroups = faceGroups;
        mesh.vertexNormals = this.vertexNormals; // L: 678
        mesh.faceNormals = this.faceNormals; // L: 679
        mesh.ambient = this.ambient; // L: 680
        mesh.contrast = this.contrast; // L: 681
        return mesh; // L: 682
    }

    public int boundingPlaneRadius;
    public int boundingSphereRadius;
    public int boundingCylinderRadius;
    public boolean isBoundsCalculated;


    public void computeBounds() {
        super.height = 0;
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

            if (-y > super.height) {
                super.height = -y;
            }

            if (y > minimumY) {
                minimumY = y;
            }
        }
        isBoundsCalculated = true;
    }

    public void computeCircularBounds() {
        super.height = 0;
        boundingPlaneRadius = 0;
        minimumY = 0;

        for (int vertex = 0; vertex < numVertices; vertex++) {
            int x = verticesX[vertex];
            int y = verticesY[vertex];
            int z = verticesZ[vertex];

            if (-y > super.height) {
                super.height = -y;
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
                .sqrt(boundingPlaneRadius * boundingPlaneRadius + super.height * super.height) + 0.99D);
        boundingSphereRadius = boundingCylinderRadius
                + (int) (Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + minimumY * minimumY) + 0.99D);

        if (boundingSphereRadius > GameRasterizer.getInstance().depthListIndices.length)
            boundingSphereRadius = GameRasterizer.getInstance().depthListIndices.length;
    }

    public void computeSphericalBounds() {
        super.height = 0;
        minimumY = 0;

        for (int vertex = 0; vertex < numVertices; vertex++) {
            int y = verticesY[vertex];
            if (-y > super.height) {
                super.height = -y;
            }

            if (y > minimumY) {
                minimumY = y;
            }
        }

        boundingCylinderRadius = (int) (Math
                .sqrt(boundingPlaneRadius * boundingPlaneRadius + super.height * super.height)
                + 0.98999999999999999D);
        boundingSphereRadius = boundingCylinderRadius
                + (int) (Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + minimumY * minimumY)
                + 0.98999999999999999D);

        if (boundingSphereRadius > GameRasterizer.getInstance().depthListIndices.length)
            boundingSphereRadius = GameRasterizer.getInstance().depthListIndices.length;
    }

    public Mesh contourGround(int[][] tileHeights, int aY, int bY, int cY, int mean, boolean createCopy, int clipType) {
        this.computeBounds();

        int var7 = aY - this.minimumX; // L: 170
        int var8 = aY + this.maximumX; // L: 171
        int var9 = cY - this.minimumZ; // L: 172
        int var10 = cY + this.maximumZ; // L: 173
        if (var7 >= 0 && var8 + 128 >> 7 < tileHeights.length && var9 >= 0 && var10 + 128 >> 7 < tileHeights[0].length) { // L: 174
            var7 >>= 7; // L: 175
            var8 = var8 + 127 >> 7; // L: 176
            var9 >>= 7; // L: 177
            var10 = var10 + 127 >> 7; // L: 178
            if (mean == tileHeights[var7][var9] && mean == tileHeights[var8][var9] && mean == tileHeights[var7][var10] && mean == tileHeights[var8][var10]) { // L: 179
                return this;
            } else {
                Mesh var11;
                if (createCopy) { // L: 181
                    var11 = new Mesh(); // L: 182
                    var11.numVertices = this.numVertices; // L: 183
                    var11.numFaces = this.numFaces; // L: 184
                    var11.numTextures = this.numTextures; // L: 185
                    var11.verticesX = this.verticesX; // L: 186
                    var11.verticesZ = this.verticesZ; // L: 187
                    var11.faceIndicesA = this.faceIndicesA; // L: 188
                    var11.faceIndicesB = this.faceIndicesB; // L: 189
                    var11.faceIndicesC = this.faceIndicesC; // L: 190
                    var11.facePriorities = this.facePriorities; // L: 194
                    var11.faceAlphas = this.faceAlphas; // L: 195
                    var11.textureRenderTypes = this.textureRenderTypes; // L: 196
                    var11.faceTextures = this.faceTextures; // L: 197
                    var11.facePriority = this.facePriority; // L: 198
                    var11.textureMappingA = this.textureMappingA; // L: 199
                    var11.textureMappingB = this.textureMappingB; // L: 200
                    var11.textureMappingC = this.textureMappingC; // L: 201
                    var11.vertexGroups = this.vertexGroups; // L: 202
                    var11.vertexBones = this.vertexBones; // L: 203
                    var11.faceSkin = this.faceSkin;
                    var11.fitsOnSingleSquare = this.fitsOnSingleSquare; // L: 204
                    var11.verticesY = new int[var11.numVertices]; // L: 205
                } else {
                    var11 = this; // L: 208
                }

                int var12;
                int var13;
                int var14;
                int var15;
                int var16;
                int var17;
                int var18;
                int var19;
                int var20;
                int var21;
                if (clipType == 0) { // L: 210
                    for (var12 = 0; var12 < var11.numVertices; ++var12) { // L: 211
                        var13 = aY + this.verticesX[var12]; // L: 212
                        var14 = cY + this.verticesZ[var12]; // L: 213
                        var15 = var13 & 127; // L: 214
                        var16 = var14 & 127; // L: 215
                        var17 = var13 >> 7; // L: 216
                        var18 = var14 >> 7; // L: 217
                        var19 = tileHeights[var17][var18] * (128 - var15) + tileHeights[var17 + 1][var18] * var15 >> 7; // L: 218
                        var20 = tileHeights[var17][var18 + 1] * (128 - var15) + var15 * tileHeights[var17 + 1][var18 + 1] >> 7; // L: 219
                        var21 = var19 * (128 - var16) + var20 * var16 >> 7; // L: 220
                        var11.verticesY[var12] = var21 + this.verticesY[var12] - bY; // L: 221
                    }
                } else {
                    for (var12 = 0; var12 < var11.numVertices; ++var12) { // L: 225
                        var13 = (-this.verticesY[var12] << 16) / super.height; // L: 226
                        if (var13 < clipType) { // L: 227
                            var14 = aY + this.verticesX[var12]; // L: 228
                            var15 = cY + this.verticesZ[var12]; // L: 229
                            var16 = var14 & 127; // L: 230
                            var17 = var15 & 127; // L: 231
                            var18 = var14 >> 7; // L: 232
                            var19 = var15 >> 7; // L: 233
                            var20 = tileHeights[var18][var19] * (128 - var16) + tileHeights[var18 + 1][var19] * var16 >> 7; // L: 234
                            var21 = tileHeights[var18][var19 + 1] * (128 - var16) + var16 * tileHeights[var18 + 1][var19 + 1] >> 7; // L: 235
                            int var22 = var20 * (128 - var17) + var21 * var17 >> 7; // L: 236
                            var11.verticesY[var12] = (clipType - var13) * (var22 - bY) / clipType + this.verticesY[var12]; // L: 237
                        }
                    }
                }

                var11.invalidate(); // L: 241
                return var11; // L: 242
            }
        } else {
            return this;
        }

    }

    private void invalidate() {
        this.vertexNormals = null; // L: 943
        this.field1821 = null; // L: 944
        this.faceNormals = null; // L: 945
        this.isBoundsCalculated = false; // L: 946
    }
}