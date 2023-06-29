package nine.lwjgl;

import org.lwjgl.opengl.GL20;

import nine.opengl.Drawing;
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

    @Override
    public Drawing depthOn(Drawing drawing)
    {
        return () ->
        {
            GL20.glEnable(GL20.GL_DEPTH);
            drawing.draw();
            GL20.glDisable(GL20.GL_DEPTH);
        };
    }
}