package com.prograavanzada.transformations;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {
  private final FloatBuffer vertexBuffer;
  private final int mProgram;
  private int mvpMatrixHandle;
  private int positionHandle;
  private int colorHandle;

  private float[] cubeCoords;

  private final float[][] faceColors = {
          {1.0f, 0.0f, 0.0f, 1.0f},   // Rojo
          {0.0f, 1.0f, 0.0f, 1.0f},     // Verde
          {0.0f, 0.0f, 1.0f, 1.0f},     // Azul
          {1.0f, 1.0f, 0.0f, 1.0f},     // Amarillo
          {0.0f, 1.0f, 1.0f, 1.0f},     // Cian
          {1.0f, 0.0f, 1.0f, 1.0f}      // Magenta
  };

  // El orden se debe aplicar en sentido antihorario
  private final short[] drawOrder = {
          // cara derecha
          0, 1, 2, 0, 2, 3,
          // cara izquierda
          4, 5, 6, 4, 6, 7,
          // cara inferior
          0, 1, 5, 0, 5, 4,
          // cara superior
          2, 3, 7, 2, 7, 6,
          // cara trasera
          0, 3, 7, 0, 7, 4,
          // cara frontal
          1, 2, 6, 1, 6, 5
  };
  static final int COORDS_POR_VERTEX = 3;
  private final int vertexStride = COORDS_POR_VERTEX*4;


  private ShortBuffer shortBuffer;


  //tomar en cuenta que las coords seran 8 por el numero de vertices necesarios
  public Cube(float[] coords) {
    this.cubeCoords = coords;
    ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
    bb.order(ByteOrder.nativeOrder());
    vertexBuffer = bb.asFloatBuffer();
    vertexBuffer.put(coords);
    vertexBuffer.position(0);


    ByteBuffer sb = ByteBuffer.allocateDirect(drawOrder.length * 4);
    sb.order(ByteOrder.nativeOrder());
    shortBuffer = sb.asShortBuffer();
    shortBuffer.put(drawOrder);
    shortBuffer.position(0);


    int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShadderCode);
    int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShadderCode);

    mProgram = GLES20.glCreateProgram();
    GLES20.glAttachShader(mProgram, vertexShader);
    GLES20.glAttachShader(mProgram, fragmentShader);
    GLES20.glLinkProgram(mProgram);
  }

  public void draw(float[] mvpMatrix) {
    GLES20.glUseProgram(mProgram);

    // Obtener handles
    positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
    colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

    // Habilitar posición
    GLES20.glEnableVertexAttribArray(positionHandle);
    GLES20.glVertexAttribPointer(
            positionHandle, COORDS_POR_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer);

    // Aplicar matriz de transformación
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

    // Dibujar cada cara con su color
    int verticesPerFace = 6; // 6 vértices por cara (2 triángulos)
    for (int face = 0; face < 6; face++) {
      // Establecer color para esta cara
      GLES20.glUniform4fv(colorHandle, 1, faceColors[face], 0);

      // Dibujar los triángulos de esta cara
      GLES20.glDrawElements(
              GLES20.GL_TRIANGLES,
              verticesPerFace,
              GLES20.GL_UNSIGNED_SHORT,
              shortBuffer.position(face * verticesPerFace)
      );
    }

    GLES20.glDisableVertexAttribArray(positionHandle);
  }


  private final String vertexShadderCode =
          "uniform mat4 uMVPMatrix;" +
                  "attribute vec4 vPosition;" +
                  "void main() {" +
                  "  gl_Position = uMVPMatrix * vPosition;" +
                  "}";

  private final String fragmentShadderCode =
          "precision mediump float;" +
                  "uniform vec4 vColor;" +
                  "void main() {" +
                  "  gl_FragColor = vColor;" +
                  "}";

  public void setCubeColor(float[] rgbaColor) {
    if (rgbaColor.length != 4) return;
    for (int i = 0; i < faceColors.length; i++) {
      faceColors[i] = rgbaColor.clone();
    }
  }


}