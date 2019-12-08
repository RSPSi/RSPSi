package com.jagex.map.tile;

import com.rspsi.options.Options;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public final class ShapedTile {

	public static int[] screenX = new int[6];
	public static int[] screenY = new int[6];
	public static int[] screenZ = new int[6];
	public static int[] viewSpaceX = new int[6];
	public static int[] viewSpaceY = new int[6];
	public static int[] viewSpaceZ = new int[6];
	public static int[] anIntArray693 = { 1, 0 };
	public static int[] anIntArray694 = { 2, 1 };
	public static int[] anIntArray695 = { 3, 3 };

	public static final int[][] tileShapePoints = { { 1, 3, 5, 7 }, { 1, 3, 5, 7 }, { 1, 3, 5, 7 }, { 1, 3, 5, 7, 6 },
			{ 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 2, 6 }, { 1, 3, 5, 7, 2, 8 },
			{ 1, 3, 5, 7, 2, 8 }, { 1, 3, 5, 7, 11, 12 }, { 1, 3, 5, 7, 11, 12 }, { 1, 3, 5, 7, 13, 14 } };

	public static final int[][] shapedTileElementData = { { 0, 1, 2, 3, 0, 0, 1, 3 }, { 1, 1, 2, 3, 1, 0, 1, 3 },
			{ 0, 1, 2, 3, 1, 0, 1, 3 }, { 0, 0, 1, 2, 0, 0, 2, 4, 1, 0, 4, 3 }, { 0, 0, 1, 4, 0, 0, 4, 3, 1, 1, 2, 4 },
			{ 0, 0, 4, 3, 1, 0, 1, 2, 1, 0, 2, 4 }, { 0, 1, 2, 4, 1, 0, 1, 4, 1, 0, 4, 3 },
			{ 0, 4, 1, 2, 0, 4, 2, 5, 1, 0, 4, 5, 1, 0, 5, 3 }, { 0, 4, 1, 2, 0, 4, 2, 3, 0, 4, 3, 5, 1, 0, 4, 5 },
			{ 0, 0, 4, 5, 1, 4, 1, 2, 1, 4, 2, 3, 1, 4, 3, 5 },
			{ 0, 0, 1, 5, 0, 1, 4, 5, 0, 1, 2, 4, 1, 0, 5, 3, 1, 5, 4, 3, 1, 4, 2, 3 },
			{ 1, 0, 1, 5, 1, 1, 4, 5, 1, 1, 2, 4, 0, 0, 5, 3, 0, 5, 4, 3, 0, 4, 2, 3 },
			{ 1, 0, 5, 4, 1, 0, 1, 5, 0, 0, 4, 3, 0, 4, 5, 3, 0, 5, 2, 3, 0, 1, 2, 5 } };


	public int[] displayColor;
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
	int[] triangleTexture;
	boolean flat;
	public int orientation;
	public boolean textured;

	private int textureId = -1;
	public int color61;
	public int color71;
	public int color81;
	public int color91;
	public int color62;
	public int color72;
	public int color82;
	public int color92;

	public ShapedTile(int type, int orientation) {
		flat = true;

		tileType = type;
		this.orientation = orientation;

		int qSize = 32;
		int halfSize = 64;
		int threeQSize = 96;
		int fullSize = 128;

		int[] tileShape = tileShapePoints[type];
		int tileShapeLength = tileShape.length;
		origVertexX = new int[tileShapeLength];
		origVertexY = new int[tileShapeLength];
		origVertexZ = new int[tileShapeLength];
		int i6 = 0;
		int j6 = 0;

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

			if (vertexType == 1) {
				vertexX = i6;
				vertexZ = j6;
			} else if (vertexType == 2) {
				vertexX = i6 + halfSize;
				vertexZ = j6;
			} else if (vertexType == 3) {
				vertexX = i6 + fullSize;
				vertexZ = j6;
			} else if (vertexType == 4) {
				vertexX = i6 + fullSize;
				vertexZ = j6 + halfSize;
			} else if (vertexType == 5) {
				vertexX = i6 + fullSize;
				vertexZ = j6 + fullSize;
			} else if (vertexType == 6) {
				vertexX = i6 + halfSize;
				vertexZ = j6 + fullSize;
			} else if (vertexType == 7) {
				vertexX = i6;
				vertexZ = j6 + fullSize;
			} else if (vertexType == 8) {
				vertexX = i6;
				vertexZ = j6 + halfSize;
			} else if (vertexType == 9) {
				vertexX = i6 + halfSize;
				vertexZ = j6 + qSize;
			} else if (vertexType == 10) {
				vertexX = i6 + threeQSize;
				vertexZ = j6 + halfSize;
			} else if (vertexType == 11) {
				vertexX = i6 + halfSize;
				vertexZ = j6 + threeQSize;
			} else if (vertexType == 12) {
				vertexX = i6 + qSize;
				vertexZ = j6 + halfSize;
			} else if (vertexType == 13) {
				vertexX = i6 + qSize;
				vertexZ = j6 + qSize;
			} else if (vertexType == 14) {
				vertexX = i6 + threeQSize;
				vertexZ = j6 + qSize;
			} else if (vertexType == 15) {
				vertexX = i6 + threeQSize;
				vertexZ = j6 + threeQSize;
			} else {
				vertexX = i6 + qSize;
				vertexZ = j6 + threeQSize;
			}

			origVertexX[idx] = vertexX;
			origVertexZ[idx] = vertexZ;
		}

        int[] ai3 = shapedTileElementData[type];
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

	}

	public ShapedTile(int y, int centerOverColour, int northUnderColour, int northEastZ, int overlay_texture, int neOverColour,
			int orientation, int centerUnderColour, int underlayColour, int neUnderColour, int northZ, int eastZ,
			int centreZ, int type, int northOverColour, int eastOverColour, int eastUnderColour, int x,
			int textureColour, int underlay_texture,
			   int underlay_color, boolean tex) {
		
		boolean hdTextures = Options.hdTextures.get();
		textured = tex;
		color61 = centerUnderColour;
		color71 = eastUnderColour;
		color81 = neUnderColour;
		color91 = centerOverColour;
		color62 = northUnderColour;
		color72 = eastOverColour;
		color82 = neOverColour;
		color92 = northOverColour;
        flat = centreZ == eastZ && centreZ == northEastZ && centreZ == northZ;

		tileType = type;
		this.orientation = orientation;
		this.underlayColour = underlayColour;
		this.textureColour = textureColour;
		textureId = overlay_texture;

		int j5 = 32;
		int i5 = 64;
		int k5 = 96;
		int c = 128;

		int[] tileShape = tileShapePoints[type];
		int tileShapeLength = tileShape.length;
		origVertexX = new int[tileShapeLength];
		origVertexY = new int[tileShapeLength];
		origVertexZ = new int[tileShapeLength];
        int[] vertexColourOverlay = new int[tileShapeLength];
        int[] vertexColourUnderlay = new int[tileShapeLength];
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
			int vertexOverlayColour;
			int vertexUnderlayColour;

			if (vertexType == 1) {
				vertexX = i6;
				vertexZ = j6;
				vertexY = centreZ;
				vertexOverlayColour = centerUnderColour;
				vertexUnderlayColour = centerOverColour;
			} else if (vertexType == 2) {
				vertexX = i6 + i5;
				vertexZ = j6;
				vertexY = centreZ + eastZ >> 1;
				vertexOverlayColour = centerUnderColour + eastUnderColour >> 1;
				vertexUnderlayColour = centerOverColour + eastOverColour >> 1;
			} else if (vertexType == 3) {
				vertexX = i6 + c;
				vertexZ = j6;
				vertexY = eastZ;
				vertexOverlayColour = eastUnderColour;
				vertexUnderlayColour = eastOverColour;
			} else if (vertexType == 4) {
				vertexX = i6 + c;
				vertexZ = j6 + i5;
				vertexY = eastZ + northEastZ >> 1;
				vertexOverlayColour = eastUnderColour + neUnderColour >> 1;
				vertexUnderlayColour = eastOverColour + neOverColour >> 1;
			} else if (vertexType == 5) {
				vertexX = i6 + c;
				vertexZ = j6 + c;
				vertexY = northEastZ;
				vertexOverlayColour = neUnderColour;
				vertexUnderlayColour = neOverColour;
			} else if (vertexType == 6) {
				vertexX = i6 + i5;
				vertexZ = j6 + c;
				vertexY = northEastZ + northZ >> 1;
				vertexOverlayColour = neUnderColour + northUnderColour >> 1;
				vertexUnderlayColour = neOverColour + northOverColour >> 1;
			} else if (vertexType == 7) {
				vertexX = i6;
				vertexZ = j6 + c;
				vertexY = northZ;
				vertexOverlayColour = northUnderColour;
				vertexUnderlayColour = northOverColour;
			} else if (vertexType == 8) {
				vertexX = i6;
				vertexZ = j6 + i5;
				vertexY = northZ + centreZ >> 1;
				vertexOverlayColour = northUnderColour + centerUnderColour >> 1;
				vertexUnderlayColour = northOverColour + centerOverColour >> 1;
			} else if (vertexType == 9) {
				vertexX = i6 + i5;
				vertexZ = j6 + j5;
				vertexY = centreZ + eastZ >> 1;
				vertexOverlayColour = centerUnderColour + eastUnderColour >> 1;
				vertexUnderlayColour = centerOverColour + eastOverColour >> 1;
			} else if (vertexType == 10) {
				vertexX = i6 + k5;
				vertexZ = j6 + i5;
				vertexY = eastZ + northEastZ >> 1;
				vertexOverlayColour = eastUnderColour + neUnderColour >> 1;
				vertexUnderlayColour = eastOverColour + neOverColour >> 1;
			} else if (vertexType == 11) {
				vertexX = i6 + i5;
				vertexZ = j6 + k5;
				vertexY = northEastZ + northZ >> 1;
				vertexOverlayColour = neUnderColour + northUnderColour >> 1;
				vertexUnderlayColour = neOverColour + northOverColour >> 1;
			} else if (vertexType == 12) {
				vertexX = i6 + j5;
				vertexZ = j6 + i5;
				vertexY = northZ + centreZ >> 1;
				vertexOverlayColour = northUnderColour + centerUnderColour >> 1;
				vertexUnderlayColour = northOverColour + centerOverColour >> 1;
			} else if (vertexType == 13) {
				vertexX = i6 + j5;
				vertexZ = j6 + j5;
				vertexY = centreZ;
				vertexOverlayColour = centerUnderColour;
				vertexUnderlayColour = centerOverColour;
			} else if (vertexType == 14) {
				vertexX = i6 + k5;
				vertexZ = j6 + j5;
				vertexY = eastZ;
				vertexOverlayColour = eastUnderColour;
				vertexUnderlayColour = eastOverColour;
			} else if (vertexType == 15) {
				vertexX = i6 + k5;
				vertexZ = j6 + k5;
				vertexY = northEastZ;
				vertexOverlayColour = neUnderColour;
				vertexUnderlayColour = neOverColour;
			} else {
				vertexX = i6 + j5;
				vertexZ = j6 + k5;
				vertexY = northZ;
				vertexOverlayColour = northUnderColour;
				vertexUnderlayColour = northOverColour;
			}

			origVertexX[idx] = vertexX;
			origVertexY[idx] = vertexY;
			origVertexZ[idx] = vertexZ;
			vertexColourOverlay[idx] = vertexOverlayColour;
			vertexColourUnderlay[idx] = vertexUnderlayColour;
		}

        int[] ai3 = shapedTileElementData[type];
		int j7 = ai3.length / 4;
		triangleA = new int[j7];
		triangleB = new int[j7];
		triangleC = new int[j7];
		triangleHslA = new int[j7];
		triangleHslB = new int[j7];
		triangleHslC = new int[j7];

		if (overlay_texture != -1 || underlay_texture != -1) {
			triangleTexture = new int[j7];
				displayColor = new int[j7];
		}
		int l7 = 0;

		for (int j8 = 0; j8 < j7; j8++) {
			int l8 = ai3[l7];
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
			if (l8 == 0) {
				triangleHslA[j8] = vertexColourOverlay[k9];
				triangleHslB[j8] = vertexColourOverlay[i10];
				triangleHslC[j8] = vertexColourOverlay[k10];
				if(hdTextures) {

					if (triangleTexture != null) {
						triangleTexture[j8] = underlay_texture;
					}
					
					if (displayColor != null) {
						displayColor[j8] = underlay_color;
					}
				} else {
					if (triangleTexture != null) {
						triangleTexture[j8] = -1;
					}
				}
			} else {
				triangleHslA[j8] = vertexColourUnderlay[k9];
				triangleHslB[j8] = vertexColourUnderlay[i10];
				triangleHslC[j8] = vertexColourUnderlay[k10];
					if(triangleTexture != null)
						triangleTexture[j8] = overlay_texture;
						
					if(displayColor != null)
						displayColor[j8] = textureColour;
				
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
		textured = !hdTextures || tex;
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

	public int[] getTriangleTexture() {
		return triangleTexture;
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

		int[] tileShape = tileShapePoints[tileType];
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

        int[] ai3 = shapedTileElementData[tileType];
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
}