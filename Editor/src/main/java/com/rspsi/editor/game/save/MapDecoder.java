package com.rspsi.editor.game.save;

import com.rspsi.jagex.chunk.Chunk;
import com.rspsi.jagex.map.MapRegion;
import com.rspsi.jagex.map.SceneGraph;
import org.joml.Vector3i;

public interface MapDecoder {

    void decode(byte[] data, Chunk chunk);
}
