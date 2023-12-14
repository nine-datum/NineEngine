package nine.math;

public class LocalTime implements ValueFloat
{
    ValueFloat start;
    ValueFloat time;

    public LocalTime()
    {
        time = new Time();
        start = ValueFloat.of(time.value());
    }

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        time.accept(t -> start.accept(s -> acceptor.call(t - s)));
    }
}