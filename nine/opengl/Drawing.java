package nine.opengl;

import nine.drawing.TransformedDrawing;
import nine.math.Matrix4f;

public interface Drawing
{
    void draw();

    static Drawing empty()
    {
        return () -> { };
    }

    default Drawing transform(Matrix4f transform, ShaderPlayer shader)
    {
        return new TransformedDrawing(transform, shader, this);
    }
}