package com.rspsi.physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import lombok.var;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.stream.Collectors;

public class WorldPhysics {

    public CollisionConfiguration collisionConfiguration;
    public CollisionDispatcher collisionDispatcher;
    public BroadphaseInterface broadphaseInterface;
    public ConstraintSolver constraintSolver;
    public DynamicsWorld dynamicsWorld;

    public void init() {
        collisionConfiguration = new DefaultCollisionConfiguration();
        collisionDispatcher = new CollisionDispatcher(collisionConfiguration);
        broadphaseInterface = new DbvtBroadphase();
        constraintSolver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(collisionDispatcher, broadphaseInterface, constraintSolver, collisionConfiguration);
    }


    public void setGravity(Vector3f gravity) {
        dynamicsWorld.setGravity(new javax.vecmath.Vector3f(gravity.x, gravity.y, gravity.z));
    }

    public void tick() {
        dynamicsWorld.stepSimulation(1.f / 60f, 10);
        var collisionObjects = dynamicsWorld.getCollisionObjectArray().parallelStream()
                .map(cO -> {
                    if (cO instanceof RigidBody)
                        return (RigidBody) cO;
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        for (RigidBody rigidBody : collisionObjects) {
            var transform = new Transform();
            if(rigidBody.getMotionState() != null) {
                rigidBody.getMotionState().getWorldTransform(transform);
            } else {
                rigidBody.getWorldTransform(transform);
            }

        }

    }
}
