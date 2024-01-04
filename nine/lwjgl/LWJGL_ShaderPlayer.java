package nine.lwjgl;

import org.lwjgl.opengl.GL20;

import nine.opengl.Drawing;
import nine.opengl.ShaderPlayer;
import nine.opengl.Uniforms;

public class LWJGL_ShaderPlayer implements ShaderPlayer
{
    int program;

    public LWJGL_ShaderPlayer(int program)
    {
        this.program = program;
    }

    @Override
    public Uniforms uniforms()
    {
        return new LWJGL_Uniforms(program);
    }

    @Override
    public Drawing play(Drawing drawing)
    {
        return () ->
        {
            GL20.glUseProgram(program);
            drawing.draw();
            GL20.glUseProgram(0);
        };
    }
}