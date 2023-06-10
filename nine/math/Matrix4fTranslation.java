package nine.math;

public class Matrix4fTranslation implements Matrix4f
{
    Vector3f translation;
    
    public Matrix4fTranslation(Vector3f translation)
    {
        this.translation = translation;
    }
    
    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        translation.accept((tx, ty, tz) -> acceptor.call(index ->
        {
            switch(index)
            {
                case 12: return tx;
                case 13: return ty;
                case 14: return tz;
                case 0:
                case 5:
                case 10:
                case 15: return 1f;
                default: return 0f;
            }
        }));
    }
}