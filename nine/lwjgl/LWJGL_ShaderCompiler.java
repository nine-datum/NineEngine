package nine.lwjgl;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.lwjgl.opengl.GL20;

import nine.drawing.Color;
import nine.drawing.ColorFloatStruct;
import nine.math.Matrix4f;
import nine.math.Vector3f;
import nine.opengl.Drawing;
import nine.opengl.Shader;
import nine.opengl.ShaderAttribute;
import nine.opengl.ShaderCompiler;
import nine.opengl.ShaderSource;
import nine.opengl.Uniform;
import nine.opengl.Uniforms;

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
        vertex.accept(vert -> fragment.accept(frag ->
        {
            GL20.glAttachShader(program, loadShaderSubprogram(vert, GL20.GL_VERTEX_SHADER));
            GL20.glAttachShader(program, loadShaderSubprogram(frag, GL20.GL_FRAGMENT_SHADER));
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
            public void play(Drawing drawing)
            {
                GL20.glUseProgram(program);
                drawing.draw();
                GL20.glUseProgram(0);
            }

            @Override
            public Uniforms uniforms()
            {
                return new Uniforms()
                {
                    @Override
                    public Uniform uniformMatrix(String name, Matrix4f matrix)
                    {
                        int location = GL20.glGetUniformLocation(program, name);
                        return () -> matrix.accept(elements ->
                        {
                            float[] buffer = new float[16];
                            for(int i = 0; i < 16; i++) buffer[i] = elements.at(i);
                            GL20.glUniformMatrix4fv(location, false, buffer);
                        });
                    }

                    @Override
                    public Uniform uniformVector(String name, Vector3f vector)
                    {
                        int location = GL20.glGetUniformLocation(program, name);
                        return () -> vector.accept((x, y, z) ->
                        {
                            GL20.glUniform3f(location, x, y, z);
                        });
                    }

                    @Override
                    public Uniform uniformColor(String name, Color color)
                    {
                        int location = GL20.glGetUniformLocation(program, name);
                        ColorFloatStruct floats = new ColorFloatStruct(color);
                        return () -> floats.acceptFloats((r, g, b, a) ->
                        {
                            GL20.glUniform4f(location, r, g, b, a);
                        });
                    }
                };
            }
        };
    }
}