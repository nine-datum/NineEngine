package nine.opengl.shader;

public class ShaderPrecisionMacro implements ShaderMacro
{
    @Override
    public void edit(ShaderEditableSource source)
    {
        source.appendStart("precision highp float;");
    }
}
