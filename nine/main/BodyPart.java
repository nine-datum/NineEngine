package nine.main;

import nine.collection.ArrayFlow;
import nine.collection.Flow;
import nine.collection.MapFlow;
import nine.math.Matrix4f;
import nine.math.Matrix4fMul;
import nine.opengl.CompositeDrawing;
import nine.opengl.Drawing;
import nine.opengl.Shader;
import nine.opengl.ShaderPlayer;

public class BodyPart
{
    Flow<BodyPart> children;
    Matrix4f transform;
    Drawing drawing;

    public BodyPart(Matrix4f transform, Drawing drawing, BodyPart... children)
    {
        this.transform = transform;
        this.children = new ArrayFlow<BodyPart>(children);
        this.drawing = drawing;
    }
    public Drawing drawing(Shader shader, Matrix4f world)
    {
        Matrix4f mul = new Matrix4fMul(transform, world);
        ShaderPlayer player = shader.player(u -> u.uniformMatrix("transform", mul));
        return new CompositeDrawing(
            player.play(drawing),
            new CompositeDrawing(
                new MapFlow<>(
                    children,
                    c -> c.drawing(shader, mul))));
    }
}