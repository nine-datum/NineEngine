package nine.geometry;

import nine.opengl.Drawing;

public interface SkinnedModel
{
    Drawing instance(Skeleton skinAnimation, Skeleton objectsAnimation, Drawing initializer);
}