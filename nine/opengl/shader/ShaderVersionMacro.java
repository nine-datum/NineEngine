package nine.opengl.shader;

public class ShaderVersionMacro implements ShaderMacro
{
    String version;

    public ShaderVersionMacro(String version)
    {
        this.version = version;
    }

    @Override
    public void edit(ShaderEditableSource source)
    {
        source.prependStart(String.format("#version %s", version));
    }
}