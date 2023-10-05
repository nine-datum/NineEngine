package nine.math;

public interface Vector2fFunction
{
    public static final Vector2fFunction identity = (x, y, action) -> action.call(x, y);

    void call(float x, float y, Vector2fAcceptor acceptor);
}