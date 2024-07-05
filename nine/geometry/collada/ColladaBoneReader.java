package nine.geometry.collada;

import nine.geometry.Animation;

public interface ColladaBoneReader
{
    void read(String name, Animation transform);
}