package nine.main;

import nine.function.FunctionDouble;
import nine.geometry.collada.Skeleton;
import nine.math.Matrix4f;
import nine.math.ValueBoolean;
import nine.math.ValueFloat;
import nine.math.Vector2f;
import nine.math.Vector3f;
import nine.opengl.Drawing;

public class PlayerDrawing implements Drawing
{
    Drawing drawing;

    public PlayerDrawing(Vector2f movement, Vector2f position, Skeleton idle, Skeleton walk, FunctionDouble<Matrix4f, Skeleton, Drawing> transformedDrawing)
    {
        ValueFloat yAngle = 
            ValueFloat.of((float)Math.PI * -0.5f).sub(
            ValueFloat.vector2fAngle(new LastNotZeroVector2f(movement)));
        ValueBoolean moved = action -> movement.accept((x, y) -> action.call(x != 0 || y != 0));
        Matrix4f transform = Matrix4f.transform(
            Vector3f.newXZ(position),
            Vector3f.newY(yAngle)
        );
        var walkDrawing = transformedDrawing.call(transform, walk);
        var idleDrawing = transformedDrawing.call(transform, idle);
        drawing = () ->
        {
            moved.accept(m ->
            {
                if(m) walkDrawing.draw();
                else idleDrawing.draw();
            });
        };
    }

    @Override
    public void draw()
    {
        drawing.draw();
    }
}