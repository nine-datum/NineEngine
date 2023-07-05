package nine.geometry.collada;

public interface ColladaSkeletonParser
{
    void read(ColladaNode node, Animator animator, SkeletonReader reader);
}