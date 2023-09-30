package nine.math;

import nine.function.RefreshStatus;
import nine.function.Refreshable;

public class Vector3fAccumulated implements Vector3f
{
    float x, y, z;
    Vector3f delta;
    Refreshable refresh;
    Vector3fFunction function;

    public Vector3fAccumulated(Vector3f delta, Vector3fFunction function, RefreshStatus status)
    {
        this.delta = delta;
        this.function = function;
        refresh = status.make();
    }

    @Override
    public void accept(Vector3fAcceptor acceptor)
    {
        if(refresh.mark())
        {
            delta.accept((x, y, z) ->
            {
                this.x += x;
                this.y += y;
                this.z += z;
            });
            function.call(x, y, z, (x, y, z) ->
            {
                this.x = x;
                this.y = y;
                this.z = z;
            });
        }
        acceptor.call(x, y, z);
    }
}