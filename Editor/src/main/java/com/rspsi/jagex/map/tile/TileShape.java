package com.rspsi.jagex.map.tile;

import com.rspsi.jagex.cache.loader.floor.FloorType;
import org.joml.Vector2i;
import org.joml.Vector3i;

public class TileShape {


    public static final int TILE_FULL = 128;
    public static final int TILE_HALF = 96;
    public static final int TILE_THREE_QUARTER = 64;
    public static final int TILE_QUARTER = 32;


    public static final int[] SHAPE_LAYOUT =
            {
                     1,   17,    2,    18,    3,
                    24,   13,    9,    14,   19,
                     8,   12,   16,    10,    4,
                    23,    0,   11,    15,   20,
                     7,   22,    6,    21,    5

            };
    public static final int[][] tileShapePoints = {
            { 1, 3, 5, 7 },
            { 1, 3, 5, 7 },
            { 1, 3, 5, 7 },
            { 1, 3, 5, 7, 6 },
            { 1, 3, 5, 7, 6 },
            { 1, 3, 5, 7, 6 },
            { 1, 3, 5, 7, 6 },
            { 1, 3, 5, 7, 2, 6 },
            { 1, 3, 5, 7, 2, 8 },
            { 1, 3, 5, 7, 2, 8 },
            { 1, 3, 5, 7, 11, 12 },
            { 1, 3, 5, 7, 11, 12 },
            { 1, 3, 5, 7, 13, 14 }};

    public static final Vector2i[] coords2D = {
            new Vector2i(32, 96), new Vector2i(0, 0), new Vector2i(64, 0), new Vector2i(128, 0),
            new Vector2i(128, 64), new Vector2i(128, 128), new Vector2i(64, 128), new Vector2i(0, 128),
            new Vector2i(0, 64), new Vector2i(64, 32), new Vector2i(96, 64), new Vector2i(64, 96),
            new Vector2i(32, 64), new Vector2i(32, 32), new Vector2i(96, 32), new Vector2i(96, 96),

            //Custom points below this line
            new Vector2i(64, 64), new Vector2i(32, 0), new Vector2i(96, 0), new Vector2i(128, 32),
            new Vector2i(128, 96), new Vector2i(96,  128), new Vector2i(32, 128), new Vector2i(0, 96),
            new Vector2i(0 , 32),

    };

    public static final Object[][] shapedTileElementData2 =
            {
                    {
                            FloorType.OVERLAY, coords2D[3], coords2D[5], coords2D[7],
                            FloorType.OVERLAY, coords2D[1], coords2D[3], coords2D[7]
                    },
                    {
                            FloorType.UNDERLAY, coords2D[3], coords2D[5], coords2D[7],
                            FloorType.UNDERLAY, coords2D[1], coords2D[3], coords2D[7]
                    },
                    {
                            FloorType.OVERLAY, coords2D[3], coords2D[5], coords2D[7],
                            FloorType.UNDERLAY, coords2D[1], coords2D[3], coords2D[7]
                    },
                    {
                            FloorType.OVERLAY, coords2D[1], coords2D[3], coords2D[5],
                            FloorType.OVERLAY, coords2D[1], coords2D[5], coords2D[6],
                            FloorType.UNDERLAY, coords2D[1], coords2D[6], coords2D[7]
                    },
                    {
                            FloorType.OVERLAY, coords2D[1], coords2D[3], coords2D[6],
                            FloorType.OVERLAY, coords2D[1], coords2D[6], coords2D[7],
                            FloorType.UNDERLAY, coords2D[3], coords2D[5], coords2D[6]
                    },
                    {
                            FloorType.OVERLAY, coords2D[1], coords2D[6], coords2D[7],
                            FloorType.UNDERLAY, coords2D[1], coords2D[3], coords2D[5],
                            FloorType.UNDERLAY, coords2D[1], coords2D[5], coords2D[6]
                    },
                    {
                            FloorType.OVERLAY, coords2D[3], coords2D[5], coords2D[6],
                            FloorType.UNDERLAY, coords2D[1], coords2D[3], coords2D[6],
                            FloorType.UNDERLAY, coords2D[1], coords2D[6], coords2D[7]
                    },
                    {
                            FloorType.OVERLAY, coords2D[2], coords2D[3], coords2D[5],
                            FloorType.OVERLAY, coords2D[2], coords2D[5], coords2D[6],
                            FloorType.UNDERLAY, coords2D[1], coords2D[2], coords2D[6],
                            FloorType.UNDERLAY, coords2D[1], coords2D[6], coords2D[7]
                    },
                    {
                            FloorType.OVERLAY, coords2D[2], coords2D[3], coords2D[5],
                            FloorType.OVERLAY, coords2D[2], coords2D[5], coords2D[7],
                            FloorType.OVERLAY, coords2D[2], coords2D[7], coords2D[8],
                            FloorType.UNDERLAY, coords2D[1], coords2D[2], coords2D[8]
                    },
                    {
                            FloorType.OVERLAY, coords2D[1], coords2D[2], coords2D[8],
                            FloorType.UNDERLAY, coords2D[2], coords2D[3], coords2D[5],
                            FloorType.UNDERLAY, coords2D[2], coords2D[5], coords2D[7],
                            FloorType.UNDERLAY, coords2D[2], coords2D[7], coords2D[8]
                    },
                    {
                            FloorType.OVERLAY, coords2D[1], coords2D[3], coords2D[12],
                            FloorType.OVERLAY, coords2D[3], coords2D[11], coords2D[12],
                            FloorType.OVERLAY, coords2D[3], coords2D[5], coords2D[11],
                            FloorType.UNDERLAY, coords2D[1], coords2D[12], coords2D[7],
                            FloorType.UNDERLAY, coords2D[12], coords2D[11], coords2D[7],
                            FloorType.UNDERLAY, coords2D[11], coords2D[5], coords2D[7]
                    },
                    {
                            FloorType.UNDERLAY, coords2D[1], coords2D[3], coords2D[12],
                            FloorType.UNDERLAY, coords2D[3], coords2D[11], coords2D[12],
                            FloorType.UNDERLAY, coords2D[3], coords2D[5], coords2D[11],
                            FloorType.OVERLAY, coords2D[1], coords2D[12], coords2D[7],
                            FloorType.OVERLAY, coords2D[12], coords2D[11], coords2D[7],
                            FloorType.OVERLAY, coords2D[11], coords2D[5], coords2D[7]
                    },
                    {
                            FloorType.UNDERLAY, coords2D[1], coords2D[14], coords2D[13],
                            FloorType.UNDERLAY, coords2D[1], coords2D[3], coords2D[14],
                            FloorType.OVERLAY, coords2D[1], coords2D[13], coords2D[7],
                            FloorType.OVERLAY, coords2D[13], coords2D[14], coords2D[7],
                            FloorType.OVERLAY, coords2D[14], coords2D[5], coords2D[7],
                            FloorType.OVERLAY, coords2D[3], coords2D[5], coords2D[14]
                    },
            };






    public static final int[][] shapedTileElementData = {
            { 0, 1, 2, 3, /**/ 0, 0, 1, 3 },
            { 1, 1, 2, 3, /**/ 1, 0, 1, 3 },
            { 0, 1, 2, 3, /**/ 1, 0, 1, 3 },
            { 0, 0, 1, 2, /**/ 0, 0, 2, 4, /**/ 1, 0, 4, 3 },
            { 0, 0, 1, 4, /**/ 0, 0, 4, 3, /**/ 1, 1, 2, 4 },
            { 0, 0, 4, 3, /**/ 1, 0, 1, 2, /**/ 1, 0, 2, 4 },
            { 0, 1, 2, 4, /**/ 1, 0, 1, 4, /**/ 1, 0, 4, 3 },
            { 0, 4, 1, 2, /**/ 0, 4, 2, 5, /**/ 1, 0, 4, 5, /**/ 1, 0, 5, 3 },
            { 0, 4, 1, 2, /**/ 0, 4, 2, 3, /**/ 0, 4, 3, 5, /**/ 1, 0, 4, 5 },
            { 0, 0, 4, 5, /**/ 1, 4, 1, 2, /**/ 1, 4, 2, 3, /**/ 1, 4, 3, 5 },
            { 0, 0, 1, 5, /**/ 0, 1, 4, 5, /**/ 0, 1, 2, 4, /**/ 1, 0, 5, 3, /**/ 1, 5, 4, 3, /**/ 1, 4, 2, 3 },
            { 1, 0, 1, 5, /**/ 1, 1, 4, 5, /**/ 1, 1, 2, 4, /**/ 0, 0, 5, 3, /**/ 0, 5, 4, 3, /**/ 0, 4, 2, 3 },
            { 1, 0, 5, 4, /**/ 1, 0, 1, 5, /**/ 0, 0, 4, 3, /**/ 0, 4, 5, 3, /**/ 0, 5, 2, 3, /**/ 0, 1, 2, 5 },
            { 1, 4, 5, 3 } };






    public static Vector3i[] get2DVertices(int type){
        int orientation = 0;
        int[] tileShape = TileShape.tileShapePoints[type];
        int tileShapeLength = tileShape.length;
        Vector3i[] vertices = new Vector3i[tileShapeLength];


        for (int index = 0; index < tileShapeLength; index++) {
            int vertexY = 0;

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
            int vertexZ;

            switch (vertexType) {
                case 1:
                default:
                    vertexX = 0;
                    vertexZ = 0;
                    break;
                case 2:
                    vertexX = TILE_HALF;
                    vertexZ = 0;
                    break;
                case 3:
                    vertexX = TILE_FULL;
                    vertexZ = 0;
                    break;
                case 4:
                    vertexX = TILE_FULL;
                    vertexZ = TILE_HALF;
                    break;
                case 5:
                    vertexX = TILE_FULL;
                    vertexZ = TILE_FULL;
                    break;
                case 6:
                    vertexX = TILE_HALF;
                    vertexZ = TILE_FULL;
                    break;
                case 7:
                    vertexX = 0;
                    vertexZ = TILE_FULL;
                    break;
                case 8:
                    vertexX = 0;
                    vertexZ = TILE_HALF;
                    break;
                case 9:
                    vertexX = TILE_HALF;
                    vertexZ = TILE_QUARTER;
                    break;
                case 10:
                    vertexX = TILE_THREE_QUARTER;
                    vertexZ = TILE_HALF;
                    break;
                case 11:
                    vertexX = TILE_HALF;
                    vertexZ = TILE_THREE_QUARTER;
                    break;
                case 12:
                    vertexX = TILE_QUARTER;
                    vertexZ = TILE_HALF;
                    break;
                case 13:
                    vertexX = TILE_QUARTER;
                    vertexZ = TILE_QUARTER;
                    break;
                case 14:
                    vertexX = TILE_THREE_QUARTER;
                    vertexZ = TILE_QUARTER;
                    break;
                case 15:
                    vertexX = TILE_THREE_QUARTER;
                    vertexZ = TILE_THREE_QUARTER;
                    break;
                case 16:
                    vertexX = TILE_QUARTER;
                    vertexZ = TILE_THREE_QUARTER;
                    break;
                case 17:
                    vertexX = TILE_HALF;
                    vertexZ = TILE_HALF;
                    break;
                case 18:
                    vertexX = TILE_QUARTER;
                    vertexZ = 0;
                    break;
                case 19:
                    vertexX = TILE_THREE_QUARTER;
                    vertexZ = 0;
                    break;
                case 20:
                    vertexX = TILE_FULL;
                    vertexZ = TILE_QUARTER;
                    break;
                case 21:
                    vertexX = TILE_FULL;
                    vertexZ = TILE_THREE_QUARTER;
                    break;
                case 22:
                    vertexX = TILE_THREE_QUARTER;
                    vertexZ = TILE_FULL;
                    break;
                case 23:
                    vertexX = TILE_QUARTER;
                    vertexZ = TILE_FULL;
                    break;
                case 24:
                    vertexX = 0;
                    vertexZ = TILE_THREE_QUARTER;
                    break;
                case 25:
                    vertexX = 0;
                    vertexZ = TILE_QUARTER;
                    break;
            }
            vertices[index] = new Vector3i(vertexX, vertexY, vertexZ);
        }
        return vertices;
    }

}
