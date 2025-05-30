package com.prograavanzada.transformations;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Cilindro {
  private final FloatBuffer lateralVertexBuffer;
  private final FloatBuffer topBaseVertexBuffer;
  private final FloatBuffer bottomBaseVertexBuffer;

  private final int mProgram;
  private int positionHandle, mvpMatrixHandle, colorHandle;

  private final int lateralVertexCount;
  private final int baseVertexCount;

  private final int vertexStride = 3 * 4; // 3 coords * 4 bytes per float

  // Colors
  private float[] topBaseColor;
  private float[] lateralColor;
  private float[] bottomBaseColor;


  private final float height = 1.2f;
  private final float radius = 0.6f;

  public Cilindro(int slices, float[] lateralColor, float[] topBaseColor, float[] bottomBaseColor) {

    this.lateralColor = lateralColor;
    this.topBaseColor = topBaseColor;
    this.bottomBaseColor = bottomBaseColor;

    // Generar vértices
    float[] lateralVertices = crearLateralVertices(slices);
    lateralVertexCount = lateralVertices.length / 3;

    float[] topBaseVertices = crearBaseVertices(slices, height / 2);
    float[] bottomBaseVertices = crearBaseVertices(slices, -height / 2);
    baseVertexCount = topBaseVertices.length / 3;

    // Buffers
    lateralVertexBuffer = createFloatBuffer(lateralVertices);
    topBaseVertexBuffer = createFloatBuffer(topBaseVertices);
    bottomBaseVertexBuffer = createFloatBuffer(bottomBaseVertices);

    // Shaders
    int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

    mProgram = GLES20.glCreateProgram();
    GLES20.glAttachShader(mProgram, vertexShader);
    GLES20.glAttachShader(mProgram, fragmentShader);
    GLES20.glLinkProgram(mProgram);
  }

  private FloatBuffer createFloatBuffer(float[] vertices) {
    ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
    bb.order(ByteOrder.nativeOrder());
    FloatBuffer fb = bb.asFloatBuffer();
    fb.put(vertices);
    fb.position(0);
    return fb;
  }

  private float[] crearLateralVertices(int slices) {
    float[] vertices = new float[slices * 6 * 3]; // 6 vértices por slice
    int index = 0;
    for (int i = 0; i < slices; i++) {
      double angle1 = 2 * Math.PI * i / slices;
      double angle2 = 2 * Math.PI * (i + 1) / slices;

      float x1 = (float) (radius * Math.cos(angle1));
      float z1 = (float) (radius * Math.sin(angle1));
      float x2 = (float) (radius * Math.cos(angle2));
      float z2 = (float) (radius * Math.sin(angle2));

      // Primer triángulo
      //Arriba izquierda: (x1, height/2, z1)
      vertices[index++] = x1; vertices[index++] = height / 2; vertices[index++] = z1;
      //Arriba derecha: (x2, height/2, z2)
      vertices[index++] = x2; vertices[index++] = height / 2; vertices[index++] = z2;
      //Abajo izquierda: (x1, -height/2, z1)
      vertices[index++] = x1; vertices[index++] = -height / 2; vertices[index++] = z1;

      // Segundo triángulo
      //Arriba derecha: (x2, height/2, z2)
      vertices[index++] = x2; vertices[index++] = height / 2; vertices[index++] = z2;
      //Abajo derecha: (x2, -height/2, z2)
      vertices[index++] = x2; vertices[index++] = -height / 2; vertices[index++] = z2;
      //Abajo izquierda: (x1, -height/2, z1)
      vertices[index++] = x1; vertices[index++] = -height / 2; vertices[index++] = z1;
    }
    return vertices;
  }

  private float[] crearBaseVertices(int slices, float y) {
    float[] vertices = new float[(slices + 2) * 3];
    int idx = 0;
    vertices[idx++] = 0f; vertices[idx++] = y; vertices[idx++] = 0f; // centro

    for (int i = 0; i <= slices; i++) {
      double angle = 2 * Math.PI * i / slices;
      float x = (float) (radius * Math.cos(angle));
      float z = (float) (radius * Math.sin(angle));
      vertices[idx++] = x;
      vertices[idx++] = y;
      vertices[idx++] = z;
    }
    return vertices;
  }

  public void drawCilindro(float[] mvpMatrix) {
    GLES20.glUseProgram(mProgram);

    positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
    colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
    GLES20.glEnableVertexAttribArray(positionHandle);


    GLES20.glEnable(GLES20.GL_BLEND);
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

    // Base inferior
    bottomBaseVertexBuffer.position(0);
    GLES20.glUniform4fv(colorHandle, 1, bottomBaseColor, 0);
    GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,
            vertexStride, bottomBaseVertexBuffer);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, baseVertexCount);

    // Lateral
    lateralVertexBuffer.position(0);
    GLES20.glUniform4fv(colorHandle, 1, lateralColor, 0);
    GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,
            vertexStride, lateralVertexBuffer);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, lateralVertexCount);

    // Base superior
    topBaseVertexBuffer.position(0);
    GLES20.glUniform4fv(colorHandle, 1, topBaseColor, 0);
    GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,
            vertexStride, topBaseVertexBuffer);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, baseVertexCount);

    GLES20.glDisable(GLES20.GL_BLEND); // Deshabilitar blending después de dibujar las caras
    GLES20.glDisable(GLES20.GL_DEPTH_TEST);


    GLES20.glDisableVertexAttribArray(positionHandle);
  }

  // Vertex Shader
  private final String vertexShaderCode =
          "uniform mat4 uMVPMatrix;" +
                  "attribute vec4 vPosition;" +
                  "void main() {" +
                  "  gl_Position = uMVPMatrix * vPosition;" +
                  "  gl_PointSize = 10.0;" +
                  "}";

  // Fragment Shader
  private final String fragmentShaderCode =
          "precision mediump float;" +
                  "uniform vec4 vColor;" +
                  "void main() {" +
                  "  gl_FragColor = vColor;" +
                  "}";
}
