package nine.function;

public interface RefreshStatus
{
    Refreshable make();

    final static RefreshStatus once = Refreshable::once;
}