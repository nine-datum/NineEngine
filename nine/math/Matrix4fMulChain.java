package nine.math;

public class Matrix4fMulChain implements Matrix4f
{
    private Matrix4f result;

    public Matrix4fMulChain(Matrix4f... chain)
    {
        if(chain.length == 0) result = Matrix4fIdentity.identity;
        else
        {
            result = chain[0];
            for(int i = 1; i < chain.length; i++) result = new Matrix4fMul(result, chain[i]);
        }
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        result.accept(acceptor);
    }
}