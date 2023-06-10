package nine.math;

public final class Vector3fX implements Vector3f
{
    ValueFloat x;

    public Vector3fX(ValueFloat x)
    {
        this.x = x;
    }

    @Override
    public void accept(Vector3fAcceptor acceptor)
    {
        x.accept(x -> acceptor.call(x, 0f, 0f));
    }
}