package nine.math;

public class Matrix4fRotationY implements Matrix4f
{
    ValueFloat sin;
    ValueFloat cos;
    
    public Matrix4fRotationY(ValueFloat rotation)
    {
        this.sin = rotation.sin();
        this.cos = rotation.cos();
    }
    
    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        sin.accept(sin -> cos.accept(cos -> acceptor.call(index ->
        {
            switch(index)
            {
                case 0: return cos;
                case 2: return -sin;
                case 8: return sin;
                case 10: return cos;
                case 5:
                case 15: return 1f;
                default: return 0f;
            }
        })));
    }
}