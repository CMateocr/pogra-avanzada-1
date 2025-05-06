package com.progavanzada.primitivas2025;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Square {
	private FloatBuffer vertexBuffer;
	private int mProgram;
	private int positionHandle;
	private int colorHandle;
	private static final int COORDS_PER_VERTEX = 3;
	private static final float[] squareCoords = {
			-0.5f, 0.8f, 0.0f, // top left vertex 0
			0.5f, 0.2f, 0.0f, // top right vertex 1
			0.1f, 0.2f, 0.0f, // bottom right vertex 2
			0.1f, 0.8f, 0.0f // bottom left vertex 3
	};
	private final short[] drawOrder = {0,1,2,0,2,3}; // ! order to draw vertices
}
