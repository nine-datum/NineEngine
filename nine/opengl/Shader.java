package nine.opengl;

import nine.io.Storage;
import nine.opengl.shader.FileShaderSource;
import nine.opengl.shader.ShaderVersionMacro;

public interface Shader
{
    ShaderPlayer player();

    static ShaderLoader loader(Storage storage, OpenGL gl)
    {
        return (vertex, fragment) -> gl.compiler().createProgram(
			new FileShaderSource(storage.open(vertex), new ShaderVersionMacro("400")),
			new FileShaderSource(storage.open(fragment), new ShaderVersionMacro("400")), acceptor ->
		{
			acceptor.call(0, "position");
			acceptor.call(1, "texcoord");
			acceptor.call(2, "normal");
		});
    }
}