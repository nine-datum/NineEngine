package nine.game;

import java.util.Map;

import nine.function.RefreshStatus;
import nine.geometry.AnimatedSkeleton;
import nine.geometry.AnimatedSkeletonSource;

public interface HumanAnimator
{
    AnimatedSkeleton animation(String name, RefreshStatus refreshStatus);

    static HumanAnimator of(Map<String, AnimatedSkeletonSource> map)
    {
        return (name, status) ->
        {
            var animation = map.get(name);
            if(animation == null) throw new RuntimeException("Animation %s does not exist".formatted(name));
            return animation.instance(status);
        };
    }
}