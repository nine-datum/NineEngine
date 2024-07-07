package nine.geometry.collada;

import nine.geometry.AnimationSource;

public interface ColladaBoneReader
{
    void read(String name, AnimationSource transform);
}