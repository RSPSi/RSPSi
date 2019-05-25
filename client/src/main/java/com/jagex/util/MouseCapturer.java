package com.jagex.util;

import com.jagex.Client;

public class MouseCapturer implements Runnable {

	private int capturedCoordinateCount;
	private Client client;
	private int[] coordinatesX = new int[500];
	private int[] coordinatesY = new int[500];
	private boolean running = true;
	private Object synchronizedObject = new Object();

	public MouseCapturer(Client client) {
		this.client = client;
	}

	public int getCapturedCoordinateCount() {
		return capturedCoordinateCount;
	}

	public int[] getCoordinatesX() {
		return coordinatesX;
	}

	public int[] getCoordinatesY() {
		return coordinatesY;
	}

	public Object getSynchronizedObject() {
		return synchronizedObject;
	}

	public boolean isRunning() {
		return running;
	}

	@Override
	public void run() {
		while (running) {
			synchronized (synchronizedObject) {
				if (capturedCoordinateCount < 500) {
					coordinatesX[capturedCoordinateCount] = client.getMouseEventX();
					coordinatesY[capturedCoordinateCount] = client.getMouseEventY();
					capturedCoordinateCount++;
				}
			}

			try {
				Thread.sleep(50L);
			} catch (Exception ex) {
			}
		}
	}

	public void setCapturedCoordinateCount(int capturedCoordinateCount) {
		this.capturedCoordinateCount = capturedCoordinateCount;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}