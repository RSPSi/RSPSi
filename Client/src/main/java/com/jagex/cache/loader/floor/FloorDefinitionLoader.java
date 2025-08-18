package com.jagex.cache.loader.floor;

import com.displee.cache.index.archive.Archive;
import com.jagex.cache.def.Floor;
import com.jagex.cache.loader.DataLoaderBase;

public abstract class FloorDefinitionLoader implements DataLoaderBase<Floor> {

    public static FloorDefinitionLoader instance;

    public static Floor getOverlay(final int id) {
        return instance.getFloor(id, FloorType.OVERLAY);
    }

    public static Floor getUnderlay(final int id) {
        return instance.getFloor(id, FloorType.UNDERLAY);
    }

    public static int getUnderlayCount() {
        return instance.getSize(FloorType.UNDERLAY);
    }

    public static int getOverlayCount() {
        return instance.getSize(FloorType.OVERLAY);
    }

    public static int getHighestUnderlayId() {
        return instance.getHighestId(FloorType.UNDERLAY);
    }

    public static int getHighestOverlayId() {
        return instance.getHighestId(FloorType.OVERLAY);
    }

    @Override
    public void init(final Archive archive) {
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Floor forId(final int id) {
        return null;
    }

    public abstract Floor getFloor(final int id, final FloorType type);

    public abstract int getSize(final FloorType type);

    public abstract int getHighestId(final FloorType type);

}
