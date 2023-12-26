package nine.function;

public interface RefreshStatus
{
    Refreshable make();

    final static RefreshStatus once = Refreshable::once;
    final static RefreshStatus always = Refreshable::always;
}