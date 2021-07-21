package com.rspsi.misc;

import java.util.Arrays;

public class ArrayUtil {

    public static int[] copyArray(int[] a) {
        if (a == null)
            return null;
        return Arrays.copyOf(a, a.length);
    }

    public static byte[] copyArray(byte[] a) {
        if (a == null)
            return null;
        return Arrays.copyOf(a, a.length);
    }
    public static boolean[] copyArray(boolean[] a) {
        if (a == null)
            return null;
        return Arrays.copyOf(a, a.length);
    }

    public static short[] copyArray(short[] a) {
        if (a == null)
            return null;
        return Arrays.copyOf(a, a.length);
    }

    public static <T> T[] copyArray(T[] a) {
        if (a == null)
            return null;
        return Arrays.copyOf(a, a.length);
    }



    public static int[][] copyArray(int[][] a) {
        if (a == null)
            return null;
        return Arrays.copyOf(a, a.length);
    }
}
