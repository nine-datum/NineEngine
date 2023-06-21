package nine.math;

public class ValueFloatVector3fX implements ValueFloat
{
    Vector3f v;

    public ValueFloatVector3fX(Vector3f v)
    {
        this.v = v;
    }

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        v.accept((x, y, z) -> acceptor.call(x));
    }
}
