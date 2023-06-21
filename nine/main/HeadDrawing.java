package nine.main;

import nine.opengl.Drawing;
import nine.opengl.OpenGL;

public class HeadDrawing implements Drawing
{
	Drawing drawing;

	public HeadDrawing(OpenGL gl)
	{
		float[] positions =
        {
            -0.5f, 0f, -3f,
            0.5f, 0f, -3f,
            1.5f, 1f, 0f,
            2.5f, 5f, -3f,
            1.5f, 6f, 0f,
            -1.5f, 6f, 0f,
            -2.5f, 5f, -3f,
            -1.5f, 1f, 0f,
            -0.5f, 2f, 3f,
            0.5f, 2f, 3f,
            0.5f, 4f, 3f,
            -0.5f, 5f, 3f
        };

		float[] uvs = new float[positions.length / 3 * 2];
		for(int i = 0; i < uvs.length; i++) uvs[i] = (1f / uvs.length) * i;

        int[] indices =
        {
            0,6,3,
            3,1,0,

            1,3,2,
            0,7,6,

            6,5,4,
            4,3,6,

            5,6,11,
            3,4,10,

            4,5,11,
            11,10,4,

            6,7,8,
            8,11,6,
            3,10,9,
            9,2,3,

            10,11,8,
            8,9,10,

            8,7,0,
            1,2,9,
            9,8,0,
            0,1,9
        };
		drawing = gl.vao(indices)
			.attribute(3, positions)
			.attribute(2, uvs).drawing();
	}

	@Override
	public void draw()
	{
		drawing.draw();
	}
}