package nine.geometry;

import java.util.HashMap;

import nine.function.Condition;
import nine.function.Function;
import nine.function.RefreshStatus;
import nine.geometry.collada.ColladaNode;
import nine.geometry.collada.ColladaBasicAnimationParser;
import nine.geometry.collada.ColladaBasicSkeletonParser;
import nine.geometry.collada.ColladaAnimationParser;
import nine.geometry.collada.ColladaSkeletonParser;
import nine.math.Matrix4f;

public interface AnimatedSkeleton
{
    Skeleton animate(float time);

    static AnimatedSkeleton fromCollada(ColladaNode node, RefreshStatus refresh)
    {
    	return fromCollada(node, Condition.equality("JOINT"), refresh);
    }
    
    static AnimatedSkeleton fromCollada(ColladaNode node, Condition<String> boneType, RefreshStatus refresh)
    {
        return fromCollada(node, new ColladaBasicAnimationParser(), new ColladaBasicSkeletonParser(boneType), refresh);
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
    
    default AnimatedSkeleton combine(AnimatedSkeleton other)
    {
    	return time -> animate(time).combine(other.animate(time));
    }
    default AnimatedSkeleton transform(Function<Matrix4f> matrixFunc)
    {
    	return time -> bone -> matrixFunc.call().mul(animate(time).transform(bone));
    }
    default AnimatedSkeleton transform(Matrix4f matrix)
    {
    	return time -> bone -> matrix.mul(animate(time).transform(bone));
    }
}