package nine.math;

public interface Vector3fFunction
{
    void call(float x, float y, float z, Vector3f.XYZAction acceptor);

    static final Vector3fFunction identity = (x, y, z, action) -> action.call(x, y, z);

    static Vector3fFunction cameraClamp()
    {
        float hpi = (float)Math.PI * 0.5f;
        return (x, y, z, acceptor) ->
        {
            if(x > hpi) x = hpi;
            if(x < -hpi) x = -hpi;
            acceptor.call(x, y, z);
        };
    }
}