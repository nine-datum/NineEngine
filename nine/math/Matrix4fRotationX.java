package nine.math;

public class Matrix4fRotationX implements Matrix4f
{
    ValueFloat sin;
    ValueFloat cos;
    
    public Matrix4fRotationX(ValueFloat rotation)
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
                case 0: return 1;
                case 5: return cos;
                case 6: return sin;
                case 9: return -sin;
                case 10: return cos;
                case 15: return 1f;
                default: return 0f;
            }
        })));
    }
}