package nine.main;

import nine.opengl.Drawing;
import nine.opengl.OpenGL;

public class CubeDrawing implements Drawing
{
    Drawing drawing;

    public CubeDrawing(OpenGL gl)
    {
        float[] positions =
        {
            0f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 1f,
            0f, 0f, 1f,

            0f, 1f, 0f,
            1f, 1f, 0f,
            1f, 1f, 1f,
            0f, 1f, 1f
        };
        float[] uvs =
        {
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f,

            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f
        };
        int[] indices =
        {
            0, 2, 1, // bottom
            3, 2, 0,

            4, 5, 6, // top
            6, 7, 4,

            0, 4, 5, // back
            5, 1, 0,

            2, 6, 7, // front
            7, 3, 2,

            0, 7, 3, // left
            0, 4, 7,

            1, 5, 6, // right
            6, 2, 1
        };
        drawing = gl.vao(indices)
            .attribute(3, positions)
            .attribute(2, uvs)
            .drawing();
    }

    @Override
    public void draw()
    {
        drawing.draw();
    }
}