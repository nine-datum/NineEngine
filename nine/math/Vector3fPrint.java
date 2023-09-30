package nine.math;

import nine.function.RefreshStatus;
import nine.function.Refreshable;

public class Vector3fPrint implements Vector3f
{
    Vector3f source;
    Refreshable refresh;

    public Vector3fPrint(Vector3f source, RefreshStatus status)
    {
        this.source = source;
        refresh = status.make();
    }

    @Override
    public void accept(Vector3fAcceptor acceptor)
    {
        if(refresh.mark())
        {
            source.accept((x, y, z) ->
            {
                System.out.printf("%f, %f, %f\n", x, y, z);
                acceptor.call(x, y, z);
            });
        }
        else
        {
            source.accept(acceptor);
        }
    }
}