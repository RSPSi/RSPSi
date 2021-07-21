package net.runelite.gpu.util;

import lombok.Getter;
import lombok.Setter;
import net.runelite.gpu.GpuFloatBuffer;
import net.runelite.gpu.GpuIntBuffer;

@Getter
@Setter
public class ModelBuffers {
    public static final int FLAG_SCENE_BUFFER = Integer.MIN_VALUE;
    public static final int MAX_TRIANGLE = 4096;
    public static final int SMALL_TRIANGLE_COUNT = 512;

    public ModelBuffers() {
        modelBufferUnordered = new GpuIntBuffer();
        modelBufferSmall = new GpuIntBuffer();
        modelBuffer = new GpuIntBuffer();
        vertexBuffer = new GpuIntBuffer();
        uvBuffer = new GpuFloatBuffer();
    }

    public void clearVertUv() {
        vertexBuffer.clear();
        uvBuffer.clear();
    }

    public void flipVertUv() {
        vertexBuffer.flip();
        uvBuffer.flip();
    }

    public void clear() {
        modelBuffer.clear();
        modelBufferSmall.clear();
        modelBufferUnordered.clear();
        smallModels = largeModels = unorderedModels = 0;
        tempOffset = 0;
        tempUvOffset = 0;
    }

    public void flip() {
        modelBuffer.flip();
        modelBufferSmall.flip();
        modelBufferUnordered.flip();
    }

    public GpuIntBuffer bufferForTriangles(int triangles) {
        if (triangles < SMALL_TRIANGLE_COUNT) {
            ++smallModels;
            return modelBufferSmall;
        } else {
            ++largeModels;
            return modelBuffer;
        }
    }

    private GpuIntBuffer vertexBuffer;
    private GpuFloatBuffer uvBuffer;

    private GpuIntBuffer modelBufferUnordered;
    private GpuIntBuffer modelBufferSmall;
    private GpuIntBuffer modelBuffer;

    private int unorderedModels;

    public void incUnorderedModels() {
        unorderedModels++;
    }

    /**
     * number of models in small buffer
     */
    private int smallModels;

    public void incSmallModels() {
        smallModels++;
    }

    /**
     * number of models in large buffer
     */
    private int largeModels;

    public void incLargeModels() {
        largeModels++;
    }

    /**
     * offset in the target buffer for model
     */
    private int targetBufferOffset;

    public void addTargetBufferOffset(int n) {
        targetBufferOffset += n;
    }

    /**
     * offset into the temporary scene vertex buffer
     */
    private int tempOffset;

    public void addTempOffset(int n) {
        tempOffset += n;
    }

    /**
     * offset into the temporary scene uv buffer
     */
    private int tempUvOffset;

    public int calcPickerId(int x, int y, int type) {
        // pack x tile in top 14 bits, y in next 14, objectId in bottom 4
        // NOTE: signed int so x can really only use 13 bits!!
        return ((x & 0xFFF) << 20) | ((y & 0xFFF) << 4) | type & 0xF;
    }
}
