package nine.game;

import nine.input.Keyboard;
import nine.input.Mouse;
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
        this.player = Player.create(graphics, keyboard, mouse);
    }

    public static Scene create(Graphics graphics, Keyboard keyboard, Mouse mouse, Projection projection, Light light)
    {
        return new Scene(graphics, keyboard, mouse, projection, light);
    }

    Light light;
    Projection projection;
    Mouse mouse;
    Player player;
    Vector2f mouseRotation = Vector2f.zero;

    @Override
    public void draw()
    {
        var mouseInput = mouse.delta().mul(0.01f);
        mouseRotation = mouseRotation.add(mouseInput).clampX(-90f, 90f);
        var cameraRotation = Vector3f.newXY(-mouseRotation.y, mouseRotation.x);

        var cameraForward = Matrix4f.rotation(cameraRotation).transformVector(Vector3f.forward);
        var cameraPosition = player.position.add(cameraForward.negative().mul(3f));

        player.update(
            projection.projection(),
            cameraPosition,
            cameraRotation,
            light.light()).draw();
    }
}