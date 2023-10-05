package nine.lwjgl;

import org.lwjgl.glfw.GLFW;

import nine.function.RefreshStatus;
import nine.function.Refreshable;
import nine.input.Key;
import nine.input.Keyboard;

public class LWJGL_Keyboard implements Keyboard
{
    int DOWN = 2;
    int UP = 1;
    int NONE = 0;
    int[] keys;
    RefreshStatus refreshStatus;

    public LWJGL_Keyboard(long window, RefreshStatus refreshStatus)
    {
        this.refreshStatus = refreshStatus;
        keys = new int[256];
        GLFW.glfwSetKeyCallback(window, this::callback);
    }

    void callback(long window, int codepoint, int scancode, int action, int mods)
    {
        if (codepoint == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) GLFW.glfwSetWindowShouldClose(window, true);
        codepoint = Character.getNumericValue(Character.toUpperCase(Character.toChars(codepoint)[0]));
        if(codepoint < 256 && codepoint >= 0)
        {
            switch(action)
            {
                case GLFW.GLFW_PRESS:
                case GLFW.GLFW_REPEAT: keys[codepoint] = DOWN; break;
                case GLFW.GLFW_RELEASE: keys[codepoint] = UP; break;
            }
        }
    }

    @Override
    public Key keyOf(char symbol)
    {
        int index = Character.getNumericValue(Character.toUpperCase(symbol));
        Refreshable refresh = refreshStatus.make();
        return new Key()
        {
            int fresh()
            {
                int value = keys[index];
                if(refresh.mark())
                {
                    if(keys[index] == UP) keys[index] = NONE;
                }
                return value;
            }

            @Override
            public boolean isDown()
            {
                return fresh() != NONE;
            }
            @Override
            public boolean isUp()
            {
                return fresh() == UP;
            }
        };
    }
}