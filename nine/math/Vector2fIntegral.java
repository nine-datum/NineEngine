package nine.math;

import nine.function.RefreshStatus;
import nine.function.Refreshable;

public class Vector2fIntegral implements Vector2f
{
    float x, y;
    Vector2f delta;
    Refreshable refresh;
    Vector2fFunction function;

    public Vector2fIntegral(Vector2f delta, Vector2fFunction function, RefreshStatus status)
    {
        this.delta = delta;
        this.function = function;
        refresh = status.make();
    }

    @Override
    public void accept(XYAction acceptor)
    {
        if(refresh.mark())
        {
            delta.accept((x, y) ->
            {
                this.x += x;
                this.y += y;
            });
            function.call(x, y, (x, y) ->
            {
                this.x = x;
                this.y = y;
            });
        }
        acceptor.call(x, y);
    }
}