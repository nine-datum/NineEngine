package nine.opengl;

public interface ShaderPlayer
{
    Uniforms uniforms();
    Drawing play(Drawing drawing);
    void dispose();
}