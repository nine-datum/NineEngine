package nine.math;

public class LocalTime implements FloatFunc
{
    FloatFunc start;
    FloatFunc time;

    public LocalTime()
    {
        time = new Time();
        start = FloatFunc.of(time.value());
    }

    @Override
    public float value()
    {
        return time.value() - start.value();
    }
}