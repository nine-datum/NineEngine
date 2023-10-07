package nine.math;

public interface Rectf
{
    void accept(RectfAcceptor acceptor);

    public static Rectf fromLocationSize(Vector2f location, Vector2f size)
    {
        return action -> location.accept((x, y) -> size.accept((w, h) -> action.call(x, y, w, h)));
    }
    public static Rectf fromLocationSize(float x, float y, float w, float h)
    {
        return new RectfStruct(x, y, w, h);
    }
    default Rectf normalized()
    {
        return new RectfNormalized(this);
    }
    default Vector2f center()
    {
        return action -> accept((x, y, w, h) -> action.call(x + w * 0.5f, y + h * 0.5f));
    }
    default Vector2f size()
    {
        return action -> accept((x, y, w, h) -> action.call(w, h));
    }
    default Vector2f location()
    {
        return action -> accept((x, y, w, h) -> action.call(x, y));
    }
    default ValueBoolean contains(Vector2f point)
    {
        return action -> accept((x, y, w, h) -> point.accept((px, py) ->
        {
            action.call(px >= x && py >= y && px < (x + w) && py < (y + h));
        }));
    }
}