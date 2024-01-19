package nine.math;

public class Sphere
{
    public final Vector3f center;
    public final float radius;

    private Sphere(Vector3f center, float radius)
    {
        this.center = center;
        this.radius = radius;
    }

    public static Sphere of(Vector3f center, float radius)
    {
        return new Sphere(center, radius);
    }
    
    public boolean intersects(Sphere other)
    {
        return other.center.sub(center).length() < (other.radius + radius);
    }
}