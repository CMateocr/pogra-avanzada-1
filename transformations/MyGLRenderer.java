package com.prograavanzada.transformations;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

  private Piramide piramide;
  private Sphere sphere;
  private Torus torus;
  private Cilindro cilindro;
  private Cube cube;
  private PrismaTriangular prismaTriangular;
  private PiramideCuadrangular piramideCuadrangular;
  private Cono cono;
  private PiramideTriangular piramideTriangular;
  private PrismaCuadrangular prismaCuadrangular;


  private final float[] mProjectionMatrix = new float[16];
  private final float[] mViewMatrix = new float[16];
  private final float[] mVPMatrix = new float[16];
  private final float[] mModelMatrix = new float[16];
  private float angle = 0.0f;

  @Override
  public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
    GLES20.glClearColor(0f, 0f, 0f, 1f);
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);

    sphere = new Sphere(1.0f, 30, 30);

    // Inicializa la pirámide para dibujar
    float[] color = {1.0f, 1.0f, 0.0f, 1.0f}; // RGBA
    piramide = new Piramide(color);

    // Crear torus: (resolución, radio mayor, radio menor)
    torus = new Torus(30, 1.5f, 0.4f);

    // Crear un cilindro con el número de "slices" que quieras (por ejemplo, 36)
    float[] lateralColor = {1.0f, 1.0f, 1.0f, 0.5f}; // Amarillo
    float[] topBaseColor = {0.0f, 1.0f, 0.0f, 1.0f}; // Azul
    float[] bottomBaseColor = {1.0f, 1f, 0.28f, 1.0f}; // Tomate

    cilindro = new Cilindro(36, lateralColor, topBaseColor, bottomBaseColor);

    float[] cubeCoords = {
            // frente
            -1.0f,  1.0f,  1.0f,   // 0
            -1.0f, -1.0f,  1.0f,   // 1
            1.0f, -1.0f,  1.0f,   // 2
            1.0f,  1.0f,  1.0f,   // 3
            // atrás
            -1.0f,  1.0f, -1.0f,   // 4
            -1.0f, -1.0f, -1.0f,   // 5
            1.0f, -1.0f, -1.0f,   // 6
            1.0f,  1.0f, -1.0f    // 7
    };
    cube = new Cube(cubeCoords);

    prismaTriangular = new PrismaTriangular();

    // Cambiar color a cian para todas las caras
    float[] cyan = {0.0f, 1.0f, 1.0f, 1.0f};
    prismaTriangular.setColor(cyan);

    // Inicializar pirámide cuadrangular
    piramideCuadrangular = new PiramideCuadrangular();

    // Inicializar cono (radio=1, altura=2, 36 segmentos)
    cono = new Cono(1.0f, 2.0f, 36);

    piramideTriangular = new PiramideTriangular();
    // Opcional: cambiar el color uniforme
    piramideTriangular.setColor(new float[]{1.0f, 0.5f, 0.0f, 1.0f}); // Naranja

    // Inicializar el prisma cuadrangular
    prismaCuadrangular = new PrismaCuadrangular();
  }

  @Override
  public void onSurfaceChanged(GL10 gl10, int width, int height) {
    GLES20.glViewport(0, 0, width, height);

    float ratio = (float) width / height;
    Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
  }

  @Override
  public void onDrawFrame(GL10 gl10) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    // Configura la cámara
    Matrix.setLookAtM(
            mViewMatrix, 0,
            0.0f, 2.0f, -8f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f
    );

    Matrix.setIdentityM(mModelMatrix, 0);

    // Escalado
    Matrix.scaleM(mModelMatrix, 0, 2f, 1.5f, 1f);

    // Rotación
    Matrix.rotateM(mModelMatrix, 0, angle, 1f, 0f, 0f);

    // Traslación
    Matrix.translateM(mModelMatrix, 0, 0, 0, -0);

    // Combinar matrices
    float[] mvMatrix = new float[16];
    Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
    Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mvMatrix, 0);

    // Dibujar
    piramide.draw(mVPMatrix);

    // Incrementar ángulo


    float[] sphereModelMatrix = new float[16];
    Matrix.setIdentityM(sphereModelMatrix, 0);
    Matrix.translateM(sphereModelMatrix, 0, 2f, 0f, 0f);  // mover esfera
    // Rotación
    Matrix.rotateM(sphereModelMatrix, 0, angle, 0f, 0f, 1f);

    // Traslación
    Matrix.translateM(sphereModelMatrix, 0, 0, 0, -0);

    float[] sphereMVMatrix = new float[16];
    float[] sphereMVPMatrix = new float[16];
    Matrix.multiplyMM(sphereMVMatrix, 0, mViewMatrix, 0, sphereModelMatrix, 0);
    Matrix.multiplyMM(sphereMVPMatrix, 0, mProjectionMatrix, 0, sphereMVMatrix, 0);

    if (angle < 180.0f) {
      angle += 1.0f;
    }

    sphere.showWireframe = true;
//    sphere.draw(sphereMVPMatrix, new float[]{0f, 0.5f, 1f, 1f});

    // =====================
// Dibuja el torus
// =====================
    float[] torusModelMatrix = new float[16];
    Matrix.setIdentityM(torusModelMatrix, 0);
    Matrix.translateM(torusModelMatrix, 0, 2f, 0f, 0f); // mover a la derecha
    Matrix.rotateM(torusModelMatrix, 0, angle * 0.5f, 0f, 1f, 0f); // rotación suave

    float[] torusMV = new float[16];
    float[] torusMVP = new float[16];
    Matrix.multiplyMM(torusMV, 0, mViewMatrix, 0, torusModelMatrix, 0);
    Matrix.multiplyMM(torusMVP, 0, mProjectionMatrix, 0, torusMV, 0);
    float[] red = {0f, 0f, 1f, 1f};
//    torus.draw(torusMVP, red);

    float[] cilindroModelMatrix = new float[16];
    Matrix.setIdentityM(cilindroModelMatrix, 0);
    Matrix.translateM(cilindroModelMatrix, 0, -2f, 0f, 0f); // mueve a la izquierda
    Matrix.rotateM(cilindroModelMatrix, 0, angle, 0f, 1f, 0f); // gira en Y

    float[] cilindroMV = new float[16];
    float[] cilindroMVP = new float[16];
    Matrix.multiplyMM(cilindroMV, 0, mViewMatrix, 0, cilindroModelMatrix, 0);
    Matrix.multiplyMM(cilindroMVP, 0, mProjectionMatrix, 0, cilindroMV, 0);

//    cilindro.drawCilindro(cilindroMVP);

    float[] cubeModelMatrix = new float[16];
    Matrix.setIdentityM(cubeModelMatrix, 0);
    Matrix.translateM(cubeModelMatrix, 0, 2f, 2f, 0f); // Lo subimos para que no se solape con otros
    Matrix.rotateM(cubeModelMatrix, 0, angle, 1f, 1f, 0f); // Rotación suave
    Matrix.scaleM(cubeModelMatrix, 0, 0.1f, 1f, 1f); // Escalado

    float[] cubeMV = new float[16];
    float[] cubeMVP = new float[16];
    Matrix.multiplyMM(cubeMV, 0, mViewMatrix, 0, cubeModelMatrix, 0);
    Matrix.multiplyMM(cubeMVP, 0, mProjectionMatrix, 0, cubeMV, 0);

    cube.setCubeColor(new float[]{0.3f, 0.8f, 0.5f, 1.0f});
//    cube.draw(cubeMVP);

    // =====================
    // Dibujar el prismaTriangular
    // =====================
    float[] prismModelMatrix = new float[16];
    Matrix.setIdentityM(prismModelMatrix, 0);
    Matrix.translateM(prismModelMatrix, 0, -2f, 0f, 0f); // Mover a la izquierda
    Matrix.rotateM(prismModelMatrix, 0, angle, 0f, 1f, 0f); // Rotar en Y

    float[] prismMV = new float[16];
    float[] prismMVP = new float[16];
    Matrix.multiplyMM(prismMV, 0, mViewMatrix, 0, prismModelMatrix, 0);
    Matrix.multiplyMM(prismMVP, 0, mProjectionMatrix, 0, prismMV, 0);

//    prismaTriangular.draw(prismMVP);

    // =====================
    // Dibujar pirámide cuadrangular
    // =====================
    float[] pyramidModelMatrix = new float[16];
    Matrix.setIdentityM(pyramidModelMatrix, 0);
    Matrix.translateM(pyramidModelMatrix, 0, -2f, 0f, 2f); // Posición diferente
    Matrix.rotateM(pyramidModelMatrix, 0, angle, 0f, 1f, 0f); // Rotación en Y

    float[] pyramidMV = new float[16];
    float[] pyramidMVP = new float[16];
    Matrix.multiplyMM(pyramidMV, 0, mViewMatrix, 0, pyramidModelMatrix, 0);
    Matrix.multiplyMM(pyramidMVP, 0, mProjectionMatrix, 0, pyramidMV, 0);

//    piramideCuadrangular.draw(pyramidMVP);

    // =====================
    // Dibujar cono
    // =====================
    float[] coneModelMatrix = new float[16];
    Matrix.setIdentityM(coneModelMatrix, 0);
    Matrix.translateM(coneModelMatrix, 0, 2f, 0f, -2f); // Posición diferente
    Matrix.rotateM(coneModelMatrix, 0, angle, 0f, 1f, 0f); // Rotación en Y

    float[] coneMV = new float[16];
    float[] coneMVP = new float[16];
    Matrix.multiplyMM(coneMV, 0, mViewMatrix, 0, coneModelMatrix, 0);
    Matrix.multiplyMM(coneMVP, 0, mProjectionMatrix, 0, coneMV, 0);

//    cono.draw(coneMVP);

    // Matriz de modelo para la pirámide
    float[] piramideTriangularModelMatrix = new float[16];
    Matrix.setIdentityM(piramideTriangularModelMatrix, 0);
    Matrix.translateM(piramideTriangularModelMatrix, 0, -2f, 0f, 0f); // Posición
    Matrix.rotateM(piramideTriangularModelMatrix, 0, angle, 0f, 1f, 0f); // Rotación

    float[] piramideTriangularMV = new float[16];
    float[] piramideTriangularMVP = new float[16];
    Matrix.multiplyMM(piramideTriangularMV, 0, mViewMatrix, 0, pyramidModelMatrix, 0);
    Matrix.multiplyMM(piramideTriangularMVP, 0, mProjectionMatrix, 0, piramideTriangularMV, 0);

    piramideTriangular.draw(piramideTriangularMVP);

    // =====================
    // Dibujar prisma cuadrangular
    // =====================
    float[] prismaCuadngularModelMatrix = new float[16];
    Matrix.setIdentityM(prismaCuadngularModelMatrix, 0);
    Matrix.translateM(prismaCuadngularModelMatrix, 0, 2f, 0f, 0f); // Mover a la derecha
    Matrix.rotateM(prismaCuadngularModelMatrix, 0, angle, 0f, 1f, 0f); // Rotar en Y
    Matrix.scaleM(prismaCuadngularModelMatrix, 0, 0.1f, 0.4f, 0.1f); // Escalar un poco

    float[] prismaCuadrangularMv = new float[16];
    float[] prismaCuadrangularMVP = new float[16];
    Matrix.multiplyMM(prismaCuadrangularMv, 0, mViewMatrix, 0, prismaCuadngularModelMatrix, 0);
    Matrix.multiplyMM(prismaCuadrangularMVP, 0, mProjectionMatrix, 0, prismaCuadrangularMv, 0);

    prismaCuadrangular.draw(prismaCuadrangularMVP);
  }

  public static int loadShader(int type, String shaderCode) {
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);
    return shader;
  }
}