package com.prograavanzada.ilumination;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Piramide {
  private final FloatBuffer vertexBuffer;
  private final ShortBuffer indexBuffer;
  private final int mProgram;
  private int positionHandle;
  private int mvpMatrixHandle;
  private int ambientHandle;
  private int colorHandle;

  static float[] vertices = {
          0.0f, 1.0f, 0.0f,   // 0 punta
          -1.0f, -1.0f, 1.0f, // 1 base izquierda
          1.0f, -1.0f, 1.0f,  // 2 base derecha
          0.0f, -1.0f, -1.0f  // 3 base trasera
  };

  private final short[] indices = {
          0, 1, 2, // Cara frontal
          0, 2, 3, // Cara derecha
          0, 3, 1, // Cara izquierda
          1, 3, 2  // Base (cara inferior)
  };

  float color[] = {0.0f, 1.0f, 0.0f, 1.0f};
  float ambientLightColor[] = {1.0f, 1.0f, 0.0f, 1.0f};

  private final String vertexShaderCode =
          "uniform mat4 uMVPMatrix;" +
                  "attribute vec4 vPosition;" +
                  "void main() {" +
                  "  gl_Position = uMVPMatrix * vPosition;" +
                  "}";

  private final String fragmentShaderCode =
          "precision mediump float;" +
                  "uniform vec4 vColor;" +      // <-- Agregado
                  "uniform vec4 ambientLight;" +
                  "void main() {" +
                  "  gl_FragColor = vColor * ambientLight;" + // luz
                  //"gl_FragColor = vColor;" + // <-- Usar el uniform solo color
                  "}";

  public Piramide() {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
    byteBuffer.order(ByteOrder.nativeOrder()); // Establecer orden de bytes (endianness)
    vertexBuffer = byteBuffer.asFloatBuffer(); // Convertir a FloatBuffer para floats
    vertexBuffer.put(vertices);                 // Copiar arreglo de vértices
    vertexBuffer.position(0);                   // Empezar desde posición 0

    // PARA EL SHORTBUFFER -- mismo procedimiento para índices
    ByteBuffer sb = ByteBuffer.allocateDirect(indices.length * 2); // short = 2 bytes
    sb.order(ByteOrder.nativeOrder());
    indexBuffer = sb.asShortBuffer();
    indexBuffer.put(indices);
    indexBuffer.position(0);

    int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

    mProgram = GLES20.glCreateProgram();          // Crear programa
    GLES20.glAttachShader(mProgram, vertexShader); // Adjuntar vertex shader
    GLES20.glAttachShader(mProgram, fragmentShader); // Adjuntar fragment shader
    GLES20.glLinkProgram(mProgram);                // Compilar programa
  }

  public void draw(float[] mvpMatrix) {
    GLES20.glUseProgram(mProgram);                 // Usar programa OpenGL

    positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition"); // Obtener atributo posición
    GLES20.glEnableVertexAttribArray(positionHandle);                  // Habilitar atributo posición

    vertexBuffer.position(0);  // Buffer en posición inicial
    GLES20.glVertexAttribPointer(
            positionHandle,
            3,                     // Componentes por vértice (x, y, z)
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
    );

    // Color
    colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    GLES20.glUniform4fv(colorHandle, 1, color, 0);

    // Luz ambiental
    ambientHandle = GLES20.glGetUniformLocation(mProgram, "ambientLight");
    GLES20.glUniform4fv(ambientHandle, 1, ambientLightColor, 0);

    // Matriz MVP
    mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

    indexBuffer.position(0);  // Índice en posición inicial
    GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.length,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
    );

    GLES20.glDisableVertexAttribArray(positionHandle); // Deshabilitar atributo
  }
}
