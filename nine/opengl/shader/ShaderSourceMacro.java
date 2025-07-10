package nine.opengl.shader;

public class ShaderSourceMacro implements ShaderMacro
{
    String file;

    public ShaderSourceMacro(String file)
    {
        this.file = file;
    }

    @Override
    public void edit(ShaderEditableSource source)
    {
        source.prependStart(String.format("// %s", file));
    }
}
