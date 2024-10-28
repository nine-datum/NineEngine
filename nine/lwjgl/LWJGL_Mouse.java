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
    final int NONE = 0, DOWN = 1, UP = 2;
    int[] buttons = { NONE, NONE, NONE };
    final int[] buttonCodes = {
        GLFW.GLFW_MOUSE_BUTTON_LEFT,
        GLFW.GLFW_MOUSE_BUTTON_RIGHT,
        GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
    };

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
            for(int i = 0; i < buttons.length; i++)
            {
                boolean pressed = GLFW.glfwGetMouseButton(windowHandle, buttonCodes[i]) == GLFW.GLFW_PRESS;
                switch(buttons[i])
                {
                    case NONE:
                        if (pressed) buttons[i] = DOWN;
                        break;
                    case DOWN:
                        if (!pressed) buttons[i] = UP;
                        break;
                    case UP:
                        buttons[i] = pressed? DOWN : NONE;
                        break;
                }
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
                return buttons[code] == DOWN;
            }

            @Override
            public boolean isUp()
            {
                return buttons[code] == UP;
            }
        };
    }

    @Override
    public Key left()
    {
        return mouseKey(0);
    }
    @Override
    public Key right()
    {
        return mouseKey(1);
    }
    @Override
    public Key middle()
    {
        return mouseKey(2);
    }
}
