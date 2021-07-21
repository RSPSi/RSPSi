/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.gpu;


import com.rspsi.jagex.Client;
import com.rspsi.jagex.entity.Renderable;
import com.rspsi.jagex.entity.model.ModelInstance;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.object.GroundDecoration;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.jagex.map.tile.ShapedTile;
import com.rspsi.jagex.map.tile.SimpleTile;
import com.rspsi.misc.Location;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.runelite.gpu.util.Perspective;
import org.joml.Vector3i;

@Slf4j
public class SceneUploader {
    private final int REGION_SIZE = 64;
    int sceneId = (int) (System.currentTimeMillis() / 1000L);
    private Client client;
    private int offset;
    private int uvoffset;

    public SceneUploader(Client client) {
        this.client = client;
    }

    public void upload(SceneGraph scene, GpuIntBuffer vertexbuffer, GpuFloatBuffer uvBuffer) {
        offset = 0;
        uvoffset = 0;
        vertexbuffer.clear();
        uvBuffer.clear();

        for (int z = 0; z < 4; ++z) {
            for (int x = 0; x < scene.width; ++x) {
                for (int y = 0; y < scene.length; ++y) {
                    val pos = new Vector3i(x, y, z);
                    SceneTile tile = scene.tiles.get(pos);
                    if (tile != null) {
                        reset(tile);
                    }
                }
            }
        }

        for (int z = 0; z < 4; ++z)
        for (int x = 0; x < scene.width; ++x) {
            for (int y = 0; y < scene.length; ++y) {
                val pos = new Vector3i(x, y, z);
                SceneTile tile = scene.tiles.get(pos);
                if (tile != null) {
                    upload(tile, vertexbuffer, uvBuffer);
                }
            }
        }
    }

    public void reset(SceneTile tile) {
		/*SceneTile bridge = tile.tileBelow;
		if (bridge != null)
		{
			reset(bridge);
		}
		 */

        SimpleTile sceneTilePaint = tile.simple;
        if (sceneTilePaint != null) {
            sceneTilePaint.setBufferOffset(-1);
        }

        ShapedTile sceneTileModel = tile.shape;
        if (sceneTileModel != null) {
            sceneTileModel.setBufferOffset(-1);
        }

        tile.nonNullStream().forEach(defaultWorldObject -> {

            if (defaultWorldObject.primary != null) {
                defaultWorldObject.primary.setBufferOffset(-1);
            }
            if (defaultWorldObject.secondary != null) {
                defaultWorldObject.secondary.setBufferOffset(-1);
            }
            defaultWorldObject.uploaded = false;
        });

    }

    public void upload(SceneTile tile, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer) {
		/*SceneTile bridge = tile.tileBelow;
		if (bridge != null)
		{
			upload(bridge, vertexBuffer, uvBuffer);
		}*/

        SimpleTile sceneTilePaint = tile.simple;
        if (sceneTilePaint != null) {
            sceneTilePaint.setBufferOffset(offset);
            if (sceneTilePaint.getOverlayTextureId() != -1) {
                sceneTilePaint.setUvBufferOffset(uvoffset);
            } else {
                sceneTilePaint.setUvBufferOffset(-1);
            }

            int len = upload(sceneTilePaint, tile.worldPos, vertexBuffer, uvBuffer, 0, 0, false);
            sceneTilePaint.setBufferLen(len);
            offset += len;
            if (sceneTilePaint.getOverlayTextureId() != -1) {
                uvoffset += len;
            }
        }

        ShapedTile sceneTileModel = tile.shape;
        if (sceneTileModel != null) {
            sceneTileModel.setBufferOffset(offset);
            if (sceneTileModel.getTextureId() != -1) {
                sceneTileModel.setUvBufferOffset(uvoffset);
            } else {
                sceneTileModel.setUvBufferOffset(-1);
            }
            int len = upload(sceneTileModel, tile.worldPos, vertexBuffer, uvBuffer, 0, 0, false);
            sceneTileModel.setBufferLen(len);
            offset += len;
            if (sceneTileModel.getTextureId() != -1) {
                uvoffset += len;
            }
        }

        tile.nonNullStream().forEachOrdered(defaultWorldObject -> {

            Renderable renderable1 = defaultWorldObject.primary;
            if (defaultWorldObject instanceof GroundDecoration && renderable1 instanceof ModelInstance && ((GroundDecoration) defaultWorldObject).getMinimapFunction() != null) {
                uploadModel((ModelInstance) renderable1, vertexBuffer, uvBuffer, true);

                return;
            }
            if (renderable1 instanceof ModelInstance) {
                uploadModel((ModelInstance) renderable1, vertexBuffer, uvBuffer, false);
            }

            Renderable renderable2 = defaultWorldObject.secondary;
            if (renderable2 instanceof ModelInstance) {
                uploadModel((ModelInstance) renderable2, vertexBuffer, uvBuffer, false);
            }
        });

    }

    public int upload(SimpleTile tile, Vector3i worldPos, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer,
                      int offsetX, int offsetY, boolean padUvs) {
        final int[][][] tileHeights = client.mapRegion.tileHeights;

        final int localX = offsetX;
        final int localY = offsetY;

        int tileZ = worldPos.z;
        int tileX = worldPos.x;
        int tileY = worldPos.y;

        int swHeight = tileHeights[tileZ][tileX][tileY];
        int seHeight = tileHeights[tileZ][tileX + 1][tileY];
        int neHeight = tileHeights[tileZ][tileX + 1][tileY + 1];
        int nwHeight = tileHeights[tileZ][tileX][tileY + 1];

        final int neColor = tile.getNorthEastColour();//TODO Set these to the correct values
        final int nwColor = tile.getNorthColour();
        final int seColor = tile.getEastColour();
        final int swColor = tile.getCentreColour();

        if (neColor == 12345678) {
            return 0;
        }

        vertexBuffer.ensureCapacity(24 * 2);
        uvBuffer.ensureCapacity(24* 2);

        // 0,0
        int vertexDx = localX;
        int vertexDy = localY;
        int vertexDz = swHeight;
        final int c1 = swColor;

        // 1,0
        int vertexCx = localX + Perspective.LOCAL_TILE_SIZE;
        int vertexCy = localY;
        int vertexCz = seHeight;
        final int c2 = seColor;

        // 1,1
        int vertexAx = localX + Perspective.LOCAL_TILE_SIZE;
        int vertexAy = localY + Perspective.LOCAL_TILE_SIZE;
        int vertexAz = neHeight;
        final int c3 = neColor;

        // 0,1
        int vertexBx = localX;
        int vertexBy = localY + Perspective.LOCAL_TILE_SIZE;
        int vertexBz = nwHeight;
        final int c4 = nwColor;

        vertexBuffer.put(vertexAx, vertexAz, vertexAy, c3);
        vertexBuffer.put(vertexBx, vertexBz, vertexBy, c4);
        vertexBuffer.put(vertexCx, vertexCz, vertexCy, c2);

        vertexBuffer.put(vertexDx, vertexDz, vertexDy, c1);
        vertexBuffer.put(vertexCx, vertexCz, vertexCy, c2);
        vertexBuffer.put(vertexBx, vertexBz, vertexBy, c4);

        if (tile.getOverlayTextureId() != -1) {
            float tex = tile.getOverlayTextureId() + 1f;
            uvBuffer.put(tex, 1.0f, 1.0f, 0f);
            uvBuffer.put(tex, 0.0f, 1.0f, 0f);
            uvBuffer.put(tex, 1.0f, 0.0f, 0f);

            uvBuffer.put(tex, 0.0f, 0.0f, 0f);
            uvBuffer.put(tex, 1.0f, 0.0f, 0f);
            uvBuffer.put(tex, 0.0f, 1.0f, 0f);
        } else if (padUvs) {

            uvBuffer.put(0f, 0f, 0f, 0f);
            uvBuffer.put(0f, 0f, 0f, 0f);
            uvBuffer.put(0f, 0f, 0f, 0f);

            uvBuffer.put(0f, 0f, 0f, 0f);
            uvBuffer.put(0f, 0f, 0f, 0f);
            uvBuffer.put(0f, 0f, 0f, 0f);
        }

        return 6;
    }

    public int upload(ShapedTile sceneTileModel, Vector3i worldPos, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer,
                      int offsetX, int offsetY, boolean padUvs) {


        int tileZ = worldPos.z;
        int tileX = worldPos.x;
        int tileY = worldPos.y;

        final int[] faceX = sceneTileModel.getTriangleA();
        final int[] faceY = sceneTileModel.getTriangleB();
        final int[] faceZ = sceneTileModel.getTriangleC();

        final int[] vertexX = sceneTileModel.getOrigVertexX();
        final int[] vertexY = sceneTileModel.getOrigVertexY();
        final int[] vertexZ = sceneTileModel.getOrigVertexZ();

        final int[] triangleColorA = sceneTileModel.getTriangleHslA();
        final int[] triangleColorB = sceneTileModel.getTriangleHslB();
        final int[] triangleColorC = sceneTileModel.getTriangleHslC();

        final int[] triangleTextures = sceneTileModel.getTriangleTextureOverlay();

        final int faceCount = faceX.length;
        vertexBuffer.ensureCapacity(faceCount * 24);
        uvBuffer.ensureCapacity(faceCount * 24);

        int baseX = Perspective.LOCAL_TILE_SIZE * tileX;
        int baseY = Perspective.LOCAL_TILE_SIZE * tileY;

        int cnt = 0;
        for (int i = 0; i < faceCount; ++i) {
            final int triangleA = faceX[i];
            final int triangleB = faceY[i];
            final int triangleC = faceZ[i];

            final int colorA = triangleColorA[i];
            final int colorB = triangleColorB[i];
            final int colorC = triangleColorC[i];

            if (colorA == 12345678) {
                continue;
            }

            cnt += 3;

            // vertexes are stored in scene local, convert to tile local
            int vertexXA = vertexX[triangleA] - baseX;
            int vertexZA = vertexZ[triangleA] - baseY;

            int vertexXB = vertexX[triangleB] - baseX;
            int vertexZB = vertexZ[triangleB] - baseY;

            int vertexXC = vertexX[triangleC] - baseX;
            int vertexZC = vertexZ[triangleC] - baseY;

            vertexBuffer.put(vertexXA + offsetX, vertexY[triangleA], vertexZA + offsetY, colorA);
            vertexBuffer.put(vertexXB + offsetX, vertexY[triangleB], vertexZB + offsetY, colorB);
            vertexBuffer.put(vertexXC + offsetX, vertexY[triangleC], vertexZC + offsetY, colorC);


            if (sceneTileModel.getTextureId() != -1) {
                float tex = triangleTextures[i] + 1f;
                uvBuffer.put(tex, vertexXA / 128f, vertexZA / 128f, 0f);
                uvBuffer.put(tex, vertexXB / 128f, vertexZB / 128f, 0f);
                uvBuffer.put(tex, vertexXC / 128f, vertexZC / 128f, 0f);
            } else if (padUvs) {
                uvBuffer.put(0, 0, 0, 0f);
                uvBuffer.put(0, 0, 0, 0f);
                uvBuffer.put(0, 0, 0, 0f);
            }

        }

        return cnt;
    }

    public void uploadModel(ModelInstance model, GpuIntBuffer vertexBuffer, GpuFloatBuffer uvBuffer, boolean forceTexture) {
        if (model.getBufferOffset() > 0) {
            return;
        }

        model.setBufferOffset(offset);
        if (model.faceTextures != null || forceTexture) {
            model.setUvBufferOffset(uvoffset);
        } else {
            model.setUvBufferOffset(-1);
        }
        model.sceneId = (sceneId);

        vertexBuffer.ensureCapacity(model.vertexBuffer.getOffset());
        uvBuffer.ensureCapacity(model.uvBuffer.getOffset());


        offset += model.vertexBuffer.getOffset();
        vertexBuffer.put(model.vertexBuffer);

        if (model.faceTextures != null || forceTexture) {
            uvoffset += model.uvBuffer.getOffset();
            uvBuffer.put(model.uvBuffer);
        }
    }



}
