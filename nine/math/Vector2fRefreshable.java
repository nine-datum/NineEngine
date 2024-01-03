package nine.math;

import nine.function.RefreshStatus;
import nine.function.Refreshable;

public class Vector2fRefreshable implements Vector2f
{
    Vector2f source;
    float x;
    float y;
    Refreshable refresh;

    public Vector2fRefreshable(Vector2f source, RefreshStatus status)
    {
        refresh = status.make();
        this.source = source;
    }

    @Override
    public Vector2f cached(RefreshStatus status)
    {
        return source.cached(status);
    }

    @Override
    public void accept(XYAction acceptor)
    {
        if(refresh.mark())
        {
            source.accept((x, y) ->
            {
                this.x = x;
                this.y = y;
                acceptor.call(x, y);
            });
        }
        else
        {
            acceptor.call(x, y);
        }
    }
}