package nine.main;

import nine.function.FunctionDouble;
import nine.math.Matrix4f;
import nine.math.Matrix4fTransform;
import nine.math.ValueBoolean;
import nine.math.ValueFloat;
import nine.math.Vector2f;
import nine.math.Vector3f;
import nine.opengl.Drawing;

public class PlayerDrawing implements Drawing
{
    Drawing drawing;

    public PlayerDrawing(Vector2f movement, Vector2f position, Drawing idle, Drawing walk, FunctionDouble<Matrix4f, Drawing, Drawing> transformedDrawing)
    {
        ValueFloat yAngle = 
            ValueFloat.newFloat((float)Math.PI * -0.5f).sub(
            ValueFloat.vector2fAngle(new LastNotZeroVector2f(movement)));
        ValueBoolean moved = action -> movement.accept((x, y) -> action.call(x != 0 || y != 0));
        Matrix4f transform = new Matrix4fTransform(
            Vector3f.newXZ(position),
            Vector3f.newY(yAngle)
        );
        drawing = transformedDrawing.call(transform, () ->
        {
            moved.accept(m ->
            {
                if(m) walk.draw();
                else idle.draw();
            });
        });
    }

    @Override
    public void draw()
    {
        drawing.draw();
    }
}