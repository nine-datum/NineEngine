package nine.math;

import nine.function.RefreshStatus;

public interface FloatFunc
{
    float value();

    static FloatFunc of(float value)
    {
        return () -> value;
    }
    static float toRadians(float degrees)
    {
        return degrees / 180f * (float)Math.PI;
    }
    default FloatFunc delta(RefreshStatus status)
    {
        return new Delta(this, status);
    }
    default FloatFunc cached(RefreshStatus status)
    {
    	var r = status.make();
    	float[] cache = { value() };
    	return () ->
    	{
    		if(r.mark())
    		{
    			cache[0] = value();
    		}
    		return cache[0];
    	};
    }
}