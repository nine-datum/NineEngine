package nine.geometry.collada;

import nine.geometry.Animation;

public interface ColladaAnimationReader
{
    void read(String bone, Animation animation);
}