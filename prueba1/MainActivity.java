package com.progavanzada.prueba1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// ! palabra de cuatro letras y usando todas las primitivas que hemos hecho
public class MainActivity extends AppCompatActivity {

	private MyGLSurfaceView glSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		glSurfaceView = new MyGLSurfaceView(this);

		setContentView(glSurfaceView);
	}
}