package nine.drawing;

import nine.math.Matrix4f;
import nine.opengl.Drawing;
import nine.opengl.ShaderPlayer;

public class TransformedDrawing implements Drawing
{
    Drawing drawing;

    public TransformedDrawing(Matrix4f transform, ShaderPlayer shader, Drawing drawing)
    {
        var uniform = shader.uniforms().uniformMatrix("transform");
        this.drawing = shader.play(() ->
        {
            uniform.load(transform);
            drawing.draw();
        });
    }

    @Override
    public void draw()
    {
        drawing.draw();
    }
}