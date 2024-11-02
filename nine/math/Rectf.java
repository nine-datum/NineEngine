package nine.math;

public class Rectf
{
    public final double x, y, w, h;

    private Rectf(double x, double y, double w, double h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void accept(RectfAcceptor acceptor)
    {
        acceptor.call(x, y, w, h);
    }

    public static Rectf fromLocationSize(Vector2f location, Vector2f size)
    {
        return new Rectf(location.x, location.y, size.x, size.y);
    }
    public static Rectf fromLocationSize(double x, double y, double w, double h)
    {
        return new Rectf(x, y, w, h);
    }
    public Rectf normalized()
    {
        double x = this.x;
        double y = this.y;
        double w = this.w;
        double h = this.h;
        if (w < 0)
        {
            x += w;
            w = Math.abs(w);
        }
        if (h < 0)
        {
            y += h;
            h = Math.abs(h);
        }
        return new Rectf(x, y, w, h);
    }
    public Vector2f center()
    {
        return Vector2f.newXY(x + w * 0.5f, y + h * 0.5f);
    }
    public Vector2f size()
    {
        return Vector2f.newXY(w, h);
    }
    public Vector2f location()
    {
        return Vector2f.newXY(x, y);
    }
    public boolean contains(Vector2f point)
    {
        return (point.x >= x && point.y >= y && point.x < (x + w) && point.y < (y + h));
    }
}