package nine.opengl;

import nine.drawing.Color;
import nine.math.Matrix4f;
import nine.math.Vector3f;

public interface Uniforms
{
    void loadUniformMatrix(String name, Matrix4f matrix);
    void loadUniformVector(String name, Vector3f vector);
    void loadUniformColor(String name, Color color);
}