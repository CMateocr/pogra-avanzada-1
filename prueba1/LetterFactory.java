package com.progavanzada.prueba1;

import java.util.ArrayList;
import java.util.List;

public class LetterFactory {

	public void drawLetterB() {
		Line firstLine = new Line(new float[]{
						-0.8f, 0.0f,
						-0.8f, 0.1f
		});

		Triangle firstTriangle = new Triangle(new float[]{
						-0.8f, 0.1f, 0.0f,  // top
						-0.8f, 0.06f, 0.0f, // bottom left
						-0.7f, 0.07f, 0.0f   // bottom right
		});

		Triangle secondTriangle = new Triangle(new float[]{
						-0.8f, 0.06f, 0.0f,  // top
						-0.8f, 0.00f, 0.0f, // bottom left
						-0.7f, 0.03f, 0.0f   // bottom right
		});

		firstLine.draw();
		firstTriangle.draw();
		secondTriangle.draw();
	}
	public void drawFirstLetterA() {
		Line firstLine = new Line(new float[]{
						-0.7f, 0.0f,
						-0.6f, 0.1f
		});

		Line secondLine = new Line(new float[]{
						-0.5f, 0.0f,
						-0.6f, 0.1f
		});

		Square firstSquare = new Square(new float[]{
						-0.66f, 0.05f, 0.0f,  // top left
						-0.66f, 0.04f, 0.0f,  // bottom left
						-0.55f, 0.04f, 0.0f,  // bottom right
						-0.55f, 0.05f, 0.0f   // top right
		});

		firstLine.draw();
		secondLine.draw();

		firstSquare.draw();
	}

	public void drawSecondLetterA() {
		Line firstLine = new Line(new float[]{
						-0.3f, 0.0f,
						-0.2f, 0.1f
		});

		Line secondLine = new Line(new float[]{
						-0.1f, 0.0f,
						-0.2f, 0.1f
		});

		Square firstSquare = new Square(new float[]{
						-0.25f, 0.05f, 0.0f,  // top left
						-0.25f, 0.04f, 0.0f,  // bottom left
						-0.13f, 0.04f, 0.0f,  // bottom right
						-0.13f, 0.05f, 0.0f   // top right
		});

		firstLine.draw();
		secondLine.draw();

		firstSquare.draw();
	}

	public void drawLetterL() {
		Line firstLine = new Line(new float[]{
						-0.46f, 0.1f,
						-0.46f, -0.0f
		});
		Square secondLine = new Square(new float[]{
						-0.46f, 0.01f, 0.0f,  // top left
						-0.46f, 0.00f, 0.0f,  // bottom left
						-0.35f, 0.00f, 0.0f,  // bottom right
						-0.35f, 0.01f, 0.0f   // top right
		});

		firstLine.draw();

		secondLine.draw();

	}
}
