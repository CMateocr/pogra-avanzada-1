package com.prograavanzada.transformations;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Piramide {

  private FloatBuffer vertexBuffer;
  private ShortBuffer indexBuffer;
  private float[] color; // Color RGBA
  private final int program;

  // Vértices de la pirámide (4 vértices)
  static float[] vertices = {
          0f, 1.0f, 0f,      // V0: vértice superior
          -1.0f, -1.0f, 1.0f, // V1: vértice inferior izquierdo
          1.0f, -1.0f, 1.0f,  // V2: vértice inferior derecho
          0f, -1.0f, -1.0f   // V3: vértice inferior trasero
  };

  // Índices para formar las caras de la pirámide (triángulos)
  private final short[] indices = {
          0, 1, 2,  // Cara frontal
          0, 2, 3,  // Cara derecha
          0, 3, 1,  // Cara izquierda
          1, 2, 3   // Base
  };

  // Código del shader de vértices
  private final String vertexShaderCode =
          "attribute vec4 vPosition;" +
                  "uniform mat4 uMVPMatrix;" +
                  "void main() {" +
                  "  gl_Position = uMVPMatrix * vPosition;" +
                  "}";

  // Código del shader de fragmentos (color rojo)
  private final String fragmentShaderCode =
          "precision mediump float;" +
                  "uniform vec4 vColor;" +  // Línea nueva
                  "void main() {" +
                  "  gl_FragColor = vColor;" +  // Usar color uniforme
                  "}";


  public Piramide(float[] color) {
    this.color = color;
    // Preparar el buffer de vértices
    ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
    bb.order(java.nio.ByteOrder.nativeOrder());
    vertexBuffer = bb.asFloatBuffer();
    vertexBuffer.put(vertices);
    vertexBuffer.position(0);

    // Preparar el buffer de índices
    ByteBuffer ib = ByteBuffer.allocateDirect(indices.length * 2);
    ib.order(java.nio.ByteOrder.nativeOrder());
    indexBuffer = ib.asShortBuffer();
    indexBuffer.put(indices);
    indexBuffer.position(0);

    // Compilar shaders
    int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

    // Crear programa OpenGL y adjuntar shaders
    program = GLES20.glCreateProgram();
    GLES20.glAttachShader(program, vertexShader);
    GLES20.glAttachShader(program, fragmentShader);
    GLES20.glLinkProgram(program);
  }

  public void draw(float[] mvpMatrix) {
    // Usar programa OpenGL
    GLES20.glUseProgram(program);

    int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
    GLES20.glUniform4fv(colorHandle, 1, color, 0);

    // Obtener el handle para el atributo de posición
    int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");

    // Habilitar atributo de posición y preparar buffer
    GLES20.glEnableVertexAttribArray(positionHandle);
    vertexBuffer.position(0);
    GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

    // Pasar la matriz de transformación al shader
    int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

    // Dibujar la pirámide usando índices
    indexBuffer.position(0);
    GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    // Deshabilitar atributo de posición
    GLES20.glDisableVertexAttribArray(positionHandle);
  }
}