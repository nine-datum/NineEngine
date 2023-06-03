package nine.opengl;

import nine.drawing.Color;
import nine.math.Matrix4f;
import nine.math.Vector3f;

public interface Uniforms
{
    Uniform uniformMatrix(String name, Matrix4f matrix);
    Uniform uniformVector(String name, Vector3f vector);
    Uniform uniformColor(String name, Color color);
}