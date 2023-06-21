package nine.collection;

public class ArrayFlow<T> implements Flow<T>
{
    T[] array;
    
    @SafeVarargs
    public ArrayFlow(T... items)
    {
        array = items;
    }

    @Override
    public void read(FlowAction<T> action)
    {
        for(T item : array) action.call(item);
    }
}