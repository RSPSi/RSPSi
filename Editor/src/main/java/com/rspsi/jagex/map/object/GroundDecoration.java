package com.rspsi.jagex.map.object;

import com.rspsi.jagex.cache.graphics.Sprite;
import com.rspsi.jagex.util.ObjectKey;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public final class GroundDecoration extends DefaultWorldObject {

	public GroundDecoration(@Nonnull ObjectKey key, @Nonnull Vector3i worldPos, @Nullable Vector3f translate, @Nullable Quat4f rotation) {
		super(key, worldPos, translate, rotation);
	}

	@Override
	public TypeFilter getTypeFilter() {
		return TypeFilter.groundDecoration;
	}

	@Getter @Setter
	private Sprite minimapFunction = null;

}