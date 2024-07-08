package nine.geometry;

import nine.math.Matrix4f;

public interface Skeleton
{
    Matrix4f transform(String bone);
    
    default Skeleton combine(Skeleton other)
    {
    	return b -> transform(b).mul(other.transform(b));
    }
    
    public static Skeleton someOf(Iterable<Skeleton> skeletons)
    {
    	return b ->
    	{
    		for(var s : skeletons)
    		{
    			var m = s.transform(b);
    			if(m != null) return m;
    		}
    		return null;
    	};
    }
}