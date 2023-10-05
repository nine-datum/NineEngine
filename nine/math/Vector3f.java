package nine.math;

public interface Vector3f
{
    void accept(Vector3fAcceptor acceptor);

    default Vector3f normalized()
    {
        return new Vector3fNormalized(this);
    }
}