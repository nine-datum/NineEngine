package nine.main;

import nine.collection.ArrayFlow;
import nine.collection.Flow;
import nine.collection.MapFlow;
import nine.math.Matrix4f;
import nine.math.Matrix4fMul;
import nine.opengl.CompositeDrawing;
import nine.opengl.Drawing;

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
    public Drawing drawing(Matrix4f world)
    {
        return new CompositeDrawing(
            drawing,
            new CompositeDrawing(
                new MapFlow<>(
                    children,
                    c -> c.drawing(new Matrix4fMul(transform, world)))));
    }
}