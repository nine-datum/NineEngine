package nine.geometry.collada;

import nine.geometry.Animator;

public interface ColladaSkeletonParser
{
    void read(ColladaNode node, Animator animator, SkeletonReader reader);
}