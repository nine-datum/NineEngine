package nine.game;

import nine.function.ActionSingle;
import nine.function.Function;
import nine.function.UpdateRefreshStatus;
import nine.geometry.collada.AnimatedSkeleton;
import nine.input.Keyboard;
import nine.input.Mouse;
import nine.main.TransformedDrawing;
import nine.math.FloatFunc;
import nine.math.LocalTime;
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
    Vector3f cameraRotation = Vector3f.zero;

    AnimatedDrawing model;
    TransformedDrawing sword;
    AnimatedSkeleton idle;
    AnimatedSkeleton walk;
    AnimatedSkeleton weaponIdle;
    AnimatedSkeleton weaponWalk;
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
        weaponIdle = graphics.animation("resources/models/Knight/LongSword_Idle.dae");
        weaponWalk = graphics.animation("resources/models/Knight/LongSword_Walk.dae");
        lightAttack = graphics.animation("resources/models/Knight/LongSword_Attack_Forward.dae");
        heavyAttack = graphics.animation("resources/models/Knight/LongSword_Attack_Left.dae");
        sword = graphics.model("resources/models/Weapons/LongSword.dae");

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
        deltaTime.value();
        this.cameraRotation = cameraRotation;

        state = state.next();
        return state.update(projection, cameraPosition, cameraRotation, worldLight);
    }

    HumanState updateWalk(float movementSpeed, HumanState self, Function<HumanState> idle)
    {
        Vector3f m = Vector3f.newXZ(Vector2f.wasd(keyboard));
        if(m.x == 0 && m.z == 0) return idle.call();
        m = Matrix4f.rotationY(cameraRotation.y).transformVector(m);

        position = position.add(m.mul(deltaTime.value() * 3f));
        rotation = Vector3f.newY(-m.xz().normalized().angle() - FloatFunc.toRadians(90));
        return self;
    }
    HumanState updateIdle(HumanState self, Function<HumanState> walk)
    {
        Vector3f m = Vector3f.newXZ(Vector2f.wasd(keyboard));
        if(m.x != 0 || m.z != 0) return walk.call();
        return self;
    }
    UpdatedDrawing withSwordOnBack(UpdatedDrawing drawing, AnimatedSkeleton skeleton, FloatFunc time)
    {
        Matrix4f swordLocation = Matrix4f.transform(Vector3f.newXYZ(0.3f, -0.2f, 0.8f), Vector3f.newXYZ(3.14f * 0.3f, 0f, 3.14f * 0.5f));
        var swordDrawing = UpdatedDrawing.ofModel(sword, () ->
        {
            return root().mul(skeleton.animate(time.value()).transform("Body")).mul(swordLocation);
        });
        return UpdatedDrawing.of(drawing, swordDrawing);
    }
    UpdatedDrawing withSwordInHand(UpdatedDrawing drawing, AnimatedSkeleton skeleton, FloatFunc time)
    {
        Matrix4f swordLocation = Matrix4f.identity;
        var swordDrawing = UpdatedDrawing.ofModel(sword, () ->
        {
            return root().mul(skeleton.animate(time.value()).transform("WeaponR")).mul(swordLocation);
        });
        return UpdatedDrawing.of(drawing, swordDrawing);
    }

    boolean weaponKeyDown()
    {
        return keyboard.keyOf('r').isUp();
    }

    HumanStates states()
    {
        return new HumanStates()
        {
            @Override
            public HumanState walk()
            {
                var drawing = withSwordOnBack(
                    UpdatedDrawing.ofModel(model, walk, time, Player.this::root),
                    walk,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    if(weaponKeyDown()) return weaponWalk();
                    return updateWalk(3f, self, this::idle);
                });
            }

            @Override
            public HumanState idle()
            {
                var drawing = withSwordOnBack(
                    UpdatedDrawing.ofModel(model, idle, time, Player.this::root),
                    idle,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    if(weaponKeyDown()) return weaponIdle();
                    return updateIdle(self, this::walk);
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

            @Override
            public HumanState weaponWalk()
            {
                var time = new LocalTime();
                var drawing = withSwordInHand(
                    UpdatedDrawing.ofModel(model, weaponWalk, time, Player.this::root),
                    weaponWalk,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    if(weaponKeyDown()) return walk();
                    return updateWalk(3f, self, this::weaponIdle);
                });
            }

            @Override
            public HumanState weaponIdle()
            {
                var time = new LocalTime();
                var drawing = withSwordInHand(
                    UpdatedDrawing.ofModel(model, weaponIdle, time, Player.this::root),
                    weaponIdle,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    if(weaponKeyDown()) return idle();
                    return updateIdle(self, this::weaponWalk);
                });
            }
        };
    }
}