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

    static Drawing of(Drawing... drawings)
    {
        return new CompositeDrawing(drawings);
    }

    default Drawing transform(Matrix4f transform, ShaderPlayer shader)
    {
        return new TransformedDrawing(transform, shader, this);
    }
}