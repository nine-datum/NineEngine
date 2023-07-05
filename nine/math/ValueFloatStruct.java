package nine.math;

public final class ValueFloatStruct implements ValueFloat
{
    public float value;

    public ValueFloatStruct(float value)
    {
        this.value = value;
    }
    public ValueFloatStruct(ValueFloat value)
    {
        value.accept(v -> this.value = v);
    }

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        acceptor.call(value);
    }
}