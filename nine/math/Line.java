package nine.math;

public class Line
{
    public final Vector2f a;
    public final Vector2f b;

    private Line(Vector2f a, Vector2f b)
    {
        this.a = a;
        this.b = b;
    }

    public static Line fromAB(Vector2f a, Vector2f b)
    {
        return new Line(a, b);
    }
}