package com.rspsi.jagex.entity.model;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.draw.raster.GameRasterizer;
import com.rspsi.jagex.entity.Renderable;
import com.rspsi.jagex.util.Constants;
import com.rspsi.jagex.util.ObjectKey;
import com.rspsi.misc.ToolType;
import com.rspsi.options.Options;
import net.runelite.gpu.GpuFloatBuffer;
import net.runelite.gpu.GpuIntBuffer;
import net.runelite.gpu.util.Perspective;

import java.util.Objects;

import static com.rspsi.misc.ArrayUtil.copyArray;

public class ModelInstance extends Renderable {


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
    public int facePriority;
    public int numTextures;
    public int[] faceTypes;
    public int[][] vertexGroups;
    public int[] vertexBones;
    public int[] verticesX;
    public int[] verticesY;
    public int[] verticesZ;
    public int numVertices;

    public int[] shadedFaceColoursX;
    public int[] shadedFaceColoursY;
    public int[] shadedFaceColoursZ;

    public byte[] textureCoordinates;
    public byte[] textureRenderTypes;
    public boolean fitsOnSingleSquare;
    public int minimumX;
    public int maximumX;
    public int maximumZ;
    public int minimumZ;
    public int xzRadius;
    public int bottomY;
    public int boundingSphereRadius;
    public int boundingCylinderRadius;
    public int ambient;
    public int contrast;
    public int[] textureMappingA;
    public int[] textureMappingB;
    public int[] textureMappingC;
    public int[] field2084;
    public float[][] faceTextureUCoordinates;
    public float[][] faceTextureVCoordinates;
    public GpuIntBuffer triangleBuffer;
    public GpuIntBuffer vertexBuffer;
    public GpuFloatBuffer uvBuffer;
    private int boundsType;
    private int extremeX = -1, extremeY = -1, extremeZ = -1;
    //public List<Vector3f> vertexes;
    private int centerX = -1, centerY = -1, centerZ = -1;

    public ModelInstance() {
        this.numVertices = 0;
        this.numFaces = 0;
        this.numTextures = 0;
        this.fitsOnSingleSquare = false;
        this.centerX = -1;
        this.centerY = -1;
        this.centerZ = -1;
        triangleBuffer = new GpuIntBuffer();
        vertexBuffer = new GpuIntBuffer();
        uvBuffer = new GpuFloatBuffer();
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

    private static final int method3027(int var0, int var1, int var2, int var3) {
        return var0 * var2 + var3 * var1 >> 16;
    }

    private static final int method3028(int var0, int var1, int var2, int var3) {
        return var2 * var1 - var3 * var0 >> 16;
    }

    public int getZVertexMax() {
        return 50;
    }

    public ModelInstance copy() {
        ModelInstance mesh = new ModelInstance();

        ModelInstance model = this;
        mesh.fitsOnSingleSquare = (model.fitsOnSingleSquare);
        mesh.minimumX = (model.minimumX);
        mesh.maximumX = (model.maximumX);
        mesh.maximumZ = (model.maximumZ);
        mesh.minimumZ = (model.minimumZ);
        mesh.xzRadius = (model.xzRadius);
        mesh.bottomY = (model.bottomY);
        mesh.boundingSphereRadius = (model.boundingSphereRadius);
        mesh.boundingCylinderRadius = (model.boundingCylinderRadius);
        mesh.ambient = model.ambient;
        mesh.contrast = model.contrast;
        mesh.shadedFaceColoursX = copyArray(model.shadedFaceColoursX);
        mesh.shadedFaceColoursY = copyArray(model.shadedFaceColoursY);
        mesh.shadedFaceColoursZ = copyArray(model.shadedFaceColoursZ);
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
        mesh.fillBuffers();
        return mesh;

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
        if (this.boundsType != 1) {
            this.computeCircularBounds();
        }

        int j2 = y * xCosine - x * xSine >> 16;
        int k2 = height * ySine + j2 * yCosine >> 16;
        int l2 = xzRadius * yCosine >> 16;
        int i3 = k2 + l2;

        if (i3 <= 50 || k2 >= 6500)
            return;

        int j3 = y * xSine + x * xCosine >> 16;
        int sceneLowerX = j3 - xzRadius << 9;
        if (sceneLowerX / i3 >= rasterizer.getCentreX())
            return;

        int sceneMaximumX = j3 + xzRadius << 9;
        if (sceneMaximumX / i3 <= -rasterizer.getCentreX())
            return;

        int i4 = height * yCosine - j2 * ySine >> 16;
        int j4 = xzRadius * ySine >> 16;
        int sceneMaximumY = i4 + j4 << 9;

        if (sceneMaximumY / i3 <= -rasterizer.getCentreY())
            return;

        int l4 = j4 + (super.height * yCosine >> 16);
        int sceneLowerY = i4 - l4 << 9;
        if (sceneLowerY / i3 >= rasterizer.getCentreY())
            return;

        int j5 = l2 + (super.height * ySine >> 16);
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
                    if (j5 > 0)
                        rasterizer.faceList[j5][rasterizer.depthListIndices[j5]++] = face;
                } else {
                    /*if (key != null && multiTileFlag) {
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
                    }*/
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

        boolean ignoreTextures = translucent || selected;

        int colourA = shadedFaceColoursX[index];
        int colourB = shadedFaceColoursY[index];
        int colourC = shadedFaceColoursZ[index];

        if (!ignoreTextures && faceTextures != null && faceTextures[index] != -1) {
            int texFaceX = faceX;
            int texFaceY = faceY;
            int texFaceZ = faceZ;
            int texId = faceTextures[index];
            if (textureCoordinates != null && textureCoordinates[index] != -1) {
                int textureCoordIndex = this.textureCoordinates[index] & 255;
                texFaceX = textureMappingA[textureCoordIndex];
                texFaceY = textureMappingB[textureCoordIndex];
                texFaceZ = textureMappingC[textureCoordIndex];
            }
            if (colourC == -1) {
                rasterizer.drawTexturedTriangle(
                        rasterizer.vertexScreenY[faceX],
                        rasterizer.vertexScreenY[faceY],
                        rasterizer.vertexScreenY[faceZ],
                        rasterizer.vertexScreenX[faceX],
                        rasterizer.vertexScreenX[faceY],
                        rasterizer.vertexScreenX[faceZ],
                        colourA, colourA, colourA,
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
            } else {

                rasterizer.drawTexturedTriangle(
                        rasterizer.vertexScreenY[faceX],
                        rasterizer.vertexScreenY[faceY],
                        rasterizer.vertexScreenY[faceZ],
                        rasterizer.vertexScreenX[faceX],
                        rasterizer.vertexScreenX[faceY],
                        rasterizer.vertexScreenX[faceZ],
                        colourA, colourB, colourC,
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
            }
        } else if (colourC == -1 || ignoreTextures) {
            int colour = selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedFaceColoursX[index]];
            rasterizer.drawShadedTriangle(rasterizer.vertexScreenY[faceX], rasterizer.vertexScreenY[faceY], rasterizer.vertexScreenY[faceZ], rasterizer.vertexScreenX[faceX],
                    rasterizer.vertexScreenX[faceY], rasterizer.vertexScreenX[faceZ], colour);
        } else {
            rasterizer.drawShadedTriangle(rasterizer.vertexScreenY[faceX], rasterizer.vertexScreenY[faceY], rasterizer.vertexScreenY[faceZ],
                    rasterizer.vertexScreenX[faceX], rasterizer.vertexScreenX[faceY], rasterizer.vertexScreenX[faceZ],
                    shadedFaceColoursX[index], shadedFaceColoursY[index], shadedFaceColoursZ[index]);
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

                int colourA = shadedFaceColoursX[index];
                int colourB = shadedFaceColoursY[index];
                int colourC = shadedFaceColoursZ[index];

                if (!ignoreTextures && faceTextures != null && faceTextures[index] != -1) {
                    int texFaceX = faceX;
                    int texFaceY = faceY;
                    int texFaceZ = faceZ;
                    int texId = faceTextures[index];
                    if (textureCoordinates != null && textureCoordinates[index] != -1) {
                        int textureCoordIndex = this.textureCoordinates[index] & 255;
                        faceX = textureMappingA[textureCoordIndex];
                        faceY = textureMappingB[textureCoordIndex];
                        faceZ = textureMappingC[textureCoordIndex];
                    }
                    if (colourC == -1) {
                        rasterizer.drawTexturedTriangle(
                                i7, j7, k7, j3, j4, j5,
                                colourA, colourA, colourA,
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
                    } else {

                        rasterizer.drawTexturedTriangle(
                                i7, j7, k7, j3, j4, j5,
                                rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1], rasterizer.anIntArray1680[2],
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
                    }
                } else if (colourC == -1 || ignoreTextures) {
                    int colour = selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedFaceColoursX[index]];
                    rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, colour);
                } else {
                    rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
                            rasterizer.anIntArray1680[2]);
                }

            }
            if (l == 4) {
                if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > rasterizer.getMaxRight() || j4 > rasterizer.getMaxRight() || j5 > rasterizer.getMaxRight()
                        || rasterizer.anIntArray1678[3] < 0 || rasterizer.anIntArray1678[3] > rasterizer.getMaxRight()) {
                    rasterizer.restrictEdges = true;
                }


                int colourA = shadedFaceColoursX[index];
                int colourB = shadedFaceColoursY[index];
                int colourC = shadedFaceColoursZ[index];

                if (!ignoreTextures && faceTextures != null && faceTextures[index] != -1) {
                    int texFaceX = faceX;
                    int texFaceY = faceY;
                    int texFaceZ = faceZ;
                    int texId = faceTextures[index];
                    if (textureCoordinates != null && textureCoordinates[index] != -1) {
                        int textureCoordIndex = this.textureCoordinates[index] & 255;
                        texFaceX = textureMappingA[textureCoordIndex];
                        texFaceY = textureMappingB[textureCoordIndex];
                        texFaceZ = textureMappingC[textureCoordIndex];
                    }
                    if (colourC == -1) {
                        rasterizer.drawTexturedTriangle(
                                i7, j7, k7, j3, j4, j5,
                                colourA, colourA, colourA,
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
                        rasterizer.drawTexturedTriangle(
                                i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
                                colourA, colourA, colourA,
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
                    } else {

                        rasterizer.drawTexturedTriangle(
                                i7, j7, k7, j3, j4, j5,
                                rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1], rasterizer.anIntArray1680[2],
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
                        rasterizer.drawTexturedTriangle(
                                i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
                                rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[2], rasterizer.anIntArray1680[3],
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
                    }
                } else if (colourC == -1 || ignoreTextures) {
                    int colour = selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[shadedFaceColoursX[index]];
                    rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, colour);
                    rasterizer.drawShadedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3], colour);
                } else {
                    rasterizer.drawShadedTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1],
                            rasterizer.anIntArray1680[2]);
                    rasterizer.drawShadedTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3],
                            rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[2], rasterizer.anIntArray1680[3]);
                }
            }
        }
    }

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

            for (int var10 = 0; var10 < this.numVertices; ++var10) {
                int var11 = method3027(this.verticesX[var10], this.verticesZ[var10], var8, var9);
                int var12 = this.verticesY[var10];
                int var13 = method3028(this.verticesX[var10], this.verticesZ[var10], var8, var9);
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

    public void computeCircularBounds() {
        if (this.boundsType != 1) {
            boundsType = 1;


            super.height = 0;
            xzRadius = 0;
            bottomY = 0;

            for (int vertex = 0; vertex < numVertices; vertex++) {
                int x = verticesX[vertex];
                int y = verticesY[vertex];
                int z = verticesZ[vertex];

                if (-y > super.height) {
                    super.height = -y;
                }

                if (y > bottomY) {
                    bottomY = y;
                }

                int radius = x * x + z * z;
                if (radius > xzRadius) {
                    xzRadius = radius;
                }
            }

            xzRadius = (int) (Math.sqrt(xzRadius) + 0.99D);
            boundingCylinderRadius = (int) (Math
                    .sqrt(xzRadius * xzRadius + super.height * super.height) + 0.99D);
            boundingSphereRadius = boundingCylinderRadius
                    + (int) (Math.sqrt(xzRadius * xzRadius + bottomY * bottomY) + 0.99D);
        }
    }

    public void computeSphericalBounds() {
        if (boundsType != 2) {
            boundsType = 2;
            super.height = 0;
            xzRadius = 0;
            bottomY = 0;


            for (int vertex = 0; vertex < numVertices; vertex++) {
                int x = verticesX[vertex];
                int y = verticesY[vertex];
                int z = verticesZ[vertex];


                int radius = x * x + z * z + y * y;
                if (radius > xzRadius) {
                    xzRadius = radius;
                }
            }

            xzRadius = (int) (Math.sqrt(xzRadius) + 0.99D);
            boundingCylinderRadius = (int) (Math
                    .sqrt(xzRadius * xzRadius + super.height * super.height) + 0.99D);
            boundingSphereRadius = boundingCylinderRadius
                    + (int) (Math.sqrt(xzRadius * xzRadius + bottomY * bottomY) + 0.99D);
        }
    }

    public ModelInstance contourGround(int[][] tileHeights, int aY, int bY, int cY, int mean, boolean createCopy, int clipType) {

        this.computeCircularBounds();

        int var7 = aY - this.xzRadius; // L: 170
        int var8 = aY + this.xzRadius; // L: 171
        int var9 = cY - this.xzRadius; // L: 172
        int var10 = cY + this.xzRadius; // L: 173
        if (var7 >= 0 && var8 + 128 >> 7 < tileHeights.length && var9 >= 0 && var10 + 128 >> 7 < tileHeights[0].length) { // L: 174
            var7 >>= 7; // L: 175
            var8 = var8 + 127 >> 7; // L: 176
            var9 >>= 7; // L: 177
            var10 = var10 + 127 >> 7; // L: 178
            if (mean == tileHeights[var7][var9] && mean == tileHeights[var8][var9] && mean == tileHeights[var7][var10] && mean == tileHeights[var8][var10]) { // L: 179
                return this;
            } else {
                ModelInstance var11;
                if (createCopy) { // L: 181
                    var11 = new ModelInstance(); // L: 182
                    var11.numVertices = this.numVertices; // L: 183
                    var11.numFaces = this.numFaces; // L: 184
                    var11.numTextures = this.numTextures; // L: 185
                    var11.verticesX = copyArray(this.verticesX); // L: 186
                    var11.verticesZ = copyArray(this.verticesZ); // L: 187
                    var11.faceIndicesA = copyArray(this.faceIndicesA); // L: 188
                    var11.faceIndicesB = copyArray(this.faceIndicesB); // L: 189
                    var11.faceIndicesC = copyArray(this.faceIndicesC); // L: 190
                    var11.shadedFaceColoursX = copyArray(this.shadedFaceColoursX); // L: 191
                    var11.shadedFaceColoursY = copyArray(this.shadedFaceColoursY); // L: 192
                    var11.shadedFaceColoursZ = copyArray(this.shadedFaceColoursZ); // L: 193
                    var11.facePriorities = copyArray(this.facePriorities); // L: 194
                    var11.faceAlphas = copyArray(this.faceAlphas); // L: 195
                    var11.textureRenderTypes = copyArray(this.textureRenderTypes); // L: 196
                    var11.faceTextures = copyArray(this.faceTextures); // L: 197
                    var11.facePriority = this.facePriority; // L: 198
                    var11.textureMappingA = copyArray(this.textureMappingA); // L: 199
                    var11.textureMappingB = copyArray(this.textureMappingB); // L: 200
                    var11.textureMappingC = copyArray(this.textureMappingC); // L: 201
                    var11.vertexGroups = copyArray(this.vertexGroups); // L: 202
                    var11.vertexBones = copyArray(this.vertexBones); // L: 203
                    var11.faceSkin = copyArray(this.faceSkin);
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

                var11.resetBounds(); // L: 241
                return var11; // L: 242
            }
        } else {
            return this;
        }

    }

    private void resetBounds() {
        boundsType = 0;
        centerX = -1;
    }

    public void computeTextureUVCoordinates() {
        this.faceTextureUCoordinates = new float[numFaces][];
        this.faceTextureVCoordinates = new float[numFaces][];

        for (int i = 0; i < numFaces; i++) {
            int textureCoordinate;
            if (textureCoordinates == null) {
                textureCoordinate = -1;
            } else {
                textureCoordinate = textureCoordinates[i];
            }

            int textureIdx;
            if (faceTextures == null) {
                textureIdx = -1;
            } else {
                textureIdx = faceTextures[i] & 0xFFFF;
            }

            if (textureIdx != -1) {
                float[] u = new float[3];
                float[] v = new float[3];

                if (textureCoordinate == -1) {
                    u[0] = 0.0F;
                    v[0] = 1.0F;

                    u[1] = 1.0F;
                    v[1] = 1.0F;

                    u[2] = 0.0F;
                    v[2] = 0.0F;
                } else {
                    textureCoordinate &= 0xFF;

                    byte textureRenderType = 0;
                    if (textureRenderTypes != null) {
                        textureRenderType = textureRenderTypes[textureCoordinate];
                    }

                    if (textureRenderType == 0) {
                        int faceVertexIdx1 = faceIndicesA[i];
                        int faceVertexIdx2 = faceIndicesB[i];
                        int faceVertexIdx3 = faceIndicesC[i];

                        int triangleVertexIdx1 = textureMappingA[textureCoordinate];
                        int triangleVertexIdx2 = textureMappingB[textureCoordinate];
                        int triangleVertexIdx3 = textureMappingC[textureCoordinate];

                        float triangleX = (float) verticesX[triangleVertexIdx1];
                        float triangleY = (float) verticesY[triangleVertexIdx1];
                        float triangleZ = (float) verticesZ[triangleVertexIdx1];

                        float f_882_ = (float) verticesX[triangleVertexIdx2] - triangleX;
                        float f_883_ = (float) verticesY[triangleVertexIdx2] - triangleY;
                        float f_884_ = (float) verticesZ[triangleVertexIdx2] - triangleZ;
                        float f_885_ = (float) verticesX[triangleVertexIdx3] - triangleX;
                        float f_886_ = (float) verticesY[triangleVertexIdx3] - triangleY;
                        float f_887_ = (float) verticesZ[triangleVertexIdx3] - triangleZ;
                        float f_888_ = (float) verticesX[faceVertexIdx1] - triangleX;
                        float f_889_ = (float) verticesY[faceVertexIdx1] - triangleY;
                        float f_890_ = (float) verticesZ[faceVertexIdx1] - triangleZ;
                        float f_891_ = (float) verticesX[faceVertexIdx2] - triangleX;
                        float f_892_ = (float) verticesY[faceVertexIdx2] - triangleY;
                        float f_893_ = (float) verticesZ[faceVertexIdx2] - triangleZ;
                        float f_894_ = (float) verticesX[faceVertexIdx3] - triangleX;
                        float f_895_ = (float) verticesY[faceVertexIdx3] - triangleY;
                        float f_896_ = (float) verticesZ[faceVertexIdx3] - triangleZ;

                        float f_897_ = f_883_ * f_887_ - f_884_ * f_886_;
                        float f_898_ = f_884_ * f_885_ - f_882_ * f_887_;
                        float f_899_ = f_882_ * f_886_ - f_883_ * f_885_;
                        float f_900_ = f_886_ * f_899_ - f_887_ * f_898_;
                        float f_901_ = f_887_ * f_897_ - f_885_ * f_899_;
                        float f_902_ = f_885_ * f_898_ - f_886_ * f_897_;
                        float f_903_ = 1.0F / (f_900_ * f_882_ + f_901_ * f_883_ + f_902_ * f_884_);

                        u[0] = (f_900_ * f_888_ + f_901_ * f_889_ + f_902_ * f_890_) * f_903_;
                        u[1] = (f_900_ * f_891_ + f_901_ * f_892_ + f_902_ * f_893_) * f_903_;
                        u[2] = (f_900_ * f_894_ + f_901_ * f_895_ + f_902_ * f_896_) * f_903_;

                        f_900_ = f_883_ * f_899_ - f_884_ * f_898_;
                        f_901_ = f_884_ * f_897_ - f_882_ * f_899_;
                        f_902_ = f_882_ * f_898_ - f_883_ * f_897_;
                        f_903_ = 1.0F / (f_900_ * f_885_ + f_901_ * f_886_ + f_902_ * f_887_);

                        v[0] = (f_900_ * f_888_ + f_901_ * f_889_ + f_902_ * f_890_) * f_903_;
                        v[1] = (f_900_ * f_891_ + f_901_ * f_892_ + f_902_ * f_893_) * f_903_;
                        v[2] = (f_900_ * f_894_ + f_901_ * f_895_ + f_902_ * f_896_) * f_903_;
                    }
                }

                this.faceTextureUCoordinates[i] = u;
                this.faceTextureVCoordinates[i] = v;
            }
        }
    }

    @Override
    public ModelInstance model() {
        return this;
    }

    public int pushFace(int face, int xOffset, int yOffset, int zOffset, int orientation) {
        final int[] vertexX = this.verticesX;
        final int[] vertexY = this.verticesY;
        final int[] vertexZ = this.verticesZ;

        final int[] trianglesX = this.faceIndicesA;
        final int[] trianglesY = this.faceIndicesB;
        final int[] trianglesZ = this.faceIndicesC;


        final int[] transparencies = this.faceAlphas;
        final int[] faceTextures = this.faceTextures;
        final int[] facePriorities = this.facePriorities;

        int triangleA = trianglesX[face];
        int triangleB = trianglesY[face];
        int triangleC = trianglesZ[face];



        computeTextureUVCoordinates();
        float[][] u = this.faceTextureUCoordinates;
        float[][] v = this.faceTextureVCoordinates;
        float[] uf, vf;

        int color1 = this.shadedFaceColoursX[face];
        int color2 = this.shadedFaceColoursY[face];
        int color3 = this.shadedFaceColoursZ[face];
        int alpha = 0;
        if (transparencies != null) {
            alpha = (transparencies[face] & 0xFF) << 24;
        }
        int priority = 0;
        if (facePriorities != null) {
            priority = (facePriorities[face] & 0xff) << 16;
        }

        int sin = 0, cos = 0;
        if (orientation != 0) {
            sin = Perspective.SINE[orientation];
            cos = Perspective.COSINE[orientation];
        }
        if (color3 == -1) {
            color2 = color1;
            color3 = color1;
        }


        triangleBuffer.put(triangleA, triangleB, triangleC);

        if (color3 == -2) {
            vertexBuffer.put(0, 0, 0, 0);
            vertexBuffer.put(0, 0, 0, 0);
            vertexBuffer.put(0, 0, 0, 0);

            uvBuffer.put(0f, 0f, 0f, 0f);
            uvBuffer.put(0f, 0f, 0f, 0f);
            uvBuffer.put(0f, 0f, 0f, 0f);


            return 3;
        }
        int[] verts = {triangleA, triangleB, triangleC};
        int[] colours = {color1, color2, color3};
        for (int i = 0; i < verts.length; i++) {
            uploadVertex(xOffset, yOffset, zOffset,
                    orientation, vertexX, vertexY, vertexZ,
                    verts[i], colours[i], alpha, priority, sin, cos);

        }


        if (faceTextures != null && u != null && v != null && (uf = u[face]) != null && (vf = v[face]) != null) {
            float texture = faceTextures[face] + 1f;
            uvBuffer.put(texture, uf[0], vf[0], 0f);
            uvBuffer.put(texture, uf[1], vf[1], 0f);
            uvBuffer.put(texture, uf[2], vf[2], 0f);
        } else {
            uvBuffer.put(0f, 0f, 0f, 0f);
            uvBuffer.put(0f, 0f, 0f, 0f);
            uvBuffer.put(0f, 0f, 0f, 0f);
        }

        return 3;

    }

    private void uploadVertex(int xOffset, int yOffset, int zOffset, int orientation, int[] vertexX, int[] vertexY, int[] vertexZ, int vertex, int colour, int alpha, int priority, int sin, int cos) {
        int a;
        int b;
        int c;
        a = vertexX[vertex];
        b = vertexY[vertex];
        c = vertexZ[vertex];

        if (orientation != 0) {
            int x = c * sin + a * cos >> 16;
            int z = c * cos - a * sin >> 16;

            a = x;
            c = z;
        }

        a += xOffset;
        b += yOffset;
        c += zOffset;

        vertexBuffer.put(a, b, c, alpha | priority | colour);
    }

    public void fillBuffers() {
        for (int i = 0; i < numFaces; ++i) {
            pushFace(i, 0, 0, 0, 0);
        }

        vertexBuffer.flip();
        uvBuffer.flip();
        triangleBuffer.flip();

    }
}
