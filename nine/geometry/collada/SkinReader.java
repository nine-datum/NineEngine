package nine.geometry.collada;

import nine.buffer.Buffer;
import nine.math.Matrix4f;

public interface SkinReader
{
    void read(String skinId, String geomId, Buffer<String> names, Skeleton inversedBindings, Matrix4f matrix, Buffer<Float> weights, Buffer<Integer> joints, int weightPerIndex);
}