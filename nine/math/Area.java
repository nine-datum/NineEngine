package nine.math;

public class Area
{
    public final Vector3f min;
    public final Vector3f max;

    private Area(Vector3f min, Vector3f max)
    {
        this.min = min;
        this.max = max;
    }

    public static Area minmax(Vector3f min, Vector3f max)
    {
        return new Area(min, max);
    }
    public static Area minmax(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new Area(Vector3f.newXYZ(minX, minY, minZ), Vector3f.newXYZ(maxX, maxY, maxZ));
    }
    public static Area centerSize(Vector3f center, Vector3f size)
    {
        return minmax(
            center.x - size.x * 0.5f,
            center.y - size.y * 0.5f,
            center.z - size.z * 0.5f,
            center.x + size.x * 0.5f,
            center.y + size.y * 0.5f,
            center.z + size.z * 0.5f);
    }
    public boolean contains(Vector3f point)
    {
        return
            point.x >= min.x && point.x < max.x &&
            point.y >= min.y && point.y < max.y &&
            point.z >= min.z && point.z < max.z;
    }
    public boolean intersects(Area area)
    {
        Vector3f c_min = area.min.clamp(min, max);
        Vector3f c_max = area.max.clamp(min, max);
        Vector3f vol = c_min.sub(c_max);
        return vol.x * vol.y * vol.z != 0f;
    }
    public boolean intersects(Sphere sphere)
    {
        return sphere.center.clamp(min, max).sub(sphere.center).length() < sphere.radius;
    }
}
