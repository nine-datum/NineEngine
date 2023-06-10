package nine.math;

public final class Vector3fY implements Vector3f
{
    ValueFloat y;

    public Vector3fY(ValueFloat y)
    {
        this.y = y;
    }

    @Override
    public void accept(Vector3fAcceptor acceptor)
    {
        y.accept(y -> acceptor.call(0f, y, 0f));
    }
}