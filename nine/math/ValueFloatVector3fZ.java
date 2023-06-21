package nine.math;

public class ValueFloatVector3fZ implements ValueFloat
{
    Vector3f v;

    public ValueFloatVector3fZ(Vector3f v)
    {
        this.v = v;
    }

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        v.accept((x, y, z) -> acceptor.call(z));
    }
}

