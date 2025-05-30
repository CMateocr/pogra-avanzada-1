package com.prograavanzada.transformations;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class PiramideTriangular {
  private final FloatBuffer vertexBuffer;
  private final FloatBuffer colorBuffer;
  private final ShortBuffer indexBuffer;
  private final int program;

  private static final int COORDS_PER_VERTEX = 3;
  private final int vertexStride = COORDS_PER_VERTEX * 4;

  // Coordenadas de los 4 vértices del tetraedro
  private static final float[] VERTICES = {
          // Vértice superior (punta)
          0.0f,  0.5f,  0.0f,  // 0

          // Vértices de la base triangular
          -0.5f, -0.5f,  0.5f,  // 1 - inferior izquierdo
          0.5f, -0.5f,  0.5f,   // 2 - inferior derecho
          0.0f, -0.5f, -0.5f    // 3 - inferior trasero
  };

  // Índices para las 4 caras triangulares
  private static final short[] INDICES = {
          0, 1, 2,  // Cara frontal
          0, 2, 3,  // Cara derecha
          0, 3, 1,  // Cara izquierda
          1, 3, 2   // Cara base
  };

  // Colores RGBA para cada cara (4 caras)
  private static final float[] COLORS = {
          // Cara frontal (roja)
          1.0f, 0.0f, 0.0f, 1.0f,
          1.0f, 0.0f, 0.0f, 1.0f,
          1.0f, 0.0f, 0.0f, 1.0f,

          // Cara derecha (verde)
          0.0f, 1.0f, 0.0f, 1.0f,
          0.0f, 1.0f, 0.0f, 1.0f,
          0.0f, 1.0f, 0.0f, 1.0f,

          // Cara izquierda (azul)
          0.0f, 0.0f, 1.0f, 1.0f,
          0.0f, 0.0f, 1.0f, 1.0f,
          0.0f, 0.0f, 1.0f, 1.0f,

          // Cara base (amarilla)
          1.0f, 1.0f, 0.0f, 1.0f,
          1.0f, 1.0f, 0.0f, 1.0f,
          1.0f, 1.0f, 0.0f, 1.0f
  };

  private static final String VERTEX_SHADER_CODE =
          "uniform mat4 uMVPMatrix;" +
                  "attribute vec4 vPosition;" +
                  "attribute vec4 aColor;" +
                  "varying vec4 vColor;" +
                  "void main() {" +
                  "   vColor = aColor;" +
                  "   gl_Position = uMVPMatrix * vPosition;" +
                  "}";

  private static final String FRAGMENT_SHADER_CODE =
          "precision mediump float;" +
                  "varying vec4 vColor;" +
                  "void main() {" +
                  "   gl_FragColor = vColor;" +
                  "}";

  public PiramideTriangular() {
    // Inicializar buffer de vértices
    ByteBuffer vb = ByteBuffer.allocateDirect(VERTICES.length * 4);
    vb.order(ByteOrder.nativeOrder());
    vertexBuffer = vb.asFloatBuffer();
    vertexBuffer.put(VERTICES);
    vertexBuffer.position(0);

    // Inicializar buffer de colores
    ByteBuffer cb = ByteBuffer.allocateDirect(COLORS.length * 4);
    cb.order(ByteOrder.nativeOrder());
    colorBuffer = cb.asFloatBuffer();
    colorBuffer.put(COLORS);
    colorBuffer.position(0);

    // Inicializar buffer de índices
    ByteBuffer ib = ByteBuffer.allocateDirect(INDICES.length * 2);
    ib.order(ByteOrder.nativeOrder());
    indexBuffer = ib.asShortBuffer();
    indexBuffer.put(INDICES);
    indexBuffer.position(0);

    // Compilar shaders
    int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
    int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);

    // Crear programa OpenGL
    program = GLES20.glCreateProgram();
    GLES20.glAttachShader(program, vertexShader);
    GLES20.glAttachShader(program, fragmentShader);
    GLES20.glLinkProgram(program);
  }

  private int loadShader(int type, String shaderCode) {
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);
    return shader;
  }

  public void draw(float[] mvpMatrix) {
    GLES20.glUseProgram(program);

    // Obtener handles de los atributos
    int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
    int colorHandle = GLES20.glGetAttribLocation(program, "aColor");
    int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

    // Pasar la matriz de transformación
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

    // Habilitar y pasar los atributos de posición
    GLES20.glEnableVertexAttribArray(positionHandle);
    GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

    // Habilitar y pasar los atributos de color
    GLES20.glEnableVertexAttribArray(colorHandle);
    GLES20.glVertexAttribPointer(colorHandle, 4,
            GLES20.GL_FLOAT, false, 0, colorBuffer);

    // Dibujar la pirámide
    GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES.length,
            GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    // Deshabilitar los arrays de atributos
    GLES20.glDisableVertexAttribArray(positionHandle);
    GLES20.glDisableVertexAttribArray(colorHandle);
  }

  public void setColor(float[] rgba) {
    // Actualizar todos los colores al mismo valor
    for (int i = 0; i < COLORS.length / 4; i++) {
      System.arraycopy(rgba, 0, COLORS, i * 4, 4);
    }
    colorBuffer.put(COLORS);
    colorBuffer.position(0);
  }
}
