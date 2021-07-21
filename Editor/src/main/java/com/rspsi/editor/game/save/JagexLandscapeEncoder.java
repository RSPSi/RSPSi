package com.rspsi.editor.game.save;

import com.rspsi.jagex.chunk.Chunk;
import com.rspsi.jagex.io.Buffer;
import com.rspsi.jagex.map.MapRegion;
import com.rspsi.jagex.map.SceneGraph;
import lombok.val;

import java.util.Arrays;

public class JagexLandscapeEncoder implements MapEncoder {

    private static final int CHUNK_SIZE = 64;

    @Override
    public byte[] encode(Chunk chunk) {
        val mapRegion = chunk.mapRegion;
        Buffer buffer = new Buffer(new byte[131072]);

        for(int z = 0; z < 4; z++){
            for(int x = chunk.offsetX;x< chunk.offsetX + CHUNK_SIZE;x++){
                for(int y = chunk.offsetY; y < chunk.offsetY + CHUNK_SIZE; y++){
                    if (mapRegion.overlays[z][x][y] != 0) {
                        buffer.writeByte(mapRegion.overlayShapes[z][x][y] * 4 + (mapRegion.overlayOrientations[z][x][y] & 3) + 2);
                        buffer.writeByte(mapRegion.overlays[z][x][y]);
                    }
                    if (mapRegion.tileFlags[z][x][y] != 0) {
                        buffer.writeByte(mapRegion.tileFlags[z][x][y] + 49);
                    }
                    if (mapRegion.underlays[z][x][y] != 0) {
                        buffer.writeByte(mapRegion.underlays[z][x][y] + 81);
                    }
                    if (mapRegion.manualTileHeight[z][x][y] == 1 || z == 0) {
                        buffer.writeByte(1);
                        if (z == 0) {
                            buffer.writeByte(-mapRegion.tileHeights[z][x][y] / 8);
                        } else {
                            buffer.writeByte(-(mapRegion.tileHeights[z][x][y] - mapRegion.tileHeights[z - 1][x][y]) / 8);
                        }
                    } else {
                        buffer.writeByte(0);
                    }
                }
            }
        }


        return Arrays.copyOf(buffer.getPayload(), buffer.getPosition());

    }
}
