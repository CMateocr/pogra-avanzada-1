package com.progavanzada.primitivas2025;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
	private FloatBuffer vertexBuffer;
	private int mProgram;
	private int positionHandle;
	private int colorHandle;
	private static final int COORDS_PER_VERTEX = 3;
	private static final float[] triangleCoords = {
			0.0f, -0.25f, 0.0f,  // top
			-0.5f, -0.75f, 0.0f, // bottom left
			0.5f, -0.75f, 0.0f   // bottom right
	};
	private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
	private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex
	private final float[] color = {0.63f, 0.76f, 0.22f, 1.0f};

	public Triangle() {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length*4);

		byteBuffer.order(ByteOrder.nativeOrder());

		vertexBuffer = byteBuffer.asFloatBuffer();

		vertexBuffer.put(triangleCoords);

		vertexBuffer.position(0);

		int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);

		int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram();

		GLES20.glAttachShader(mProgram, vertexShader);

		GLES20.glAttachShader(mProgram, fragmentShader);

		GLES20.glLinkProgram(mProgram);
	}

	public void draw() {
		GLES20.glUseProgram(mProgram);

		positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		GLES20.glEnableVertexAttribArray(positionHandle);

		colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		GLES20.glVertexAttribPointer(
						positionHandle,
						COORDS_PER_VERTEX,
						GLES20.GL_FLOAT,
						false,
						vertexStride,
						vertexBuffer
		);

		GLES20.glUniform4fv(colorHandle, 1, color, 0);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

		GLES20.glDisableVertexAttribArray(positionHandle);
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
}

