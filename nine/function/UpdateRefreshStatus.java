package nine.function;

public class UpdateRefreshStatus implements RefreshStatus
{
    int index = -1;

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