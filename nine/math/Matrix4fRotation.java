package nine.math;

public class Matrix4fRotation implements Matrix4f
{
    Matrix4f rotation;

    public Matrix4fRotation(Vector3f rotation)
    {
        this.rotation = new Matrix4fMulChain(
            new Matrix4fRotationX(new ValueFloatVector3fX(rotation)),
            new Matrix4fRotationY(new ValueFloatVector3fY(rotation)),
            new Matrix4fRotationZ(new ValueFloatVector3fZ(rotation))
        );
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        rotation.accept(acceptor);
    }
}