package nine.math;

public interface Vector2fFunction
{
    public static final Vector2fFunction identity = (x, y, action) -> action.call(x, y);

    void call(double x, double y, Vector2fAcceptor acceptor);
}