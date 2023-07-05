package nine.geometry.collada;

import nine.math.Matrix4f;
import nine.math.ValueFloat;

public interface Animation
{
    Matrix4f animate(ValueFloat time);
}