package com.rspsi.editor.game.save;

import com.rspsi.jagex.chunk.Chunk;
import com.rspsi.jagex.entity.object.ObjectGroup;
import com.rspsi.jagex.io.Buffer;
import com.rspsi.jagex.map.MapRegion;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.object.DefaultWorldObject;
import com.rspsi.jagex.map.object.TypeFilter;
import com.rspsi.jagex.util.ObjectKey;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
public class JagexObjectEncoder implements MapEncoder {

    private static final int CHUNK_SIZE = 64;

    public List<DefaultWorldObject> getChunkObjects(SceneGraph sceneGraph, Chunk chunk) {

        return sceneGraph.tiles
                .values()
                .stream()
                .filter(tile -> chunk.inChunk(tile.worldPos))
                .flatMap(tile -> tile.getObjectsStream(TypeFilter.noFilter))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] encode(Chunk chunk) {
        val sceneGraph = chunk.scenegraph;

        TreeMap<Integer, ObjectGroup> objectGroupMap = new TreeMap<>();
        List<DefaultWorldObject> chunkObjects = getChunkObjects(sceneGraph, chunk);

        chunkObjects.forEach(worldObject -> {
            ObjectKey objectKey = worldObject.getKey();
            ObjectGroup objectGroup = objectGroupMap.getOrDefault(objectKey.getId(), new ObjectGroup(objectKey.getId()));
            objectGroup.addObject(worldObject);
            objectGroupMap.put(objectKey.getId(), objectGroup);
        });

        Buffer buff = new Buffer(new byte[131072]);

        int lastObjectId = -1;


        for (Map.Entry<Integer, ObjectGroup> entry : objectGroupMap.entrySet()) {

            int objectId = entry.getKey();
            ObjectGroup group = entry.getValue();

            if (group.getObjects().size() <= 0)
                log.warn("Problem while saving. Object ID {} was present as key but no objects were found!", objectId);
            int newObj = objectId - lastObjectId;

            buff.writeUSmartInt(newObj);
            group.sort();
            int previousLocHash = 0;
            for (DefaultWorldObject obj : group.getObjects()) {

                int locHash = obj.getLocHash();

                int newLocHash = locHash - previousLocHash + 1;
                if (previousLocHash != locHash) {
                    buff.writeUSmartInt(newLocHash);
                } else {
                    buff.writeUSmartInt(1);
                }
                buff.writeByte(obj.getConfig());
                previousLocHash = locHash;
            }
            buff.writeUSmart(0);
            lastObjectId = objectId;
        }

        buff.writeUSmartInt(0);

        return Arrays.copyOf(buff.getPayload(), buff.getPosition());
    }
}
