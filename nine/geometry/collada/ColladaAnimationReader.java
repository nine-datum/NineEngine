package nine.geometry.collada;

import nine.function.FunctionSingle;
import nine.geometry.Animation;

public interface ColladaAnimationReader
{
    void read(FunctionSingle<String, Animation> animations);
}