package nine.math;

public class Matrix4fLerp implements Matrix4f
{
    Matrix4f a;
    Matrix4f b;
    ValueFloat t;
    
    public Matrix4fLerp(Matrix4f a, Matrix4f b, ValueFloat t)
    {
        this.a = a;
        this.b = b;
        this.t = t;
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        a.accept(ea -> b.accept(eb -> t.accept(t -> acceptor.call(index ->
        {
            float a = ea.at(index);
            float b = eb.at(index);
            return a + (b - a) * t;
        }))));
    }
}