package nine.geometry.collada;

import nine.math.Matrix4f;

public interface ColladaVisualSceneReader
{
    void read(String geomId, Matrix4f root);
}