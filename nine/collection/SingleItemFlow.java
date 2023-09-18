package nine.collection;

public class SingleItemFlow<T> implements Flow<T>
{
    T item;

    public SingleItemFlow(T item)
    {
        this.item = item;
    }

    @Override
    public void read(FlowAction<T> action)
    {
        action.call(item);
    }
}