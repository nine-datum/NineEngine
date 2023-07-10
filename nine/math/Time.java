package nine.math;

public class Time implements ValueFloat
{
    @Override
    public void accept(FloatAcceptor acceptor)
    {
        long masked = System.currentTimeMillis() % 86_400_000;
        acceptor.call((masked * 0.001f));
    }
}