package nine.math;

public class Matrix4fRotation implements Matrix4f
{
    Matrix4f rotation;

    public Matrix4fRotation(Vector3f rotation)
    {
        this.rotation = new Matrix4fMulChain(
            new Matrix4fRotationX(rotation.x()),
            new Matrix4fRotationY(rotation.y()),
            new Matrix4fRotationZ(rotation.z())
        );
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        rotation.accept(acceptor);
    }
}