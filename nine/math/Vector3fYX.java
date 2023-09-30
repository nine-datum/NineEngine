package nine.math;

public class Vector3fYX implements Vector3f
{
    Vector2f source;

    public Vector3fYX(Vector2f source)
    {
        this.source = source;
    }

    @Override
    public void accept(Vector3fAcceptor acceptor)
    {
        source.accept((x, y) -> acceptor.call(y, x, 0f));
    }
}