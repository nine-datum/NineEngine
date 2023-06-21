package nine.math;

public class Matrix4fRotationZ implements Matrix4f
{
    ValueFloat sin;
    ValueFloat cos;
    
    public Matrix4fRotationZ(ValueFloat rotation)
    {
        this.sin = new ValueFloatSin(rotation);
        this.cos = new ValueFloatCos(rotation);
    }
    
    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        sin.accept(sin -> cos.accept(cos -> acceptor.call(index ->
        {
            switch(index)
            {
                case 0: return cos;
                case 1: return sin;
                case 4: return -sin;
                case 5: return cos;
                case 10:
                case 15: return 1f;
                default: return 0f;
            }
        })));
    }
}
