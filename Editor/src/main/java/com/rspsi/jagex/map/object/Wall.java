package com.rspsi.jagex.map.object;

import com.rspsi.jagex.util.ObjectKey;
import org.joml.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public final class Wall extends DefaultWorldObject {

	public int anInt276;

	public int anInt277;

	public Wall(@Nonnull ObjectKey key, @Nonnull Vector3i worldPos, @Nullable Vector3f translate, @Nullable Quat4f rotation) {
		super(key, worldPos, translate, rotation);
	}

	@Override
	public TypeFilter getTypeFilter() {
		return TypeFilter.wallObjects;
	}


}