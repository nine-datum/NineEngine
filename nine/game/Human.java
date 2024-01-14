package nine.game;

import java.util.Map;

import nine.function.Function;
import nine.geometry.collada.AnimatedSkeleton;
import nine.main.TransformedDrawing;
import nine.math.FloatFunc;
import nine.math.LocalTime;
import nine.math.Matrix4f;
import nine.math.Time;
import nine.math.Vector3f;
import nine.opengl.Drawing;

public class Human implements UpdatedDrawing
{
    Vector3f position;
    Vector3f rotation;
    FloatFunc time = new Time();
    FloatFunc deltaTime = () -> 1f / 60f;
    HumanState state;

    AnimatedDrawing model;
    TransformedDrawing weapon;
    HumanAnimator animator;

    HumanController controller;

    private Human()
    {
    }

    public final static HumanCreateFunction human = (model, weapon, animator) -> (controller, position, rotation) ->
    {
        var human = new Human();
        human.model = model;
        human.weapon = weapon;
        human.animator = animator;
        human.controller = controller;
        human.position = position;
        human.rotation = Vector3f.newY(rotation);
        human.state = human.states().idle();
        return human;
    };

    public static HumanInstance knight(Graphics graphics)
    {
        var model = graphics.animatedModel("resources/models/Knight/LongSword_Idle.dae");
        var idle = graphics.animation("resources/models/Knight/Idle.dae");
        var walk = graphics.animation("resources/models/Knight/Walk.dae");
        var weaponIdle = graphics.animation("resources/models/Knight/LongSword_Idle.dae");
        var weaponWalk = graphics.animation("resources/models/Knight/LongSword_Walk.dae");
        var lightAttack = graphics.animation("resources/models/Knight/LongSword_Attack_Forward.dae");
        var heavyAttack = graphics.animation("resources/models/Knight/LongSword_Attack_Left.dae");
        var weapon = graphics.model("resources/models/Weapons/LongSword.dae");
        
        var animator = HumanAnimator.of(Map.ofEntries(
            Map.entry("idle", idle),
            Map.entry("walk", walk),
            Map.entry("weaponIdle", weaponIdle),
            Map.entry("weaponWalk", weaponWalk),
            Map.entry("lightAttack", lightAttack),
            Map.entry("heavyAttack", heavyAttack)
        ));

        return human.instance(
            model,
            weapon,
            animator);
    }

    Matrix4f root()
    {
        return Matrix4f.transform(position, rotation.add(Vector3f.newY(3.14f)));
    }

    @Override
    public Drawing update(Matrix4f projection, Vector3f cameraPosition, Vector3f cameraRotation, Vector3f worldLight)
    {
        state = state.next();
        return state.update(projection, cameraPosition, cameraRotation, worldLight);
    }

    HumanState updateWalk(float movementSpeed, HumanState self, Function<HumanState> idle)
    {
        Vector3f m = controller.movement().normalized();
        if(m.x == 0 && m.z == 0) return idle.call();

        position = position.add(m.mul(deltaTime.value() * movementSpeed));
        rotation = Vector3f.newY(-m.xz().angle() + FloatFunc.toRadians(90));
        return self;
    }

    HumanState updateIdle(HumanState self, Function<HumanState> walk)
    {
        Vector3f m = controller.movement();
        if(m.x != 0 || m.z != 0) return walk.call();
        return self;
    }

    UpdatedDrawing withSwordOnBack(UpdatedDrawing drawing, AnimatedSkeleton skeleton, FloatFunc time)
    {
        Matrix4f swordLocation = Matrix4f.transform(Vector3f.newXYZ(0.3f, -0.2f, 0.8f), Vector3f.newXYZ(3.14f * 0.3f, 0f, 3.14f * 0.5f));
        var swordDrawing = UpdatedDrawing.ofModel(weapon, () ->
        {
            return root().mul(skeleton.animate(time.value()).transform("Body")).mul(swordLocation);
        });
        return UpdatedDrawing.of(drawing, swordDrawing);
    }
    UpdatedDrawing withSwordInHand(UpdatedDrawing drawing, AnimatedSkeleton skeleton, FloatFunc time)
    {
        Matrix4f swordLocation = Matrix4f.identity;
        var swordDrawing = UpdatedDrawing.ofModel(weapon, () ->
        {
            return root().mul(skeleton.animate(time.value()).transform("WeaponR")).mul(swordLocation);
        });
        return UpdatedDrawing.of(drawing, swordDrawing);
    }

    interface WithSwordDrawing
    {
        UpdatedDrawing apply(UpdatedDrawing drawing, AnimatedSkeleton skeleton, FloatFunc time);
    }

    HumanState transitionState(WithSwordDrawing withSwordFunc, FloatFunc startTime, AnimatedSkeleton start, AnimatedSkeleton end, Function<HumanState> destination, float length)
    {
        var time = new LocalTime();
        AnimatedSkeleton skeleton = t ->
        {
            var lerp = t / length;
            return name -> start.animate(startTime.value()).transform(name).lerp(end.animate(0f).transform(name), lerp);
        };
        var drawing = withSwordFunc.apply(UpdatedDrawing.ofModel(model, skeleton, time, Human.this::root), skeleton, time);
        return HumanState.ofDrawing(drawing, self ->
        {
            if(time.value() < length) return self;
            return destination.call();
        });
    }

    HumanStates states()
    {
        return new HumanStates()
        {
            @Override
            public HumanState walk()
            {
                var walk = animator.animation("walk");
                var drawing = withSwordOnBack(
                    UpdatedDrawing.ofModel(model, walk, time, Human.this::root),
                    walk,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    if(controller.weapon()) return weaponWalk();
                    return updateWalk(3f, self, this::idle);
                });
            }

            @Override
            public HumanState idle()
            {
                var idle = animator.animation("idle");
                var drawing = withSwordOnBack(
                    UpdatedDrawing.ofModel(model, idle, time, Human.this::root),
                    idle,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    if(controller.weapon()) return weaponIdle();
                    return updateIdle(self, this::walk);
                });
            }

            @Override
            public HumanState attackLight()
            {
                var lightAttack = animator.animation("lightAttack");
                var time = new LocalTime();
                var drawing = withSwordInHand(
                    UpdatedDrawing.ofModel(model, lightAttack, time, Human.this::root),
                    lightAttack,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    var direction = Matrix4f.rotation(rotation).transformVector(Vector3f.forward);
                    position = position.add(direction.mul(deltaTime.value()));

                    if(time.value() > 1.2f) return weaponIdle();
                    return self;
                });
            }

            @Override
            public HumanState attackHeavy()
            {
                var heavyAttack = animator.animation("heavyAttack");
                var time = new LocalTime();
                var drawing = withSwordInHand(
                    UpdatedDrawing.ofModel(model, heavyAttack, time, Human.this::root),
                    heavyAttack,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    float jumpStart = 0.3f;
                    float jumpEnd = 1.2f;
                    float t = time.value();
                    if(t > jumpStart && t < jumpEnd)
                    {
                        var direction = Matrix4f.rotation(rotation).transformVector(Vector3f.forward);
                        position = position.add(direction.mul(deltaTime.value() * 4f));
                    }
                    if(t > 2f)
                    {
                        position = position.withY(0f);
                        return weaponIdle();
                    }
                    return self;
                });
            }

            @Override
            public HumanState weaponWalk()
            {
                var weaponWalk = animator.animation("weaponWalk");
                var time = new LocalTime();
                var drawing = withSwordInHand(
                    UpdatedDrawing.ofModel(model, weaponWalk, time, Human.this::root),
                    weaponWalk,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    if(controller.weapon()) return walk();
                    if(controller.lightAttack()) return attackLight();
                    if(controller.heavyAttack()) return attackHeavy();
                    return updateWalk(5f, self, this::weaponIdle);
                });
            }

            @Override
            public HumanState weaponIdle()
            {
                var weaponIdle = animator.animation("weaponIdle");
                var time = new LocalTime();
                var drawing = withSwordInHand(
                    UpdatedDrawing.ofModel(model, weaponIdle, time, Human.this::root),
                    weaponIdle,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    if(controller.weapon()) return idle();
                    if(controller.lightAttack()) return attackLight();
                    if(controller.heavyAttack()) return attackHeavy();
                    return updateIdle(self, this::weaponWalk);
                });
            }
        };
    }
}