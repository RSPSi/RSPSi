package com.rspsi.jagex.map.object;

import com.rspsi.jagex.util.ObjectKey;
import org.joml.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class GameObject extends DefaultWorldObject {

	public int anInt527;

	public int lastRenderCycle;

	public GameObject(@Nonnull ObjectKey key, @Nonnull Vector3i worldPos, @Nullable Vector3f translate, @Nullable Quat4f rotation) {
		super(key, worldPos, translate, rotation);
	}


	@Override
	public TypeFilter getTypeFilter() {
		return TypeFilter.genericAndRoof;
	}

}