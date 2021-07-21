package com.rspsi.physics;

import com.bulletphysics.collision.dispatch.CollisionWorld;

import javax.vecmath.Vector3f;

public abstract class DefaultRayResult extends CollisionWorld.ClosestRayResultCallback {

    public DefaultRayResult(Vector3f rayFromWorld, Vector3f rayToWorld) {
        super(rayFromWorld, rayToWorld);
    }



}
