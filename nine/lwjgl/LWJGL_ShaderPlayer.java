package nine.lwjgl;

import org.lwjgl.opengl.GL20;

import nine.opengl.CompositeUniform;
import nine.opengl.Drawing;
import nine.opengl.ShaderPlayer;
import nine.opengl.Uniform;
import nine.opengl.UniformBinding;

public class LWJGL_ShaderPlayer implements ShaderPlayer
{
    int program;
    Uniform uniform;

    public LWJGL_ShaderPlayer(int program, Uniform uniform)
    {
        this.program = program;
        this.uniform = uniform;
    }

    @Override
    public ShaderPlayer uniforms(UniformBinding handler)
    {
        Uniform uniform = handler.uniform(new LWJGL_Uniforms(program));
        return new LWJGL_ShaderPlayer(program, new CompositeUniform(this.uniform, uniform));
    }

    @Override
    public Drawing play(Drawing drawing)
    {
        return () ->
        {
            GL20.glUseProgram(program);
            uniform.load();
            drawing.draw();
            GL20.glUseProgram(0);
        };
    }
}