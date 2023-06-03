package nine.lwjgl;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.lwjgl.opengl.GL20;

import nine.drawing.Color;
import nine.drawing.ColorFloatStruct;
import nine.function.ActionSingle;
import nine.math.Matrix4f;
import nine.math.Vector3f;
import nine.opengl.Shader;
import nine.opengl.ShaderAttribute;
import nine.opengl.ShaderCompiler;
import nine.opengl.ShaderSource;
import nine.opengl.Uniforms;

public class LWJGL_ShaderCompiler implements ShaderCompiler
{
    interface FunctionThrows<TResult, TError extends Throwable>
    {
        TResult call() throws TError;
    }
    interface FunctionSingle<TArg, TResult>
    {
        TResult call(TArg arg);
    }
    interface Function<TResult>
    {
        TResult call();
    }
    class FunctionThrowsHandler<TResult, TError extends Throwable> implements Function<TResult>
    {
        FunctionThrows<TResult, TError> function;
        FunctionSingle<Throwable, TResult> errorCase;

        public FunctionThrowsHandler(FunctionThrows<TResult, TError> function, FunctionSingle<Throwable, TResult> errorCase)
        {
            this.function = function;
            this.errorCase = errorCase;
        }

        @Override
        public TResult call()
        {
            try
            {
                return function.call();
            }
            catch(Throwable error)
            {
                return errorCase.call(error);
            }
        }
    }

    private int loadShaderSubprogram(String source, int type)
    {
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, source);
        GL20.glCompileShader(shaderID);

        IntBuffer compileStatus = IntBuffer.allocate(1);
        GL20.glGetShaderiv(shaderID, GL20.GL_COMPILE_STATUS, compileStatus);
        if (compileStatus.get(0) == 0)
        {
            IntBuffer length = IntBuffer.allocate(1);
            ByteBuffer message = ByteBuffer.allocate(2048);
            GL20.glGetShaderInfoLog(shaderID, length, message);

            String[] lines = source.split("\n");
            System.out.println(new Exception(String.format("Shader compile error\n%s\n%s", new FunctionThrowsHandler<String, UnsupportedEncodingException>(() -> new String(message.array(), "UTF-8"), error -> "encoding error").call(),
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
            public void play(ActionSingle<Uniforms> action)
            {
                GL20.glUseProgram(program);
                action.call(new Uniforms()
                {
                    @Override
                    public void loadUniformMatrix(String name, Matrix4f matrix)
                    {
                        matrix.accept(elements ->
                        {
                            float[] buffer = new float[16];
                            for(int i = 0; i < 16; i++) buffer[i] = elements.at(i);
                            GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(program, name), false, buffer);
                        });
                    }

                    @Override
                    public void loadUniformVector(String name, Vector3f vector)
                    {
                        vector.accept((x, y, z) ->
                        {
                            GL20.glUniform3f(GL20.glGetUniformLocation(program, name), x, y, z);
                        });
                    }

                    @Override
                    public void loadUniformColor(String name, Color color)
                    {
                        new ColorFloatStruct(color).acceptFloats((r, g, b, a) ->
                        {
                            GL20.glUniform4f(GL20.glGetUniformLocation(program, name), r, g, b, a);
                        });
                    }
                });
                GL20.glUseProgram(0);
            }
        };
    }
}