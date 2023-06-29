package nine.main;

import nine.buffer.Buffer;
import nine.buffer.FloatArrayBuffer;
import nine.buffer.FlowToBuffer;
import nine.buffer.IntegerArrayBuffer;
import nine.buffer.RangeBuffer;
import nine.collection.NormalsFlow;
import nine.collection.UnwrapFlow;
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
        for(int i = 0; i < positions.length; i++) positions[i] -= 0.5f; 
        
        int[] indices =
        {
            0, 1, 2, // bottom
            2, 3, 0,

            6, 5, 4, // top
            7, 6, 4,

            0, 4, 5, // back
            5, 1, 0,

            2, 6, 7, // front
            7, 3, 2,

            0, 3, 7, // left
            7, 4, 0,

            1, 5, 6, // right
            6, 2, 1
        };
        Buffer<Float> unwrap = new FlowToBuffer<Float>(
            new UnwrapFlow(
                new FloatArrayBuffer(positions),
                new IntegerArrayBuffer(indices),
                3));

        Buffer<Float> normals = new FlowToBuffer<Float>(new NormalsFlow(unwrap));

		drawing = gl.vao(new RangeBuffer(indices.length))
			.attribute(3, unwrap)
			.attribute(2, unwrap)
            .attribute(3, normals).drawing();
    }

    @Override
    public void draw()
    {
        drawing.draw();
    }
}