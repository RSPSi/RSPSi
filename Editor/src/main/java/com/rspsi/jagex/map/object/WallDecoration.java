package com.rspsi.jagex.map.object;

import com.rspsi.jagex.util.ObjectKey;
import org.joml.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public final class WallDecoration extends DefaultWorldObject {

	private int decorData;

	private int orientation;

	public WallDecoration(@Nonnull ObjectKey key, @Nonnull Vector3i worldPos, @Nullable Vector3f translate, @Nullable Quat4f rotation) {
		super(key, worldPos, translate, rotation);
	}


	public int getDecorData() {
		return decorData;
	}

	@Override
	public int getOrientation() {
		return orientation;
	}

	@Override
	public TypeFilter getTypeFilter() {
		return TypeFilter.wallDecoration;
	}

	public void setDecorData(int decorData) {
		this.decorData = decorData;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
}