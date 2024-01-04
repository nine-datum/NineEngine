package nine.math;

import nine.function.RefreshStatus;
import nine.function.Refreshable;

public class Delta implements FloatFunc
{
    FloatFunc value;
    Refreshable refresh;
    float lastValue;
    float lastDelta;
    boolean initialized;

    public Delta(FloatFunc value, RefreshStatus refreshStatus)
    {
        this.value = value;
        refresh = refreshStatus.make();
    }

    @Override
    public float value()
    {
        if(refresh.mark())
        {
            float v = value.value();
            if(!initialized)
            {
                lastValue = v;
                initialized = true;
            }
            lastDelta = v - lastValue;
            lastValue = v;
        }
        return lastDelta;
    }
}