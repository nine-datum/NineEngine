package nine.opengl;

import nine.drawing.TransformUniformDrawing;
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

    default Drawing transformUniform(Matrix4f transform, ShaderPlayer shader)
    {
        return new TransformUniformDrawing(transform, shader, this);
    }
}