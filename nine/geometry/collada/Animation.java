package nine.geometry.collada;

import nine.math.Matrix4f;

public interface Animation
{
    Matrix4f animate(float time);

    static final Animation none = time -> Matrix4f.identity;
}