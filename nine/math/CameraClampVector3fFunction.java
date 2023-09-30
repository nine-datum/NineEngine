package nine.math;

public class CameraClampVector3fFunction implements Vector3fFunction
{
    final float HPI = (float)Math.PI * 0.5f;

    @Override
    public void call(float x, float y, float z, Vector3fAcceptor acceptor)
    {
        if(x > HPI) x = HPI;
        if(x < -HPI) x = -HPI;
        acceptor.call(x, y, z);
    }
}