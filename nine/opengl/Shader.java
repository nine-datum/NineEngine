package nine.opengl;

import nine.io.Storage;
import nine.opengl.shader.FileShaderSource;
import nine.opengl.shader.ShaderVersionMacro;
import nine.opengl.shader.ShaderPrecisionMacro;
import nine.opengl.shader.ShaderSourceMacro;

public interface Shader
{
    ShaderPlayer player();

    static ShaderLoader loader(Storage storage, OpenGL gl)
    {
        return (vertex, fragment) -> gl.compiler().createProgram(
			new FileShaderSource(storage.open(vertex),
			  new ShaderVersionMacro("300 es"),
			  new ShaderPrecisionMacro(),
			  new ShaderSourceMacro(vertex)),
			new FileShaderSource(storage.open(fragment),
			  new ShaderVersionMacro("300 es"),
			  new ShaderPrecisionMacro(),
			  new ShaderSourceMacro(fragment)),
		  acceptor ->
		{
			acceptor.call(0, "position");
			acceptor.call(1, "texcoord");
			acceptor.call(2, "normal");
		});
    }
}
