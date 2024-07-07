package nine.geometry;

import java.util.ArrayList;
import java.util.HashMap;

import nine.function.Condition;
import nine.function.Function;
import nine.function.FunctionSingle;
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

    static AnimatedSkeletonSource fromCollada(ColladaNode node)
    {
    	return fromCollada(node, Condition.equality("JOINT"));
    }
    
    static AnimatedSkeletonSource fromCollada(ColladaNode node, Condition<String> boneType)
    {
        return fromCollada(node, new ColladaBasicAnimationParser(), new ColladaBasicSkeletonParser(boneType));
    }
    static AnimatedSkeletonSource fromCollada(ColladaNode node, ColladaAnimationParser animationParser, ColladaSkeletonParser skeletonParser)
    {
        AnimatedSkeletonSource[] skeletonBox = { null };
        ArrayList<Animator> animBox = new ArrayList<>();
        animationParser.read(node, animBox::add);
        Animator animator = (boneId, boneName) -> animBox.size() == 0 ? null : animBox.get(0).animation(boneId, boneName);
        skeletonParser.read(node, animator, (id, skeleton) ->
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