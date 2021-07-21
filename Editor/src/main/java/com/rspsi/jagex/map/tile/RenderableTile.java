package com.rspsi.jagex.map.tile;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDrawable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.gpu.util.ModelBuffers;
import org.joml.Vector3i;

import java.util.ArrayList;

@RequiredArgsConstructor
@Getter
public abstract class RenderableTile {

    private final Vector3i position;

    protected ArrayList<Vector3i> vertices;
    protected ArrayList<Vector3i> faces;
    protected ArrayList<Vector3i> faceColours;

    /**
     * Each index represents a single face in the <code>getFaces()</code> array
     */
    protected int[] textureIds;

}
