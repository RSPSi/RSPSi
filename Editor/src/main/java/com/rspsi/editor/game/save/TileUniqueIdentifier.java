package com.rspsi.editor.game.save;
import lombok.Data;
import org.joml.Vector3i;

import javax.vecmath.Vector3f;

@Data
public class TileUniqueIdentifier {
	
	private final Vector3i position;
	private final long key;

}
