package nine.math;

public class Vector3fXY implements Vector3f
{
    Vector2f source;

    public Vector3fXY(Vector2f source)
    {
        this.source = source;
    }

    @Override
    public void accept(Vector3fAcceptor acceptor)
    {
        source.accept((x, y) -> acceptor.call(x, y, 0f));
    }
}