package nine.math;

public class Vector2fPrint implements Vector2f
{
    Vector2f source;

    public Vector2fPrint(Vector2f source)
    {
        this.source = source;
    }

    @Override
    public void accept(Vector2fAcceptor acceptor)
    {
        source.accept((x, y) ->
        {
            System.out.printf("%f, %f\n", x, y);
            acceptor.call(x, y);
        });
    }
}