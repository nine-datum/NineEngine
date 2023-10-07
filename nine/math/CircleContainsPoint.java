package nine.math;

public final class CircleContainsPoint implements ValueBoolean
{
    private ValueFloat delta;
    private ValueFloat radius;
    
    public CircleContainsPoint(Vector2f center, ValueFloat radius, Vector2f point)
    {
        this.radius = radius;
        this.delta = point.sub(center).length();
    }

    @Override
    public void accept(BoolAcceptor acceptor)
    {
        delta.accept(d -> radius.accept(r ->
        {
            acceptor.call(d <= r);
        }));
    }
}