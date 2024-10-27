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
        keys = new int[512];
        GLFW.glfwSetKeyCallback(window, this::callback);
    }

    void callback(long window, int codepoint, int scancode, int action, int mods)
    {
        if(codepoint >= 0 && codepoint < 512)
        {
            if(codepoint >= 'a' && codepoint <= 'z') codepoint = Character.toUpperCase((char)codepoint);
            switch(action)
            {
                case GLFW.GLFW_PRESS:
                case GLFW.GLFW_REPEAT: keys[codepoint] = DOWN; break;
                case GLFW.GLFW_RELEASE: keys[codepoint] = UP; break;
            }
        }
    }

    public Key keyOfIndex(int index)
    {
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

    public Key escape()
    {
        return keyOfIndex(GLFW.GLFW_KEY_ESCAPE);
    }
    public Key space()
    {
        return keyOfIndex(GLFW.GLFW_KEY_SPACE);
    }
    public Key leftCtrl()
    {
        return keyOfIndex(GLFW.GLFW_KEY_LEFT_CONTROL);
    }
    public Key leftShift()
    {
        return keyOfIndex(GLFW.GLFW_KEY_LEFT_SHIFT);
    }
    public Key leftAlt()
    {
        return keyOfIndex(GLFW.GLFW_KEY_LEFT_ALT);
    }
    public Key backspace()
    {
        return keyOfIndex(GLFW.GLFW_KEY_BACKSPACE);
    }

    @Override
    public Key keyOf(char symbol)
    {
        if(symbol >= 'a' && symbol <= 'z') symbol = Character.toUpperCase(symbol);
        return keyOfIndex((int)symbol);
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