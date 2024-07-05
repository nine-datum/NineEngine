package nine.geometry;

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
        	var anim = map.get(bone);
            var m = anim == null ? null : anim.animate(time);
            return m == null ? Matrix4f.identity : m;
        };
    }
}