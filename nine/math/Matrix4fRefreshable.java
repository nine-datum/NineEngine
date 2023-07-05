package nine.math;

import nine.function.RefreshStatus;
import nine.function.Refreshable;

public class Matrix4fRefreshable implements Matrix4f
{
    Matrix4fStruct cache = new Matrix4fStruct();
    Matrix4f source;
    Refreshable status;

    public Matrix4fRefreshable(Matrix4f source, RefreshStatus status)
    {
        this.source = source;
        this.status = status.make();
    }

    @Override
    public void accept(ElementsAcceptor acceptor)
    {
        if(status.mark())
        {
            cache.apply(source);
        }
        cache.accept(acceptor);
    }
}