package nine.game;

import nine.main.TransformedDrawing;

public interface HumanCreateFunction
{
    HumanInstance instance(AnimatedDrawing model,
        TransformedDrawing weapon,
        HumanAnimator animator);
}