package nine.math;

import nine.buffer.Buffer;
import nine.input.Key;
import nine.input.Keyboard;

public final class Vector3f
{
    public final float x, y, z;

    private Vector3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public interface XYZAction
    {
        void call(float x, float y, float z);
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

    public Vector3f clampLength(float limit)
    {
        float length = length();
        if (length > limit) return normalized().mul(limit);
        return this;
    }
    public Vector3f clamp(Vector3f min, Vector3f max)
    {
        float x = this.x;
        float y = this.y;
        float z = this.z;
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
    public float length()
    {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }
    public Vector3f normalized()
    {
        float len = length();
        if (len != 0)
            return new Vector3f(x / len, y / len, z / len);
        else
            return new Vector3f(0f, 0f, 0f);
    }
    public float dot(Vector3f b)
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
    public Vector3f mul(float f)
    {
        return new Vector3f(x * f, y * f, z * f);
    }
    public Vector3f div(float f)
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
    public Vector3f withX(float x)
    {
        return new Vector3f(x, y, z);
    }
    public Vector3f withY(float y)
    {
        return new Vector3f(x, y, z);
    }
    public Vector3f withZ(float z)
    {
        return new Vector3f(x, y, z);
    }
    public Vector3f lerp(Vector3f b, float t)
    {
        float dx = b.x - x;
        float dy = b.y - y;
        float dz = b.z - z;
        return new Vector3f(x + dx * t, y + dy * t, z + dz * t);
    }
    public static Vector3f newX(float x)
    {
        return new Vector3f(x, 0f, 0f);
    }
    public static Vector3f newY(float y)
    {
        return new Vector3f(0f, y, 0f);
    }
    public static Vector3f newZ(float z)
    {
        return new Vector3f(0f, 0f, z);
    }
    public static Vector3f newXY(float x, float y)
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
    public static Vector3f newXZ(float x, float z)
    {
        return new Vector3f(x, 0f, z);
    }
    public static Vector3f newXZ(Vector2f xz)
    {
        return new Vector3f(xz.x, 0f, xz.y);
    }
    public static Vector3f newXYZ(float x, float y, float z)
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
        float x = 0f;
        float y = 0f;
        float z = 0f;
        if(w.isDown()) y++;
        if(s.isDown()) y--;
        if(d.isDown()) x++;
        if(a.isDown()) x--;
        if(e.isDown()) z++;
        if(q.isDown()) z--;
        return new Vector3f(x, y, z);
    }
}