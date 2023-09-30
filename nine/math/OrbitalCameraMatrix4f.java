package nine.math;

public class OrbitalCameraMatrix4f implements Matrix4f
{
    Matrix4f result;

    public OrbitalCameraMatrix4f(Vector3f position, Vector3f rotation, ValueFloat distance)
    {
        result = new Matrix4fMulChain(
            new Matrix4fTranslation(new Vector3fAdd(position, new Vector3fZ(distance))),
            new Matrix4fRotation(new Vector3fNegative(rotation))
        );
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        result.accept(acceptor);
    }
}