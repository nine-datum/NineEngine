package nine.lwjgl;

import org.lwjgl.opengl.GL20;

import nine.buffer.Buffer;
import nine.opengl.Drawing;
import nine.opengl.DrawingBuffer;
import nine.opengl.OpenGL;
import nine.opengl.ShaderCompiler;

public class LWJGL_OpenGL implements OpenGL
{
    @Override
    public DrawingBuffer vao(Buffer<Integer> elements)
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
            GL20.glEnable(GL20.GL_DEPTH_TEST);
            drawing.draw();
            GL20.glDisable(GL20.GL_DEPTH_TEST);
        };
    }
    @Override
    public Drawing smooth(Drawing drawing)
    {
        return () ->
        {
            GL20.glShadeModel(GL20.GL_SMOOTH);
            drawing.draw();
            GL20.glShadeModel(GL20.GL_FLAT);
        };
    }
    @Override
    public Drawing clockwise(Drawing drawing)
    {
        return () ->
        {
            GL20.glFrontFace(GL20.GL_CW);
            drawing.draw();
            GL20.glFrontFace(GL20.GL_CCW);
        };
    }
}