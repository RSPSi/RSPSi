package com.rspsi.jagex.map.object;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rspsi.jagex.cache.loader.map.MapIndexLoader;
import com.rspsi.jagex.entity.Renderable;
import com.rspsi.jagex.entity.model.Mesh;
import com.rspsi.jagex.entity.model.ModelInstance;
import com.rspsi.jagex.util.ObjectKey;
import com.rspsi.misc.Vector3;
import com.rspsi.physics.BasicMotionState;
import com.rspsi.physics.RigidBodyEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import net.runelite.gpu.GpuIntBuffer;
import net.runelite.gpu.util.ModelBuffers;
import org.joml.Vector3i;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static net.runelite.gpu.util.ModelBuffers.FLAG_SCENE_BUFFER;

@ToString
@Slf4j

public abstract class DefaultWorldObject implements WorldObject, RigidBodyEntity {

    public Vector3i worldPos;

    @JsonIgnore
    public boolean temporary;

    @JsonIgnore
    public Transform worldTransform = new Transform();

    public Vector3i center;
    public Vector3i minimum;
    public Vector3i maximum;
    public int yaw;
    public int attributes;

    public int primaryCollusion, secondaryCollusion;

    @JsonIgnore
    public boolean uploaded;

    @JsonIgnore
    public ObjectKey key;

    @JsonIgnore
    public boolean selected;

    @JsonIgnore
    public RigidBody rigidBody;

    @JsonIgnore
    public Renderable primary;

    @JsonIgnore
    public Renderable secondary;

    public DefaultWorldObject(ObjectKey key, Vector3i worldPos, Vector3f translate, Quat4f rotation) {
        this.key = key;
        this.worldPos = worldPos;
        this.center = worldPos;
        this.minimum = worldPos;
        this.maximum = worldPos;
        worldTransform.setIdentity();
        worldTransform.origin.set(new Vector3f(worldPos.x, worldPos.y, worldPos.z));
        worldTransform.setRotation(rotation);
        worldTransform.transform(translate);
    }

    public int getConfig() {
        int rotation = key.getOrientation();
        int type = key.getType();
        return type << 2 | rotation;
    }

    public int getId() {
        return key.getId();
    }

    @Override
    public ObjectKey getKey() {
        return key;
    }

    @JsonIgnore
    public int getLocHash() {

        int y = key.getY() & 63;
        int x = key.getX() & 63;
        return key.getZ() << 12 | x << 6 | y;
    }


    public abstract TypeFilter getTypeFilter();


    public void setSelected(boolean selected) {
        if (primary != null) {
            primary = primary.copy();
            primary.selected = selected;
        }
        if (secondary != null) {
            secondary = secondary.copy();
            secondary.selected = selected;
        }
        this.selected = selected;
    }

    @Override
    public RigidBody buildRigidBody() {
        var triangleIndexVertexArray = new TriangleIndexVertexArray();
        for(ModelInstance modelInstance: new ModelInstance[] {(ModelInstance) primary, (ModelInstance) secondary}) {
            var indexedMesh = new IndexedMesh();
            indexedMesh.numVertices = modelInstance.numVertices;
            indexedMesh.numTriangles = modelInstance.numFaces;
            indexedMesh.vertexBase = modelInstance.vertexBuffer.toByteBuffer();
            indexedMesh.triangleIndexBase = modelInstance.triangleBuffer.toByteBuffer();
            indexedMesh.vertexStride = Integer.BYTES * 3;
            triangleIndexVertexArray.addIndexedMesh(indexedMesh);
        }
        var collisionShape = new BvhTriangleMeshShape(triangleIndexVertexArray, true);
        var constructionInfo = new RigidBodyConstructionInfo(1.0f, new DefaultMotionState(worldTransform), collisionShape);
        this.rigidBody = new RigidBody(constructionInfo);


        return rigidBody;
    }

    public void draw(ModelBuffers modelBuffers, int x, int y, int z) {
        if (uploaded)
            return;
        uploaded = true;

        if (primary instanceof ModelInstance) {
            //log.info("Mesh rendere");
            int orientation = getOrientation();
            if (key.getType() == 2) {
                orientation += 4;
            }
            uploadMeshInfo(modelBuffers, x, y, z, orientation, (ModelInstance) primary);
        }
        if (secondary instanceof ModelInstance) {
            log.info("Drawing secondary mesh!");
            int orientation = getOrientation();
            if (key.getType() == 2) {
                orientation = orientation + 1 & 3;
            }
            uploadMeshInfo(modelBuffers, x, y, z, orientation, (ModelInstance) secondary);
        }

    }

    public void uploadMeshInfo(ModelBuffers modelBuffers, int x, int y, int z, int orientation, ModelInstance mesh) {

        GpuIntBuffer b = modelBuffers.bufferForTriangles(mesh.numFaces);

        b.ensureCapacity(8);
        IntBuffer buffer = b.getBuffer();
        buffer.put(mesh.getBufferOffset());
        buffer.put(mesh.getUvBufferOffset());
        buffer.put(mesh.numFaces);
        buffer.put(modelBuffers.getTargetBufferOffset());

        buffer.put(FLAG_SCENE_BUFFER | (mesh.getHeight() << 12) | orientation);
        buffer.put(x).put(y).put(z);

        modelBuffers.addTargetBufferOffset(mesh.numFaces * 3);
    }

    public int getOrientation() {
        return getKey().getOrientation();
    }

    @Override
    public int getPlane() {
        return worldPos.z;
    }

    @Override
    public int getRenderHeight() {
        return (int) worldTransform.getMatrix(new Matrix4f()).m23;
    }

    @Override
    public int getX() {
        return worldPos.x;
    }

    @Override
    public int getY() {
        return worldPos.y;
    }

    @Override
    public RigidBody getRigidBody() {
        return rigidBody;
    }

    public int getDecorData() {
        return attributes;
    }
}

