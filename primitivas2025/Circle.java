package com.progavanzada.primitivas2025;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Circle {
	private final FloatBuffer vertexBuffer;
	private final int mProgram;
	private int positionHandle;
	private int colorHandle;
	private static final int COORDS_PER_VERTEX = 3;

	float[] color = {0.0f, 0.0f, 1.0f, 1.0f};
	private final int vertexCount;
	private final int vertexStride = COORDS_PER_VERTEX*4; // ? cantidad de vertices que hay entre el inicio y el final
	private float[] circleCoords;

	private float[] createCircleCoords(float radius, int numPoints) {
		List<Float> coords = new ArrayList<>();

		coords.add(0.0f); // center x
		coords.add(0.0f); // center y
		coords.add(0.0f); // center z

		double angle = 2.0 * Math.PI / numPoints;

		for (int i = 0; i <= numPoints; i++) {
			double angle2 = i * angle;

			float x = (float) (radius * Math.cos(angle2));
			float y = (float) (radius * Math.sin(angle2));
			coords.add(x);
			coords.add(y);
			coords.add(0.0f); // z coordinate
		}

		float[] array = new float[coords.size()];

		for(int i = 0; i < coords.size(); i++) {
			array[i] = coords.get(i);
		}

		return array;
	}
	public Circle(float radius, int numPoints) {
		circleCoords = createCircleCoords(radius, numPoints);

		vertexCount = circleCoords.length / COORDS_PER_VERTEX;

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(circleCoords.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuffer.asFloatBuffer();
		vertexBuffer.put(circleCoords);
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

		colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		GLES20.glEnableVertexAttribArray(positionHandle);

		GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		GLES20.glUniform4fv(colorHandle, 1, color, 0);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

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
