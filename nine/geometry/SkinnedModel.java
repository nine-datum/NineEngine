package nine.geometry;

import nine.math.Matrix4f;
import nine.opengl.Drawing;

public interface SkinnedModel
{
    Drawing instance(Skeleton skinAnimation, Skeleton objectsAnimation, Matrix4f root, MaterialProvider materials, Drawing initializer);
}