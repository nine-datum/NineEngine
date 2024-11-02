package nine.math;

import nine.buffer.Buffer;
import nine.input.Key;
import nine.input.Keyboard;

public final class Vector3f
{
    public final double x, y, z;

    private Vector3f(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public interface XYZAction
    {
        void call(double x, double y, double z);
    }

    public void accept(XYZAction acceptor)
    {
        acceptor.call(x, y, z);
    }

    public static final Vector3f zero = new Vector3f(0, 0, 0);
    public static final Vector3f right = new Vector3f(1, 0, 0);
    public static final Vector3f up = new Vector3f(0, 1, 0);
    public static final Vector3f forward = new Vector3f(0, 0, 1);
    public static final Vector3f one = new Vector3f(1, 1, 1);

    public String toString()
    {
        return String.format("%f, %f, %f", x, y, z);
    }

    public Vector3f clampLength(double limit)
    {
        double length = length();
        if (length > limit) return normalized().mul(limit);
        return this;
    }
    public Vector3f clamp(Vector3f min, Vector3f max)
    {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        if(x < min.x) x = min.x;
        if(y < min.y) y = min.y;
        if(z < min.z) z = min.z;
        if(x > max.x) x = max.x;
        if(y > max.y) y = max.y;
        if(z > max.z) z = max.z;
        return new Vector3f(x, y, z);
    }
    public Vector3f cross(Vector3f b)
    {
        return new Vector3f(
            y * b.z - z * b.y,
            z * b.x - x * b.z,
            x * b.y - y * b.x);
    }
    public double length()
    {
        return Math.sqrt(x * x + y * y + z * z);
    }
    public Vector3f normalized()
    {
        double len = length();
        if (len != 0)
            return new Vector3f(x / len, y / len, z / len);
        else
            return new Vector3f(0f, 0f, 0f);
    }
    public double dot(Vector3f b)
    {
        return x * b.x + y * b.y + z * b.z;
    }
    public Vector3f negative()
    {
        return new Vector3f(-x, -y, -z);
    }
    public Vector3f mul(Vector3f b)
    {
        return new Vector3f(x * b.x, y * b.y, z * b.z);
    }
    public Vector3f div(Vector3f b)
    {
        return new Vector3f(x / b.x, y / b.y, z / b.z);
    }
    public Vector3f mul(double f)
    {
        return new Vector3f(x * f, y * f, z * f);
    }
    public Vector3f div(double f)
    {
        if(f == 0) return new Vector3f(0f, 0f, 0f);
        return new Vector3f(x / f, y / f, z / f);
    }
    public Vector3f sub(Vector3f b)
    {
        return new Vector3f(x - b.x, y - b.y, z - b.z);
    }
    public Vector3f add(Vector3f b)
    {
        return new Vector3f(x + b.x, y + b.y, z + b.z);
    }
    public Vector2f xy()
    {
        return Vector2f.newXY(x, y);
    }
    public Vector2f xz()
    {
        return Vector2f.newXY(x, z);
    }
    public Vector3f withX(double x)
    {
        return new Vector3f(x, y, z);
    }
    public Vector3f withY(double y)
    {
        return new Vector3f(x, y, z);
    }
    public Vector3f withZ(double z)
    {
        return new Vector3f(x, y, z);
    }
    public Vector3f lerp(Vector3f b, double t)
    {
        double dx = b.x - x;
        double dy = b.y - y;
        double dz = b.z - z;
        return new Vector3f(x + dx * t, y + dy * t, z + dz * t);
    }
    public static Vector3f newX(double x)
    {
        return new Vector3f(x, 0f, 0f);
    }
    public static Vector3f newY(double y)
    {
        return new Vector3f(0f, y, 0f);
    }
    public static Vector3f newZ(double z)
    {
        return new Vector3f(0f, 0f, z);
    }
    public static Vector3f newXY(double x, double y)
    {
        return new Vector3f(x, y, 0f);
    }
    public static Vector3f newXY(Vector2f xy)
    {
        return new Vector3f(xy.x, xy.y, 0f);
    }
    public static Vector3f newYX(Vector2f yx)
    {
        return new Vector3f(yx.y, yx.x, 0f);
    }
    public static Vector3f newXZ(double x, double z)
    {
        return new Vector3f(x, 0f, z);
    }
    public static Vector3f newXZ(Vector2f xz)
    {
        return new Vector3f(xz.x, 0f, xz.y);
    }
    public static Vector3f newXYZ(double x, double y, double z)
    {
        return new Vector3f(x, y, z);
    }
    public static Vector3f newBuffer(Buffer<Float> buffer, int start)
    {
        return new Vector3f(buffer.at(start), buffer.at(start + 1), buffer.at(start + 2));
    }
    public static Vector3f wasdeq(Keyboard keyboard)
    {
        Key w = keyboard.keyOf('w');
        Key a = keyboard.keyOf('a');
        Key s = keyboard.keyOf('s');
        Key d = keyboard.keyOf('d');
        Key e = keyboard.keyOf('e');
        Key q = keyboard.keyOf('q');
        double x = 0f;
        double y = 0f;
        double z = 0f;
        if(w.isDown()) y++;
        if(s.isDown()) y--;
        if(d.isDown()) x++;
        if(a.isDown()) x--;
        if(e.isDown()) z++;
        if(q.isDown()) z--;
        return new Vector3f(x, y, z);
    }
}