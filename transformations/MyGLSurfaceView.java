package com.prograavanzada.transformations;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

  private final MyGLRenderer renderer;

  public MyGLSurfaceView(Context context) {
    super(context);

    // Indica que usaremos OpenGL ES 2.0
    setEGLContextClientVersion(2);

    // Crea el renderer y asígnalo a esta GLSurfaceView
    renderer = new MyGLRenderer();
    setRenderer(renderer);
  }
}