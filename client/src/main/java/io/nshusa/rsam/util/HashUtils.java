package io.nshusa.rsam.util;

public final class HashUtils {

    private HashUtils() {

    }

    public static int nameToHash(String name) {
        int hash = 0;
        name = name.toUpperCase();
        for (int i = 0; i < name.length(); i++) {
            hash = (hash * 61 + name.charAt(i)) - 32;
        }
        return hash;
    }

    public static long hashSpriteName(String name) {
        name = name.toUpperCase();
        long hash = 0;
        for (int index = 0; index < name.length(); index++) {
            hash = hash * 61 + name.charAt(index) - 32;
            hash = hash + (hash >> 56) & 0xFFFFFFFFFFFFFFL;
        }

        return hash;
    }

}
