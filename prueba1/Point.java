package com.progavanzada.prueba1;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

// ! opengl trabaja solo con identificadores
public class Point {

	private final FloatBuffer vertexBuffer;
	private final int mProgram;
	private int positionHandle;
	private int colorHandle;
	static final int COORDS_PER_VERTEX = 3;
	static float pointCoord[] = {-0.5f, -0.5f, 0.0f};
	private final int vertexCount = pointCoord.length/COORDS_PER_VERTEX;
	private final int vertexStride = COORDS_PER_VERTEX*4; // ? cantidad de vertices que hay entre el inicio y el final
	float color[] = {1.0f, 0.0f, 0.0f, 1.0f};

	public Point() {

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(pointCoord.length*4);

		byteBuffer.order(ByteOrder.nativeOrder());

		vertexBuffer = byteBuffer.asFloatBuffer();

		vertexBuffer.put(pointCoord);

		vertexBuffer.position(0);

		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);

		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram();

		GLES20.glAttachShader(mProgram, vertexShader);

		GLES20.glAttachShader(mProgram, fragmentShader);

		GLES20.glLinkProgram(mProgram);
	}

	public void draw() {
		GLES20.glUseProgram(mProgram);

		positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		GLES20.glEnableVertexAttribArray(positionHandle);

		GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		GLES20.glUniform4fv(colorHandle, 1, color, 0);

		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);

		GLES20.glDisableVertexAttribArray(positionHandle);
	}
	private final String vertexShaderCode =
					"attribute vec4 vPosition;" +
						"void main() {" +
						"gl_Position = vPosition;" +
						"gl_PointSize = 70.0;" +
					"}";
	private final String fragmentShaderCode =
					"precision mediump float;" +
					"uniform vec4 vColor;" +
					"void main() {" +
						"gl_FragColor = vColor;" +
					"}";

	private int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);

		GLES20.glShaderSource(shader, shaderCode);

		GLES20.glCompileShader(shader);

		return shader;
	}

}
