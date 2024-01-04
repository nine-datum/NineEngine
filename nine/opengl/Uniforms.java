package nine.opengl;

import nine.buffer.Buffer;
import nine.drawing.Color;
import nine.math.Matrix4f;
import nine.math.Vector3f;

public interface Uniforms
{
    Uniform<Matrix4f> uniformMatrix(String name);
    Uniform<Buffer<Matrix4f>> uniformMatrixArray(String name, int capacity);
    Uniform<Vector3f> uniformVector(String name);
    Uniform<Color> uniformColor(String name);
}