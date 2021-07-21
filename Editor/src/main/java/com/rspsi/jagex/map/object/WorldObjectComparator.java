package com.rspsi.jagex.map.object;

import com.google.common.collect.ComparisonChain;
import com.rspsi.jagex.entity.Renderable;
import com.rspsi.jagex.entity.model.Mesh;

import java.util.Comparator;
import java.util.Objects;

public class WorldObjectComparator implements Comparator<DefaultWorldObject> {
    private static Comparator<Renderable> nullSafeComparator = Comparator
            .nullsFirst(Renderable::compareTo);//this is broken

    @Override
    public int compare(DefaultWorldObject o1, DefaultWorldObject o2) {
        return ComparisonChain.start()
                .compare(o1.primary, o2.primary, nullSafeComparator)
                .compare(o1.secondary, o2.secondary, nullSafeComparator)
                .result();


    }
}
