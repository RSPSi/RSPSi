package com.rspsi.ui;

import com.google.gson.GsonBuilder;
import com.jagex.map.tile.ShapedTile;

import com.rspsi.core.misc.XTEA;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import org.apache.commons.compress.utils.Lists;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class Testing {

	public static void main(String[] args) throws Exception{
		File folder = new File("F:\\61xteasunpakced\\unpacked\\");
		List<XTEA> xteas = Lists.newArrayList();
		for(File file : folder.listFiles()) {
			if(file.getName().toLowerCase().endsWith(".txt")) {
				int regionId = Integer.parseInt(file.getName().toLowerCase().replace(".txt", "").trim());
				int[] keys = Files.readAllLines(file.toPath()).stream().filter(Objects::nonNull).filter(s -> !s.isEmpty()).mapToInt(Integer::parseInt).toArray();
				xteas.add(new XTEA(regionId, keys));
			}
		}
		try(FileWriter fw = new FileWriter("F:/614xteas.json")) {
			new GsonBuilder().setPrettyPrinting().create().toJson(xteas, fw);
		}
	}

	public static Pane generateImage(int type) {
		ShapedTile tile = new ShapedTile(type, 0);
		Pane group = new Pane();
		group.setMaxSize(32, 32);
		group.setMinSize(32, 32);

		Rectangle r = new Rectangle();
		r.setFill(Paint.valueOf("#FFFFFF"));
		r.setWidth(32);
		r.setHeight(32);

		group.getChildren().add(r);

        int[] ai3 = ShapedTile.shapedTileElementData[tile.getTileType()];
		int triangleCount = tile.getTriangleA().length;
		int offset = 0;
		for (int triangleIndex = 0; triangleIndex < triangleCount; triangleIndex++) {

			if (ai3[offset] == 0) {// Underlay
				int indexA = tile.getTriangleA()[triangleIndex];
				int indexB = tile.getTriangleB()[triangleIndex];
				int indexC = tile.getTriangleC()[triangleIndex];

				double[] points = { tile.getOrigVertexX()[indexA] / 4, tile.getOrigVertexZ()[indexA] / 4,
						tile.getOrigVertexX()[indexB] / 4, tile.getOrigVertexZ()[indexB] / 4,
						tile.getOrigVertexX()[indexC] / 4, tile.getOrigVertexZ()[indexC] / 4 };

				Polygon p = new Polygon(points);
				Color c = Color.BLACK;
				p.setFill(c);
				p.setLayoutX(0);
				p.setLayoutY(0);
				p.setSmooth(true);
				p.setStroke(c);
				p.setStrokeWidth(1);
				group.getChildren().add(p);
			} else {// Underlay
			}
			offset += 4;
		}
		return group;
	}

}
