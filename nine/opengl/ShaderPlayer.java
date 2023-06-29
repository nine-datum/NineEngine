package nine.opengl;

public interface ShaderPlayer
{
    ShaderPlayer uniforms(UniformBinding handler);
    Drawing play(Drawing drawing);
}