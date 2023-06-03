package nine.opengl;

public interface ShaderCompiler
{
    Shader createProgram(ShaderSource vertex, ShaderSource fragment, ShaderAttribute attributes);
}