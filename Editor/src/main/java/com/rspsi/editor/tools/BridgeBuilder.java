package com.rspsi.editor.tools;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.Client.LoadState;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.jagex.util.BitFlag;
import com.rspsi.jagex.util.RenderFlags;
import com.rspsi.options.Options;
import lombok.val;
import org.joml.Vector3i;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BridgeBuilder {

    public static void buildBridge() throws Exception, ToolNotRegisteredException {
        Client client = Client.getSingleton();
        if (client.loadState.get() == LoadState.ACTIVE) {
            if (Options.currentHeight.get() == 3) {
                throw new Exception("Invalid height");
            }
            int oldHeight = Options.tileHeightLevel.get();
            BitFlag oldFlag = Options.tileFlags.get();

            int lowerZ = Options.currentHeight.get();
            Collection<SceneTile> selectedTiles = client.sceneGraph.getSelectedTiles();
            List<SceneTile> tilesAbove = selectedTiles
                    .stream()
                    .map(tile ->
                            client.sceneGraph.tiles.get(new Vector3i(tile.worldPos).add(0, 0, 1))
                    ).collect(Collectors.toList());

            List<SceneTile> tilesAround = tilesAbove
                    .stream()
                    .flatMap(tile -> IntStream
                            .rangeClosed(-1, 1)
                            .boxed()
                            .flatMap(x -> IntStream.rangeClosed(-1, 1).mapToObj(y -> client.sceneGraph.tiles.get(new Vector3i(tile.worldPos).add(x, y, 0)))))
                    .filter(tile -> !tilesAbove.contains(tile))
                    .collect(Collectors.toList());
            int highestHeight = selectedTiles.stream().mapToInt(tile -> -client.mapRegion.tileHeights[lowerZ][tile.worldPos.x][tile.worldPos.y]).max().getAsInt();

            BitFlag bridgeFlag = new BitFlag();
            bridgeFlag.flag(RenderFlags.BRIDGE_TILE);

            System.out.println(tilesAround.size());
            System.out.println("Setting abs values to " + Options.tileHeightLevel.get());

            val mapRegion = client.sceneGraph.getMapRegion();

            tilesAbove.forEach(tile -> {
                mapRegion.tileFlags[tile.worldPos.z][tile.worldPos.x][tile.worldPos.y] = bridgeFlag.encode();
                mapRegion.tileHeights[tile.worldPos.z][tile.worldPos.x][tile.worldPos.y] = highestHeight;

                mapRegion.overlays[tile.worldPos.z][tile.worldPos.x][tile.worldPos.y] = 1;
                mapRegion.overlayShapes[tile.worldPos.z][tile.worldPos.x][tile.worldPos.y] = 1;
            });

            tilesAround.forEach(tile -> {
                mapRegion.tileHeights[tile.worldPos.z][tile.worldPos.x][tile.worldPos.y] = highestHeight;
            });

            Options.tileHeightLevel.set(oldHeight);
            Options.tileFlags.set(oldFlag);

            client.sceneGraph.tileQueue.clear();

        }
    }

}
