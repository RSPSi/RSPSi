package com.rspsi.tools;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.major.map.RenderFlags;

import com.jagex.Client;
import com.jagex.Client.LoadState;
import com.jagex.map.tile.SceneTile;
import com.jagex.util.BitFlag;
import com.rspsi.options.Options;

public class BridgeBuilder {
	
	public static void buildBridge() throws Exception {
		Client client = Client.getSingleton();
		if(client.loadState == LoadState.ACTIVE) {
			if(Options.currentHeight.get() == 3) {
				throw new Exception("Invalid height");
			}
			int oldHeight = Options.tileHeightLevel.get();
			BitFlag oldFlag = Options.tileFlags.get();
			
			int lowerZ = Options.currentHeight.get();
			List<SceneTile> selectedTiles = client.sceneGraph.getSelectedTiles();
			List<SceneTile> tilesAbove = selectedTiles.stream().map(tile -> client.sceneGraph.tiles[tile.plane + 1][tile.positionX][tile.positionY]).collect(Collectors.toList());
			List<SceneTile> tilesAround = tilesAbove
					.stream()
					.flatMap(tile -> IntStream
										.rangeClosed(-1, 1)
										.boxed()
										.flatMap(x -> IntStream.rangeClosed(-1, 1).mapToObj(y -> client.sceneGraph.tiles[tile.plane][tile.positionX + x][tile.positionY + y])))
					.filter(tile -> !tilesAbove.contains(tile))
					.collect(Collectors.toList());
			int highestHeight = selectedTiles.stream().mapToInt(tile -> -client.mapRegion.tileHeights[lowerZ][tile.positionX][tile.positionY]).max().getAsInt();

			BitFlag bridgeFlag = new BitFlag();
			bridgeFlag.flag(RenderFlags.BRIDGE_TILE);
			
			Options.tileHeightLevel.set(highestHeight);
			Options.tileFlags.set(bridgeFlag);
			Options.overlayPaintShapeId.set(1);
			Options.overlayPaintId.set(1);
			
			System.out.println(tilesAround.size());
			System.out.println("Setting abs values to " + Options.tileHeightLevel.get());
			client.sceneGraph.setTileFlags(tilesAbove);
			client.sceneGraph.setTileHeights(tilesAbove, true);
			client.sceneGraph.setTileHeights(tilesAround, true);
			client.sceneGraph.setTileOverlays(tilesAbove);
 
			Options.tileHeightLevel.set(oldHeight);
			Options.tileFlags.set(oldFlag);
			
			client.sceneGraph.tileQueue.clear();
			
		}
	}

}
