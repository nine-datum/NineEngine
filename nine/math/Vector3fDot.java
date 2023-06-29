package nine.math;

public final class Vector3fDot implements ValueFloat
{
    Vector3f a;
    Vector3f b;

    public Vector3fDot(Vector3f a, Vector3f b)
    {
        this.a = a;
        this.b = b;
    }
    @Override
    public void accept(FloatAcceptor acceptor)
    {
        a.accept((ax, ay, az) -> b.accept((bx, by, bz) ->
        {
            acceptor.call(ax * bx + ay * by + az * bz);
        }));
    }
}