package nine.math;

import nine.buffer.Buffer;
import nine.function.RefreshStatus;

public interface Vector3f
{
    public interface XYZAction
    {
        void call(float x, float y, float z);
    }

    void accept(XYZAction acceptor);

    default Vector3f print()
    {
        return action -> accept((x, y, z) ->
        {
            System.out.printf("%f, %f, %f\n", x, y, z);
            action.call(x, y, z);
        });
    }
    default Vector3f clampLength(ValueFloat limit)
    {
        ValueFloat length = length();
        Vector3f clamped = normalized().mul(limit);
        return a -> limit.accept(lim -> length.accept(len ->
        {
            if (len > lim) clamped.accept(a);
            else accept(a);
        }));
    }
    default Vector3f cross(Vector3f b)
    {
        return action -> accept((ax, ay, az) -> b.accept((bx, by, bz) ->
        {
            action.call(
                ay * bz - az * by,
                az * bx - ax * bz,
                ax * by - ay * bx
            );
        }));
    }
    default ValueFloat length()
    {
        return action -> accept((x, y, z) -> action.call((float)Math.sqrt(x * x + y * y + z * z)));
    }
    default Vector3f normalized()
    {
        return new Vector3fNormalized(this);
    }
    default ValueFloat dot(Vector3f b)
    {
        return action -> accept((ax, ay, az) -> b.accept((bx, by, bz) -> action.call(ax * bx + ay * by + az * bz)));
    }
    default Vector3f negative()
    {
        return action -> accept((x, y, z) -> action.call(-x, -y, -z));
    }
    default Vector3f mul(Vector3f b)
    {
        return action -> accept((ax, ay, az) -> b.accept((bx, by, bz) -> action.call(ax * bx, ay * by, az * bz)));
    }
    default Vector3f div(Vector3f b)
    {
        return action -> accept((ax, ay, az) -> b.accept((bx, by, bz) -> action.call(ax / bx, ay / by, az / bz)));
    }
    default Vector3f mul(ValueFloat f)
    {
        return action -> accept((x, y, z) -> f.accept(m -> action.call(x * m, y * m, z * m)));
    }
    default Vector3f div(ValueFloat f)
    {
        return action -> accept((x, y, z) -> f.accept(m -> action.call(x / m, y / m, z / m)));
    }
    default Vector3f sub(Vector3f b)
    {
        return action -> accept((ax, ay, az) -> b.accept((bx, by, bz) -> action.call(ax - bx, ay - by, az - bz)));
    }
    default Vector3f add(Vector3f v)
    {
        return action -> accept((ax, ay, az) -> v.accept((bx, by, bz) -> action.call(ax + bx, ay + by, az + bz)));
    }
    default ValueFloat x()
    {
        return action -> accept((x, y, z) -> action.call(x));
    }
    default ValueFloat y()
    {
        return action -> accept((x, y, z) -> action.call(y));
    }
    default ValueFloat z()
    {
        return action -> accept((x, y, z) -> action.call(z));
    }
    default Vector3f integral(Vector3fFunction function, RefreshStatus refreshStatus)
    {
        return new Vector3fIntegral(this, function, refreshStatus);
    }

    public static Vector3f newX(ValueFloat x)
    {
        return action -> x.accept(xv -> action.call(xv, 0f, 0f));
    }
    public static Vector3f newY(ValueFloat y)
    {
        return action -> y.accept(yv -> action.call(0f, yv, 0f));
    }
    public static Vector3f newZ(ValueFloat z)
    {
        return action -> z.accept(zv -> action.call(0f, 0f, zv));
    }
    public static Vector3f newX(float x)
    {
        return action -> action.call(x, 0f, 0f);
    }
    public static Vector3f newY(float y)
    {
        return action -> action.call(0f, y, 0f);
    }
    public static Vector3f newZ(float z)
    {
        return action -> action.call(0f, 0f, z);
    }
    public static Vector3f newXY(ValueFloat x, ValueFloat y)
    {
        return action -> x.accept(xv -> y.accept(yv -> action.call(xv, yv, 0f)));
    }
    public static Vector3f newXY(Vector2f xy)
    {
        return action -> xy.accept((x, y) -> action.call(x, y, 0f));
    }
    public static Vector3f newYX(Vector2f yx)
    {
        return action -> yx.accept((y, x) -> action.call(x, y, 0f));
    }
    public static Vector3f newXZ(ValueFloat x, ValueFloat z)
    {
        return action -> x.accept(xv -> z.accept(zv -> action.call(xv, 0f, zv)));
    }
    public static Vector3f newXZ(Vector2f xz)
    {
        return action -> xz.accept((x, z) -> action.call(x, 0f, z));
    }
    public static Vector3f newXYZ(ValueFloat x, ValueFloat y, ValueFloat z)
    {
        return action -> x.accept(vx -> y.accept(vy -> z.accept(vz -> action.call(vx, vy, vz))));
    }
    public static Vector3f newXYZ(float x, float y, float z)
    {
        return new Vector3fStruct(x, y, z);
    }
    public static Vector3f newBuffer(Buffer<Float> buffer, int start)
    {
        return action -> action.call(buffer.at(start), buffer.at(start + 1), buffer.at(start + 2));
    }
}