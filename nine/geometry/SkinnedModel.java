package nine.geometry;

import nine.geometry.collada.Skeleton;
import nine.opengl.Drawing;

public interface SkinnedModel
{
    Drawing instance(Skeleton skinAnimation, Skeleton objectsAnimation);
}