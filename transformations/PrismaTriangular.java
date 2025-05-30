package com.prograavanzada.transformations;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
// piramide triangular
public class PrismaTriangular {
  // Coordenadas del prisma triangular
  private static final float[] VERTICES = {
          // Base inferior (triángulo)
          -0.5f, 0.0f, -0.5f,   // 0 - vértice izquierdo
          0.5f, 0.0f, -0.5f,    // 1 - vértice derecho
          0.0f, 0.0f, 0.5f,     // 2 - vértice frontal

          // Base superior (triángulo)
          -0.5f, 1.0f, -0.5f,   // 3 - vértice izquierdo
          0.5f, 1.0f, -0.5f,    // 4 - vértice derecho
          0.0f, 1.0f, 0.5f      // 5 - vértice frontal
  };

  // Colores para cada vértice (RGBA)
  private static final float[] COLORS = {
          // Base inferior (roja)
          1.0f, 0.0f, 0.0f, 1.0f,  // vértice 0 - rojo
          1.0f, 0.0f, 0.0f, 1.0f,  // vértice 1 - rojo
          1.0f, 0.0f, 0.0f, 1.0f,  // vértice 2 - rojo

          // Base superior (verde)
          0.0f, 1.0f, 0.0f, 1.0f,  // vértice 3 - verde
          0.0f, 1.0f, 0.0f, 1.0f,  // vértice 4 - verde
          0.0f, 1.0f, 0.0f, 1.0f   // vértice 5 - verde
  };

  // Índices para dibujar las caras con diferentes colores
  private static final short[] INDICES = {
          // Base inferior (roja)
          0, 1, 2,

          // Base superior (verde)
          3, 4, 5,

          // Cara trasera (azul)
          0, 1, 4,
          0, 4, 3,

          // Cara derecha (amarilla)
          1, 2, 5,
          1, 5, 4,

          // Cara izquierda (magenta)
          2, 0, 3,
          2, 3, 5
  };

  // Colores para cada cara (6 caras, 2 triángulos por cara)
  private static final float[] FACE_COLORS = {
          // Base inferior (roja)
          1.0f, 0.0f, 0.0f, 1.0f,
          1.0f, 0.0f, 0.0f, 1.0f,

          // Base superior (verde)
          0.0f, 1.0f, 0.0f, 1.0f,
          0.0f, 1.0f, 0.0f, 1.0f,

          // Cara trasera (azul)
          0.0f, 0.0f, 1.0f, 1.0f,
          0.0f, 0.0f, 1.0f, 1.0f,

          // Cara derecha (amarilla)
          1.0f, 1.0f, 0.0f, 1.0f,
          1.0f, 1.0f, 0.0f, 1.0f,

          // Cara izquierda (magenta)
          1.0f, 0.0f, 1.0f, 1.0f,
          1.0f, 0.0f, 1.0f, 1.0f
  };

  private final FloatBuffer vertexBuffer;
  private final FloatBuffer colorBuffer;
  private final ShortBuffer indexBuffer;
  private final int program;

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

  public PrismaTriangular() {
    // Crear buffer de vértices
    ByteBuffer vb = ByteBuffer.allocateDirect(VERTICES.length * 4);
    vb.order(ByteOrder.nativeOrder());
    vertexBuffer = vb.asFloatBuffer();
    vertexBuffer.put(VERTICES);
    vertexBuffer.position(0);

    // Crear buffer de colores
    ByteBuffer cb = ByteBuffer.allocateDirect(FACE_COLORS.length * 4);
    cb.order(ByteOrder.nativeOrder());
    colorBuffer = cb.asFloatBuffer();
    colorBuffer.put(FACE_COLORS);
    colorBuffer.position(0);

    // Crear buffer de índices
    ByteBuffer ib = ByteBuffer.allocateDirect(INDICES.length * 2);
    ib.order(ByteOrder.nativeOrder());
    indexBuffer = ib.asShortBuffer();
    indexBuffer.put(INDICES);
    indexBuffer.position(0);

    // Compilar shaders
    int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
    int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);

    // Crear programa y adjuntar shaders
    program = GLES20.glCreateProgram();
    GLES20.glAttachShader(program, vertexShader);
    GLES20.glAttachShader(program, fragmentShader);
    GLES20.glLinkProgram(program);
  }

  private int loadShader(int type, String shaderCode) {
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);

    int[] compileStatus = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
    if (compileStatus[0] == 0) {
      GLES20.glDeleteShader(shader);
      throw new RuntimeException("Error compilando shader: " + GLES20.glGetShaderInfoLog(shader));
    }

    return shader;
  }

  public void draw(float[] mvpMatrix) {
    GLES20.glUseProgram(program);

    // Pasar la matriz de transformación
    int matrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
    GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0);

    // Pasar las posiciones de los vértices
    int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
    GLES20.glEnableVertexAttribArray(positionHandle);
    GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

    // Pasar los colores (usando el buffer de colores por cara)
    int colorHandle = GLES20.glGetAttribLocation(program, "aColor");
    GLES20.glEnableVertexAttribArray(colorHandle);
    GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

    // Dibujar el prisma
    GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    // Deshabilitar los arrays de atributos
    GLES20.glDisableVertexAttribArray(positionHandle);
    GLES20.glDisableVertexAttribArray(colorHandle);

  }
  public void setColor(float[] rgba) {
    // Cada cara tiene dos triángulos, y cada vértice necesita su color
    for (int i = 0; i < FACE_COLORS.length / 4; i++) {
      colorBuffer.put(rgba);
    }
    colorBuffer.position(0);
  }
}
