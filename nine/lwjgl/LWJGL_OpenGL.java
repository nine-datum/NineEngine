package nine.lwjgl;

import nine.opengl.DrawingBuffer;
import nine.opengl.OpenGL;
import nine.opengl.ShaderCompiler;

public class LWJGL_OpenGL implements OpenGL
{
    @Override
    public DrawingBuffer vao(int[] elements)
    {
        return new LWJGL_DrawingBuffer(elements);
    }

    @Override
    public ShaderCompiler compiler()
    {
        return new LWJGL_ShaderCompiler();
    }
}