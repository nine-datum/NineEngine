package nine.geometry.collada;

import nine.geometry.AnimatedSkeleton;

public interface SkeletonReader
{
    void read(String skinId, AnimatedSkeleton skeleton);
}