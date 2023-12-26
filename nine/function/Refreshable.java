package nine.function;

public interface Refreshable
{
    boolean mark();

    static Refreshable once()
    {
        boolean[] refreshed = { false };
        return () ->
        {
            if(refreshed[0]) return false;
            refreshed[0] = true;
            return true;
        };
    }
}