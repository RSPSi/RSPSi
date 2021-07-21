package com.rspsi.jagex.map.tile;

import com.google.common.collect.Lists;
import com.rspsi.jagex.cache.loader.floor.FloorType;
import com.rspsi.options.Options;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runelite.gpu.GpuIntBuffer;
import net.runelite.gpu.util.ModelBuffers;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.nio.IntBuffer;

@ToString
@Getter
@Setter
public final class ShapedTile extends RenderableTile {


    public static int[] screenX = new int[6];
    public static int[] screenY = new int[6];
    public static int[] screenZ = new int[6];
    public static int[] viewSpaceX = new int[6];
    public static int[] viewSpaceY = new int[6];
    public static int[] viewSpaceZ = new int[6];
    public static int[] anIntArray693 = {1, 0};
    public static int[] anIntArray694 = {2, 1};
    public static int[] anIntArray695 = {3, 3};
    public int orientation;
    public boolean textured;
    public int color61;
    public int color71;
    public int color81;
    public int color91;
    public int color62;
    public int color72;
    public int color82;
    public int color92;
    int uvBufferOffset, bufferOffset, bufferLen;
    int tileType;
    int underlayColour;
    int textureColour;// Overlay colour?
    int[] origVertexX;
    int[] origVertexY;
    int[] origVertexZ;
    int[] triangleHslA;
    int[] triangleHslB;
    int[] triangleHslC;
    int[] triangleA;
    int[] triangleB;
    int[] triangleC;
    int[] triangleTextureOverlay;
    int[] triangleTextureUnderlay;
    boolean flat;
    private int textureId = -1;

    public ShapedTile(int type, int orientation, int textureId) {
        super(new Vector3i(0, 0, 0));
        flat = true;

        this.tileType = type;
        this.orientation = orientation;

        int qSize = 32;
        int halfSize = 64;
        int threeQSize = 96;
        int fullSize = 128;

        int[] tileShape = TileShape.tileShapePoints[type];
        int tileShapeLength = tileShape.length;

        vertices = Lists.newArrayList();


        for (int index = 0; index < tileShapeLength; index++) {
            int vertexZ = 0;

            int vertexType = tileShape[index];
            if ((vertexType != 1) && vertexType <= 8) {
                vertexType = (vertexType - orientation - orientation - 1 & 7) + 1;
            }

            if (vertexType > 8 && vertexType <= 12) {
                vertexType = (vertexType - 9 - orientation & 3) + 9;
            }

            if (vertexType > 12 && vertexType <= 16) {
                vertexType = (vertexType - 13 - orientation & 3) + 13;
            }

            int vertexX;
            int vertexY;

            switch (vertexType) {
                case 0:
                    vertexX = qSize;
                    vertexY = threeQSize;
                    break;
                case 1:
                default:
                    vertexX = 0;
                    vertexY = 0;
                    break;
                case 2:
                    vertexX = halfSize;
                    vertexY = 0;
                    break;
                case 3:
                    vertexX = fullSize;
                    vertexY = 0;
                    break;
                case 4:
                    vertexX = fullSize;
                    vertexY = halfSize;
                    break;
                case 5:
                    vertexX = fullSize;
                    vertexY = fullSize;
                    break;
                case 6:
                    vertexX = halfSize;
                    vertexY = fullSize;
                    break;
                case 7:
                    vertexX = 0;
                    vertexY = fullSize;
                    break;
                case 8:
                    vertexX = 0;
                    vertexY = halfSize;
                    break;
                case 9:
                    vertexX = halfSize;
                    vertexY = qSize;
                    break;
                case 10:
                    vertexX = threeQSize;
                    vertexY = halfSize;
                    break;
                case 11:
                    vertexX = halfSize;
                    vertexY = threeQSize;
                    break;
                case 12:
                    vertexX = qSize;
                    vertexY = halfSize;
                    break;
                case 13:
                    vertexX = qSize;
                    vertexY = qSize;
                    break;
                case 14:
                    vertexX = threeQSize;
                    vertexY = qSize;
                    break;
                case 15:
                    vertexX = threeQSize;
                    vertexY = threeQSize;
                    break;
                case 16:
                    vertexX = halfSize;
                    vertexY = halfSize;
                    break;
                case 17:
                    vertexX = qSize;
                    vertexY = 0;
                    break;
                case 18:
                    vertexX = threeQSize;
                    vertexY = 0;
                    break;
                case 19:
                    vertexX = fullSize;
                    vertexY = qSize;
                    break;
                case 20:
                    vertexX = fullSize;
                    vertexY = threeQSize;
                    break;
                case 21:
                    vertexX = threeQSize;
                    vertexY = fullSize;
                    break;
                case 22:
                    vertexX = qSize;
                    vertexY = fullSize;
                    break;
                case 23:
                    vertexX = 0;
                    vertexY = threeQSize;
                    break;
                case 24:
                    vertexX = 0;
                    vertexY = qSize;
                    break;
            }
            vertices.add(new Vector3i(vertexX, vertexY, vertexZ));
        }

        Object[] shapedTileElement = TileShape.shapedTileElementData2[type];
        int numFaces = shapedTileElement.length / 4;
        faces = Lists.newArrayList();
        faceColours = Lists.newArrayList();

        int overlayColour = 0xFFFFFF;
        int underlayColour = 0x00000;


        for (int face = 0, elementIndex = 0; face < numFaces; face++, elementIndex += 4) {
            boolean isOverlay = shapedTileElement[elementIndex] == FloorType.OVERLAY;
            Vector2i faceIndiceA = (Vector2i) shapedTileElement[elementIndex + 1];
            Vector2i faceIndiceB = (Vector2i) shapedTileElement[elementIndex + 2];
            Vector2i faceIndiceC = (Vector2i) shapedTileElement[elementIndex + 3];
			/*if (faceIndiceA < 4) {
				faceIndiceA = faceIndiceA - orientation & 3;
			}
			if (faceIndiceB < 4) {
				faceIndiceB = faceIndiceB - orientation & 3;
			}
			if (faceIndiceC < 4) {
				faceIndiceC = faceIndiceC - orientation & 3;
			}*/
            faces.add(new Vector3i(vertices.indexOf(new Vector3i(faceIndiceA, 0)), vertices.indexOf(new Vector3i(faceIndiceB, 0)), vertices.indexOf(new Vector3i(faceIndiceC, 0))));

            if (isOverlay) {
                faceColours.add(new Vector3i(overlayColour, overlayColour, overlayColour));
                if (textureIds != null) {
                    textureIds[face] = textureId;
                }

            } else {
                faceColours.add(new Vector3i(underlayColour, underlayColour, underlayColour));
                if (textureIds != null) {
                    textureIds[face] = textureId;
                }

            }
        }


    }

    public ShapedTile(int type, int orientation, int x, int y,
                      int underlayColour, int underlayTextureId, int textureColour, int overlayTextureId,
                      int centreZ, int northEastZ, int northZ, int eastZ,
                      int centerOverColour, int northOverColour, int neOverColour, int eastOverColour,
                      int northUnderColour, int neUnderColour, int eastUnderColour, int centerUnderColour) {
        super(new Vector3i(x, y, 0));
        boolean hdTextures = Options.hdTextures.get();
        this.textured = overlayTextureId != -1;
        this.color61 = centerUnderColour;
        this.color71 = eastUnderColour;
        this.color81 = neUnderColour;
        this.color91 = centerOverColour;
        this.color62 = northUnderColour;
        this.color72 = eastOverColour;
        this.color82 = neOverColour;
        this.color92 = northOverColour;
        this.flat = centreZ == eastZ && centreZ == northEastZ && centreZ == northZ;

        this.tileType = type;
        this.orientation = orientation;
        this.underlayColour = underlayColour;
        this.textureColour = textureColour;
        this.textureId = overlayTextureId;

        int j5 = 32;
        int i5 = 64;
        int k5 = 96;
        int c = 128;

        int[] tileShape = TileShape.tileShapePoints[type];
        int tileShapeLength = tileShape.length;
        origVertexX = new int[tileShapeLength];
        origVertexY = new int[tileShapeLength];
        origVertexZ = new int[tileShapeLength];
        int[] vertexColoursUnderlay = new int[tileShapeLength];
        int[] vertexColoursOverlay = new int[tileShapeLength];
        int i6 = x * c;
        int j6 = y * c;

        for (int idx = 0; idx < tileShapeLength; idx++) {
            int vertexType = tileShape[idx];
            if ((vertexType & 1) == 0 && vertexType <= 8) {
                vertexType = (vertexType - orientation - orientation - 1 & 7) + 1;
            }

            if (vertexType > 8 && vertexType <= 12) {
                vertexType = (vertexType - 9 - orientation & 3) + 9;
            }

            if (vertexType > 12 && vertexType <= 16) {
                vertexType = (vertexType - 13 - orientation & 3) + 13;
            }

            int vertexX;
            int vertexZ;
            int vertexY;
            int underlayC;
            int overlayColour;

            if (vertexType == 1) {
                vertexX = i6;
                vertexZ = j6;
                vertexY = centreZ;
                underlayC = centerUnderColour;
                overlayColour = centerOverColour;
            } else if (vertexType == 2) {
                vertexX = i6 + i5;
                vertexZ = j6;
                vertexY = centreZ + eastZ >> 1;
                underlayC = centerUnderColour + eastUnderColour >> 1;
                overlayColour = centerOverColour + eastOverColour >> 1;
            } else if (vertexType == 3) {
                vertexX = i6 + c;
                vertexZ = j6;
                vertexY = eastZ;
                underlayC = eastUnderColour;
                overlayColour = eastOverColour;
            } else if (vertexType == 4) {
                vertexX = i6 + c;
                vertexZ = j6 + i5;
                vertexY = eastZ + northEastZ >> 1;
                underlayC = eastUnderColour + neUnderColour >> 1;
                overlayColour = eastOverColour + neOverColour >> 1;
            } else if (vertexType == 5) {
                vertexX = i6 + c;
                vertexZ = j6 + c;
                vertexY = northEastZ;
                underlayC = neUnderColour;
                overlayColour = neOverColour;
            } else if (vertexType == 6) {
                vertexX = i6 + i5;
                vertexZ = j6 + c;
                vertexY = northEastZ + northZ >> 1;
                underlayC = neUnderColour + northUnderColour >> 1;
                overlayColour = neOverColour + northOverColour >> 1;
            } else if (vertexType == 7) {
                vertexX = i6;
                vertexZ = j6 + c;
                vertexY = northZ;
                underlayC = northUnderColour;
                overlayColour = northOverColour;
            } else if (vertexType == 8) {
                vertexX = i6;
                vertexZ = j6 + i5;
                vertexY = northZ + centreZ >> 1;
                underlayC = northUnderColour + centerUnderColour >> 1;
                overlayColour = northOverColour + centerOverColour >> 1;
            } else if (vertexType == 9) {
                vertexX = i6 + i5;
                vertexZ = j6 + j5;
                vertexY = centreZ + eastZ >> 1;
                underlayC = centerUnderColour + eastUnderColour >> 1;
                overlayColour = centerOverColour + eastOverColour >> 1;
            } else if (vertexType == 10) {
                vertexX = i6 + k5;
                vertexZ = j6 + i5;
                vertexY = eastZ + northEastZ >> 1;
                underlayC = eastUnderColour + neUnderColour >> 1;
                overlayColour = eastOverColour + neOverColour >> 1;
            } else if (vertexType == 11) {
                vertexX = i6 + i5;
                vertexZ = j6 + k5;
                vertexY = northEastZ + northZ >> 1;
                underlayC = neUnderColour + northUnderColour >> 1;
                overlayColour = neOverColour + northOverColour >> 1;
            } else if (vertexType == 12) {
                vertexX = i6 + j5;
                vertexZ = j6 + i5;
                vertexY = northZ + centreZ >> 1;
                underlayC = northUnderColour + centerUnderColour >> 1;
                overlayColour = northOverColour + centerOverColour >> 1;
            } else if (vertexType == 13) {
                vertexX = i6 + j5;
                vertexZ = j6 + j5;
                vertexY = centreZ;
                underlayC = centerUnderColour;
                overlayColour = centerOverColour;
            } else if (vertexType == 14) {
                vertexX = i6 + k5;
                vertexZ = j6 + j5;
                vertexY = eastZ;
                underlayC = eastUnderColour;
                overlayColour = eastOverColour;
            } else if (vertexType == 15) {
                vertexX = i6 + k5;
                vertexZ = j6 + k5;
                vertexY = northEastZ;
                underlayC = neUnderColour;
                overlayColour = neOverColour;
            } else {
                vertexX = i6 + j5;
                vertexZ = j6 + k5;
                vertexY = northZ;
                underlayC = northUnderColour;
                overlayColour = northOverColour;
            }

            origVertexX[idx] = vertexX;
            origVertexY[idx] = vertexY;
            origVertexZ[idx] = vertexZ;
            vertexColoursUnderlay[idx] = underlayC;
            vertexColoursOverlay[idx] = overlayColour;
        }

        int[] shapedTileElement = TileShape.shapedTileElementData[type];
        int j7 = shapedTileElement.length / 4;
        triangleA = new int[j7];
        triangleB = new int[j7];
        triangleC = new int[j7];
        triangleHslA = new int[j7];
        triangleHslB = new int[j7];
        triangleHslC = new int[j7];

        if (overlayTextureId != -1) {
            triangleTextureOverlay = new int[j7];
        }
        if(underlayTextureId != -1) {
            triangleTextureUnderlay = new int[j7];
        }
        int l7 = 0;

        for (int j8 = 0; j8 < j7; j8++) {
            int l8 = shapedTileElement[l7];
            int k9 = shapedTileElement[l7 + 1];
            int i10 = shapedTileElement[l7 + 2];
            int k10 = shapedTileElement[l7 + 3];
            l7 += 4;
            if (k9 < 4) {
                k9 = k9 - orientation & 3;
            }
            if (i10 < 4) {
                i10 = i10 - orientation & 3;
            }
            if (k10 < 4) {
                k10 = k10 - orientation & 3;
            }
            triangleA[j8] = k9;
            triangleB[j8] = i10;
            triangleC[j8] = k10;
            if (l8 == 0) {
                triangleHslA[j8] = vertexColoursUnderlay[k9];
                triangleHslB[j8] = vertexColoursUnderlay[i10];
                triangleHslC[j8] = vertexColoursUnderlay[k10];
                if (triangleTextureUnderlay != null)
                    triangleTextureUnderlay[j8] = underlayTextureId;

            } else {
                triangleHslA[j8] = vertexColoursOverlay[k9];
                triangleHslB[j8] = vertexColoursOverlay[i10];
                triangleHslC[j8] = vertexColoursOverlay[k10];
                if (triangleTextureOverlay != null)
                    triangleTextureOverlay[j8] = overlayTextureId;

            }
        }

        int i9 = centreZ;
        int l9 = eastZ;

        if (eastZ < i9) {
            i9 = eastZ;
        } else if (eastZ > l9) {
            l9 = eastZ;
        }

        if (northEastZ < i9) {
            i9 = northEastZ;
        } else if (northEastZ > l9) {
            l9 = northEastZ;
        }

        if (northZ < i9) {
            i9 = northZ;
        } else if (northZ > l9) {
            l9 = northZ;
        }

        i9 /= 14;
        l9 /= 14;
        textured = !hdTextures || overlayTextureId != -1 || underlayTextureId != -1;
    }

    public int getOrientation() {
        return orientation;
    }

    public int[] getOrigVertexX() {
        return origVertexX;
    }

    public int[] getOrigVertexY() {
        return origVertexY;
    }

    public int[] getOrigVertexZ() {
        return origVertexZ;
    }

    public int getTextureColour() {
        return textureColour;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getTileType() {
        return tileType;
    }

    public int[] getTriangleA() {
        return triangleA;
    }

    public int[] getTriangleB() {
        return triangleB;
    }

    public int[] getTriangleC() {
        return triangleC;
    }

    public int[] getTriangleHslA() {
        return triangleHslA;
    }

    public int[] getTriangleHslB() {
        return triangleHslB;
    }

    public int[] getTriangleHslC() {
        return triangleHslC;
    }

    public int[] getTriangleTextureOverlay() {
        return triangleTextureOverlay;
    }

    public int getUnderlayColour() {
        return underlayColour;
    }

    public boolean isFlat() {
        return flat;
    }

    public void regenerateHeights(int x, int y, int centreZ, int northZ, int northEastZ, int eastZ, int southEastZ,
                                  int southZ, int southWestZ, int westZ) {

        int j5 = 32;
        int i5 = 64;
        int k5 = 96;
        int c = 128;

        int i6 = x * c;
        int j6 = y * c;

        int[] tileShape = TileShape.tileShapePoints[tileType];
        int tileShapeLength = tileShape.length;
        for (int idx = 0; idx < tileShapeLength; idx++) {
            int vertexType = tileShape[idx];
            if ((vertexType & 1) == 0 && vertexType <= 8) {
                vertexType = (vertexType - orientation - orientation - 1 & 7) + 1;
            }

            if (vertexType > 8 && vertexType <= 12) {
                vertexType = (vertexType - 9 - orientation & 3) + 9;
            }

            if (vertexType > 12 && vertexType <= 16) {
                vertexType = (vertexType - 13 - orientation & 3) + 13;
            }

            int vertexX;
            int vertexZ;
            int vertexY;

            if (vertexType == 1) {
                vertexX = i6;
                vertexZ = j6;
                vertexY = centreZ;
            } else if (vertexType == 2) {
                vertexX = i6 + i5;
                vertexZ = j6;
                vertexY = centreZ + eastZ >> 1;
            } else if (vertexType == 3) {
                vertexX = i6 + c;
                vertexZ = j6;
                vertexY = eastZ;
            } else if (vertexType == 4) {
                vertexX = i6 + c;
                vertexZ = j6 + i5;
                vertexY = eastZ + northEastZ >> 1;
            } else if (vertexType == 5) {
                vertexX = i6 + c;
                vertexZ = j6 + c;
                vertexY = northEastZ;
            } else if (vertexType == 6) {
                vertexX = i6 + i5;
                vertexZ = j6 + c;
                vertexY = northEastZ + northZ >> 1;
            } else if (vertexType == 7) {
                vertexX = i6;
                vertexZ = j6 + c;
                vertexY = northZ;
            } else if (vertexType == 8) {
                vertexX = i6;
                vertexZ = j6 + i5;
                vertexY = northZ + centreZ >> 1;
            } else if (vertexType == 9) {
                vertexX = i6 + i5;
                vertexZ = j6 + j5;
                vertexY = centreZ + eastZ >> 1;
            } else if (vertexType == 10) {
                vertexX = i6 + k5;
                vertexZ = j6 + i5;
                vertexY = eastZ + northEastZ >> 1;
            } else if (vertexType == 11) {
                vertexX = i6 + i5;
                vertexZ = j6 + k5;
                vertexY = northEastZ + northZ >> 1;
            } else if (vertexType == 12) {
                vertexX = i6 + j5;
                vertexZ = j6 + i5;
                vertexY = northZ + centreZ >> 1;
            } else if (vertexType == 13) {
                vertexX = i6 + j5;
                vertexZ = j6 + j5;
                vertexY = centreZ;
            } else if (vertexType == 14) {
                vertexX = i6 + k5;
                vertexZ = j6 + j5;
                vertexY = eastZ;
            } else if (vertexType == 15) {
                vertexX = i6 + k5;
                vertexZ = j6 + k5;
                vertexY = northEastZ;
            } else {
                vertexX = i6 + j5;
                vertexZ = j6 + k5;
                vertexY = northZ;
            }

            origVertexX[idx] = vertexX;
            origVertexY[idx] = vertexY;
            origVertexZ[idx] = vertexZ;
        }

        int[] ai3 = TileShape.shapedTileElementData[tileType];
        int j7 = ai3.length / 4;
        triangleA = new int[j7];
        triangleB = new int[j7];
        triangleC = new int[j7];
        int l7 = 0;

        for (int j8 = 0; j8 < j7; j8++) {
            int k9 = ai3[l7 + 1];
            int i10 = ai3[l7 + 2];
            int k10 = ai3[l7 + 3];
            l7 += 4;
            if (k9 < 4) {
                k9 = k9 - orientation & 3;
            }
            if (i10 < 4) {
                i10 = i10 - orientation & 3;
            }
            if (k10 < 4) {
                k10 = k10 - orientation & 3;
            }
            triangleA[j8] = k9;
            triangleB[j8] = i10;
            triangleC[j8] = k10;

        }

        int i9 = centreZ;
        int l9 = eastZ;

        if (eastZ < i9) {
            i9 = eastZ;
        } else if (eastZ > l9) {
            l9 = eastZ;
        }

        if (northEastZ < i9) {
            i9 = northEastZ;
        } else if (northEastZ > l9) {
            l9 = northEastZ;
        }

        if (northZ < i9) {
            i9 = northZ;
        } else if (northZ > l9) {
            l9 = northZ;
        }

        i9 /= 14;
        l9 /= 14;
    }

    public void draw(ModelBuffers modelBuffers, int x, int y, int z) {

        GpuIntBuffer b = modelBuffers.getModelBufferUnordered();
        modelBuffers.incUnorderedModels();

        b.ensureCapacity(8);
        IntBuffer buffer = b.getBuffer();
        buffer.put(getBufferOffset());
        buffer.put(getUvBufferOffset());
        buffer.put(getBufferLen() / 3);
        buffer.put(modelBuffers.getTargetBufferOffset());
        buffer.put(ModelBuffers.FLAG_SCENE_BUFFER);
        buffer.put(x).put(z).put(y);

        modelBuffers.addTargetBufferOffset(getBufferLen());
    }
}