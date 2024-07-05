package nine.geometry.collada;

import nine.function.RefreshStatus;
import nine.geometry.Animator;

public interface ColladaSkeletonParser
{
    void read(ColladaNode node, Animator animator, RefreshStatus refresh, SkeletonReader reader);
}