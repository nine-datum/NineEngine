package nine.opengl;

public interface OpenGL
{
    DrawingBuffer vao(int[] elements);
    ShaderCompiler compiler();
}