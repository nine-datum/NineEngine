package nine.geometry.collada;

import nine.function.RefreshStatus;
import nine.function.Refreshable;

public class UpdateRefreshStatus implements RefreshStatus
{
    int index;

    @Override
    public Refreshable make()
    {
        return new Refreshable()
        {
            int local;

            @Override
            public boolean mark()
            {
                if(local != index)
                {
                    local = index;
                    return true;
                }
                return false;
            }
        };
    }

    public void update()
    {
        index++;
    }
}