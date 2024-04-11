package nine.math;

public class Circle
{
    public final Vector2f center;
    public final float radius;

    private Circle(Vector2f center, float radius)
    {
        this.center = center;
        this.radius = radius;
    }

    public static Circle fromCenterRadius(Vector2f center, float radius)
    {
        return new Circle(center, radius);
    }
}