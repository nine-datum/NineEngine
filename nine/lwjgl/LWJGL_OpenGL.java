package nine.lwjgl;

import java.nio.IntBuffer;

import nine.opengl.DrawingBuffer;

public class LWJGL_OpenGL
{
    public DrawingBuffer vao(IntBuffer elements)
    {
        return new LWJGL_DrawingBuffer(elements);
    }
}