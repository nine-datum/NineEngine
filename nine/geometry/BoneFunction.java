package nine.geometry;

import nine.math.Matrix4f;

public interface BoneFunction
{
    Matrix4f bone(String key, Matrix4f bone);
}