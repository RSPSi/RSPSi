package com.rspsi.physics;

import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import org.joml.Vector3f;

public class BasicMotionState extends MotionState {

    private final Transform worldTransform;

    public BasicMotionState(Transform worldTransform) {
        this.worldTransform = worldTransform;
    }

    @Override
    public Transform getWorldTransform(Transform out) {
        out.set(worldTransform);
        return out;
    }

    @Override
    public void setWorldTransform(Transform worldTrans) {
        worldTransform.set(worldTrans);
    }
}
