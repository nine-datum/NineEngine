package nine.math;

public interface FloatFunc
{
    float value();

    static FloatFunc of(float value)
    {
        return () -> value;
    }
    static float toRadians(float degrees)
    {
        return degrees / 180f * (float)Math.PI;
    }
}