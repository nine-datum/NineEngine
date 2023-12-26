package nine.geometry.collada;

import java.util.HashMap;

import nine.function.RefreshStatus;
import nine.math.ValueFloat;

public interface AnimatedSkeleton
{
    Skeleton animate(ValueFloat time);

    static AnimatedSkeleton fromCollada(ColladaNode node, RefreshStatus refresh)
    {
        return fromCollada(node, new ColladaBasicAnimationParser(), new ColladaBasicSkeletonParser(), refresh);
    }
    static AnimatedSkeleton fromCollada(ColladaNode node, ColladaAnimationParser animationParser, ColladaSkeletonParser skeletonParser, RefreshStatus refresh)
    {
        HashMap<String, Animation> animations = new HashMap<>();
        AnimatedSkeleton[] skeletonBox = { null };
        animationParser.read(node, animations::put);
        skeletonParser.read(node, animations::get, refresh, (id, skeleton) ->
        {
            skeletonBox[0] = skeleton;
        });
        return skeletonBox[0];
    }
}