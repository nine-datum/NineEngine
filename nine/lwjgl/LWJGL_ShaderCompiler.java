package nine.lwjgl;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.lwjgl.opengl.GL20;

import nine.opengl.Shader;
import nine.opengl.ShaderAttribute;
import nine.opengl.ShaderCompiler;
import nine.opengl.ShaderPlayer;
import nine.opengl.ShaderSource;

public class LWJGL_ShaderCompiler implements ShaderCompiler
{
    private int loadShaderSubprogram(CharSequence source, int type)
    {
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, source);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0)
        {
            String message = GL20.glGetShaderInfoLog(shaderID, 2048);

            String[] lines = source.toString().split("\n");
            System.out.println(new Exception(String.format("Shader compile error\n%s\n%s", message,
                String.join("\n", IntStream.range(0, lines.length).boxed().map(i -> String.format("%d\t%s", i, lines[i])).collect(Collectors.toList())))));
        }
        return shaderID;
    }

    @Override
    public Shader createProgram(ShaderSource vertex, ShaderSource fragment, ShaderAttribute attributes)
    {
        int program = GL20.glCreateProgram();
        int[] vertShader = { 0 };
        int[] fragShader = { 0 };
        vertex.accept(vert -> fragment.accept(frag ->
        {
            GL20.glAttachShader(program, vertShader[0] = loadShaderSubprogram(vert, GL20.GL_VERTEX_SHADER));
            GL20.glAttachShader(program, fragShader[0] = loadShaderSubprogram(frag, GL20.GL_FRAGMENT_SHADER));
        }));
        attributes.accept((index, name) ->
        {
            GL20.glBindAttribLocation(program, index, name);
        });

        GL20.glLinkProgram(program);
        GL20.glValidateProgram(program);

        return new Shader()
        {
            @Override
            public ShaderPlayer player()
            {
                return new LWJGL_ShaderPlayer(program, vertShader[0], fragShader[0]);
            }
        };
    }
}