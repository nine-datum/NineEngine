package nine.math;

public class LocalTime implements ValueFloat
{
    ValueFloat start;
    ValueFloat time;

    public LocalTime()
    {
        time = new Time();
        start = new ValueFloatStruct(time);
    }

    @Override
    public void accept(FloatAcceptor acceptor)
    {
        time.accept(t -> start.accept(s -> acceptor.call(t - s)));
    }
}