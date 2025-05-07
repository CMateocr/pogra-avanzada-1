package com.progavanzada.primitivas2025;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {
	private FloatBuffer vertexBuffer;
	private int mProgram;
	private int positionHandle;
	private int colorHandle;
	private static final int COORDS_PER_VERTEX = 3;
	private final float[] squareCoords = {
					-0.5f, 0.8f, 0.0f,  // top left
					-0.5f, 0.2f, 0.0f,  // bottom left
					0.1f, 0.2f, 0.0f,  // bottom right
					0.1f, 0.8f, 0.0f   // top right
	};

	private final float[] color = {0.0f, 1.0f, 0.0f, 1.0f}; // green color
	private final short[] drawOrder = {0,1,2,0,2,3}; // ! order to draw vertices

	private final ShortBuffer shortBuffer;

	public Square() {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(squareCoords.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuffer.asFloatBuffer();
		vertexBuffer.put(squareCoords);
		vertexBuffer.position(0);

		ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(drawOrder.length * 2);
		byteBuffer2.order(ByteOrder.nativeOrder());
		shortBuffer = byteBuffer2.asShortBuffer();
		shortBuffer.put(drawOrder);
		shortBuffer.position(0);

		int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(mProgram, vertexShader);
		GLES20.glAttachShader(mProgram, fragmentShader);
		GLES20.glLinkProgram(mProgram);
	}

	private final String vertexShaderCode =
			"attribute vec4 vPosition;" +
					"void main() {" +
					"  gl_Position = vPosition;" +
					"}";
	private final String fragmentShaderCode =
			"precision mediump float;" +
					"uniform vec4 vColor;" +
					"void main() {" +
					"  gl_FragColor = vColor;" +
					"}";

	public void draw() {
		GLES20.glUseProgram(mProgram);

		positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(
				positionHandle,
				COORDS_PER_VERTEX,
				GLES20.GL_FLOAT,
				false,
				COORDS_PER_VERTEX * 4,
				vertexBuffer
		);

		colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		GLES20.glUniform4fv(colorHandle, 1, color, 0);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, shortBuffer);

		GLES20.glDisableVertexAttribArray(positionHandle);
	}
}