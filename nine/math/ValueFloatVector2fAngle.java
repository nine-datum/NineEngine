package nine.math;

public final class ValueFloatVector2fAngle implements ValueFloat
{
    Vector2f source;

    public ValueFloatVector2fAngle(Vector2f source)
    {
        this.source = new Vector2fNormalized(source);
    }

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        source.accept((x, y) ->
        {
            if (x == 0f && y == 0f) acceptor.call(0f);
            else
            {
			    acceptor.call((float)Math.acos(x) * (y > 0 ? 1f : -1f));
            }
        });
    }
}