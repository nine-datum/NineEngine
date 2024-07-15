package nine.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import nine.buffer.Buffer;
import nine.collection.Flow;
import nine.function.Condition;
import nine.function.Function;
import nine.function.RefreshStatus;
import nine.function.UpdateRefreshStatus;
import nine.geometry.AnimatedSkeleton;
import nine.geometry.Animator;
import nine.geometry.MaterialProvider;
import nine.geometry.collada.ColladaBasicSkeletonParser;
import nine.geometry.collada.ColladaBasicSkinParser;
import nine.geometry.collada.ColladaNode;
import nine.io.FileStorage;
import nine.math.FloatFunc;
import nine.math.LocalTime;
import nine.math.Matrix4f;
import nine.math.Vector3f;
import nine.opengl.Drawing;

public class Human implements UpdatedDrawing
{
    Vector3f position;
    Vector3f rotation;
    FloatFunc time;
    FloatFunc deltaTime;
    HumanState state;

    AnimatedDrawing model;
    HumanAnimator animator;

    HumanController controller;
    
    UpdateRefreshStatus refreshStatus = new UpdateRefreshStatus();

    private Human()
    {
    }

    public final static HumanCreateFunction human = (model, animator) -> (controller, position, rotation, time, deltaTime) ->
    {
        var human = new Human();
        human.time = time;
        human.deltaTime = deltaTime;
        human.model = model;
        human.animator = animator;
        human.controller = controller;
        human.position = position;
        human.rotation = Vector3f.newY(rotation);
        human.state = human.states().weaponIdle();
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
        var damageFlight = graphics.animation("resources/models/Knight/DamageFlight.dae");
        var defeated = graphics.animation("resources/models/Knight/Defeated.dae");
        
        var weapon_idle = graphics.animation("resources/models/Knight/Idle.dae", Condition.equality("NODE"));
        var weapon_walk = graphics.animation("resources/models/Knight/Walk.dae", Condition.equality("NODE"));
        var weapon_weaponIdle = graphics.animation("resources/models/Knight/LongSword_Idle.dae", Condition.equality("NODE"));
        var weapon_weaponWalk = graphics.animation("resources/models/Knight/LongSword_Walk.dae", Condition.equality("NODE"));
        var weapon_lightAttack = graphics.animation("resources/models/Knight/LongSword_Attack_Forward.dae", Condition.equality("NODE"));
        var weapon_heavyAttack = graphics.animation("resources/models/Knight/LongSword_Attack_Left.dae", Condition.equality("NODE"));
        var weapon_damageFlight = graphics.animation("resources/models/Knight/DamageFlight.dae", Condition.equality("NODE"));
        var weapon_defeated = graphics.animation("resources/models/Knight/Defeated.dae", Condition.equality("NODE"));
        
        HashSet<String> boneNames = new HashSet<>();
        
        new ColladaBasicSkeletonParser(b -> true).read(ColladaNode.fromFile(new FileStorage().open("resources/models/Knight/LongSword_Idle.dae")),
        		Animator.none,
        		(skinId, bones, source) ->
        {
        	boneNames.addAll(Flow.iterable(bones).collect().toList());
        });
        
        var animator = HumanAnimator.of(Map.ofEntries(
            Map.entry("idle", idle),
            Map.entry("walk", walk),
            Map.entry("weaponIdle", weaponIdle),
            Map.entry("weaponWalk", weaponWalk),
            Map.entry("lightAttack", lightAttack),
            Map.entry("heavyAttack", heavyAttack),
            Map.entry("damageFlight", damageFlight),
            Map.entry("defeated", defeated),
            
            Map.entry("weapon_idle", weapon_idle),
            Map.entry("weapon_walk", weapon_walk),
            Map.entry("weapon_weaponIdle", weapon_weaponIdle),
            Map.entry("weapon_weaponWalk", weapon_weaponWalk),
            Map.entry("weapon_lightAttack", weapon_lightAttack),
            Map.entry("weapon_heavyAttack", weapon_heavyAttack),
            Map.entry("weapon_damageFlight", weapon_damageFlight),
            Map.entry("weapon_defeated", weapon_defeated)
        ));

        return human.instance(
            model,
            (s, r) -> AnimatedSkeleton.cached(animator.animation(s, RefreshStatus.always), boneNames, 30, 1));
    }

    Matrix4f visualRoot()
    {
        return Matrix4f.transform(position, rotation.add(Vector3f.newY(3.14f)));
    }
    Matrix4f gameRoot()
    {
        return Matrix4f.transform(position, rotation);
    }

    @Override
    public Drawing update(Matrix4f projection, Vector3f cameraPosition, Vector3f cameraRotation, Vector3f worldLight, MaterialProvider materials)
    {
        state = state.next();
        refreshStatus.update();
        return state.update(projection, cameraPosition, cameraRotation, worldLight, materials);
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
    	return drawing;
//        Matrix4f swordLocation = Matrix4f.transform(Vector3f.newXYZ(0.3f, -0.2f, 0.8f), Vector3f.newXYZ(3.14f * 0.3f, 0f, 3.14f * 0.5f));
//        var swordDrawing = UpdatedDrawing.ofModel(weapon, () ->
//        {
//            return visualRoot().mul(skeleton.animate(time.value()).transform("Body")).mul(swordLocation);
//        });
//        return UpdatedDrawing.of(drawing, swordDrawing);
    }
    UpdatedDrawing withSwordInHand(UpdatedDrawing drawing, AnimatedSkeleton skeleton, FloatFunc time)
    {
    	return drawing;
//        Matrix4f swordLocation = Matrix4f.identity;
//        var swordDrawing = UpdatedDrawing.ofModel(weapon, () ->
//        {
//            return visualRoot().mul(skeleton.animate(time.value()).transform("WeaponR")).mul(swordLocation);
//        });
//        return UpdatedDrawing.of(drawing, swordDrawing);
    }

    interface WithSwordDrawing
    {
        UpdatedDrawing apply(UpdatedDrawing drawing, AnimatedSkeleton skeleton, FloatFunc time);
    }

//    HumanState transitionState(WithSwordDrawing withSwordFunc, FloatFunc startTime, AnimatedSkeleton start, AnimatedSkeleton end, Function<HumanState> destination, float length)
//    {
//        var time = new LocalTime();
//        AnimatedSkeleton skeleton = t ->
//        {
//            var lerp = t / length;
//            return name -> start.animate(startTime.value()).transform(name).lerp(end.animate(0f).transform(name), lerp);
//        };
//        var drawing = withSwordFunc.apply(UpdatedDrawing.ofModel(model, skeleton, weaponObjects, time, Human.this::visualRoot), skeleton, time);
//        return HumanState.ofDrawing(drawing, self ->
//        {
//            if(time.value() < length) return self;
//            return destination.call();
//        });
//    }

    HumanStates states()
    {
        return new HumanStates()
        {
            @Override
            public HumanState walk()
            {
                var walk = animator.animation("walk", refreshStatus);
                var weaponObjects = animator.animation("weapon_walk", refreshStatus);
                
                var drawing = withSwordOnBack(
                    UpdatedDrawing.ofModel(model, walk, weaponObjects, time, Human.this::visualRoot),
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
                var idle = animator.animation("idle", refreshStatus);
                var weaponObjects = animator.animation("weapon_idle", refreshStatus);
                var drawing = withSwordOnBack(
                    UpdatedDrawing.ofModel(model, idle, weaponObjects, time, Human.this::visualRoot),
                    idle,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    if(controller.heavyAttack()) return damageFlight();
                    if(controller.weapon()) return weaponIdle();
                    return updateIdle(self, this::walk);
                });
            }

            @Override
            public HumanState attackLight()
            {
                var lightAttack = animator.animation("lightAttack", refreshStatus);
                var weaponObjects = animator.animation("weapon_lightAttack", refreshStatus);
                var time = new LocalTime();
                var drawing = withSwordInHand(
                    UpdatedDrawing.ofModel(model, lightAttack, weaponObjects, time, Human.this::visualRoot),
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
                var heavyAttack = animator.animation("heavyAttack", refreshStatus);
                var weaponObjects = animator.animation("weapon_heavyAttack", refreshStatus);
                var time = new LocalTime();
                var drawing = withSwordInHand(
                    UpdatedDrawing.ofModel(model, heavyAttack, weaponObjects, time, Human.this::visualRoot),
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
                var weaponWalk = animator.animation("weaponWalk", refreshStatus);
                var weaponObjects = animator.animation("weapon_weaponWalk", refreshStatus);
                var time = new LocalTime();
                var drawing = withSwordInHand(
                    UpdatedDrawing.ofModel(model, weaponWalk, weaponObjects, time, Human.this::visualRoot),
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
                var weaponIdle = animator.animation("weaponIdle", refreshStatus);
                var weaponObjects = animator.animation("weapon_weaponIdle", refreshStatus);
                var time = new LocalTime();
                var drawing = withSwordInHand(
                    UpdatedDrawing.ofModel(model, weaponIdle, weaponObjects, time, Human.this::visualRoot),
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

            @Override
            public HumanState damageFlight()
            {
                var damageFlight = animator.animation("damageFlight", refreshStatus);
                var weaponObjects = animator.animation("weapon_damageFlight", refreshStatus);
                
                var time = new LocalTime();
                var drawing = withSwordOnBack(
                    UpdatedDrawing.ofModel(model, damageFlight, weaponObjects, time, Human.this::visualRoot),
                    damageFlight,
                    time);
                return HumanState.ofDrawing(drawing, self ->
                {
                    float t = time.value();
                    float l = 1f;
                    if(t < l)
                    {
                        float timeLeft = l - t;
                        var back = gameRoot().transformVector(Vector3f.newZ(-deltaTime.value() * 5f));
                        position = position.add(back).withY((float)Math.sqrt(timeLeft));
                        return self;
                    }
                    return defeated();
                });
            }

            @Override
            public HumanState defeated()
            {
                var defeated = animator.animation("defeated", refreshStatus);
                var weaponObjects = animator.animation("weapon_defeated", refreshStatus);
                
                var time = new LocalTime();
                var drawing = withSwordOnBack(
                    UpdatedDrawing.ofModel(model, defeated, weaponObjects, time, Human.this::visualRoot),
                    defeated,
                    time);
                return HumanState.ofDrawing(drawing, self -> self);
            }
        };
    }
}