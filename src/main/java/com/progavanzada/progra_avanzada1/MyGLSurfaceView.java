package com.progavanzada.progra_avanzada1;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

	private final MyGLRenderer renderer;

	public MyGLSurfaceView(Context context) {
		super(context);

		// opengl version
		setEGLContextClientVersion(2);

		renderer = new MyGLRenderer();
		setRenderer(renderer);
	}
}
