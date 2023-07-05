package nine.geometry.collada;

import nine.math.Matrix4f;

public interface ColladaBoneReader
{
    void read(String name, Matrix4f transform);
}