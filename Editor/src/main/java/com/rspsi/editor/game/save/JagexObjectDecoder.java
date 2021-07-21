package com.rspsi.editor.game.save;

import com.displee.io.impl.InputBuffer;
import com.rspsi.jagex.cache.def.ObjectDefinition;
import com.rspsi.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.rspsi.jagex.chunk.Chunk;
import com.rspsi.jagex.io.Buffer;
import com.rspsi.jagex.map.MapRegion;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.tile.TileUtils;
import lombok.val;
import org.joml.Vector3i;

public class JagexObjectDecoder implements MapDecoder {

    @Override
    public void decode(byte[] data, Chunk chunk) {
        val sceneGraph = chunk.scenegraph;
        val mapRegion = chunk.mapRegion;

        val buffer = new InputBuffer(data);
        int id = -1;
        do {
            int idOffset = buffer.readSmart2();
            if (idOffset == 0) {
                break;
            }

            id += idOffset;
            int config = 0;

            do {
                int offset = buffer.readSmart2();
                if (offset == 0) {
                    break;
                }

                config += offset - 1;
                int x = config & 0x3f;
                int y = config >> 6 & 0x3f;
                int objectPlane = config >> 12;
                int packed = buffer.readUnsignedByte();
                int type = packed >> 2;
                int rotation = packed & 3;

                val definition = ObjectDefinitionLoader.lookup(id);
                int localX = TileUtils.getObjectXOffset(x & 7, y & 7, definition.getWidth(),
                        definition.getLength(), 0);
                int localY = TileUtils.getObjectYOffset(x & 7, y & 7, definition.getWidth(),
                        definition.getLength(), 0);
                val localPosition = new Vector3i(localX, localY, objectPlane);
                localPosition.add(new Vector3i(chunk.offsetX, chunk.offsetY, 0));
                mapRegion.spawnObjectToWorld(sceneGraph, localPosition, id, type, rotation & 3, false);

            } while (true);
        } while (true);
    }
}
