package com.progravanzada.camera;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
	private final FloatBuffer vertexBuffer; //buffer de memoria que guarda coordenadas de vertices
	private final int mProgram; //combinar vertex shader con fragment shader (para ejecutar)
	private int positionHandle; //pasarle los datos de posición del vértice al shader.
	private int colorHandle; //almacena id del color
	private int mvpMatrixHandle; //almacena id de la matriz de proyección
	static final int COORDS_PER_VERTEX = 3;

	private final int vertexCount = 3; //cuenta vertices que vamos a dibujar (el punto tiene un vertice)
	private final int vertexStride = COORDS_PER_VERTEX*4;
	float color[] ;

	public Triangle(float[] triangleCoords,float[] color){
		this.color = color;
		// siempre necesarias
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length*4);
		byteBuffer.order(ByteOrder.nativeOrder()); //sirve para establecer el orden de bytes (endianness) del buffer
		vertexBuffer = byteBuffer.asFloatBuffer(); //convierte a floatBuffer para trabajar con floats en lugar de bytes
		vertexBuffer.put(triangleCoords); //Copia los valores del arreglo pointCoord al FloatBuffer
		vertexBuffer.position(0); //para que empiece desde la posicion 0 del arreglo

		int vertexShader = MyGLRenderer.loadShaders(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = MyGLRenderer.loadShaders(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); //crea el programa
		GLES20.glAttachShader(mProgram,vertexShader); //agrega el vertex a la inicicializacion del programa
		GLES20.glAttachShader(mProgram,fragmentShader);
		GLES20.glLinkProgram(mProgram); //para compilar programa
	}

	public void draw(float[] mvpMatrix){
		GLES20.glUseProgram(mProgram);
		positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition"); // identificador de la posicion
		GLES20.glEnableVertexAttribArray(positionHandle);

		GLES20.glVertexAttribPointer(
						positionHandle, COORDS_PER_VERTEX,
						GLES20.GL_FLOAT, false,
						vertexStride, vertexBuffer
		);

		colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		GLES20.glUniform4fv(
						colorHandle, 1, //numero de vectores que se va a enviar
						color, 0); //donde se empiezan a leer los valores (indice arreglo)

		mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

		GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0); //enviar matriz de proyeccion

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

		GLES20.glDisableVertexAttribArray(positionHandle); //es como limpiar
	}

	private final String vertexShaderCode =
				"uniform mat4 uMVPMatrix;" +
					"attribute vec4 vPosition;"+ //es como una variable vec4 es como un tipo de dato y vPosition siempre debe llamarse asi
									"void main(){"+
									"gl_Position = uMVPMatrix * vPosition;"+
									"}"; //ATTRIBUTES

	private final String fragmentShaderCode=
					"precision mediump float;"+
									"uniform vec4 vColor;"+
									"void main(){"+
									"gl_FragColor =vColor; "+
									"}"; //UNIFORMS

}