package com.progavanzada.prueba1;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Line {
	private final FloatBuffer vertexBuffer;
	private final int mProgram;
	private int positionHandle;
	private int colorHandle;
	private final float[] lineCoords;

	float[] color = {1.0f, 1.0f, 0.0f, 1.0f};

	// ! generar una malla de puntos coonstruir un metodo en elq ue pueda ingresar un numero de puntos que va a tener la malla fila por columna
	public Line(float[] coords) {
		this.lineCoords = coords;
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(lineCoords.length*4);

		byteBuffer.order(ByteOrder.nativeOrder());

		vertexBuffer = byteBuffer.asFloatBuffer();

		vertexBuffer.put(lineCoords);

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

		GLES20.glEnableVertexAttribArray(positionHandle);

		GLES20.glVertexAttribPointer(
						positionHandle,
						2,
						GLES20.GL_FLOAT,
						false,
						0,
						vertexBuffer
		);

		colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		GLES20.glUniform4fv(colorHandle, 1, color, 0);

		GLES20.glLineWidth(10.0f);

		GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);

		GLES20.glDisableVertexAttribArray(positionHandle);
	}
	private final String vertexShaderCode =
					"attribute vec4 vPosition;" +
									"void main() {" +
									"gl_Position = vPosition;" +
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
