package com.rspsi.editor.game.save;

import com.displee.io.impl.InputBuffer;
import com.rspsi.jagex.chunk.Chunk;
import com.rspsi.jagex.io.Buffer;
import com.rspsi.jagex.map.MapRegion;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.util.Constants;
import org.joml.Vector3i;

public class JagexLandscapeDecoder implements MapDecoder {

    @Override
    public void decode(byte[] data, Chunk chunk) {
        InputBuffer buffer = new InputBuffer(data);
        int regionX = 0;//TODO
        int regionY = 0;
        int maxZ = 4;
        int maxX = 64;
        int maxY = 64;
        int[][][] tileHeights = new int[maxZ][maxX][maxY];
        byte[][][] manualTileHeight = new byte[maxZ][maxX][maxY];
        byte[][][] tileFlags = new byte[maxZ][maxX][maxY];
        byte[][][] overlays = new byte[maxZ][maxX][maxY];
        byte[][][] overlayShapes = new byte[maxZ][maxX][maxY];
        byte[][][] overlayOrientations = new byte[maxZ][maxX][maxY];
        byte[][][] underlays = new byte[maxZ][maxX][maxY];


        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    do {
                        int type = buffer.readUnsignedByte();

                        if (type == 0) {
                            manualTileHeight[z][x][y] = 0;
                            if (z == 0) {
                                tileHeights[0][x][y] = -calculateHeight(0xe3b7b + x + regionX, 0x87cce + y + regionY) * 8;
                            } else {
                                tileHeights[z][x][y] = tileHeights[z - 1][x][y] - 240;
                            }

                            break;
                        } else if (type == 1) {
                            manualTileHeight[z][x][y] = 1;
                            int height = buffer.readUnsignedByte();
                            if (height == 1) {
                                height = 0;
                            }
                            if (z == 0) {
                                tileHeights[0][x][y] = -height * 8;
                            } else {
                                tileHeights[z][x][y] = tileHeights[z - 1][x][y] - height * 8;
                            }

                            break;
                        } else if (type <= 49) {
                            overlays[z][x][y] = (byte) buffer.readByte();
                            overlayShapes[z][x][y] = (byte) ((type - 2) / 4);
                            overlayOrientations[z][x][y] = (byte) (type - 2 & 3);
                        } else if (type <= 81) {
                            tileFlags[z][x][y] = (byte) (type - 49);
                        } else {
                            underlays[z][x][y] = (byte) (type - 81);
                        }


                    } while (true);
                }
            }
        }
    }
    public int calculateHeight(int x, int y) {
        int height = interpolatedNoise(x + 45365, y + 0x16713, 4) - 128
                + (interpolatedNoise(x + 10294, y + 37821, 2) - 128 >> 1) + (interpolatedNoise(x, y, 1) - 128 >> 2);
        height = (int) (height * 0.3D) + 35;

        if (height < 10) {
            height = 10;
        } else if (height > 60) {
            height = 60;
        }

        return height;
    }

    private static int interpolate(int a, int b, int angle, int frequencyReciprocal) {
        int cosine = 0x10000 - Constants.COSINE[angle * 1024 / frequencyReciprocal] >> 1;
        return (a * (0x10000 - cosine) >> 16) + (b * cosine >> 16);
    }

    private static int interpolatedNoise(int x, int y, int frequencyReciprocal) {
        int adj_x = x / frequencyReciprocal;
        int i1 = x & frequencyReciprocal - 1;
        int adj_y = y / frequencyReciprocal;
        int k1 = y & frequencyReciprocal - 1;
        int l1 = smoothNoise(adj_x, adj_y);
        int i2 = smoothNoise(adj_x + 1, adj_y);
        int j2 = smoothNoise(adj_x, adj_y + 1);
        int k2 = smoothNoise(adj_x + 1, adj_y + 1);
        int l2 = interpolate(l1, i2, i1, frequencyReciprocal);
        int i3 = interpolate(j2, k2, i1, frequencyReciprocal);
        return interpolate(l2, i3, k1, frequencyReciprocal);
    }



    private static int perlinNoise(int x, int y) {
        int n = x + y * 57;
        n = n << 13 ^ n;
        n = n * (n * n * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
        return n >> 19 & 0xff;
    }

    private static int smoothNoise(int x, int y) {
        int corners = perlinNoise(x - 1, y - 1) + perlinNoise(x + 1, y - 1) + perlinNoise(x - 1, y + 1)
                + perlinNoise(x + 1, y + 1);
        int sides = perlinNoise(x - 1, y) + perlinNoise(x + 1, y) + perlinNoise(x, y - 1) + perlinNoise(x, y + 1);
        int center = perlinNoise(x, y);
        return corners / 16 + sides / 8 + center / 4;
    }



}
