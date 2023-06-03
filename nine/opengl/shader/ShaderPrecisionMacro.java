package nine.opengl.shader;

public class ShaderPrecisionMacro implements ShaderMacro
{
    @Override
    public void edit(ShaderEditableSource source)
    {
        source.appendStart("#ifdef GL_ES\nprecision mediump float;\n#endif");
    }
}