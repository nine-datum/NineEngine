package nine.math;

public class Matrix4fIdentity implements Matrix4f, Elements
{
    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        acceptor.call(this);
    }

    @Override
    public float at(int index)
    {
        switch(index)
        {
            case 0:
            case 5:
            case 10:
            case 15:
                return 1f;
            default:
                return 0f;
        }
    }
}