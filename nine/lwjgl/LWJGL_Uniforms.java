package nine.lwjgl;

import org.lwjgl.opengl.GL20;

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
}