package com.progravanzada.transformaciones;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

	private Piramide piramide;

	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];
	private final float[] mVPMatrix = new float[16];
	private final float[] mModelMatrix = new float[16];
	private float angle = 0.0f;

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		GLES20.glClearColor(0f, 0f, 0f, 1f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// Inicializa la pirámide para dibujar
		piramide = new Piramide();
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);

	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Configura la cámara con setLookAtM
		Matrix.setLookAtM(
						mViewMatrix, 0,
						0.0f, 2.0f, -8f,    // Posición de la cámara (elevada sobre el objeto)
						0.0f, 0.0f, 0.0f,   // Punto al que mira (el centro del prisma)
						0.0f, 1.0f, 0.0f    // Vector "arriba" (eje Y)
		);

		Matrix.setIdentityM(mModelMatrix, 0);

		// Traslación

		Matrix.translateM(mModelMatrix, 0, 2, 3, -1);

		float[] mvMatrix = new float[16];

		Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

		Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mvMatrix, 0);

		piramide.draw(mVPMatrix);
	}

	public static int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		return shader;
	}
}
