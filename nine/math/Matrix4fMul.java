package nine.math;

public class Matrix4fMul implements Matrix4f
{
    private Matrix4f a;
    private Matrix4f b;
    
    public Matrix4fMul(Matrix4f a, Matrix4f b)
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        a.accept(ea -> b.accept(eb ->
        {
            acceptor.call(index ->
            {
                int i = index & 3;
                int j = index >> 2;
                int startA = i;
                int startB = j << 2;

                return ea.at(startA) * eb.at(startB) +
                        ea.at(startA + 4) * eb.at(startB + 1) +
                        ea.at(startA + 8) * eb.at(startB + 2) +
                        ea.at(startA + 12) * eb.at(startB + 3);
            });
        }));
    }
}