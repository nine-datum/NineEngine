package nine.drawing;

import nine.math.Matrix4f;
import nine.opengl.Drawing;
import nine.opengl.ShaderPlayer;

public class TransformedDrawing implements Drawing
{
    Drawing drawing;

    public TransformedDrawing(Matrix4f transform, ShaderPlayer shader, Drawing drawing)
    {
        ShaderPlayer player = shader.uniforms(u -> u.uniformMatrix("transform", transform));
        this.drawing = player.play(drawing);
    }

    @Override
    public void draw()
    {
        drawing.draw();
    }
}