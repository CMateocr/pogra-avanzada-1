package com.prograavanzada.ilumination;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];
	private final float[] mVPMatrix = new float[16];
	private final float[] mModelMatrix = new float[16];
	private float angle = 0.0f;
	private Piramide piramide;

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		piramide = new Piramide();
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST); // Habilita el test de profundidad!

		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		Matrix.setLookAtM(
						mViewMatrix, 0,
						0f, 0f, 5f,    // Cámara detrás en Z
						0.0f, 0f, 0.0f, // Mira al centro
						0.0f, 1.0f, 0.0f
		);

		Matrix.setIdentityM(mModelMatrix, 0); //identidad, matriz sin transformacion// estamos como seteando desde cero
		Matrix.translateM(mModelMatrix, 0, 0, 0, 1); // La pirámide se aleja en Z (entra al frustum)
		Matrix.rotateM(mModelMatrix, 0, angle, 1, 0, 0); // Rotación alrededor del eje Y

		angle += 0.5f; // Incrementa el ángulo de rotación

		float[] mvMatrix = new float[16];
		Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, mModelMatrix, 0); //el resultado se almacena en mvMatrix
		Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mvMatrix,0);

		piramide.draw(mVPMatrix);
	}
	public static int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		int[] compileStatus = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

		if (compileStatus[0] == 0) {
			String error = GLES20.glGetShaderInfoLog(shader);
			GLES20.glDeleteShader(shader);
			throw new RuntimeException("Error al compilar el shader: " + error);
		}

		return shader;
	}
}