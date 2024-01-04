package nine.geometry.collada;

import java.util.Map;

import nine.math.Matrix4f;

public class MapSkeleton implements AnimatedSkeleton
{
    Map<String, Animation> map;

    public MapSkeleton(Map<String, Animation> map)
    {
        this.map = map;
    }

    @Override
    public Skeleton animate(float time)
    {
        return bone ->
        {
            var m = map.get(bone).animate(time);
            return m == null ? Matrix4f.identity : m;
        };
    }
}