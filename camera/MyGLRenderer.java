package com.progravanzada.camera;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//se encarga de hacer el dibujo
public class MyGLRenderer implements GLSurfaceView.Renderer {
	private Triangle t ;
	private final float[] mProjectionMatrix = new float[16]; //perspectiva
	private final float[] mViewMatrix = new float[16]; //posicion de la camara
	private final float[] mVPMatrix = new float[16]; //resultado de proyeccion*vista
	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST); //activar concepto de profundidad
		t= new Triangle(new float[]{0.0f,1.0f,0.0f, -1.0f,-1.0f,0.0f, 1.0f, -1.0f,0.0f}, new float[] {1.0f,0.0f,0.0f,1.0f});
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		GLES20.glViewport(0,0,width,height);

		float ratio = (float) width/height ;
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1,1,1,10.0f);
		// Matrix clase
		//frustum escoger proyeccion
		// (matriz, indicar indice arr en que empezamos, limite izquierdo, limite derecho, extension vertical, extension vertical, distancias entre los planos de vision (cercano), lejano)


	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		//limpiar buffer de colores     |   limpiar buffer de profundidad
		//CÁMARA
		Matrix.setLookAtM(mViewMatrix, 0, //configurar matriz de vista
						0.0f,0.0f, -3.0f, // es como que la camara está hacia atras

						0.0f,0.0f,0.0f, // apunta al origen
						0.0f, 1.0f, 0.0f//posicion de camara lente hacia arriba
		);

		Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


		//despues de cero es posicion de camara
		t.draw(mVPMatrix);


	}

	//BRUTAL
	public static int loadShaders(int type, String shaderCode){ //para cargar los shaders
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);


		return shader;
	}
}