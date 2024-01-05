package nine.main;

import nine.math.Matrix4f;
import nine.math.Vector3f;
import nine.opengl.Drawing;

public interface TransformedDrawing
{
    Drawing transform(Matrix4f projection, Vector3f worldLight, Matrix4f root);
}