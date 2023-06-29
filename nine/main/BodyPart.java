package nine.main;

import nine.collection.ArrayFlow;
import nine.collection.Flow;
import nine.collection.MapFlow;
import nine.math.Matrix4f;
import nine.math.Matrix4fMul;
import nine.math.Matrix4fMulChain;
import nine.opengl.CompositeDrawing;
import nine.opengl.Drawing;
import nine.opengl.ShaderPlayer;

public class BodyPart
{
    Flow<BodyPart> children;
    Matrix4f transform;
    Matrix4f local;
    Drawing drawing;

    public BodyPart(Matrix4f transform, Matrix4f local, Drawing drawing, BodyPart... children)
    {
        this.transform = transform;
        this.children = new ArrayFlow<BodyPart>(children);
        this.drawing = drawing;
        this.local = local;
    }
    public Drawing drawing(ShaderPlayer shader, Matrix4f world)
    {
        ShaderPlayer player = shader.uniforms(u -> u.uniformMatrix("transform", new Matrix4fMulChain(world, transform, local)));
        return new CompositeDrawing(
            player.play(drawing),
            new CompositeDrawing(
                new MapFlow<>(
                    children,
                    c -> c.drawing(shader, new Matrix4fMul(world, transform)))));
    }
}