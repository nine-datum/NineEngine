package nine.lwjgl;

import org.lwjgl.opengl.GL20;

import nine.buffer.Buffer;
import nine.drawing.Color;
import nine.drawing.ColorFloatStruct;
import nine.math.Matrix4f;
import nine.math.Vector3f;
import nine.opengl.Uniform;
import nine.opengl.Uniforms;

public class LWJGL_Uniforms implements Uniforms
{
    int program;

    public LWJGL_Uniforms(int program)
    {
        this.program = program;
    }

    @Override
    public Uniform<Matrix4f> uniformMatrix(String name)
    {
        int location = GL20.glGetUniformLocation(program, name);
        float[] buffer = new float[16];
        return matrix ->
        {
            for(int i = 0; i < 16; i++) buffer[i] = matrix.at(i);
            GL20.glUniformMatrix4fv(location, false, buffer);
        };
    }

    @Override
    public Uniform<Buffer<Matrix4f>> uniformMatrixArray(String name, int capacity)
    {
        int location = GL20.glGetUniformLocation(program, name);        
        float[] buffer = new float[16 * capacity];
        return matrices ->
        {
            int length = matrices.length();
            for(int m = 0; m < length; m++)
            {
                int mat = m;
                var matrix = matrices.at(m);
                for(int i = 0; i < 16; i++) buffer[mat * 16 + i] = matrix.at(i);
            }
            GL20.glUniformMatrix4fv(location, false, buffer);
        };
    }

    @Override
    public Uniform<Vector3f> uniformVector(String name)
    {
        int location = GL20.glGetUniformLocation(program, name);
        return vector ->
        {
            GL20.glUniform3f(location, vector.x, vector.y, vector.z);
        };
    }

    @Override
    public Uniform<Color> uniformColor(String name)
    {
        int location = GL20.glGetUniformLocation(program, name);
        return color -> new ColorFloatStruct(color).acceptFloats((r, g, b, a) ->
        {
            GL20.glUniform4f(location, r, g, b, a);
        });
    }
}