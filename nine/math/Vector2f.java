package nine.math;

public class Vector2f
{
    public final float x, y;

    private Vector2f(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public interface XYAction
    {
        void call(float x, float y);
    }
    
    public void accept(XYAction acceptor)
    {
        acceptor.call(x, y);
    }
    
    public String toString()
    {
        return String.format("%f, %f", x, y);
    }

    public Vector2f clampLength(float limit)
    {
        float length = length();
        if(length > limit)
        {
            return normalized().mul(length);
        }
        return this;
    }
    public Vector2f clampRect(Rectf rect)
    {
        float rx = x;
        float ry = y;
        if (rx < x) rx = x;
        else if (rx > (x + rect.w)) rx = x + rect.w;
        if (ry < y) ry = y;
        else if (ry > (y + rect.h)) ry = y + rect.h;
        return new Vector2f(rx, ry);
    }
    public float length()
    {
        return (float)Math.sqrt(x * x + y * y);
    }
    public Vector2f normalized()
    {
        var length = length();
        if(length == 0) return new Vector2f(0f, 0f);
        return new Vector2f(x / length, y / length);
    }
    public float dot(Vector2f b)
    {
        return x * b.x + y * b.y;
    }
    public Vector2f negative()
    {
        return new Vector2f(-x, -y);
    }
    public Vector2f mul(Vector2f b)
    {
        return new Vector2f(x * b.x, y * b.y);
    }
    public Vector2f div(Vector2f b)
    {
        return new Vector2f(x / b.x, y / b.y);
    }
    public Vector2f mul(float f)
    {
        return new Vector2f(x * f, y * f);
    }
    public Vector2f div(float f)
    {
        if(f == 0) return new Vector2f(0f, 0f);
        else return new Vector2f(x / f, y / f);
    }
    public Vector2f sub(Vector2f b)
    {
        return new Vector2f(x - b.x, y - b.y);
    }
    public Vector2f add(Vector2f b)
    {
        return new Vector2f(x + b.x, y + b.y);
    }
    public Vector2f rotate(float angle)
    {
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        return new Vector2f(cos * x + sin * y, sin * x + cos * y);
    }
    public float angle()
    {
        if (x == 0f && y == 0f) return 0f;
        else
        {
		    return (float)Math.acos(x) * (y > 0 ? 1f : -1f);
        }
    }
    public Vector2f transformPoint(Matrix3f m)
    {
        float x = this.x * m.at(0) + this.y * m.at(3) + m.at(6);
        float y = this.x * m.at(1) + this.y * m.at(4) + m.at(7);
        return new Vector2f(x, y);
    }
    public int compareLength(Vector2f v)
    {
        return Float.compare(length(), v.length());
    }
    public static Vector2f newXY(float x, float y)
    {
        return new Vector2f(x, y);
    }
}