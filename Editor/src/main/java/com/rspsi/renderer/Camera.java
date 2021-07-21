package com.rspsi.renderer;

import com.jogamp.nativewindow.util.Rectangle;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.math.Quaternion;
import com.rspsi.jagex.Client;
import com.rspsi.util.ChangeListenerUtil;
import javafx.geometry.Bounds;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.function.Consumer;

@Data
@Slf4j
public class Camera  {


	private int centerX, centerY;
	public Vector3i position = new Vector3i(0, 0, 0);
	public Vector3f direction = new Vector3f(0f, 0f, 0f);

	private Matrix4f viewMatrix;
	private Matrix4f modelMatrix;

	public Matrix4f combined() {
		return new Matrix4f(viewMatrix).mulAffine(modelMatrix);
	}
	private Quaternion rotation = new Quaternion();

	private Rectangle viewport;


	public void setup(NewtCanvasJFX glCanvas) {
		Consumer<Bounds> boundsToViewport = bounds -> {
			log.info("Bounds {}", bounds);
			int width = (int) (bounds.getMaxX() - bounds.getMinX());
			int height = (int) (bounds.getMaxY() - bounds.getMinY());
			Client.getSingleton().canvasWidth = width;
			Client.getSingleton().canvasHeight = height;
			viewport = new Rectangle((int) bounds.getMinX(), (int) bounds.getMinY(), width, height);
			log.info("Set viewport to {}", viewport);
			centerX = width / 2;
			centerY = height / 2;
		};

		viewport = new Rectangle(0, 0, (int) glCanvas.getWidth(), (int) glCanvas.getHeight());
		boundsToViewport.accept(glCanvas.getBoundsInLocal());
		ChangeListenerUtil.addListener(boundsToViewport, glCanvas.boundsInLocalProperty());
	}


	//TODO Remove this
	public void temporaryBind(Client client){
		//viewMatrix.perspective((float) Math.toRadians(45.0f), 1.0f, 1.0f, 100.0f);
		position.set(client.xCameraPos, client.yCameraPos,  client.zCameraPos);
		//rotation.setFromEuler(client.cameraRoll / 512, client.cameraYaw, client.cameraRotationZ);


	}

	public Vector3i getLookPosition() {
		return position;
	}
}
