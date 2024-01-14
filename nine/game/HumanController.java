package nine.game;

import nine.function.Function;
import nine.input.Keyboard;
import nine.math.Matrix4f;
import nine.math.Vector2f;
import nine.math.Vector3f;

public interface HumanController
{
    Vector3f movement();
    boolean weapon();
    boolean lightAttack();
    boolean heavyAttack();

    static HumanController empty()
    {
        return new HumanController()
        {
            @Override
            public Vector3f movement()
            {
                return Vector3f.zero;
            }
            @Override
            public boolean weapon()
            {
                return false;
            }
            @Override
            public boolean lightAttack()
            {
                return false;
            }
            @Override
            public boolean heavyAttack()
            {
                return false;
            }
        };
    }

    static HumanController player(Keyboard keyboard, Function<Vector3f> cameraRotation)
    {
        return new HumanController()
        {
            @Override
            public Vector3f movement()
            {
                return Matrix4f.rotationY(cameraRotation.call().y).transformVector(Vector3f.newXZ(Vector2f.wasd(keyboard)));
            }
            @Override
            public boolean weapon()
            {
                return keyboard.keyOf('r').isUp();
            }
            @Override
            public boolean lightAttack()
            {
                return keyboard.keyOf('e').isDown();
            }
            @Override
            public boolean heavyAttack()
            {
                return keyboard.keyOf('q').isDown();
            }  
        };
    }
}