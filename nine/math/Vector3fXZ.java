package nine.math;

public class Vector3fXZ implements Vector3f
{
    Vector2f source;

    public Vector3fXZ(Vector2f source)
    {
        this.source = source;
    }

    @Override
    public void accept(Vector3fAcceptor acceptor)
    {
        source.accept((x, y) -> acceptor.call(x, 0f, y));
    }
}