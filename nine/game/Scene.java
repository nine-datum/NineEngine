package nine.game;

import java.util.ArrayList;
import java.util.List;

import nine.function.Condition;
import nine.function.UpdateRefreshStatus;
import nine.geometry.AnimatedSkeleton;
import nine.geometry.MaterialProvider;
import nine.input.Keyboard;
import nine.input.Mouse;
import nine.main.TransformedDrawing;
import nine.math.FloatFunc;
import nine.math.Matrix4f;
import nine.math.Time;
import nine.math.Vector2f;
import nine.math.Vector3f;
import nine.opengl.Drawing;

public class Scene implements Drawing
{
    public interface Light
    {
        Vector3f light();
    }
    public interface Projection
    {
        Matrix4f projection();
    }

    private Scene(Graphics graphics, Keyboard keyboard, Mouse mouse, Projection projection, Light light)
    {
        this.light = light;
        this.projection = projection;
        this.mouse = mouse;
        this.keyboard = keyboard;

        var time = new Time();
        FloatFunc deltaTime = () -> 1f / 60f;

        var knightTemplate = Human.knight(graphics);

        player = knightTemplate.create(
            HumanController.player(keyboard, () -> cameraRotation),
            Vector3f.newXYZ(0f, 0f, 0f),
            0f,
            time,
            deltaTime);

        npcs.add(knightTemplate.create(
            HumanController.empty(),
            Vector3f.newXYZ(0f, 0f, 2f),
            3.14f,
            time,
            deltaTime));
        
        var mountainsFile = "resources/models/Scenes/Mountains.dae";
        var statueFile = "resources/datum/ninja.dae";
        var knightFile = "resources/models/Knight/LongSword_Idle.dae";
        
        materials = MaterialProvider.some(
    		List.of(
        		graphics.materials(mountainsFile),
        		graphics.materials(statueFile),
        		graphics.materials(knightFile)
    		),
    		null
		);

        var mountains = graphics.model(mountainsFile);
        statue = graphics.animatedModel(statueFile);
        statueAnim = graphics.animation(statueFile).instance(refreshStatus);
        statueObjectAnim = graphics.animation(statueFile, Condition.equality("NODE")).instance(refreshStatus);
        scene = mountains;
    }

    public static Scene create(Graphics graphics, Keyboard keyboard, Mouse mouse, Projection projection, Light light)
    {
        return new Scene(graphics, keyboard, mouse, projection, light);
    }

    Light light;
    Projection projection;
    Mouse mouse;
    Keyboard keyboard;
    Human player;
    List<Human> npcs = new ArrayList<Human>();
    TransformedDrawing scene;
    AnimatedDrawing statue;
    AnimatedSkeleton statueAnim;
    AnimatedSkeleton statueObjectAnim;
    UpdateRefreshStatus refreshStatus = new UpdateRefreshStatus();
    MaterialProvider materials;
    
    Vector2f mouseRotation = Vector2f.zero;
    Vector3f cameraRotation = Vector3f.zero;
    Vector3f cameraTarget = Vector3f.up;

    @Override
    public void draw()
    {
        var mouseInput = mouse.delta().mul(0.01f);
        mouseRotation = mouseRotation.add(mouseInput).clampY(-3.14f * 0.5f, 3.14f * 0.5f);
        cameraRotation = Vector3f.newXY(mouseRotation.y, mouseRotation.x);

        var cameraForward = Matrix4f.rotation(cameraRotation).transformVector(Vector3f.forward);
        var target = player.position;
        cameraTarget = cameraTarget.lerp(target, 0.1f);
        var cameraPosition = cameraTarget.add(Vector3f.newXYZ(0f, 1.5f, 0f)).add(cameraForward.mul(-3f));

        UpdatedDrawing.of(
            player,
            UpdatedDrawing.of(npcs.toArray(Human[]::new)),
            UpdatedDrawing.ofModel(statue, statueAnim, statueObjectAnim, new Time(), () -> Matrix4f.translation(Vector3f.newXYZ(0f, 2f, 0f))),
            UpdatedDrawing.ofModel(scene, () -> Matrix4f.scale(Vector3f.newXYZ(10f, 10f, 10f)))
        )
        .update(
            projection.projection(),
            cameraPosition,
            cameraRotation,
            light.light(),
            materials).draw();

            
        keyboard.update();
    }
}