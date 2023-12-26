package nine.geometry;

import nine.geometry.collada.Skeleton;

public interface SkinnedModel
{
    Model load(Skeleton animation);
}