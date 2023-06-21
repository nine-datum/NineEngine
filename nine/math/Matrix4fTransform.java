package nine.math;

public class Matrix4fTransform implements Matrix4f
{
    Matrix4f result;

    public Matrix4fTransform(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        result = new Matrix4fMulChain(
            new Matrix4fTranslation(position),
            new Matrix4fRotation(rotation),
            new Matrix4fScale(scale)
        );
    }
    public Matrix4fTransform(Vector3f position, Vector3f rotation)
    {
        result = new Matrix4fMul(
            new Matrix4fTranslation(position),
            new Matrix4fRotation(rotation));
    }


    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        result.accept(acceptor);
    }
}