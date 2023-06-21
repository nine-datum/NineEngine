package nine.math;

public final class ValueFloatDegreesToRadians implements ValueFloat
{
    ValueFloat degrees;

    public ValueFloatDegreesToRadians(ValueFloat degrees)
    {
        this.degrees = degrees;
    }
    public ValueFloatDegreesToRadians(float degrees)
    {
        this.degrees = new ValueFloatStruct(degrees);
    }

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        degrees.accept(degrees -> acceptor.call(degrees * (float)Math.PI * (1f / 180f)));
    }
}