package nine.math;

public class OrbitalCameraMatrix4f implements Matrix4f
{
    Matrix4f result;

    public OrbitalCameraMatrix4f(Vector3f position, Vector3f rotation, ValueFloat distance)
    {
        result = new Matrix4fMulChain(
            new Matrix4fTranslation(Vector3f.newZ(distance)),
            new Matrix4fRotation(rotation.negative()),
            new Matrix4fTranslation(position.negative())
        );
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        result.accept(acceptor);
    }
}