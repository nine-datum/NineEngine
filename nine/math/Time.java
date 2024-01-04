package nine.math;

public class Time implements FloatFunc
{
    @Override
    public float value()
    {
        long masked = System.currentTimeMillis() % 86_400_000;
        return (masked * 0.001f);
    }
}