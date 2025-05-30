package com.prograavanzada.transformations;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class PiramideCuadrangular {
  private final FloatBuffer vertexBuffer;
  private final ShortBuffer drawListBuffer;
  private final int mProgram;
  private int positionHandle, colorHandle, mvpMatrixHandle;
  private static final int COORDS_PER_VERTEX = 3;
  private final int vertexStride = COORDS_PER_VERTEX * 4;

  // Coordenadas: 4 base, 1 vértice superior (punta)

  private float[] vertices = {
          // base cuadrada (y = 0)
          -0.5f, 0.0f, -0.5f,   // 0: atrás izquierda
          0.5f, 0.0f, -0.5f,   // 1: atrás derecha
          0.5f, 0.0f,  0.5f,   // 2: frente derecha
          -0.5f, 0.0f,  0.5f,   // 3: frente izquierda
          0.0f, 1.0f,  0.0f    // 4: punta
  };

  // 6 triángulos (2 base + 4 lados)
  private short[] drawOrder = {
          0, 1, 2,    // base parte 1
          0, 2, 3,    // base parte 2
          0, 1, 4,    // cara 1
          1, 2, 4,    // cara 2
          2, 3, 4,    // cara 3
          3, 0, 4     // cara 4
  };

  // Colores RGBA para cada triángulo
  private float[][] colores = {
          {1.0f, 0.5f, 0.5f, 1.0f}, // base 1 - rosa claro
          {1.0f, 0.5f, 0.5f, 1.0f}, // base 2 - rosa claro
          {0.0f, 0.0f, 1.0f, 1.0f}, // cara 1 - azul
          {1.0f, 1.0f, 0.0f, 0.5f}, // cara 2 - amarillo
          {1.0f, 0.0f, 1.0f, 0.5f}, // cara 3 - magenta
          {0.0f, 1.0f, 0.0f, 1.0f}  // cara 4 - cian
  };

  private final String vertexShaderCode =
          "uniform mat4 uMVPMatrix;" +
                  "attribute vec4 vPosition;" +
                  "void main() {" +
                  "  gl_Position = uMVPMatrix * vPosition;" +
                  "}";

  private final String fragmentShaderCode =
          "precision mediump float;" +
                  "uniform vec4 vColor;" +
                  "void main() {" +
                  "  gl_FragColor = vColor;" +
                  "}";

  public PiramideCuadrangular() {
    ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length * 4);
    vb.order(ByteOrder.nativeOrder());

    vertexBuffer = vb.asFloatBuffer();
    vertexBuffer.put(vertices);
    vertexBuffer.position(0);

    ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
    dlb.order(ByteOrder.nativeOrder());
    drawListBuffer = dlb.asShortBuffer();
    drawListBuffer.put(drawOrder);
    drawListBuffer.position(0);

    int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

    mProgram = GLES20.glCreateProgram();
    GLES20.glAttachShader(mProgram, vertexShader);
    GLES20.glAttachShader(mProgram, fragmentShader);
    GLES20.glLinkProgram(mProgram);
  }

  public void draw(float[] mvpMatrix) {
    GLES20.glEnable(GLES20.GL_BLEND);
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

    GLES20.glUseProgram(mProgram);

    positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
    GLES20.glEnableVertexAttribArray(positionHandle);
    GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

    mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

    colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

    // Dibujar cada triángulo por separado con su color
    for (int i = 0; i < 6; i++) {
      GLES20.glUniform4fv(colorHandle, 1, colores[i], 0);
      drawListBuffer.position(i * 3); // cada triángulo tiene 3 índices
      GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3,
              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
    }

    GLES20.glDisableVertexAttribArray(positionHandle);

    GLES20.glDisable(GLES20.GL_BLEND);
  }
}
