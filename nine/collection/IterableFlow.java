package nine.collection;

public class IterableFlow<T> implements Flow<T>
{
    Iterable<T> iterable;

    public IterableFlow(Iterable<T> iterable)
    {
        this.iterable = iterable;
    }

    @Override
    public void read(FlowAction<T> action)
    {
        for(T item : iterable) action.call(item);
    }
}