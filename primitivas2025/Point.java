package com.progavanzada.primitivas2025;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

// opengl trabaja solo con identificadores
public class Point {

	private final FloatBuffer vertexBuffer;
	private final int mProgram;
	private int positionHandle;
	private int colorHandle;
	static final int COORDS_PER_VERTEX = 3;
	static float pointCoord[] = {0.0f, 0.0f, 0.0f};
	private final int vertexCount = pointCoord.length/COORDS_PER_VERTEX;
	private final int vertexStride = COORDS_PER_VERTEX*4;
	float color[] = {1.0f, 0.0f, 0.0f, 1.0f};

	public Point() {

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(pointCoord.length*4);

		byteBuffer.order(ByteOrder.nativeOrder());

		vertexBuffer = byteBuffer.asFloatBuffer();

		vertexBuffer.put(pointCoord);

		vertexBuffer.position(0);

	}

	private final String vertexShaderCode =
					"attribute vec4 vPosition;" +
					"void main() {" +
					"" +
					"}";
	private final String fragmentShaderCode = "";



}
