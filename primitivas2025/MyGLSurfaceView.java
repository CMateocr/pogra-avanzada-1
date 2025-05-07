package com.progavanzada.primitivas2025;

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
