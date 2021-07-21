package com.rspsi.editor.tools;

import com.google.common.primitives.UnsignedBytes;
import com.rspsi.misc.BrushType;
import com.rspsi.options.Options;
import org.joml.Vector3i;

import java.util.function.BiConsumer;

public class BrushGroup implements BrushLayer {

    public BrushLayer[] layers;
    public int radius, diameter;

    private int[] combined;

    @Override
    public int[] getAlpha() {
        return combined;
    }

    @Override
    public void resize(int radius) {
        this.radius = radius;
        this.diameter = 2 * radius;
        combined = new int[diameter * diameter];
        for (BrushLayer layer : layers) {
            layer.resize(radius);
            int[] layerAlpha = layer.getAlpha();
            for (int x = 0; x < diameter; x++) {
                for (int y = 0; y < diameter; y++) {
                    int mergedPixel = combined[x + (y * diameter)] + layerAlpha[x + (y * diameter)];
                    if(mergedPixel > 0xFF){
                        mergedPixel = 0xFF;
                    } else if(mergedPixel < 0){
                        mergedPixel = 0;
                    }

                    combined[x + (y * diameter)] = mergedPixel;
                }
            }
        }


    }


}
