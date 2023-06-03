package nine.math;

public class Matrix4fScale implements Matrix4f
{
    Vector3f scale;
    
    public Matrix4fScale(Vector3f scale)
    {
        this.scale = scale;
    }
    
    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        scale.accept((x, y, z) -> acceptor.call(index ->
        {
            switch(index)
            {
                case 0: return x;
                case 5: return y;
                case 10: return z;
                case 15: return 1f;
                default: return 0f;
            }
        }));
    }
}