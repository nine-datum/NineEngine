package nine.math;

public interface Vector3fFunction
{
    void call(float x, float y, float z, Vector3f.XYZAction acceptor);

    static final Vector3fFunction identity = (x, y, z, action) -> action.call(x, y, z);
}