package nine.math;

import nine.function.RefreshStatus;
import nine.function.Refreshable;

public class Delta implements ValueFloat
{
    ValueFloat value;
    Refreshable refresh;
    float lastValue;
    float lastDelta;
    boolean initialized;

    public Delta(ValueFloat value, RefreshStatus refreshStatus)
    {
        this.value = value;
        refresh = refreshStatus.make();
    }

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        if(refresh.mark())
        {
            value.accept(v ->
            {
                if(!initialized)
                {
                    lastValue = v;
                    initialized = true;
                }
                lastDelta = v - lastValue;
                lastValue = v;
            });
        }
        acceptor.call(lastDelta);
    }
}