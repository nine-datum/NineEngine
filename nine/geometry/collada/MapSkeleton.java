package nine.geometry.collada;

import java.util.Map;

import nine.math.Matrix4f;
import nine.math.Matrix4fIdentity;

public class MapSkeleton implements Skeleton
{
    Map<String, Matrix4f> map;

    public MapSkeleton(Map<String, Matrix4f> map)
    {
        this.map = map;
    }

    @Override
    public Matrix4f transform(String bone)
    {
        Matrix4f m = map.get(bone);
        return m == null ? Matrix4fIdentity.identity : m;
    }
}