package com.prograavanzada.transformations;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class PrismaCuadrangular {

  private FloatBuffer vertexBuffer;
  private FloatBuffer colorBuffer;
  private ShortBuffer indexBuffer;
  private int positionHandle;
  private int colorHandle;
  private final int mProgram;
  private int mMVPMatrixHandle;

  // Vértices del prisma cuadrangular
  private static final float[] vertices = {

          // Cara superior
          -1.0f, 1.0f, -3.0f,   // V4
          1.0f, 1.0f, -3.0f,    // V5
          1.0f, 1.0f, 2.0f,     // V6
          -1.0f, 1.0f, 2.0f,    // V7

          // Cara inferior
          -1.0f, -1.0f, -3.0f,  // V0
          1.0f, -1.0f, -3.0f,   // V1
          1.0f, -1.0f, 2.0f,    // V2
          -1.0f, -1.0f, 2.0f,   // V3
  };
  private static final float[] colors = {
          1f, 0f, 0f, 1f,  // V0: Rojo
          0f, 1f, 0f, 1f,  // V1: Verde
          0f, 0f, 1f, 1f,  // V2: Azul
          1f, 1f, 0f, 1f,  // V3: Amarillo

          // Vértices de la base superior
          0f, 1f, 1f, 1f,  // V4: Cian
          1f, 0f, 1f, 1f,  // V5: Magenta
          1f, 0.647f, 0f, 1f,  // V6: Naranja
          1f, 1f, 1f, 1f   // V7: Blanco
  };

  // Índices para las caras del prisma
  private static final short[] indices = {
          // Base inferior
          0, 1, 2,  0, 2, 3,

          // Base superior
          4, 5, 6,  4, 6, 7,

          // Cara frontal
          0, 1, 5,  0, 5, 4,

          // Cara derecha
          1, 2, 6,  1, 6, 5,

          // Cara trasera
          2, 3, 7,  2, 7, 6,

          // Cara izquierda
          3, 0, 4,  3, 4, 7
  };

  public PrismaCuadrangular() {
    // Preparar el buffer de vértices
    ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length * 4);
    vb.order(ByteOrder.nativeOrder());
    vertexBuffer = vb.asFloatBuffer();
    vertexBuffer.put(vertices);
    vertexBuffer.position(0);

    // Preparar el buffer de índices
    ByteBuffer ib = ByteBuffer.allocateDirect(indices.length * 2);
    ib.order(ByteOrder.nativeOrder());
    indexBuffer = ib.asShortBuffer();
    indexBuffer.put(indices);
    indexBuffer.position(0);

    // Preparar el buffer de colores
    ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
    cb.order(ByteOrder.nativeOrder());
    colorBuffer = cb.asFloatBuffer();
    colorBuffer.put(colors);
    colorBuffer.position(0);

    int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

    mProgram = GLES20.glCreateProgram();
    GLES20.glAttachShader(mProgram, vertexShader);
    GLES20.glAttachShader(mProgram, fragmentShader);
    GLES20.glLinkProgram(mProgram);
  }

  public void draw(float[] mvpMatrix) {
    GLES20.glUseProgram(mProgram);

    // Obtener las ubicaciones de los atributos y uniformes
    positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
    GLES20.glEnableVertexAttribArray(positionHandle);
    GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

    colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
    GLES20.glEnableVertexAttribArray(colorHandle);
    GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

    mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

    // Dibujar el prisma
    GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    // Deshabilitar los atributos después de dibujar
    GLES20.glDisableVertexAttribArray(positionHandle);
    GLES20.glDisableVertexAttribArray(colorHandle);
  }

  // Compilación de shaders
  private final String vertexShaderCode = "uniform mat4 uMVPMatrix;" +
          "attribute vec4 vPosition;" +
          "attribute vec4 vColor;" +
          "varying vec4 outColor;" +
          "void main() {" +
          "  gl_Position = uMVPMatrix * vPosition;" +
          "  outColor = vColor;" +
          "}";

  private final String fragmentShaderCode = "precision mediump float;" +
          "varying vec4 outColor;" +
          "void main() {" +
          "  gl_FragColor = outColor;" +
          "}";
  private int loadShader(int type, String shaderCode) {
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);
    return shader;
  }
}
