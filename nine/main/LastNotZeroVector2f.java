package nine.main;

import nine.math.Vector2f;

public class LastNotZeroVector2f implements Vector2f
{
    Vector2f source;
    float x;
    float y = 1f;

    public LastNotZeroVector2f(Vector2f source)
    {
        this.source = source;
    }

    @Override
    public void accept(XYAction acceptor)
    {
        source.accept((x, y) ->
        {
            final float eps = 0.01f;
            if(Math.abs(x) > eps || Math.abs(y) > eps)
            {
                this.x = x;
                this.y = y;
            }
        });
        acceptor.call(x, y);
    }
}