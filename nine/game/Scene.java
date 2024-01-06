package nine.game;

import nine.input.Keyboard;
import nine.input.Mouse;
import nine.main.TransformedDrawing;
import nine.math.Matrix4f;
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
        player = Player.create(graphics, keyboard, mouse);
        scene = graphics.model("resources/models/Scenes/Mountains.dae");
    }

    public static Scene create(Graphics graphics, Keyboard keyboard, Mouse mouse, Projection projection, Light light)
    {
        return new Scene(graphics, keyboard, mouse, projection, light);
    }

    Light light;
    Projection projection;
    Mouse mouse;
    Player player;
    TransformedDrawing scene;
    Vector2f mouseRotation = Vector2f.zero;
    Vector3f cameraTarget = Vector3f.up;

    @Override
    public void draw()
    {
        var mouseInput = mouse.delta().mul(0.01f);
        mouseRotation = mouseRotation.add(mouseInput).clampY(-3.14f * 0.5f, 3.14f * 0.5f);
        var cameraRotation = Vector3f.newXY(-mouseRotation.y, mouseRotation.x);

        var cameraForward = Matrix4f.rotation(cameraRotation).transformVector(Vector3f.forward);
        var target = player.position;
        cameraTarget = cameraTarget.lerp(target, 0.1f);
        var cameraPosition = cameraTarget.add(Vector3f.newXYZ(0f, 1.5f, 0f)).add(cameraForward.mul(-3f));

        UpdatedDrawing.of(
            player,
            UpdatedDrawing.ofModel(scene, () -> Matrix4f.scale(Vector3f.newXYZ(10f, 10f, 10f))))
        .update(
            projection.projection(),
            cameraPosition,
            cameraRotation,
            light.light()).draw();
    }
}