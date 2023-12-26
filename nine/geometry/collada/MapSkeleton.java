package nine.geometry.collada;

import java.util.Map;

public class MapSkeleton implements AnimatedSkeleton
{
    Map<String, Animation> map;

    public MapSkeleton(Map<String, Animation> map)
    {
        this.map = map;
    }

    @Override
    public Animation transform(String bone)
    {
        Animation m = map.get(bone);
        return m == null ? Animation.none : m;
    }
}