package com.rspsi.jagex.map.tile;

import com.rspsi.jagex.util.Orientation;

public class TileUtils {

    public static final SimpleTile NON_WALKABLE = new SimpleTile(64183, -1, 0, 64183, 64183, 64183, 64183, true);
    public static final SimpleTile NON_WALKABLE_OTHER_HEIGHT = new SimpleTile(54183, -1, 54183, 54183, 54183, 54183, 54183, true);
    public static final SimpleTile SELECTED_TILE = new SimpleTile(0x0c3c69, -1, 0, 0x0c3c69, 0x0c3c69, 0x0c3c69, 0x0c3c69, true);
    public static final SimpleTile BEING_SELECTED_TILE = new SimpleTile(49073, -1, 0, 49073, 49073, 49073, 49073, true);
    public static final SimpleTile HIGHLIGHT_TILE = new SimpleTile(0xFF00FF, -1, 0, 0, 0, 0, 0, true);
    public static final SimpleTile HIDDEN_TILE = new SimpleTile(0x00ffff, -1, 0, 0x00ffff, 0x00ffff, 0x00ffff, 0x00ffff, true);


    public static final int NON_WALKABLE_BIT = 1;

    public static final int VISIBLE_ON_FLOOR_BELOW_BIT = 2;

    public static final int REMOVE_ROOF_BIT = 4;

    public static final int DONT_RENDER_BIT = 8;

    public static int getObjectXOffset(int x, int y, int width, int length, int orientation) {
        orientation &= 3;
        if (orientation == Orientation.NORTH)
            return y;
        else if (orientation == Orientation.EAST)
            return x;
        else if (orientation == Orientation.SOUTH)
            return 7 - y - (width - 1);

        return 7 - x - (length - 1);
    }

    public static int getObjectYOffset(int x, int y, int width, int length, int rotation) {
        rotation &= 3;
        if (rotation == Orientation.NORTH)
            return x;
        else if (rotation == Orientation.EAST)
            return 7 - y - (width - 1);
        else if (rotation == Orientation.SOUTH)
            return 7 - x - (length - 1);

        return y;
    }

    public static int getXOffset(int x, int y, int orientation) {
        orientation &= 3;
        if (orientation == Orientation.NORTH)
            return x;
        else if (orientation == Orientation.EAST)
            return y;
        else if (orientation == Orientation.SOUTH)
            return 7 - x;

        return 7 - y;
    }

    public static int getYOffset(int x, int y, int orientation) {
        orientation &= 3;
        if (orientation == Orientation.NORTH)
            return y;
        else if (orientation == Orientation.EAST)
            return 7 - x;
        else if (orientation == Orientation.SOUTH)
            return 7 - y;

        return x;
    }

}