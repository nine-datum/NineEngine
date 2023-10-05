package nine.geometry.collada;

import java.util.HashMap;

import nine.function.RefreshStatus;
import nine.math.Matrix4f;

public class AnimationFromColladaNode implements Skeleton
{
    Skeleton skeleton;

    public AnimationFromColladaNode(ColladaNode node, ColladaAnimationParser animationParser, ColladaSkeletonParser skeletonParser, RefreshStatus refresh)
    {
        HashMap<String, Animation> animations = new HashMap<>();
        animationParser.read(node, animations::put);
        skeletonParser.read(node, animations::get, refresh, (id, skeleton) ->
        {
            this.skeleton = skeleton;
        });
    }
    public AnimationFromColladaNode(ColladaNode node, RefreshStatus refresh)
    {
        this(node, new ColladaBasicAnimationParser(), new ColladaBasicSkeletonParser(), refresh);
    }
    @Override
    public Matrix4f transform(String bone)
    {
        return skeleton.transform(bone);
    }
}