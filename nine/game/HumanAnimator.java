package nine.game;

import java.util.Map;

import nine.geometry.collada.AnimatedSkeleton;

public interface HumanAnimator
{
    AnimatedSkeleton animation(String name);

    static HumanAnimator of(Map<String, AnimatedSkeleton> map)
    {
        return name ->
        {
            var animation = map.get(name);
            if(animation == null) throw new RuntimeException("Animation %s does not exist".formatted(name));
            return animation;
        };
    }
}