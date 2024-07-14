package nine.geometry.collada;

import nine.geometry.AnimatedSkeletonSource;

public interface SkeletonReader
{
    void read(String skinId, Iterable<String> bones, AnimatedSkeletonSource skeleton);
}