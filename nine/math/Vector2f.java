package nine.math;

import nine.function.RefreshStatus;

public interface Vector2f
{
    public interface XYAction
    {
        void call(float x, float y);
    }

    void accept(XYAction acceptor);

    default Vector2f print()
    {
        return action -> accept((x, y) ->
        {
            System.out.printf("%f, %f\n", x, y);
            action.call(x, y);
        });
    }
    default Vector2f clampLength(ValueFloat limit)
    {
        ValueFloat length = length();
        Vector2f clamped = normalized().mul(limit);
        return a -> limit.accept(lim -> length.accept(len ->
        {
            if (len > lim) clamped.accept(a);
            else accept(a);
        }));
    }
    default Vector2f clampRect(Rectf rect)
    {
        return a -> accept((sx, sy) -> rect.accept((x, y, w, h) ->
        {
            float rx = sx;
            float ry = sy;
            if (rx < x) rx = x;
            else if (rx > (x + w)) rx = x + w;
            if (ry < y) ry = y;
            else if (ry > (y + h)) ry = y + h;
            a.call(rx, ry);
        }));
    }
    default ValueFloat length()
    {
        return action -> accept((x, y) -> action.call((float)Math.sqrt(x * x + y * y)));
    }
    default Vector2f normalized()
    {
        return new Vector2fNormalized(this);
    }
    default ValueFloat dot(Vector2f b)
    {
        return action -> accept((ax, ay) -> b.accept((bx, by) -> action.call(ax * bx + ay * by)));
    }
    default Vector2f negative()
    {
        return action -> accept((x, y) -> action.call(-x, -y));
    }
    default Vector2f mul(Vector2f b)
    {
        return action -> accept((ax, ay) -> b.accept((bx, by) -> action.call(ax * bx, ay * by)));
    }
    default Vector2f div(Vector2f b)
    {
        return action -> accept((ax, ay) -> b.accept((bx, by) -> action.call(ax / bx, ay / by)));
    }
    default Vector2f mul(ValueFloat f)
    {
        return action -> accept((x, y) -> f.accept(m -> action.call(x * m, y * m)));
    }
    default Vector2f div(ValueFloat f)
    {
        return action -> accept((x, y) -> f.accept(m -> action.call(x / m, y / m)));
    }
    default Vector2f sub(Vector2f b)
    {
        return action -> accept((ax, ay) -> b.accept((bx, by) -> action.call(ax - bx, ay - by)));
    }
    default Vector2f add(Vector2f v)
    {
        return action -> accept((ax, ay) -> v.accept((bx, by) -> action.call(ax + bx, ay + by)));
    }
    default ValueFloat x()
    {
        return action -> accept((x, y) -> action.call(x));
    }
    default ValueFloat y()
    {
        return action -> accept((x, y) -> action.call(y));
    }
    default Vector2f rotate(ValueFloat angle)
    {
        return newXY(angle.cos(), angle.sin()).mul(x()).add(
            newXY(angle.sin().negative(), angle.cos()).mul(y()));
    }
    default Vector2f cached(RefreshStatus refreshStatus)
    {
        return new Vector2fRefreshable(this, refreshStatus);
    }
    default Vector2f integral(Vector2fFunction function, RefreshStatus refreshStatus)
    {
        return new Vector2fIntegral(this, function, refreshStatus);
    }
    default int compareLength(Vector2f v)
    {
        float[] buffer = new float[2];
        length().accept(l -> buffer[0] = l);
        v.length().accept(l -> buffer[1] = l);
        return Float.compare(buffer[0], buffer[1]);
    }
    public static Vector2f newXY(ValueFloat x, ValueFloat y)
    {
        return action -> x.accept(vx -> y.accept(vy -> action.call(vx, vy)));
    }
    public static Vector2f newXY(float x, float y)
    {
        return new Vector2fStruct(x, y);
    }
}