package nine.lwjgl;

import org.lwjgl.glfw.GLFW;

import nine.function.RefreshStatus;
import nine.function.Refreshable;
import nine.input.Mouse;
import nine.math.Vector2f;

public class LWJGL_Mouse implements Mouse
{
    long windowHandle;
    double[] posXBuffer = new double[1];
    double[] posYBuffer = new double[1];
    float posX;
    float posY;
    float lastPosX;
    float lastPosY;
    boolean initialized;
    Refreshable refresh;

    public LWJGL_Mouse(long windowHandle, RefreshStatus refreshStatus)
    {
        this.windowHandle = windowHandle;
        this.refresh = refreshStatus.make();
    }

    void update()
    {
        if(refresh.mark())
        {
            GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            GLFW.glfwGetCursorPos(windowHandle, posXBuffer, posYBuffer);
            lastPosX = posX;
            lastPosY = posY;
            posX = (float)posXBuffer[0];
            posY = (float)posYBuffer[0];
            if(!initialized)
            {
                lastPosX = posX;
                lastPosY = posY;
                initialized = true;
            }
        }
    }

    @Override
    public Vector2f position()
    {
        return p ->
        {
            update();
            p.call(posX, posY);
        };
    }

    @Override
    public Vector2f delta()
    {
        return d ->
        {
            update();
            d.call(posX - lastPosX, posY - lastPosY);
        };
    }
}
