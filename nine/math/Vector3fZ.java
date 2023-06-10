package nine.math;

public final class Vector3fZ implements Vector3f
{
    ValueFloat z;

    public Vector3fZ(ValueFloat z)
    {
        this.z = z;
    }

    @Override
    public void accept(Vector3fAcceptor acceptor)
    {
        z.accept(z -> acceptor.call(0f, 0f, z));
    }
}