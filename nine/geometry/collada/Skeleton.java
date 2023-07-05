package nine.geometry.collada;

import nine.math.Matrix4f;

public interface Skeleton
{
    Matrix4f transform(String bone);
}