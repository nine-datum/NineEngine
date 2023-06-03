package nine.opengl.shader;

public interface ShaderEditableSource
{
    void appendStart(String line);
    void prependStart(String line);
}