package nine.math;

public class Vector3fCross implements Vector3f
{
    Vector3f a;
    Vector3f b;

    public Vector3fCross(Vector3f a, Vector3f b)
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public void accept(XYZAction acceptor)
    {
        a.accept((ax, ay, az) -> b.accept((bx, by, bz) ->
        {
            acceptor.call(
                ay * bz - az * by,
                az * bx - ax * bz,
                ax * by - ay * bx
            );
        }));
    }
}