package nine.math;

public final class ValueFloatPI implements ValueFloat
{
    public static final ValueFloat instance = new ValueFloatPI();

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        acceptor.call((float)Math.PI);
    }
}