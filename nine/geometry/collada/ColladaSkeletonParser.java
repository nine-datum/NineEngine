package nine.geometry.collada;

import nine.function.RefreshStatus;

public interface ColladaSkeletonParser
{
    void read(ColladaNode node, Animator animator, RefreshStatus refresh, SkeletonReader reader);
}