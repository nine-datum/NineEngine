package nine.math;

public class Matrix4fTransponed implements Matrix4f
{
    Matrix4f source;

    public Matrix4fTransponed(Matrix4f source)
    {
        this.source = source;
    }

    @Override
    public void accept(ElementsAcceptor acceptor) 
    {
        source.accept(e -> acceptor.call(i -> e.at((i >> 2) + (i & 3) * 4)));
    }
}