package nine.game;

import nine.function.ActionSingle;
import nine.function.UpdateRefreshStatus;
import nine.geometry.collada.AnimatedSkeleton;
import nine.input.Keyboard;
import nine.input.Mouse;
import nine.math.FloatFunc;
import nine.math.Matrix4f;
import nine.math.Time;
import nine.math.Vector2f;
import nine.math.Vector3f;
import nine.opengl.Drawing;

public class Player implements UpdatedDrawing
{
    Vector3f position = Vector3f.newXYZ(0f, 0f, 2f);
    Vector3f rotation = Vector3f.newXYZ(0f, 0f, 0f);
    Keyboard keyboard;
    Mouse mouse;
    ActionSingle<Vector3f> cameraRotationAction;
    UpdateRefreshStatus updateStatus;
    FloatFunc time;
    FloatFunc deltaTime;
    HumanState state;

    AnimatedDrawing model;
    AnimatedSkeleton idle;
    AnimatedSkeleton walk;
    AnimatedSkeleton lightAttack;
    AnimatedSkeleton heavyAttack;

    private Player(Graphics graphics, Keyboard keyboard, Mouse mouse)
    {
        this.keyboard = keyboard;
        this.mouse = mouse;

        updateStatus = new UpdateRefreshStatus();
        time = new Time();
        deltaTime = time.delta(updateStatus);

        model = graphics.animatedModel("resources/models/Knight/LongSword_Idle.dae");
        idle = graphics.animation("resources/models/Knight/Idle.dae");
        walk = graphics.animation("resources/models/Knight/Walk.dae");

        state = states().idle();
    }

    public static Player create(Graphics graphics, Keyboard keyboard, Mouse mouse)
    {
        return new Player(graphics, keyboard, mouse);
    }

    Matrix4f root()
    {
        return Matrix4f.transform(position, rotation);
    }

    @Override
    public Drawing update(Matrix4f projection, Vector3f cameraPosition, Vector3f cameraRotation, Vector3f worldLight)
    {
        updateStatus.update();
        state = state.next();
        return state.update(projection, cameraPosition, cameraRotation, worldLight);
    }

    HumanStates states()
    {
        return new HumanStates()
        {
            @Override
            public HumanState walk()
            {
                var drawing = UpdatedDrawing.ofModel(model, walk, time, Player.this::root);
                return HumanState.ofDrawing(drawing, self ->
                {
                    Vector3f m = Vector3f.newXZ(Vector2f.wasd(keyboard));
                    if(m.x == 0 && m.z == 0) return idle();

                    position = position.add(m.mul(deltaTime.value() * 3f));
                    rotation = Vector3f.newY(-m.xz().normalized().angle() - FloatFunc.toRadians(90));
                    
                    return self;
                });
            }

            @Override
            public HumanState idle()
            {
                var drawing = UpdatedDrawing.ofModel(model, idle, time, Player.this::root);
                return HumanState.ofDrawing(drawing, self ->
                {
                    Vector3f m = Vector3f.newXZ(Vector2f.wasd(keyboard));
                    if(m.x != 0 || m.z != 0) return walk();
                    return self;
                });
            }

            @Override
            public HumanState attackLight()
            {
                return null;
            }

            @Override
            public HumanState attackHeavy()
            {
                return null;
            }
        };
    }
}