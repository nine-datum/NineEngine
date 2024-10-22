package nine.lwjgl;

import org.lwjgl.glfw.GLFW;

import nine.function.RefreshStatus;
import nine.function.Refreshable;
import nine.input.Key;
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
        update(false);
    }

    void update(boolean lockCursor)
    {
        if(refresh.mark())
        {
            if (lockCursor)
            {
                GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            }
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
        update();
        return Vector2f.newXY(posX, posY);
    }

    @Override
    public Vector2f delta()
    {
        update();
        return Vector2f.newXY(posX - lastPosX, posY - lastPosY);
    }

    Key mouseKey(int code)
    {
        return new Key()
        {
            @Override
            public boolean isDown()
            {
                return GLFW.glfwGetMouseButton(windowHandle, code) == GLFW.GLFW_PRESS;
            }

            @Override
            public boolean isUp()
            {
                return GLFW.glfwGetMouseButton(windowHandle, code) == GLFW.GLFW_RELEASE;
            }
        };
    }

    @Override
    public Key left()
    {
        return mouseKey(GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }
    @Override
    public Key right()
    {
        return mouseKey(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    }
    @Override
    public Key middle()
    {
        return mouseKey(GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
    }
}
