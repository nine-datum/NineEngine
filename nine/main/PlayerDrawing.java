package nine.main;

import nine.function.FunctionDouble;
import nine.math.Matrix4f;
import nine.math.Matrix4fTransform;
import nine.math.ValueBoolean;
import nine.math.ValueFloat;
import nine.math.ValueFloatStruct;
import nine.math.ValueFloatSub;
import nine.math.ValueFloatVector2fAngle;
import nine.math.Vector2f;
import nine.math.Vector3fXZ;
import nine.math.Vector3fY;
import nine.opengl.Drawing;

public class PlayerDrawing implements Drawing
{
    Drawing drawing;

    public PlayerDrawing(Vector2f movement, Vector2f position, Drawing idle, Drawing walk, FunctionDouble<Matrix4f, Drawing, Drawing> transformedDrawing)
    {
        ValueFloat yAngle = new ValueFloatSub(
            new ValueFloatStruct((float)Math.PI * -0.5f),
            new ValueFloatVector2fAngle(new LastNotZeroVector2f(movement)));
        ValueBoolean moved = action -> movement.accept((x, y) -> action.call(x != 0 || y != 0));
        Matrix4f transform = new Matrix4fTransform(
            new Vector3fXZ(position),
            new Vector3fY(yAngle)
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