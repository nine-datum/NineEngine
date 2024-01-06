package nine.lwjgl;

import org.lwjgl.glfw.GLFW;

import nine.input.Key;
import nine.input.Keyboard;

public class LWJGL_Keyboard implements Keyboard
{
    int DOWN = 2;
    int UP = 1;
    int NONE = 0;
    int[] keys;

    public LWJGL_Keyboard(long window)
    {
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
        return new Key()
        {
            @Override
            public boolean isDown()
            {
                return keys[index] != NONE;
            }
            @Override
            public boolean isUp()
            {
                return keys[index] == UP;
            }
        };
    }

    @Override
    public void update()
    {
        for(int i = 0; i < keys.length; i++)
        {
            if(keys[i] == UP) keys[i] = NONE;
        }
    }
}