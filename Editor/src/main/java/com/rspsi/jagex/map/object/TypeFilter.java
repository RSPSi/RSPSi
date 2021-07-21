package com.rspsi.jagex.map.object;

import kotlin.ranges.IntRange;
import org.apache.commons.math3.util.IntegerSequence;

import java.util.stream.Stream;

import static org.apache.commons.math3.util.IntegerSequence.Range;

public class TypeFilter {

    public static TypeFilter genericAndRoof = new TypeFilter(new IntRange(10, 21));//this name is stupid
    public static TypeFilter wallObjects = new TypeFilter(new IntRange(0, 3), new IntRange(9, 9));
    public static TypeFilter wallDecoration = new TypeFilter(new IntRange(4, 4));
    public static TypeFilter roofObjects = new TypeFilter(new IntRange(12, 21));
    public static TypeFilter groundDecoration = new TypeFilter(22);
    public static TypeFilter noFilter = new TypeFilter(new IntRange(0, 22));

    public IntRange[] validTypes;

    public TypeFilter(int type) {
        this.validTypes = new IntRange[] { new IntRange(type, type) };
    }
    public TypeFilter(IntRange... validTypes) {
        this.validTypes = validTypes;
    }

    public boolean contains(int type) {
        return Stream.of(validTypes).anyMatch(typeRange -> typeRange.contains(type));
    }
}
