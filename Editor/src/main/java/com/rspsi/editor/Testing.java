package com.rspsi.editor;

import com.rspsi.jagex.cache.loader.floor.FloorType;
import com.rspsi.jagex.map.tile.ShapedTile;
import com.rspsi.jagex.map.tile.TileShape;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import org.joml.Vector3i;

import java.io.IOException;
import java.util.ArrayList;

public class Testing {

	public static void main(String[] args) throws IOException {

	}

	public static Pane generateImage(int type) {
		ShapedTile tile = new ShapedTile(type, 0, -1);
		Pane group = new Pane();
		group.setMaxSize(32, 32);
		group.setMinSize(32, 32);

		Rectangle r = new Rectangle();
		r.setFill(Paint.valueOf("#FFFFFF"));
		r.setWidth(32);
		r.setHeight(32);

		group.getChildren().add(r);

		ArrayList<Vector3i> vertices = tile.getVertices();
        Object[] ai3 = TileShape.shapedTileElementData2[tile.getTileType()];
		int triangleCount = tile.getFaces().size();
		int offset = 0;
		for (int triangleIndex = 0; triangleIndex < triangleCount; triangleIndex++) {

			if (ai3[offset] == FloorType.OVERLAY) {// Underlay
				Vector3i face = tile.getFaces().get(triangleIndex);

				double[] points = {
						vertices.get(face.x).x / 4.0, vertices.get(face.x).y / 4.0,
						vertices.get(face.y).x / 4.0, vertices.get(face.y).y / 4.0,
						vertices.get(face.z).x / 4.0, vertices.get(face.z).y / 4.0
				};

				Polygon p = new Polygon(points);
				Color c = Color.BLACK;
				p.setFill(c);
				p.setLayoutX(0);
				p.setLayoutY(0);
				p.setSmooth(true);
				p.setStroke(c);
				p.setStrokeWidth(1);
				group.getChildren().add(p);
			}
			offset += 4;
		}
		return group;
	}

}
