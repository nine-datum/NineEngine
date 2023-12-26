package nine.geometry.collada;

import nine.math.Matrix4f;
import nine.math.Matrix4fIdentity;
import nine.math.ValueFloat;

public interface Animation
{
    Matrix4f animate(ValueFloat time);

    static final Animation none = time -> Matrix4fIdentity.identity;
}