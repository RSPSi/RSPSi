package com.rspsi.editor.game.save;

import com.rspsi.jagex.chunk.Chunk;
import com.rspsi.jagex.entity.object.ObjectGroup;
import com.rspsi.jagex.io.Buffer;
import com.rspsi.jagex.map.MapRegion;
import com.rspsi.jagex.map.SceneGraph;
import com.rspsi.jagex.map.object.DefaultWorldObject;

import java.util.*;
import java.util.stream.Collectors;

public interface MapEncoder {

   byte[] encode(Chunk chunk);

}
