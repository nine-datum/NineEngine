package nine.game;

import nine.geometry.collada.AnimatedSkeleton;
import nine.main.TransformedDrawing;

public interface HumanCreateFunction
{
    HumanInstance instance(AnimatedDrawing model,
        TransformedDrawing weapon,
        AnimatedSkeleton idle,
        AnimatedSkeleton walk,
        AnimatedSkeleton weaponIdle,
        AnimatedSkeleton weaponWalk,
        AnimatedSkeleton lightAttack,
        AnimatedSkeleton heavyAttack);
}