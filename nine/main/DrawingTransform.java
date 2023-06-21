package nine.main;

import nine.math.Matrix4f;
import nine.opengl.Drawing;

public interface DrawingTransform
{
    DrawingTransform transform(Matrix4f matrix);
    void draw(Drawing drawing);
}