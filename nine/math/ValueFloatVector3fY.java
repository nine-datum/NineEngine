package nine.math;

public class ValueFloatVector3fY implements ValueFloat
{
    Vector3f v;

    public ValueFloatVector3fY(Vector3f v)
    {
        this.v = v;
    }

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        v.accept((x, y, z) -> acceptor.call(y));
    }
}