package com.rspsi.core.misc;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Camera {
	
	private Vector3D position;
	private float pitch, yaw;
	private float velocity;
	
	
	public Vector3D getPosition() {
		return position;
	}
	
	public void setPosition(Vector3D position) {
		this.position = position;
	}
	
	public float getPitch() {
		return pitch;
	}
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	public float getYaw() {
		return yaw;
	}
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	
	
	
}
